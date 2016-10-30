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

jint Java_jni_Linuxc_receiveMsgUartHex(JNIEnv *env, jclass jc, jint descriptor);
jint Java_jni_Linuxc_sendMsgUart(JNIEnv *env, jclass jc, int a3, int a4);
void Java_jni_Linuxc_sendMsgUartByte(JNIEnv *env, jclass jc, int a3, int a4, size_t na);
void Java_jni_Linuxc_sendMsgUartHex(JNIEnv *env, jclass jc, int a3, int a4, size_t a5);
jint Java_jni_Linuxc_setUart(JNIEnv *env, jclass jc, jint descriptor, int a4);
jint Java_jni_Linuxc_closeUart(JNIEnv *env, jclass jc, jint descriptor);
jint Java_jni_Linuxc_openUart(JNIEnv *env, jclass jc, jstring jPath);

#endif //P6300LIB_READER_H
