#include <jni.h>
#include <fcntl.h>
#include <termio.h>
#include <android/log.h>
#include "firmware.h"
#include <stdio.h>
#include <stdbool.h>

// устройство считывателя
// /dev/ttyMT2

// низкоуровневая работа с устройством
int32_t poweron();

int32_t poweroff();

int32_t openportserial(uint8_t *ppath, uint32_t baud);

int32_t writeport(void *pout, int32_t oldsize);

ssize_t readportserial(void *pout, uint32_t oldsize);

void closeport();

void closeserial();

int32_t rf_fw_download(uint8_t *path);

// сервисные функции
void Cleantemp();

void Reset();

void MagicCheckSum(void *pBuffer, uint32_t nLen);

bool MagicIsCheckSum(void *pBuffer, uint32_t nLen);

uint32_t MagicMakeMessageData(uint8_t btCmd, uint8_t *pdata, uint32_t nlen);

int32_t MagicGetParameter();

int32_t MagicInventory();

int32_t MagicWriteTagMemory(int32_t nPL, uint8_t *APassword, int32_t nUL,
                            uint8_t *EPC, uint8_t membank, int32_t nSA,
                            int32_t nDL,
                            uint8_t *DT);

int32_t MagicReadTagMemory(int32_t nPL, uint8_t *APassword, uint32_t nUL,
                           uint8_t *EPC, uint8_t membank, int32_t nSA,
                           int32_t nDL);

int32_t MagicSetTransmissionPower(int32_t nPower);

int32_t MagicGetTransmissionPower();

int32_t MagicLock(uint32_t nPL, uint8_t *aPassword, uint32_t nUL, uint8_t *EPC,
                  uint32_t nLD);

int32_t MagicKill(uint32_t nPL, uint8_t *KPassword, uint32_t nUL, uint8_t *EPC);

// экспортируемые функции
jint Java_android_hardware_uhf_magic_reader_Init(JNIEnv *env, jclass jc,
                                                 jstring jPath);

jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
                                                 jstring jPath);

jint Java_android_hardware_uhf_magic_reader_Read(JNIEnv *env, jclass jc,
                                                 jbyteArray jpout, jint nStart,
                                                 jint nread);

jint Java_android_hardware_uhf_magic_reader_Write(JNIEnv *env, jclass jc,
                                                  jbyteArray jpout, jint nStart,
                                                  jint nwrite);

jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc);

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

void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc);

void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc);

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

uint8_t *device_pwr = (uint8_t *) "/dev/msm_io_cm7"; // устройство управления питанием считывателя(наше)

int32_t g_port = -1; // дескриптор порта считывателя

uint8_t Magicmessagebuf[1024]; // глобальный массив в котором строятся команды считывателю

int32_t setio = -1; // дескриптор порта управляющего устройства?

int8_t *TAG = "ScannerJNI";

#define TMP_BUFFER_SIZE 1024

int32_t writeport(void *pout, int32_t oldsize) {

    size_t count = (size_t) oldsize;
    ssize_t rc;

    if (g_port >= 0) {
        while (1) {
            rc = write(g_port, pout, count);
            if (rc >= 0) {
                if (count == rc) {
                    return oldsize;
                }
                count -= rc;
            }
        }
    }
    return 0;
}

ssize_t readportserial(void *pout, uint32_t oldsize) {

    ssize_t result = 0;

    if (g_port >= 0) {
        result = read(g_port, pout, oldsize);
    }

    return result;
}

void Cleantemp() {

    // хз где в ndk эта константа
    uint32_t FLUSH_BOTH = 2;
    if (g_port < 0) {
        ioctl(g_port, TCFLSH, FLUSH_BOTH);
    }
}

int32_t poweroff() {

    int32_t rc = -1;

    if (setio > 0) {
        rc = ioctl(setio, 1, 6);
    }

    return rc;
}

void closeport() {

    if (setio > 0) {
        ioctl(setio, 1, 8);
        close(setio);
        setio = -1;
    }

    if (g_port > 0) {
        close(g_port);
        g_port = -1;
    }

    poweroff();
}

void closeserial() {

    if (g_port > 0) {
        close(g_port);
        g_port = -1;
    }
}

