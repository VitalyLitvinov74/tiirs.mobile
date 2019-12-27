#include <jni.h>
#include <fcntl.h>
#include <termio.h>
#include <android/log.h>
#include "firmware.h"
#include <stdio.h>
#include <stdbool.h>
#include "reader.h"
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

// данные названия не отражают суть, т.к. не известно,
// что именно делают эти команды
#define READER_START 9
#define READER_STOP 8
#define CTRL_START 1
#define CTRL_STOP 2

#define READER_POWER_ON 7
#define READER_POWER_OFF 6

// устройство считывателя
// /dev/ttyMT2

// устройство управления питанием считывателя(наше)
int8_t *deviceCtrlName = (int8_t *) "/dev/msm_io_cm7";

// дескриптор порта управляющего устройства
int32_t deviceCtrlHandler = -1;

// дескриптор порта считывателя
int32_t deviceHandler = -1;

// метка для сообщений
const char *TAG = "ScannerJNI";

#define TMP_BUFFER_SIZE 1024

// глобальный массив в котором строятся команды считывателю
uint8_t Magicmessagebuf[1024];


/**
 * Открываем устройство считывателя.
 * Возвращает
 *  0 порт открыт,
 *  1 порт уже открыт,
 * -1 не удалось открыть порт,
 * -2 не удалось получить конфигурацию порта,
 * -3 не удалось установить новую конфигурацию порта.
 */
int32_t openSerial(int8_t *path, uint32_t baud) {

    int32_t rc;
    struct termios cfg;

    rc = 1; // порт уже открыт
    if (deviceHandler < 0) {
        deviceHandler = open((const char *) path, O_RDWR);
        if (deviceHandler > 0) {
            if (ioctl(deviceHandler, TCGETS, &cfg)) {
                close(deviceHandler);
                rc = -2; // не удалось получить конфигурацию порта
            } else {
                cfg.c_iflag = IGNPAR | INPCK | IUCLC | IXANY;
                cfg.c_oflag = OLCUC | ONLCR | OCRNL;
                cfg.c_lflag = XCASE | ECHOE | ECHOK | NOFLSH | TOSTOP | ECHOCTL
                              | ECHOPRT | ECHOKE | FLUSHO | PENDIN | 0020000;
                cfg.c_cflag = baud | CS8; // установка скорости и длины данных
                cfg.c_cc[VMIN] = 0; // минимальное количество байтов для приёма
                cfg.c_cc[VTIME] = 1; // время ожидания данных в секундах
                if (ioctl(deviceHandler, TCSETS, &cfg)) {
                    close(deviceHandler);
                    rc = -3; // не удалось установить новую конфигурацию порта
                } else {
                    rc = 0; // порт успешно открыт и установлена новая конфигурация
                }
            }
        } else {
            rc = -1; // не удалось открыть порт
        }
    }

    return rc;
}

/**
 * Закрываем устройство считывателя.
 */
int32_t closeSerial() {

    int32_t rc = -1;

    if (deviceHandler > 0) {
        rc = close(deviceHandler);
    }

    deviceHandler = -1;

    return rc;
}

/**
 * Пишем в устройство считывателя.
 */
int32_t writeSerial(void *buffer, int32_t size) {

    size_t count = (size_t) size;
    ssize_t rc;

    if (deviceHandler >= 0) {
        while (1) {
            rc = write(deviceHandler, buffer, count);
            if (rc >= 0) {
                if (count == rc) {
                    return size;
                }
                count -= rc;
            }
        }
    }

    return 0;
}

/**
 * Читаем из устройства считывателя.
 */
ssize_t readSerial(void *buffer, int32_t size) {

    ssize_t rc = 0;

    if (deviceHandler >= 0) {
        rc = read(deviceHandler, buffer, (size_t) size);
        __android_log_print(ANDROID_LOG_INFO, TAG, "readed %d byte, %s", (int) rc,
                            (char *) makeHexString(buffer, 0, (int32_t) rc));
    }

    return rc;
}

/**
 * Сбрасываем буферы устройства считывателя.
 */
