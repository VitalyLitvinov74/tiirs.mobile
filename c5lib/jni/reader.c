#include <jni.h>
#include <fcntl.h>
#include <termio.h>
#include <android/log.h>
#include <stdio.h>
#include "firmware.h"

// устройство считывателя
// /dev/ttyMT2

// низкоуровневая работа с устройством
void poweron();
void poweroff();
int openportserial(unsigned char *ppath, int baud);
int writeport(unsigned char *pout, int oldsize);
int readportserial(unsigned char *pout, int oldsize);
void closeport();
void closeserial();
int rf_fw_download(unsigned char *path);

// сервисные функции
void Cleantemp();
void Reset();
void MagicCheckSum(unsigned char *pBuffer, int nLen);
int MagicIsCheckSum(unsigned char *pBuffer, int nLen);
int MagicMakeMessageData(unsigned char btCmd, unsigned char *pdata, int nlen);
int MagicGetParameter();
int MagicInventory();
int MagicWriteTagMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL,
		unsigned char *DT);
int MagicReadTagMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL);
int MagicSetTransmissionPower(int nPower);
int MagicGetTransmissionPower();

// экспортируемые функции
jint Java_android_hardware_uhf_magic_reader_Init(JNIEnv *env, jclass jc,
		jstring strpath);
jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
		jstring strpath);
jint Java_android_hardware_uhf_magic_reader_Read(JNIEnv *env, jclass jc,
		jbyteArray jpout, jint nStart, jint nread);
jint Java_android_hardware_uhf_magic_reader_Write(JNIEnv *env, jclass jc,
		jbyteArray jpout, jint nStart, jint nwrite);
jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc);
jint Java_android_hardware_uhf_magic_reader_WriteTag(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jPCEPC, jbyte membank,
		jint nSA, jint nDL, jbyteArray jDT);
jint Java_android_hardware_uhf_magic_reader_ReadTag(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jbyte membank,
		jint nSA, jint nDL);
void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc);
void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc);
jint Java_android_hardware_uhf_magic_reader_SetTransmissionPower(JNIEnv *env,
		jclass jc, jint nPower);
jint Java_android_hardware_uhf_magic_reader_GetTransmissionPower(JNIEnv *env,
		jclass jc);
jint Java_android_hardware_uhf_magic_reader_Lock(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jint nLD);
jint Java_android_hardware_uhf_magic_reader_Kill(JNIEnv *env, jclass jc,
		jbyteArray jKPassword, jint nUL, jbyteArray jEPC);

const unsigned char *device_pwr = "/dev/msm_io_cm7"; // устройство управления питанием считывателя(наше)

int g_port = -1; // дескриптор порта считывателя

unsigned char Magicmessagebuf[1024]; // глобальный массив в котором строятся команды считывателю
int setio = -1; // дескриптор порта управляющего устройства?
unsigned char messagebuf[1024]; //

char *TAG = "ScannerJNI";

