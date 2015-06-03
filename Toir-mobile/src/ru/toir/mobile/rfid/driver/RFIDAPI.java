package ru.toir.mobile.rfid.driver;

/**
 * cpuїЁІвКФЦґРРІЅЦиИзПВЈє
 * µЪ1ІЅ GetChallenge »сИЎ4ЧЦЅЪЛж»ъКэ 
 * µЪ2ІЅ authentication 4ЧЦЅЪЛж»ъКэ+4ЧЦЅЪ0 ГЬФїКЗ8ёцЧЦЅЪ0xFFЈ¬ЅшРРНвІїИПЦ¤  
 * µЪ3ІЅ DeleteFile ЙѕіэMFДїВјПВЛщУРОДјю     
 * µЪ4ІЅ InitMF ЅЁБўMFДїВј   //Мш№э ДїЗ°ОЮ·ЁґґЅЁ
 * µЪ5ІЅ InitADF ЅЁБўDFДїВј //Мш№э ФЭК±ОґІвКФ
 * µЪ6ІЅ InitKEF ЅЁБўГЬФїОДјю //ЅЁБўMFДїВјПВГЬФїОДјю
 * µЪ7ІЅ appendKEY МнјУГЬФї //ДїЗ°Ц»РиТЄМнјУНвІїИПЦ¤ГЬФї ОЄБЛ·Ѕ±гјЗТдЈ¬ФЭК±ИЎ8ёцЧЦЅЪ0xFF
 * µЪ8ІЅ InitBEF ЅЁБў¶юЅшЦЖОДјю //
 * µЪ9ІЅ Select_BinaryFile СЎФс¶юЅшЦЖОДјю //СЎФсОДјюФЭК±Мш№э І»У°Пм¶БРґ
 * µЪ10ІЅ UpdateBinary Рґ¶юЅшЦЖОДјю
 * µЪ11ІЅ ReadBinary ¶БИЎ¶юЅшЦЖОДјю  і¤¶ИЙиОЄ0 јґ±нКѕ¶БИЎХыМхјЗВјЈ¬ДїЗ°І»Ц§іЦИОТві¤¶И¶БИЎ
 * µЪ12ІЅ InitREF ЅЁБўјЗВјОДјю ДїЗ°Ц»ЧцБЛЅЁБўС­»·¶Ёі¤јЗВјОДјю
 * µЪ13ІЅ AppendRecord ФцјУС­»·¶Ёі¤јЗВјОДјю  і¤¶И±ШРлёъТСЅЁБўµДОДјюЦРЛщ№ж¶ЁµДі¤¶ИТ»ЦВ
 * µЪ14ІЅ ReadRecord ¶БИЎјЗВјОДјю   і¤¶ИЙиОЄ0 јґ±нКѕ¶БИЎХыМхјЗВјЈ¬ДїЗ°І»Ц§іЦИОТві¤¶И¶БИЎ
 */
import java.io.UnsupportedEncodingException;
import java.util.concurrent.locks.Lock;

import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import ru.toir.mobile.serial.SerialPortManager;

// Wrapper for native library

public class RFIDAPI {

	public final  int SCARD_UNKNOWN		=	0x0001;	/*!< Unknown state */
	public final int SCARD_UART_NOT_CONNECTED	=		0x0002;	/*!< UART Not connected   */
	public final int SCARD_ABSENT	=		0x0003;	/*!< Card is absent */
	//smartCOS Create_File
	public final int SCARD_COMMAND_EXECUTED_CORRECTLY	=		0x0004;	/*!< Command executed correctly  */
	public final int SCARD_WRITE_EEPROM_FAIL	=		0x0005;	/*!< Write EEPROM failure   */
	public final int SCARD_DATA_LENGTH_ERROR	=		0x0006;	/*!< Data length error    */
	public final int SCARD_ALLOW_CODE_TRANSFER_ERROR_COUNT	=		0x0007;	/*!< Allow the code transfer error count    */
	public final int SCARD_CREATE_CONDITION_NOT_SATISFIED	=		0x0008;	/*!< Create condition not satisfied     */

