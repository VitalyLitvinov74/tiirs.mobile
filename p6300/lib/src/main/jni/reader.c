//
// Created by koputo on 28.10.16.
//
#include "reader.h"

const char *unk_27F4 = "some message"; // weak
const char *unk_2830 = "some text format"; // weak
char *_data_start; // weak
char buffer[1024]; // weak


/**
 *
 */
jint Java_jni_Linuxc_openUart(JNIEnv *env, jclass jc, jstring jPath) {
    JNIEnv e = *env;
    jboolean copy = JNI_TRUE;
    const char *path = e->GetStringUTFChars(env, jPath, &copy);;
    int32_t result;

    __android_log_print(ANDROID_LOG_INFO, TAG, "openUart: %p, %p", env, jc);

    result = open(path, O_RDWR, 438);

    e->ReleaseStringUTFChars(env, jPath, path);

    return result;
}

/**
 *
 */
jint Java_jni_Linuxc_closeUart(JNIEnv *env, jclass jc, jint handler) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "closeUart: %p, %p", env, jc);

    return close(handler);
}

/**
 *
 */
jint Java_jni_Linuxc_setUart(JNIEnv *env, jclass jc, int descriptor, int a4) {
    int v5; // r8@1
    int v6; // r3@1
    signed int v10; // [sp+0h] [bp-40h]@1

    struct termios oldcfg;
    struct termios newcfg;
    int32_t FLUSH_BOTH = 2;

    __android_log_print(ANDROID_LOG_INFO, TAG, "setUart: %p, %p", env, jc);

    v5 = a4;
    v10 = 9;

    ioctl(descriptor, TCGETS, &oldcfg);
    ioctl(descriptor, TCGETS, &newcfg);

    v6 = *(&v10 + v5);
//    unk_4B1C = 0;
//    unk_4B18 = ((unk_4B18 & 0xFFFFEFF0 | v6 & 0x100F) & 0xFFFFEFF0 | v6 & 0x100F | 0x30) & 0xFFFFFEBF;
//    newtio &= 0xFFFFE2CD;
//    unk_4B14 &= 0xFFFFFFFE;
    ioctl(descriptor, TCFLSH, FLUSH_BOTH);
//    unk_4B26 = 0;
//    unk_4B27 = 0;

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
jint Java_jni_Linuxc_receiveMsgUartHex(JNIEnv *env, jclass jc, jint descriptor) {
    int v3; // r10@1
    char *v4; // r5@1
    int v5; // r6@1
    char *v6; // r7@1
    ssize_t v7; // r0@1
    ssize_t v8; // r2@1
    char v9; // r3@2
    int result; // r0@3

    v3 = descriptor;
    v4 = buffer;
    v5 = env;
    memset(&buffer, 0, 0x400u);
    v6 = _data_start;
    memset(&_data_start, 0, 0x1000u);
    v7 = read(v3, &buffer, 0x400u);
    v8 = v7;
    if ( 2 * v7 > 0 ) {
        do {
            v9 = *(char *)v4;
            v4 = (char *)v4 + 1;
            *(char *)v6 = v9;
            v6 = (char *)v6 + 2;
        } while ( v4 != (char *)((char *)&buffer + 2 * v7) );
    }

    result = 0;
    if ( v8 > 0 ) {
        result = (*(int (**)(int, char *)) (*(int32_t *) v5 + 652))(v5, &_data_start);
    }

    return result;
}

/**
 *
 */
jint Java_jni_Linuxc_sendMsgUart(JNIEnv *env, jclass jc, int descriptor, int a4) {
    int v5; // r5@1
    int v6; // r4@1
    const void *v7; // r6@1
    size_t v8; // r0@1

    v5 = a4;
    v6 = env;
    v7 = (const void *)(*(int (**)(void))(*(int32_t *)env + 676))();
    v8 = (*(int (**)(int, int))(*(int32_t *)v6 + 656))(v6, v5);
    write(descriptor, v7, v8);
    return (*(int (**)(int, int, const void *))(*(int32_t *)v6 + 680))(v6, v5, v7);
}

/**
 *
 */
void Java_jni_Linuxc_sendMsgUartByte(JNIEnv *env, jclass jc, jint descriptor, int a4, size_t na) {

    const void *v6; // r4@1
    v6 = (const void *)(*(int (**)(void))(*(int32_t *)env + 736))();
    write(descriptor, v6, na);
    free((void *)v6);
}

/**
 *
 */
void Java_jni_Linuxc_sendMsgUartHex(JNIEnv *env, jclass jc, jint descriptor, int a4, size_t a5) {
    JNIEnv e = *env;
    int v5; // r9@1
    int v6; // r11@1
    const void *v7; // r6@1
    int v8; // r7@1
    signed int v9; // r4@2

    v5 = env;
    v6 = a4;
    v7 = malloc(2 * a5);
    v8 = (*(int (**)(int, int, int32_t))(*(int32_t *)v5 + 660))(v5, v6, 0);
    if ( (signed int)a5 > 0 ) {
        v9 = 0;
        do {
            *((char *)v7 + v9) = *(char *)(v8 + 2 * v9);
            ++v9;
            __android_log_print(4, &unk_27F4, &unk_2830, a5);
        } while ( (signed int)a5 > v9 );
    }

    write(descriptor, v7, a5);

    (*(void (**)(int, int, int))(*(int32_t *)v5 + 664))(v5, v6, v8);

    free((void *)v7);
}



