package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.serial.SerialPortManager;
import android.util.Log;

public class RFIDDriver15693API {
	private static final byte[] CONFIGURATION_READER_MODE = "c05060103\r\n".getBytes();
	private static final byte[] CONFIGURATION_PROTOCOL_MODE = "c05060C1001\r\n".getBytes();
	private static final byte[] SET_CHECK_CODE = "c05060C04C1\r\n".getBytes();
	private static final byte[] FIND = "f260100\r\n".getBytes();
	private static final String FIND_WITH_AFI = "f3601";
	private static final String KEEP_SILENT = "f2002";
	private static final String SELECT = "f2025";
	private static final String READ_ONE = "f1020";
	private static final String WRITE_ONE = "f1021";
	private static final String LOCK_PIECE = "f1022";
	private static final String READ_MORE = "f1023";
	private static final String WRITE_MORE = "f1024";
	private static final byte[] RESET = "f1026\r\n".getBytes();
	private static final String WRITE_AFI = "f1027";
	private static final byte[] LOCK_AFI = "f1028\r\n".getBytes();
	private static final String WRITE_DSFID = "f1029";
	private static final byte[] LOCK_DSFID = "f102A\r\n".getBytes();
	private static final byte[] GET_SYSTEM_INFO = "f102B\r\n".getBytes();
	private static final String GET_PIECE_STATUS = "f102C";	
	private static final String ENTER = "\r\n";
	private static final String NO_RESPONSE = "No response from card.";
	private byte[] mBuffer = new byte[1024];

