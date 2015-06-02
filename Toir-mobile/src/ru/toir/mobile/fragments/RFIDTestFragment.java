package ru.toir.mobile.fragments;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRApplication;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card.OnFindCardListener;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card.OnInitListener;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card.OnReadListener;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card.OnReadMoreListener;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card.OnWriteListener;
import ru.toir.mobile.rfid.asynctask.AsyncRFID15693Card.OnWriteMoreListener;
import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.utils.ToastUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
//import android.app.Activity;

public class RFIDTestFragment extends Fragment {
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.rfid_15693_activity, container, false);
		initView(rootView);
		//ToastUtil.showToast(getActivity(), "3");
		// инициализируем функции асинхронного обмена 
		//initData();
		ToastUtil.showToast(getActivity(), "4");
		return rootView;
	}

	int positionCount = 0;
	private Button init;
	private Button find;
	private TextView dsfid;
	private TextView uid;
	private Button read;
	private EditText readPosition;
	private TextView readInfo;
	private Button write;
	private EditText writePosition;
	private EditText writeText;
	
	private Button readMore;
	private EditText readMorePosition;
	private TextView readMoreInfo;
	
	private Button writeMore;
	private EditText writeMorePosition;
	private EditText writeMoreText;

	private AsyncRFID15693Card reader;	
	private TOiRApplication application;

	private void initView(View view) {
		init = (Button) view.findViewById(R.id.init);
		find = (Button) view.findViewById(R.id.find);
		dsfid = (TextView) view.findViewById(R.id.dsfid);
		uid = (TextView) view.findViewById(R.id.uid);
		read = (Button) view.findViewById(R.id.read_15693);
		readPosition = (EditText) view.findViewById(R.id.read_position_15693);
		readInfo = (TextView) view.findViewById(R.id.read_info_15693);
		write = (Button) view.findViewById(R.id.write_15693);
		writePosition = (EditText) view.findViewById(R.id.write_position_15693);
		writeText = (EditText) view.findViewById(R.id.write_text_15693);
			
		readMore = (Button) view.findViewById(R.id.read_more_15693);
		readMorePosition = (EditText) view.findViewById(R.id.read_more_position_15693);
		readMoreInfo = (TextView) view.findViewById(R.id.read_more_info_15693);
		
		writeMore = (Button) view.findViewById(R.id.write_more_15693);
		writeMorePosition = (EditText) view.findViewById(R.id.write_more_position_15693);
		writeMoreText = (EditText) view.findViewById(R.id.write_more_text_15693);
		init.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {                 
				mOnClickListener (v);
			}  
			});  		
		find.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {                 
				mOnClickListener (v);
			}  
			});  		
		read.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {                 
				mOnClickListener (v);
			}  
			});  		
		write.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {                 
				mOnClickListener (v);
			}  
			});  		
		writeMore.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {                 
				mOnClickListener (v);
			}  
			});  		
		readMore.setOnClickListener(new View.OnClickListener() {  
			public void onClick(View v) {                 
				mOnClickListener (v);
			}  
			});  		
	}

	/* Функция инициализирует асинхронный обмен с RFID устройством A370 */
	private void initData() {
		application = (TOiRApplication) getActivity().getApplication();		
		reader = new AsyncRFID15693Card(application.getHandlerThread().getLooper());		

		reader.setOnInitListener(new OnInitListener() {			
			@Override
			public void initSuccess() {
				ToastUtil.showToast(getActivity(), R.string.init_success);
			}			
			@Override
			public void initFail() {
				ToastUtil.showToast(getActivity(), R.string.init_success);
			}
		});
		
		reader.setOnFindCardListener(new OnFindCardListener() {			
			@Override
			public void findSuccess(byte[] data) {
				//dsfid.setText(DataUtils.byte2Hexstr(data[0]));
				ToastUtil.showToast(getActivity(), DataUtils.byte2Hexstr(data[0]));
				byte[] uidData = new byte[8];
				System.arraycopy(data, 1, uidData, 0, uidData.length);
				//uid.setText(DataUtils.toHexString(uidData));
				ToastUtil.showToast(getActivity(), DataUtils.toHexString(uidData));
			}
			
			@Override
			public void findFail() {
				ToastUtil.showToast(getActivity(), R.string.init_fail);
			}
		});
		
		reader.setOnReadListener(new OnReadListener() {			
			@Override
			public void readSuccess(byte[] data) {
				//readInfo.setText(new String(data));
				ToastUtil.showToast(getActivity(), new String(data));
			}
			
			@Override
			public void readFail() {
				//ToastUtil.showToast(RFID15693Activity.this, R.string.read_15693_fail);
				ToastUtil.showToast(getActivity(), R.string.read_fail);
			}
		});
		
		reader.setOnReadMoreListener(new OnReadMoreListener() {
			
			@Override
			public void readMoreSuccess(byte[] data) {
				//readMoreInfo.setText(new String(data));
				ToastUtil.showToast(getActivity(), new String(data));
			}
			
			@Override
			public void readMoreFail() {
				ToastUtil.showToast(getActivity(), R.string.read_fail);
			}
		});
		
		reader.setOnWriteListener(new OnWriteListener() {
			
			@Override
			public void writeSuccess() {
				ToastUtil.showToast(getActivity(), R.string.write_15693_success);
			}
			
			@Override
			public void writeFail() {
				ToastUtil.showToast(getActivity(), R.string.write_15693_fail);
			}
		});
		
		reader.setOnWriteMoreListener(new OnWriteMoreListener() {
			
			@Override
			public void writeMoreSuccess() {
				ToastUtil.showToast(getActivity(), R.string.write_15693_success);
			}
			
			@Override
			public void writeMoreFail() {
				ToastUtil.showToast(getActivity(), R.string.write_15693_fail);
			}
		});
	}
