//package android.hardware.rfid;
package android.hardware.uhf;

import java.util.Arrays;
import java.util.regex.Pattern;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

//import android.util.Log; //mifarereader
public class reader {
	static public Handler m_handler = null;
	static Boolean m_bASYC = false;
	static byte[] m_buf = new byte[10240];
	static int m_nCount = 0;
	/**
	 * open the device
	 * @param strpath CM719 device address is"//dev//ttyS4"
	 * @return success return0,failed return the error code(-20 is device type not right,
	 * -1 is can't open the device,1 is device is already opened,-2 the parameter can't set) 
	 * 
	 */
	static public native int Open(String strpath);
	/**
	 * Read data
	 * @param pout		restore the read data 
	 * @param nStart	restore the data in the pout start position
	 * @param nCount	the read data length
	 * @return
	 */
	static public native int Read(byte[] pout, int nStart, int nCount);
/**
 * close device
 */
	static public native void Close();
	/**
	 * clear the data in the cache
	 */
	static public native void Clean();
	/**
	 * restart the device
	 * @param readID 	the reader address(0-254)255 is the public address
	 * @return 			success is return 0x10, failed return 0x11
	 */
	static public native int ResetDevice(byte readID);
	/**
	 *  get the hardware version
	 * @param readID 	the reader address(0-254)255 is the public address
	 * @param pout		2bytes,pout[0] is hardware main version,pout[1] is hardware second version
	 * @return 			success return 0x10, failed return 0x11
	 */
	static public native int GetFirmwareVersion(byte readID, byte[] pout);
	/**
	 * set the reader address
	 * @param readID	the current address of the reader 
	 * @param newID		the new address of the reader
	 * @return			success return 0x10, failed return 0x11
	 */
	static public native int SetReaderAddress(byte readID, byte newID);
	/**
	 * set the work antenna, it's only one antenna in the device, so needn't to set
	 * @param readID		the reader address 
	 * @param WorkAntenna	antenna number(1-4)
	 * @return			    success return 0x10, failed return 0x11
	 */
	static public native int SetWorkAntenna(byte readID, byte WorkAntenna);
	/**
	 *  Get the current antenna number
	 * @param readID		The reader address
	 * @return				antenna number
	 */
	static public native int GetWorkAntenna(byte readID);
	/**
	 * set the Output Power
	 * @param readID		The reader address
	 * @param outputPower	output power£¨0x14-0x21£©
	 * @return				success return 0x10, failed return 0x11
	 */
	static public native int SetOutputPower(byte readID, byte outputPower);
	/**
	 * Get the current transmitted power
	 * @param readID		The reader address
	 * @return				failed return 0x11£¬success return transmitted power£¨0x14-0x21£©
	 */
	static public native int GetOutputPower(byte readID);
	/**
	 * To obtain the frequency point return loss value of the current working antenna gain the frequency point in the current working return loss value of the antenna 
	 * @param readID		The reader address
	 * @param Frequency		Test frequency
	 * @return				failed return 0x11£¬success return "Return loss value"£¨unit is db£©
	 */
	static public native int GetRFReturnLoss(byte readID, byte Frequency);
	/**
	 * set system default frequency point
	 * @param readID		The reader address
	 * @param Region		Radio-frequency cables£¨0x01 FCC£¬0x02 EISI£¬0x03 CHN£©
	 * @param StartRegion	frequency start point
	 * @param EndRegion		frequency end point
	 * @return				success return 0x10, failed return 0x11
	 */
	static public native int SetFrequencyRegion(byte readID, byte Region,
			byte StartRegion, byte EndRegion);
	/**
	 * The custom setting frequency points
	 * @param readID			The reader address
	 * @param nStartFreq		The start frequency(unit is KHZ£¬In the former hexadecimal high£¬e.g.£º915000KHZ£¬send 0D F6 38£©
	 * @param btFreqSpace		Frequency interval£¨btFreqSpace*10KHZ£©
	 * @param btRreqQuantity	Contains the number of frequency points starting frequency£¬must more than 0
	 * @return					success return 0x10, failed return 0x11
	 */
	static public native int SetUserDefineFrequency(byte readID,
			int nStartFreq, byte btFreqSpace, byte btRreqQuantity);

