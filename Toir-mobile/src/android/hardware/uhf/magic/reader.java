package android.hardware.uhf.magic;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class reader {

	private static final String TAG = "reader";

	// обработчик через который класс общается с внешним миром
	static public Handler m_handler = null;

	// маски доступа к областям памяти
	public static final int UNLOCK = 0;
	public static final int PERMANENT_UNLOCK = 1;
	public static final int LOCK = 2;
	public static final int PERMANENT_LOCK = 3;

	// области памяти метки для блокировки
	public static final int MEMBLOCK_KILL_PWD = 0;
	public static final int MEMBLOCK_ACCESS_PWD = 1;
	public static final int MEMBLOCK_TID = 2;
	public static final int MEMBLOCK_EPC = 3;
	public static final int MEMBLOCK_USER = 4;

	private static ParseThread readThread;

	static {
		System.loadLibrary("uhf-tools");
	}

	/**
	 * Запуск процесса деактивации метки. Новый вариант, с правильным разбором
	 * данных поступающих из считывателя.
	 * 
	 * @param password
	 * @param pcEpc
	 * @param memoryBank
	 * @param offset
	 * @param data
	 * @param timeOut
	 */
	static public void killTag(String password, String pcEpc, int timeOut) {

		final byte[] fPassword = string2Bytes(password);
		final byte[] fPcEpc = string2Bytes(pcEpc);

		// обработчик для повторной отправки команды в считыватель или отправки
		// сообщения о успешном выполнении
		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					// отправляем сообщение о успешном чтении данных
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
					m_handler.sendMessage(message);
				} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
					// отправляем сообщение о таймауте
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_TIMEOUT;
					m_handler.sendMessage(message);
				} else {
					// деактивация не удалась
					// отправляем повторно команду деактивации
					Kill(fPassword, fPcEpc.length, fPcEpc);
				}
				return true;
			}
		});

		readThread = new ParseThread(ParseThread.KILL_TAG_COMMAND, timeOut);
		readThread.setResendCommandHandler(handler);
		readThread.start();

		// отправляем команду деактивации
		Kill(fPassword, fPcEpc.length, fPcEpc);

	}

	/**
	 * Запуск процесса блокировки метки. Новый вариант, с правильным разбором
	 * данных поступающих из считывателя.
	 * 
	 * @param password
	 * @param pcEpc
	 * @param memoryBank
	 * @param offset
	 * @param data
	 * @param timeOut
	 */
	static public void lockTag(String password, String pcEpc, int mask,
			int timeOut) {

		final byte[] fPassword = string2Bytes(password);
		final byte[] fPcEpc = string2Bytes(pcEpc);
		final int fMask = mask;

		// обработчик для повторной отправки команды в считыватель или отправки
		// сообщения о успешном выполнении
		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					// отправляем сообщение о успешном чтении данных
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
					m_handler.sendMessage(message);
				} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
					// отправляем сообщение о таймауте
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_TIMEOUT;
					m_handler.sendMessage(message);
				} else {
					// блокировка не удалась
					// отправляем повторно команду блокировки
					Lock(fPassword, fPcEpc.length, fPcEpc, fMask);
				}
				return true;
			}
		});

		readThread = new ParseThread(ParseThread.LOCK_TAG_COMMAND, timeOut);
		readThread.setResendCommandHandler(handler);
		readThread.start();

		// отправляем команду блокировки
		Lock(fPassword, fPcEpc.length, fPcEpc, fMask);

	}

	/**
	 * Запуск процесса записи данных в метку. Новый вариант, с правильным
	 * разбором данных поступающих из считывателя.
	 * 
	 * @param password
	 * @param pcEpc
	 * @param memoryBank
	 * @param offset
	 * @param data
	 * @param timeOut
	 */
	static public void writeTagData(String password, String pcEpc,
			int memoryBank, int offset, String data, int timeOut) {

		final byte[] fPassword = string2Bytes(password);
		final byte[] fPcEpc = string2Bytes(pcEpc);
		final byte fMemoryBank = (byte) memoryBank;
		final int fOffset = offset / 2;
		final byte[] fData = string2Bytes(data);

		// обработчик для повторной отправки команды в считыватель или отправки
		// сообщения о успешном выполнении
		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					// отправляем сообщение о успешном чтении данных
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
					m_handler.sendMessage(message);
				} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
					// отправляем сообщение о таймауте
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_TIMEOUT;
					m_handler.sendMessage(message);
				} else {
					// запись не удалась
					// отправляем повторно команду записи
					WriteTag(fPassword, fPcEpc.length, fPcEpc, fMemoryBank,
							fOffset, fData.length, fData);
				}
				return true;
			}
		});

		readThread = new ParseThread(ParseThread.WRITE_TAG_DATA_COMMAND,
				timeOut);
		readThread.setResendCommandHandler(handler);
		readThread.start();

		// отправляем команду записи
		WriteTag(fPassword, fPcEpc.length, fPcEpc, fMemoryBank, fOffset,
				fData.length, fData);

	}

	/**
	 * Запуск процесса чтения Id доступных меток. Новый вариант, с правильным
	 * разбором данных поступающих из считывателя.
	 * 
	 * @param timeOut
	 */
	static public void readTagId(int timeOut) {

		// обработчик для повторной отправки команды в считыватель или отправки
		// сообщения о успешном выполнении
		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				Log.d(TAG, "readTagId: msg.what=" + msg.what);
				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					// отправляем сообщение о успешном чтении данных
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
					message.obj = msg.obj;
					m_handler.sendMessage(message);
				} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_TIMEOUT;
					m_handler.sendMessage(message);
				} else {
					// чтение не удалось, отправляем повторно команду
					// чтения Id
					Inventory();
				}
				return true;
			}
		});

		readThread = new ParseThread(ParseThread.READ_TAG_ID_COMMAND, timeOut);
		readThread.setResendCommandHandler(handler);
		readThread.start();

		// отправляем команду чтения Id метки
		Inventory();

	}

	/**
	 * Запуск процесса чтения области памяти метки. Новый вариант, с правильным
	 * разбором данных поступающих из считывателя.
	 * 
	 * @param password
	 * @param pcEpc
	 * @param memoryBank
	 * @param offset
	 * @param count
	 */
	static public void readTagData(String password, String pcEpc,
			int memoryBank, int offset, int count, int timeOut) {

		final byte[] fPassword = string2Bytes(password);
		final byte[] fPcEpc = string2Bytes(pcEpc);
		final byte fMemoryBank = (byte) memoryBank;
		final int fOffset = offset / 2;
		final int fCount = count / 2;

		// обработчик для повторной отправки команды в считыватель или отправки
		// сообщения о успешном выполнении
		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
					// отправляем сообщение о успешном чтении данных
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_SUCCESS;
					message.obj = msg.obj;
					m_handler.sendMessage(message);
				} else if (msg.what == RfidDriverBase.RESULT_RFID_TIMEOUT) {
					Message message = new Message();
					message.what = RfidDriverBase.RESULT_RFID_TIMEOUT;
					m_handler.sendMessage(message);
				} else {
					// чтение данных не удалось, отправляем повторно команду
					// чтения данных
					ReadTag(fPassword, fPcEpc.length, fPcEpc, fMemoryBank,
							fOffset, fCount);
				}
				return true;
			}
		});

		readThread = new ParseThread(ParseThread.READ_TAG_DATA_COMMAND, timeOut);
		readThread.setResendCommandHandler(handler);
		readThread.start();

		// отправляем команду чтения памяти метки
		ReadTag(fPassword, fPcEpc.length, fPcEpc, fMemoryBank, fOffset, fCount);

	}

	/**
	 * Возвращает маску доступа к областям памяти метки
	 * 
	 * @param memoryBlock
	 * @param lockType
	 * @return
	 */
	static public int getLockPayload(int memoryBlock, int lockType) {

		int result = 0;
		int shiftBits;
		int tmpMask;

		switch (memoryBlock) {
		case 0:
			shiftBits = 8;
			break;
		case 1:
			shiftBits = 6;
			break;
		case 2:
			shiftBits = 4;
			break;
		case 3:
			shiftBits = 2;
			break;
		case 4:
			shiftBits = 0;
			break;
		default:
			return 0;
		}

		switch (lockType) {
		case UNLOCK:
		case LOCK:
			// 10 binary
			tmpMask = 2;
			break;
		case PERMANENT_UNLOCK:
		case PERMANENT_LOCK:
			// 11 binary
			tmpMask = 3;
			break;
		default:
			return 0;
		}

		result |= (tmpMask << (shiftBits + 10));
		result |= (lockType << shiftBits);

		return result;
	}

	/**
	 * Перевод строки шестнадцатиричных значений в массив byte
	 * 
	 * @param hexString
	 * @return
	 */
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

	/**
	 * Перевод символа в byte
	 * 
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * Перевод массив byte в строку шестнадцатиричных значений
	 * 
	 * @param b
	 * @param nS
	 * @param ncount
	 * @return
	 */
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

	/**
	 * Перевод массива byte[4] в int
	 * 
	 * @param b
	 * @return
	 */
	public static int byteToInt(byte[] b) {
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

	/**
	 * Перевод из массива byte в int
	 * 
	 * @param b
	 * @param nIndex
	 * @param ncount
	 * @return
	 */
	public static int byteToInt(byte[] b, int nIndex, int ncount) {
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

	/**
	 * Перевод int в массив byte[16]
	 * 
	 * @param content
	 * @param offset
	 * @return
	 */
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

	/**
	 * 
	 * @param string
	 * @return
	 */
	private static byte[] string2Bytes(String string) {
		int blen = string.length() / 2;
		byte[] data = new byte[blen];
		for (int i = 0; i < blen; i++) {
			String bStr = string.substring(2 * i, 2 * (i + 1));
			data[i] = (byte) Integer.parseInt(bStr, 16);
		}
		return data;
	}

	/**
	 * 
	 * @param string
	 * @param encoding
	 * @return
	 */
	private static String decodeString(String string, String encoding) {
		try {
			byte[] data = string2Bytes(string);
			return new String(data, encoding);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// далее идут native медоты из сопутствующей библиотеки

	/**
	 * Initialization device
	 * 
	 * @param strpath
	 */
	static public native void Init(String strpath);

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
	 * write data
	 * 
	 * @param jpout
	 *            store write data
	 * @param nStart
	 *            write data in the starting position of jpout
	 * @param nwrite
	 *            to write data length
	 * @return
	 */
	public static native int Write(byte[] jpout, int nStart, int nwrite);

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

	/**
	 * set to send Select commands
	 * 
	 * @param data
	 *            (0x01 is to cancel the Select instruction, 0x00 is the Select
	 *            instruction)
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
	static public native int ReadTag(byte[] password, int nUL, byte[] PCandEPC,
			byte membank, int nSA, int nDL);

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
	static public native int WriteTag(byte[] password, int nUL,
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

}
