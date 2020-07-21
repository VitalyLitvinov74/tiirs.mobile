package ru.toir.mobile.multi;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import ru.toir.mobile.multi.db.realm.Message;
import ru.toir.mobile.multi.db.realm.User;

import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

public class MessageInfoActivity extends AppCompatActivity {
    private final static String TAG = "MessageInfo";
    private static String message_uuid;
    CoordinatorLayout rootLayout;
    private Realm realmDB;
    private Context context;
    private ListViewClickListener mainListViewClickListener = new ListViewClickListener();
    private ImageView image;
    private TextView userFrom;
    private TextView date;
    private TextView text;

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        realmDB = Realm.getDefaultInstance();
        Bundle b = getIntent().getExtras();
        if (b != null && b.getString("message_uuid") != null) {
            message_uuid = b.getString("message_uuid");
        } else {
            finish();
            return;
        }

        setMainLayout(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        image = findViewById(R.id.user_image);
        userFrom = findViewById(R.id.user_from);
        date = findViewById(R.id.date);
        text = findViewById(R.id.text);

        initView();
    }

    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.message_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setSubtitle(R.string.menu_messages);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }
    }

    private void initView() {
        final Message message = realmDB.where(Message.class).equalTo("uuid", message_uuid).findFirst();
        if (message == null) {
            return;
        }

        userFrom.setText(message.getFromUser().getName());
        String path = context.getExternalFilesDir("/" + User.getImageRoot()) + File.separator;
        Bitmap user_bitmap = getResizedBitmap(path,
                message.getFromUser().getImage(), 0, 70,
                message.getFromUser().getChangedAt().getTime());
        image.setImageBitmap(user_bitmap);
        text.setText(message.getText());
        String sDate = new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(message.getDate());
        date.setText(sDate);
        if (message.getStatus() == Message.Status.MESSAGE_READ) {
            userFrom.setTypeface(null, Typeface.NORMAL);
            text.setTypeface(null, Typeface.NORMAL);
        } else {
            userFrom.setTypeface(null, Typeface.BOLD);
            text.setTypeface(null, Typeface.BOLD);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realmDB.close();
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, View selectedItemView, final int position, long id) {
        }
    }
}
