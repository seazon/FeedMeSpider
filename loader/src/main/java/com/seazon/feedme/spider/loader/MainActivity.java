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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.seazon.feedme.spider.BaseSpiderService;
import com.seazon.feedme.spider.ISpider;
import com.seazon.feedme.spider.SpiderItem;
import com.seazon.feedme.spider.SpiderStream;

import java.util.ArrayList;
import java.util.List;

import kale.adapter.CommonRcvAdapter;
import kale.adapter.item.AdapterItem;

public class MainActivity extends AppCompatActivity {

    public static final String UNSPECIFIED = "Unspecified";

    Spinner spiderSpinner;
    EditText urlEdit;
    Button loadNextBtn;
    private RecyclerView recyclerView;

    String[] keys;
    ISpider iSpider;
    String text;
    String continuation;
    List<SpiderItem> list = new ArrayList<>();
    CommonRcvAdapter rcvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSpiderKeyValue();
        initSpider();

        urlEdit = (EditText) findViewById(R.id.urlEdit);
//        urlEdit.setText("5954781019"); // toutiao 环球网
//        urlEdit.setText("4198268867"); // toutiao 爱范儿
//        urlEdit.setText("50037963924"); // toutiao 鹿幽鸣
//        urlEdit.setText("54564710422"); // toutiao 老司机数码
//        urlEdit.setText("924503095"); // baijia 孙永杰
//        urlEdit.setText("2151445694"); // baijia 数据猿
//        urlEdit.setText("898208290"); // baijia 顾泽辉
//        urlEdit.setText("14362"); // yidian 毒舌电影
        urlEdit.setText("54547876128,1557065300203521"); //toutiao 职场学
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rcvAdapter = new CommonRcvAdapter<SpiderItem>(list) {
            @Override
            public AdapterItem<SpiderItem> createItem(Object o) {
                return new Item(MainActivity.this);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rcvAdapter);
        loadNextBtn = (Button) findViewById(R.id.loadNextBtn);
        loadNextBtn.setEnabled(false);
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

    private void loadRss(String url, String continuation1) throws RemoteException {
        String rep = iSpider.getFeed(url);
        text = rep + "\n\n";
        rep = iSpider.getItems(url, continuation1);
        SpiderStream stream = parse(rep);
        list = stream.items;
        this.continuation = stream.continuation;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean enable = (continuation != null);
                loadNextBtn.setEnabled(enable);
                rcvAdapter.setData(list);
                rcvAdapter.notifyDataSetChanged();
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
