package ru.toir.mobile.rfid.driver;

import ru.toir.mobile.R;
import ru.toir.mobile.rfid.IRfidDriver;
import ru.toir.mobile.rfid.RfidDriverBase;
import android.app.DialogFragment;
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
public class RfidDriverText extends RfidDriverBase implements IRfidDriver {

	private String TAG = "RfidDriverText";

	public RfidDriverText(DialogFragment dialog, Handler handler) {
		super(dialog, handler);
	}

	@Override
	public boolean init() {
		/*
		 * Для этого драйвера ни какой специальной инициализации не нужно.
		 */

		return true;
	}

	@Override
	public void readTagId() {
		/*
		 * В данном драйвере реального считывания не происходит.
		 */
	}

	@Override
	public void writeTagData(String password, int memoryBank, int address,
			byte[] data) {
		/*
		 * В данном драйвере реальной записи не происходит.
		 */
	}

	@Override
	public void close() {
	}

	@Override
	public View getView(LayoutInflater inflater, ViewGroup viewGroup) {

		View view = inflater.inflate(R.layout.rfid_dialog_text, viewGroup);

		Button ok = (Button) view.findViewById(R.id.rfid_dialog_text_button_OK);
		ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Log.d(TAG, "pressed OK");

				Spinner spinner = (Spinner) v.getRootView().findViewById(
						R.id.rfid_dialog_text_spinner_lables);
				Message message = new Message();
				message.arg1 = RfidDriverBase.RESULT_RFID_SUCCESS;
				Bundle bundle = new Bundle();
				bundle.putString(RfidDriverBase.RESULT_RFID_TAG_ID,
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
				message.arg1 = RfidDriverBase.RESULT_RFID_CANCEL;
				mHandler.sendMessage(message);
			}
		});

		Spinner spinner = (Spinner) view
				.findViewById(R.id.rfid_dialog_text_spinner_lables);
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
				inflater.getContext(), R.array.list,
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

		return view;
	}

	@Override
	public void readTagData(String password, int memoryBank, int address,
			int count) {
		/*
		 * В данном драйвере реального считывания не происходит.
		 */
	}

	@Override
	public void readTagData(String password, String tagId, int memoryBank,
			int address, int count) {
		/*
		 * В данном драйвере реального считывания не происходит.
		 */
	}

	@Override
	public void writeTagData(String password, String tagId, int memoryBank,
			int address, byte[] data) {
		/*
		 * В данном драйвере реальной записи не происходит.
		 */
	}

}