int writeport(unsigned char *pout, int oldsize) {

	int count;
	ssize_t rc;

	count = oldsize;
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

int readportserial(unsigned char *pout, int oldsize) {

	int result = 0;

	if (g_port >= 0) {
		result = read(g_port, pout, oldsize);
	}

	return result;
}

void Cleantemp() {

	// хз где в ndk эта константа
	int FLUSH_BOTH = 2;
	if (g_port < 0) {
		ioctl(g_port, TCFLSH, FLUSH_BOTH);
	}
}

void poweroff() {

	int rc;

	if (setio > 0) {
		rc = ioctl(setio, 1, 6);
	}
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

}

void closeserial() {

	if (g_port > 0) {
		close(g_port);
		g_port = -1;
	}
}

int openportserial(unsigned char *ppath, int baud) {

	int result;
	int fd;
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

void poweron() {

	int rc;

	if (setio > 0) {
		rc = ioctl(setio, 1, 7);
	} else {
		setio = open(device_pwr, O_RDWR);
		if (setio > 0) {
			rc = ioctl(setio, 1, 7);
		}
	}

}

void Reset() {

	int fd;
	int result;

	__android_log_print(ANDROID_LOG_INFO, TAG, "reset");

	fd = open(device_pwr, O_RDWR);
	if (fd > 0) {
		result = ioctl(fd, 1, 2);
		result = ioctl(fd, 1, 6);
		result = ioctl(fd, 1, 8);
		usleep(300000); // 0.3 cекунды
		result = ioctl(fd, 1, 1);
		result = ioctl(fd, 1, 7);
		result = ioctl(fd, 1, 9);
		usleep(100000); // 0.1 cекунды
		close(fd);
	}
}

/**
 * какая-то контрольная сумма
 */
void MagicCheckSum(unsigned char *pBuffer, int nLen) {

	int checkSum;
	int i;
	unsigned char value;

	checkSum = 0;
	if (nLen > 1) {
		i = 1;
		do {
			value = pBuffer[i++];
			checkSum = (checkSum + value) & 0xFF;
		} while (i != nLen);
	}
	pBuffer[nLen] = checkSum;
}

int MagicIsCheckSum(unsigned char *pBuffer, int nLen) {

	int checkSum;
	signed int i;
	int value;

	checkSum = 0;
	if (nLen > 1) {
		i = 1;
		do {
			value = pBuffer[i++];
			checkSum = (checkSum + value) & 0xFF;
		} while (i != nLen);
	}
	return (unsigned int) pBuffer[nLen] - checkSum <= 0;
}

/**
 * возвращает полную длину пакета
 */
int MagicMakeMessageData(unsigned char btCmd, unsigned char *pdata, int nlen) {

	int result;

	Magicmessagebuf[0] = 0xBB;
	Magicmessagebuf[1] = 0;
	Magicmessagebuf[2] = btCmd;
	memcpy(&Magicmessagebuf[3], pdata, nlen);
	Magicmessagebuf[nlen + 4] = 0x7E;
	MagicCheckSum(Magicmessagebuf, nlen + 3);
	result = nlen + 5;
	return result;
}

int MagicInventory() {

	int packetLen;
	int result;
	unsigned char arybuf[2] = { 0, 0 };

	packetLen = MagicMakeMessageData(0x22, arybuf, 2);
	result = writeport(Magicmessagebuf, packetLen);

	return result;
}

int MagicWriteTagMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL,
		unsigned char *DT) {

	int tmpPcecpLen;
	unsigned char *tmpBuff;
	int packetLen;
	int result;
	unsigned char arybuf[1024];

	tmpPcecpLen = nUL;

	memset(arybuf, 0, sizeof(arybuf));

	arybuf[0] = (unsigned short) (nPL & 0xFF00) >> 8;
	arybuf[1] = nPL;

	memcpy(&arybuf[2], APassword, 4);

	*(int *) &arybuf[6] = (unsigned char) ((unsigned short) (tmpPcecpLen
			& 0xFF00) >> 8);
	arybuf[7] = tmpPcecpLen;
	memcpy(&arybuf[8], EPC, tmpPcecpLen);

	tmpBuff = &arybuf[tmpPcecpLen];
	tmpBuff[8] = membank;
	tmpBuff[9] = (unsigned short) (nSA & 0xFF00) >> 8;
	tmpBuff[10] = nSA;
	tmpPcecpLen += 13;
	tmpBuff[11] = (unsigned short) (nDL & 0xFF00) >> 8;
	tmpBuff[12] = nDL;
	memcpy(&arybuf[tmpPcecpLen], DT, nDL);

	packetLen = MagicMakeMessageData(0x49, arybuf, tmpPcecpLen + nDL);

	result = writeport(Magicmessagebuf, packetLen);

	return result;
}

int MagicReadTagMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL) {

	unsigned char *tmpArray;
	int packetLen;
	int result;
	unsigned char arybuf[1024];
	int v13;

	memset(arybuf, 0, sizeof(arybuf));

	// рамер полезной нагрузки
	arybuf[0] = (unsigned short) (nPL & 0xFF00) >> 8;
	arybuf[1] = nPL;
	// пароль к метке
	memcpy(&arybuf[2], APassword, 4);
	// размер pc+epc
	*(int *) &arybuf[6] =
			(unsigned char) ((unsigned short) (nUL & 0xFF00) >> 8);
	arybuf[7] = nUL;
	// pcepc
	memcpy(&arybuf[8], EPC, nUL);

	tmpArray = &arybuf[nUL];
	// область памяти
	tmpArray[8] = membank;
	// смещение в памяти
	tmpArray[9] = (unsigned short) (nSA & 0xFF00) >> 8;
	tmpArray[10] = nSA;
	// размер данных для чтения
	tmpArray[12] = nDL;
	tmpArray[11] = (unsigned short) (nDL & 0xFF00) >> 8;

	// формируем команду
	packetLen = MagicMakeMessageData(0x39, arybuf, nUL + 13);

	// отправляем команду в считыватель
	result = writeport(Magicmessagebuf, packetLen);

	return result;
}