int32_t flushSerial() {

    int32_t rc = -1;

    uint32_t FLUSH_BOTH = 2;
    if (deviceHandler > 0) {
        rc = ioctl(deviceHandler, TCFLSH, FLUSH_BOTH);
    }

    return rc;
}

/**
 * Открываем устройство управления считывателем
 */
int32_t openCtrl(int8_t *path) {

    if (deviceCtrlHandler > 0) {
        close(deviceCtrlHandler);
    }

    deviceCtrlHandler = open((const char *) path, O_RDWR);

    return deviceCtrlHandler;
}

/**
 * Закрываем устройство управления считывателем
 */
int32_t closeCtrl() {

    int32_t rc = -1;

    if (deviceCtrlHandler > 0) {
        rc = close(deviceCtrlHandler);
    }

    return rc;
}

/**
 * Подаём питание на считыватель.
 */
int32_t powerOn() {

    int32_t rc = -1;

    if (deviceCtrlHandler > 0) {
        rc = ioctl(deviceCtrlHandler, 1, READER_POWER_ON);
    }

    return rc;
}

/**
 * Выключаем питание считывателя.
 */
int32_t powerOff() {

    int32_t rc = -1;

    if (deviceCtrlHandler > 0) {
        rc = ioctl(deviceCtrlHandler, 1, READER_POWER_OFF);
    }

    return rc;
}

/**
 * Сборос устройства управления считывателем.
 */
int32_t resetCtrl() {

    int32_t rc = -1;

    __android_log_print(ANDROID_LOG_INFO, TAG, "resetCtrl");

    if (deviceCtrlHandler > 0) {
        rc = ioctl(deviceCtrlHandler, 1, CTRL_STOP);
        if (rc < 0) {
            return rc;
        }

        rc = ioctl(deviceCtrlHandler, 1, READER_POWER_OFF);
        if (rc < 0) {
            return rc;
        }

        rc = ioctl(deviceCtrlHandler, 1, READER_STOP);
        if (rc < 0) {
            return rc;
        }

        // 0.3 cекунды
        usleep(300000);

        rc = ioctl(deviceCtrlHandler, 1, CTRL_START);
        if (rc < 0) {
            return rc;
        }

        rc = ioctl(deviceCtrlHandler, 1, READER_POWER_ON);
        if (rc < 0) {
            return rc;
        }

        rc = ioctl(deviceCtrlHandler, 1, READER_START);
        if (rc < 0) {
            return rc;
        }

        // 0.1 cекунды
        usleep(100000);
    }

    return rc;
}

/**
 * Выключаем питание считывателя, закрываем устройство управления считывателем,
 * закрываем устройство считывателя.
 */
void closeAll() {

    powerOff();

    if (deviceCtrlHandler > 0) {
        ioctl(deviceCtrlHandler, 1, READER_STOP);
        closeCtrl();
    }

    closeSerial();

}

/**
 * Создаёт строку шестнадцатеричных значений по данным из переданного буффера.
 */
int8_t *makeHexString(void *buffer, int32_t start, int32_t count) {

    int8_t *result;
    uint8_t *charBuffer = buffer;
    int32_t resultLen = count * 2 + 1;

    result = calloc((size_t) resultLen, 1);
    if (result != NULL) {
        for (int32_t i = 0; i < count; i++) {
            sprintf((char *) &result[i * 2], "%02X", charBuffer[start + i]);
        }
    }

    return result;
}

/**
 * Проверяем инициализирован ли считываетль.
 */

bool readerIsInited() {

    uint8_t Magicbuf[32] = {0};
    int32_t count = 0;
    int32_t watchDog = 0;

    // не ясно какие параметры мы получим,
    // но сам факт их наличия говорит о том, что считыватель инициализирован
    MagicGetParameter();

    // пытаемся получить ответ от считывателя
    // вообще ответ целиком занимает 11 байт в частном случае

    while (1) {
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 10) {
            // предположительно считыватель ответил как надо, всё путём
            __android_log_print(ANDROID_LOG_INFO, TAG, "На запрос параметров получили ответ: %s",
                                (char *) makeHexString(Magicbuf, 0, count));
            __android_log_print(ANDROID_LOG_INFO, TAG, "Cчитыватель инициализирован.");
            return true;
        }

        // если не было прочитано 11 символов, 20 раз подряд, возвращаем ошибку
        if (watchDog > 19) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "Cчитыватель не инициализирован.");
            return false;
        }
    }
}

