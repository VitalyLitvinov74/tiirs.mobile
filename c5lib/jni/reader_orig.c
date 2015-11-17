#include <jni.h>
#include <fcntl.h>
#include <termio.h>
#include <android/log.h>

// устройство считывателя
// /dev/ttyMT2

#define LOWORD(l) ((short)(l))
#define HIWORD(l) ((short)(((int)(l) >> 16) & 0xFFFF))

#define LOBYTE(l) ((char)(l))
#define HIBYTE(l) ((char)(((short)(l) >> 16) & 0xFFFF))

#define BYTE0(l) ((char)((int)(l) &0x000000FF))
#define BYTE1(l) ((char)(((int)(l) >> 8) &0x000000FF))
#define BYTE2(l) ((char)(((int)(l) >> 16) &0x000000FF))
#define BYTE3(l) ((char)(((int)(l) >> 24) &0x000000FF))

void CheckSum(unsigned char *pBuffer, int nLen);
int IsCheckSum(unsigned char *pBuffer, int nLen);
int MakeMessage(unsigned char btReadId, unsigned char btCmd);
int MakeMessageData(unsigned char btReadId, unsigned char btCmd,
		unsigned char *pdata, int nlen);
int QueryTagISO18000(unsigned char btReadId, unsigned char *pbtAryUID,
		unsigned char btWordAdd);
int LockTagISO18000(unsigned char btReadId, unsigned char *pbtAryUID,
		unsigned char btWordAdd);
int ReadTagISO18000(unsigned char btReadId, unsigned char *pbtAryUID,
		unsigned char btWordAdd, unsigned char btWordCnt);
int InventoryISO18000(unsigned char btReadId);
int GetBufferDataFrameInterval12(unsigned char btReadId);
int SetBufferDataFrameInterval12(unsigned char btReadId,
		unsigned char btInterval);
int ResetInventoryBuffer12(unsigned char btReadId);
int GetInventoryBufferTagCount12(unsigned char btReadId);
int GetAndResetInventoryBuffer12(unsigned char btReadId);
int GetInventoryBuffer12(unsigned char btReadId);
int FastSwitchInventory12(unsigned char btReadId, unsigned char *pbtAryData);
int InventoryReal12(unsigned char btReadId, unsigned char byRound);
int GetAccessEpcMatch12(unsigned char btReadId);
int CancelAccessEpcMatch(unsigned char btReadId, unsigned char btMode);
int KillTag12(unsigned char btReadId, unsigned char *pbtAryPassWord);
int LockTag12(unsigned char btReadId, unsigned char *pbtAryPassWord,
		unsigned char btMembank, unsigned char btLockType);
int ReadTag12(unsigned char btReadId, unsigned char btMemBank,
		unsigned char btWordAdd, unsigned char btWordCnt,
		unsigned char *pPassword);
int CustomizedInventory12(unsigned char btReadId, unsigned char session,
		unsigned char target, unsigned char byRound);
int Inventory12(unsigned char btReadId, unsigned char byRepeat);
int SetReaderIdentifier(unsigned char btReadId, unsigned char *identifier);
int GetReaderIdentifier(unsigned char btReadId);
int GetRfProfile(unsigned char btReadId);
int SetRfProfile(unsigned char btReadId, unsigned char btProfile);
int SetImpinjFastTid(unsigned char btReadId, unsigned char btFastTid);
int GetImpinjFastTid(unsigned char btReadId);
int GetAntDetector12(unsigned char btReadId);
int SetAntDetector12(unsigned char btReadId, unsigned char btDetectorStatus);
int WriteGpioValue12(unsigned char btReadId, unsigned char btChooseGpio,
		unsigned char btGpioValue);
int ReadGpioValue12(unsigned char btReadId);
int GetDrmMode12(unsigned char btReadId);
int SetDrmMode12(unsigned char btReadId, unsigned char btDrmMode);
int GetReaderTemperature(unsigned char btReadId);
int SetBeepMode(unsigned char btReadId, unsigned char btMode);
int GetFrequencyRegion12(unsigned char btReadId);
int SetUserDefineFrequency(unsigned char btReadId, int nStartFreq,
		unsigned char btFreqSpace, unsigned char btRreqQuantity);
int SetFrequencyRegion(unsigned char btReadId, unsigned char btRegion,
		unsigned char btStartRegion, unsigned char btEndRegion);
int GetRFReturnLoss(unsigned char btReadId, unsigned char btFrequency);
int GetOutputPower(unsigned char btReadId);
int SetOutputPower(unsigned char btReadId, unsigned char btOutputPower);
int GetWorkAntenna(unsigned char btReadId);
int SetWorkAntenna(unsigned char btReadId, unsigned char btWorkAntenna);
int SetReaderAddress(unsigned char btReadId, unsigned char btNewReadId);
int GetFirmwareVersion(unsigned char btReadId);
int ResetDevice(unsigned char btReadId);
int WriteTagISO18000(unsigned char btReadId, unsigned char btIDlen,
		unsigned char *pbtAryUID, unsigned char btWordAdd, unsigned char btlen,
		unsigned char *btBuffer);
int SetAccessEpcMatch(unsigned char btReadId, unsigned char btMode,
		unsigned char btEpcLen, unsigned char *pbtAryEpc);
int WriteTag12(unsigned char btReadId, unsigned char *pbtAryPassWord,
		unsigned char btMemBank, unsigned char btWordAdd,
		unsigned char btWordCnt, unsigned char *pbtAryData, int nlen);
int check(int data);
int writeport(unsigned char *pout, int oldsize);
int readportserial(unsigned char *pout, int oldsize);
void Cleantemp();
void poweroff();
void closeport();
void closeserial();
int openportserial(unsigned char *ppath, int baud);
void poweron();
void Reset();
int openport(unsigned char *ppath);
int IsCilico();
int readbuf(unsigned char *pout, int oldsize);
int readbufauto(unsigned char *pout, int oldsize);
int readport(unsigned char *pout, int oldsize);
void MagicCheckSum(unsigned char *pBuffer, int nLen);
int MagicIsCheckSum(unsigned char *pBuffer, int nLen);
int MagicMakeMessage(unsigned char btCmd);
int MagicMakeMessageData(unsigned char btCmd, unsigned char *pdata, int nlen);
int MagicSetIOParameter(unsigned char p1, unsigned char p2, unsigned char p3);
int MagicTestRssi();
int MagicScanJammer();
int MagicSetParameter(unsigned char bMixer, unsigned char bIF, int nThrd);
int MagicGetParameter();
int MagicSetContinuousCarrier(unsigned char bOn);
int MagicSetTransmissionPower(int nPower);
int MagicGetTransmissionPower();
int MagicSetAutoFrequencyHopping(unsigned char bAuto);
int MagicGetChannel();
int MagicSetChannel(unsigned char channel);
int MagicSetFrequency(unsigned char region);
int MagicSetQuery(int nPara);
int MagicQuery();
int MagicSetSelect(int nPL, unsigned char data);
int MagicStopMultiInventory();
int MagicMultiInventory(int ntimes);
int MagicInventory();
int MagicKill(int nPL, unsigned char *KPassword, int nUL, unsigned char *EPC);
int MagicLock(int nPL, unsigned char *aPassword, int nUL, unsigned char *EPC,
		int nLD);
int MagicWriteLableMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL,
		unsigned char *DT);
int MagicReadLableMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL);
int MagicSelect(int nPL, unsigned char selPa, int nPTR, unsigned char nMaskLen,
		unsigned char turncate, unsigned char *pMask);
int rf_fw_download(unsigned char *path);

jint Java_android_hardware_uhf_magic_reader_Read(JNIEnv *env, jclass jc,
		jbyteArray jpout, jint nStart, jint nread);
jint Java_android_hardware_uhf_magic_reader_SetIOParameter(JNIEnv *env,
		jclass jc, jbyte p1, jbyte p2, jbyte p3, jbyteArray jout);
jint Java_android_hardware_uhf_magic_reader_TestRssi(JNIEnv *env, jclass jc,
		jbyteArray jout);
jint Java_android_hardware_uhf_magic_reader_ScanJammer(JNIEnv *env, jclass jc,
		jbyteArray jout);
jint Java_android_hardware_uhf_magic_reader_SetParameter(JNIEnv *env, jclass jc,
		jbyte bMixer, jbyte bIF, jint nThrd);
jint Java_android_hardware_uhf_magic_reader_GetParameter(JNIEnv *env, jclass jc,
		jbyteArray jout);
jint Java_android_hardware_uhf_magic_reader_SetContinuousCarrier(JNIEnv *env,
		jclass jc, jbyte bOn);
jint Java_android_hardware_uhf_magic_reader_SetTransmissionPower(JNIEnv *env,
		jclass jc, jint nPower);
jint Java_android_hardware_uhf_magic_reader_GetTransmissionPower(JNIEnv *env,
		jclass jc);
jint Java_android_hardware_uhf_magic_reader_SetAutoFrequencyHopping(JNIEnv *env,
		jclass jc, jbyte bAuto);
jint Java_android_hardware_uhf_magic_reader_GetChannel(JNIEnv *env, jclass jc);
jint Java_android_hardware_uhf_magic_reader_SetChannel(JNIEnv *env, jclass jc,
		jbyte channel);
jint Java_android_hardware_uhf_magic_reader_SetFrequency(JNIEnv *env, jclass jc,
		jbyte region);
jint Java_android_hardware_uhf_magic_reader_SetQuery(JNIEnv *env, jclass jc,
		jint nPara);
jint Java_android_hardware_uhf_magic_reader_Query(JNIEnv *env, jclass jc);
jint Java_android_hardware_uhf_magic_reader_Kill(JNIEnv *env, jclass jc,
		jbyteArray jKPassword, jint nUL, jbyteArray jEPC);
jint Java_android_hardware_uhf_magic_reader_Lock(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jint nLD);
jint Java_android_hardware_uhf_magic_reader_WriteLable(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jPCEPC, jbyte membank,
		jint nSA, jint nDL, jbyteArray jDT);
jint Java_android_hardware_uhf_magic_reader_ReadLable(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jbyte membank,
		jint nSA, jint nDL);
jint Java_android_hardware_uhf_magic_reader_SetSelect(JNIEnv *env, jclass jc,
		jbyte data);
