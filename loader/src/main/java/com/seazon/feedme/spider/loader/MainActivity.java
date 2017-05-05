package com.seazon.feedme.spider.loader;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seazon.feedme.spider.BaseSpiderService;
import com.seazon.feedme.spider.ISpider;
import com.seazon.feedme.spider.SpiderItem;
import com.seazon.feedme.spider.SpiderStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String UNSPECIFIED = "Unspecified";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    Spinner spiderSpinner;
    EditText urlEdit;
    TextView outputView;

    String[] keys;
    ISpider iSpider;
    String text;
    String continuation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSpiderKeyValue();
        initSpider();

        urlEdit = (EditText) findViewById(R.id.urlEdit);
        outputView = (TextView) findViewById(R.id.outputView);
    }

    private void initSpider() {

        spiderSpinner = (Spinner) findViewById(R.id.spiderSpinner);
        SpinnerAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, keys);
        spiderSpinner.setAdapter(adapter);
        spiderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = keys[position];
                if (UNSPECIFIED.equals(value)) {
                    return;
                }
                Intent service = new Intent(BaseSpiderService.ACTION_EXTENSION);
                service.setPackage(value);
                bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iSpider = ISpider.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iSpider = null;
        }
    };

    public void onLoadFirst(View v) {
        if (iSpider != null) {
            final String url = urlEdit.getText().toString();
            new Thread() {
                @Override
                public void run() {
                    try {
                        loadRss(url, null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void onLoadNext(View v) {
        if (iSpider != null) {
            final String url = urlEdit.getText().toString();
            new Thread() {
                @Override
                public void run() {
                    try {
                        loadRss(url, continuation);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    private void loadRss(String url, String continuation) throws RemoteException {
        String rep = iSpider.getFeed(url);
        text = rep + "\n\n";
        rep = iSpider.getItems(url, continuation);
        SpiderStream stream = parse(rep);
        this.continuation = stream.continuation;
        if (stream != null && stream.items != null) {
            for (SpiderItem item : stream.items) {
                text += "      title:" + item.title + "\n";
                text += "        url:" + item.url + "\n";
                text += "publishTime:" + sdf.format(new Date(item.publishTime)) + "\n";
                text += "  thumbnail:" + item.thumbnail + "\n";
                text += "     author:" + item.author + "\n";
                text += "    content:" + item.content + "\n";
                text += "\n";
            }
        } else {
            text += "null";
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                outputView.setText(text);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    public static SpiderStream parse(String json) throws JsonSyntaxException {
        return new Gson().fromJson(json, SpiderStream.class);
    }

    /**
     * Returns a listing of all available (installed) extensions.
     */
    private List<ComponentName> getAvailableExtensions(Context context) {
        List<ComponentName> availableExtensions = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(
                new Intent(BaseSpiderService.ACTION_EXTENSION), PackageManager.GET_META_DATA);
        for (ResolveInfo resolveInfo : resolveInfos) {
            availableExtensions.add(new ComponentName(resolveInfo.serviceInfo.packageName,
                    resolveInfo.serviceInfo.name));
        }

        return availableExtensions;
    }

    private void initSpiderKeyValue() {
        List<ComponentName> list = getAvailableExtensions(this);
        keys = new String[1 + list.size()];
        int index = 0;
        keys[index++] = UNSPECIFIED;
        for (ComponentName ri : list) {
            keys[index++] = ri.getPackageName();
        }
    }
}
