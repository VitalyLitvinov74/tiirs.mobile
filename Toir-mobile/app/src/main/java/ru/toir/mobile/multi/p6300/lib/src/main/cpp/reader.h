//
// Created by koputo on 28.10.16.
//

#ifndef P6300LIB_READER_H
#define P6300LIB_READER_H

const char *TAG = "P6300Scanner";

JNIEXPORT jint JNICALL
        Java_jni_Linuxc_openUart(JNIEnv *env, jclass jc, jstring jPath);
JNIEXPORT jint JNICALL
        Java_jni_Linuxc_closeUart(JNIEnv *env, jclass jc, jint descriptor);
JNIEXPORT jint JNICALL
        Java_jni_Linuxc_setUart(JNIEnv *env, jclass jc, jint descriptor, jint baudRate, jint timeOut, jint minLen);

JNIEXPORT jstring JNICALL
        Java_jni_Linuxc_receiveMsgUartHex(JNIEnv *env, jclass jc, jint descriptor);
JNIEXPORT void JNICALL
        Java_jni_Linuxc_sendMsgUartHex(JNIEnv *env, jclass jc, jint descriptor, jstring command, jsize commandSize);

#endif //P6300LIB_READER_H