jint Java_android_hardware_uhf_magic_reader_Select(JNIEnv *env, jclass jc,
		jbyte selPa, jint nPTR, jbyte nMaskLen, jbyte turncate,
		jbyteArray jpMask);
jint Java_android_hardware_uhf_magic_reader_StopMultiInventory(JNIEnv *env,
		jclass jc);
jint Java_android_hardware_uhf_magic_reader_MultiInventory(JNIEnv *env,
		jclass jc, jint ntimes);
jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc);
void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc);
void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc);
jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
		jstring strpath);
void Java_android_hardware_uhf_magic_reader_init(JNIEnv *env, jclass jc,
		jstring strpath);

//-------------------------------------------------------------------------
// Data declarations

const unsigned char name980_3812[12] = "/dev/ttyMT2"; // idb
const unsigned char name719q_3813[11] = "/dev/ttyS4"; // idb
const unsigned char name980m2_3814[12] = "/dev/ttyMT4"; // idb
const unsigned char *aDevMsm_io_cm7 = "/dev/msm_io_cm7";
const unsigned char * const device_pwr = (const unsigned char *) &aDevMsm_io_cm7; // idb
const unsigned char *aDevScan = "/dev/scan";
const unsigned char * const device_pwr_388 = (const unsigned char *) &aDevScan; // idb
int g_port = -1; // дескриптор порта считывателя
unsigned char messagebuf[50]; // idb
unsigned char Magicmessagebuf[50]; // idb
int setio; // дескриптор порта управляющего устройства?
unsigned char buf[50]; // idb
unsigned char Magicbuf[50]; // idb

int firmwareLen = 14388;
unsigned char *firmware_magicrf;

//----- (000027FC) --------------------------------------------------------
void CheckSum(unsigned char *pBuffer, int nLen) {
	int v2; // r2@1
	int v3; // r3@2
	int v4; // r4@3

	v2 = 0;
	if (nLen > 0) {
		v3 = 0;
		do {
			v4 = pBuffer[v3++];
			v2 = (v2 + v4) & 0xFF;
		} while (v3 != nLen);
		v2 = 255 * v2 & 0xFF;
	}
	pBuffer[nLen] = v2;
}

//----- (00002820) --------------------------------------------------------
int IsCheckSum(unsigned char *pBuffer, int nLen) {
	int v2; // r2@1
	int v3; // r3@2
	int v4; // r4@3

	v2 = 0;
	if (nLen > 0) {
		v3 = 0;
		do {
			v4 = pBuffer[v3++];
			v2 = (v2 + v4) & 0xFF;
		} while (v3 != nLen);
		v2 = -v2 & 0xFF;
	}
	return (unsigned int) pBuffer[nLen] - v2 <= 0;
}

//----- (00002848) --------------------------------------------------------
int MakeMessage(unsigned char btReadId, unsigned char btCmd) {
	messagebuf[0] = -96;
	messagebuf[1] = 3;
	messagebuf[2] = btReadId;
	messagebuf[3] = btCmd;
	CheckSum(messagebuf, 4);
	return 5;
}

//----- (00002874) --------------------------------------------------------
int MakeMessageData(unsigned char btReadId, unsigned char btCmd,
		unsigned char *pdata, int nlen) {
	int v4; // r5@1

	v4 = nlen;
	messagebuf[0] = -96;
	messagebuf[1] = nlen + 3;
	messagebuf[2] = btReadId;
	messagebuf[3] = btCmd;
	memcpy(&messagebuf[4], pdata, nlen);
	CheckSum(messagebuf, v4 + 4);
	return v4 + 5;
}

//----- (000028AC) --------------------------------------------------------
int QueryTagISO18000(unsigned char btReadId, unsigned char *pbtAryUID,
		unsigned char btWordAdd) {
	int v5; // r0@1
	int result; // r0@1
	unsigned char btAryBuffer[9]; // [sp+0h] [bp-28h]@1
	memcpy(btAryBuffer, pbtAryUID, 8u);
	v5 = MakeMessageData(btReadId, 0xB4u, btAryBuffer, 9);
	result = writeport(messagebuf, v5);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00002908) --------------------------------------------------------
int LockTagISO18000(unsigned char btReadId, unsigned char *pbtAryUID,
		unsigned char btWordAdd) {
	unsigned char v3; // r8@1
	unsigned char v4; // r7@1
	int v5; // r0@1
	int result; // r0@1
	unsigned char btAryBuffer[9]; // [sp+0h] [bp-28h]@1
	unsigned char v8; // [sp+9h] [bp-1Fh]@1

	v3 = btReadId;
	v4 = btWordAdd;
	memcpy(btAryBuffer, pbtAryUID, 8u);
	v8 = v4;
	v5 = MakeMessageData(v3, 0xB3u, btAryBuffer, 9);
	result = writeport(messagebuf, v5);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00002964) --------------------------------------------------------
int ReadTagISO18000(unsigned char btReadId, unsigned char *pbtAryUID,
		unsigned char btWordAdd, unsigned char btWordCnt) {
	unsigned char v4; // r8@1
	unsigned char v5; // r9@1
	unsigned char v6; // r7@1
	int v7; // r0@1
	int result; // r0@1
	unsigned char btAryBuffer[10]; // [sp+0h] [bp-30h]@1
	int v10; // [sp+Ch] [bp-24h]@1

	v4 = btWordCnt;
	v5 = btReadId;
	v6 = btWordAdd;
	////v10 = _stack_chk_guard;
	memcpy(btAryBuffer, pbtAryUID, 8u);
	btAryBuffer[9] = v4;
	btAryBuffer[8] = v6;
	v7 = MakeMessageData(v5, 0xB1u, btAryBuffer, 10);
	result = writeport(messagebuf, v7);
	////if ( v10 != _stack_chk_guard ) _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (000029C8) --------------------------------------------------------
int InventoryISO18000(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0xB0u);
	return writeport(messagebuf, v1);
}

//----- (000029E8) --------------------------------------------------------
int GetBufferDataFrameInterval12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x95u);
	return writeport(messagebuf, v1);
}

//----- (00002A08) --------------------------------------------------------
int SetBufferDataFrameInterval12(unsigned char btReadId,
		unsigned char btInterval) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btInterval;
	v2 = MakeMessageData(btReadId, 0x94u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002A34) --------------------------------------------------------
int ResetInventoryBuffer12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x93u);
	return writeport(messagebuf, v1);
}

//----- (00002A54) --------------------------------------------------------
int GetInventoryBufferTagCount12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x92u);
	return writeport(messagebuf, v1);
}

//----- (00002A74) --------------------------------------------------------
int GetAndResetInventoryBuffer12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x91u);
	return writeport(messagebuf, v1);
}

//----- (00002A94) --------------------------------------------------------
int GetInventoryBuffer12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x90u);
	return writeport(messagebuf, v1);
}

//----- (00002AB4) --------------------------------------------------------
int FastSwitchInventory12(unsigned char btReadId, unsigned char *pbtAryData) {
	int v2; // r0@1

	v2 = MakeMessageData(btReadId, 0x8Au, pbtAryData, 10);
	return writeport(messagebuf, v2);
}

//----- (00002AD8) --------------------------------------------------------
int InventoryReal12(unsigned char btReadId, unsigned char byRound) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = byRound;
	v2 = MakeMessageData(btReadId, 0x89u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002B04) --------------------------------------------------------
int GetAccessEpcMatch12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x86u);
	return writeport(messagebuf, v1);
}

//----- (00002B24) --------------------------------------------------------
int CancelAccessEpcMatch(unsigned char btReadId, unsigned char btMode) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btMode;
	v2 = MakeMessageData(btReadId, 0x85u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002B50) --------------------------------------------------------
int KillTag12(unsigned char btReadId, unsigned char *pbtAryPassWord) {
	unsigned char v2; // r6@1
	int v3; // r0@1
	unsigned char btAryBuffer[4]; // [sp+4h] [bp-14h]@1

	v2 = btReadId;
	memcpy(btAryBuffer, pbtAryPassWord, 4u);
	v3 = MakeMessageData(v2, 0x84u, btAryBuffer, 4);
	return writeport(messagebuf, v3);
}

//----- (00002B88) --------------------------------------------------------
int LockTag12(unsigned char btReadId, unsigned char *pbtAryPassWord,
		unsigned char btMembank, unsigned char btLockType) {
	unsigned char v4; // r8@1
	unsigned char v5; // r6@1
	unsigned char v6; // r7@1
	int v7; // r0@1
	unsigned char btAryBuffer[6]; // [sp+0h] [bp-20h]@1

	v4 = btReadId;
	v5 = btMembank;
	v6 = btLockType;
	memcpy(btAryBuffer, pbtAryPassWord, 4u);
	btAryBuffer[4] = v5;
	btAryBuffer[5] = v6;
	v7 = MakeMessageData(v4, 0x83u, btAryBuffer, 6);
	return writeport(messagebuf, v7);
}

//----- (00002BD0) --------------------------------------------------------
int ReadTag12(unsigned char btReadId, unsigned char btMemBank,
		unsigned char btWordAdd, unsigned char btWordCnt,
		unsigned char *pPassword) {
	unsigned char v5; // r6@1
	int v6; // r0@1
	unsigned char btAryData[7]; // [sp+0h] [bp-18h]@1

	v5 = btReadId;
	btAryData[0] = btMemBank;
	btAryData[1] = btWordAdd;
	btAryData[2] = btWordCnt;
	memcpy(&btAryData[3], pPassword, 4u);
	v6 = MakeMessageData(v5, 0x81u, btAryData, 7);
	return writeport(messagebuf, v6);
}

//----- (00002C10) --------------------------------------------------------
int CustomizedInventory12(unsigned char btReadId, unsigned char session,
		unsigned char target, unsigned char byRound) {
	int v4; // r0@1
	unsigned char btAryData[3]; // [sp+4h] [bp-14h]@1

	btAryData[0] = session;
	btAryData[1] = target;
	btAryData[2] = byRound;
	v4 = MakeMessageData(btReadId, 0x8Bu, btAryData, 3);
	return writeport(messagebuf, v4);
}

//----- (00002C40) --------------------------------------------------------
int Inventory12(unsigned char btReadId, unsigned char byRepeat) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = byRepeat;
	v2 = MakeMessageData(btReadId, 0x80u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002C6C) --------------------------------------------------------
