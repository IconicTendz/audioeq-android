#ifndef AUDIOEQ_DSP_ENGINE_H
#define AUDIOEQ_DSP_ENGINE_H

#include <vector>
#include <array>
#include <cmath>
#include <cstdint>
#include "BiquadFilter.h"

class DspEngine {
public:
    DspEngine() = default;

    void setSampleRate(int rate) { sampleRate_ = rate; }
    int getSampleRate() const { return sampleRate_; }

    // EQ
    void configureEq(const float* frequencies, const float* gains,
                     const float* qValues, const int* filterTypes,
                     int bandCount, bool enabled);
    void setEqEnabled(bool enabled) { eqEnabled_ = enabled; }

    // Compressor
    void configureCompressor(float thresholdDb, float ratio, float attackMs,
                            float releaseMs, float kneeDb, float makeupGainDb,
                            bool enabled);
    void configureMultibandCompressor(float lowThreshold, float lowRatio,
                                      float midThreshold, float midRatio,
                                      float highThreshold, float highRatio,
                                      float crossoverLow, float crossoverMid,
                                      bool enabled);

    // Limiter
    void configureLimiter(float thresholdDb, float ceilingDb, float attackMs,
                          float releaseMs, bool autoRelease, bool lookahead,
                          bool enabled);

    // Effects
    void configureEpicenter(float intensity, float centerFreq, float resonance, bool enabled);
    void configureAmplifier(float gainDb, float headroomDb, float harmonicDrive, bool enabled);
    void configureBassTuning(float boostDb, float extensionHz, float widthHz, float subHarmonic, bool enabled);
    void configureLoudness(float boostDb, float referenceLevel, bool enabled);
    void configureMonoblock(bool enabled);
    void configureSpatialEnhancer(float width, float crossfeed, bool enabled);

    // Processing
    void process(float* left, float* right, int numFrames);
    void reset();

private:
    int sampleRate_ = 48000;
    bool eqEnabled_ = false;

    // EQ
    std::vector<BiquadFilter> eqFilters_;

    // Compressor state
    bool compressorEnabled_ = false;
    bool multibandEnabled_ = false;
    float compThreshold_ = -24.0f, compRatio_ = 4.0f;
    float compAttack_ = 5.0f, compRelease_ = 100.0f, compKnee_ = 6.0f, compMakeup_ = 0.0f;
    float compGain_ = 0.0f;
    float lowCompGain_ = 0.0f, midCompGain_ = 0.0f, highCompGain_ = 0.0f;
    float lowThreshold_ = -24.0f, lowRatio_ = 4.0f;
    float midThreshold_ = -24.0f, midRatio_ = 4.0f;
    float highThreshold_ = -24.0f, highRatio_ = 4.0f;
    float crossoverLow_ = 250.0f, crossoverMid_ = 2000.0f;
    BiquadFilter lpFilter_, hpFilter1_, hpFilter2_;

    // Limiter state
    bool limiterEnabled_ = false;
    float limiterThreshold_ = -2.0f, limiterCeiling_ = 0.0f;
    float limiterAttack_ = 0.5f, limiterRelease_ = 50.0f, limiterGain_ = 1.0f;
    bool limiterAutoRelease_ = true, limiterLookahead_ = true;
    std::vector<float> lookaheadBuffer_;
    int lookaheadWritePos_ = 0;

    // Effects
    bool epicenterEnabled_ = false;
    float epicenterIntensity_ = 0.5f, epicenterFreq_ = 40.0f, epicenterResonance_ = 0.5f;
    BiquadFilter epicenterFilter_;

    bool amplifierEnabled_ = false;
    float amplifierGain_ = 1.0f, amplifierHeadroom_ = 3.0f, amplifierDrive_ = 0.0f;

    bool bassTuningEnabled_ = false;
    float bassBoost_ = 0.0f, bassExtensionHz_ = 30.0f, bassWidthHz_ = 80.0f, bassSubHarmonic_ = 0.0f;

    bool loudnessEnabled_ = false;
    float loudnessBoost_ = 0.0f, loudnessRefLevel_ = 85.0f;

    bool monoblockEnabled_ = false;
    bool spatialEnabled_ = false;
    float spatialWidth_ = 100.0f, spatialCrossfeed_ = 0.0f;

    // Envelope follower state
    float envelope_ = 0.0f;
    float lowEnvelope_ = 0.0f, midEnvelope_ = 0.0f, highEnvelope_ = 0.0f;

    // Helpers
    float computeEnvelope(float input, float envelope, float attackMs, float releaseMs);
    float computeGainReduction(float envelope, float threshold, float ratio, float knee);
    float applySoftClip(float sample, float drive);
    float msToCoeff(float ms);
    void applyLookahead(float* left, float* right, int numFrames);
};

#endif // AUDIOEQ_DSP_ENGINE_H