int32_t openportserial(uint8_t *ppath, uint32_t baud) {

    int32_t result;
    int32_t fd;
    struct termios cfg;

    result = 1; // порт уже открыт
    if (g_port <= 0) {
        fd = open((const char *) ppath, O_RDWR);
        g_port = fd;
        if (fd > 0) {
            if (ioctl(fd, TCGETS, &cfg)) {
                close(g_port);
                result = -2; // не удалось получить конфигурацию порта
            } else {
                cfg.c_iflag = IGNPAR | INPCK | IUCLC | IXANY;
                cfg.c_oflag = OLCUC | ONLCR | OCRNL;
                cfg.c_lflag = XCASE | ECHOE | ECHOK | NOFLSH | TOSTOP | ECHOCTL
                              | ECHOPRT | ECHOKE | FLUSHO | PENDIN | 0020000;
                cfg.c_cflag = baud | CS8; // установка скорости и длины данных
                cfg.c_cc[VMIN] = 0; // минимальное количество байтов для приёма
                cfg.c_cc[VTIME] = 1; // время ожидания данных в секундах
                if (ioctl(g_port, TCSETS, &cfg)) {
                    close(g_port);
                    result = -3; // не удалось установить новую конфигурацию порта
                } else {
                    result = 0; // порт успешно открыт и установлена новая конфигурация
                }
            }
        } else {
            result = -1; // не удалось открыть порт
        }
    }
    return result;
}

int32_t poweron() {

    int32_t rc = -1;

    if (setio > 0) {
        rc = ioctl(setio, 1, 7);
    } else {
        setio = open((const char *) device_pwr, O_RDWR);
        if (setio > 0) {
            rc = ioctl(setio, 1, 7);
        }
    }
    return rc;
}

void Reset() {

    int32_t fd;

    __android_log_print(ANDROID_LOG_INFO, TAG, "reset");

    fd = open((const char *) device_pwr, O_RDWR);
    if (fd > 0) {
        ioctl(fd, 1, 2);
        ioctl(fd, 1, 6);
        ioctl(fd, 1, 8);
        usleep(300000); // 0.3 cекунды
        ioctl(fd, 1, 1);
        ioctl(fd, 1, 7);
        ioctl(fd, 1, 9);
        usleep(100000); // 0.1 cекунды
        close(fd);
    }
}

/**
 * какая-то контрольная сумма
 */
void MagicCheckSum(void *pBuffer, uint32_t nLen) {

    uint8_t *buffer = pBuffer;
    uint8_t checkSum;

    checkSum = 0;
    if (nLen > 1) {
//        uint8_t value;
//        uint32_t i = 1;
//        do {
//            value = buffer[i++];
//            checkSum = (checkSum + value) & 0xFF;
//        } while (i != nLen);
        for (uint32_t i = 1; i < nLen; i++) {
            checkSum = checkSum + buffer[i];
        }
    }
    buffer[nLen] = checkSum;
}

bool MagicIsCheckSum(void *pBuffer, uint32_t nLen) {

    uint8_t checkSum;
    uint8_t *buffer = pBuffer;
    int32_t i;
    uint8_t value;

    checkSum = 0;
    if (nLen > 1) {
        i = 1;
        do {
            value = buffer[i++];
            checkSum = (checkSum + value);
        } while (i != nLen);
    }
    if ((buffer[nLen] - checkSum) <= 0) {
        return true;
    } else {
        return false;
    }
}

/**
 * возвращает полную длину пакета
 */
uint32_t MagicMakeMessageData(uint8_t btCmd, uint8_t *pdata, uint32_t nlen) {

    uint32_t result;

    Magicmessagebuf[0] = 0xBB;
    Magicmessagebuf[1] = 0;
    Magicmessagebuf[2] = btCmd;
    memcpy(&Magicmessagebuf[3], pdata, nlen);
    Magicmessagebuf[nlen + 4] = 0x7E;
    MagicCheckSum(Magicmessagebuf, nlen + 3);
    result = nlen + 5;
    return result;
}

int32_t MagicInventory() {

    int32_t packetLen;
    int32_t result;
    uint8_t arybuf[2] = {0};

    packetLen = MagicMakeMessageData(0x22, arybuf, 2);
    result = writeport(Magicmessagebuf, packetLen);

    return result;
}

