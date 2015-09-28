package ru.toir.mobile.fragments;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.*;
import ru.toir.mobile.db.tables.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.toir.mobile.utils.DataUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class EquipmentInfoFragment extends Fragment {
	private String equipment_uuid;
	private Spinner Spinner_operation;
	private ListView lv;
	ArrayAdapter<String> spinner_operation_adapter;
	ArrayList<String> list = new ArrayList<String>();
	
/*	
    android:id="@+id/equipment_image"
    android:id="@+id/equipment_listView_main"
*/
	
	private TextView tv_equipment_id;
	private TextView tv_equipment_name;
	private TextView tv_equipment_type;
	private TextView tv_equipment_position;
	private TextView tv_equipment_date;	
	private TextView tv_equipment_tasks;	
	private TextView tv_equipment_critical;
	private ImageView tv_equipment_image;
	//private TextView tv_equipment_text_date;
	//private TextView tv_equipment_text_tasks;
	private TextView tv_equipment_task_date;
	
	public static EquipmentInfoFragment newInstance(String uuid) {
		EquipmentInfoFragment f = new EquipmentInfoFragment(uuid);
        return f;
    }
	
	public EquipmentInfoFragment (String uuid)
		{		
		 equipment_uuid = uuid;
		}
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.equipment_layout, container, false);
		//tv_equipment_id = (TextView) rootView.findViewById(R.id.equipment_text_id);
		tv_equipment_name = (TextView) rootView.findViewById(R.id.equipment_text_name);
		tv_equipment_type = (TextView) rootView.findViewById(R.id.equipment_text_type);		
		tv_equipment_position = (TextView) rootView.findViewById(R.id.equipment_position);
		//tv_equipment_date = (TextView) rootView.findViewById(R.id.equipment_start_date);	
		tv_equipment_critical = (TextView) rootView.findViewById(R.id.equipment_critical);	
		tv_equipment_task_date = (TextView) rootView.findViewById(R.id.equipment_text_date);
		tv_equipment_tasks = (TextView) rootView.findViewById(R.id.equipment_text_tasks);	
		tv_equipment_image = (ImageView) rootView.findViewById(R.id.equipment_image);
	 	lv = (ListView) rootView.findViewById(R.id.equipment_listView_main);	 	
	 	FillListViewOperations(rootView);
	 	initView(rootView);
		return rootView;
	}
	
	private void initView(View view) {		
		
		spinner_operation_adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, list);
		Spinner_operation = (Spinner) view.findViewById(R.id.equipment_spinner_operations);
		Spinner_operation.setAdapter(spinner_operation_adapter);
		
		// TODO: настоящие операции над оборудованием (возможные)
		spinner_operation_adapter.clear();
		spinner_operation_adapter.add("Ремонт задвижки");
      	spinner_operation_adapter.add("Осмотр задвижки");
      	 
		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
		EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
		EquipmentOperationResultDBAdapter eqOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
		
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
		Equipment equipment = equipmentDBAdapter.getItem(equipment_uuid);
		ArrayList<EquipmentOperation> equipmentOperationList = eqOperationDBAdapter.getItemsByTaskAndEquipment("", equipment.getUuid());		
		tv_equipment_id.setText("TAGID: " + equipment.getTag_id());
		tv_equipment_name.setText("Название: " + equipment.getTitle());			
		tv_equipment_type.setText("Тип: " + eqTypeDBAdapter.getNameByUUID(equipment.getEquipment_type_uuid()));
		tv_equipment_position.setText("" + equipment.getLatitude() + " / " + equipment.getLongitude());
		tv_equipment_date.setText(DataUtils.getDate(equipment.getStart_date(),"dd-MM-yyyy hh:mm"));
		tv_equipment_critical.setText("Критичность: " + criticalTypeDBAdapter.getNameByUUID(equipment.getCritical_type_uuid()));
		if (equipmentOperationList.size()>0)
			tv_equipment_task_date.setText("Дата обслуживания: " + taskDBAdapter.getCompleteTimeByUUID(equipmentOperationList.get(0).getTask_uuid()));
		else tv_equipment_task_date.setText("Дата обслуживания: оборудование еще не обслуживалось");
		if (equipmentOperationList.size()>0)
			tv_equipment_tasks.setText("Результат обслуживания: " + eqOperationResultDBAdapter.getOperationResultByUUID(equipmentOperationList.get(0).getOperation_status_uuid()));
		else tv_equipment_tasks.setText("Результат обслуживания: оборудование еще не обслуживалось");
		File imgFile = new File(equipment.getImg());
		if(imgFile.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    tv_equipment_image.setImageBitmap(myBitmap);
		}
	}

 private void FillListViewOperations(View view)
	{				 
	 EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
	 EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
     CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
	 EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
	 OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext()));
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
		 	 hm.put("name", "Операция: " + operationTypeDBAdapter.getOperationTypeByUUID(equipmentOperationList.get(cnt).getOperation_type_uuid()));
		 	 hm.put("descr","Критичность: " + criticalTypeDBAdapter.getNameByUUID(eqDBAdapter.getCriticalByUUID(equipmentOperationList.get(cnt).getEquipment_uuid())) 
		 			 		+ " [" + DataUtils.getDate(equipmentOperationResultDBAdapter.getStartDateByUUID(equipmentOperationList.get(cnt).getEquipment_uuid()),"dd-MM-yyyy hh:mm") + "]");			 // Creation row
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
		SimpleAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), aList, R.layout.listview, from, to);		 
		// Setting the adapter to the listView
		lv.setAdapter(adapter);
	}
}
