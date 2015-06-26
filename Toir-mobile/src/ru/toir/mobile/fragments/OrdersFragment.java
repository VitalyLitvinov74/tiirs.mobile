package ru.toir.mobile.fragments;

import java.util.ArrayList;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.utils.DataUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class OrdersFragment extends Fragment {
	private TableLayout tl_task;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.orders_layout, container, false);
		initView(rootView);
		return rootView;
		}

	private void initView(View view)
		{
		 tl_task = (TableLayout) view.findViewById(R.id.TableLayout01);
		 String tagId = "01234567";
		 UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		 Users user = users.getUserByTagId(tagId);
		 users.close();
		 if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
		 } else {
			 TaskDBAdapter dbOrder = new TaskDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
//			 TaskStatusDBAdapter TaskStatusDBAdapt = new TaskStatusDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
			 ArrayList<Task> ordersList = dbOrder.getOrdersByTagId(user.getUuid());	
			 Integer cnt=0;
			 final TableRow tableHead = new TableRow(getActivity().getApplicationContext());
			 //TableRow.LayoutParams params = new TableRow.LayoutParams();
			 tableHead.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
			 tableHead.setBackgroundColor(getResources().getColor(R.color.darkgrey));
			 final TextView tv_head1 = new TextView(getActivity().getApplicationContext());
			 tv_head1.setText("S");
			 tableHead.addView(tv_head1);
			 final TextView tv_head2 = new TextView(getActivity().getApplicationContext());
			 tv_head2.setText("uuid");
			 tableHead.addView(tv_head2);
			 final TextView tv_head3 = new TextView(getActivity().getApplicationContext());
			 tv_head3.setText("создан");
			 tableHead.addView(tv_head3);
			 final TextView tv_head4 = new TextView(getActivity().getApplicationContext());
			 tv_head4.setText("изменен");
			 tableHead.addView(tv_head4);
			 final TextView tv_head5 = new TextView(getActivity().getApplicationContext());
			 tv_head5.setText("сдан");
			 tableHead.addView(tv_head5);
			 final TextView tv_head6 = new TextView(getActivity().getApplicationContext());
			 tv_head6.setText("поп");
			 tableHead.addView(tv_head6);
			 final TextView tv_head7 = new TextView(getActivity().getApplicationContext());
			 tv_head7.setText("от");
			 tableHead.addView(tv_head7);
			 tl_task.addView(tableHead);
			 
			 while (cnt<ordersList.size())
			 	{				
				 // Creation row
				 final TableRow tableRow = new TableRow(getActivity().getApplicationContext());
				 TableLayout.LayoutParams tableRowParams= new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
				 tableRowParams.setMargins(5, 2, 5, 2);
				 //TableRow.LayoutParams params = new TableRow.LayoutParams();
				 tableRow.setLayoutParams(tableRowParams);
				 tableRow.setBackgroundColor(getResources().getColor(R.color.black));

				 // Creation row
				 final ImageView img = new ImageView(getActivity().getApplicationContext());
				 img.setImageResource(R.drawable.checkmark_32);
				 if (ordersList.get(cnt).getTask_status_uuid().equals(TaskStatusDBAdapter.STATUS_UUID_UNCOMPLETED))
					 img.setImageResource(R.drawable.forbidden_32);
				 if (ordersList.get(cnt).getTask_status_uuid().equals(TaskStatusDBAdapter.STATUS_UUID_COMPLETED))
					 img.setImageResource(R.drawable.checkmark_32);
				 if (ordersList.get(cnt).getTask_status_uuid().equals(TaskStatusDBAdapter.STATUS_UUID_RECIEVED))
					 img.setImageResource(R.drawable.information_32);
				 if (ordersList.get(cnt).getTask_status_uuid().equals(TaskStatusDBAdapter.STATUS_UUID_CREATED))
					 img.setImageResource(R.drawable.help_32);
	         
				 final TextView tv_uuid = new TextView(getActivity().getApplicationContext());
				 tv_uuid.setText(ordersList.get(cnt).getUuid().toString().subSequence(0,10));
				 tv_uuid.setGravity(Gravity.CENTER_HORIZONTAL);
				 final TextView tv_create = new TextView(getActivity().getApplicationContext());
				 tv_create.setText(DataUtils.getDate(ordersList.get(cnt).getCreate_date(),"dd-MM-yyyy hh:mm"));
				 tv_create.setGravity(Gravity.CENTER_HORIZONTAL);
				 final TextView tv_modify = new TextView(getActivity().getApplicationContext());
				 tv_modify.setText(DataUtils.getDate(ordersList.get(cnt).getModify_date(),"dd-MM-yyyy hh:mm"));
				 final TextView tv_close = new TextView(getActivity().getApplicationContext());
				 tv_close.setText(DataUtils.getDate(ordersList.get(cnt).getClose_date(),"dd-MM-yyyy hh:mm"));
				 final TextView tv_count = new TextView(getActivity().getApplicationContext());
				 tv_count.setText(""+ordersList.get(cnt).getAttempt_count());
				 tv_count.setGravity(Gravity.CENTER_HORIZONTAL);
				 
				 final ImageView img2 = new ImageView(getActivity().getApplicationContext());
				 img2.setImageResource(R.drawable.help_32);
				 if (ordersList.get(cnt).isSuccessefull_send())
					 img2.setImageResource(R.drawable.checkmark_32);
				 else
					 img2.setImageResource(R.drawable.forbidden_32);
				 
				 tableRow.addView(img);
				 tableRow.addView(tv_uuid);
				 tableRow.addView(tv_create);
				 tableRow.addView(tv_modify);
				 tableRow.addView(tv_close);
				 tableRow.addView(tv_count);
				 tableRow.addView(img2);
				 tl_task.addView(tableRow);	    	 
				 cnt=cnt+1;
				 //if (cnt>10) break;
			 	}
			 dbOrder.close();
		 	}
		}
}
