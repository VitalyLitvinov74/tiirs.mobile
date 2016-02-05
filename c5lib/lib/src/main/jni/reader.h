//
// Created by koputo on 05.02.16.
//

#ifndef C5TOOLAS_READER_H
#define C5TOOLAS_READER_H

// низкоуровневая работа с устройством
int32_t openSerial(int8_t *path, uint32_t baud);

int32_t closeSerial();

int32_t writeSerial(void *buffer, int32_t size);

ssize_t readSerial(void *buffer, int32_t size);

int32_t flushSerial();

int32_t openCtrl(int8_t *path);

int32_t closeCtrl();

int32_t powerOn();

int32_t powerOff();

int32_t resetCtrl();

void closeAll();

int32_t firmwareDownload(int8_t *path);

// сервисные функции
void MagicCheckSum(void *pBuffer, uint32_t nLen);

bool MagicIsCheckSum(void *pBuffer, uint32_t nLen);

uint32_t MagicMakeMessageData(uint8_t btCmd, uint8_t *pdata, uint32_t nlen);

int32_t MagicGetParameter();

int32_t MagicInventory();

int32_t MagicWriteTagMemory(int32_t pwdLen, uint8_t *pwd, int32_t pcepcLen,
                            uint8_t *pcepc, uint8_t membank, int32_t offset,
                            int32_t dataLen,
                            uint8_t *data);

int32_t MagicReadTagMemory(int32_t pwdLen, uint8_t *pwd, uint32_t pcepcLen,
                           uint8_t *pcepc, uint8_t membank, int32_t offset,
                           int32_t dataLen);

int32_t MagicSetTransmissionPower(int32_t nPower);

int32_t MagicGetTransmissionPower();

int32_t MagicLock(uint32_t nPL, uint8_t *aPassword, uint32_t nUL, uint8_t *EPC,
                  uint32_t nLD);

int32_t MagicKill(uint32_t nPL, uint8_t *KPassword, uint32_t nUL, uint8_t *EPC);


jint Java_android_hardware_uhf_magic_reader_Init(JNIEnv *env, jclass jc,
                                                 jstring jPath);

jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
                                                 jstring devicePath);

void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc);

void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc);

jint Java_android_hardware_uhf_magic_reader_Read(JNIEnv *env, jclass jc,
                                                 jbyteArray buffer, jint start,
                                                 jint count);

jint Java_android_hardware_uhf_magic_reader_Write(JNIEnv *env, jclass jc,
                                                  jbyteArray jpout, jint nStart,
                                                  jint nwrite);

jint Java_android_hardware_uhf_magic_reader_WriteTag(JNIEnv *env, jclass jc,
                                                     jbyteArray jAPassword,
                                                     jint nUL,
                                                     jbyteArray jPCEPC,
                                                     jbyte membank,
                                                     jint nSA, jint nDL,
                                                     jbyteArray jDT);

jint Java_android_hardware_uhf_magic_reader_ReadTag(JNIEnv *env, jclass jc,
                                                    jbyteArray jAPassword,
                                                    jint nUL, jbyteArray jEPC,
                                                    jbyte membank,
                                                    jint nSA, jint nDL);

jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc);

jint Java_android_hardware_uhf_magic_reader_SetTransmissionPower(JNIEnv *env,
                                                                 jclass jc,
                                                                 jint nPower);

jint Java_android_hardware_uhf_magic_reader_GetTransmissionPower(JNIEnv *env,
                                                                 jclass jc);

jint Java_android_hardware_uhf_magic_reader_Lock(JNIEnv *env, jclass jc,
                                                 jbyteArray jAPassword,
                                                 jint nUL, jbyteArray jEPC,
                                                 jint nLD);

jint Java_android_hardware_uhf_magic_reader_Kill(JNIEnv *env, jclass jc,
                                                 jbyteArray jKPassword,
                                                 jint nUL, jbyteArray jEPC);

#endif //C5TOOLAS_READER_H