int SetReaderIdentifier(unsigned char btReadId, unsigned char *identifier) {
	int v2; // r0@1

	v2 = MakeMessageData(btReadId, 0x67u, identifier, 12);
	return writeport(messagebuf, v2);
}

//----- (00002C90) --------------------------------------------------------
int GetReaderIdentifier(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x68u);
	return writeport(messagebuf, v1);
}

//----- (00002CB0) --------------------------------------------------------
int GetRfProfile(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x6Au);
	return writeport(messagebuf, v1);
}

//----- (00002CD0) --------------------------------------------------------
int SetRfProfile(unsigned char btReadId, unsigned char btProfile) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btProfile;
	v2 = MakeMessageData(btReadId, 0x69u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002CFC) --------------------------------------------------------
int SetImpinjFastTid(unsigned char btReadId, unsigned char btFastTid) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btFastTid;
	v2 = MakeMessageData(btReadId, 0x8Cu, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002D28) --------------------------------------------------------
int GetImpinjFastTid(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x8Eu);
	return writeport(messagebuf, v1);
}

//----- (00002D48) --------------------------------------------------------
int GetAntDetector12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x63u);
	return writeport(messagebuf, v1);
}

//----- (00002D68) --------------------------------------------------------
int SetAntDetector12(unsigned char btReadId, unsigned char btDetectorStatus) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btDetectorStatus;
	v2 = MakeMessageData(btReadId, 0x62u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002D94) --------------------------------------------------------
int WriteGpioValue12(unsigned char btReadId, unsigned char btChooseGpio,
		unsigned char btGpioValue) {
	int v3; // r0@1
	unsigned char buf[2]; // [sp+4h] [bp-Ch]@1

	buf[0] = btChooseGpio;
	buf[1] = btGpioValue;
	v3 = MakeMessageData(btReadId, 0x61u, buf, 2);
	return writeport(messagebuf, v3);
}

//----- (00002DC4) --------------------------------------------------------
int ReadGpioValue12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x60u);
	return writeport(messagebuf, v1);
}

//----- (00002DE4) --------------------------------------------------------
int GetDrmMode12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x7Du);
	return writeport(messagebuf, v1);
}

//----- (00002E04) --------------------------------------------------------
int SetDrmMode12(unsigned char btReadId, unsigned char btDrmMode) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btDrmMode;
	v2 = MakeMessageData(btReadId, 0x7Cu, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002E30) --------------------------------------------------------
int GetReaderTemperature(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x7Bu);
	return writeport(messagebuf, v1);
}

//----- (00002E50) --------------------------------------------------------
int SetBeepMode(unsigned char btReadId, unsigned char btMode) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btMode;
	v2 = MakeMessageData(btReadId, 0x7Au, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002E7C) --------------------------------------------------------
int GetFrequencyRegion12(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x79u);
	return writeport(messagebuf, v1);
}

//----- (00002E9C) --------------------------------------------------------
int SetUserDefineFrequency(unsigned char btReadId, int nStartFreq,
		unsigned char btFreqSpace, unsigned char btRreqQuantity) {
	int v4; // r0@1
	unsigned char buf[6]; // [sp+0h] [bp-18h]@1

	buf[2] = btRreqQuantity;
	buf[3] = (unsigned int) nStartFreq >> 16;
	buf[0] = 4;
	buf[1] = btFreqSpace;
	buf[4] = BYTE1(nStartFreq);
	buf[5] = nStartFreq;
	v4 = MakeMessageData(btReadId, 0x78u, buf, 6);
	return writeport(messagebuf, v4);
}

//----- (00002ED8) --------------------------------------------------------
int SetFrequencyRegion(unsigned char btReadId, unsigned char btRegion,
		unsigned char btStartRegion, unsigned char btEndRegion) {
	int v4; // r0@1
	unsigned char buf[3]; // [sp+4h] [bp-14h]@1

	buf[0] = btRegion;
	buf[1] = btStartRegion;
	buf[2] = btEndRegion;
	v4 = MakeMessageData(btReadId, 0x78u, buf, 3);
	return writeport(messagebuf, v4);
}

//----- (00002F08) --------------------------------------------------------
int GetRFReturnLoss(unsigned char btReadId, unsigned char btFrequency) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btFrequency;
	v2 = MakeMessageData(btReadId, 0x7Eu, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002F34) --------------------------------------------------------
int GetOutputPower(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x77u);
	return writeport(messagebuf, v1);
}

//----- (00002F54) --------------------------------------------------------
int SetOutputPower(unsigned char btReadId, unsigned char btOutputPower) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btOutputPower;
	v2 = MakeMessageData(btReadId, 0x76u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002F80) --------------------------------------------------------
int GetWorkAntenna(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x75u);
	return writeport(messagebuf, v1);
}

//----- (00002FA0) --------------------------------------------------------
int SetWorkAntenna(unsigned char btReadId, unsigned char btWorkAntenna) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btWorkAntenna;
	v2 = MakeMessageData(btReadId, 0x74u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002FCC) --------------------------------------------------------
int SetReaderAddress(unsigned char btReadId, unsigned char btNewReadId) {
	int v2; // r0@1
	unsigned char v4; // [sp+7h] [bp-9h]@1

	v4 = btNewReadId;
	v2 = MakeMessageData(btReadId, 0x73u, &v4, 1);
	return writeport(messagebuf, v2);
}

//----- (00002FF8) --------------------------------------------------------
int GetFirmwareVersion(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x72u);
	return writeport(messagebuf, v1);
}

//----- (00003018) --------------------------------------------------------
int ResetDevice(unsigned char btReadId) {
	int v1; // r0@1

	v1 = MakeMessage(btReadId, 0x70u);
	return writeport(messagebuf, v1);
}