/**
 * Функция заливает в считыватель прошивку.
 */
int32_t firmwareDownload(int8_t *path) {

    ssize_t readCount = 0;

    uint8_t end[6] = {0xD3, 0xD3, 0xD3, 0xD3, 0xD3, 0xD3};
    uint8_t cmd;
    uint8_t inBuffer[1];
    int32_t answer = 0;

    if (resetCtrl() < 0) {
        return -1;
    }

    if (openSerial(path, B9600) < 0) {
        return -1;
    }

    cmd = 0xFE;
    __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xFE");
    // подключаемся на 9600
    for (int32_t i = 0; i < 10; i++) {

        writeSerial(&cmd, 1);
        usleep(5000); // 0.005 cекунды

        readCount = readSerial(inBuffer, 1);

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

        closeSerial();
        if (openSerial(path, B115200) < 0) {
            return -1;
        }

        for (int32_t i = 0; i < 10; i++) {
            writeSerial(&cmd, 1);
            usleep(5000); // 0.005 cекунды

            readCount = readSerial(inBuffer, 1);

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
        closeSerial();
        return -1;
    }

    // до считывателя видимо достучались
    // начинаем заливать прошивку
    __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xB5");
    cmd = 0xB5;
    writeSerial(&cmd, 1);
    usleep(5000); // 0.005 cекунды
    closeSerial();

    if (openSerial(path, B115200) < 0) {
        return -1;
    }
    usleep(10000); // 0.01 cекунды

    __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xDB");
    answer = 0;
    cmd = 0xDB;
    int32_t tryCount;
    for (tryCount = 0; tryCount < 100; tryCount++) {

        writeSerial(&cmd, 1);
        usleep(5000); // 0.005 cекунды

        readCount = readSerial(inBuffer, 1);

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
        closeSerial();
        return -1;
    }

    usleep(5000); // 0.005 cекунды

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "С %d попытки начинаем заливать прошивку.",
                        tryCount);

    __android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xFD");
    cmd = 0xFD;
    writeSerial(&cmd, 1);
    usleep(5000); // 0.005 cекунды

    for (int32_t i = 0; i < FIRMWARE_LENGTH; i++) {
        writeSerial(&firmware[i], 1);
    }
    __android_log_print(ANDROID_LOG_INFO, TAG, "Записано %d байт",
                        FIRMWARE_LENGTH);

    usleep(10000); // 0.01 cекунды
    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "Отправляем cmd = 0xD3D3D3D3D3D3");
    writeSerial(end, 6);

    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "Запись прошивки успешна завершена.");

    closeSerial();
    return 0;
}

/**
 * Самопальная контрольная сумма.
 */
void MagicCheckSum(void *pBuffer, uint32_t nLen) {

    uint8_t *buffer = pBuffer;
    uint8_t checkSum = 0;

    if (nLen > 1) {
        for (uint32_t i = 1; i < nLen; i++) {
            checkSum = checkSum + buffer[i];
        }
    }

    buffer[nLen] = checkSum;
}

/**
 * Проверка самопальной контрольной суммы.
 */
bool MagicIsCheckSum(void *pBuffer, uint32_t nLen) {

    uint8_t checkSum = 0;
    uint8_t *buffer = pBuffer;

    if (nLen > 1) {
        for (uint32_t i = 1; i < nLen; i++) {
            checkSum = checkSum + buffer[i];
        }
    }

    return (buffer[nLen] - checkSum) <= 0;
}

/**
 * Строит указанную команду, возвращает полную длину пакета команды.
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

/**
 * Команда поиска метки.
 */