	/**
	 * Configuration reader mode
	 * @return
	 */
	public boolean configurationReaderMode() {
		int length = receive(CONFIGURATION_READER_MODE, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		if (length > 0
				&& receiveData.startsWith("RF carrier on! ISO/IEC15693.")) {
			Log.i("whw", "configurationReaderMode   str=" + receiveData);
			return true;
		}
		return false;
	}

	/**
	 * According to the card type card protocol configuration mode (subcarrier rate modulation)
	 * @return
	 */
	public boolean configurationProtocolMode() {
		int length = receive(CONFIGURATION_PROTOCOL_MODE, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i("whw", "configurationProtocolMode   str=" + receiveData);
		if (length > 0 && receiveData.startsWith("0x01")) {
			return true;
		}
		return false;
	}

	/**
	 * Set the reader check code
	 * @return
	 */
	public boolean setCheckCode() {
		int length = receive(SET_CHECK_CODE, mBuffer);
		String receiveData = new String(mBuffer, 0, length);
		Log.i("whw", "setCheckCode   str=" + receiveData);
		if (length > 0 && receiveData.startsWith("0xc1")) {
			return true;
		}
		return false;
	}

	/**
	 * DSFID(data storage format identifier):1byte   UID:8bytes
	 * @return 
	 */
	public byte[] findCard() {
		int length = receive(FIND, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "findCard   str=" + receiveData);
		if (length > 0) {
			if(!receiveData.startsWith(NO_RESPONSE)){
				if(length==22){
					return DataUtils.hexStringTobyte(receiveData.substring(2));
				}
			}
		}
		return null;
	}
	
	/**
	 * DSFID:1byte   UID:8bytes
	 * @param AFI(application family identifier)
	 * @return
	 */
	public byte[] findCardWithAFI(byte AFI) {
		String command = FIND_WITH_AFI+DataUtils.byte2Hexstr(AFI)+"00"+ENTER;
		int length = receive(command.getBytes(), mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "findCardWithAFI   str=" + receiveData);
		if (length > 0) {
			if(!receiveData.startsWith(NO_RESPONSE)){
				return DataUtils.hexStringTobyte(receiveData.substring(2));
			}
		}
		return null;
	}
	
	/**
	 * When receiving a silent command, VICC will enter the silence state and not return a response.
	 * @param UID
	 */
	public void keepSilent(byte[] UID){
		byte[] command = (KEEP_SILENT + DataUtils.toHexString(UID) + "\r\n").getBytes();
		SerialPortManager.getInstance().write(command);
	}

	public boolean selectCard(byte[] UID) {
		int length = receive((SELECT + DataUtils.toHexString(UID) + "\r\n").getBytes(), mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		if (length > 0 && "00".equals(receiveData)) {
			Log.i("whw", "selectCard   str=" + receiveData);
			return true;
		}
		return false;
	}

	public boolean writeOne(int position, byte[] data) {
		byte[] command = (WRITE_ONE + DataUtils.byte2Hexstr((byte) position)
				+ DataUtils.toHexString(data) + ENTER).getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "writeOne   str=" + receiveData);
		if (length > 0 && "00".equals(receiveData)) {
			return true;
		}
		return false;
	}

	public boolean writeMore(int startPosition, int positionCount, byte[] data) {
		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < data.length; i++) {
			sb.append(DataUtils.toHexString(data));
//		}
		byte[] command = (WRITE_MORE
				+ DataUtils.byte2Hexstr((byte) startPosition)
				+ DataUtils.byte2Hexstr((byte) --positionCount) + sb.toString() + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "writeMore   str=" + receiveData);
		if (length > 0 && "00".equals(receiveData)) {
			return true;
		}
		return false;
	}

	public byte[] readOne(int position) {
		byte[] command = (READ_ONE + DataUtils.byte2Hexstr((byte) position) + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "readOne   str=" + receiveData);
		if (length > 0 && receiveData.startsWith("00")) {
			String data = receiveData.substring(2);
			return DataUtils.hexStringTobyte(data);
		}
		return null;
	}

	public byte[] readMore(int startPosition, int positionCount) {
		byte[] command = (READ_MORE
				+ DataUtils.byte2Hexstr((byte) startPosition)
				+ DataUtils.byte2Hexstr((byte) --positionCount) + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "readMore   str=" + receiveData);
		if (length > 0 && receiveData.startsWith("00")) {
			Log.i("whw", "readMore   str=" + receiveData);
			String data = receiveData.substring(2);
			return DataUtils.hexStringTobyte(data);
		}
		return null;
	}

	public boolean lockPiece(int position) {
		byte[] command = (LOCK_PIECE + DataUtils.byte2Hexstr((byte) position) + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "lockPiece   str=" + receiveData);
		if (length > 0 && "00".equals(receiveData)) {
			return true;
		}
		return false;
	}
	
	/**
	 * When receiving a reset Readiness Command, VICC will return to the ready state.
	 * @return
	 */
	public boolean reset(){
		int length = receive(RESET, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		if (length > 0 && "00".equals(receiveData)) {
			Log.i("whw", "reset   str=" + receiveData);
			return true;
		}
		return false;
	}
	
	public boolean writeAFI(byte AFI){
		byte[] command = (WRITE_AFI + DataUtils.byte2Hexstr(AFI) + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "writeAFI   str=" + receiveData);
		if (length > 0 && "00".equals(receiveData)) {
			return true;
		}
		return false;
	}
	
	public boolean lockAFI(){
		int length = receive(LOCK_AFI, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "lockAFI   str=" + receiveData);
		if (length > 0 && "00".equals(receiveData)) {
			return true;
		}
		return false;
	}
	
	public boolean writeDSFID(byte DSFID){
		byte[] command = (WRITE_DSFID + DataUtils.byte2Hexstr(DSFID) + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		if (length > 0 && "00".equals(receiveData)) {
			Log.i("whw", "writeDSFID   str=" + receiveData);
			return true;
		}
		return false;
	}
	
	public boolean lockDSFID(){
		int length = receive(LOCK_DSFID, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		if (length > 0 && "00".equals(receiveData)) {
			Log.i("whw", "lockDSFID   str=" + receiveData);
			return true;
		}
		return false;
	}
	
	/**
	 * InfoTag:1byte UID:8bytes  DSFID:1byte AFI:1byte   piece count:1byte  piece capacity:1byte  IC reference:1byte
	 * Note:piece count:n+1, piece capacity:n+1.
	 * @return
	 */
	public byte[] getSystemInfo(){
		int length = receive(GET_SYSTEM_INFO, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		Log.i("whw", "getSystemInfo   str=" + receiveData);
		if (length > 0 && receiveData.startsWith("00")) {
			if(!receiveData.startsWith(NO_RESPONSE)){
				return DataUtils.hexStringTobyte(receiveData.substring(2));
			}
		}
		return null;
	}
	
	/**
	 * Access to multiple piece security status.
	 * @param startPosition
	 * @param positionCount
	 * @return Each byte to represent each piece state.  0:no lock    1:lock
	 */
	public byte[] getPieceStatus(int startPosition, int positionCount) {
		byte[] command = (GET_PIECE_STATUS
				+ DataUtils.byte2Hexstr((byte) startPosition)
				+ DataUtils.byte2Hexstr((byte) --positionCount) + ENTER)
				.getBytes();
		int length = receive(command, mBuffer);
		String receiveData = new String(mBuffer, 0, length).trim();
		if (length > 0 && receiveData.startsWith("00")) {
			Log.i("whw", "getPieceStatus   str=" + receiveData);
			String data = receiveData.substring(2);
			return DataUtils.hexStringTobyte(data);
		}
		return null;
	}

	private void sendCommand(byte[] command) {
		SerialPortManager.getInstance().write(command);
	}
	
	private int receive(byte[] command, byte[] buffer) {
		int length = -1;
		if (!SerialPortManager.switchRFID) {
			SerialPortManager.getInstance().switchStatus();
		}
		SerialPortManager.getInstance().write(command);
		Log.i("whw", "command hex=" + new String(command)+"  switchRFID"+SerialPortManager.switchRFID);
		length = SerialPortManager.getInstance().read(buffer, 150, 5);
		return length;
	}
}