//----- (00003038) --------------------------------------------------------
int WriteTagISO18000(unsigned char btReadId, unsigned char btIDlen,
		unsigned char *pbtAryUID, unsigned char btWordAdd, unsigned char btlen,
		unsigned char *btBuffer) {
	int v6; // r5@1
	int v7; // r11@1
	int v8; // r10@1
	int v9; // r0@1
	int result; // r0@1
	int v11; // [sp+0h] [bp+0h]@1
	void *src; // [sp+4h] [bp+4h]@1
	int v13; // [sp+8h] [bp+8h]@1
	unsigned char btReadIda[4]; // [sp+Ch] [bp+Ch]@1
	int v15; // [sp+14h] [bp+14h]@1

	v13 = btWordAdd;
	v6 = btIDlen;
	*(int *) btReadIda = btReadId;
	src = btBuffer;
	////v15 = _stack_chk_guard;
	v7 = btIDlen + 2 + btlen;
	v8 = btIDlen + 2;
	memcpy(&v11, pbtAryUID, btIDlen);
	*((char *) &v11 + v6) = v13;
	*((char *) &v11 + v6 + 1) = btlen;
	memcpy((char *) &v11 + v8, src, btlen);
	v9 = MakeMessageData(btReadIda[0], 0xB2u, (unsigned char *) &v11, v7);
	result = writeport(messagebuf, v9);
	////if ( v15 != _stack_chk_guard ) _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (000030E0) --------------------------------------------------------
int SetAccessEpcMatch(unsigned char btReadId, unsigned char btMode,
		unsigned char btEpcLen, unsigned char *pbtAryEpc) {
	unsigned char v4; // r8@1
	int v5; // r9@1
	int *v6; // r6@1
	int v7; // r0@1
	int result; // r0@1
	int v9; // [sp+0h] [bp+0h]@1
	int v10; // [sp+4h] [bp+4h]@1

	v4 = btReadId;
	////v10 = _stack_chk_guard;
	v5 = btEpcLen + 2;
	v6 = &v9 - 2 * ((unsigned int) (btEpcLen + 16) >> 3);
	*(char *) v6 = btMode;
	*((char *) v6 + 1) = btEpcLen;
	memcpy((char *) v6 + 2, pbtAryEpc, btEpcLen);
	v7 = MakeMessageData(v4, 0x85u, (unsigned char *) v6, v5);
	result = writeport(messagebuf, v7);
	////if ( v10 != _stack_chk_guard )    _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00003150) --------------------------------------------------------
int WriteTag12(unsigned char btReadId, unsigned char *pbtAryPassWord,
		unsigned char btMemBank, unsigned char btWordAdd,
		unsigned char btWordCnt, unsigned char *pbtAryData, int nlen) {
	unsigned char v7; // r10@1
	unsigned char v8; // r9@1
	int v9; // r0@1
	int result; // r0@1
	int v11; // [sp+0h] [bp+0h]@1
	void *src; // [sp+4h] [bp+4h]@1
	unsigned char btReadIda[4]; // [sp+8h] [bp+8h]@1
	int v14; // [sp+Ch] [bp+Ch]@1
	int v15; // [sp+14h] [bp+14h]@1

	v7 = btWordAdd;
	v8 = btMemBank;
	src = pbtAryData;
	v14 = nlen + 7;
	*(int *) btReadIda = btReadId;
	////v15 = _stack_chk_guard;
	memcpy(&v11, pbtAryPassWord, 4u);
	// хз что здесь должно происходить
	////LOBYTE (src) = v8;
	////BYTE1 (src) = v7;
	////BYTE2 (src) = btWordCnt;
	memcpy((char *) &src + 3, src, nlen);
	v9 = MakeMessageData(btReadIda[0], 0x82u, (unsigned char *) &v11, v14);
	result = writeport(messagebuf, v9);
	////if ( v15 != _stack_chk_guard )    _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (000031F4) --------------------------------------------------------
int check(int data) {
	return ((unsigned int) BYTE1(data) >> 1) ^ ((unsigned int) BYTE2(data) >> 2)
			^ (unsigned char) data ^ (BYTE3(data) + 11);
}

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

//----- (00003284) --------------------------------------------------------
void Cleantemp() {

	// хз где в ndk эта константа
	int FLUSH_BOTH = 2;
	if (g_port < 0) {
		ioctl(g_port, TCFLSH, FLUSH_BOTH);
	}
}

void poweroff() {

	if (setio > 0) {
		ioctl(setio, 1, 6);
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

	result = 1;
	if (g_port <= 0) {
		fd = open((const char *) "/dev/ttyMSM0", O_RDWR);
		g_port = fd;
		if (fd >= 0 || (fd = open("/dev/ttyMT2", O_RDWR), g_port = fd, fd >= 0)
				|| (fd = open("/dev/ttyS4", O_RDWR), g_port = fd, fd >= 0)
				|| (fd = open("/dev/ttyMT2", O_RDWR), g_port = fd, fd >= 0)) {
			if (ioctl(fd, TCGETS, &cfg)) {
				close(g_port);
				result = -1;
			} else {
				//cfg.c_iflag &= 0xFFFFFA14;
				// 0xFFFFF000 | (IGNPAR | INPCK | IUCLC | IXANY)
				//cfg.c_iflag &= 0xFFFFF000 | (IGNPAR | INPCK | IUCLC | IXANY);
				cfg.c_iflag = IGNPAR | INPCK | IUCLC | IXANY;
				//cfg.c_oflag &= 0xFFFFFFFE;
				// 0xFFFFFFF0 | (OLCUC | ONLCR | OCRNL)
				//cfg.c_oflag &= 0xFFFFFFF0 | (OLCUC | ONLCR | OCRNL);
				cfg.c_oflag = OLCUC | ONLCR | OCRNL;
				//cfg.c_lflag &= 0xFFFF7FB4;
				// 0xFFFF0000 | (XCASE | ECHOE | ECHOK | NOFLSH | TOSTOP | ECHOCTL | ECHOPRT | ECHOKE | FLUSHO | PENDIN | 0020000)
				//cfg.c_lflag &= 0xFFFF0000 | (XCASE | ECHOE | ECHOK | NOFLSH | TOSTOP | ECHOCTL | ECHOPRT | ECHOKE | FLUSHO | PENDIN | 0020000);
				cfg.c_lflag = XCASE | ECHOE | ECHOK | NOFLSH | TOSTOP | ECHOCTL
						| ECHOPRT | ECHOKE | FLUSHO | PENDIN | 0020000;
				cfg.c_cc[VMIN] = 0; // минимальное количество байтов для приёма
				//cfg.c_cflag = baud & 0x100F | ((cfg.c_cflag & 0xFFFFFECF | 0x30) & 0xFFFFEFF0 | baud & 0x100F) & 0xFFFFEFF0;
				//cfg.c_cflag = baud & CBAUD | ((cfg.c_cflag & (~CSIZE | CS8)) & ~CBAUD | baud & CBAUD) & ~CBAUD; // установка скорости и длины данных
				//cfg.c_cflag &= (~CBAUD | (baud & CBAUD)); // установка скорости
				//cfg.c_cflag &= (~CSIZE | CS8); // установка длины данных
				cfg.c_cflag = baud | CS8; // установка скорости, длины данных
				cfg.c_cc[VTIME] = 1; // время ожидания данных в секундах
				if (ioctl(g_port, TCSETS, &cfg)) {
					close(g_port);
					result = -2;
				} else {
					result = 0;
				}
			}
		} else {
			result = 0;
		}
	}
	return result;
}

void poweron() {
	int v0;
	setio = open("/dev/msm_io_cm7", O_RDWR);
	if (setio > 0) {
		ioctl(setio, 1, 7);
	}
}

void Reset() {

	int fd;
	int fd1;
	int fd2;
	char data;

	__android_log_print(ANDROID_LOG_INFO, "ScannerJNI", "reset");

	data = 48;
		fd = open(aDevMsm_io_cm7, O_RDWR);
	fd1 = fd;
	if (fd1 < 0) {
		data = 48;
		fd2 = open(aDevScan, O_RDWR);
		write(fd2, &data, 1);
		usleep(300000);
		data = 49;
		write(fd2, &data, 1);
		close(fd2);
	} else {
		ioctl(fd1, 1, 2);
		ioctl(fd1, 1, 6);
		ioctl(fd1, 1, 8);
		usleep(300000);
		ioctl(fd1, 1, 1);
		ioctl(fd1, 1, 7);
		ioctl(fd1, 1, 9);
		usleep(100000);
		close(fd1);
	}
}
// 27A8: using guessed type int _android_log_print(int, int, const char *, ...);

//----- (0000355C) --------------------------------------------------------
int openport(unsigned char *ppath) {
	clock_t v2;
	int v3;
	int v4;
	int v5;
	int v6;
	int v7;
	size_t v8;
	ssize_t v9;
	int v10;
	clock_t v11;
	int v12;
	int result;
	unsigned char buf[2];
	struct termios cfg;

	buf[0] = 85;
	buf[1] = 85;
	setio = open("/dev/msm_io_cm7", 2);
	if (setio < 0) {
		setio = open("/dev/scan", 0);
		if (setio < 0) {
			usleep(0x2EC50000u);
			result = -20;
			goto LABEL_6;
		}
		v11 = clock() & 0xFFFFF;
		v12 = check(v11);
		if (v12 != write(setio, buf, v11)) {
			usleep(0xA43B7400);
			result = -20;
			goto LABEL_6;
		}
	} else {
		v2 = clock();
		v5 = check(v2);
		v3 = check(v2);
		v6 = ioctl(setio, v2, v5);
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
				"check=%dread=%d\r\n", v3, v6);
		v7 = check(v2);
		v8 = (unsigned short) v2;
		v9 = write(setio, buf, v8);
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
				"check=%dwrite=%d\r\n", v7, v9);
		v10 = check(v8);
		if (v10 != write(setio, buf, v8)) {
			usleep(0x2A05F200u);
			result = -20;
			goto LABEL_6;
		}
		ioctl(setio, 1u, 9);
		usleep(0x7A120u);
	}
	result = 1;
	if (g_port > 0)
		goto LABEL_6;
	g_port = open((const char *) ppath, 2);
	if (g_port < 0)
		goto LABEL_21;
	if (ioctl(g_port, 0x5401u, &cfg)) {
		close(g_port);
		result = -1;
		goto LABEL_6;
	}
	cfg.c_iflag &= 0xFFFFFA14;
	cfg.c_oflag &= 0xFFFFFFFE;
	cfg.c_cc[6] = 0;
	cfg.c_cc[5] = 1;
	cfg.c_lflag &= 0xFFFF7FB4;
	cfg.c_cflag = (((cfg.c_cflag & 0xFFFFFECF) | 0x30) & (0xFFFFEFF0 | 0x1002))
			(& 0xFFFFEFF0 | 0x1002);
	if (ioctl(g_port, 0x5402u, &cfg)) {
		close(g_port);
		result = -2;
	} else {
		LABEL_21: result = 0;
	}
	LABEL_6: return result;
}
// 27A8: using guessed type int _android_log_print(int, int, const char *, ...);
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00003760) --------------------------------------------------------
int IsCilico() {
	clock_t v0; // r5@1
	char v1; // r6@1

	v0 = clock();
	v1 = check(v0);
	return (v1 & 1u) - ioctl(setio, v0, 0) <= 0;
}

//----- (00003794) --------------------------------------------------------
int readbuf(unsigned char *pout, int oldsize) {
	unsigned char *v2; // r7@1
	int v3; // r5@1
	int v4; // r0@2
	signed int v5; // r6@4
	int v6; // r4@4
	ssize_t v7; // r0@8
	int v8; // r5@10
	int v9; // r3@11

	v2 = pout;
	v3 = oldsize;
	if (!IsCilico()) {
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
				"32523543543535435");
		usleep(0x3B9ACA00u);
		return 0;
	}
	v4 = g_port;
	if (g_port < 0 || v3 <= 0)
		return 0;
	v5 = 0;
	v6 = 0;
	while (1) {
		v7 = read(v4, &v2[v6], v3);
		if (!v7)
			break;
		v6 += v7;
		v5 = 0;
		if (v3 <= v6)
			goto LABEL_10;
		LABEL_7: v4 = g_port;
	}
	++v5;
	if (v5 <= 3) {
		if (v3 <= v6)
			goto LABEL_10;
		goto LABEL_7;
	}
	if (v6 <= 0)
		return v6;
	LABEL_10: v8 = 0;
	do {
		v9 = v2[v8++];
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI", "-%02x");
	} while (v8 != v6);
	return v6;
}
// 27A8: using guessed type int _android_log_print(int, int, const char *, ...);

//----- (00003858) --------------------------------------------------------
int readbufauto(unsigned char *pout, int oldsize) {
	unsigned char *v2; // r7@1
	int v3; // r5@1
	int v4; // r0@2
	signed int v5; // r6@4
	int v6; // r4@4
	ssize_t v7; // r0@8
	int v8; // r5@10
	int v9; // r1@11

	v2 = pout;
	v3 = oldsize;
	__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI", "readbufauto");
	if (!IsCilico()) {
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
				"32523543543535435");
		usleep(0x3B9ACA00u);
		return 0;
	}
	v4 = g_port;
	if (g_port < 0 || v3 <= 0)
		return 0;
	v5 = 0;
	v6 = 0;
	while (1) {
		v7 = read(v4, &v2[v6], v3);
		if (!v7)
			break;
		v6 += v7;
		v5 = 0;
		if (v3 <= v6)
			goto LABEL_10;
		LABEL_7: v4 = g_port;
	}
	++v5;
	if (v5 <= 3) {
		if (v3 <= v6)
			goto LABEL_10;
		goto LABEL_7;
	}
	if (v6 <= 0)
		return v6;
	LABEL_10: v8 = 0;
	do {
		v9 = v2[v8++];
		printf("-%02x", v9);
	} while (v8 != v6);
	return v6;
}
// 27A8: using guessed type int _android_log_print(int, int, const char *, ...);

//----- (00003920) --------------------------------------------------------
int readport(unsigned char *pout, int oldsize) {
	unsigned char *v2; // r6@1
	int v3; // r5@1
	int result; // r0@2

	v2 = pout;
	v3 = oldsize;
	if (IsCilico()) {
		result = 0;
		if (g_port >= 0)
			result = read(g_port, v2, v3);
	} else {
		usleep(0x3B9ACA00u);
		result = 0;
	}
	return result;
}

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

//----- (00004B3C) --------------------------------------------------------
int MagicIsCheckSum(unsigned char *pBuffer, int nLen) {
	int v2; // r2@1
	signed int v3; // r3@2
	int v4; // r4@3

	v2 = 0;
	if (nLen > 1) {
		v3 = 1;
		do {
			v4 = pBuffer[v3++];
			v2 = (v2 + v4) & 0xFF;
		} while (v3 != nLen);
	}
	return (unsigned int) pBuffer[nLen] - v2 <= 0;
}

