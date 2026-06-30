#ifndef AUDIOEQ_BIQUAD_FILTER_H
#define AUDIOEQ_BIQUAD_FILTER_H

#include <cmath>
#include <cstdint>

class BiquadFilter {
public:
    enum FilterType {
        PEAKING,
        LOW_SHELF,
        HIGH_SHELF,
        LOW_PASS,
        HIGH_PASS,
        BAND_PASS,
        NOTCH,
        ALL_PASS
    };

    BiquadFilter() = default;

    void setParams(FilterType type, double freq, double q, double gainDb, double sampleRate) {
        double A = std::pow(10.0, gainDb / 40.0);
        double w0 = 2.0 * M_PI * freq / sampleRate;
        double cosW0 = std::cos(w0);
        double sinW0 = std::sin(w0);
        double alpha = sinW0 / (2.0 * q);
        double beta = std::sqrt(A) / q;

        b0 = b1 = b2 = a0 = a1 = a2 = 0.0;

        switch (type) {
            case PEAKING:
                b0 = 1.0 + alpha * A;
                b1 = -2.0 * cosW0;
                b2 = 1.0 - alpha * A;
                a0 = 1.0 + alpha / A;
                a1 = -2.0 * cosW0;
                a2 = 1.0 - alpha / A;
                break;
            case LOW_SHELF:
                b0 = A * ((A + 1.0) - (A - 1.0) * cosW0 + beta * sinW0);
                b1 = 2.0 * A * ((A - 1.0) - (A + 1.0) * cosW0);
                b2 = A * ((A + 1.0) - (A - 1.0) * cosW0 - beta * sinW0);
                a0 = (A + 1.0) + (A - 1.0) * cosW0 + beta * sinW0;
                a1 = -2.0 * ((A - 1.0) + (A + 1.0) * cosW0);
                a2 = (A + 1.0) + (A - 1.0) * cosW0 - beta * sinW0;
                break;
            case HIGH_SHELF:
                b0 = A * ((A + 1.0) + (A - 1.0) * cosW0 + beta * sinW0);
                b1 = -2.0 * A * ((A - 1.0) + (A + 1.0) * cosW0);
                b2 = A * ((A + 1.0) + (A - 1.0) * cosW0 - beta * sinW0);
                a0 = (A + 1.0) - (A - 1.0) * cosW0 + beta * sinW0;
                a1 = 2.0 * ((A - 1.0) - (A + 1.0) * cosW0);
                a2 = (A + 1.0) - (A - 1.0) * cosW0 - beta * sinW0;
                break;
            case LOW_PASS:
                b0 = (1.0 - cosW0) / 2.0;
                b1 = 1.0 - cosW0;
                b2 = (1.0 - cosW0) / 2.0;
                a0 = 1.0 + alpha;
                a1 = -2.0 * cosW0;
                a2 = 1.0 - alpha;
                break;
            case HIGH_PASS:
                b0 = (1.0 + cosW0) / 2.0;
                b1 = -(1.0 + cosW0);
                b2 = (1.0 + cosW0) / 2.0;
                a0 = 1.0 + alpha;
                a1 = -2.0 * cosW0;
                a2 = 1.0 - alpha;
                break;
            case BAND_PASS:
                b0 = sinW0 / 2.0;
                b1 = 0.0;
                b2 = -sinW0 / 2.0;
                a0 = 1.0 + alpha;
                a1 = -2.0 * cosW0;
                a2 = 1.0 - alpha;
                break;
            case NOTCH:
                b0 = 1.0;
                b1 = -2.0 * cosW0;
                b2 = 1.0;
                a0 = 1.0 + alpha;
                a1 = -2.0 * cosW0;
                a2 = 1.0 - alpha;
                break;
            case ALL_PASS:
                b0 = 1.0 - alpha;
                b1 = -2.0 * cosW0;
                b2 = 1.0 + alpha;
                a0 = 1.0 + alpha;
                a1 = -2.0 * cosW0;
                a2 = 1.0 - alpha;
                break;
        }

        // Normalize coefficients
        b0 /= a0; b1 /= a0; b2 /= a0;
        a1 /= a0; a2 /= a0;
    }

    float process(float sample) {
        double output = b0 * sample + b1 * xn1 + b2 * xn2 - a1 * yn1 - a2 * yn2;
        xn2 = xn1;
        xn1 = sample;
        yn2 = yn1;
        yn1 = output;
        return static_cast<float>(output);
    }

    void reset() {
        xn1 = xn2 = yn1 = yn2 = 0.0;
    }

private:
    double b0 = 1.0, b1 = 0.0, b2 = 0.0;
    double a0 = 1.0, a1 = 0.0, a2 = 0.0;
    double xn1 = 0.0, xn2 = 0.0;
    double yn1 = 0.0, yn2 = 0.0;
};

#endif // AUDIOEQ_BIQUAD_FILTER_H
