package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.RFID;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * @author Dmitriy Logachov
 *         <p>
 *         Драйвер считывателя RFID который "считывает" содержимое меток из
 *         текстового файла.
 *         </p>
 */
public class RFIDDriverText implements RFIDDriver {

	private View view;
	private String TAG = "RFIDDriverText";
	private Context mContext;
	private Handler mHandler;

	/**
	 * <p>
	 * Инициализируем драйвер
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean init(byte type) {

		Button ok = (Button) view.findViewById(R.id.rfid_dialog_text_button_OK);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "pressed OK");

				Spinner spinner = (Spinner) v.getRootView().findViewById(
						R.id.rfid_dialog_text_spinner_lables);
				Message message = new Message();
				message.arg1 = RFID.RESULT_RFID_SUCCESS;
				Bundle bundle = new Bundle();
				bundle.putString(RFID.RESULT_RFID_TAG_ID,
						(String) spinner.getSelectedItem());
				message.setData(bundle);
				mHandler.sendMessage(message);
			}
		});

		Button cancel = (Button) view
				.findViewById(R.id.rfid_dialog_text_button_CANCEL);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "pressed CANCEL");

				Message message = new Message();
				message.arg1 = RFID.RESULT_RFID_CANCEL;
				mHandler.sendMessage(message);
			}
		});

		Spinner spinner = (Spinner) view
				.findViewById(R.id.rfid_dialog_text_spinner_lables);
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
				mContext, R.array.list,
				android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner.setSelection(Adapter.NO_SELECTION, false);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				Log.d(TAG, (String) parent.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		return true;
	}

	@Override
	public void read(byte type) {
	}

	/**
	 * <p>
	 * Записываем в метку
	 * </p>
	 * 
	 * @param outBuffer
	 * @return
	 */
	@Override
	public boolean write(byte[] outBuffer) {
		return false;
	}

	/**
	 * <p>
	 * Завершаем работу драйвера
	 * </p>
	 */
	@Override
	public void close() {
	}

	/**
	 * <p>
	 * Устанавливаем тип операции
	 * </p>
	 * 
	 * @return boolean
	 */
	@Override
	public boolean SetOperationType(byte type) {

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.toir.mobile.rfid.driver.RFIDDriver#getView(android.view.LayoutInflater
	 * , android.view.ViewGroup)
	 */
	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		mContext = inflater.getContext();
		view = inflater.inflate(R.layout.rfid_dialog_text, viewGroup);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.toir.mobile.rfid.driver.RFIDDriver#setHandler(android.os.Handler)
	 */
	@Override
	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.toir.mobile.rfid.driver.RFIDDriver#setActivity(android.app.DialogFragment
	 * )
	 */
	@Override
	public void setDialogFragment(DialogFragment fragment) {

	}

}