	/**
	 * Get the frequency range
	 * @param readID		The reader address
	 * @param pout			If use the system default will return 3 bytes(From low to high, RF specifications, RF, RF end points from low to high starting point, the RF specifications, starting point of RF, RF end points)
	 * If auto will return 5 bytes(Frequency interval, frequency point number, frequency starting point(3 bytes))
	 * @return				success return 0x10, failed return 0x11
	 */
	static public native int GetFrequencyRegion(byte readID, byte[] pout);

	static public native int SetBeepMode(byte readID, byte mode);

	static public native int GetReaderTemperature(byte readID, byte[] pout);
	/**
	 * set DRM mode
	 * @param readID			The reader address
	 * @param btDrmMode			DRM mode£¨0 is closeDRM£¬1 is open DRM£©
	 * @return					success return 0x10, failed return 0x11
	 */
	static public native int SetDrmMode(byte readID, byte btDrmMode);
	/**
	 *  Get DRM mode
	 * @param readID			The reader address
	 * @return					return the current mode(0 is close DRM, 1 is open DRM, 0x11 is failed)
	 */
	static public native int GetDrmMode(byte readID);
	/**
	 * Get GPIO value
	 * @param readID			The reader address
	 * @param pout				
	 * @return
	 */
	static public native int ReadGpioValue(byte readID, byte[] pout);

	static public native int WriteGpioValue(byte readID, byte btChooseGpio,
			byte btGpioValue);

	static public native int SetAntDetector(byte readID, byte DetectorStatus);

	static public native int GetImpinjFastTid(byte readID);

	static public native int SetImpinjFastTid(byte readID, byte FastTid);

	static public native int SetRfProfile(byte readID, byte ProfileId);

	static public native int GetRfProfile(byte readID);

	static public native int GetReaderIdentifier(byte readID, byte[] pout);

	static public native int SetReaderIdentifier(byte readID, byte[] pin);

	static public native int Inventory(byte readID, byte btRepeat, byte[] pout);

	static public native int SetAccessEpcMatch(byte readID, byte btMode,
			byte btEpcLen, byte[] pin);

	static public native int GetAccessEpcMatch(byte readID, byte[] pout);

	static public native int GetInventoryBufferTagCount(byte readID);

	static public native int LockTagISO18000(byte readID, byte[] AryUID,
			byte btWordAdd, byte[] pout);

	static public native int QueryTagISO18000(byte readID, byte[] AryUID,
			byte WordAdd, byte[] pout);

	static public native int ReadTag(byte btReadId, byte btMemBank,
			byte btWordAdd, byte btWordCnt, byte[] pPassword);

	static public native int WriteTag(byte btReadId, byte[] AryPassWord,
			byte btMemBank, byte btWordAdd, byte btWordCnt, byte[] jAryData);

	static public native int LockTag(byte btReadId, byte[] pbtAryPassWord,
			byte btMembank, byte btLockType);

	static public native int KillTag(byte btReadId, byte[] pbtAryPassWord);

	static public native int GetInventoryBuffer(byte btReadId);

	static public native int GetAndResetInventoryBuffer(byte btReadId);

