//
// Created by koputo on 28.10.16.
//

#ifndef P6300LIB_READER_H
#define P6300LIB_READER_H

#include <jni.h>
#include <fcntl.h>
#include <android/log.h>
#include <termios.h>

const char *TAG = "P6300Scanner";

jstring Java_jni_Linuxc_receiveMsgUartHex(JNIEnv *env, jclass jc, jint descriptor);
void Java_jni_Linuxc_sendMsgUart(JNIEnv *env, jclass jc, jint descriptor, jstring array);
void Java_jni_Linuxc_sendMsgUartByte(JNIEnv *env, jclass jc, jint descriptor, jbyteArray *array, jsize arraySize);
void Java_jni_Linuxc_sendMsgUartHex(JNIEnv *env, jclass jc, jint descriptor, jbyte *array, jsize arraySize);
jint Java_jni_Linuxc_setUart(JNIEnv *env, jclass jc, jint descriptor, jint baudRate, jint timeOut, jint minLen);
jint Java_jni_Linuxc_closeUart(JNIEnv *env, jclass jc, jint descriptor);
jint Java_jni_Linuxc_openUart(JNIEnv *env, jclass jc, jstring jPath);

#endif //P6300LIB_READER_H
