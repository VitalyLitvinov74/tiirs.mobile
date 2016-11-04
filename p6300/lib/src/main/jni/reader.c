//
// Created by koputo on 28.10.16.
//
#include "reader.h"

/**
 * Открываем порт через который работаем с RFID считывателем.
 */
JNIEXPORT jint JNICALL
Java_android_hardware_p6300_jni_Linuxc_openUart(JNIEnv *env, jclass jc, jstring jPath) {
    JNIEnv e = *env;
    const char *path = e->GetStringUTFChars(env, jPath, NULL);
    int32_t result;

    __android_log_print(ANDROID_LOG_INFO, TAG, "openUart: env=%p, jc=%p, jPath=%p", env, jc, jPath);

    result = open(path, O_RDWR, S_IWOTH | S_IROTH | S_IWGRP | S_IRGRP | S_IWUSR | S_IRUSR);
    __android_log_print(ANDROID_LOG_INFO, TAG, "openUart: descriptor = %d", result);
    e->ReleaseStringUTFChars(env, jPath, path);
    return result;
}

/**
 * Закрываем порт через который работаем с RFID считывателем.
 */
JNIEXPORT jint JNICALL
Java_android_hardware_p6300_jni_Linuxc_closeUart(JNIEnv *env, jclass jc, jint descriptor) {
    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "closeUart: env=%p, jc=%p, descriptor=%d",
                        env, jc, descriptor);
    return close(descriptor);
}

/**
 * Устанавливаем параметры работы порта через который работаем с RFID считывателем.
 * "/dev/ttysWK2",Linuxc.BAUD_RATE_115200,1,0
 */
JNIEXPORT jint JNICALL
Java_android_hardware_p6300_jni_Linuxc_setUart(JNIEnv *env, jclass jc, jint descriptor,
                             jint baudRate, jint timeOut, jint minLen) {
    struct termios oldcfg;
    struct termios newcfg;
    int32_t FLUSH_BOTH = 2;

    __android_log_print(ANDROID_LOG_INFO, TAG,
            "setUart: env=%p, jc=%p, descriptor=%d, baudRate=%d, timeOut=%d, minLen=%d",
            env, jc, descriptor, baudRate, timeOut, minLen);

    ioctl(descriptor, TCGETS, &oldcfg);
    ioctl(descriptor, TCGETS, &newcfg);

    // эмпирические данные
    newcfg.c_iflag = 0;
    newcfg.c_oflag = ONLCR;
    newcfg.c_cflag = HUPCL | CLOCAL | B115200 | CS8 | CREAD;
    newcfg.c_lflag = 0;
    newcfg.c_line = 0;
    // конец эмпирических данных

    newcfg.c_cc[VMIN] = (cc_t)minLen;
    newcfg.c_cc[VTIME] = (cc_t)timeOut;

    ioctl(descriptor, TCFLSH, FLUSH_BOTH);

    if ( ioctl(descriptor, TCSETS, &newcfg) ) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "setUart: fail");
        return 0;
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "setUart: success");
        return 1;
    }
}

/**
 * Читаем данные из порта через который работаем с RFID считывателем.
 */
JNIEXPORT jstring JNICALL
Java_android_hardware_p6300_jni_Linuxc_receiveMsgUartHex(JNIEnv *env, jclass jc,
                                          jint descriptor) {
    JNIEnv e = *env;
    jstring result = NULL;
    jchar *data;
    size_t bufferSize = 1024;
    char buffer[bufferSize];
    ssize_t countChars;

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "receiveMsgUartHex: env=%p, jc=%p, descriptor=%d",
                        env, jc, descriptor);

    memset(&buffer, 0, bufferSize);
    countChars = read(descriptor, &buffer, bufferSize);
    if (countChars > 0) {
        data = calloc((size_t)countChars, sizeof(jchar));
        memset((void*)data, 0, (size_t)(countChars * sizeof(jchar)));
        for (int i = 0; i < countChars; i++) {
            data[i] = (jchar) buffer[i];
        }

        result = e->NewString(env, (const jchar *)data, (jsize)(countChars));
        free((void *)data);
    }

    return result;
}

/**
 *
 */
JNIEXPORT void JNICALL
Java_android_hardware_p6300_jni_Linuxc_sendMsgUartHex(JNIEnv *env, jclass jc, jint descriptor,
                                                      jstring command, jsize commandSize) {
    JNIEnv e = *env;
    const jchar *data = e->GetStringChars(env, command, NULL);
    char *buffer = calloc((size_t)commandSize, sizeof(char));

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "sendMsgUartHex: env=%p, jc=%p, array=%p, arraySize=%d",
                        env, jc, command, commandSize);

    if (commandSize > 0) {
        for (int i = 0; i < commandSize; i++) {
            buffer[i] = (char) data[i];
        }

        write(descriptor, buffer, (size_t)commandSize);
    }

    e->ReleaseStringChars(env, command, data);
}
