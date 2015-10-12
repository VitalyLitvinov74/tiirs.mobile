package android.hardware.uhf.magic;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Pattern;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class reader {
	static public Handler m_handler = null;
	static Boolean m_bASYC = false;
	static byte[] m_buf = new byte[10240];
	static int m_nCount = 0;
	static Boolean m_bLoop = false;
	static Boolean m_bOK = false;
	static int m_nReSend = 0;
	static int m_nread = 0;
	static int msound = 0;
	static SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
	static public String m_strPCEPC = "";

	/**
	 * Initialization device
	 * 
	 * @param strpath
	 */
	static public native void init(String strpath);

	/**
	 * open device
	 * 
	 * @param strpath
	 *            device address is "//dev//ttyS4 CM719"
	 * @return successful return 0, failure to return error flag (-20 product is
	 *         wrong, -1 device could not be opened, 1 the device is turned on,
	 *         the -2 parameter of the equipment could not set)
	 */
	static public native int Open(String strpath);

	/**
	 * read data
	 * 
	 * @param pout
	 *            store read data
	 * @param nStart
	 *            store data in the starting position of pout
	 * @param nCount
	 *            to read data length
	 * @return
	 */
	static public native int Read(byte[] pout, int nStart, int nCount);

	/**
	 * Closing device
	 */
	static public native void Close();

	/**
	 * Clear the cache data equipment
	 */
	static public native void Clean();

	/**
	 * single polling
	 * 
	 * @return OK 0x10,wrong 0x11
	 */
	static public native int Inventory();

	/**
	 * single polling
	 * 
	 * @return return 0x10, error return 0X11
	 */
	static public native int MultiInventory(int ntimes);

	/**
	 * stop repeatedly polling
	 * 
	 * @return return 0x10, error return 0X11
	 */
	static public native int StopMultiInventory();

	/**
	 * set the Select parameter, and set before a single polling or multiple
	 * polling Inventory, to send Select commands. In the multi label case, can
	 * only poll for a specific tag Inventory operation.
	 * 
	 * @param selPa
	 *            parameter (Target: 3B 000, Action: 3B 000, MemBank: 2B 01)
	 * @param nPTR
	 *            (in bit, non word) starting from PC and EPC storage position
	 * @param nMaskLen
	 *            Mask length
	 * @param turncate
	 *            (0x00 is Disable truncation, 0x80 is Enable truncation)
	 * @param mask
	 * @return return 0x00, error is returned not 0
	 */
	static public native int Select(byte selPa, int nPTR, byte nMaskLen,
			byte turncate, byte[] mask);

	/* *
	 * set to send Select commands
	 * 
	 * @param data (0x01 is to cancel the Select instruction, 0x00 is the Select
	 * instruction)
	 * 
	 * @return return 0x00, error is returned not 0
	 */
	static public native int SetSelect(byte data);

	/**
	 * read tag data storage area
	 * 
	 * @param password
	 *            read the password, 4 bytes
	 * @param nUL
	 *            PC+EPC length
	 * @param PCandEPC
	 *            PC+EPC data
	 * @param membank
	 *            tag data storage area
	 * @param nSA
	 *            read tag data address offset
	 * @param nDL
	 *            read tag data address length
	 * @return
	 */
	static public native int ReadLable(byte[] password, int nUL,
			byte[] PCandEPC, byte membank, int nSA, int nDL);

	/**
	 * write tag data storage area
	 * 
	 * @param password
	 *            password 4 bytes
	 * @param nUL
	 *            PC+EPC length
	 * @param PCandEPC
	 *            PC+EPC data
	 * @param membank
	 *            tag data storage area
	 * @param nSA
	 *            write the tag data address offset
	 * @param nDL
	 *            write tag data area data length
	 * @param data
	 *            write data
	 * @return
	 */
	static public native int WriteLable(byte[] password, int nUL,
			byte[] PCandEPC, byte membank, int nSA, int nDL, byte[] data);

	/**
	 * for a single label, data store Lock lock or unlock Unlock the label
	 * 
	 * @param password
	 *            lock password
	 * @param nUL
	 *            PC+EPC length
	 * @param PCandEPC
	 *            PC+EPC data
	 * @param nLD
	 *            lock or unlock command
	 * @return
	 */

	static public native int Lock(byte[] password, int nUL, byte[] PCandEPC,
			int nLD);

	/**
	 * the inactivation of Kill Tags
	 * 
	 * @param password
	 *            Cipher
	 * @param nUL
	 *            PC+EPC length
	 * @param EPC
	 *            PC+EPC content
	 * @return
	 */
	static public native int Kill(byte[] password, int nUL, byte[] EPC);

	/**
	 * inactivated label (results through the Handle asynchronous transmission)
	 * 
	 * @param btReadId
	 *            reader.
	 * @param pbtAryPassWord
	 *            destroy the password (4 bytes)
	 * @return
	 */
	static public int KillLables(byte[] password, int nUL, byte[] EPC) {
		Clean();
		int nret = Kill(password, nUL, EPC);
		if (!m_bASYC) {
			StartASYCKilllables();
		}
		return nret;
	}

	/**
	 * To obtain the parameters
	 * 
	 * @return
	 */
	static public native int Query();

	/**
	 * set the relevant parameters in the Query command
	 * 
	 * @param nParam
	 *            parameter is 2 bytes, the specific parameters of the following
	 *            bit spliced: DR (1 bit): DR=8 (1b0), DR=64/3 (1B1). M mode
	 *            only supports DR=8 (2 bit): M=1 (2b00), M=2 (2B01), M=4
	 *            (2b10), M=8 (2B11). Mode of TRext only supports M=1 (1 bit):
	 *            No pilot tone (1b0), Use pilot tone (1B1). Only supports Use
	 *            pilot tone (1B1) model of Sel (2 bit): ALL (2b00/2b01), ~SL
	 *            (2b10), SL (2B11) Session (2 bit): S0 (2b00), S1 (2B01), S2
	 *            (2b10), S3 (2B11) Target (1 bit): A (1b0), B (1B1) Q (4 bit):
	 *            4b0000-4b1111
	 * @return
	 */
	static public native int SetQuery(int nParam);

	/**
	 * set working area band
	 * 
	 * @param region
	 *            Region Parameter 01 900MHz 04 800MHz 02 China Chinese American
	 *            03 Europe 06 of South Korea
	 * @return
	 */
	static public native int SetFrequency(byte region);

	/**
	 * set working channel
	 * 
	 * @param channel
	 *            formula China 900MHz channel parameters, Freq_CH channel
	 *            frequency: CH_Index = (Freq_CH-920.125M) /0.25M
	 * 
	 *            formula China 800MHz channel parameters, Freq_CH channel
	 *            frequency: CH_Index = (Freq_CH-840.125M) /0.25M
	 * 
	 *            formula USA channel parameters, Freq_CH channel frequency:
	 *            CH_Index = (Freq_CH-902.25M) /0.5M
	 * 
	 *            formula of European channel parameters, Freq_CH channel
	 *            frequency: CH_Index = (Freq_CH-865.1M) /0.2M
	 * 
	 *            formula of Korea channel parameters, Freq_CH channel
	 *            frequency: CH_Index = (Freq_CH-917.1M) /0.2M
	 * @return
	 */
	static public native int SetChannel(byte channel);

	/**
	 * get working channel
	 * 
	 * calculation formula of @return Chinese 900MHz channel parameters, Freq_CH
	 * channel frequency: Freq_CH = CH_Index * 0.25M + 920.125M
	 * 
	 * formula Chinese 800MHz channel parameters, Freq_CH channel frequency:
	 * Freq_CH = CH_Index * 0.25M + 840.125M
	 * 
	 * formula American channel parameters, Freq_CH channel frequency: Freq_CH =
	 * CH_Index * 0.5M + 902.25M
	 * 
	 * formula of European channel parameters, Freq_CH channel frequency:
	 * Freq_CH = CH_Index * 0.2M + 865.1M
	 */
	static public native int GetChannel();

	/**
	 * set to automatic frequency hopping pattern or cancel automatic frequency
	 * hopping pattern
	 * 
	 * @param Auto
	 *            0xFF set for automatic frequency hopping, 0x00 to cancel
	 *            automatic frequency hopping
	 * @return
	 */
	static public native int SetAutoFrequencyHopping(byte auto);

	/**
	 * access transmission power
	 * 
	 * @return returns the actual transmission power
	 */
	static public native int GetTransmissionPower();

	/**
	 * set the transmit power
	 * 
	 * @param nPower
	 *            emission power
	 * @return
	 */
	static public native int SetTransmissionPower(int nPower);

	/**
	 * set emission continuous carrier or closed continuous carrier
	 * 
	 * @param bOn
	 *            0xFF continuous wave 0x00 is open, closed continuous wave
	 * @return
	 */
	static public native int SetContinuousCarrier(byte bOn);

	/**
	 * get the current reader receiving demodulator parameters
	 * 
	 * @param bufout
	 *            two bytes, the first mixer gain, second intermediate frequency
	 *            amplifier gain mixer gain table Type Mixer Mixer_G (dB) 0x00 0
	 *            0x01 3 0x02 6 0x03 9 0x04 12 0x05 15 0x06 16 IF AMP IF
	 *            amplifier gain table Type IF_G (dB) 0x00 12 0x01 18 0x02 21
	 *            0x03 24 0x04 27 0x05 30 0x06 36 0x07 40
	 * @return
	 */
	static public native int GetParameter(byte[] bufout);

	/**
	 * set the current reader receiving demodulator parameters
	 * 
	 * @param bMixer
	 *            the mixer gain
	 * @param bIF
	 *            if amplifier gain
	 * @param nThrd
	 *            signal regulating threshold, signal demodulation threshold is
	 *            small can demodulate the tag returns RSSI is lower, but more
	 *            unstable, less than one fixed value can not be completely
	 *            opposite bigger demodulation; threshold can return signal
	 *            demodulation label RSSI is larger, the shorter the distance,
	 *            the more stable. 0x01B0 is the minimum recommended
	 * @return
	 */
	static public native int SetParameter(byte bMixer, byte bIF, int nThrd);

	/**
	 * Test RF input blocking signal
	 * 
	 * @param bufout
	 * @return
	 */
	static public native int ScanJammer(byte[] bufout);

	/**
	 * Test RF input RSSI signal size, used to detect the current environment
	 * without the reader at work
	 * 
	 * @param bufout
	 * @return
	 */
	static public native int TestRssi(byte[] bufout);

	/**
	 * Set the IO port direction, read the IO level and IO level setting
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 *            description length specification 0 parameters for 01 byte
	 *            operation type selection: 0x00: set IO direction; 0x01: set IO
	 *            level; 0x02: read the IO level. To the operation of the pins
	 *            in parameter 1 specifies 1 Parameters byte parameter value
	 *            range is 11 0x01~0x04, corresponding to the operation of the
	 *            port 2 of the IO1~IO4 parameters of the 21 byte values are
	 *            0x00 or 0x01. Parameter0 Parameter2 description of the 0x00
	 *            0x00 IO is configured as an input mode of 0x00 0x01 IO
	 *            configuration IO output to output mode 0x01 0x00 is set to a
	 *            low level 0x01 0x01 sets the IO output is high when parameter
	 *            0 is 0x02, no meaning of the parameters.
	 * @param bufout
	 * @return
	 */
	static public native int SetIOParameter(byte p1, byte p2, byte p3,
			byte[] bufout);

	static public int InventoryLables() {
		int nret = Inventory();
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;
	}

	static public int InventoryLablesLoop() {
		int nret = Inventory();
		m_bLoop = true;
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;
	}

	static public void StopLoop() {
		m_bLoop = false;
	}

	static public int MultInventoryLables() {
		int nret = MultiInventory(65535);
		if (!m_bASYC) {
			StartASYClables();
		}
		return nret;
	}

	/***
	 * read the label (results through the Handle asynchronous send a card, a
	 * message)
	 * 
	 * @param password
	 *            read the password, 4 bytes
	 * @param nUL
	 *            PC+EPC length
	 * @param PCandEPC
	 *            PC+EPC data
	 * @param membank
	 *            tag data storage area
	 * @param nSA
	 *            read tag data address offset
	 * @param nDL
	 *            read tag data address length
	 * @return
	 */
	static public int ReadLables(byte[] password, int nUL, byte[] PCandEPC,
			byte membank, int nSA, int nDL) {
		int nret = 0;
		if (!m_bASYC) {
			Clean();
			nret = ReadLable(password, nUL, PCandEPC, membank, nSA, nDL);
			m_bOK = false;
			m_nReSend = 0;
			StartASYCReadlables();
			while ((!m_bOK) && (m_nReSend < 20)) {
				m_nReSend++;
				ReadLable(password, nUL, PCandEPC, membank, nSA, nDL);
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return nret;
	}

	/**
	 * for a single label, data store Lock lock or unlock Unlock the label
	 * 
	 * @param password
	 *            lock password
	 * @param nUL
	 *            PC+EPC length
	 * @param PCandEPC
	 *            PC+EPC data
	 * @param nLD
	 *            lock or unlock command
	 * @return
	 */
	static public int LockLables(byte[] password, int nUL, byte[] PCandEPC,
			int nLD) {
		Clean();
		int nret = Lock(password, nUL, PCandEPC, nLD);
		if (!m_bASYC) {
			StartASYCLocklables();
		}
		return nret;
	}

	/**
	 * write tag (results through the Handle asynchronous send a card, a
	 * message)
	 * 
	 * @param password
	 *            password 4 bytes
	 * @param nUL
	 *            PC+EPC length
	 * @param PCandEPC
	 *            PC+EPC data
	 * @param membank
	 *            tag data storage area
	 * @param nSA
	 *            write the tag data address offset
	 * @param nDL
	 *            write tag data area data length
	 * @param data
	 *            write data
	 * @return
	 */
	static public int Writelables(byte[] password, int nUL, byte[] PCandEPC,
			byte membank, int nSA, int nDL, byte[] data) {
		Clean();
		int nret = WriteLable(password, nUL, PCandEPC, membank, nSA, nDL, data);
		if (!m_bASYC) {
			m_bOK = false;
			m_nReSend = 0;
			StartASYCWritelables();
			while ((!m_bOK) && (m_nReSend < 20)) {
				m_nReSend++;
				WriteLable(password, nUL, PCandEPC, membank, nSA, nDL, data);
				try {
					Thread.sleep(60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return nret;
	}
	
	static public int getLockPayLoadNew(byte memoryBank, byte accessMask) {
		int mask = 0x000000;
		switch (memoryBank) {
		// kill password
		case 0:
			switch (accessMask) {
			// unlock
			case 0:
				mask = 0x0c0000;
				break;
			// lock
			case 1:
				mask = 0x0c0200;
				break;
			// permanent unlock
			case 2:
				mask = 0x0c0100;
				break;
			// permanent lock
			case 3:
				mask = 0x0C0300;
				break;
			}
			break;
		// access password
		case 1:
			switch (accessMask) {
			// open
			case 0:
				mask = 0x080000;
				break;
			// pwd r/w
			case 1:
				mask = 0x080200;
				break;
			// permanent open
			case 2:
				mask = 0x080100;
				break;
			// permanent close
			case 3:
				mask = 0x080300;
				break;
			}
			break;
		// EPC
		case 2:
			break;
		// TID
		case 3:
			break;
		// USER
		case 4:
			break;
		default:
			break;
		}
		
		return mask;
	}

	static public int GetLockPayLoad(byte membank, byte Mask) {
		int nret = 0;
		/*
		switch (Mask) {
		case 0:
			switch (membank) {
		*/
		switch (membank) {
		case 0:
			switch (Mask) {
			case 0:
				nret = 0x080000;
				break;
			case 1:
				nret = 0x080200;
				break;
			case 2:
				nret = 0x0c0100;
				break;
			case 3:
				nret = 0x0c0300;
				break;
			}
			break;
		case 1:
			switch (Mask) {
			case 0:
				nret = 0x020000;
				break;
			case 1:
				nret = 0x020080;
				break;
			case 2:
				nret = 0x030040;
				break;
			case 3:
				nret = 0x0300c0;
				break;
			}
			break;
		case 2:
			switch (Mask) {
			case 0:
				nret = 0x008000;
				break;
			case 1:
				nret = 0x008020;
				break;
			case 2:
				nret = 0x00c010;
				break;
			case 3:
				nret = 0x00c030;
				break;
			}
			break;
		case 3:
			switch (Mask) {
			case 0:
				nret = 0x002000;
				break;
			case 1:
				nret = 0x002008;
				break;
			case 2:
				nret = 0x003004;
				break;
			case 3:
				nret = 0x00300c;
				break;
			}
			break;
		case 4:
			switch (Mask) {
			case 0:
				nret = 0x000800;
				break;
			case 1:
				nret = 0x000802;
				break;
			case 2:
				nret = 0x000c01;
				break;
			case 3:
				nret = 0x000c03;
				break;
			}
			break;
		}
		return nret;
	}

	static void StartASYCKilllables() {
		m_bASYC = true;
		Thread thread = new Thread(new Runnable() {
			public void run() {
				int nTemp = 0;
				m_nCount = 0;
				m_nread = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread > 5)
							break;

					}
					// Log.e("test",""+m_nCount+"="+m_nread);
					String str = reader.BytesToString(m_buf, 0, m_nCount);
					// Log.e("test",str);
					String[] substr = Pattern.compile("BB0165").split(str);
					// Log.e("test","sub="+substr.length);
					for (int i = 0; i < substr.length; i++) {
						if (substr[i].length() >= 10) {
							if (substr[i].substring(0, 10).equals("000100677E")) {
								Message msg = new Message();
								msg.what = 2;
								msg.obj = "OK".getBytes();
								m_handler.sendMessage(msg);
							} else {
								Message msg = new Message();
								msg.what = 2;
								msg.obj = substr[i];
								m_handler.sendMessage(msg);
							}
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
				m_nread = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread > 5)
							break;

					}
					String str = reader.BytesToString(m_buf, 0, m_nCount);
					String[] substr = Pattern.compile("BB0182").split(str);
					for (int i = 0; i < substr.length; i++) {
						if (substr[i].length() >= 10) {
							if (substr[i].substring(0, 10).equals("000100847E")) {
								Message msg = new Message();
								msg.what = 2;
								msg.obj = "OK";
								m_handler.sendMessage(msg);
								Log.d("test", "Lock=OK");
							} else {
								Message msg = new Message();
								msg.what = 2;
								msg.obj = substr[i];
								m_handler.sendMessage(msg);
								Log.d("test", "Lock=FAIL");
							}
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
				m_nread = 0;
				while (m_handler != null) {

					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread > 5)
							break;
					}
					String str = reader.BytesToString(m_buf, 0, m_nCount);
					// Log.e("test", str);
					String[] substr = Pattern.compile("BB0149").split(str);
					// Log.e("test", "strlen="+substr.length);
					for (int i = 0; i < substr.length; i++) {
						if (substr[i].length() >= 10) {
							if (substr[i].substring(0, 10).equals("0001004B7E")) {
								m_bOK = true;
								// Log.e("test", "ok");
								Message msg = new Message();
								msg.what = 2;
								msg.obj = "OK";
								m_handler.sendMessage(msg);
							}
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
				m_nread = 0;
				int m_nReRead=0;
				while (m_handler != null && (m_nReRead >= 0)) {
					nTemp = Read(m_buf, m_nCount, 1024);
					m_nCount += nTemp;
					m_nReRead++;
					if (nTemp == 0) {
						m_nread++;
						if (m_nread > 5)
							break;
					}
					String str = reader.BytesToString(m_buf, 0, m_nCount);
					Log.e("test", str);
					Log.e("test","m_bOK="+m_bOK);
					String[] substr = Pattern.compile("BB0139").split(str);
					for (int i = 0; i < substr.length; i++) {
						Log.e("test",substr[i]);
						if (substr[i].length() > 10) {
							if (!substr[i].substring(0, 2).equals("BB")) {
								Log.e("test","read ok");
								m_bOK = true;
								Message msg = new Message();
								msg.what = (substr[i].length() - 8) / 2;
								msg.obj = substr[i].substring(4,
										substr[i].length() - 4);
								/*
								msg.obj = decodeString(
										substr[i].substring(4,
												substr[i].length() - 4), "GBK");
								*/
								m_handler.sendMessage(msg);
							}
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
				int nTemp = 0, nIndex = 0;
				int m_nReRead=0;
				boolean tag_find = false;
				m_nCount = 0;
				m_nReSend = 0;
				nIndex = 0;
				while (m_handler != null && (m_nReRead >= 0)) {
					// nIndex = m_nCount;
					nTemp = Read(m_buf, m_nCount, 10240 - m_nCount);
					m_nCount += nTemp;
					m_nReRead++;
					// Log.e("777777777777777777", "count=" + m_nCount);
					if (nTemp == 0) {

						String str = reader.BytesToString(m_buf, nIndex,
								m_nCount - nIndex);
						Log.e("test", str);
						String[] substr = Pattern.compile("BB0222").split(str);
						Log.e("test", "len=" + substr.length);
						for (int i = 0; i < substr.length; i++) {
							Log.e("test", substr[i]);
							if (substr[i].length() > 16) {
								if (!substr[i].substring(0, 2).equals("BB")) {
									int nlen = Integer.valueOf(
											substr[i].substring(0, 4), 16);
									Log.e("test",substr[i].substring(0,4));
									if ((nlen > 3)
											&& (nlen < (substr[i].length() - 6) / 2)) {
										Message msg = new Message();
										msg.what = (substr[i].length() - 12) / 2;
										msg.obj = substr[i].substring(6,
												nlen * 2);
										m_handler.sendMessage(msg);
										tag_find = true;
										m_bOK = true;
									}
								}
							}
						}
						if (tag_find) {

							//mSoundPool.play(msound, 1.0f, 1.0f, 0, 0, 1.0f);
						}
						if (m_bLoop) {
							m_nCount = 0;
							InventoryLablesLoop();
							tag_find = false;
						} else {
							if ((m_nReSend < 20) && (!tag_find)) {
								Inventory();
								m_nReSend++;
							} else
								break;
							tag_find = false;
							// Log.e("efsfsd", "m_nReSend=" + m_nReSend);
						}

						if (m_nCount >= 1024)
							m_nCount = 0;
					}
				}
				// Log.e("end", "quit");
				m_bASYC = false;
			}
		});
		thread.start();
	}

	static {
		System.loadLibrary("uhf-tools");
		msound = mSoundPool.load("/system/media/audio/notifications/Altair.ogg",
				1);
	}

	public static byte[] stringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase(Locale.ENGLISH);
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
			ret += hex.toUpperCase(Locale.ENGLISH);
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

	public static int byteToInt(byte[] b, int nIndex, int ncount) // byteToInt
	{
		int t2 = 0, temp = 0;
		for (int i = 0; i < ncount; i++) {
			t2 = t2 << 8;
			temp = b[i + nIndex];
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

	private static byte[] string2Bytes(String string) {
		int blen = string.length() / 2;
		byte[] data = new byte[blen];
		for (int i = 0; i < blen; i++) {
			String bStr = string.substring(2 * i, 2 * (i + 1));
			data[i] = (byte) Integer.parseInt(bStr, 16);
		}
		return data;
	}

	private static String decodeString(String string, String encoding) {
		try {
			byte[] data = string2Bytes(string);
			return new String(data, encoding);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
