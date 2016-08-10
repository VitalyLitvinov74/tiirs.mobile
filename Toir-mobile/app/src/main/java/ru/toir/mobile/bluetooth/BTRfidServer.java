/**
 * 
 */
package ru.toir.mobile.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import ru.toir.mobile.rfid.RfidDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 *         Класс сервера работающего с блютус.
 */
public class BTRfidServer {
	private static final String TAG = "BTRfidServer";

	private Handler mHandler;
	private BluetoothAdapter mAdapter;

	public static final UUID BT_SERVICE_RECORD_UUID = UUID
			.fromString("E8627152-8F74-460B-B31E-A879194BB431");
	public static final String BT_SERVICE_RECORD_NAME = "ToirBTServer";

	public AcceptThread mAcceptThread;
	public CommunicationThread mCommunicationThread;

	public static final String SERVER_STATE_ACTION = "ru.toir.mobile.btserver.state";
	public static final String SERVER_STATE_PARAM = "state";

	public static final int SERVER_STATE_STOPED = 1;
	public static final int SERVER_STATE_WAITING_CONNECTION = 2;
	public static final int SERVER_STATE_CONNECTED = 3;
	public static final int SERVER_STATE_DISCONNECTED = 4;
	public static final int SERVER_STATE_READ_COMMAND = 5;

	// текущее состояние сервера
	private int mState;

	/**
	 * Конструктор.
	 */
	public BTRfidServer(Context context, Handler handler) {
		mHandler = handler;
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mState = SERVER_STATE_STOPED;
	}

	/**
	 * Запускаем ожидание входящего соединения от клиента.
	 */
	public void startServer() {
		Log.d(TAG, "startServer()");
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}

		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread();
			mAcceptThread.start();

