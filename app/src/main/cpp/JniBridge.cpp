#include <jni.h>
#include <android/log.h>
#include "DspEngine.h"

#define LOG_TAG "AudioEQ-DSP"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static DspEngine* gEngine = nullptr;

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_audioeq_audio_DspBridge_nativeCreate(JNIEnv* env, jobject thiz) {
    if (gEngine) delete gEngine;
    gEngine = new DspEngine();
    LOGI("DSP Engine created");
    return reinterpret_cast<jlong>(gEngine);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeDestroy(JNIEnv* env, jobject thiz, jlong ptr) {
    if (gEngine) {
        delete gEngine;
        gEngine = nullptr;
        LOGI("DSP Engine destroyed");
    }
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeSetSampleRate(JNIEnv* env, jobject thiz,
                                                       jlong ptr, jint sampleRate) {
    if (gEngine) gEngine->setSampleRate(sampleRate);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureEq(JNIEnv* env, jobject thiz,
                                                     jlong ptr, jfloatArray frequencies,
                                                     jfloatArray gains, jfloatArray qValues,
                                                     jintArray filterTypes, jint bandCount,
                                                     jboolean enabled) {
    if (!gEngine) return;

    jfloat* freqPtr = env->GetFloatArrayElements(frequencies, nullptr);
    jfloat* gainPtr = env->GetFloatArrayElements(gains, nullptr);
    jfloat* qPtr = env->GetFloatArrayElements(qValues, nullptr);
    jint* typePtr = env->GetIntArrayElements(filterTypes, nullptr);

    gEngine->configureEq(freqPtr, gainPtr, qPtr, typePtr, bandCount, enabled);

    env->ReleaseFloatArrayElements(frequencies, freqPtr, 0);
    env->ReleaseFloatArrayElements(gains, gainPtr, 0);
    env->ReleaseFloatArrayElements(qValues, qPtr, 0);
    env->ReleaseIntArrayElements(filterTypes, typePtr, 0);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeSetEqEnabled(JNIEnv* env, jobject thiz,
                                                      jlong ptr, jboolean enabled) {
    if (gEngine) gEngine->setEqEnabled(enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureCompressor(JNIEnv* env, jobject thiz,
                                                             jlong ptr, jfloat thresholdDb,
                                                             jfloat ratio, jfloat attackMs,
                                                             jfloat releaseMs, jfloat kneeDb,
                                                             jfloat makeupGainDb,
                                                             jboolean enabled) {
    if (gEngine) gEngine->configureCompressor(thresholdDb, ratio, attackMs,
                                              releaseMs, kneeDb, makeupGainDb, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureMultibandCompressor(
    JNIEnv* env, jobject thiz, jlong ptr,
    jfloat lowThreshold, jfloat lowRatio,
    jfloat midThreshold, jfloat midRatio,
    jfloat highThreshold, jfloat highRatio,
    jfloat crossoverLow, jfloat crossoverMid,
    jboolean enabled) {
    if (gEngine) gEngine->configureMultibandCompressor(
        lowThreshold, lowRatio, midThreshold, midRatio,
        highThreshold, highRatio, crossoverLow, crossoverMid, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureLimiter(JNIEnv* env, jobject thiz,
                                                          jlong ptr, jfloat thresholdDb,
                                                          jfloat ceilingDb, jfloat attackMs,
                                                          jfloat releaseMs, jboolean autoRelease,
                                                          jboolean lookahead, jboolean enabled) {
    if (gEngine) gEngine->configureLimiter(thresholdDb, ceilingDb, attackMs,
                                           releaseMs, autoRelease, lookahead, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureEpicenter(JNIEnv* env, jobject thiz,
                                                           jlong ptr, jfloat intensity,
                                                           jfloat centerFreq, jfloat resonance,
                                                           jboolean enabled) {
    if (gEngine) gEngine->configureEpicenter(intensity, centerFreq, resonance, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureAmplifier(JNIEnv* env, jobject thiz,
                                                           jlong ptr, jfloat gainDb,
                                                           jfloat headroomDb, jfloat harmonicDrive,
                                                           jboolean enabled) {
    if (gEngine) gEngine->configureAmplifier(gainDb, headroomDb, harmonicDrive, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureBassTuning(JNIEnv* env, jobject thiz,
                                                            jlong ptr, jfloat boostDb,
                                                            jfloat extensionHz, jfloat widthHz,
                                                            jfloat subHarmonic, jboolean enabled) {
    if (gEngine) gEngine->configureBassTuning(boostDb, extensionHz, widthHz, subHarmonic, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureMonoblock(JNIEnv* env, jobject thiz,
                                                           jlong ptr, jboolean enabled) {
    if (gEngine) gEngine->configureMonoblock(enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeConfigureSpatialEnhancer(JNIEnv* env, jobject thiz,
                                                                 jlong ptr, jfloat width,
                                                                 jfloat crossfeed,
                                                                 jboolean enabled) {
    if (gEngine) gEngine->configureSpatialEnhancer(width, crossfeed, enabled);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeProcess(JNIEnv* env, jobject thiz,
                                                 jlong ptr, jfloatArray leftChannel,
                                                 jfloatArray rightChannel, jint numFrames) {
    if (!gEngine) return;

    jfloat* left = env->GetFloatArrayElements(leftChannel, nullptr);
    jfloat* right = env->GetFloatArrayElements(rightChannel, nullptr);

    gEngine->process(left, right, numFrames);

    env->ReleaseFloatArrayElements(leftChannel, left, 0);
    env->ReleaseFloatArrayElements(rightChannel, right, 0);
}

JNIEXPORT void JNICALL
Java_com_audioeq_audio_DspBridge_nativeReset(JNIEnv* env, jobject thiz, jlong ptr) {
    if (gEngine) gEngine->reset();
}

} // extern "C"