//----- (00004B60) --------------------------------------------------------
int MagicMakeMessage(unsigned char btCmd) {
	int result; // r0@1

	Magicmessagebuf[0] = 0xBB;
	Magicmessagebuf[1] = 0;
	Magicmessagebuf[2] = btCmd;
	MagicCheckSum(Magicmessagebuf, 3);
	result = 5;
	Magicmessagebuf[4] = 0x7E;
	return result;
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

//----- (00004BBC) --------------------------------------------------------
int MagicSetIOParameter(unsigned char p1, unsigned char p2, unsigned char p3) {
	int v3; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	arybuf[0] = 0;
	arybuf[1] = 3;
	arybuf[2] = p1;
	arybuf[3] = p2;
	arybuf[4] = p3;
	v3 = MagicMakeMessageData(0x1Au, arybuf, 5);
	return writeport(Magicmessagebuf, v3);
}

//----- (00004BF0) --------------------------------------------------------
int MagicTestRssi() {
	int v0; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	arybuf[4] = 0;
	*(int *) arybuf = 0;
	v0 = MagicMakeMessageData(0xF3u, arybuf, 2);
	return writeport(Magicmessagebuf, v0);
}

//----- (00004C18) --------------------------------------------------------
int MagicScanJammer() {
	int v0; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	arybuf[4] = 0;
	*(int *) arybuf = 0;
	v0 = MagicMakeMessageData(0xF2u, arybuf, 2);
	return writeport(Magicmessagebuf, v0);
}

//----- (00004C40) --------------------------------------------------------
int MagicSetParameter(unsigned char bMixer, unsigned char bIF, int nThrd) {
	int v3; // r4@1
	unsigned char arybuf[6]; // [sp+0h] [bp-10h]@1

	arybuf[0] = 0;
	arybuf[3] = bIF;
	arybuf[1] = 4;
	arybuf[2] = bMixer;
	arybuf[4] = BYTE1(nThrd);
	arybuf[5] = nThrd;
	v3 = MagicMakeMessageData(0xF0u, arybuf, 6);
	__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI", "messa=%d", v3,
			*(int *) arybuf, *(int *) &arybuf[4]);
	return writeport(Magicmessagebuf, v3);
}
// 27A8: using guessed type int _android_log_print(int, int, const char *, ...);

int MagicGetParameter() {

	int len;
	unsigned char arybuf[5];

	memset((void*) arybuf, 0, sizeof(arybuf));

	len = MagicMakeMessageData(0xF1, arybuf, 2);
	return writeport(Magicmessagebuf, len);
}

//----- (00004CB8) --------------------------------------------------------
int MagicSetContinuousCarrier(unsigned char bOn) {
	int v1; // r0@1
	int arybuf; // [sp+0h] [bp-10h]@1
	char v4; // [sp+4h] [bp-Ch]@1

	arybuf = 0;
	v4 = 0;
	// хз что здесь должно происходить
	////BYTE1 (arybuf) = 1;
	////HIWORD (arybuf) = (unsigned char) -((unsigned int) (bOn - 1) <= 0);
	v1 = MagicMakeMessageData(0xB0u, (unsigned char *) &arybuf, 3);
	return writeport(Magicmessagebuf, v1);
}

//----- (00004CF0) --------------------------------------------------------
int MagicSetTransmissionPower(int nPower) {
	int pktLen;
	unsigned char arybuf[4];

	arybuf[0] = 0;
	arybuf[1] = 2;
	arybuf[2] = nPower >> 8;
	arybuf[3] = nPower & 0x00FF;
	pktLen = MagicMakeMessageData(0xB6u, arybuf, 4);
	return writeport(Magicmessagebuf, pktLen);
}

//----- (00004D24) --------------------------------------------------------
int MagicGetTransmissionPower() {
	int v0; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	arybuf[4] = 0;
	*(int *) arybuf = 0;
	v0 = MagicMakeMessageData(0xB7u, arybuf, 2);
	return writeport(Magicmessagebuf, v0);
}

//----- (00004D4C) --------------------------------------------------------
int MagicSetAutoFrequencyHopping(unsigned char bAuto) {
	int v1; // r0@1
	int arybuf; // [sp+0h] [bp-10h]@1
	char v4; // [sp+4h] [bp-Ch]@1

	arybuf = 0;
	v4 = 0;
	// хз что здесь должно происходить
	////BYTE1 (arybuf) = 1;
	////HIWORD (arybuf) = (unsigned char) -((unsigned int) (bAuto - 1) <= 0);
	v1 = MagicMakeMessageData(0xADu, (unsigned char *) &arybuf, 3);
	return writeport(Magicmessagebuf, v1);
}

//----- (00004D84) --------------------------------------------------------
int MagicGetChannel() {
	int v0; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	arybuf[4] = 0;
	*(int *) arybuf = 0;
	v0 = MagicMakeMessageData(0xAAu, arybuf, 2);
	return writeport(Magicmessagebuf, v0);
}

//----- (00004DAC) --------------------------------------------------------
int MagicSetChannel(unsigned char channel) {
	int v1; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	*(int *) arybuf = 0;
	arybuf[4] = 0;
	arybuf[1] = 1;
	arybuf[2] = channel;
	v1 = MagicMakeMessageData(0xABu, arybuf, 3);
	return writeport(Magicmessagebuf, v1);
}

//----- (00004DDC) --------------------------------------------------------
int MagicSetFrequency(unsigned char region) {
	int v1; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	*(int *) arybuf = 0;
	arybuf[4] = 0;
	arybuf[1] = 1;
	arybuf[2] = region;
	v1 = MagicMakeMessageData(7u, arybuf, 3);
	return writeport(Magicmessagebuf, v1);
}

//----- (00004E0C) --------------------------------------------------------
int MagicSetQuery(int nPara) {
	int v1; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	arybuf[4] = 0;
	arybuf[0] = 0;
	arybuf[1] = 2;
	arybuf[2] = BYTE1(nPara);
	arybuf[3] = nPara;
	v1 = MagicMakeMessageData(0xEu, arybuf, 4);
	return writeport(Magicmessagebuf, v1);
}

//----- (00004E40) --------------------------------------------------------
int MagicQuery() {
	int v0; // r0@1
	unsigned char arybuf[2]; // [sp+4h] [bp-Ch]@1

	*(short *) arybuf = 0;
	v0 = MagicMakeMessageData(0xDu, arybuf, 2);
	return writeport(Magicmessagebuf, v0);
}

//----- (00004E68) --------------------------------------------------------
int MagicSetSelect(int nPL, unsigned char data) {
	int v2; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	*(int *) arybuf = 0;
	arybuf[4] = 0;
	arybuf[0] = BYTE1(nPL);
	arybuf[1] = nPL;
	arybuf[2] = data;
	v2 = MagicMakeMessageData(0x12u, arybuf, 3);
	return writeport(Magicmessagebuf, v2);
}

//----- (00004E9C) --------------------------------------------------------
int MagicStopMultiInventory() {
	int pcktlen; // r0@1
	unsigned char arybuf[2]; // [sp+4h] [bp-Ch]@1

	*(short *) arybuf = 0;
	pcktlen = MagicMakeMessageData(0x28u, arybuf, 2);
	return writeport(Magicmessagebuf, pcktlen);
}

//----- (00004EC4) --------------------------------------------------------
int MagicMultiInventory(int ntimes) {
	int v1; // r0@1
	unsigned char arybuf[5]; // [sp+0h] [bp-10h]@1

	*(int *) arybuf = 0;
	arybuf[1] = 3;
	arybuf[2] = 34;
	arybuf[3] = BYTE1(ntimes);
	arybuf[4] = ntimes;
	v1 = MagicMakeMessageData(0x27u, arybuf, 5);
	return writeport(Magicmessagebuf, v1);
}

int MagicInventory() {

	int packetLen;
	unsigned char arybuf[2] = { 0, 0 };

	packetLen = MagicMakeMessageData(0x22, arybuf, 2);
	return writeport(Magicmessagebuf, packetLen);
}

