package ru.toir.mobile.serial;

//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
import ru.toir.mobile.utils.DataUtils;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.os.SystemClock;
//import android.text.TextUtils;
import android.util.Log;

public class M1CardAPI {
	public static final int KEY_A = 1;
	public static final int KEY_B = 2;
	private static final String NO_RESPONSE = "No respon";
	private static final String DATA_PREFIX = "c050605";
	private static final String FIND_CARD_ORDER = "01";
	private static final String PASSWORD_SEND_ORDER = "02";
	private static final String PASSWORD_VALIDATE_ORDER = "03";
	//private static final String READ_DATA_ORDER = "04";
	private static final String WRITE_DATA_ORDER = "05";
	private static final String ENTER = "\r\n";
	//private static final String TURN_OFF = "c050602\r\n";
	private static final String FIND_CARD = DATA_PREFIX + FIND_CARD_ORDER + ENTER;
	private static final String SEND_PASSWORD = DATA_PREFIX + PASSWORD_SEND_ORDER + "ffffffffffffffffffffffff" + ENTER;
	// private static final String FIND_SUCCESS = "c05060501" + ENTER + "0x00,";
	private static final String FIND_SUCCESS = "0x00,";
	private static final String WRITE_SUCCESS = " Write Success!" + ENTER;
	
	public byte[] buffer = new byte[100];
	private int receive(byte[] command, byte[] buffer) {
		int length = -1;
		if (!SerialPortManager.switchRFID) {
			SerialPortManager.getInstance().switchStatus();
		}
		sendCommand(command);
		length = SerialPortManager.getInstance().read(buffer, 300, 5);
		return length;
	}

	private void sendCommand(byte[] command) {
		SerialPortManager.getInstance().write(command);
	}

	private static final String DEFAULT_PASSWORD = "ffffffffffff";
	private String getCompletePassword(int keyType, String passwordHexStr) {
		StringBuffer passwordBuffer = new StringBuffer();
		passwordBuffer.append(passwordHexStr);
		if (passwordHexStr != null && passwordHexStr.length() < 12) {
			int length = 12 - passwordHexStr.length();
			for (int i = 0; i < length; i++) {
				passwordBuffer.append('0');
			}
		}
		passwordHexStr = passwordBuffer.toString();
		String completePasswordHexStr = "";
		switch (keyType) {
		case KEY_A:
			completePasswordHexStr = passwordHexStr + DEFAULT_PASSWORD;
			break;
		case KEY_B:
			completePasswordHexStr = DEFAULT_PASSWORD + passwordHexStr;
			break;

		default:
			break;
		}
		return completePasswordHexStr;
	}

	private String getKeyTypeStr(int keyType) {
		String keyTypeStr = null;
		switch (keyType) {
		case KEY_A:
			keyTypeStr = "60";
			break;
		case KEY_B:
			keyTypeStr = "61";
			break;
		default:
			keyTypeStr = "60";
			break;
		}
		return keyTypeStr;
	}

	private String getZoneId(int position) {
		return DataUtils.byte2Hexstr((byte) position);
	}

	/**
	 *  Read the M1 card number
	 * @return
	 */
	public Result readCardNum() {
		Log.i("whw", "!!!!!!!!!!!!readCard");
		Result result = new Result();
		byte[] command = FIND_CARD.getBytes();
		int length = receive(command, buffer);
		if (length == 0) {
			result.confirmationCode = Result.TIME_OUT;
			return result;
		}
		String msg = "";
		msg = new String(buffer, 0, length);
		Log.i("whw", "msg hex=" + msg);
		turnOff();
		if (msg.startsWith(FIND_SUCCESS)) {
			result.confirmationCode = Result.SUCCESS;
			result.num = msg.substring(FIND_SUCCESS.length());
		} else {
			result.confirmationCode = Result.FIND_FAIL;
		}
		return result;
	}

	/**
	 * Verify password
	 * @param position
	 *            block number
	 * @param keyType
	 *            Password type
	 * @param password
	 * @return
	 */
	public boolean validatePassword(int position, int keyType, byte[] password) {
		Log.i("whw", "!!!!!!!!!!!!!!keyType=" + keyType);
		byte[] command1 = null;
		if (password == null) {
			command1 = SEND_PASSWORD.getBytes();
		} else {
			String passwordHexStr = DataUtils.toHexString(password);
			String completePassword = getCompletePassword(keyType,
					passwordHexStr);
			command1 = (DATA_PREFIX + PASSWORD_SEND_ORDER + completePassword + ENTER)
					.getBytes();
		}

		int tempLength = receive(command1, buffer);
		String verifyStr = new String(buffer, 0, tempLength);
		Log.i("whw", "validatePassword verifyStr=" + verifyStr);
		byte[] command2 = (DATA_PREFIX + PASSWORD_VALIDATE_ORDER
				+ getKeyTypeStr(keyType) + getZoneId(position) + ENTER)
				.getBytes();

		int length = receive(command2, buffer);
		String msg = new String(buffer, 0, length);
		Log.i("whw", "validatePassword msg=" + msg);
		String prefix = "0x00,\r\n";
		if (msg.startsWith(prefix)) {
			return true;
		}
		return false;
	}