int32_t MagicWriteTagMemory(int32_t nPL, uint8_t *APassword, int32_t nUL,
                            uint8_t *EPC, uint8_t membank, int32_t nSA,
                            int32_t nDL,
                            uint8_t *DT) {

    size_t tmpPcecpLen;
    uint8_t *tmpBuff;
    uint32_t packetLen;
    int32_t result;
    uint8_t *arybuf;

    arybuf = calloc(TMP_BUFFER_SIZE, 1);

    if (arybuf == NULL) {
        return -1;
    }

    tmpPcecpLen = (size_t) nUL;

    arybuf[0] = (uint8_t) ((nPL & 0xFF00) >> 8);
    arybuf[1] = (uint8_t) nPL;

    memcpy(&arybuf[2], APassword, 4);

    arybuf[6] = (uint8_t) ((nUL & 0xFF00) >> 8);
    arybuf[7] = (uint8_t) nUL;
    memcpy(&arybuf[8], EPC, tmpPcecpLen);

    tmpBuff = &arybuf[nUL];
    tmpBuff[8] = membank;
    tmpBuff[9] = (uint8_t) ((nSA & 0xFF00) >> 8);
    tmpBuff[10] = (uint8_t) nSA;
    tmpPcecpLen += 13;
    tmpBuff[11] = (uint8_t) ((nDL & 0xFF00) >> 8);
    tmpBuff[12] = (uint8_t) nDL;
    memcpy(&arybuf[tmpPcecpLen], DT, (size_t) nDL);

    packetLen = MagicMakeMessageData(0x49, arybuf, tmpPcecpLen + nDL);

    result = writeport(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

int32_t MagicReadTagMemory(int32_t nPL, uint8_t *APassword, uint32_t nUL,
                           uint8_t *EPC, uint8_t membank, int32_t nSA,
                           int32_t nDL) {

    uint8_t *tmpArray;
    uint32_t packetLen;
    int32_t result;
    uint8_t *arybuf;

    arybuf = calloc(TMP_BUFFER_SIZE, 1);

    if (arybuf == NULL) {
        return -1;
    }

    // рамер полезной нагрузки
    arybuf[0] = (uint8_t) ((nPL & 0xFF00) >> 8);
    arybuf[1] = (uint8_t) nPL;
    // пароль к метке
    memcpy(&arybuf[2], APassword, 4);
    // размер pc+epc
    arybuf[6] = (uint8_t) ((nUL & 0xFF00) >> 8);
    arybuf[7] = (uint8_t) nUL;
    // pcepc
    memcpy(&arybuf[8], EPC, (size_t) nUL);

    tmpArray = &arybuf[nUL];
    // область памяти
    tmpArray[8] = membank;
    // смещение в памяти
    tmpArray[9] = (uint8_t) ((nSA & 0xFF00) >> 8);
    tmpArray[10] = (uint8_t) nSA;
    // размер данных для чтения
    tmpArray[11] = (uint8_t) ((nDL & 0xFF00) >> 8);
    tmpArray[12] = (uint8_t) nDL;

    // формируем команду
    packetLen = MagicMakeMessageData(0x39, arybuf, (nUL + 13));

    // отправляем команду в считыватель
    result = writeport(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

int8_t *makeHexString(void *buffer, uint32_t start, uint32_t count) {

    int8_t *result;
    int8_t *charBuffer = buffer;
    uint32_t resultLen = count * 2 + 1;

    result = calloc(resultLen, 1);
    if (result != NULL) {
        for (uint32_t i = 0; i < count; i++) {
            sprintf(&result[i * 2], "%02X", charBuffer[start + i]);
        }
    }
    return result;
}

// оригинальный вариант
int32_t rf_fw_download(unsigned char *path) {
makeHexString(NULL,0,0);
    int32_t readCount = 0;

    uint8_t end[6] = {0xD3, 0xD3, 0xD3, 0xD3, 0xD3, 0xD3};
    uint8_t cmd;
    uint8_t inBuffer[1];
    int32_t answer = 0;
    int32_t i;

    poweron();
    poweron();

    openportserial(path, B115200);

    MagicGetParameter();

    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);
    readCount += readportserial(inBuffer, 1);

    closeserial();

    if (readCount <= 4) {
        // видимо считыватель не ответил так как нам хотелось
        // видимо это проверка на наличие прошивки
        // далее идёт процесс обновления прошивки

        Reset();

        openportserial(path, B9600);

        cmd = 0xFE;
        __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xFE");
        // подключаемся на 9600
        for (i = 0; i < 10; i++) {

            writeport(&cmd, 1);
            usleep(5000); // 0.005 cекунды

            readCount = readportserial(inBuffer, 1);

            if (readCount > 0) {

                __android_log_print(ANDROID_LOG_INFO, TAG, "Прочитали = %02X",
                                    (int) inBuffer[0]);

                if (inBuffer[0] == 0xFF) {
                    // считыватель ответил
                    answer = 1;
                    break;
                }
            }

            usleep(3000); // 0.003 cекунды
        }

        // на 9600 считыватель не ответил
        // подключаемся на 115200
        if (answer == 0) {

            closeserial();
            openportserial(path, B115200);

            for (i = 0; i < 10; i++) {
                writeport(&cmd, 1);
                usleep(5000); // 0.005 cекунды

                readCount = readportserial(inBuffer, 1);

                if (readCount > 0) {

                    __android_log_print(ANDROID_LOG_INFO, TAG,
                                        "Прочитали = %02X", inBuffer[0]);

                    if (inBuffer[0] == 0xFF) {
                        // считыватель ответил
                        answer = 1;
                        break;
                    }
                }

                usleep(3000); // 0.003 cекунды
            }
        }

        // неудалось подключится к считывателю
        if (answer == 0) {
            __android_log_print(ANDROID_LOG_ERROR, TAG,
                                "Считыватель не ответил.");
            closeserial();
            return -1;
        }

        // до считывателя видимо достучались
        // начинаем заливать прошивку
        __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xB5");
        cmd = 0xB5;
        writeport(&cmd, 1);
        usleep(5000); // 0.005 cекунды
        closeserial();

        openportserial(path, B115200);
        usleep(10000); // 0.01 cекунды

        __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xDB");
        answer = 0;
        cmd = 0xDB;
        for (i = 0; i < 100; i++) {

            writeport(&cmd, 1);
            usleep(5000); // 0.005 cекунды

            readCount = readportserial(inBuffer, 1);

            if (readCount > 0) {

                __android_log_print(ANDROID_LOG_INFO, TAG, "Прочитали = %02X",
                                    inBuffer[0]);

                if (inBuffer[0] == 0xBF) {
                    // считыватель ответил на команду заливки прошивки
                    answer = 1;
                    break;
                }
            }

            usleep(3000); // 0.003 cекунды
        }

        // считыватель не ответил
        if (answer == 0) {
            __android_log_print(ANDROID_LOG_ERROR, TAG,
                                "Считыватель не ответил на команду заливки прошивки.");
            closeserial();
            return -1;
        }

        usleep(5000); // 0.005 cекунды

        __android_log_print(ANDROID_LOG_INFO, TAG,
                            "С %d попытки начинаем заливать прошивку.", i);

        __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xFD");
        cmd = 0xFD;
        writeport(&cmd, 1);
        usleep(5000); // 0.005 cекунды

        uint32_t fwIndex = 0;
        for (fwIndex = 0; fwIndex < FIRMWARE_LENGTH; fwIndex++) {
            writeport(&firmware[fwIndex], 1);
        }
        __android_log_print(ANDROID_LOG_INFO, TAG, "Записано %d байт",
                            FIRMWARE_LENGTH);

        usleep(10000); // 0.01 cекунды
        __android_log_print(ANDROID_LOG_INFO, TAG,
                            "Отправляем cmd = 0xD3D3D3D3D3D3");
        writeport(end, 6);

        __android_log_print(ANDROID_LOG_INFO, TAG,
                            "Запись прошивки успешна завершена.");

        closeserial();
        return 0;
    } else {
        // предположительно считыватель ответил как надо, всё путём
        __android_log_print(ANDROID_LOG_INFO, TAG,
                            "Прочитали %d байт, считыватель уже инициализирован.",
                            readCount);
        return 0;
    }
}

jint Java_android_hardware_uhf_magic_reader_Read(JNIEnv *env, jclass jc,
                                                 jbyteArray jpout, jint nStart,
                                                 jint nread) {

    JNIEnv e = *env;
    jsize arrayLen;
    jbyteArray arrayElements;
    uint8_t *pointer;
    ssize_t readed;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Read: %p", jc);

    arrayLen = e->GetArrayLength(env, jpout);
    arrayElements = e->GetByteArrayElements(env, jpout, 0);

    if (arrayLen > 0 && arrayElements) {
        pointer = (uint8_t *) (arrayElements + nStart);
        if (arrayLen > nread) {
            readed = readportserial(pointer, (uint32_t) nread);
        } else {
            readed = readportserial(pointer, (uint32_t) arrayLen);
        }
    } else {
        readed = 0;
    }

    e->ReleaseByteArrayElements(env, jpout, arrayElements, 0);

    return readed;
}

jint Java_android_hardware_uhf_magic_reader_WriteTag(JNIEnv *env, jclass jc,
                                                     jbyteArray jAPassword,
                                                     jint nUL,
                                                     jbyteArray jPCEPC,
                                                     jbyte membank,
                                                     jint nSA, jint nDL,
                                                     jbyteArray jDT) {

    jint passwordLen;
    jbyte *password;
    jint pcepcLen;
    jbyte *pcepc;
    jint dataLen;
    jbyte *data;

    int32_t packetLen;
    int32_t expectLen;
    int32_t result;

    __android_log_print(ANDROID_LOG_INFO, TAG, "WriteTag: %p", jc);

    passwordLen = (*env)->GetArrayLength(env, jAPassword);
    password = (*env)->GetByteArrayElements(env, jAPassword, 0);
    pcepcLen = (*env)->GetArrayLength(env, jPCEPC);
    pcepc = (*env)->GetByteArrayElements(env, jPCEPC, 0);
    dataLen = (*env)->GetArrayLength(env, jDT);
    data = (*env)->GetByteArrayElements(env, jDT, 0);

    if (passwordLen <= 3 || !password || pcepcLen < nUL || !pcepc
        || dataLen < nDL || !data
        ||
        (packetLen = MagicWriteTagMemory(nUL + 11 + nDL, (uint8_t *) password,
                                         nUL, (uint8_t *) pcepc,
                                         (uint8_t) membank, nSA, nDL,
                                         (uint8_t *) data), expectLen =
                 nUL + 22 + nDL, result =
                 16, packetLen != expectLen)) {
        result = 0x11;
    }

    (*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
    (*env)->ReleaseByteArrayElements(env, jPCEPC, pcepc, 0);
    (*env)->ReleaseByteArrayElements(env, jDT, data, 0);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_ReadTag(JNIEnv *env, jclass jc,
                                                    jbyteArray jAPassword,
                                                    jint nUL, jbyteArray jEPC,
                                                    jbyte membank,
                                                    jint nSA, jint nDL) {

    jint passwordLen;
    jbyte *password;
    jint pcepcLen;
    jbyte *pcepc;
    jint result;

    __android_log_print(ANDROID_LOG_INFO, TAG, "ReadTag: %p", jc);

    passwordLen = (*env)->GetArrayLength(env, jAPassword);
    password = (*env)->GetByteArrayElements(env, jAPassword, 0);

    pcepcLen = (*env)->GetArrayLength(env, jEPC);
    pcepc = (*env)->GetByteArrayElements(env, jEPC, 0);

    if (passwordLen <= 3 || !password || pcepcLen < nUL || !pcepc
        || (result = 16,
            MagicReadTagMemory(nUL + 11, (uint8_t *) password, (uint32_t) nUL,
                               (uint8_t *) pcepc, (uint8_t) membank, nSA,
                               nDL) != nUL + 22)) {
        result = 0x11;
    }

    (*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
    (*env)->ReleaseByteArrayElements(env, jEPC, pcepc, 0);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc) {

    __android_log_print(ANDROID_LOG_INFO, TAG, "Inventory: %p, %p", env, jc);

    return 0x11 - ((MagicInventory() - 7) <= 0);
}

void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc) {

    __android_log_print(ANDROID_LOG_INFO, TAG, "Close: %p, %p", env, jc);

    closeport();
}

void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc) {

    __android_log_print(ANDROID_LOG_INFO, TAG, "Clean: %p, %p", env, jc);

    Cleantemp();
}

jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
                                                 jstring jPath) {

    jbyte *path;
    int32_t result;
    jboolean copy = JNI_TRUE;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Open: %p", jc);

    path = (jbyte *) e->GetStringUTFChars(env, jPath, &copy);

    result = openportserial((uint8_t *) path, B115200);
    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "Открытие порта считывателя, result = %d", result);

    e->ReleaseByteArrayElements(env, jPath, path, 0);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_Init(JNIEnv *env, jclass jc,
                                                 jstring jPath) {

    jbyte *path;
    jboolean copy = JNI_TRUE;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Init: %p", jc);

    path = (jbyte *) e->GetStringUTFChars(env, jPath, &copy);

    int32_t result = rf_fw_download((uint8_t *) path);

    e->ReleaseByteArrayElements(env, jPath, path, 0);

    return result;
}

int32_t MagicGetParameter() {

    uint32_t len;
    uint8_t arybuf[2] = {0};

    len = MagicMakeMessageData(0xF1, arybuf, 2);
    return writeport(Magicmessagebuf, len);
}

jint Java_android_hardware_uhf_magic_reader_Write(JNIEnv *env, jclass jc,
                                                  jbyteArray jpout, jint nStart,
                                                  jint nwrite) {

    jint result;
    jboolean copy = JNI_TRUE;
    jbyte *data;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Write: %p", jc);

    data = e->GetByteArrayElements(env, jpout, &copy);

    result = writeport(&data[nStart], (uint32_t) nwrite);

    e->ReleaseByteArrayElements(env, jpout, data, 0);

    return result;
}

int32_t MagicSetTransmissionPower(int32_t nPower) {

    uint32_t pktLen;
    uint8_t arybuf[4];

    arybuf[0] = 0;
    arybuf[1] = 2;
    arybuf[2] = (uint8_t) ((nPower & 0xFF00) >> 8);
    arybuf[3] = (uint8_t) nPower;
    pktLen = MagicMakeMessageData(0xB6, arybuf, 4);

    return writeport(Magicmessagebuf, pktLen);
}

int32_t MagicGetTransmissionPower() {

    uint32_t packetLen;
    uint8_t arybuf[2] = {0, 0};

    packetLen = MagicMakeMessageData(0xB7, arybuf, 2);
    return writeport(Magicmessagebuf, packetLen);
}

jint Java_android_hardware_uhf_magic_reader_SetTransmissionPower(JNIEnv *env,
                                                                 jclass jc,
                                                                 jint nPower) {

    // костыль, более 17 байт прочитать не получится
    uint8_t Magicbuf[32];
    int32_t watchDog = 0;
    int32_t count = 0;

    __android_log_print(ANDROID_LOG_INFO, TAG, "SetTransmissionPower: %p, %p",
                        env, jc);

    MagicSetTransmissionPower(nPower);

    while (1) {
        count += readportserial(&Magicbuf[count], 8);
        ++watchDog;
        if (count > 7) {
            break;
        }
        // если не было прочитано более 7 символов, 20 раз подряд, возвращаем ошибку
        if (watchDog > 19) {
            return 0x11;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        return Magicbuf[5];
    }

    return 0x11;
}

jint Java_android_hardware_uhf_magic_reader_GetTransmissionPower(JNIEnv *env,
                                                                 jclass jc) {

    // костыль, более 17 байт прочитать не получится
    uint8_t Magicbuf[32];
    int32_t watchDog = 0;
    int32_t count = 0;

    __android_log_print(ANDROID_LOG_INFO, TAG, "GetTransmissionPower: %p, %p",
                        env, jc);

    MagicGetTransmissionPower();

    while (1) {
        count += readportserial(&Magicbuf[count], 9);
        ++watchDog;
        if (count > 8) {
            break;
        }
        // если не было прочитано более 8 символов, 20 раз подряд, возвращаем ошибку
        if (watchDog > 19) {
            return 0x11;
        }
    }

    if (!MagicIsCheckSum(Magicbuf, 7) || Magicbuf[0] != 0xBB
        || Magicbuf[1] != 0x01 || Magicbuf[2] != 0xB7) {
        return 0x11;
    }

    return (Magicbuf[5] << 8) | Magicbuf[6];
}

jint Java_android_hardware_uhf_magic_reader_Lock(JNIEnv *env, jclass jc,
                                                 jbyteArray jAPassword,
                                                 jint nUL, jbyteArray jEPC,
                                                 jint nLD) {

    jboolean copy = JNI_TRUE;
    jint passwordLen;
    jbyte *password;
    jbyte *PCEPC;
    jint result;
    jint PCEPCLen;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Lock: %p", jc);

    passwordLen = e->GetArrayLength(env, jAPassword);
    password = e->GetByteArrayElements(env, jAPassword, &copy);
    PCEPCLen = e->GetArrayLength(env, jEPC);
    PCEPC = e->GetByteArrayElements(env, jEPC, &copy);

    if (passwordLen <= 3 || !password || PCEPCLen < nUL || !PCEPC || (result =
                                                                              16,
            MagicLock((uint32_t) (nUL + 9), (uint8_t *) password,
                      (uint32_t) nUL, (uint8_t *) PCEPC,
                      (uint32_t) nLD) != nUL + 16)) {
        result = 17;
    }
    e->ReleaseByteArrayElements(env, jAPassword, password, 0);
    e->ReleaseByteArrayElements(env, jEPC, PCEPC, 0);
    return result;
}

int32_t MagicLock(uint32_t nPL, uint8_t *aPassword, uint32_t nUL, uint8_t *EPC,
                  uint32_t nLD) {

    uint8_t *tmpBuff;
    uint32_t packetLen;
    int32_t result;
    uint8_t *arybuf;

    arybuf = calloc(TMP_BUFFER_SIZE, 1);

    if (arybuf == NULL) {
        return -1;
    }

    arybuf[0] = (uint8_t) ((nPL & 0xFF00) >> 8);
    arybuf[1] = (uint8_t) nPL;
    memcpy(&arybuf[2], aPassword, 4);
    arybuf[6] = (uint8_t) ((nUL & 0xFF00) >> 8);
    arybuf[7] = (uint8_t) nUL;
    memcpy(&arybuf[8], EPC, (size_t) nUL);
    tmpBuff = &arybuf[nUL];
    tmpBuff[8] = (uint8_t) (nLD & 0xFF0000 >> 16);
    tmpBuff[9] = (uint8_t) ((nLD & 0xFF00) >> 8);
    tmpBuff[10] = (uint8_t) nLD;
    packetLen = MagicMakeMessageData(0x82, arybuf, nUL + 11);

    result = writeport(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_Kill(JNIEnv *env, jclass jc,
                                                 jbyteArray jKPassword,
                                                 jint nUL, jbyteArray jEPC) {

    jboolean copy = JNI_TRUE;
    jint passwordLen;
    jbyte *password;
    jbyte *PCEPC;
    jint result;
    jint PCEPCLen;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Kill: %p", jc);

    passwordLen = e->GetArrayLength(env, jKPassword);
    password = e->GetByteArrayElements(env, jKPassword, &copy);
    PCEPCLen = e->GetArrayLength(env, jEPC);
    PCEPC = e->GetByteArrayElements(env, jEPC, &copy);

    if (passwordLen <= 3 || !password || PCEPCLen < nUL || !PCEPC || (result =
                                                                              16,
            MagicKill((uint32_t) (nUL + 6), (uint8_t *) password,
                      (uint32_t) nUL, (uint8_t *) PCEPC) !=
            nUL + 13)) {
        result = 17;
    }

    e->ReleaseByteArrayElements(env, jKPassword, password, 0);
    e->ReleaseByteArrayElements(env, jEPC, PCEPC, 0);

    return result;
}

int32_t MagicKill(uint32_t nPL, uint8_t *KPassword, uint32_t nUL,
                  uint8_t *EPC) {

    uint32_t packetLen;
    int32_t result;
    uint8_t *arybuf;

    arybuf = calloc(TMP_BUFFER_SIZE, 1);

    if (arybuf == NULL) {
        return -1;
    }

    arybuf[0] = (uint8_t) ((nPL & 0xFF00) >> 8);
    arybuf[1] = (uint8_t) nPL;
    memcpy(&arybuf[2], KPassword, 4);
    arybuf[6] = (uint8_t) ((nUL & 0xFF00) >> 8);
    arybuf[7] = (uint8_t) nUL;
    memcpy(&arybuf[8], EPC, nUL);
    packetLen = MagicMakeMessageData(0x65, arybuf, nUL + 8);
    result = writeport(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}
