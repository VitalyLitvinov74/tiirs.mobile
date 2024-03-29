package ru.toir.mobile.multi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
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
    private ListView messageListView;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.messages_layout, container, false);
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setSubtitle(getString(R.string.menu_messages));
        realmDB = Realm.getDefaultInstance();

        messageListView = rootView.findViewById(R.id.message_listView);
        TextView output_text = rootView.findViewById(R.id.output_text);
        TextView input_text = rootView.findViewById(R.id.input_text);
        TextView output_count = rootView.findViewById(R.id.sent_count);
        TextView input_count = rootView.findViewById(R.id.input_count);

        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user != null) {
            long sendMessagesCount = realmDB.where(Message.class).equalTo("toUser._id", user.get_id()).count();
            long inputMessagesCount = realmDB.where(Message.class).equalTo("fromUser._id", user.get_id()).count();
            output_count.setText(String.format(Locale.ENGLISH, "%d", sendMessagesCount));
            input_count.setText(String.format(Locale.ENGLISH, "%d", inputMessagesCount));
        }
        messageListView.setOnItemClickListener(new ListviewClickListener());

/*
        FloatingActionButton addMessage = rootView.findViewById(R.id.fab_add);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
*/

        output_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillListViewMessages(2);
            }
        });
        input_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillListViewMessages(1);
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

    private void FillListViewMessages(int type) {
        RealmResults<Message> messages;
        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user != null) {
            messages = realmDB.where(Message.class)
                    .equalTo("toUser._id", user.get_id())
                    .or()
                    .equalTo("fromUser._id", user.get_id())
                    .sort("date", Sort.DESCENDING)
                    .findAll();
            if (type == 1) {
                messages = realmDB.where(Message.class)
                        .equalTo("toUser._id", user.get_id())
                        .sort("date", Sort.DESCENDING)
                        .findAll();
            }
            if (type == 2) {
                messages = realmDB.where(Message.class)
                        .equalTo("fromUser._id", user.get_id())
                        .sort("date", Sort.DESCENDING)
                        .findAll();
            }
            MessageAdapter messageAdapter = new MessageAdapter(messages);
            messageListView.setAdapter(messageAdapter);
        }
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
