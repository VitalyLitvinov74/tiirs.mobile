package ru.toir.mobile.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import ru.toir.mobile.rfid.EquipmentTagStructure;
import ru.toir.mobile.rfid.TagRecordStructure;
import android.text.TextUtils;


public class DataUtils {

	/**
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringTobyte(String hex) {
		if(TextUtils.isEmpty(hex)){
			return null;
		}
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		//String temp = "";
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
			// removed temp variable (10.06.2015)
			//temp += result[i] + ",";
		}
		// uiHandler.obtainMessage(206, hex + "=read=" + new String(result))
		// .sendToTarget();
		return result;
	}

	public static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	/**
	 * convert to HEX
	 * 
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) {
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) {
			return "0" + s;
		} else {
			return s;
		}
	}

	/**
	 * function converts HEX to string
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * function convert string to HEX
	 */
	public static String str2Hexstr(String str) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	public static String byte2Hexstr(byte b) {
		String temp = Integer.toHexString(0xFF & b);
		if (temp.length() < 2) {
			temp = "0" + temp;
		}
		// was default locale (10.06.2015)
		temp = temp.toUpperCase(Locale.ENGLISH);
		return temp;
	}

	public static String str2Hexstr(String str, int size) {
		byte[] byteStr = str.getBytes();
		byte[] temp = new byte[size];
		System.arraycopy(byteStr, 0, temp, 0, byteStr.length);
		temp[size - 1] = (byte) byteStr.length;
		String hexStr = toHexString(temp);
		return hexStr;
	}

	/**
	 * internal function of RFID library 
	 * @param str
	 * @return
	 */
	public static String[] hexStr2StrArray(String str) {
		int len = 32;
		int size = str.length() % len == 0 ? str.length() / len : str.length()
				/ len + 1;
		String[] strs = new String[size];
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				String temp = str.substring(i * len);
				for (int j = 0; j < len - temp.length(); j++) {
					temp = temp + "0";
				}
				strs[i] = temp;
			} else {
				strs[i] = str.substring(i * len, (i + 1) * len);
			}
		}
		return strs;
	}

	/**
	 * gzip array of bytes
	 * @param hexstr
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] data) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		return out.toByteArray();
	}

	/**
	 * unzip function for array
	 * @param hexstr
	 * @return
	 * @throws IOException
	 */
	public static byte[] uncompress(byte[] data) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	public static byte[] short2byte(short s) {
		byte[] size = new byte[2];
		size[0] = (byte) (s >>> 8);
		short temp = (short) (s << 8);
		size[1] = (byte) (temp >>> 8);
		// size[0] = (byte) ((s >> 8) & 0xff);
		// size[1] = (byte) (s & 0x00ff);
		return size;
	}

	public static short[] hexStr2short(String hexStr) {
		byte[] data = hexStringTobyte(hexStr);
		short[] size = new short[4];
		for (int i = 0; i < size.length; i++) {
			size[i] = getShort(data[i * 2], data[i * 2 + 1]);
		}
		return size;
	}

	public static short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}
	
	public static String getDate(long time, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String date = sdf.format(time);
		
		return date;
	}

	public static String StringToUUID(String temp) {
		temp = temp.toLowerCase(Locale.ENGLISH);
		if (temp.length() == 32)
			temp=temp.substring(0, 8) + "-" + temp.substring(8, 12) + "-" + temp.substring(12, 16) + "-" + temp.substring(16, 20) + "-" + temp.substring(20, 32);
		return temp;
	}
	
	public static byte[] PackToSend(EquipmentTagStructure equipmenttag, ArrayList<TagRecordStructure> tagrecords) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		byte out_buffer[] = new byte[256];
		byte temp_buffer[] = new byte[32];
		String temp;
		int cur=0;
		temp = equipmenttag.get_equipment_uuid().replace("-", "");
		temp_buffer = hexStringToByteArray(temp);
		outputStream.write(temp_buffer);		
		temp = equipmenttag.get_status();
		temp_buffer = hexStringToByteArray(temp);
		outputStream.write(temp_buffer);
		temp = equipmenttag.get_last();
		temp_buffer = hexStringToByteArray(temp);
		outputStream.write(temp_buffer);
		for (cur=0; cur<tagrecords.size(); cur++)
			{
			 temp_buffer = longToBytes(tagrecords.get(0).operation_date);
			 outputStream.write(temp_buffer);
			 temp_buffer = shortToBytes((short)tagrecords.get(0).operation_length);
			 outputStream.write(temp_buffer);
			 temp = tagrecords.get(0).operation_type;
			 temp_buffer = hexStringToByteArray(temp);
			 outputStream.write(temp_buffer);
			 temp = tagrecords.get(0).operation_result;
			 temp_buffer = hexStringToByteArray(temp);
			 outputStream.write(temp_buffer);			 
			 temp = tagrecords.get(0).user;
			 temp_buffer = hexStringToByteArray(temp);
			 outputStream.write(temp_buffer);
			}
		for (cur=outputStream.size(); cur<64; cur++)
			 outputStream.write((byte)0);
		out_buffer = outputStream.toByteArray( );
		return out_buffer;
	}
	
	public static byte[] hexStringToByteArray(String s) {		
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static byte[] longToBytes(long x) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; ++i) {
		  b[i] = (byte) (x >> (8 - i - 1 << 3));
		}
	    return b;
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}

	public static byte[] shortToBytes(short x) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte)(x & 0xff);
		bytes[1] = (byte)((x >> 8) & 0xff);
	    return bytes;
	}
}
