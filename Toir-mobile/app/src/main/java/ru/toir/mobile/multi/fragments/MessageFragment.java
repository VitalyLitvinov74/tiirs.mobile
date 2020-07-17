package ru.toir.mobile.multi.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.MessageInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.MessageAdapter;
import ru.toir.mobile.multi.db.realm.Message;
import ru.toir.mobile.multi.db.realm.User;

public class MessageFragment extends Fragment {
    private static final String TAG;

    static {
        TAG = MessageFragment.class.getSimpleName();
    }

    private Realm realmDB;
    private boolean isInit;
    private Spinner typeSpinner;
    private ListView messageListView;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    private static void showMessageInfoActivity(Context context, String uuid) {
        Intent messageInfo = new Intent(context, MessageInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("message_uuid", uuid);
        messageInfo.putExtras(bundle);
        context.startActivity(messageInfo);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.message_layout, container, false);
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setSubtitle(getString(R.string.menu_messages));
        realmDB = Realm.getDefaultInstance();

        messageListView = rootView.findViewById(R.id.message_listView);

        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        RealmResults<Message> messages = realmDB.where(Message.class).equalTo("toUser", user.get_id()).findAll();

        messageListView.setOnItemClickListener(new ListviewClickListener());

        FloatingActionButton addMessage = rootView.findViewById(R.id.fab_add);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initView();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        isInit = true;

        return rootView;
    }

    private void initView() {
        FillListViewMessages(0);
    }

    private void FillListViewMessages(int status) {
        RealmResults<Message> messages;
        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        messages = realmDB.where(Message.class)
                .equalTo("toUser", user.get_id())
                .equalTo("status", status)
                .findAll();
        MessageAdapter messageAdapter = new MessageAdapter(messages);
        messageListView.setAdapter(messageAdapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isInit) {
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            Message message = (Message) parentView.getItemAtPosition(position);
            if (message != null) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }

                String message_uuid = message.getUuid();
                Intent messageInfo = new Intent(activity, MessageInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("message_uuid", message_uuid);
                messageInfo.putExtras(bundle);
                activity.startActivity(messageInfo);
            }
        }
    }
}