			// сообщаем активити о том что перешли в режим ожидания входящего
			// сообщения
			setState(SERVER_STATE_WAITING_CONNECTION);
			mHandler.obtainMessage(SERVER_STATE_WAITING_CONNECTION)
					.sendToTarget();
		}
	}

	/**
	 * Останавливаем ожидание входящего соединения от клиента.
	 */
	public void stopServer() {
		Log.d(TAG, "stopServer()");
		if (mAcceptThread != null) {
			mAcceptThread.cancel();
			mAcceptThread = null;
		}

		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}

		// сообщаем активити о том что остановили сервер
		setState(SERVER_STATE_STOPED);
		mHandler.obtainMessage(SERVER_STATE_STOPED).sendToTarget();
	}

	/**
	 * Запускаем поток взаимодействующий с клиентом.
	 * 
	 * @param socket
	 *            Сокет через который работаем с клиентом.
	 */
	private void startCommunication(BluetoothSocket socket) {
		Log.d(TAG, "startCommunication()");
		if (mCommunicationThread != null) {
			mCommunicationThread.cancel();
			mCommunicationThread = null;
		}

		if (mCommunicationThread == null) {
			mCommunicationThread = new CommunicationThread(socket);
			mCommunicationThread.start();
		}
	}

	/**
	 * Отправка данных клиенту.
	 * 
	 * @param buffer
	 *            Массив данных отправляемых клиенту.
	 */
	public void write(byte[] buffer) {
		Log.d(TAG, "write()");
		mCommunicationThread.write(buffer);
	}

	/**
	 * Установка текущего состояния сервера.
	 * 
	 * @param state
	 */
	public synchronized void setState(int state) {
		mState = state;
	}

	/**
	 * Текущее состояние сервера.
	 * 
	 * @return
	 */
	public synchronized int getState() {
		return mState;
	}

	/**
	 * Отправка id метки в ответ на запрос её чтения.
	 * 
	 * @param tagId
	 *            Id метки отправляемый клиенту.
	 * @param result
	 *            Результат выполнения команды.
	 */
	public void answerReadId(String tagId, int result) {
		// маркер начала, тип пакета, команда, размер полезной нагрузки,
		// маркер конца
		int serviceDataLength = 1 + 1 + 1 + 2 + 1;
		int payloadLength = 0;
		byte[] tagIdBuffer;
		if (tagId != null) {
			tagIdBuffer = tagId.getBytes();
		} else {
			tagIdBuffer = new byte[0];
		}

		// 1 байт на результат выполнения команды
		payloadLength += 1;

		// 2 байта на длину id метки + длина id метки
		payloadLength += 2 + tagIdBuffer.length;

		byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
		int commandBufferIndex = 0;

		// маркер начала пакета
		commandBuffer[commandBufferIndex++] = (byte) 0xBB;

		// тип пакета
		commandBuffer[commandBufferIndex++] = 0x01;

		// команда
		commandBuffer[commandBufferIndex++] = RfidDialog.READER_COMMAND_READ_ID;

		// размер полезной нагрузки
		commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
		commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

		// результат выполнения команды
		commandBuffer[commandBufferIndex++] = (byte) (result);

		// размер данных id метки
		commandBuffer[commandBufferIndex++] = (byte) ((tagIdBuffer.length >> 8) & 0xFF);
		commandBuffer[commandBufferIndex++] = (byte) (tagIdBuffer.length & 0xFF);

		// id метки
		for (int i = 0; i < tagIdBuffer.length; i++) {
			commandBuffer[commandBufferIndex++] = tagIdBuffer[i];
		}

		// маркер конца
		commandBuffer[commandBufferIndex++] = 0x7E;
		mCommunicationThread.write(commandBuffer);
	}

	/**
	 * Отправка клиенту прочитанных из метки данных.
	 * 
	 * @param command
	 *            Команда которую мы выполняли.
	 * @param data
	 *            Данные из метки отправляемые клиенту.
	 * @param result
	 *            Результат выполнения команды.
	 */
	public void answerReadData(int command, String data, int result) {
		// маркер начала, тип пакета, команда, размер полезной нагрузки,
		// маркер конца
		int serviceDataLength = 1 + 1 + 1 + 2 + 1;
		int payloadLength = 0;
		byte[] dataBuffer;
		if (data != null) {
			dataBuffer = data.getBytes();
		} else {
			dataBuffer = new byte[0];
		}

		// 1 байт на результат выполнения команды
		payloadLength += 1;

		// размер самих данных
		payloadLength += dataBuffer.length;

		byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
		int commandBufferIndex = 0;

		// маркер начала пакета
		commandBuffer[commandBufferIndex++] = (byte) 0xBB;

		// тип пакета
		commandBuffer[commandBufferIndex++] = 0x01;

		// команда
		commandBuffer[commandBufferIndex++] = (byte) command;

		// размер полезной нагрузки
		commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
		commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

		// результат выполнения команды
		commandBuffer[commandBufferIndex++] = (byte) result;

		// данные
		for (int i = 0; i < dataBuffer.length; i++) {
			commandBuffer[commandBufferIndex++] = dataBuffer[i];
		}

		// маркер конца
		commandBuffer[commandBufferIndex++] = 0x7E;
		mCommunicationThread.write(commandBuffer);
	}

	/**
	 * Отправка клиенту результата попытки записи в метку.
	 * 
	 * @param command
	 *            Команда которую мы выполняли.
	 * @param result
	 *            Результат выполнения команды.
	 */
	public void answerWriteData(int command, int result) {
		// маркер начала, тип пакета, команда, размер полезной нагрузки,
		// маркер конца
		int serviceDataLength = 1 + 1 + 1 + 2 + 1;
		int payloadLength;

		// 1 байт на код результата выполнения команды
		payloadLength = 1;

		byte[] commandBuffer = new byte[serviceDataLength + payloadLength];
		int commandBufferIndex = 0;

		// маркер начала пакета
		commandBuffer[commandBufferIndex++] = (byte) 0xBB;

		// тип пакета
		commandBuffer[commandBufferIndex++] = 0x01;

		// команда
		commandBuffer[commandBufferIndex++] = (byte) command;

		// размер полезной нагрузки
		commandBuffer[commandBufferIndex++] = (byte) ((payloadLength >> 8) & 0xFF);
		commandBuffer[commandBufferIndex++] = (byte) (payloadLength & 0xFF);

		// результат выполнения
		commandBuffer[commandBufferIndex++] = (byte) result;

		// маркер конца
		commandBuffer[commandBufferIndex++] = 0x7E;
		mCommunicationThread.write(commandBuffer);
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 *         Класс отвечающий за ожидание входящего соединения от клиента.
	 * 
	 */
	private class AcceptThread extends Thread {
		private static final String TAG = "AcceptThread";
		private BluetoothServerSocket mServerSocket;

		/**
		 * Конструктор.
		 */
		public AcceptThread() {
			BluetoothServerSocket socket = null;

			// получаем серверный сокет
			try {
				socket = mAdapter.listenUsingRfcommWithServiceRecord(
						BT_SERVICE_RECORD_NAME, BT_SERVICE_RECORD_UUID);
				Log.d(TAG, "Получили серверный сокет...");
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}

			mServerSocket = socket;
		}

		@Override
		public void run() {
			Log.d(TAG, "run()");
			mAdapter.cancelDiscovery();

			// запускаем ожидание соединения от клиента
			while (true) {
				try {
					BluetoothSocket socket = mServerSocket.accept();
					Log.d(TAG, "Входящее соединение получено...");
					Thread.sleep(1000);

					// запускаем поток сервера, ожидающего команды
					startCommunication(socket);
					break;
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());
					break;
				} catch (InterruptedException e) {
					Log.e(TAG, e.getLocalizedMessage());
					break;
				} catch (NullPointerException e) {
					Log.e(TAG, "mServerSocket = null");
					break;
				}
			}

			Log.d(TAG, "Завершился поток ожидания входящего соединения...");

			// сообщаем активити о том что отключили режим ожидания
			// входящего соединения
			setState(SERVER_STATE_STOPED);
			mHandler.obtainMessage(SERVER_STATE_STOPED).sendToTarget();
		}

		public void cancel() {
			Log.d(TAG, "cancel()");
			try {
				mServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			} catch (NullPointerException e) {
			}
		}
	}

	/**
	 * @author Dmitriy Logachov
	 * 
	 *         Класс отвечающий за работу через установленное соединение.
	 */
	private class CommunicationThread extends Thread {
		private static final String TAG = "CommunicationThread";

		private BluetoothSocket mSocket;
		private InputStream mInputStream;
		private OutputStream mOutputStream;

		public CommunicationThread(BluetoothSocket socket) {
			mSocket = socket;
			InputStream tmpInputStream = null;
			OutputStream tmpOutputStream = null;

			try {
				tmpInputStream = socket.getInputStream();
				tmpOutputStream = socket.getOutputStream();
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}

			mInputStream = tmpInputStream;
			mOutputStream = tmpOutputStream;
		}

		public void write(byte[] command) {
			Log.d(TAG, "write()");
			try {
				mOutputStream.write(command);
				Log.d(TAG, "Успешно отправили данные клиенту...");
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			}
		}

		@Override
		public void run() {
			Log.d(TAG, "run()");
			int count = 0;
			int bufferLength = 1024;
			byte buffer[] = new byte[bufferLength];
			int dataLength = 1024;
			byte data[] = new byte[dataLength];
			int dataIndex = 0;
			int bufferIndex = 0;
			int parseIndex = 0;

			boolean packetStart = false;
			boolean typePacketExists = false;
			boolean commandExists = false;
			int command = 0;
			boolean payloadLengthExists = false;
			int payloadLength = 0;
			byte[] payloadLenBuff = new byte[2];
			int payloadLenBuffIndex = 0;
			boolean packetEnd = false;

			while (true) {
				try {
					count = mInputStream.read(buffer, bufferIndex, bufferLength
							- bufferIndex);
					if (count > 0) {
						// разбираем данные полученные от клиента
						bufferIndex += count;
						while (parseIndex < bufferIndex) {
							if (packetStart) {
								if (typePacketExists) {
									if (commandExists) {
										if (payloadLengthExists) {
											if (packetEnd) {
												// пакет разобран
												// мы сюда не должны попадать
											} else {
												byte tmpData = buffer[parseIndex++];
												if (tmpData == (byte) 0x7E
														&& dataIndex == payloadLength) {
													// добрались до конца пакета
													packetEnd = true;

													Bundle bundle = parseCommand(
															command, data,
															payloadLength);
													mHandler.obtainMessage(
															SERVER_STATE_READ_COMMAND,
															command, -1, bundle)
															.sendToTarget();

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
													if (dataIndex >= payloadLength) {
														// не нашли маркера
														// конца пакета после
														// полезной нагрузки
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
														data[dataIndex++] = tmpData;
													}
												}
											}
										} else {
											payloadLenBuff[payloadLenBuffIndex++] = buffer[parseIndex++];
											if (payloadLenBuffIndex >= 2) {
												payloadLength = ((int) (0xFF & payloadLenBuff[0]) << 8)
														+ (int) (0xFF & payloadLenBuff[1]);
												payloadLengthExists = true;
											}
										}
									} else {
										command = (int) (buffer[parseIndex++] & 0xFF);
										int[] commands = new int[] {
												RfidDialog.READER_COMMAND_READ_ID,
												RfidDialog.READER_COMMAND_READ_DATA,
												RfidDialog.READER_COMMAND_READ_DATA_ID,
												RfidDialog.READER_COMMAND_WRITE_DATA,
												RfidDialog.READER_COMMAND_WRITE_DATA_ID };
										if (Arrays.binarySearch(commands,
												command) > -1) {
											commandExists = true;
										} else {
											command = 0;
											packetStart = false;
											typePacketExists = false;
										}
									}
								} else {
									if (buffer[parseIndex++] == (byte) 0x00) {
										typePacketExists = true;
									} else {
										packetStart = false;
									}
								}
							} else {
								if (buffer[parseIndex++] == (byte) 0xBB) {
									packetStart = true;
								}
							}
						}
					}
					if (bufferIndex >= bufferLength) {
						bufferIndex = 0;
						parseIndex = 0;
					}
				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage());

					// сообщаем что соединение с клиентом потеряно
					setState(SERVER_STATE_STOPED);
					mHandler.obtainMessage(SERVER_STATE_DISCONNECTED)
							.sendToTarget();
					break;
				} catch (IndexOutOfBoundsException e) {
					// случилось невероятное
				}
			}

			Log.d(TAG, "Завершился поток взаимодействия с клиентом...");
		}

		private Bundle parseCommand(int command, byte[] data, int dataLength) {
			switch (command) {
			case RfidDialog.READER_COMMAND_READ_ID:
				return null;
			case RfidDialog.READER_COMMAND_READ_DATA:
				return parseCommandReadData(data, dataLength);
			case RfidDialog.READER_COMMAND_READ_DATA_ID:
				return parseCommandReadDataId(data, dataLength);
			case RfidDialog.READER_COMMAND_WRITE_DATA:
				return parseCommandWriteData(data, dataLength);
			case RfidDialog.READER_COMMAND_WRITE_DATA_ID:
				return parseCommandWriteDataId(data, dataLength);
			default:
				return null;
			}
		}

		private Bundle parseCommandReadData(byte[] data, int dataLength) {
			Bundle bundle = new Bundle();
			int index = 0;

			// длина пароля
			int passwordLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сам пароль в виде строки
			String password = new String(Arrays.copyOfRange(data, index,
					passwordLength + index));
			index += passwordLength;
			bundle.putString("password", password);

			// банк памяти
			int memoryBank = reader.byteToInt(data, index++, 1);
			bundle.putInt("memoryBank", memoryBank);

			// смещение в банке памяти
			int address = reader.byteToInt(data, index, 2);
			index += 2;
			bundle.putInt("address", address);

			// количество данных для чтения
			int count = reader.byteToInt(data, index, 2);
			index += 2;
			bundle.putInt("count", count);
			return bundle;
		}

		private Bundle parseCommandReadDataId(byte[] data, int dataLength) {
			Bundle bundle = new Bundle();
			int index = 0;

			// длина пароля
			int passwordLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сам пароль в виде строки
			String password = new String(Arrays.copyOfRange(data, index,
					passwordLength + index));
			index += passwordLength;
			bundle.putString("password", password);

			// длина id метки
			int tagIdLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сама id метки в виде строки
			String tagId = new String(Arrays.copyOfRange(data, index,
					tagIdLength + index));
			index += tagIdLength;
			bundle.putString("tagId", tagId);

			// банк памяти
			int memoryBank = data[index++];
			bundle.putInt("memoryBank", memoryBank);

			// смещение в банке памяти
			int address = reader.byteToInt(data, index, 2);
			index += 2;
			bundle.putInt("address", address);

			// количество данных для чтения
			int count = reader.byteToInt(data, index, 2);
			index += 2;
			bundle.putInt("count", count);
			return bundle;
		}

		private Bundle parseCommandWriteData(byte[] data, int dataLength) {
			Bundle bundle = new Bundle();
			int index = 0;

			// длина пароля
			int passwordLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сам пароль в виде строки
			String password = new String(Arrays.copyOfRange(data, index,
					passwordLength + index));
			index += passwordLength;
			bundle.putString("password", password);

			// банк памяти
			int memoryBank = reader.byteToInt(data, index++, 1);
			bundle.putInt("memoryBank", memoryBank);

			// смещение в банке памяти
			int address = reader.byteToInt(data, index, 2);
			index += 2;
			bundle.putInt("address", address);

			// размер данных
			int tmpDataLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сами данные
			String tmpData = new String(Arrays.copyOfRange(data, index,
					tmpDataLength + index));
			bundle.putString("data", tmpData);
			return bundle;
		}

		private Bundle parseCommandWriteDataId(byte[] data, int dataLength) {
			Bundle bundle = new Bundle();
			int index = 0;

			// длина пароля
			int passwordLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сам пароль в виде строки
			String password = new String(Arrays.copyOfRange(data, index,
					passwordLength + index));
			index += passwordLength;
			bundle.putString("password", password);

			// длина id метки
			int tagIdLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сама id метки в виде строки
			String tagId = new String(Arrays.copyOfRange(data, index,
					tagIdLength + index));
			index += tagIdLength;
			bundle.putString("tagId", tagId);

			// банк памяти
			int memoryBank = reader.byteToInt(data, index++, 1);
			bundle.putInt("memoryBank", memoryBank);

			// смещение в банке памяти
			int address = reader.byteToInt(data, index, 2);
			index += 2;
			bundle.putInt("address", address);

			// размер данных
			int tmpDataLength = reader.byteToInt(data, index, 2);
			index += 2;

			// сами данные
			String tmpData = new String(Arrays.copyOfRange(data, index,
					tmpDataLength + index));
			bundle.putString("data", tmpData);
			return bundle;
		}

		public void cancel() {
			Log.d(TAG, "cancel()");
			try {
				mSocket.close();
				Thread.sleep(2000);
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
			} catch (NullPointerException e) {
			} catch (InterruptedException e) {
			}
		}
	}
}
