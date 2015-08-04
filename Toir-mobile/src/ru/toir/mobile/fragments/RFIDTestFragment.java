package ru.toir.mobile.fragments;

import com.example.magicuhf.EPC;
import com.example.magicuhf.Lock;
import com.example.magicuhf.ReadWrite;
import com.example.magicuhf.Setting;

import ru.toir.mobile.R;
import android.content.Intent;
import android.hardware.uhf.magic.reader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
//import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.TextView;
//import android.app.Activity;

public class RFIDTestFragment extends Fragment {
	Button m_setting,m_btnEPC,m_btnread,m_btnLock;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_magic_main, container, false);
		initView(rootView);
		super.onCreate(savedInstanceState);
		//Init();
		return rootView;		
	}
	private void initView(View view) {
		m_setting=(Button)view.findViewById(R.id.Setting);
		m_btnEPC=(Button)view.findViewById(R.id.ReadEPC);
		m_btnread=(Button)view.findViewById(R.id.ReadWrite);
		m_btnLock=(Button)view.findViewById(R.id.Lockkill);
		m_setting.setOnClickListener(new OnClickListener() {			   
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	    		Intent intentTo = new Intent();  
 	    		intentTo.setClass(RFIDTestFragment.this.getActivity(), Setting.class);
 	    		startActivity(intentTo);  			
			
			}
		});
		m_btnEPC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	    		Intent intentTo = new Intent();  
 	    		intentTo.setClass(RFIDTestFragment.this.getActivity(), EPC.class);
 	    		startActivity(intentTo);  					
			}
		});	
		m_btnread.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	    		Intent intentTo = new Intent();  
 	    		intentTo.setClass(RFIDTestFragment.this.getActivity(), ReadWrite.class);
 	    		startActivity(intentTo);  					
			}
		});			
		m_btnLock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	    		Intent intentTo = new Intent();  
 	    		intentTo.setClass(RFIDTestFragment.this.getActivity(), Lock.class);
 	    		startActivity(intentTo);  					
			}
		});						
	}
		
	//@Override
	//protected void onDestroy() {
		// TODO Auto-generated method stub
	//	super.onDestroy();
	//	android.hardware.uhf.magic.reader.Close();
	//}

	void Init()
    {  
		Thread thread = new Thread(new Runnable() {
			   public void run() {
			        android.hardware.uhf.magic.reader.init("/dev/ttyMT1");
			        android.hardware.uhf.magic.reader.Open("/dev/ttyMT1");
			        Log.e("7777777777","111111111111111111111111111111111111");
			        if(reader.SetTransmissionPower(1950)!=0x11)
			        {
				        if(reader.SetTransmissionPower(1950)!=0x11)
				        {
				        	reader.SetTransmissionPower(1950);
				        }
			        }
			   }     
			 });        
			 thread.start();	
    }

/*
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
	}*/
    //		application = (TOiRApplication) getActivity().getApplication();		
    //		reader = new AsyncRFID15693Card(application.getHandlerThread().getLooper());		
    //		ToastUtil.showToast(getActivity(), R.string.write_15693_success);
}
