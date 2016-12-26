/**
 * 
 */
package android.hardware.uhf.magic;

import java.util.Arrays;

//import ru.toir.mobile.rfid.RfidDriverBase;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author koputo
 * 
 */
public class ParseThread extends Thread {

	private static final String TAG = "ParseThread";

	private byte expectCommand;

	public static final byte COMMAND_ERROR = (byte) 0xFF;
	public static final byte READ_TAG_ID_COMMAND = (byte) 0x22;
	public static final byte READ_TAG_DATA_COMMAND = (byte) 0x39;
	public static final byte WRITE_TAG_DATA_COMMAND = (byte) 0x49;
	public static final byte LOCK_TAG_COMMAND = (byte) 0x82;
	public static final byte KILL_TAG_COMMAND = (byte) 0x65;

	// флаг успешности выполнения команды
	private boolean tagOperationSuccess;

	// обработчик повторной отправки команды в считыватель
	private Handler resendCommandHandler = null;

	/**
	 * Конструктор
	 * 
	 * @param command Команда ответ на которую мы должны разобрать.
	 * @param timeOut Время на выполнение разбора ответа.
	 */
	public ParseThread(byte command, int timeOut) {

		expectCommand = command;

		if (timeOut > 0) {
			Handler readTimerHandler = new Handler();
			Runnable readTimerRunnable = new Runnable() {

				@Override
				public void run() {
					if (!tagOperationSuccess) {
						// время вышло, вменяемого ответа не получили
						// останавливаем процесс чтения данных из считывателя
						interrupt();
						if (resendCommandHandler != null) {
							Message message = new Message();
							message.what = reader.RESULT_TIMEOUT;
							resendCommandHandler.sendMessage(message);
						}
					}
				}
			};

			readTimerHandler.postDelayed(readTimerRunnable, timeOut);
		}

	}