char * makeHexString(unsigned char *buffer, int start, int count) {

	char *result;
	int resultLen = count * 2 + 1;
	int i;

	result = malloc(resultLen);
	if (result != NULL) {
		memset(result, 0, resultLen);
		for (i = 0; i < count; i++) {
			sprintf(&result[i * 2], "%02X", buffer[start + i]);
		}

	}
	return result;
}

int rf_fw_download_new(unsigned char *path) {

	int bufferLen = 1024;
	char buffer[bufferLen];
	int index = 0;
	int readCount = 0;
	int tryCount = 8;
	int rc;

	poweron();
	poweron();

	rc = openportserial(path, B115200);

	// магическая команда считывателю
	MagicGetParameter();

	// читаем данные в ответ
	while (--tryCount >= 0) {
		readCount = readportserial(&buffer[index], bufferLen - index);
		index += readCount;
		if (index >= bufferLen) {
			index = 0;
		}
	}

	__android_log_print(ANDROID_LOG_ERROR, TAG, "index = %d", index);

	closeserial();

	// если хоть что-то считали, показываем
	if (index > 0) {
		char *readData = makeHexString(buffer, 0, index);
		__android_log_print(ANDROID_LOG_ERROR, TAG, "read data = %s", readData);
	}

	if (index <= 4) {
		// не удалось инициализировать считыватель
		// заливаем прошивку
		return -1;
	} else {
		// считаем что прошивка актуальна
		// cчитыватель инициализирован
		return 0;
	}
}

