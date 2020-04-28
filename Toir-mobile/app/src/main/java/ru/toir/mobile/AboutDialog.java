package ru.toir.mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

class AboutDialog extends Dialog {

    private Context mContext = null;

    AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        TextView tv;
        setContentView(R.layout.about);

        tv = findViewById(R.id.legal_text);
        tv.setText(readRawTextFile(R.raw.legal));
        tv = findViewById(R.id.info_text);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(readRawTextFile(R.raw.info),Html.FROM_HTML_MODE_LEGACY);
        } else {
            Html.fromHtml(readRawTextFile(R.raw.info));
        }
        PackageInfo pInfo = null;
        // вставил вручную, чтобы не заморачиваться с ребилдами
        tv.setText(getContext().getString(R.string.program_version, "5.5"));
        Linkify.addLinks(tv, Linkify.ALL);
    }

    private String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while ((line = buf.readLine()) != null)
                text.append(line);
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}