	@Override
	public void run() {
		int buffSize = 1024;
		byte[] buff = new byte[buffSize];
		Arrays.fill(buff, (byte) 0);
		int count;
		int buffIndex = 0;
		String readedString;
		byte[] data = new byte[1024];
		int dataIndex = 0;

		int parseIndex = 0;
		boolean packetStart = false;
		boolean typePacketExists = false;
		boolean commandExists = false;
		byte command = 0;
		boolean payloadLengthExists = false;
		int payloadLength = 0;
		byte[] payloadLenBuff = new byte[2];
		int payloadLenBuffIndex = 0;
		boolean packetEnd = false;

		int rc;

		while (true) {

			if (isInterrupted()) {
				Log.d(TAG, "Tag data read interrupted. expect cmd = " + String.format("%x", expectCommand));
				break;
			}

			count = reader.Read(buff, buffIndex, buffSize - buffIndex);

			if (count > 0) {

				readedString = reader.BytesToString(buff, buffIndex, count);
				Log.d(TAG, "прочитано: " + readedString);

				buffIndex += count;

				int i = parseIndex;
				while (i < buffIndex) {

					if (packetStart) {
						if (typePacketExists) {
							if (commandExists) {
								if (payloadLengthExists) {
									// ищем маркер конца пакета
									if (packetEnd) {
										Log.d(TAG, "packet parsed");
									} else {
										if (buff[parseIndex] == (byte) 0x7E
												&& dataIndex - 1 == payloadLength) {
											packetEnd = true;
											Log.d(TAG, "end of packet found");
											Log.d(TAG,
													"data = "
															+ reader.BytesToString(
																	data, 0,
																	payloadLength));

											if (command != expectCommand) {
												if (command != COMMAND_ERROR) {
													Log.e(TAG,
															"Разобран ответ на другую команду.");
												}
												// шлём сообщение о ошибке,
												// чтобы сигнализировать
												// драйверу что нужно послать
												// комманду снова
												Message message = new Message();
												message.what = reader.RESULT_READ_ERROR;
												resendCommandHandler.sendMessage(message);
											} else {
												tagOperationSuccess = true;
												// стопорим поток чтения и
												// разбора данных
												interrupt();

												Message message = new Message();

												// в зависимости от команды,
												// выполняем постобработку
												// полученных данных
												switch (command) {
                                                    case READ_TAG_ID_COMMAND:
                                                        Log.d(TAG,
                                                                "Id карты прочитан успешно!");
                                                        message.what = reader.RESULT_SUCCESS;
                                                        message.obj = reader
                                                                .BytesToString(
                                                                        data,
                                                                        1,
                                                                        payloadLength - 3);
                                                        break;
                                                    case READ_TAG_DATA_COMMAND:
                                                        Log.d(TAG,
                                                                "Данные карты прочитаны успешно!");
                                                        message.what = reader.RESULT_SUCCESS;
                                                        message.obj = reader
                                                                .BytesToString(
                                                                        data, 0,
                                                                        payloadLength);
                                                        break;
                                                    case WRITE_TAG_DATA_COMMAND:
                                                        rc = reader.byteToInt(data,
                                                                0, 1);
                                                        Log.d(TAG,
                                                                "код возврата после записи = "
                                                                        + rc);
                                                        if (rc == 0) {
                                                            Log.d(TAG,
                                                                    "Данные записаны успешно!");
                                                            message.what = reader.RESULT_SUCCESS;
                                                        } else {
                                                            Log.d(TAG,
                                                                    "Не удалось записать данные!");
                                                            message.what = reader.RESULT_WRITE_ERROR;
                                                        }
                                                        break;
                                                    case LOCK_TAG_COMMAND:
                                                        rc = reader.byteToInt(data,
                                                                0, 1);
                                                        Log.d(TAG,
                                                                "код возврата после блокировки = "
                                                                        + rc);
                                                        if (rc == 0) {
                                                            Log.d(TAG,
                                                                    "Блокировка выполненна успешно!");
                                                            message.what = reader.RESULT_SUCCESS;
                                                        } else {
                                                            Log.d(TAG,
                                                                    "Не удалось выполнить блокировку!");
                                                            message.what = reader.RESULT_WRITE_ERROR;
                                                        }
                                                        break;
                                                    case KILL_TAG_COMMAND:
                                                        rc = reader.byteToInt(data,
                                                                0, 1);
                                                        Log.d(TAG,
                                                                "код возврата после деактивации = "
                                                                        + rc);
                                                        if (rc == 0) {
                                                            Log.d(TAG,
                                                                    "Деактивация выполненна успешно!");
                                                            message.what = reader.RESULT_SUCCESS;
                                                        } else {
                                                            Log.d(TAG,
                                                                    "Не удалось выполнить деактивацию!");
                                                            message.what = reader.RESULT_WRITE_ERROR;
                                                        }
                                                        break;
												}

												resendCommandHandler.sendMessage(message);
											}

											// сбрасываем всё
											packetStart = false;
											typePacketExists = false;
											payloadLengthExists = false;
											payloadLength = 0;
											payloadLenBuffIndex = 0;
											commandExists = false;
											dataIndex = 0;
											packetEnd = false;

										} else {
											data[dataIndex] = buff[parseIndex];
											dataIndex++;
										}

										parseIndex++;
									}
								} else {
									payloadLenBuff[payloadLenBuffIndex] = buff[parseIndex];
									payloadLenBuffIndex++;
									parseIndex++;
									if (payloadLenBuffIndex >= 2) {
										payloadLength = reader.byteToInt(
												payloadLenBuff, 0, 2);
										payloadLengthExists = true;
										Log.d(TAG, "payload len = "
												+ payloadLength);
									}
								}
							} else {
								commandExists = true;
								command = buff[parseIndex];
								parseIndex++;
							}
						} else {
							if (buff[parseIndex] == (byte) 0x01
									|| buff[parseIndex] == (byte) 0x02) {
								typePacketExists = true;
								Log.d(TAG, "type packet found");
							} else {
								packetStart = false;
							}
							parseIndex++;
						}
					} else {
						if (buff[parseIndex] == (byte) 0xBB) {
							packetStart = true;
							Log.d(TAG, "packet start");
						}
						parseIndex++;
					}

					i++;
				}

				if (buffIndex >= buffSize) {
					buffIndex = 0;
					parseIndex = 0;
				}
			}
		}

        Log.d(TAG, "exit readThread, expected cmd = " + String.format("%x", expectCommand));
	}

	public Handler getResendCommandHandler() {
		return resendCommandHandler;
	}

	public void setResendCommandHandler(Handler resendCommandHandler) {
		this.resendCommandHandler = resendCommandHandler;
	}

}
