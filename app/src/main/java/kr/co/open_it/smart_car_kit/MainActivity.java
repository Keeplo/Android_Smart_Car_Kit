package kr.co.open_it.smart_car_kit;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import kr.co.open_it.smart_car_kit.handler.MqttHandler;
import kr.co.open_it.smart_car_kit.model.Status_Data;
import kr.co.open_it.smart_car_kit.model.User_Data;
import kr.co.open_it.smart_car_kit.subView.ControlFragment;
import kr.co.open_it.smart_car_kit.subView.SettingFragment;
import kr.co.open_it.smart_car_kit.subView.WebViewFragment;

public class MainActivity extends AppCompatActivity {
    private MqttHandler mqttHandler;

    View Setting, WebView, Control;
    Menu currentMenu;
    int page_type = 0;

    private User_Data user_data;
    private Status_Data status_data;

    ControlFragment controlFragment;
    WebViewFragment webViewFragment;
    SettingFragment settingFragment;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("MainActivity onCreate()");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.toolbar_icon);

        fragmentManager = getFragmentManager();
        webViewFragment = (WebViewFragment) fragmentManager.findFragmentById(R.id.fragment_webView);
        controlFragment = (ControlFragment) fragmentManager.findFragmentById(R.id.fragment_control);
        settingFragment = (SettingFragment) fragmentManager.findFragmentById(R.id.fragment_setting);

        Setting = (View) settingFragment.getView();
        WebView = (View) webViewFragment.getView();
        Control = (View) controlFragment.getView();


        user_data =  new User_Data("", "", "", "");
        status_data = new Status_Data("", "", "", "");

        //MQTT Client 생성
        mqttHandler = new MqttHandler(this.getApplicationContext());

        switch (page_type) {
            case 0: // Webview
                getSupportActionBar().setTitle("MQTT Car 제어");
                Setting.setVisibility(View.GONE);
                WebView.setVisibility(View.VISIBLE);
                Control.setVisibility(View.VISIBLE);
                break;

            case 1: // SettingView
                getSupportActionBar().setTitle("설정 입력");
                Setting.setVisibility(View.VISIBLE);
                WebView.setVisibility(View.GONE);
                Control.setVisibility(View.VISIBLE);
                break;

            case 9999:
                Toast.makeText(MainActivity.this, "잘못된 접근입니다.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        currentMenu = menu;
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(true);

        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                getSupportActionBar().setTitle("스마트 카 킷");
                Setting.setVisibility(View.GONE);
                WebView.setVisibility(View.VISIBLE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                currentMenu.getItem(0).setVisible(true);
                page_type = 0;
                break;

            case R.id.setting:
                getSupportActionBar().setTitle("기본 설정");
                WebView.setVisibility(View.GONE);
                Setting.setVisibility(View.VISIBLE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                currentMenu.getItem(0).setVisible(false);
                page_type = 1;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void initConnection() {
        ConnectView();
    }
    public void publishMessage(String msg) {
        this.mqttHandler.publishMessage(msg);
    }
    public void getStatusData() {
        this.mqttHandler.getStatusData();
    }
    public void disConnection() {
        this.mqttHandler.disConnectToServer();
        setStatus_data("-", "-", "-", "-");
        controlFragment.changeStatus();
        Toast.makeText(getApplicationContext(), "현재 서버와의 연결을 종료합니다.", Toast.LENGTH_SHORT).show();
    }
    public Boolean getConnectOK() {
        return this.mqttHandler.getConnectOK();
    }
    public Boolean getUpdateOK() {
        return this.mqttHandler.getUpdateOk();
    }

    //저장 데이터 접근
    public void setUser_data(String webURL, String MqttURL, String pubTopic, String subTopic) {
        user_data.webURL = webURL;
        user_data.MqttURL = MqttURL;
        user_data.pubTopic = pubTopic;
        user_data.subTopic = subTopic;
    }
    public User_Data getUser_data() {
        return user_data;
    }
    public void setStatus_data(String temperature, String humidity, String brightness, String distance) {
        if(temperature.equals("-")) {
            status_data.temperature = temperature;
            status_data.humidity = humidity;
            status_data.brightness = brightness;
            status_data.distance = distance;
        } else {
            status_data.temperature = temperature + "°C";
            status_data.humidity = humidity + "DA";
            status_data.brightness = brightness + "LUX";
            status_data.distance = distance + "";
        }
    }
    public Status_Data getStatus_data() {
        return status_data;
    }

    public void setWebViewURL(String url) {
        webViewFragment.playWebView(url);
    }

    public void StatusView() {
        getStatusData();
        new GetStatusTask().execute();
    }
    private class GetStatusTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog asyncDialog ;
        //로딩 팝업
        protected void onPreExecute() {
            if(asyncDialog==null){
                asyncDialog  = new ProgressDialog(MainActivity.this);
            }
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCancelable(false);
            asyncDialog.setMessage("센서정보를 가져오고 있습니다..");
            asyncDialog.show();
        }
        //second Thread 센서정보 받아오기
        protected Integer doInBackground(String... urls) {
            int i = 0;
            while(!getUpdateOK()) {
                //통신 시간
                if(i==100000000) {
                    break;
                }
                i++;
            }
            return i;
        }
        //센서정보 UI업데이트
        protected void onPostExecute(Integer i) {
            if(i.intValue() == 100000000) {
                Toast.makeText(getApplicationContext(), "연결상태 불량, 다시 시도하세요", Toast.LENGTH_SHORT).show();
                asyncDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "상태 정보 업데이트", Toast.LENGTH_SHORT).show();
                setStatus_data(mqttHandler.temperature, mqttHandler.humidity, mqttHandler.brightness, mqttHandler.distance);
                controlFragment.changeStatus();
                asyncDialog.dismiss();
            }
        }
    }
    public void getStatus(View v) {
        if(getConnectOK()) {
            StatusView();
        } else {
            Toast.makeText(getApplicationContext(), "서버와 연결이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void ConnectView() {
        this.mqttHandler.initConnection(user_data.MqttURL, user_data.pubTopic, user_data.subTopic);
        new GetConnectTask().execute();
    }
    private class GetConnectTask extends AsyncTask<String, Void, Integer> {

        ProgressDialog asyncDialog ;
        //로딩 팝업
        protected void onPreExecute() {
            if(asyncDialog==null){
                asyncDialog  = new ProgressDialog(MainActivity.this);
            }
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setCancelable(false);
            asyncDialog.setMessage("서버와 연결을 시도하고 있습니다..");
            asyncDialog.show();
        }
        //second Thread 센서정보 받아오기
        protected Integer doInBackground(String... urls) {
            int i = 0;
            int j = 0;
            while(!(getConnectOK()&&getUpdateOK())) {
                //통신 시간
                if(i==10000000) {
                    break;
                }
                i++;
            }
            return i;
        }
        //센서정보 UI업데이트
        protected void onPostExecute(Integer i) {
            if(i.intValue() == 10000000) {
                Toast.makeText(getApplicationContext(), "연결상태 불량, 다시 시도하세요", Toast.LENGTH_SHORT).show();

                asyncDialog.dismiss();
            } else {
                Toast.makeText(getApplicationContext(), "서버와 연결 성공", Toast.LENGTH_SHORT).show();
                setStatus_data(mqttHandler.temperature, mqttHandler.humidity, mqttHandler.brightness, mqttHandler.distance);
                controlFragment.changeStatus();
                asyncDialog.dismiss();
            }
        }
    }

    protected void onStart() {
        super.onStart();
        System.out.println("MainActivity onStart()");
    }
    protected void onStop() {
        super.onStop();
        System.out.println("MainActivity onStop()");
        if (getConnectOK()) {
            disConnection();
        }
    }
    protected void onResume() {
        super.onResume();
        System.out.println("MainActivity onResume()");
    }
}
