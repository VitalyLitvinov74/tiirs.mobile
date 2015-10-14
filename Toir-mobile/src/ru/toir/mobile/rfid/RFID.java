/**
 * 
 */
package ru.toir.mobile.rfid;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Класс работы с RFID
 *         </p>
 */
// TODO избавиться от этой не нужной прокладки
public class RFID {

	public static final int RESULT_RFID_SUCCESS = 0;
	public static final int RESULT_RFID_READ_ERROR = 1;
	public static final int RESULT_RFID_INIT_ERROR = 2;
	public static final int RESULT_RFID_CLASS_NOT_FOUND = 3;
	public static final int RESULT_RFID_WRITE_ERROR = 4;
	public static final int RESULT_RFID_CANCEL = 5;

	public static final String RESULT_RFID_TAG_ID = "tagId";


}
