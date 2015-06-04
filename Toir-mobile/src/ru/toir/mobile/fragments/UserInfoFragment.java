package ru.toir.mobile.fragments;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class UserInfoFragment extends Fragment {

	private TextView user_name;
	private TextView user_id;
	private TextView user_uuid;
	private TextView user_type;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.user_layout, container, false);
		initView(rootView);
		return rootView;
	}
	private void initView(View view) {
		user_name = (TextView) view.findViewById(R.id.EditText06);
		user_id = (TextView) view.findViewById(R.id.EditText07);
		user_type = (TextView) view.findViewById(R.id.EditText05);
		// hardcoded for test, tagID for user must be global
		String tagId = "01234567";
		
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		Users user = users.getUserByTagId(tagId);
		users.close();
		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
		} else {
			user_name.setText(user.getName());
			user_id.setText(user.toString());
		}

	}
}