	public final int SCARD_SECURITY_CONDITION_NOT_SATISFIED		=	0x0007;	/*!< Security condition is not satisfied     */
	public final int SCARD_IDENTIFIER_ALREADY_EXISTS	=		0x0008;	/*!< Identifier already exists      */
	public final int SCARD_FUNCTION_NOT_SUPPORTED	=		0x0009;	/*!< Function not supported       */
	public final int SCARD_FILE_NOT_FOUND	=		0x0011;	/*!< File not found        */
	public final int SCARD_NOT_ENOUGH_SPACE		=	0x0012;	/*!< Not enough space */
	public final int SCARD_PAREMETER_IS_INCORRECT	=		0x0013;	/*!< The parameter is incorrect         */
	public final int SCARD_INS_IS_INCORRECT		=	0x0014;	/*!< The INS is incorrect         */
	public final int SCARD_CLA_IS_INCORRECT		=	0x0015;	/*!< The CLA is incorrect         */

	//Write_KEY
	public final int SCARD_CMD_NOT_MATCH_TYPES	=		0x0016;	/*!< Command file types do not match        */
	public final int SCARD_KEY_LOCK		=	0x0017;	/*!< Key lock         */
	public final int SCARD_GET_RANDOM_INVALID	=		0x0018;	/*!< From a random number is invalid         */
	public final int SCARD_CONDITION_OF_USE_NOT_SATISFIED	=		0x0019;	/*!< Conditions of use does not satisfied          */
	public final int SCARD_MAC_INCORRECT	=		0x0020;	/*!< MAC is incorrect          */
	public final int SCARD_DATA_NOT_CORRECT		=	0x0021;	/*!< Data domain is not correct           */
	public final int SCARD_CARD_LOCK	=		0x0022;	/*!< Card lock            */
	public final int SCARD_FILE_SPACE_INSUFFICIENT		=	0x0023;	/*!< File space is insufficient              */
	public final int SCARD_P1_AND_P2_NOT_CORRECT	=		0x0024;	/*!< P1 and P2 not correct            */
	public final int SCARD_APP_PERMANENT_LOCK		=	0x0025;	/*!< Application  permanent lock             */
	public final int SCARD_KEY_NOT_FOUND	=		0x0026;	/*!< KEY is not found             */

	public final int SCARD_NOT_BINARY_FILE		=	0x0027;	/*!< not binary file           */
	public final int SCARD_CONDITION_OF_READ_NOT_SATISFIED	=		0x0028;	/*!< the conditions of read does not satisfied           */
	public final int SCARD_CONDITION_OF_CMD_NOT_SATISFIED	=		0x0029;	/*!< the condition of command execution does not satisfied           */
	public final int SCARD_RECORD_NOT_FOUND		=	0x0030;	/*!< record not found        */
	public final int SCARD_NO_DATA_RETURN		=	0x0031;	/*!< Card no data can be returned       */

	public final int SCARD_SECURITY_DATA_NOT_CORRECT	=		0x0032;	/*!< Security message data item is not correct        */
	public final int SCARD_P1_AND_P2_OUT_OF_GAUGE		=	0x0033;	/*!< P1 and P2 are out of gauge            */
	public final int SCARD_FILE_NOT_LINEAR_FIXED_FILE	=		0x0034;	/*!< The file is not a linear fixed length file             */
	public final int SCARD_APP_TEMPORARY_LOCED	=		0x0035;	/*!< APP Temporary locked           */
	public final int SCARD_FILE_STORAGE_SPACE_NOT_ENOUGH	=		0x0036;	/*!< File storage space is not enough          */

	public final int PROCLAIMED = 0;//ГчОД
	public final int CIPHERTEXT = 1;//ГЬОД
	private RealRFID rfid = null;
	private static final byte[] SWITCH_COMMAND = "D&C00040104".getBytes();

	public RFIDAPI() {
		//String className,String MethodSend,String MethodRecv
		rfid = new RealRFID();
		String className = new String("android_serialport_api/RFIDAPI");
		String MethodSend = new String("BluetoothSend");
		String MethodRecv = new String("BluetoothRecv");
		UninitRFID();
		InitRFID(className,MethodSend,MethodRecv);
    }
	