//----- (00004F20) --------------------------------------------------------
int MagicKill(int nPL, unsigned char *KPassword, int nUL, unsigned char *EPC) {
	unsigned char *v4; // r8@1
	int v5; // r5@1
	int v6; // r6@1
	int v7; // r0@1
	int result; // r0@1
	unsigned char arybuf[50]; // [sp+0h] [bp-50h]@1
	int v10; // [sp+34h] [bp-1Ch]@1

	v4 = EPC;
	v5 = nUL;
	////v10 = _stack_chk_guard;
	*(int *) arybuf = 0;
	*(short *) &arybuf[48] = 0;
	*(int *) &arybuf[4] = 0;
	*(int *) &arybuf[8] = 0;
	*(int *) &arybuf[12] = 0;
	*(int *) &arybuf[16] = 0;
	*(int *) &arybuf[20] = 0;
	*(int *) &arybuf[24] = 0;
	*(int *) &arybuf[28] = 0;
	*(int *) &arybuf[32] = 0;
	*(int *) &arybuf[36] = 0;
	*(int *) &arybuf[40] = 0;
	*(int *) &arybuf[44] = 0;
	arybuf[1] = nPL;
	arybuf[0] = (unsigned short) (nPL & 0xFF00) >> 8;
	v6 = (nUL & 0xFF00) >> 8;
	memcpy(&arybuf[2], KPassword, 4u);
	arybuf[6] = v6;
	arybuf[7] = v5;
	memcpy(&arybuf[8], v4, v5);
	v7 = MagicMakeMessageData(0x65u, arybuf, v5 + 8);
	result = writeport(Magicmessagebuf, v7);
	////if ( v10 != _stack_chk_guard ) _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00004FB4) --------------------------------------------------------
int MagicLock(int nPL, unsigned char *aPassword, int nUL, unsigned char *EPC,
		int nLD) {
	unsigned char *v5; // r9@1
	int v6; // r5@1
	unsigned char *v7; // r3@1
	int v8; // r0@1
	int result; // r0@1
	unsigned char arybuf[50]; // [sp+0h] [bp-58h]@1
	int v11; // [sp+34h] [bp-24h]@1

	v5 = EPC;
	v6 = nUL;
	////v11 = _stack_chk_guard;
	*(int *) arybuf = 0;
	*(short *) &arybuf[48] = 0;
	*(int *) &arybuf[4] = 0;
	*(int *) &arybuf[8] = 0;
	*(int *) &arybuf[12] = 0;
	*(int *) &arybuf[16] = 0;
	*(int *) &arybuf[20] = 0;
	*(int *) &arybuf[24] = 0;
	*(int *) &arybuf[28] = 0;
	*(int *) &arybuf[32] = 0;
	*(int *) &arybuf[36] = 0;
	*(int *) &arybuf[40] = 0;
	*(int *) &arybuf[44] = 0;
	arybuf[1] = nPL;
	arybuf[0] = (unsigned short) (nPL & 0xFF00) >> 8;
	memcpy(&arybuf[2], aPassword, 4u);
	*(int *) &arybuf[6] = (unsigned char) ((unsigned short) (v6 & 0xFF00) >> 8);
	arybuf[7] = v6;
	memcpy(&arybuf[8], v5, v6);
	v7 = &arybuf[v6];
	v7[8] = (unsigned int) nLD >> 16;
	v7[10] = nLD;
	v7[9] = (unsigned short) (nLD & 0xFF00) >> 8;
	v8 = MagicMakeMessageData(0x82u, arybuf, v6 + 11);
	result = writeport(Magicmessagebuf, v8);
	////if ( v11 != _stack_chk_guard )   _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00005064) --------------------------------------------------------
int MagicWriteLableMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL,
		unsigned char *DT) {
	unsigned char *v8; // r9@1
	int v9; // r6@1
	unsigned char *v10; // r3@1
	int v11; // r0@1
	int result; // r0@1
	unsigned char arybuf[50]; // [sp+8h] [bp-60h]@1
	int v14; // [sp+3Ch] [bp-2Ch]@1

	v8 = EPC;
	v9 = nUL;
	////v14 = _stack_chk_guard;
	*(int *) arybuf = 0;
	*(short *) &arybuf[48] = 0;
	*(int *) &arybuf[4] = 0;
	*(int *) &arybuf[8] = 0;
	*(int *) &arybuf[12] = 0;
	*(int *) &arybuf[16] = 0;
	*(int *) &arybuf[20] = 0;
	*(int *) &arybuf[24] = 0;
	*(int *) &arybuf[28] = 0;
	*(int *) &arybuf[32] = 0;
	*(int *) &arybuf[36] = 0;
	*(int *) &arybuf[40] = 0;
	*(int *) &arybuf[44] = 0;
	arybuf[1] = nPL;
	arybuf[0] = (unsigned short) (nPL & 0xFF00) >> 8;
	memcpy(&arybuf[2], APassword, 4u);
	*(int *) &arybuf[6] = (unsigned char) ((unsigned short) (v9 & 0xFF00) >> 8);
	arybuf[7] = v9;
	memcpy(&arybuf[8], v8, v9);
	v10 = &arybuf[v9];
	v10[8] = membank;
	v10[9] = (unsigned short) (nSA & 0xFF00) >> 8;
	v10[10] = nSA;
	v9 += 13;
	v10[12] = nDL;
	v10[11] = (unsigned short) (nDL & 0xFF00) >> 8;
	memcpy(&arybuf[v9], DT, nDL);
	v11 = MagicMakeMessageData(0x49u, arybuf, v9 + nDL);
	result = writeport(Magicmessagebuf, v11);
	////if ( v14 != _stack_chk_guard ) _stack_chk_fail(result);
	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (00005140) --------------------------------------------------------
int MagicReadLableMemory(int nPL, unsigned char *APassword, int nUL,
		unsigned char *EPC, unsigned char membank, int nSA, int nDL) {

	unsigned char *tmpArray;
	int packetLen;
	int result;
	////unsigned char arybuf[50];
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
	packetLen = MagicMakeMessageData(0x39u, arybuf, nUL + 13);
	// отправляем команду в считыватель
	result = writeport(Magicmessagebuf, packetLen);

	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

//----- (0000520C) --------------------------------------------------------
int MagicSelect(int nPL, unsigned char selPa, int nPTR, unsigned char nMaskLen,
		unsigned char turncate, unsigned char *pMask) {
	int pktlen; // r0@1
	int result; // r0@1
	unsigned char arybuf[50]; // [sp+0h] [bp-58h]@1

	*(int *) &arybuf[8] = 0;
	*(int *) &arybuf[12] = 0;
	*(int *) &arybuf[16] = 0;
	*(int *) &arybuf[20] = 0;
	*(int *) &arybuf[24] = 0;
	*(int *) &arybuf[28] = 0;
	*(short *) &arybuf[48] = 0;
	*(int *) &arybuf[32] = 0;
	*(int *) &arybuf[36] = 0;
	*(int *) &arybuf[40] = 0;
	*(int *) &arybuf[44] = 0;

	arybuf[0] = (unsigned short) (nPL & 0xFF00) >> 8;
	arybuf[1] = nPL;
	arybuf[2] = selPa;
	arybuf[3] = BYTE3(nPTR);
	arybuf[4] = (unsigned int) nPTR >> 16;
	arybuf[5] = (unsigned short) (nPTR & 0xFF00) >> 8;
	arybuf[6] = nPTR;
	arybuf[7] = nMaskLen;
	arybuf[8] = turncate;
	memcpy(&arybuf[9], pMask, nMaskLen / 8);

	pktlen = MagicMakeMessageData(0x0Cu, arybuf, nMaskLen / 8 + 9);
	result = writeport(Magicmessagebuf, pktlen);

	return result;
}
// 27B4: using guessed type int __fastcall _stack_chk_fail(int);

int rf_fw_download_tmp(unsigned char *path) {
	char *v1; // r4@1
	int v2; // r5@1
	int v3; // r5@1
	int v4; // r5@1
	int v5; // r5@1
	int v6; // r5@1
	int v7; // r5@1
	int v8; // r5@1
	int v9; // r5@1
	signed int v10; // r11@3
	signed int v11; // r6@3
	int result; // r0@10
	int v13; // r5@12
	int v14; // r4@15
	int v15; // r0@15
	int v16; // [sp+4h] [bp-1Ch]@1
	char v17[] = { 0xD3, 0xD3, 0xD3, 0xD3, 0xD3, 0xD3 }; // [sp+18h] [bp-8h]@1
	char v19; // [sp+20h] [bp+0h]@1
	unsigned int v20; // [sp+24h] [bp+4h]@1

	v1 = (char *) &v20;
	v19 = -2;
	poweron();
	poweron();
	openportserial(v16, 4098);
	MagicGetParameter();
	v2 = readportserial(&v20, 8u);
	v3 = readportserial(&v20, 8u) + v2;
	v4 = v3 + readportserial(&v20, 8u);
	v5 = v4 + readportserial(&v20, 8u);
	v6 = v5 + readportserial(&v20, 8u);
	v7 = v6 + readportserial(&v20, 8u);
	v8 = v7 + readportserial(&v20, 8u);
	v9 = v8 + readportserial(&v20, 8u);
	closeserial();
	if (v9 <= 4) {
		Reset();
		openportserial(v16, 13);
		v10 = 0;
		v11 = 0;
		while (1) {
			writeport(&v19, 1u);
			usleep(0x1388u);
			readportserial(v1, 1u);
			v1 = (char *) &v20;
			_android_log_print(6, 25224, "recv=%02x\r\n", v20);
			if (v20 == 255)
				break;
			++v10;
			usleep(0xBB8u);
			if (v10 > 10) {
				closeserial();
				openportserial(v16, 4098);
				if (v11 > 10)
					goto LABEL_9;
				while (1) {
					writeport(&v19, 1u);
					usleep(0x1388u);
					readportserial(v1, 1u);
					_android_log_print(6, "ScannerJNI", "recv=%02x\r\n", v20);
					v1 = (char *) &v20;
					if (v20 == 255)
						break;
					++v11;
					usleep(0xBB8u);
					if (v11 == 11)
						goto LABEL_9;
				}
			}
		}
		v19 = -75;
		writeport(&v19, 1u);
		usleep(0x1388u);
		closeserial();
		openportserial(v16, 4098);
		usleep(0x2710u);
		v19 = -37;
		v13 = 0;
		while (1) {
			writeport(&v19, 1u);
			usleep(0x1388u);
			readportserial(v1, 1u);
			v1 = (char *) &v20;
			if (v20 == 191)
				break;
			++v13;
			usleep(0xBB8u);
			if (v13 == 101)
				goto LABEL_10;
		}
		_android_log_print(4, "ScannerJNI",
				"[rf_fw_download] -- trycnts1111 : 0x%X ", v13);
		usleep(0x1388u);
		v19 = -3;
		writeport(&v19, 1u);
		usleep(0x1388u);
		printf(" read : 0x%X", 14388);
		v14 = 0;
		v15 = 0;
		do {
			++v14;
			writeport(&firmware[v15], 1u);
			v15 = v14;
		} while (v14 != 14388);
		_android_log_print(6, "ScannerJNI", " read : 0x%X", 14388);
		usleep(0x2710u);
		writeport(&v17, 6u);
		_android_log_print(6, "ScannerJNI", "[rf_fw_download]  ok-- ");
		LABEL_9: closeserial();
	} else {
		_android_log_print(6, "ScannerJNI", "return  -- %x", v9);
	}
	LABEL_10: result = 0;
	return result;
}

int rf_fw_download(unsigned char *path) {

	unsigned char *v1;
	int v2;
	int v3;
	int v4;
	int v5;
	int v6;
	int v7;
	int v8;
	int v9;
	signed int v10;
	signed int v11;
	int result;
	int v13;
	int v14;
	int v15;

	unsigned char end[6];
	unsigned char cmd[2];
	unsigned char recv[1024];
	int v20;

	v1 = recv;
	*(int *) end = -741092397;
	*(short *) &end[4] = *(short *) "УУ";
	cmd[0] = -2;
	poweron();
	poweron();
	openportserial(path, B115200);
	MagicGetParameter();
	v2 = readportserial(recv, 8);
	v3 = readportserial(recv, 8) + v2;
	v4 = v3 + readportserial(recv, 8);
	v5 = v4 + readportserial(recv, 8);
	v6 = v5 + readportserial(recv, 8);
	v7 = v6 + readportserial(recv, 8);
	v8 = v7 + readportserial(recv, 8);
	v9 = v8 + readportserial(recv, 8);
	closeserial();
	if (v9 <= 4) {
		Reset();
		openportserial(path, B9600);
		v10 = 0;
		v11 = 0;
		while (1) {
			writeport(cmd, 1);
			usleep(5000);
			readportserial(v1, 1);
			v1 = recv;
			__android_log_print(ANDROID_LOG_ERROR, 25224, "recv=%02x\r\n",
					recv[0]);
			if (recv[0] == 255) {
				break;
			}
			++v10;
			usleep(3000);
			if (v10 > 10) {
				closeserial();
				openportserial(path, B115200);
				if (v11 > 10) {
					goto LAST_END;
				}
				while (1) {
					writeport(cmd, 1);
					usleep(5000);
					readportserial(v1, 1);
					__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
							"recv=%02x\r\n", recv[0]);
					v1 = recv;
					if (recv[0] == 255) {
						break;
					}
					++v11;
					usleep(3000);
					if (v11 == 11) {
						goto LAST_END;
					}
				}
			}
		}
		cmd[0] = -75;
		writeport(cmd, 1);
		usleep(3000);
		closeserial();
		openportserial(path, B115200);
		usleep(10000);
		cmd[0] = -37;
		v13 = 0;
		while (1) {
			writeport(cmd, 1);
			usleep(3000);
			readportserial(v1, 1);
			v1 = recv;
			if (recv[0] == 191) {
				break;
			}
			++v13;
			usleep(3000);
			if (v13 == 101) {
				goto LABEL_10;
			}
		}
		__android_log_print(ANDROID_LOG_INFO, "ScannerJNI",
				"[rf_fw_download] -- trycnts1111 : 0x%X ", v13);
		usleep(5000);
		cmd[0] = -3;
		writeport(cmd, 1);
		usleep(5000);
		printf(" read : 0x%X", firmwareLen);
		v14 = 0;
		v15 = 0;
		do {
			++v14;
			writeport(&firmware_magicrf[v15], 1);
			v15 = v14;
		} while (v14 != firmwareLen);
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI", " read : 0x%X",
				firmwareLen);
		usleep(10000);
		writeport(end, 6);
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
				"[rf_fw_download]  ok-- ");
		LAST_END: closeserial();
	} else {
		__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI", "return  -- %x",
				v9);
	}
	LABEL_10: result = 0;

	return result;
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

