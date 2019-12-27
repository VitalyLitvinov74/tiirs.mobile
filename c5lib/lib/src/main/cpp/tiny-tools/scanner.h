//
// Created by koputo on 29.01.16.
//

#ifndef C5LIB_READER2D_H
#define C5LIB_READER2D_H

jstring Java_android_hardware_barcode_Scanner_ReadSCAAuto(JNIEnv *env, jclass jc);

jstring Java_android_hardware_barcode_Scanner_ReadSCA(JNIEnv *env, jclass jc, jint nCode);

jstring
Java_android_hardware_barcode_Scanner_ReadSCAEx(JNIEnv *env, jclass jc, jint nCommand, jint nCode);

jint Java_android_hardware_barcode_Scanner_ReadDataSCA(JNIEnv *env, jclass jc, jint nCommand,
                                                       jbyte *buf);

jbyte *Java_android_hardware_barcode_Scanner_ReadData(JNIEnv *env, jclass jc, jint nCommand);

jint Java_android_hardware_barcode_Scanner_InitSCA(JNIEnv *env, jclass jc);

jint Java_android_hardware_barcode_Scanner_CloseSCA(JNIEnv *env, jclass jc);

#endif //C5LIB_READER2D_H