// оригинальный вариант
int rf_fw_download(unsigned char *path) {

	int readCount = 0;
	int fwIndex;

	unsigned char end[6] = { 0xD3, 0xD3, 0xD3, 0xD3, 0xD3, 0xD3 };
	unsigned char cmd;
	unsigned char recv[1];
	int answer = 0;
	int i;

	poweron();
	poweron();

	openportserial(path, B115200);

	MagicGetParameter();

	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);
	readCount += readportserial(recv, 1);

	closeserial();

	if (readCount <= 4) {
		// видимо считыватель не ответил так как нам хотелось
		// видимо это проверка на актуальную прошивку
		// далее идёт процесс обновления прошивки

		Reset();

		openportserial(path, B9600);

		cmd = 0xFE;
		__android_log_print(ANDROID_LOG_INFO, TAG, "Отправляем cmd = 0xFE");
		// подключаемся на 9600
		for (i = 0; i < 10; i++) {

			writeport(&cmd, 1);
			usleep(5000); // 0.005 cекунды

			readCount = readportserial(recv, 1);

			if (readCount > 0) {

				__android_log_print(ANDROID_LOG_INFO, TAG, "Прочитали = %02X",
						(int) recv[0]);

				if (recv[0] == 0xFF) {
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

				readCount = readportserial(recv, 1);

				if (readCount > 0) {

					__android_log_print(ANDROID_LOG_INFO, TAG,
							"Прочитали = %02X", recv[0]);

					if (recv[0] == 0xFF) {
						// считыватель ответил
						answer = 1;
						break;
					}
				}

				usleep(3000); // 0.003 cекунды
			}
		}

		// не удалось подключится к считывателю
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

			readCount = readportserial(recv, 1);

			if (readCount > 0) {

				__android_log_print(ANDROID_LOG_INFO, TAG, "Прочитали = %02X",
						(unsigned char) recv[0]);

				if (recv[0] == 0xBF) {
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

		fwIndex = 0;
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
		jbyteArray jpout, jint nStart, jint nread) {

	jsize arrayLen;
	jbyteArray arrayElements;

	unsigned char *pointer;
	int readed;

	arrayLen = (*env)->GetArrayLength(env, jpout);
	arrayElements = (*env)->GetByteArrayElements(env, jpout, 0);

	if (arrayLen > 0 && arrayElements) {
		pointer = (unsigned char *) (arrayElements + nStart);
		if (arrayLen > nread) {
			readed = readportserial(pointer, nread);
		} else {
			readed = readportserial(pointer, arrayLen);
		}
	} else {
		readed = 0;
	}

	(*env)->ReleaseByteArrayElements(env, jpout, arrayElements, 0);

	return readed;
}

jint Java_android_hardware_uhf_magic_reader_WriteTag(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jPCEPC, jbyte membank,
		jint nSA, jint nDL, jbyteArray jDT) {

	int passwordLen;
	unsigned char *password;
	int pcepcLen;
	unsigned char *pcepc;
	int dataLen;
	unsigned char *data;

	int packetLen;
	int expectLen;
	int result;

	passwordLen = (*env)->GetArrayLength(env, jAPassword);
	password = (unsigned char *) (*env)->GetByteArrayElements(env, jAPassword,
			0);
	pcepcLen = (*env)->GetArrayLength(env, jPCEPC);
	pcepc = (unsigned char *) (*env)->GetByteArrayElements(env, jPCEPC, 0);
	dataLen = (*env)->GetArrayLength(env, jDT);
	data = (unsigned char *) (*env)->GetByteArrayElements(env, jDT, 0);

	if (passwordLen <= 3 || !password || pcepcLen < nUL || !pcepc
			|| dataLen < nDL || !data
			|| (packetLen = MagicWriteTagMemory(nUL + 11 + nDL, password, nUL,
					pcepc, membank, nSA, nDL, data), expectLen = nUL + 22 + nDL, result =
					16, packetLen != expectLen)) {
		result = 0x11;
	}

	(*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
	(*env)->ReleaseByteArrayElements(env, jPCEPC, pcepc, 0);
	(*env)->ReleaseByteArrayElements(env, jDT, data, 0);

	return result;
}

jint Java_android_hardware_uhf_magic_reader_ReadTag(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jbyte membank,
		jint nSA, jint nDL) {

	int passwordLen;
	unsigned char *password;
	int pcepcLen;
	unsigned char *pcepc;
	jint result;

	passwordLen = (*env)->GetArrayLength(env, jAPassword);
	password = (unsigned char *) (*env)->GetByteArrayElements(env, jAPassword,
			0);

	pcepcLen = (*env)->GetArrayLength(env, jEPC);
	pcepc = (unsigned char *) (*env)->GetByteArrayElements(env, jEPC, 0);

	if (passwordLen <= 3 || !password || pcepcLen < nUL || !pcepc
			|| (result = 16, MagicReadTagMemory(nUL + 11, password, nUL, pcepc,
					membank, nSA, nDL) != nUL + 22)) {
		result = 0x11;
	}

	(*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
	(*env)->ReleaseByteArrayElements(env, jEPC, pcepc, 0);

	return result;
}

jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc) {
	return 0x11 - ((unsigned int) (MagicInventory() - 7) <= 0);
}

void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc) {
	closeport();
}

void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc) {
	Cleantemp();
}

jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
		jstring strpath) {

	unsigned char *path;
	int result;
	jboolean copy = JNI_TRUE;

	path = (unsigned char *) (*env)->GetStringUTFChars(env, strpath, &copy);

	result = openportserial(path, B115200);
	__android_log_print(ANDROID_LOG_INFO, TAG,
			"Открытие порта считывателя, result = %d", result);
	return result;
}

int Java_android_hardware_uhf_magic_reader_Init(JNIEnv *env, jclass jc,
		jstring strpath) {

	unsigned char *path;
	jboolean copy = JNI_TRUE;

	path = (unsigned char *) (*env)->GetStringUTFChars(env, strpath, &copy);

	return rf_fw_download(path);
}

int MagicGetParameter() {

	int len;
	unsigned char arybuf[] = { 0x00, 0x00 };

	len = MagicMakeMessageData(0xF1, arybuf, sizeof(arybuf));
	return writeport(Magicmessagebuf, len);
}

jint Java_android_hardware_uhf_magic_reader_Write(JNIEnv *env, jclass jc,
		jbyteArray jpout, jint nStart, jint nwrite) {

	int result;
	jboolean copy = JNI_TRUE;
	unsigned char *data = (*env)->GetByteArrayElements(env, jpout, &copy);
	result = writeport(data, nwrite);
	(*env)->ReleaseByteArrayElements(env, jpout, data, 0);
	return result;
}

int MagicSetTransmissionPower(int nPower) {

	int pktLen;
	unsigned char arybuf[4];

	arybuf[0] = 0;
	arybuf[1] = 2;
	arybuf[2] = nPower >> 8;
	arybuf[3] = nPower & 0x00FF;
	pktLen = MagicMakeMessageData(0xB6, arybuf, 4);

	return writeport(Magicmessagebuf, pktLen);
}

int MagicGetTransmissionPower() {

	int packetLen;
	unsigned char arybuf[5];

	memset(arybuf, 0, sizeof(arybuf));

	packetLen = MagicMakeMessageData(0xB7, arybuf, 2);
	return writeport(Magicmessagebuf, packetLen);
}

jint Java_android_hardware_uhf_magic_reader_SetTransmissionPower(JNIEnv *env,
		jclass jc, jint nPower) {

	// костыль, более 17 байт прочитать не получится
	unsigned char Magicbuf[32];
	signed int watchDog = 0;
	signed int count = 0;

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
	unsigned char Magicbuf[32];
	signed int watchDog = 0;
	signed int count = 0;

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
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jint nLD) {

	jboolean copy = JNI_TRUE;
	int passwordLen;
	unsigned char *password;
	unsigned char *PCEPC;
	jint result;
	int PCEPCLen;

	passwordLen = (*env)->GetArrayLength(env, jAPassword);
	password = (*env)->GetByteArrayElements(env, jAPassword, &copy);
	PCEPCLen = (*env)->GetArrayLength(env, jEPC);
	PCEPC = (*env)->GetByteArrayElements(env, jEPC, &copy);

	if (passwordLen <= 3 || !password || PCEPCLen < nUL || !PCEPC || (result =
			16, MagicLock(nUL + 9, password, nUL, PCEPC, nLD) != nUL + 16)) {
		result = 17;
	}
	(*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
	(*env)->ReleaseByteArrayElements(env, jEPC, PCEPC, 0);
	return result;
}

int MagicLock(int nPL, unsigned char *aPassword, int nUL, unsigned char *EPC,
		int nLD) {

	unsigned char *tmpBuff;
	int packetLen;
	int result;
	unsigned char arybuf[1024];

	memset(arybuf, 0, sizeof(arybuf));

	arybuf[0] = (nPL & 0xFF00) >> 8;
	arybuf[1] = nPL;
	memcpy(&arybuf[2], aPassword, 4);
	arybuf[6] = (nUL & 0xFF00) >> 8;
	arybuf[7] = nUL;
	memcpy(&arybuf[8], EPC, nUL);
	tmpBuff = &arybuf[nUL];
	tmpBuff[8] = nLD >> 16;
	tmpBuff[9] = (nLD & 0xFF00) >> 8;
	tmpBuff[10] = nLD;
	packetLen = MagicMakeMessageData(0x82, arybuf, nUL + 11);

	result = writeport(Magicmessagebuf, packetLen);

	return result;
}

jint Java_android_hardware_uhf_magic_reader_Kill(JNIEnv *env, jclass jc,
		jbyteArray jKPassword, jint nUL, jbyteArray jEPC) {

	jboolean copy = JNI_TRUE;
	int passwordLen;
	unsigned char *password;
	unsigned char *PCEPC;
	jint result;
	int PCEPCLen;

	passwordLen = (*env)->GetArrayLength(env, jKPassword);
	password = (unsigned char *) (*env)->GetByteArrayElements(env, jKPassword, &copy);
	PCEPCLen = (*env)->GetArrayLength(env, jEPC);
	PCEPC = (unsigned char *) (*env)->GetByteArrayElements(env, jEPC, &copy);

	if (passwordLen <= 3 || !password || PCEPCLen < nUL || !PCEPC
			|| (result = 16, MagicKill(nUL + 6, password, nUL, PCEPC) != nUL + 13)) {
		result = 17;
	}
	
	(*env)->ReleaseByteArrayElements(env, jKPassword, password, 0);
	(*env)->ReleaseByteArrayElements(env, jEPC, PCEPC, 0);
	
	return result;
}

int MagicKill(int nPL, unsigned char *KPassword, int nUL, unsigned char *EPC) {

	int packetLen;
	int result;
	unsigned char arybuf[1024];

	memset(arybuf, 0, sizeof(arybuf));

	arybuf[0] = (nPL & 0xFF00) >> 8;
	arybuf[1] = nPL;
	memcpy(&arybuf[2], KPassword, 4);
	arybuf[6] = (nUL & 0xFF00) >> 8;
	arybuf[7] = nUL;
	memcpy(&arybuf[8], EPC, nUL);
	packetLen = MagicMakeMessageData(0x65, arybuf, nUL + 8);
	result = writeport(Magicmessagebuf, packetLen);

	return result;
}
