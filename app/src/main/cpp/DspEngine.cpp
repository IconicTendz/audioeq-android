#include "DspEngine.h"
#include <algorithm>
#include <cmath>
#include <cstring>

#define DB_TO_LINEAR(x) std::pow(10.0f, (x) / 20.0f)
#define LINEAR_TO_DB(x) (20.0f * std::log10(std::max(x, 1e-10f)))

void DspEngine::configureEq(const float* frequencies, const float* gains,
                            const float* qValues, const int* filterTypes,
                            int bandCount, bool enabled) {
    eqEnabled_ = enabled;
    eqFilters_.resize(bandCount);

    for (int i = 0; i < bandCount; ++i) {
        if (frequencies[i] > 0.0f && frequencies[i] < sampleRate_ * 0.5f) {
            eqFilters_[i].setParams(
                static_cast<BiquadFilter::FilterType>(filterTypes[i]),
                frequencies[i], qValues[i], gains[i],
                static_cast<double>(sampleRate_)
            );
        }
    }
}

void DspEngine::configureCompressor(float thresholdDb, float ratio, float attackMs,
                                    float releaseMs, float kneeDb, float makeupGainDb,
                                    bool enabled) {
    compressorEnabled_ = enabled;
    compThreshold_ = thresholdDb;
    compRatio_ = ratio;
    compAttack_ = attackMs;
    compRelease_ = releaseMs;
    compKnee_ = kneeDb;
    compMakeup_ = makeupGainDb;
}

void DspEngine::configureMultibandCompressor(float lowThreshold, float lowRatio,
                                              float midThreshold, float midRatio,
                                              float highThreshold, float highRatio,
                                              float crossoverLow, float crossoverMid,
                                              bool enabled) {
    multibandEnabled_ = enabled;
    lowThreshold_ = lowThreshold;
    lowRatio_ = lowRatio;
    midThreshold_ = midThreshold;
    midRatio_ = midRatio;
    highThreshold_ = highThreshold;
    highRatio_ = highRatio;
    crossoverLow_ = crossoverLow;
    crossoverMid_ = crossoverMid;

    // Configure crossover filters
    lpFilter_.setParams(BiquadFilter::LOW_PASS, crossoverLow_, 0.707, 0, sampleRate_);
    hpFilter1_.setParams(BiquadFilter::HIGH_PASS, crossoverLow_, 0.707, 0, sampleRate_);
    hpFilter2_.setParams(BiquadFilter::HIGH_PASS, crossoverMid_, 0.707, 0, sampleRate_);
}

void DspEngine::configureLimiter(float thresholdDb, float ceilingDb, float attackMs,
                                 float releaseMs, bool autoRelease, bool lookahead,
                                 bool enabled) {
    limiterEnabled_ = enabled;
    limiterThreshold_ = thresholdDb;
    limiterCeiling_ = ceilingDb;
    limiterAttack_ = attackMs;
    limiterRelease_ = releaseMs;
    limiterAutoRelease_ = autoRelease;
    limiterLookahead_ = lookahead;

    // Allocate lookahead buffer if needed
    if (lookahead && lookaheadBuffer_.empty()) {
        int lookaheadSamples = static_cast<int>(sampleRate_ * 0.001f); // 1ms lookahead
        lookaheadBuffer_.resize(lookaheadSamples * 2, 0.0f);
        lookaheadWritePos_ = 0;
    }
}

void DspEngine::configureEpicenter(float intensity, float centerFreq, float resonance, bool enabled) {
    epicenterEnabled_ = enabled;
    epicenterIntensity_ = intensity / 100.0f;
    epicenterFreq_ = centerFreq;
    epicenterResonance_ = resonance;

    if (enabled) {
        epicenterFilter_.setParams(BiquadFilter::PEAKING, centerFreq,
                                   1.0f / std::max(resonance, 0.01f),
                                   intensity * 0.24f, sampleRate_);
    }
}

void DspEngine::configureAmplifier(float gainDb, float headroomDb, float harmonicDrive, bool enabled) {
    amplifierEnabled_ = enabled;
    amplifierGain_ = DB_TO_LINEAR(gainDb);
    amplifierHeadroom_ = headroomDb;
    amplifierDrive_ = harmonicDrive / 100.0f;
}

void DspEngine::configureBassTuning(float boostDb, float extensionHz, float widthHz,
                                    float subHarmonic, bool enabled) {
    bassTuningEnabled_ = enabled;
    bassBoost_ = boostDb;
    bassExtensionHz_ = extensionHz;
    bassWidthHz_ = widthHz;
    bassSubHarmonic_ = subHarmonic / 100.0f;
}

void DspEngine::configureLoudness(float boostDb, float referenceLevel, bool enabled) {
    loudnessEnabled_ = enabled;
    loudnessBoost_ = boostDb;
    loudnessRefLevel_ = referenceLevel;
}

void DspEngine::configureMonoblock(bool enabled) {
    monoblockEnabled_ = enabled;
}