	static public native int InventoryReal(byte btReadId, byte byRound);
	/**
	 * Destruction of the label   £¨the result is send by Handle and asynchronous sending mode£©
	 * @param btReadId			The reader address
	 * @param pbtAryPassWord	Destruction of the password£¨4 bytes£©
	 * @return
	 */
	static public int KillLables(byte btReadId, byte[] pbtAryPassWord) {
		Clean();
		int nret = KillTag(btReadId, pbtAryPassWord);
		if (!m_bASYC) {
			StartASYCKilllables();
		}
		return nret;
	}
	/**
	 * Lock tag £¨the result is send by Handle and asynchronous sending mode£©
	 * @param btReadId			The reader address
	 * @param pbtAryPassWord	Access Password£¨4  bytes£©
	 * @param btMembank			Lock regional£¨Access Password£¨4£©¡¢Destruction of the password£¨5£©¡¢EPC£¨3£©¡¢TID£¨2£©¡¢USER£¨1£©£©
	 * @param btLockType		Lock type£¨open£¨0£©¡¢lock£¨1£©¡¢Open forever£¨2£©¡¢lock forever£¨3£©£©
	 * @return
	 */
	static public int LockLables(byte btReadId, byte[] pbtAryPassWord,
			byte btMembank, byte btLockType) {
		Clean();
		int nret = LockTag(btReadId, pbtAryPassWord, btMembank, btLockType);
		if (!m_bASYC) {
			StartASYCLocklables();
		}
		return nret;
	}
	/**
	 * Search card£¨the result is send by Handle and asynchronous sending mode£©
	 * @param btReadId			The reader address
	 * @param byRound			The number of repeat inventory process
	 * @return
	 */
	static public int SearchLables(byte btReadId, byte byRound) {
		Clean();
		int nret = InventoryReal(btReadId, byRound);
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;    
	}  
	/**
	 * Read Tag(the result is send by Handle and asynchronous sending mode£¬one card, one information)
	 * @param btReadId			The reader address
	 * @param btMemBank			The Tag storage areas£¨reserve£¨0£©¡¢EPC£¨1£©¡¢TID£¨2£©¡¢USER£¨3£©£©
	 * @param btWordAdd			The first address of read data
	 * @param btWordCnt			The length of read data
	 * @param pPassword			The access password of the tag(4bytes)
	 * @return
	 */
	static public int ReadLables(byte btReadId, byte btMemBank, byte btWordAdd,
			byte btWordCnt, byte[] pPassword) {
		int nret = 0;
		if (!m_bASYC) {
			Clean();
			nret = ReadTag(btReadId, btMemBank, btWordAdd, btWordCnt, pPassword);

			StartASYCReadlables();
		}
		return nret;
	}
	/**
	 * Write Tag(the result is send by Handle and asynchronous sending mode£¬one card, one information£©
	 * @param btReadId			The Reader address
	 * @param AryPassWord		access password£¨4bytes£©
	 * @param btMemBank			The Tag storage areas£¨reserve£¨0£©¡¢EPC£¨1£©¡¢TID£¨2£©¡¢USER£¨3£©£©
	 * @param btWordAdd			The first address of read data
	 * @param btWordCnt			The length of write data
	 * @param jAryData			write data
	 * @return
	 */
	static public int Writelables(byte btReadId, byte[] AryPassWord,
			byte btMemBank, byte btWordAdd, byte btWordCnt, byte[] jAryData) {
		Clean();
		int nret = WriteTag(btReadId, AryPassWord, btMemBank, btWordAdd,
				btWordCnt, jAryData);
		if (!m_bASYC) {
			StartASYCWritelables();
		}
		return nret;
	}