//----- (000055F8) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetIOParameter(JNIEnv *env,
		jclass jc, jbyte p1, jbyte p2, jbyte p3, jbyteArray jout) {
	unsigned char v6; // r8@1
	unsigned char v7; // r5@1
	int v9; // r4@1
	jbyteArray v10; // r9@1
	jint v11; // r11@3
	signed int v12; // r5@3
	signed int v13; // r4@3

	v6 = p2;
	v7 = p1;

	v9 = (*env)->GetArrayLength(env, jout);
	v10 = (*env)->GetByteArrayElements(env, jout, 0);
	if (!v10 || v9 <= 2)
		goto END;
	v11 = 17 - ((unsigned int) (MagicSetIOParameter(v7, v6, p3) - 10) <= 0);
	v12 = 0;
	v13 = 0;
	while (1) {
		v13 += readportserial(&Magicbuf[v13], 10);
		++v12;
		if (v13 > 9)
			break;
		if (v12 > 19)
			goto END;
	}
	if (MagicIsCheckSum(Magicbuf, 8)) {
		*(char *) v10 = Magicbuf[5];
		*(char *) (v10 + 1) = Magicbuf[6];
		*(char *) (v10 + 2) = Magicbuf[7];
	} else {
		END: v11 = 17;
	}
	(*env)->ReleaseByteArrayElements(env, jout, v10, 0);
	return v11;
}

//----- (000056D8) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_TestRssi(JNIEnv *env, jclass jc,
		jbyteArray jout) {
	jbyteArray v3; // r9@1
	int v5; // r4@1
	void *v6; // r10@1
	jint v7; // r11@3
	signed int v8; // r5@3
	signed int v9; // r4@3

	v3 = jout;

	v5 = (*env)->GetArrayLength(env, v3);
	v6 = (*env)->GetByteArrayElements(env, v3, 0);
	if (!v6 || v5 <= 21)
		goto END;
	v7 = 17 - ((unsigned int) (MagicTestRssi() - 7) <= 0);
	v8 = 0;
	v9 = 0;
	while (1) {
		v9 += readportserial(&Magicbuf[v9], 29);
		++v8;
		if (v9 > 28)
			break;
		if (v8 > 19)
			goto END;
	}
	if (MagicIsCheckSum(Magicbuf, 27))
		memcpy(v6, &Magicbuf[5], 0x16u);
	else
		END: v7 = 17;
	(*env)->ReleaseByteArrayElements(env, v3, v6, 0);
	return v7;
}

//----- (00005794) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_ScanJammer(JNIEnv *env, jclass jc,
		jbyteArray jout) {
	jbyteArray v3; // r9@1

	int v5; // r4@1
	void *v6; // r10@1
	jint v7; // r11@3
	signed int v8; // r5@3
	signed int v9; // r4@3

	v3 = jout;

	v5 = (*env)->GetArrayLength(env, v3);
	v6 = (void *) (*env)->GetByteArrayElements(env, v3, 0);
	if (!v6 || v5 <= 3)
		goto END;
	v7 = 17 - ((unsigned int) (MagicScanJammer() - 7) <= 0);
	v8 = 0;
	v9 = 0;
	while (1) {
		v9 += readportserial(&Magicbuf[v9], 29);
		++v8;
		if (v9 > 28)
			break;
		if (v8 > 19)
			goto END;
	}
	if (MagicIsCheckSum(Magicbuf, 27))
		memcpy(v6, &Magicbuf[5], 0x16u);
	else
		END: v7 = 17;
	(*env)->ReleaseByteArrayElements(env, v3, v6, 0);
	return v7;
}

//----- (00005850) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetParameter(JNIEnv *env, jclass jc,
		jbyte bMixer, jbyte bIF, jint nThrd) {
	signed int v5; // r5@1
	signed int v6; // r4@1

	MagicSetParameter(bMixer, bIF, nThrd);
	v5 = 0;
	v6 = 0;
	while (1) {
		v6 += readportserial(&Magicbuf[v6], 8);
		++v5;
		if (v6 > 7)
			break;
		if (v5 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return Magicbuf[5];
	return 17;
}

//----- (000058AC) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_GetParameter(JNIEnv *env, jclass jc,
		jbyteArray jout) {
	jbyteArray v3; // r9@1
	JNIEnv *v4; // r6@1
	int v5; // r4@1
	void *v6; // r10@1
	jint v7; // r11@3
	signed int v8; // r5@3
	signed int v9; // r4@3

	v3 = jout;
	v4 = env;
	v5 = (*env)->GetArrayLength(env, v3);
	v6 = (void *) (*v4)->GetByteArrayElements(v4, v3, 0);
	if (!v6 || v5 <= 3)
		goto END;
	v7 = 17 - ((unsigned int) (MagicGetParameter() - 7) <= 0);
	v8 = 0;
	v9 = 0;
	while (1) {
		v9 += readportserial(&Magicbuf[v9], 11);
		++v8;
		if (v9 > 10)
			break;
		if (v8 > 19)
			goto END;
	}
	if (MagicIsCheckSum(Magicbuf, 9))
		memcpy(v6, &Magicbuf[5], 4u);
	else
		END: v7 = 17;
	(*v4)->ReleaseByteArrayElements(v4, v3, v6, 0);
	return v7;
}

//----- (00005968) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetContinuousCarrier(JNIEnv *env,
		jclass jc, jbyte bOn) {
	signed int v3; // r5@1
	signed int v4; // r4@1

	MagicSetContinuousCarrier(bOn);
	v3 = 0;
	v4 = 0;
	while (1) {
		v4 += readportserial(&Magicbuf[v4], 8);
		++v3;
		if (v4 > 7)
			break;
		if (v3 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return Magicbuf[5];
	return 17;
}

//----- (000059BC) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetTransmissionPower(JNIEnv *env,
		jclass jc, jint nPower) {
	signed int i; // r5@1
	signed int count; // r4@1

	MagicSetTransmissionPower(nPower);
	i = 0;
	count = 0;
	while (1) {
		count += readportserial(&Magicbuf[count], 8);
		++i;
		if (count > 7) {
			break;
		}
		if (i > 19) {
			return 17;
		}
	}
	if (MagicIsCheckSum(Magicbuf, 6)) {
		return Magicbuf[5];
	}
	return 17;
}

//----- (00005A10) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_GetTransmissionPower(JNIEnv *env,
		jclass jc) {
	signed int v2; // r5@1
	signed int v3; // r4@1

	MagicGetTransmissionPower();
	v2 = 0;
	v3 = 0;
	while (1) {
		v3 += readportserial(&Magicbuf[v3], 9);
		++v2;
		if (v3 > 8)
			break;
		if (v2 > 19)
			return 17;
	}
	if (!MagicIsCheckSum(Magicbuf, 7) || Magicbuf[0] != 187 || Magicbuf[1] != 1
			|| Magicbuf[2] != 183)
		return 17;
	return (Magicbuf[5] << 8) | Magicbuf[6];
}

