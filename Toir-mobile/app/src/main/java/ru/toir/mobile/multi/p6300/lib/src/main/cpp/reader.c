//
// Created by koputo on 28.10.16.
//
#include <jni.h>
#include <fcntl.h>
#include <android/log.h>
#include <termios.h>
#include <sys/stat.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include "reader.h"

/**
 * Открываем порт через который работаем с RFID считывателем.
 */
jint JNICALL
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
    tcflag_t realBaud;

    __android_log_print(ANDROID_LOG_INFO, TAG,
            "setUart: env=%p, jc=%p, descriptor=%d, baudRate=%d, timeOut=%d, minLen=%d",
            env, jc, descriptor, baudRate, timeOut, minLen);

    switch (baudRate) {
        case 0 :
            realBaud = 000011;
            break;
        case 1 :
            realBaud = 000013;
            break;
        case 2 :
            realBaud = 000014;
            break;
        case 3 :
            realBaud = 000015;
            break;
        case 4 :
            realBaud = 000016;
            break;
        case 5 :
            realBaud = 000017;
            break;
        case 6 :
            realBaud = 010001;
            break;
        case 7 :
            realBaud = 010002;
            break;
        case 8 :
            realBaud = 010003;
            break;
        case 9 :
            realBaud = 010007;
            break;
        default:
            realBaud = 010002;
            break;
    }

    ioctl(descriptor, TCGETS, &oldcfg);
    ioctl(descriptor, TCGETS, &newcfg);

    newcfg.c_iflag = 0;
    newcfg.c_oflag = ONLCR;
    newcfg.c_cflag = HUPCL | CLOCAL | realBaud | CS8 | CREAD;
    newcfg.c_lflag = 0;
    newcfg.c_line = 0;
    newcfg.c_cc[VMIN] = (cc_t)minLen;
    newcfg.c_cc[VTIME] = (cc_t)timeOut;

    ioctl(descriptor, TCFLSH, FLUSH_BOTH);

    if ( ioctl(descriptor, TCSETS, &newcfg) ) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "setUart: fail");
        return 1;
    } else {
        __android_log_print(ANDROID_LOG_INFO, TAG, "setUart: success");
        return 0;
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

//    __android_log_print(ANDROID_LOG_INFO, TAG,
//                        "receiveMsgUartHex: env=%p, jc=%p, descriptor=%d",
//                        env, jc, descriptor);

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
