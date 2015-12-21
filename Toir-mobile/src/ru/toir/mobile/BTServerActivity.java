package ru.toir.mobile;

import ru.toir.mobile.bluetooth.BTRfidServer;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class BTServerActivity extends Activity {

	private static final String TAG = "BTServerActivity";
	private static final int BT_ENABLE_REQUEST_CODE = 666;

	// состояния сервера
	private TextView serverStatusTextView;

	// объект сервера блютус
	private BTRfidServer mBtRfidServer;

	// обработчик сообщений от блютус сервера
	private Handler mHandler;

	// диалог для работы с rfid драйвером
	private RfidDialog rfidDialog;

	// адаптер
	private BluetoothAdapter mBluetoothAdapter;

	public BTServerActivity() {

		mHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				switch (msg.what) {
				case BTRfidServer.SERVER_STATE_STOPED:
					Log.e(TAG, "SERVER_STATE_STOPED");
					serverStatusTextView.setText("Остановлен...");
					if (!mBluetoothAdapter.isEnabled()) {
						finish();
					}

					break;
				case BTRfidServer.SERVER_STATE_WAITING_CONNECTION:
					Log.e(TAG, "SERVER_STATE_WAITING_CONNECTION");
					serverStatusTextView
							.setText("Ожидание входящего соединения от клиента...");
					break;
				case BTRfidServer.SERVER_STATE_CONNECTED:
					Log.e(TAG, "SERVER_STATE_CONNECTED");
					serverStatusTextView.setText("Соединение установленно...");
					break;
				case BTRfidServer.SERVER_STATE_DISCONNECTED:
					Log.e(TAG, "SERVER_STATE_DISCONNECTED");
					serverStatusTextView.setText("Клиент отключился...");
					mBtRfidServer.startServer();
					break;
				case BTRfidServer.SERVER_STATE_READ_COMMAND:
					Log.d(TAG, "Получили сообщение от клиента!!!");
					Bundle bundle = (Bundle) msg.obj;

					// разбираемся какую команду нужно выполнить
					switch (msg.arg1) {
					case RfidDialog.READER_COMMAND_READ_ID: {
						Log.d(TAG, "Чтение id метки...");
						Handler handler = new Handler(new Handler.Callback() {

							@Override
							public boolean handleMessage(Message msg) {
								Log.d(TAG, "Получили сообщение из драйвера.");
								String tagId = null;
								if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
									tagId = (String) msg.obj;
									Log.d(TAG, tagId);
								}

								// отправляем полученное значение клиенту
								mBtRfidServer.answerReadId(tagId, msg.what);

								// закрываем диалог
								rfidDialog.dismiss();
								return true;
							}
						});

						rfidDialog = new RfidDialog(getApplicationContext(),
								handler);
						rfidDialog.readTagId();
						rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
						break;
					}
					case RfidDialog.READER_COMMAND_READ_DATA: {
						Log.d(TAG, "Чтение данных случайной метки..");

						// получаем данные для выполнения операции
						String password = bundle.getString("password");
						int memoryBank = bundle.getInt("memoryBank");
						int address = bundle.getInt("address");
						int count = bundle.getInt("count");

						Handler handler = new Handler(new Handler.Callback() {

							@Override
							public boolean handleMessage(Message msg) {
								Log.d(TAG, "Получили сообщение из драйвера.");
								String data = null;
								if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
									data = (String) msg.obj;
									Log.d(TAG, data);
								}

								// отправляем полученное значение клиенту
								mBtRfidServer.answerReadData(
										RfidDialog.READER_COMMAND_READ_DATA,
										data, msg.what);

								// закрываем диалог
								rfidDialog.dismiss();
								return true;
							}
						});

						rfidDialog = new RfidDialog(getApplicationContext(),
								handler);
						rfidDialog.readTagData(password, memoryBank, address,
								count);
						rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
						break;
					}
					case RfidDialog.READER_COMMAND_READ_DATA_ID: {
						Log.d(TAG, "Чтение данных конкретной метки...");

						// получаем данные для выполнения операции
						String password = bundle.getString("password");
						String tagId = bundle.getString("tagId");
						int memoryBank = bundle.getInt("memoryBank");
						int address = bundle.getInt("address");
						int count = bundle.getInt("count");

						Handler handler = new Handler(new Handler.Callback() {

							@Override
							public boolean handleMessage(Message msg) {
								Log.d(TAG, "Получили сообщение из драйвера.");
								String data = null;
								if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
									data = (String) msg.obj;
									Log.d(TAG, data);
								}

								// отправляем полученное значение клиенту
								mBtRfidServer.answerReadData(
										RfidDialog.READER_COMMAND_READ_DATA_ID,
										data, msg.what);

								// закрываем диалог
								rfidDialog.dismiss();
								return true;
							}
						});

						rfidDialog = new RfidDialog(getApplicationContext(),
								handler);
						rfidDialog.readTagData(password, tagId, memoryBank,
								address, count);
						rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
						break;
					}
					case RfidDialog.READER_COMMAND_WRITE_DATA: {
						Log.d(TAG, "Запись данных в случайную метку...");

						// получаем данные для выполнения операции
						String password = bundle.getString("password");
						int memoryBank = bundle.getInt("memoryBank");
						int address = bundle.getInt("address");
						String data = bundle.getString("data");

						Handler handler = new Handler(new Handler.Callback() {

							@Override
							public boolean handleMessage(Message msg) {
								Log.d(TAG, "Получили сообщение из драйвера.");

								// отправляем полученное значение клиенту
								mBtRfidServer.answerWriteData(
										RfidDialog.READER_COMMAND_WRITE_DATA,
										msg.what);

								// закрываем диалог
								rfidDialog.dismiss();
								return true;
							}
						});

						rfidDialog = new RfidDialog(getApplicationContext(),
								handler);
						rfidDialog.writeTagData(password, memoryBank, address,
								data);
						rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
						break;
					}
					case RfidDialog.READER_COMMAND_WRITE_DATA_ID: {
						Log.d(TAG, "Запись данных в конкретную метку...");

						// получаем данные для выполнения операции
						String password = bundle.getString("password");
						String tagId = bundle.getString("tagId");
						int memoryBank = bundle.getInt("memoryBank");
						int address = bundle.getInt("address");
						String data = bundle.getString("data");

						Handler handler = new Handler(new Handler.Callback() {

							@Override
							public boolean handleMessage(Message msg) {
								Log.d(TAG, "Получили сообщение из драйвера.");

								// отправляем полученное значение клиенту
								mBtRfidServer.answerWriteData(
										RfidDialog.READER_COMMAND_WRITE_DATA,
										msg.what);

								// закрываем диалог
								rfidDialog.dismiss();
								return true;
							}
						});

						rfidDialog = new RfidDialog(getApplicationContext(),
								handler);
						rfidDialog.writeTagData(password, tagId, memoryBank,
								address, data);
						rfidDialog.show(getFragmentManager(), RfidDialog.TAG);
						break;
					}
					default:
						Log.d(TAG, "Неизвестная команда от клиента...");
						break;
					}
					break;
				default:
					break;
				}
				return true;
			}
		});

		mBtRfidServer = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_btserver);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(),
					"Оборудование не поддерживает блютус.", Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		serverStatusTextView = (TextView) findViewById(R.id.bluetoothServerStatus);

		// создаём объект сервера блютус
		mBtRfidServer = new BTRfidServer(getApplicationContext(), mHandler);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");
		if (!mBluetoothAdapter.isEnabled()) {
			Intent intentStartBT = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(intentStartBT, BT_ENABLE_REQUEST_CODE);
		} else {
			mBtRfidServer.startServer();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		mBtRfidServer.startServer();
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop()");
		mBtRfidServer.stopServer();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BT_ENABLE_REQUEST_CODE) {
			if (resultCode != Activity.RESULT_OK) {
				Log.d(TAG, "отказались включать блютус");

				// гасим активити сервера
				finish();
			}
			return;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