//----- (00005A7C) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetAutoFrequencyHopping(JNIEnv *env,
		jclass jc, jbyte bAuto) {
	signed int v3; // r5@1
	signed int v4; // r4@1

	MagicSetAutoFrequencyHopping(bAuto);
	v3 = 0;
	v4 = 0;
	while (1) {
		v4 += readportserial(&Magicbuf[v4], 8);
		++v3;
		if (v4 > 7)
			break;
		if (v3 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return Magicbuf[5];
	return 17;
}

//----- (00005AD0) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_GetChannel(JNIEnv *env, jclass jc) {
	signed int v2; // r5@1
	signed int v3; // r4@1

	MagicGetChannel();
	v2 = 0;
	v3 = 0;
	while (1) {
		v3 += readportserial(&Magicbuf[v3], 8);
		++v2;
		if (v3 > 7)
			break;
		if (v2 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return Magicbuf[5];
	return 17;
}

//----- (00005B20) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetChannel(JNIEnv *env, jclass jc,
		jbyte channel) {
	jint v3; // r8@1
	signed int v4; // r5@1
	signed int v5; // r4@1

	v3 = 17 - ((unsigned int) (MagicSetChannel(channel) - 8) <= 0);
	v4 = 0;
	v5 = 0;
	while (1) {
		v5 += readportserial(&Magicbuf[v5], 8);
		++v4;
		if (v5 > 7)
			break;
		if (v4 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return v3;
	return 17;
}

//----- (00005B84) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetFrequency(JNIEnv *env, jclass jc,
		jbyte region) {
	jint v3; // r8@1
	signed int v4; // r5@1
	signed int v5; // r4@1

	v3 = 17 - ((unsigned int) (MagicSetFrequency(region) - 8) <= 0);
	v4 = 0;
	v5 = 0;
	while (1) {
		v5 += readportserial(&Magicbuf[v5], 8);
		++v4;
		if (v5 > 7)
			break;
		if (v4 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return v3;
	return 17;
}

//----- (00005BE8) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetQuery(JNIEnv *env, jclass jc,
		jint nPara) {
	jint v3; // r8@1
	signed int v4; // r5@1
	signed int v5; // r4@1

	v3 = 17 - ((unsigned int) (MagicSetQuery(nPara) - 9) <= 0);
	v4 = 0;
	v5 = 0;
	while (1) {
		v5 += readportserial(&Magicbuf[v5], 8);
		++v4;
		if (v5 > 7)
			break;
		if (v4 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return v3;
	return 17;
}

//----- (00005C4C) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_Query(JNIEnv *env, jclass jc) {
	jint v2; // r8@1
	signed int v3; // r5@1
	signed int v4; // r4@1

	v2 = 17 - ((unsigned int) (MagicQuery() - 7) <= 0);
	v3 = 0;
	v4 = 0;
	while (1) {
		v4 += readportserial(&Magicbuf[v4], 9);
		++v3;
		if (v4 > 8)
			break;
		if (v3 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 7))
		return v2;
	return 17;
}

//----- (00005CAC) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_Kill(JNIEnv *env, jclass jc,
		jbyteArray jKPassword, jint nUL, jbyteArray jEPC) {
	int v5; // r9@1
	jbyteArray v6; // r7@1
	JNIEnv *v7; // r4@1
	int v8; // r11@1
	unsigned char *v9; // r10@1
	unsigned char *v10; // r0@1
	unsigned char *v11; // r6@1
	jint v12; // r11@4
	int v14; // [sp+4h] [bp-2Ch]@1

	v5 = nUL;
	v6 = jKPassword;
	v7 = env;
	v8 = (*env)->GetArrayLength(env, v6);
	v9 = (unsigned char *) (*v7)->GetByteArrayElements(v7, v6, 0);
	v14 = (*v7)->GetArrayLength(v7, jEPC);
	v10 = (unsigned char *) (*v7)->GetByteArrayElements(v7, jEPC, 0);
	v11 = v10;
	if (v8 <= 3 || !v9 || v14 < v5 || !v10
			|| (v12 = 16, MagicKill(v5 + 6, v9, v5, v10) != v5 + 13)) {
		v12 = 17;
	}
	(*v7)->ReleaseByteArrayElements(v7, v6, v9, 0);
	(*v7)->ReleaseByteArrayElements(v7, jEPC, v11, 0);
	return v12;
}

//----- (00005D68) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_Lock(JNIEnv *env, jclass jc,
		jbyteArray jAPassword, jint nUL, jbyteArray jEPC, jint nLD) {

	int v6; // r9@1

	int v9; // r11@1
	unsigned char *v10; // r10@1
	unsigned char *v11; // r0@1
	unsigned char *v12; // r6@1
	jint v13; // r11@4
	int v15; // [sp+Ch] [bp-2Ch]@1

	v6 = nUL;

	v9 = (*env)->GetArrayLength(env, jAPassword);
	v10 = (unsigned char *) (*env)->GetByteArrayElements(env, jAPassword, 0);
	v15 = (*env)->GetArrayLength(env, jEPC);
	v11 = (unsigned char *) (*env)->GetByteArrayElements(env, jEPC, 0);
	v12 = v11;
	if (v9 <= 3 || !v10 || v15 < v6 || !v11
			|| (v13 = 16, MagicLock(v6 + 9, v10, v6, v11, nLD) != v6 + 16))
		v13 = 17;
	(*env)->ReleaseByteArrayElements(env, jAPassword, v10, 0);
	(*env)->ReleaseByteArrayElements(env, jEPC, v12, 0);
	return v13;
}

jint Java_android_hardware_uhf_magic_reader_WriteLable(JNIEnv *env, jclass jc,
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
			|| (packetLen = MagicWriteLableMemory(nUL + 11 + nDL, password, nUL,
					pcepc, membank, nSA, nDL, data), expectLen = nUL + 22 + nDL, result =
					16, packetLen != expectLen)) {
		result = 17;
	}

	(*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
	(*env)->ReleaseByteArrayElements(env, jPCEPC, pcepc, 0);
	(*env)->ReleaseByteArrayElements(env, jDT, data, 0);

	return result;
}

jint Java_android_hardware_uhf_magic_reader_ReadLable(JNIEnv *env, jclass jc,
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
			|| (result = 16, MagicReadLableMemory(nUL + 11, password, nUL,
					pcepc, membank, nSA, nDL) != nUL + 22)) {
		result = 17;
	}

	(*env)->ReleaseByteArrayElements(env, jAPassword, password, 0);
	(*env)->ReleaseByteArrayElements(env, jEPC, pcepc, 0);

	return result;
}

//----- (00006010) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_SetSelect(JNIEnv *env, jclass jc,
		jbyte data) {
	signed int v3; // r5@1
	signed int v4; // r4@1

	MagicSetSelect(1, data);
	v3 = 0;
	v4 = 0;
	while (1) {
		v4 += readportserial(&Magicbuf[v4], 8);
		++v3;
		if (v4 > 7)
			break;
		if (v3 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return Magicbuf[5];
	return 17;
}

//----- (00006068) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_Select(JNIEnv *env, jclass jc,
		jbyte selPa, jint nPTR, jbyte nMaskLen, jbyte turncate,
		jbyteArray jpMask) {
	signed int v9; // r8@1
	unsigned char *v10; // r9@1
	jint v11; // r11@3
	signed int v13; // r5@5
	signed int v14; // r4@5
	v9 = (*env)->GetArrayLength(env, jpMask);
	v10 = (unsigned char *) (*env)->GetByteArrayElements(env, jpMask, 0);
	if (!v10 || v9 < nMaskLen)
		goto END;

	v11 = 17
			- MagicSelect(
					((signed int) (((unsigned int) ((unsigned char) nMaskLen
							<< 24 >> 31) >> 29) + nMaskLen) >> 3) + 7, selPa,
					nPTR, nMaskLen, turncate, v10) - (nMaskLen + 14) <= 0;
	v13 = 0;
	v14 = 0;
	while (1) {
		v14 += readportserial(&Magicbuf[v14], 8);
		++v13;
		if (v14 > 7)
			break;
		if (v13 > 19)
			goto END;
	}
	if (!MagicIsCheckSum(Magicbuf, 6))
		END: v11 = 17;
	(*env)->ReleaseByteArrayElements(env, jpMask, v10, 0);
	return v11;
}

//----- (00006154) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_StopMultiInventory(JNIEnv *env,
		jclass jc) {
	jint v2; // r8@1
	signed int v3; // r5@1
	signed int v4; // r4@1

	v2 = 17 - ((unsigned int) (MagicStopMultiInventory() - 7) <= 0);
	v3 = 0;
	v4 = 0;
	while (1) {
		v4 += readportserial(&Magicbuf[v4], 8);
		++v3;
		if (v4 > 7)
			break;
		if (v3 > 19)
			return 17;
	}
	if (MagicIsCheckSum(Magicbuf, 6))
		return v2;
	return 17;
}

//----- (000061B4) --------------------------------------------------------
jint Java_android_hardware_uhf_magic_reader_MultiInventory(JNIEnv *env,
		jclass jc, jint ntimes) {
	return 17 - ((unsigned int) (MagicMultiInventory(ntimes) - 10) <= 0);
}

jint Java_android_hardware_uhf_magic_reader_Inventory(JNIEnv *env, jclass jc) {
	return 17 - ((unsigned int) (MagicInventory() - 7) <= 0);
}

//----- (000061DC) --------------------------------------------------------
void Java_android_hardware_uhf_magic_reader_Close(JNIEnv *env, jclass jc) {
	closeport();
}

//----- (000061E4) --------------------------------------------------------
void Java_android_hardware_uhf_magic_reader_Clean(JNIEnv *env, jclass jc) {
	Cleantemp();
}

jint Java_android_hardware_uhf_magic_reader_Open(JNIEnv *env, jclass jc,
		jstring strpath) {

	unsigned char *path;
	jboolean copy = JNI_FALSE;

	path = (unsigned char *) (*env)->GetStringUTFChars(env, strpath, &copy);

	__android_log_print(ANDROID_LOG_ERROR, "ScannerJNI",
			"77777777777777777777777777777777777777777");
	return openportserial(path, B115200);
}
// 27A8: using guessed type int _android_log_print(int, int, const char *, ...);

void Java_android_hardware_uhf_magic_reader_init(JNIEnv *env, jclass jc,
		jstring strpath) {

	unsigned char *path;
	jboolean copy = JNI_FALSE;
	path = (unsigned char *) (*env)->GetStringUTFChars(env, strpath, &copy);
	rf_fw_download(path);
}

int MagicKill(int nPL, unsigned char *KPassword, int nUL, unsigned char *EPC) {

	int packetLen;
	int result; // r0@1
	unsigned char arybuf[1024];

	memset(arybuf, 0, sizeof(arybuf));

	arybuf[0] = (nPL & 0xFF00) >> 8;
	arybuf[1] = nPL;
	memcpy(&arybuf[2], KPassword, 4);
	arybuf[6] = (nUL & 0xFF00) >> 8;
	arybuf[7] = nUL;
	memcpy(&arybuf[8], EPC, nUL);
	packetLen = MagicMakeMessageData(0x65u, arybuf, nUL + 8);
	result = writeport(Magicmessagebuf, packetLen);

	return result;
}