int32_t MagicInventory() {

    int32_t packetLen;
    int32_t result;
    uint8_t arybuf[2] = {0};

    packetLen = MagicMakeMessageData(INVENTORY, arybuf, 2);
    result = writeSerial(Magicmessagebuf, packetLen);

    return result;
}

/**
 * Команда чтения данных из метки.
 */
int32_t MagicReadTagMemory(int32_t pwdLen, uint8_t *pwd, uint32_t pcepcLen,
                           uint8_t *pcepc, uint8_t membank, int32_t offset,
                           int32_t dataLen) {

    uint8_t *tmpArray;
    uint32_t packetLen;
    int32_t result;
    uint8_t *arybuf;

    arybuf = calloc(TMP_BUFFER_SIZE, 1);

    if (arybuf == NULL) {
        return -1;
    }

    // рамер полезной нагрузки
    arybuf[0] = (uint8_t) ((pwdLen & 0xFF00) >> 8);
    arybuf[1] = (uint8_t) pwdLen;
    // пароль к метке
    memcpy(&arybuf[2], pwd, 4);
    // размер pc+epc
    arybuf[6] = (uint8_t) ((pcepcLen & 0xFF00) >> 8);
    arybuf[7] = (uint8_t) pcepcLen;
    // pcepc
    memcpy(&arybuf[8], pcepc, (size_t) pcepcLen);

    tmpArray = &arybuf[pcepcLen];
    // область памяти
    tmpArray[8] = membank;
    // смещение в памяти
    tmpArray[9] = (uint8_t) ((offset & 0xFF00) >> 8);
    tmpArray[10] = (uint8_t) offset;
    // размер данных для чтения
    tmpArray[11] = (uint8_t) ((dataLen & 0xFF00) >> 8);
    tmpArray[12] = (uint8_t) dataLen;

    // формируем команду
    packetLen = MagicMakeMessageData(READ_TAG_DATA, arybuf, (pcepcLen + 13));

    // отправляем команду в считыватель
    result = writeSerial(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

/**
 * Команда записи данных в метку.
 */
int32_t MagicWriteTagMemory(int32_t pwdLen, uint8_t *pwd, int32_t pcepcLen,
                            uint8_t *pcepc, uint8_t membank, int32_t offset,
                            int32_t dataLen, uint8_t *data) {

    size_t tmpPcecpLen;
    uint8_t *tmpBuff;
    uint32_t packetLen;
    int32_t result;
    uint8_t *arybuf;

    arybuf = calloc(TMP_BUFFER_SIZE, 1);

    if (arybuf == NULL) {
        return -1;
    }

    tmpPcecpLen = (size_t) pcepcLen;

    arybuf[0] = (uint8_t) ((pwdLen & 0xFF00) >> 8);
    arybuf[1] = (uint8_t) pwdLen;

    memcpy(&arybuf[2], pwd, 4);

    arybuf[6] = (uint8_t) ((pcepcLen & 0xFF00) >> 8);
    arybuf[7] = (uint8_t) pcepcLen;
    memcpy(&arybuf[8], pcepc, tmpPcecpLen);

    tmpBuff = &arybuf[pcepcLen];
    tmpBuff[8] = membank;
    tmpBuff[9] = (uint8_t) ((offset & 0xFF00) >> 8);
    tmpBuff[10] = (uint8_t) offset;
    tmpPcecpLen += 13;
    tmpBuff[11] = (uint8_t) ((dataLen & 0xFF00) >> 8);
    tmpBuff[12] = (uint8_t) dataLen;
    memcpy(&arybuf[tmpPcecpLen], data, (size_t) dataLen);

    packetLen = MagicMakeMessageData(WRITE_TAG_DATA, arybuf, (uint32_t) (tmpPcecpLen + dataLen));

    result = writeSerial(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

/**
 * Команда на получение каких-то параметров.
 */
int32_t MagicGetParameter() {

    uint32_t len;
    uint8_t arybuf[2] = {0};

    len = MagicMakeMessageData(GET_PARAMETER, arybuf, 2);
    return writeSerial(Magicmessagebuf, len);
}

/**
 * Команда установки условий доступа к областям памяти метки.
 */
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
    packetLen = MagicMakeMessageData(LOCK_TAG, arybuf, nUL + 11);

    result = writeSerial(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

/**
 * Команда вечной блокировки метки.
 */
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
    packetLen = MagicMakeMessageData(KILL_TAG, arybuf, nUL + 8);
    result = writeSerial(Magicmessagebuf, packetLen);

    free(arybuf);

    return result;
}

/**
 * Команда установки мощности передаваемого сигнала.
 */
int32_t MagicSetTransmissionPower(int32_t nPower) {

    uint32_t pktLen;
    uint8_t arybuf[4];

    arybuf[0] = 0;
    arybuf[1] = 2;
    arybuf[2] = (uint8_t) ((nPower & 0xFF00) >> 8);
    arybuf[3] = (uint8_t) nPower;
    pktLen = MagicMakeMessageData(SET_TRANSMISSION_POWER, arybuf, 4);

    return writeSerial(Magicmessagebuf, pktLen);
}

/**
 * Команда получение текущей мощности передаваемого сигнала.
 */
int32_t MagicGetTransmissionPower() {

    uint32_t packetLen;
    uint8_t arybuf[2] = {0, 0};

    packetLen = MagicMakeMessageData(GET_TRANSMISSION_POWER, arybuf, 2);
    return writeSerial(Magicmessagebuf, packetLen);
}

/**
 * Инициализация считывателя.
 * Возвращает 0 если успешно.
 */
jint Java_android_hardware_uhf_magic_reader_Init(JNIEnv *env, jclass jc,
                                                 jstring jPath) {

    jbyte *path;
    jboolean copy = JNI_TRUE;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Init: %p", jc);

    path = (jbyte *) e->GetStringUTFChars(env, jPath, &copy);

    int32_t rc;

    // открыть управляющее устройство
    rc = openCtrl(deviceCtrlName);
    if (rc < 0) {
        return -1;
    }

    // подать питание на считыватель
    rc = powerOn();
    if (rc < 0) {
        return -1;
    }

    // открыть устройство считывателя
    rc = openSerial(path, B115200);
    if (rc < 0) {
        return -1;
    }

    // проверить инициализирован он или нет, если нет залить прошивку
    bool inited = readerIsInited();
    closeSerial();
    if (!inited) {
        rc = firmwareDownload(path);
        if (rc < 0) {
            return -1;
        }

        // открыть устройство считывателя
        rc = openSerial(path, B115200);
        if (rc < 0) {
            return -1;
        }

        // проверить что прошивка залита и считыватель работает
        inited = readerIsInited();
        closeSerial();
        if (!inited) {
            return -1;
        }
    }

    // важнейшая часть инициализации!!! без установки мощности считыватель не считыватель
    int32_t power = 1950;

    rc = openSerial(path, B115200);
    if (rc < 0) {
        return -1;
    }

    rc = Java_android_hardware_uhf_magic_reader_SetTransmissionPower(NULL, NULL,
                                                                     power);
    if (rc == 0x11) {
        rc = Java_android_hardware_uhf_magic_reader_SetTransmissionPower(NULL,
                                                                         NULL,
                                                                         power);
        if (rc == 0x11) {
            rc = Java_android_hardware_uhf_magic_reader_SetTransmissionPower(
                    NULL, NULL, power);
            if (rc == 0x11) {
                return -1;
            }
        }
    }

    closeSerial();


    e->ReleaseStringUTFChars(env, jPath, (char *) path);

    return 0;
}

/**
 * Открываем порт считывателя для работы.
 */
jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
                                                 jstring devicePath) {

    jbyte *path;
    int32_t rc;
    jboolean copy = JNI_TRUE;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Open: %p", jc);

    path = (jbyte *) e->GetStringUTFChars(env, devicePath, &copy);

    rc = openSerial(path, B115200);
    __android_log_print(ANDROID_LOG_INFO, TAG,
                        "Открытие порта считывателя, rc = %d", rc);

    e->ReleaseStringUTFChars(env, devicePath, (char *) path);

    return rc;
}

/**
 * Закрываем все открытые устройства.
 */
void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc) {

    __android_log_print(ANDROID_LOG_INFO, TAG, "Close: %p, %p", env, jc);

    closeAll();
}

/**
 * Сбрасываем буферы порта считывателя.
 */
void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc) {

    __android_log_print(ANDROID_LOG_INFO, TAG, "Clean: %p, %p", env, jc);

    flushSerial();
}

/**
 * Читаем напрямую из порта считывателя.
 */
jint Java_android_hardware_uhf_magic_reader_Read(JNIEnv *env, jclass jc,
                                                 jbyteArray buffer, jint start,
                                                 jint count) {

    JNIEnv e = *env;
    jsize arrayLen;
    jbyteArray arrayElements;
    uint8_t *pointer;
    ssize_t readed;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Read: %p", jc);

    arrayLen = e->GetArrayLength(env, buffer);
    arrayElements = e->GetByteArrayElements(env, buffer, 0);

    if (arrayLen > 0 && arrayElements) {
        pointer = (uint8_t *) (arrayElements + start);
        if (arrayLen > count) {
            readed = readSerial(pointer, count);
        } else {
            readed = readSerial(pointer, arrayLen);
        }
    } else {
        readed = 0;
    }

    e->ReleaseByteArrayElements(env, buffer, arrayElements, 0);

    return (jint) readed;
}

/**
 * Пишем напрямую в порт считывателя.
 */
jint Java_android_hardware_uhf_magic_reader_Write(JNIEnv *env, jclass jc,
                                                  jbyteArray jpout, jint nStart,
                                                  jint nwrite) {

    jint result;
    jboolean copy = JNI_TRUE;
    jbyte *data;
    JNIEnv e = *env;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Write: %p", jc);

    data = e->GetByteArrayElements(env, jpout, &copy);

    result = writeSerial(&data[nStart], (uint32_t) nwrite);

    e->ReleaseByteArrayElements(env, jpout, data, 0);

    return result;
}

/**
 * Пишем данные в метку.
 */
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

/**
 * Читаем данные из метки.
 */
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

/**
 * Поиск доступной метки.
 */
jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc) {

    __android_log_print(ANDROID_LOG_INFO, TAG, "Inventory: %p, %p", env, jc);

    int32_t rc;

    rc = MagicInventory();

    // в чём тайный смысл - не ясно (rc всегда равно 7)
    return 0x11 - ((rc - 7) <= 0);
}