/* обработчик событий нажатий клавиш активити */
	private void mOnClickListener(View view) {	    	
		int id2 = view.getId();
		if (id2 == R.id.init) {
			reader.init();
		} else if (id2 == R.id.find) {
			reader.findCard();
		} else if (id2 == R.id.read_15693) {
			String str1 = readPosition.getEditableText().toString();
			if(TextUtils.isEmpty(str1)){
				ToastUtil.showToast(getActivity(), "");
				return;
			}
			int position1 = Integer.parseInt(str1);
			reader.read(position1);
		} else if (id2 == R.id.write_15693) {
			String str2 = writePosition.getEditableText().toString();
			String str3 = writeText.getEditableText().toString();
			if(TextUtils.isEmpty(str2)||TextUtils.isEmpty(str3)){
				ToastUtil.showToast(getActivity(), "");
				return;
			}
			int position2 = Integer.parseInt(str2);
			byte[] data = new byte[4];
			byte[] temp = str3.getBytes();
			if(temp.length>=4){
				System.arraycopy(temp, 0, data, 0, 4);
			}else if(temp.length<4){
				System.arraycopy(temp, 0, data, 0, temp.length);
			}
			reader.write(position2, data);
		} else if (id2 == R.id.write_more_15693) {
			String str4 = writeMorePosition.getEditableText().toString();
			String str5 = writeMoreText.getEditableText().toString();
			if(TextUtils.isEmpty(str4)||TextUtils.isEmpty(str5)){
				ToastUtil.showToast(getActivity(), "");
				return;
			}
			int position3 = Integer.parseInt(str4);
			byte[] moreData = str5.getBytes();
			positionCount = moreData.length % 4 == 0 ? moreData.length / 4
					: moreData.length / 4 + 1;
			reader.writeMore(position3, moreData);
			Log.i("whw", "positionCount="+positionCount+"     moreData.length="+moreData.length);
		} else if (id2 == R.id.read_more_15693) {
			String str10 = readMorePosition.getEditableText().toString();
			if(TextUtils.isEmpty(str10)){
				ToastUtil.showToast(getActivity(), "");
				return;
			}
			int position10 = Integer.parseInt(str10);
			reader.readMore(position10,positionCount);
		} else {
		}
	};
}