	static void StartASYCKilllables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0)
						break;
					String str=reader.BytesToString(m_buf, 0, m_nCount);
					String[]substr=Pattern.compile("A0(.*?)84").split(str); 
					for(int i=0;i<substr.length;i++)
					{
						if(substr[i].length()>4)
						{
							Message msg = new Message();
							msg.what = (substr[i].length()-14)/2;
							msg.obj = substr[i].substring(6, substr[i].length()-8);
							m_handler.sendMessage(msg);
						}
						else
						{
							Message msg = new Message();
							msg.what = substr[i].length() == 4 ? -1 : 0;
							msg.obj = "";
							m_handler.sendMessage(msg);
						}
					
					}

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYCLocklables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0)
						break;
					String str=reader.BytesToString(m_buf, 0, m_nCount);
					String[]substr=Pattern.compile("A0(.*?)83").split(str); 
					for(int i=0;i<substr.length;i++)
					{
						if(substr[i].length()>4)
						{
							Message msg = new Message();
							msg.what = (substr[i].length()-14)/2;
							msg.obj = substr[i].substring(6, substr[i].length()-8);
							m_handler.sendMessage(msg);
						}
						else
						{
							Message msg = new Message(); 
							msg.what = substr[i].length() == 4 ? -1 : 0;
							msg.obj = ""+i;
							m_handler.sendMessage(msg);
						}
					
					}

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYCWritelables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0)
						break;
					String str=reader.BytesToString(m_buf, 0, m_nCount);
					String[]substr=Pattern.compile("A0(.*?)82").split(str); 
					for(int i=0;i<substr.length;i++)
					{
						if(substr[i].length()>4)
						{
							Message msg = new Message();
							msg.what = (substr[i].length()-14)/2;
							msg.obj = substr[i].substring(6, substr[i].length()-8);
							m_handler.sendMessage(msg);
						}
						else
						{
							Message msg = new Message();
							msg.what = substr[i].length() == 4 ? -1 : 0;
							msg.obj = "";
							m_handler.sendMessage(msg);
						}
					
					}

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYCReadlables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0)
						break;
					String str=reader.BytesToString(m_buf, 0, m_nCount);
					String[]substr=Pattern.compile("A0(.*?)81").split(str); 
					for(int i=0;i<substr.length;i++)
					{
						if(substr[i].length()>4)
						{
							Message msg = new Message();
							msg.what = (substr[i].length()-14)/2;
							msg.obj = substr[i].substring(6, substr[i].length()-8);
							m_handler.sendMessage(msg);
						}
						else
						{
							Message msg = new Message();
							msg.what = substr[i].length() == 4 ? -1 : 0;
							msg.obj = "";
							m_handler.sendMessage(msg);
						}
					
					}

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static void StartASYClables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0,nIndex=0;
				boolean tag_find=false;
				m_nCount = 0;
				while (m_handler != null) {  
					nIndex=m_nCount;
					nTemp = Read(m_buf, m_nCount, 10240-m_nCount);
					m_nCount += nTemp;
					if (nTemp == 0)
						break;
					String str=reader.BytesToString(m_buf, nIndex, m_nCount-nIndex);
					String[]substr=Pattern.compile("A0(.*?)89").split(str); 
					for(int i=0;i<substr.length;i++)
					{
						Log.e("777777777777777777777777777", substr[i]);
						if(substr[i].length()>16)
						{
							tag_find = true;
							Message msg = new Message();
							msg.what = (substr[i].length()-10)/2;
							msg.obj = substr[i].substring(6, substr[i].length()-4);
							m_handler.sendMessage(msg);
						}
						else
						{
							
							Message msg = new Message();
							msg.what = substr[i].length() == 4 ? -1 : 0;
							msg.obj = tag_find?"1":"0";
							//msg.obj = "";
							m_handler.sendMessage(msg);
							Log.e("end", "tertretretert");
							tag_find=false;
						}

					}
					if(m_nCount>=1024)  
					m_nCount=0;

				}

				m_bASYC = false;
			}
		});
		thread.start();
	}

	static {
		System.loadLibrary("uhf-tools");
	}

	public static byte[] stringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String BytesToString(byte[] b, int nS, int ncount) {
		String ret = "";
		int nMax = ncount > (b.length - nS) ? b.length - nS : ncount;
		for (int i = 0; i < nMax; i++) {
			String hex = Integer.toHexString(b[i + nS] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static int byteToInt(byte[] b) // byteToInt
	{
		int t2 = 0, temp = 0;
		for (int i = 3; i >= 0; i--) {
			t2 = t2 << 8;
			temp = b[i];
			if (temp < 0) {
				temp += 256;
			}
			t2 = t2 + temp;

		}
		return t2;

	}
	public static int byteToInt(byte[] b,int nIndex) // byteToInt
	{
		int t2 = 0, temp = 0;
		for (int i = 3; i >= 0; i--) {
			t2 = t2 << 8;
			temp = b[i];
			if (temp < 0) {
				temp += 256;
			}
			t2 = t2 + temp;

		}
		return t2;

	}

	/**** int to byte ******/
	public static byte[] intToByte(int content, int offset) {

		byte result[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		for (int j = offset; j < result.length; j += 4) {
			result[j + 3] = (byte) (content & 0xff);
			result[j + 2] = (byte) ((content >> 8) & 0xff);
			result[j + 1] = (byte) ((content >> 16) & 0xff);
			result[j] = (byte) ((content >> 24) & 0xff);
		}
		return result;
	}

}