	/**
	 * Reads the specified number stored data, length of
	 * 16 bytes
	 * @param position
	 *            block number
	 * @return
	 */
	public byte[][] read(int startPosition, int num) {
		byte[] command = { 'c', '0', '5', '0', '6', '0', '5', '0', '4', '0',
				'0', '\r', '\n' };
		byte[][] pieceDatas = new byte[num][];
		for (int i = 0; i < num; i++) {
			char[] c = getZoneId(startPosition + i).toCharArray();
			command[9] = (byte) c[0];
			command[10] = (byte) c[1];
			int readTime = 0;
			int length = 0;
			String data = "";
			while (readTime < 3) {
				readTime++;
				length = receive(command, buffer);
				data = new String(buffer, 0, length);
				if (data != null && data.startsWith(NO_RESPONSE)) {
					continue;
				} else {
					break;
				}
			}
			Log.i("whw", "read data=" + data+"   readTime="+readTime);
			String[] split = data.split(";");
			String msg = "";
			if (split.length == 2) {
				int index = split[1].indexOf("\r\n");
				if (index != -1) {
					msg = split[1].substring(0, index);
				}

				Log.i("whw",
						"split msg=" + msg + "  msg length=" + msg.length());
			}
			pieceDatas[i] = DataUtils.hexStringTobyte(msg);
		}
		return pieceDatas;
	}

	/**
	 * Write data to the specified block, length is 16 bytes
	 * argument should be data[i].length == num
	 * @param data
	 * @param startPosition
	 * @param num  the number of block
	 * @return
	 */
	public boolean write(byte[][] data, int startPosition, int num) {
		if(data.length != num){
			return false;
		}
		for (int i = 0; i < num; i++) {
			String hexStr = DataUtils.toHexString(data[i]);
			byte[] command = (DATA_PREFIX + WRITE_DATA_ORDER + getZoneId(startPosition+i)
					+ hexStr + ENTER).getBytes();
			Log.i("whw", "***write hexStr=" + hexStr);

			int length = receive(command, buffer);
			boolean isWrite = false;
			if (length > 0) {
				String writeResult = new String(buffer, 0, length);
				Log.i("whw", "write result=" + writeResult);
				isWrite = M1CardAPI.WRITE_SUCCESS.equals(writeResult);
			}
			if(!isWrite){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Write data to the specified block, length is 16 bytes
	 * @param data
	 * @param position
	 * @return
	 */
	public boolean write(byte[] data, int position) {
		String hexStr = DataUtils.toHexString(data);
		byte[] command = (DATA_PREFIX + WRITE_DATA_ORDER + getZoneId(position)
				+ hexStr + ENTER).getBytes();
		Log.i("whw", "***write hexStr=" + hexStr);

		int length = receive(command, buffer);
		if (length > 0) {
			String writeResult = new String(buffer, 0, length);
			Log.i("whw", "write result=" + writeResult);
			return M1CardAPI.WRITE_SUCCESS.equals(writeResult);
		}
		return false;
	}

	// не используется
	public String turnOff() {
		// byte[] command = TURN_OFF.getBytes();
		// int length = receive(command, buffer);
		// String str = "";
		// if (length > 0) {
		// str = new String(buffer, 0, length);
		// }
		// return str;
		return "";
	}

	public static class Result {
		// успешно
		public static final int SUCCESS = 1;
		// карта не найдена
		public static final int FIND_FAIL = 2;
		// ошибка проверки
		public static final int VALIDATE_FAIL = 3;
		// ошибка чтения
		public static final int READ_FAIL = 4;
		// ошибка записи
		public static final int WRITE_FAIL = 5;
		// таймаут
		public static final int TIME_OUT = 6;
		// other exception
		public static final int OTHER_EXCEPTION = 7;

		// код подтверждения
		public int confirmationCode;
		// Result: when the code is 1, then determine whether to have the result
		public Object resultInfo;
		// номер карты
		public String num;
	}

}
