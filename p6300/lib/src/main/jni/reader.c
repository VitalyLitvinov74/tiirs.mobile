//
// Created by koputo on 28.10.16.
//
#include "reader.h"

/**
 *
 */
jint Java_jni_Linuxc_openUart(JNIEnv *env, jclass jc, jstring jPath) {
    JNIEnv e = *env;
    jboolean copy = JNI_TRUE;
    const char *path = e->GetStringUTFChars(env, jPath, &copy);;
    int32_t result;

    __android_log_print(ANDROID_LOG_INFO, TAG, "openUart: env=%p, jc=%p, jPath=%p", env, jc, jPath);

    result = open(path, O_RDWR, 438);
    e->ReleaseStringUTFChars(env, jPath, path);
    return result;
}

/**
 *
 */
jint Java_jni_Linuxc_closeUart(JNIEnv *env, jclass jc, jint descriptor) {
    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "closeUart: env=%p, jc=%p, descriptor=%d",
                        env, jc, descriptor);
    return close(descriptor);
}

/**
 *
 */
jint Java_jni_Linuxc_setUart(JNIEnv *env, jclass jc, jint descriptor,
                             jint baudRate, jint timeOut, jint minLen) {
    int v5;
    int v6;
    signed int v10;

    struct termios oldcfg;
    struct termios newcfg;
    int32_t FLUSH_BOTH = 2;

    __android_log_print(ANDROID_LOG_INFO, TAG,
            "setUart: env=%p, jc=%p, descriptor=%d, baudRate=%d, timeOut=%d, minLen=%d",
            env, jc, descriptor, baudRate, timeOut, minLen);

    v5 = baudRate;
    v10 = 9;

    ioctl(descriptor, TCGETS, &oldcfg);
    ioctl(descriptor, TCGETS, &newcfg);

    v6 = *(&v10 + v5);
//    unk_4B14 &= 0xFFFFFFFE;
    newcfg.c_iflag &= 0xFFFFFFFE;
//    unk_4B18 = ((unk_4B18 & 0xFFFFEFF0 | v6 & 0x100F) & 0xFFFFEFF0 | v6 & 0x100F | 0x30) & 0xFFFFFEBF;
    newcfg.c_oflag = ((newcfg.c_oflag & 0xFFFFEFF0 | v6 & 0x100F) & 0xFFFFEFF0 | v6 & 0x100F | 0x30) & 0xFFFFFEBF;
//    unk_4B1C = 0;
    newcfg.c_cflag = 0;
//    unk_4B26 = 0;
//    unk_4B27 = 0;
//    newtio &= 0xFFFFE2CD;

    ioctl(descriptor, TCFLSH, FLUSH_BOTH);

    if ( ioctl(descriptor, TCSETS, &newcfg) ) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "setUart: fail");
        return 0;
    } else {
        return 1;
    }
}

/**
 *
 */
jstring Java_jni_Linuxc_receiveMsgUartHex(JNIEnv *env, jclass jc,
                                          jint descriptor) {
    JNIEnv e = *env;
    jstring result;
    const jchar *_data_start;
    char buffer[1024];

    char *v4;
    char *v6;
    ssize_t v7;
    ssize_t v8;
    char v9;

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "receiveMsgUartHex: env=%p, jc=%p, descriptor=%d",
                        env, jc, descriptor);

    v4 = buffer;
    memset(&buffer, 0, 0x400u);
    v6 = (char *)&_data_start;
    memset(&_data_start, 0, 0x1000u);
    v7 = read(descriptor, &buffer, 0x400u);
    v8 = v7;
    if ( 2 * v7 > 0 ) {
        do {
            v9 = *v4;
            v4 = v4 + 1;
            *v6 = v9;
            v6 = v6 + 2;
        } while ( v4 != ((char *)&buffer + 2 * v7) );
    }

    result = NULL;
    if (v8 > 0) {
        result = e->NewString(env, _data_start, v8);
    }

    return result;
}

/**
 *
 */
void Java_jni_Linuxc_sendMsgUart(JNIEnv *env, jclass jc, jint descriptor,
                                 jstring array) {
    JNIEnv e = *env;
    jsize dataSize = e->GetStringLength(env, array);
    const char *data =  e->GetStringUTFChars(env, array, NULL);

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "sendMsgUart: env=%p, jc=%p, array=%p", env, jc, array);

    write(descriptor, data, (size_t)dataSize);
    e->ReleaseStringUTFChars(env, array, data);
}

/**
 *
 */
void Java_jni_Linuxc_sendMsgUartByte(JNIEnv *env, jclass jc, jint descriptor,
                                     jbyteArray *array, jsize arraySize) {
    JNIEnv e = *env;
    jsize arrayLen = e->GetArrayLength(env, array);
    jbyte *data = e->GetByteArrayElements(env, array, NULL);

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "sendMsgUartByte: env=%p, jc=%p, array=%p, arryaSize=%d",
                        env, jc, array, arraySize);

    write(descriptor, data, (size_t)arrayLen);
    e->ReleaseByteArrayElements(env, array, data, JNI_ABORT);
}

/**
 *
 */
void Java_jni_Linuxc_sendMsgUartHex(JNIEnv *env, jclass jc, jint descriptor,
                                    jbyte *array, jsize arraySize) {
    JNIEnv e = *env;
    const jchar *data = e->GetStringChars(env, array, NULL);
    const void *v7;
    signed int index;

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "sendMsgUartHex: env=%p, jc=%p, array=%p, arraySize=%d",
                        env, jc, array, arraySize);

    v7 = malloc(2 * (size_t)arraySize);
    if (arraySize > 0) {
        index = 0;
        do {
            *((char *)v7 + index) = *(char *)(data + 2 * index);
            ++index;
        } while (arraySize > index);
    }

    write(descriptor, v7, (size_t)arraySize);
    e->ReleaseStringChars(env, array, data);

    free((void *)v7);
}



