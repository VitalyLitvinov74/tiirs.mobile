package ru.toir.mobile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.*;
import ru.toir.mobile.db.tables.*;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import ru.toir.mobile.rfid.EquipmentTagStructure;
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.TagRecordStructure;
import ru.toir.mobile.rfid.driver.RFIDDriver;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EquipmentInfoActivity extends Activity {
		private String equipment_uuid;
		private Spinner Spinner_operation;
		private ListView lv;
		private ArrayAdapter<String> spinner_operation_adapter;
		private ArrayList<String> list = new ArrayList<String>();

		private String driverClassName;
		private Class<?> driverClass;
		private RFIDDriver driver;

		TagRecordStructure tagrecord = new TagRecordStructure();
		TagRecordStructure tagrecord2 = new TagRecordStructure();
		private ArrayList<TagRecordStructure> tagrecords = new ArrayList<TagRecordStructure>();
		EquipmentTagStructure equipmenttag = new EquipmentTagStructure();
		
	/*	
	    android:id="@+id/equipment_image"
	    android:id="@+id/equipment_listView_main"
	*/
		
		private TextView tv_equipment_id;
		private TextView tv_equipment_name;
		private TextView tv_equipment_type;
		private TextView tv_equipment_position;
		//private TextView tv_equipment_date;	
		private TextView tv_equipment_tasks;	
		private TextView tv_equipment_critical;
		private ImageView tv_equipment_image;
		private TextView tv_equipment_status;
		//private TextView tv_equipment_text_date;
		//private TextView tv_equipment_text_tasks;
		private TextView tv_equipment_task_date;
		private TextView tv_equipment_documentation;
		private Button read_rfid_button;

		private	RFID rfid;
		
		/* (non-Javadoc)
		 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {		
			super.onCreate(savedInstanceState);
			Bundle b = getIntent().getExtras();
	        equipment_uuid = b.getString("equipment_uuid"); 
	        setContentView(R.layout.equipment_layout);
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	       
			tv_equipment_id = (TextView) findViewById(R.id.equipment_text_name);
			tv_equipment_name = (TextView) findViewById(R.id.equipment_text_name);
			tv_equipment_type = (TextView) findViewById(R.id.equipment_text_type);		
			tv_equipment_position = (TextView) findViewById(R.id.equipment_position);
			//tv_equipment_date = (TextView) findViewById(R.id.equipment_start_date);
			tv_equipment_status = (TextView) findViewById(R.id.equipment_status);
			tv_equipment_critical = (TextView) findViewById(R.id.equipment_critical);	
			tv_equipment_task_date = (TextView) findViewById(R.id.equipment_text_date);
			tv_equipment_tasks = (TextView) findViewById(R.id.equipment_text_tasks);	
			tv_equipment_image = (ImageView) findViewById(R.id.equipment_image);
			tv_equipment_documentation = (TextView) findViewById(R.id.equipment_text_documentation);
		 	lv = (ListView) findViewById(R.id.equipment_listView_main);		 			 
			FillListViewOperations();
			// получаем текущий драйвер считывателя
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			driverClassName = sp.getString(getString(R.string.RFIDDriver), "RFIDDriverNull");

			// пытаемся получить класс драйвера
			try {
				driverClass = Class.forName("ru.toir.mobile.rfid.driver." + driverClassName);
			}catch(ClassNotFoundException e){
				setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
				finish();
			}
			
			// пытаемся создать объект драйвера
			try{
				driver = (RFIDDriver)driverClass.newInstance();
			}catch(InstantiationException e){
				setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
				e.printStackTrace();
				finish();
			}catch(IllegalAccessException e){
				setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
				e.printStackTrace();
				finish();
			}

			rfid = new RFID(driver);
    		rfid.setActivity(this);

			read_rfid_button = (Button) findViewById(R.id.button_read);		
    		// инициализируем драйвер
    		if (rfid.init((byte)1)) {
    			read_rfid_button.setOnClickListener(
		            new View.OnClickListener() {
		                @Override
		                public void onClick(View v) {		            		
		            			// запускаем процедуру считывания
		            			rfid.read((byte)2);		            			
		            		} 
		                });
    			}
    		else {
    			setResult(RFID.RESULT_RFID_INIT_ERROR);
    			finish();
    		}	
			initView();
		}

		private void initView() {		
			
			spinner_operation_adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, list);
			Spinner_operation = (Spinner) findViewById(R.id.equipment_spinner_operations);
			Spinner_operation.setAdapter(spinner_operation_adapter);
			
			// TODO: настоящие операции над оборудованием (возможные)
			spinner_operation_adapter.clear();
			spinner_operation_adapter.add("Ремонт задвижки");
	      	spinner_operation_adapter.add("Осмотр задвижки");
	      	 
			TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			EquipmentOperationResultDBAdapter eqOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			
			CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			Equipment equipment = equipmentDBAdapter.getItem(equipment_uuid);
			ArrayList<EquipmentOperation> equipmentOperationList = eqOperationDBAdapter.getItemsByTaskAndEquipment("", equipment.getUuid());		
			tv_equipment_id.setText("TAGID: " + equipment.getTag_id());
			tv_equipment_name.setText("Название: " + equipment.getTitle());			
			tv_equipment_type.setText("Тип: " + eqTypeDBAdapter.getNameByUUID(equipment.getEquipment_type_uuid()));
			tv_equipment_position.setText("" + equipment.getLatitude() + " / " + equipment.getLongitude());
			//tv_equipment_date.setText(DataUtils.getDate(equipment.getStart_date(),"dd-MM-yyyy hh:mm"));
			tv_equipment_critical.setText("Критичность: " + criticalTypeDBAdapter.getNameByUUID(equipment.getCritical_type_uuid()));
			if (equipmentOperationList != null && equipmentOperationList.size()>0)
				tv_equipment_task_date.setText("" + taskDBAdapter.getCompleteTimeByUUID(equipmentOperationList.get(0).getTask_uuid()));
			else tv_equipment_task_date.setText("еще не обслуживалось");
			if (equipmentOperationList != null && equipmentOperationList.size()>0)
				tv_equipment_tasks.setText("" + eqOperationResultDBAdapter.getOperationResultByUUID(equipmentOperationList.get(0).getOperation_status_uuid()));
			else tv_equipment_tasks.setText("еще не обслуживалось");
			//File imgFile = new File(getApplicationInfo().dataDir + equipment.getImg());
			File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName() + File.separator + "img" + File.separator+ equipment.getImg());			
			
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName() + File.separator + "img";
			Log.d("Files", "Path: " + path);
			File f = new File(path);        
			File file[] = f.listFiles();
			Log.d("Files", "Size: "+ file.length);
			for (int i=0; i < file.length; i++)
			{
			    Log.d("Files", "FileName:" + file[i].getName());
			}
			// temporary!
			tv_equipment_documentation.setText("Tag UUID: " + equipment.getUuid());
			//tv_equipment_documentation.setText(Environment.getExternalStorageDirectory() + "/Android/data/ru.toir.mobile" + equipment.getImg());
			Long sd=imgFile.length();			
			if(imgFile.exists() && imgFile.isFile()){
			    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    tv_equipment_image.setImageBitmap(myBitmap);			    			    
			}			
		}

	 private void FillListViewOperations()
		{				 
		 EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
		 EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
	     CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
		 EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
		 OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
	     ArrayList<EquipmentOperation> equipmentOperationList = eqOperationDBAdapter.getItemsByTaskAndEquipment("", equipment_uuid);		
	     int operation_type;
	 	 int cnt=0;
		 List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
		 String[] from = { "name","descr","img"};
		 int[] to = { R.id.lv_firstLine,R.id.lv_secondLine,R.id.lv_icon};
		 if (equipmentOperationList!=null)
		 while (cnt<equipmentOperationList.size())
				{	 		 		
			 	 HashMap<String, String> hm = new HashMap<String,String>();
			 	 hm.put("name", "Операция: " + operationTypeDBAdapter.getOperationTypeByUUID(equipmentOperationList.get(cnt).getOperation_type_uuid())+ " [" + DataUtils.getDate(equipmentOperationResultDBAdapter.getStartDateByUUID(equipmentOperationList.get(cnt).getEquipment_uuid()),"dd-MM-yyyy hh:mm") + "]");
			 	 hm.put("descr","Критичность: " + criticalTypeDBAdapter.getNameByUUID(eqDBAdapter.getCriticalByUUID(equipmentOperationList.get(cnt).getEquipment_uuid())) 
			 			 		+ " Результат: [" + equipmentOperationResultDBAdapter.getOperationResultByUUID(equipmentOperationList.get(cnt).getUuid()) + "]");
				 // Creation row
			 	 operation_type = equipmentOperationResultDBAdapter.getOperationResultTypeByUUID(equipmentOperationList.get(cnt).getOperation_status_uuid());
			 	 switch (operation_type)
			 		{
			 		 case 1: hm.put("img", Integer.toString(R.drawable.img_status_4)); break;
			 		 case 2: hm.put("img", Integer.toString(R.drawable.img_status_3)); break;
			 		 case 3: hm.put("img", Integer.toString(R.drawable.img_status_1)); break;
			 		 default: hm.put("img", Integer.toString(R.drawable.img_status_1));
			 		}
			 	 aList.add(hm);
	 			 cnt++;	 
				}
			SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), aList, R.layout.listview, from, to);		 
			// Setting the adapter to the listView
			lv.setAdapter(adapter);
		}

		public void Callback(String result) {
			//Intent data = null;
			if(result == null){
				setResult(RFID.RESULT_RFID_READ_ERROR);	
			} else {
				if (result.length()<100)
					{
					 Toast.makeText(this, "Ответ слишком короткий",Toast.LENGTH_SHORT).show();					
					 return;
					}

				equipmenttag.set_equipment_uuid(DataUtils.StringToUUID(result.substring(0, 32)));
				equipmenttag.set_status(result.substring(32, 36).toLowerCase(Locale.ENGLISH));
				equipmenttag.set_last(result.substring(36, 40));
				tagrecord.operation_date=Long.parseLong(result.substring(40, 56),16);
				tagrecord.operation_length = Short.parseShort(result.substring(56, 60),16);
				tagrecord.operation_type = result.substring(60, 64).toLowerCase(Locale.ENGLISH);
				tagrecord.operation_result = result.substring(64, 68).toLowerCase(Locale.ENGLISH);
				tagrecord.user = result.substring(68, 72).toLowerCase(Locale.ENGLISH);
				tagrecords.add(0,tagrecord);
				tagrecord2.operation_date=Long.parseLong(result.substring(72, 88),16);
				tagrecord2.operation_length = Short.parseShort(result.substring(88, 92),16);
				tagrecord2.operation_type = result.substring(92, 96).toLowerCase(Locale.ENGLISH);
				tagrecord2.operation_result = result.substring(96, 100).toLowerCase(Locale.ENGLISH);
				tagrecord2.user = result.substring(100, 104).toLowerCase(Locale.ENGLISH);
				tagrecords.add(1,tagrecord2);
				
				EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
			    EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
				OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
				EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
				EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
		
				tv_equipment_name.setText("Название: " + eqDBAdapter.getEquipsNameByUUID(equipmenttag.get_equipment_uuid()));		
				tv_equipment_type.setText("Тип: " + eqTypeDBAdapter.getNameByUUID(eqDBAdapter.getEquipsTypeByUUID(equipmenttag.get_equipment_uuid())));
				tv_equipment_position.setText("" + eqDBAdapter.getLocationCoordinatesByUUID(equipmenttag.get_equipment_uuid()));
				tv_equipment_status.setText(equipmentStatusDBAdapter.getNameByPartOfUUID(equipmenttag.get_status()));
				tv_equipment_documentation.setText("TAGID: " + equipmenttag.get_equipment_uuid());
				
			 	int cnt=0, operation_type=0;
				List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
				String[] from = { "name","descr","img"};
				int[] to = { R.id.lv_firstLine,R.id.lv_secondLine,R.id.lv_icon};
				// пока одна запись
			    while (cnt < 2)
			    	{	 		 		
					 HashMap<String, String> hm = new HashMap<String,String>();
											 
					 hm.put("name", "Операция: " + operationTypeDBAdapter.getOperationTypeByPartOfUUID(tagrecords.get(cnt).operation_type)+ " [" + DataUtils.getDate(tagrecords.get(cnt).operation_date,"dd-MM-yyyy hh:mm") + "]");
					 hm.put("descr","Результат: [" + equipmentOperationResultDBAdapter.getOperationResultByPartOfUUID(tagrecords.get(cnt).operation_result) + "]");
					 // Creation row
					 operation_type = equipmentOperationResultDBAdapter.getOperationResultTypeByPartOfUUID(tagrecords.get(cnt).operation_result);
					 switch (operation_type)
					 	{
					 	 case 1: hm.put("img", Integer.toString(R.drawable.img_status_4)); break;
					 	 case 2: hm.put("img", Integer.toString(R.drawable.img_status_3)); break;
					 	 case 3: hm.put("img", Integer.toString(R.drawable.img_status_1)); break;
					 	 default: hm.put("img", Integer.toString(R.drawable.img_status_1));
					 	}
					 aList.add(hm);
			 		 cnt++;	 
					}
				SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), aList, R.layout.listview, from, to);		 
				// Setting the adapter to the listView
				lv.setAdapter(adapter);

				//tv_equipment_id.setText("TAGID: " + equipmenttag.get_equipment_uuid());
				if (tagrecords.get(0).operation_date>0)
					{
					 tv_equipment_task_date.setText(DataUtils.getDate(tagrecords.get(0).operation_date,"dd-MM-yyyy hh:mm"));
					 tv_equipment_tasks.setText(equipmentOperationResultDBAdapter.getOperationResultByPartOfUUID(tagrecords.get(0).operation_result));
					}
				else tv_equipment_task_date.setText("еще не обслуживалось");
			}
		}
	}