void DspEngine::configureSpatialEnhancer(float width, float crossfeed, bool enabled) {
    spatialEnabled_ = enabled;
    spatialWidth_ = width;
    spatialCrossfeed_ = crossfeed / 100.0f;
}

float DspEngine::msToCoeff(float ms) {
    if (ms <= 0.0f) return 1.0f;
    return 1.0f - std::exp(-1000.0f / (ms * static_cast<float>(sampleRate_)));
}

float DspEngine::computeEnvelope(float input, float envelope, float attackMs, float releaseMs) {
    float absInput = std::abs(input);
    float attackCoeff = msToCoeff(attackMs);
    float releaseCoeff = msToCoeff(releaseMs);
    float coeff = (absInput > envelope) ? attackCoeff : releaseCoeff;
    return envelope + coeff * (absInput - envelope);
}

float DspEngine::computeGainReduction(float envelope, float threshold, float ratio, float knee) {
    float envDb = LINEAR_TO_DB(std::max(envelope, 1e-10f));
    float threshDb = threshold;

    if (knee > 0.0f) {
        float kneeStart = threshDb - knee / 2.0f;
        float kneeEnd = threshDb + knee / 2.0f;

        if (envDb < kneeStart) {
            return 0.0f;
        } else if (envDb < kneeEnd) {
            // Soft knee
            float x = envDb - kneeStart;
            float reduction = (x * x) / (2.0f * knee) * (1.0f - 1.0f / ratio);
            return reduction;
        } else {
            return (envDb - threshDb) * (1.0f - 1.0f / ratio);
        }
    } else {
        if (envDb <= threshDb) return 0.0f;
        return (envDb - threshDb) * (1.0f - 1.0f / ratio);
    }
}

float DspEngine::applySoftClip(float sample, float drive) {
    if (drive <= 0.0f) return sample;
    float threshold = 1.0f / (1.0f + drive * 2.0f);
    float absSample = std::abs(sample);

    if (absSample <= threshold) {
        return sample;
    } else if (absSample < 1.0f) {
        float sign = (sample >= 0.0f) ? 1.0f : -1.0f;
        float x = (absSample - threshold) / (1.0f - threshold);
        float y = threshold + (1.0f - threshold) * (x * (2.0f - x));
        return sign * y;
    } else {
        return (sample >= 0.0f) ? 1.0f : -1.0f;
    }
}

void DspEngine::applyLookahead(float* left, float* right, int numFrames) {
    if (lookaheadBuffer_.empty()) return;
    int lookaheadSamples = static_cast<int>(lookaheadBuffer_.size() / 2);

    for (int i = 0; i < numFrames; ++i) {
        int readPos = (lookaheadWritePos_ + 1) % lookaheadSamples;
        float delayedL = lookaheadBuffer_[readPos * 2];
        float delayedR = lookaheadBuffer_[readPos * 2 + 1];

        lookaheadBuffer_[lookaheadWritePos_ * 2] = left[i];
        lookaheadBuffer_[lookaheadWritePos_ * 2 + 1] = right[i];
        lookaheadWritePos_ = (lookaheadWritePos_ + 1) % lookaheadSamples;

        left[i] = delayedL;
        right[i] = delayedR;
    }
}