/**
 * Устанавливаем мощность передаваемого сигнала.
 */
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
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 7) {
            break;
        }
        // если не было прочитано более 7 символов, 20 раз подряд, возвращаем ошибку
        if (watchDog > 19) {
            return -1;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        // возвращаем код выполнения полученный от считывателя
        return Magicbuf[5];
    }

    return -1;
}

/**
 * Получаем текущую мощность передаваемого сигнала.
 */
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
        count += readSerial(&Magicbuf[count], 1);
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

/**
 * Устанавливаем маску доступа к областям метки.
 */
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

/**
 * Блокируем навечно метку.
 */
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

int32_t MagicGetChannel() {

    int32_t packetLen;
    uint8_t arybuf[5];
    int32_t result;

    memset(arybuf, 0, 5);

    packetLen = MagicMakeMessageData(GET_CHANNEL, arybuf, 2);
    result = writeSerial(Magicmessagebuf, packetLen);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_GetChannel(JNIEnv *env, jclass jc) {

    int32_t watchDog = 0;
    int32_t count = 0;
    uint8_t Magicbuf[32];

    __android_log_print(ANDROID_LOG_INFO, TAG, "MagicGetChannel: %p, %p", env, jc);

    MagicGetChannel();

    while (1) {
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 7) {
            break;
        }

        if (watchDog > 19) {
            return 17;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        return Magicbuf[5];
    }

    return 17;
}

int32_t MagicSetChannel(uint8_t channel) {

    int32_t packetLen;
    uint8_t arybuf[5];
    int32_t result;

    memset(arybuf, 0, 5);

    arybuf[1] = 1;
    arybuf[2] = channel;
    packetLen = MagicMakeMessageData(SET_CHANNEL, arybuf, 3);
    result = writeSerial(Magicmessagebuf, packetLen);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_SetChannel(JNIEnv *env, jclass jc, jbyte channel) {

    jint result;
    int32_t watchDog = 0;
    int32_t count = 0;
    uint8_t Magicbuf[32];

    __android_log_print(ANDROID_LOG_INFO, TAG, "MagicSetChannel: %p, %p, %d", env, jc, channel);

    result = 17 - ((uint32_t) (MagicSetChannel((uint8_t) channel) - 8) <= 0);

    while (1) {
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 7) {
            break;
        }

        if (watchDog > 19) {
            return 17;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        return result;
    }

    return 17;
}

int32_t MagicSetFrequency(uint8_t region) {

    int32_t packetLen;
    uint8_t arybuf[5];
    int32_t result;

    memset(arybuf, 0, 5);

    arybuf[1] = 1;
    arybuf[2] = region;
    packetLen = MagicMakeMessageData(SET_FREQUENCY, arybuf, 3);
    result = writeSerial(Magicmessagebuf, packetLen);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_SetFrequency(JNIEnv *env, jclass jc, jbyte region) {

    jint result;
    int32_t watchDog = 0;
    int32_t count = 0;
    uint8_t Magicbuf[32];

    __android_log_print(ANDROID_LOG_INFO, TAG, "MagicSetFrequency: %p, %p, %d", env, jc, region);

    result = 17 - ((uint32_t) (MagicSetFrequency((uint8_t) region) - 8) <= 0);

    while (1) {
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 7) {
            break;
        }

        if (watchDog > 19) {
            return 17;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        return result;
    }

    return 17;
}

/**
 * nPL длина полезной нагрузки
 * selPa Target, Acttion, Membank
 * nPTR смещение в Membank в битах
 * nMaskLen размер маски в битах
 * turncate обрезать/не обрезать проверяемые данные по длине маски
 * pMask маска
 *
 */
int32_t MagicSelect(int16_t nPL, uint8_t selPa, int32_t nPTR, uint8_t nMaskLen, uint8_t turncate,
                    uint8_t *pMask) {

    int32_t pktlen;
    int32_t result;
    uint8_t arybuf[50];

    memset(arybuf, 0, 50);

    arybuf[0] = (uint8_t) (nPL & 0xFF00) >> 8;
    arybuf[1] = (uint8_t) nPL;
    arybuf[2] = selPa;
    arybuf[3] = (uint8_t) ((nPTR & 0xff000000) >> 24);
    arybuf[4] = (uint8_t) ((nPTR & 0x00ff0000) >> 16);
    arybuf[5] = (uint8_t) ((nPTR & 0x0000ff00) >> 8);
    arybuf[6] = (uint8_t) (nPTR & 0x000000ff);
    arybuf[7] = nMaskLen;
    arybuf[8] = turncate;
    memcpy(&arybuf[9], pMask, (size_t) (nMaskLen / 8));

    uint32_t dataLen = (uint32_t) nMaskLen / 8 + 9;
    pktlen = MagicMakeMessageData(SELECT, arybuf, dataLen);
    int8_t *pkt = makeHexString(Magicmessagebuf, 0, pktlen);
    __android_log_print(ANDROID_LOG_INFO, TAG, "Select pkt: %s", (char *) pkt);
    result = writeSerial(Magicmessagebuf, pktlen);

    return result;
}

jint Java_android_hardware_uhf_magic_reader_Select(JNIEnv *env, jclass jc,
                                                   jbyte selPa, jint nPTR, jbyte nMaskLen,
                                                   jbyte turncate, jbyteArray jpMask) {

    JNIEnv e = *env;
    uint8_t Magicbuf[32];
    int32_t maskLen;
    uint8_t *mask;
    jint result;
    int32_t watchDog = 0;
    int32_t count = 0;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Select: %p, %p", env, jc);

    maskLen = e->GetArrayLength(env, jpMask) * 8;
    mask = (uint8_t *) e->GetByteArrayElements(env, jpMask, 0);

    if (!mask || maskLen < nMaskLen) {
        return 1;
    }

    MagicSelect(
            (int16_t) ((nMaskLen >> 3) + ((nMaskLen % 8) > 0) + 7),
            (uint8_t) selPa,
            nPTR,
            (uint8_t) nMaskLen,
            (uint8_t) turncate,
            mask);

    while (1) {
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 7) {
            break;
        }

        if (watchDog > 19) {
            goto END;
        }
    }

    if (!MagicIsCheckSum(Magicbuf, 6)) {
        END:
        result = 1;
    } else {
        result = Magicbuf[5];
    }

    e->ReleaseByteArrayElements(env, jpMask, (jbyte *) mask, 0);

    return result;
}

int32_t MagicSetSelect(int32_t nPL, uint8_t data) {

    int32_t size;
    uint8_t arybuf[5];

    memset(arybuf, 0, 5);
    arybuf[0] = (uint8_t) ((nPL & 0xff00) >> 8);
    arybuf[1] = (uint8_t) (nPL & 0x00ff);
    arybuf[2] = data;
    size = MagicMakeMessageData(SET_SELECT, arybuf, 3);
    return writeSerial(Magicmessagebuf, size);
}

jint Java_android_hardware_uhf_magic_reader_SetSelect(JNIEnv *env, jclass jc, jbyte data) {

    uint8_t Magicbuf[32];
    int32_t watchDog = 0;
    int32_t count = 0;

    __android_log_print(ANDROID_LOG_INFO, TAG, "Select: %p, %p", env, jc);

    MagicSetSelect(1, (uint8_t) data);
    while (1) {
        count += readSerial(&Magicbuf[count], 1);
        ++watchDog;
        if (count > 7) {
            break;
        }

        if (watchDog > 19) {
            return 1;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        return Magicbuf[5];
    }

    return 1;
}

int32_t MagicMultiInventory(int32_t ntimes) {
    int32_t pcktlen;
    uint8_t arybuf[5] = {0};

    arybuf[1] = 3;
    arybuf[2] = 34;
    arybuf[3] = (uint8_t) ((ntimes & 0xff00) >> 8);
    arybuf[4] = (uint8_t) (ntimes & 0x00ff);
    pcktlen = MagicMakeMessageData(MULTI_INVENTORY, arybuf, 5);
    return writeSerial(Magicmessagebuf, pcktlen);
}

int32_t MagicStopMultiInventory() {
    int pcktlen;
    uint8_t arybuf[2] = {0};

    pcktlen = MagicMakeMessageData(STOP_MULTI_INVENTORY, arybuf, 2);
    return writeSerial(Magicmessagebuf, pcktlen);
}

jint Java_android_hardware_uhf_magic_reader_MultiInventory(JNIEnv *env, jclass jc, jint ntimes) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "MultiInventory: %p, %p, %d", env, jc, ntimes);
    return 17 - ((uint32_t) (MagicMultiInventory(ntimes) - 10) <= 0);
}

jint Java_android_hardware_uhf_magic_reader_StopMultiInventory(JNIEnv *env, jclass jc) {
    jint result;
    int32_t watchDog = 0;
    int32_t count = 0;
    uint8_t Magicbuf[32] = {0};

    __android_log_print(ANDROID_LOG_INFO, TAG, "StopMultiInventory: %p, %p", env, jc);

    result = 0x11 - ((uint32_t) (MagicStopMultiInventory() - 7) <= 0);
    while (1) {
        count += readSerial(&Magicbuf[count], 8);
        ++watchDog;
        if (count > 7) {
            break;
        }

        if (watchDog > 19) {
            return 0x11;
        }
    }

    if (MagicIsCheckSum(Magicbuf, 6)) {
        return result;
    }

    return 0x11;
}