	public static void switchStatus() {
		SerialPortManager.getInstance().write(SWITCH_COMMAND);
		SystemClock.sleep(100);
	}
    
    public void InitRFID(String className,String MethodSend,String MethodRecv)
	{
		rfid.InitClassName(className.getBytes(),MethodSend.getBytes(),MethodRecv.getBytes());
	}
	
	public void UninitRFID()
	{
		rfid.DeInitClassName();
	}
	
//ґґЅЁMFОДјю
	public int InitMF()
	{
		byte TransCode = (byte) 0xFF;
		char Authority = 0xF0F0;
		byte FileId = 0x01;

		return rfid.InitMF(TransCode, Authority, FileId);
	}
//ґґЅЁDFОДјю
	public int InitADF()
	{
		byte FileId = (byte) 0x95;
		char Authority = 0xF0F0;
		int NameLen = 9;
		byte[] ADFName = {(byte) 0xA0,0x00,0x00,0x00,0x03,(byte) 0x86,(byte) 0x98,0x07,0x01};
		
		return  rfid.InitADF(FileId,Authority,NameLen,ADFName);
	}
	//ґґЅЁMFОДјюПВГЬФїОДјю
	public int InitMKEF()
	{
		char FileId = (0x00<<8)|0x00;
		byte FileType = (byte) 0x3F;
		char Authority = 0x01F0;
		char FileLen = 0x00B0;
		
		return rfid.InitBEF(FileId, FileType, Authority, FileLen);
	}
//ґґЅЁ¶юЅшЦЖОДјю
	public int InitBEF()
	{
		char FileId = (0x00<<8)|0x16;
		byte FileType = (byte) 0xA8;
		char Authority = 0xF0F0;
		char FileLen = 0x0027;
		
		return rfid.InitBEF(FileId, FileType, Authority, FileLen);
	}
//ґґЅЁС­»·јЗВјОДјю
	public int InitREF()
	{
		char FileId = (0x00<<8)|0x18;
		byte FileType = 0x2E;
		char Authority = 0xF0F0;
		char FileLen = 0x0A17;//±нКѕґґЅЁ0x0AМхјЗВјЈ¬ГїТ»МхјЗВјµДі¤¶ИКЗ0x17
		
		return rfid.InitREF(FileId,FileType,Authority,FileLen);
	}
//¶Б¶юЅшЦЖОДјю
	/**
	 * 
	 * @param RecBuf
	 * @param Count і¤¶ИІ»ТЄґуУЪТСЅЁБўµД¶юЅшЦЖОДјюµДі¤¶ИЈ¬ ФЭК±¶ЁОЄ0x27
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int ReadBinary(byte[] RecBuf,int Count)
	{
		byte FileId = (0x00<<8)|0x16;
		byte Offset = 0x00;
		return rfid.ReadBinary(FileId, Offset, Count, RecBuf);
	}
//ёьРВ¶юЅшЦЖОДјю
	public int UpdateBinary(byte[] SendBuf,int Count)
	{
		byte FileId = (0x00<<8)|0x16;
		byte Offset = 0x00;
		int level = 0x00;
		return rfid.UpdateBinary(FileId,Offset,level,SendBuf,Count);
	}
//¶БС­»·јЗВјОДјю
	public int ReadRecord(byte[] RecBuf,int Count)
	{
		byte FileId = (0x00<<8)|0x18;
        int Index = 0x01;
        int level = 0x00;

		return rfid.ReadRecord(FileId, level, Index, RecBuf, 0);
	}
//ФцјУС­»·¶Ёі¤јЗВјОДјю
	/**
	 * 
	 * @param SendBuf  
	 * @param Count  і¤¶ИЧоґуІ»ДЬі¬№эТСЅЁБўОДјю№ж¶ЁµДµҐМхјЗВјµДі¤¶И ФЭК±ОЄ0x17
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int AppendRecord(
        byte[] SendBuf,
        int Count
    )
	{
		byte FileId = (0x00<<8)|0x18;
        int level = 0x00;
        byte[] tempBuf = new byte[0x17];
        
        if(Count>tempBuf.length)
        	Count = tempBuf.length;
        
       	System.arraycopy(SendBuf, 0, tempBuf, 0, Count);

		return rfid.AppendRecord(FileId,level,tempBuf,tempBuf.length);
	}
//ёьРВС­»·јЗВјОДјю
	/**
	 * 
	 * @param SendBuf
	 * @param Count і¤¶ИЧоґуІ»ДЬі¬№эТСЅЁБўОДјю№ж¶ЁµДµҐМхјЗВјµДі¤¶И ФЭК±ОЄ0x17
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int UpdateRecord(byte[] SendBuf,int Count)
	{
		byte FileId = (0x00<<8)|0x18;
        int level = 0x00;
        int Index = 0x00;//index ОЄјЗВјєЕЈ¬ИфёГОДјюУРNМхјЗВјЈ¬ФтјЗВјєЕїЙТФКЗ1-NЎЈ
        
        byte[] tempBuf = new byte[0x17];
        
        if(Count>tempBuf.length)
        	Count = tempBuf.length;
        
       	System.arraycopy(SendBuf, 0, tempBuf, 0, Count);
       	
		return rfid.UpdateRecord(FileId, level, Index, tempBuf, tempBuf.length);
	}
//СЎФс¶юЅшЦЖОДјю
	/**
	 * µ±ДїВјПВґжФЪ¶аёц¶юЅшЦЖОДјюК±Ј¬РиТЄСЎФс¶ФУ¦µД¶юЅшЦЖОДјюЈ¬И»єуІЕДЬЅшРР¶БРґ
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int Select_BinaryFile() {
		int FileType = 0x02;
		//int FileIndex = 0x00;
		int Idlen = 0x02;
		byte[] FileId = {0x00,0x16};

		return rfid.SelectFile(FileType, Idlen, FileId);
	}
//СЎФсС­»·јЗВјОДјю	
	/**
	 * µ±ДїВјПВґжФЪ¶аёцС­»·јЗВјОДјюК±Ј¬РиТЄСЎФс¶ФУ¦µДјЗВјОДјюЈ¬И»єуІЕДЬЅшРР¶БРґ
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int Select_RecordFile() {
		int FileType = 0x02;
		//int FileIndex = 0x00;
		int Idlen = 0x02;
		byte[] FileId = {0x00,0x18};

		return rfid.SelectFile(FileType,Idlen,FileId);
	}
	
	//СЎФсMFДїВј
	/**
	 * СЎФсЦчДїВјЈ¬УЙУЪЦ»УРТ»ёцЦчДїВјЈ¬їЙІ»УГСЎФс
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int Select_MasterFile() {
		int FileType = 0x00;
		//int FileIndex = 0x00;
		int Idlen = 0x00;
		byte[] FileId = {0x00,0x00};
		
		return rfid.SelectFile(FileType,Idlen,FileId);
	}
	
	//СЎФсDFОДјю	
	/**
	 * µ±РиТЄФЪТСґґЅЁµДDFДїВјПВІЩЧчК±РиТЄПИСЎФс¶ФУ¦DFДїВј
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int Select_DedicatedFile() {
		int FileType = 0x04;
		//int FileIndex = 0x00;
		int Idlen = 0x09;
		//char FileId = (0x3F<<8)|0x00;
		byte[] FileId = {(byte) 0xA0,0x00,0x00,0x00,0x03,(byte) 0x86,(byte) 0x98,0x07,0x01};
		return rfid.SelectFile(FileType,Idlen,FileId);
	}
	
//»сИЎЛж»ъКэ
	public int GetChallenge(int Count,byte[] RecBuf)
	{
		return rfid.GetChallenge(Count, RecBuf);
	}
	//НвІїИПЦ¤
	/**
	 * 4ЧЦЅЪЛж»ъКэјУЙП4ЧЦЅЪ0x00Ул8ЧЦЅЪГЬФї¶ФУ¦Ј¬»тХЯ8ЧЦЅЪЛж»ъКэУл16ЧЦЅЪГЬФї¶ФУ¦
	 * @param random Лж»ъКэ
	 * @param Keycode  ГЬФї
	 * @param KeyLen  ГЬФїі¤¶И
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int authentication(byte[] random , byte[] Keycode,int KeyLen)
	{
		byte KeyID = 0x00;
		//byte[] Keycode = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
		//int KeyLen = 16;
		//byte[] Keycode = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
		//int KeyLen = 8;
		return rfid.authentication(KeyID, random,Keycode,KeyLen);
	}
//»сИЎ·µ»ШЦµ
	/**
	 * Count і¤¶И
	 * RecBuf »сИЎCPUїЁµДКэѕЭ
	 */
	public int GetResponse(
        int Count,
        byte[] RecBuf
    )
	{
		return rfid.GetResponse(Count, RecBuf);
	}
//ЙѕіэОДјю
	/**
	 * ЙѕіэMFЦчОДјюОДјю
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int DeleteFile()
	{
		return rfid.DeleteFile();
	}
	//1Ј®	ЕдЦГ¶БїЁЖчДЈКЅ
	public int setReaderMode(byte[] RecBuf)
	{
		return rfid.setReaderMode(RecBuf);
	}
	//ёщѕЭїЁЖ¬АаРНЕдЦГ¶БїЁР­ТйДЈКЅ
	public int setProtocolMode(byte[] RecBuf)
	{
		return rfid.setProtocolMode(RecBuf);
	}
//ЙиЦГ¶БїЁЖчµДРЈСйВл·ЅКЅ ЧФ¶ЇЅУКХі¬К±ЕР±р
	public int setCheckMode(byte[] RecBuf)
	{
		return rfid.setCheckMode(RecBuf);
	}
//С°їЁ
	public int SearchCard(byte[] RecBuf)
	{
		return rfid.SearchCard(RecBuf);
	}
//ЕцЧІСЎїЁ
	public int Anticoll(byte[] RecBuf)
	{
		return rfid.Anticoll(RecBuf);
	}
//СЎїЁ
	/**
	 * 
	 * @param Count їЁєЕД¬ИП4О»Ј¬јУ1О»РЈСйО»
	 * @param SendBuf КдИлµДїЁєЕ
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int SelectCard(int Count,byte[] SendBuf)
	{
		return rfid.SelectCard(Count,SendBuf);
	}
//ёґО»
	/**
	 * 
	 * @param Count ·µ»ШДЪИЭµДі¤¶И Д¬ИП16ЧЦЅЪ
	 * @param RecBuf ёґО»·µ»ШµДДЪИЭ
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int ResetCard(byte[] RecBuf)
	{
		return rfid.ResetCard(RecBuf);
	}

	//МнјУГЬФї
	/**
 	 * 
	 * @param Keycode 8ЧЦЅЪ»т10ЧЦЅЪГЬФї
	 * @param KeyLen ГЬФїі¤¶И
	 * @return ·µ»Ш0x04±нКѕХэИ·
	 */
	public int appendKEY(byte[] Keycode,int KeyLen)
	{
		byte KEY_InMode = PROCLAIMED;
		byte KEY_Opt = 0x01;
		byte KEY_ID = 0x00;
		int Key_Msglen = 5+KeyLen;
		byte[] Key_MsgData = new byte[Key_Msglen] ;
		Key_MsgData[0] = 0x39;
		Key_MsgData[1] = (byte)0xF0;
		Key_MsgData[2] = (byte)0xF0;
		Key_MsgData[3] = (byte)0xAA;
		Key_MsgData[4] = 0x55;
		System.arraycopy(Keycode, 0, Key_MsgData, 5, KeyLen);
		
		return rfid.WriteKEY(KEY_InMode,KEY_Opt,KEY_ID,Key_MsgData,Key_Msglen);
	}
	
	public static int BluetoothSend(byte[] send) {
		if (!SerialPortManager.switchRFID) {
			switchStatus();
		}
		SerialPortManager.getInstance().write(send);
		return 1;

	}

	public static int BluetoothRecv(byte[] Recv, int bytes) {
		bytes = SerialPortManager.getInstance().read(Recv, 3000, 1000);
		if(bytes <=0)
		{
			String rec = "No respose from A602";
			byte[] tempRecv = null;
			try {
				tempRecv = rec.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("from A602 bytes="+bytes);
			System.arraycopy(tempRecv, 0, Recv, 0, tempRecv.length); 
			bytes = tempRecv.length;
		}
		return bytes;
	}
}