void DspEngine::process(float* left, float* right, int numFrames) {
    if (numFrames <= 0) return;

    // Apply lookahead for limiter
    if (limiterEnabled_ && limiterLookahead_) {
        applyLookahead(left, right, numFrames);
    }

    for (int i = 0; i < numFrames; ++i) {
        float sampleL = left[i];
        float sampleR = right[i];

        // --- EQ Processing ---
        if (eqEnabled_) {
            for (auto& filter : eqFilters_) {
                sampleL = filter.process(sampleL);
                sampleR = filter.process(sampleR);
            }
        }

        // --- Epicenter (bass enhancement) ---
        if (epicenterEnabled_) {
            float bassL = epicenterFilter_.process(sampleL) - sampleL;
            float bassR = epicenterFilter_.process(sampleR) - sampleR;
            bassL *= epicenterIntensity_ * 2.0f;
            bassR *= epicenterIntensity_ * 2.0f;
            sampleL += bassL;
            sampleR += bassR;
        }

        // --- Bass Tuning ---
        if (bassTuningEnabled_) {
            float bassBoostLinear = DB_TO_LINEAR(bassBoost_);
            sampleL *= (1.0f + (bassBoostLinear - 1.0f) * 0.5f);
            sampleR *= (1.0f + (bassBoostLinear - 1.0f) * 0.5f);
        }

        // --- Loudness Compensation ---
        if (loudnessEnabled_) {
            float loudnessGain = DB_TO_LINEAR(loudnessBoost_);
            sampleL *= (1.0f + (loudnessGain - 1.0f) * 0.3f);
            sampleR *= (1.0f + (loudnessGain - 1.0f) * 0.3f);
        }

        // --- Amplifier / Saturation ---
        if (amplifierEnabled_) {
            sampleL *= amplifierGain_;
            sampleR *= amplifierGain_;
            if (amplifierDrive_ > 0.0f) {
                sampleL = applySoftClip(sampleL, amplifierDrive_);
                sampleR = applySoftClip(sampleR, amplifierDrive_);
            }
            float headroomGain = DB_TO_LINEAR(-amplifierHeadroom_);
            sampleL *= headroomGain;
            sampleR *= headroomGain;
        }

        // --- Mono block ---
        if (monoblockEnabled_) {
            float mono = (sampleL + sampleR) * 0.5f;
            sampleL = mono;
            sampleR = mono;
        }

        // --- Spatial Enhancer ---
        if (spatialEnabled_) {
            float mid = (sampleL + sampleR) * 0.5f;
            float side = (sampleR - sampleL) * 0.5f;
            float widthScaled = spatialWidth_ / 100.0f;
            sampleL = mid - side * widthScaled;
            sampleR = mid + side * widthScaled;
        }

        left[i] = sampleL;
        right[i] = sampleR;
    }

    // --- Compressor (after effects, before limiter) ---
    if (compressorEnabled_) {
        for (int i = 0; i < numFrames; ++i) {
            float inputL = left[i];
            float inputR = right[i];

            if (multibandEnabled_) {
                // Multiband crossover
                float bandLow = lpFilter_.process((inputL + inputR) * 0.5f);
                float bandMid = hpFilter1_.process((inputL + inputR) * 0.5f);
                float bandHigh = hpFilter2_.process(bandMid);
                bandMid -= bandHigh;

                lowEnvelope_ = computeEnvelope(bandLow, lowEnvelope_, compAttack_, compRelease_);
                midEnvelope_ = computeEnvelope(bandMid, midEnvelope_, compAttack_, compRelease_);
                highEnvelope_ = computeEnvelope(bandHigh, highEnvelope_, compAttack_, compRelease_);

                float lowReduction = computeGainReduction(lowEnvelope_, lowThreshold_, lowRatio_, compKnee_);
                float midReduction = computeGainReduction(midEnvelope_, midThreshold_, midRatio_, compKnee_);
                float highReduction = computeGainReduction(highEnvelope_, highThreshold_, highRatio_, compKnee_);

                lowCompGain_ = DB_TO_LINEAR(-lowReduction);
                midCompGain_ = DB_TO_LINEAR(-midReduction);
                highCompGain_ = DB_TO_LINEAR(-highReduction);

                inputL = bandLow * lowCompGain_ + bandMid * midCompGain_ + bandHigh * highCompGain_;
                inputR = inputL; // Linked stereo for multiband
            } else {
                // Standard compressor
                float sidechain = (inputL + inputR) * 0.5f;
                envelope_ = computeEnvelope(sidechain, envelope_, compAttack_, compRelease_);
                float reduction = computeGainReduction(envelope_, compThreshold_, compRatio_, compKnee_);
                compGain_ = DB_TO_LINEAR(-reduction + compMakeup_);

                inputL *= compGain_;
                inputR *= compGain_;
            }

            left[i] = inputL;
            right[i] = inputR;
        }
    }

    // --- Limiter (final stage) ---
    if (limiterEnabled_) {
        float thresholdLinear = DB_TO_LINEAR(limiterThreshold_);
        float ceilingLinear = DB_TO_LINEAR(limiterCeiling_);

        for (int i = 0; i < numFrames; ++i) {
            float inputL = left[i];
            float inputR = right[i];

            // Stereo detection
            float detection = std::max(std::abs(inputL), std::abs(inputR));

            // Envelope
            float attackCoeff = msToCoeff(limiterAttack_);
            float releaseCoeff = msToCoeff(limiterRelease_);
            if (limiterAutoRelease_) {
                releaseCoeff = msToCoeff(limiterRelease_ * (1.0f + detection * 5.0f));
            }

            envelope_ = (detection > envelope_)
                ? envelope_ + attackCoeff * (detection - envelope_)
                : envelope_ + releaseCoeff * (detection - envelope_);

            // Compute gain
            if (envelope_ > thresholdLinear) {
                limiterGain_ = thresholdLinear / envelope_;
            } else {
                limiterGain_ = 1.0f;
            }

            // Apply ceiling
            float gainWithCeiling = limiterGain_ * ceilingLinear;

            inputL *= gainWithCeiling;
            inputR *= gainWithCeiling;

            left[i] = inputL;
            right[i] = inputR;
        }
    }
}

void DspEngine::reset() {
    for (auto& filter : eqFilters_) filter.reset();
    lpFilter_.reset();
    hpFilter1_.reset();
    hpFilter2_.reset();
    epicenterFilter_.reset();
    envelope_ = 0.0f;
    lowEnvelope_ = midEnvelope_ = highEnvelope_ = 0.0f;
    compGain_ = 1.0f;
    limiterGain_ = 1.0f;
    std::fill(lookaheadBuffer_.begin(), lookaheadBuffer_.end(), 0.0f);
    lookaheadWritePos_ = 0;
}
