package kr.co.open_it.smart_car_kit.subView;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.open_it.smart_car_kit.MainActivity;
import kr.co.open_it.smart_car_kit.R;

/**
 * Created by yongwookim on 2017-02-10.
 */

public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";
    MainActivity mainActivity;

    public TextView tv_webviewURL, tv_mqttURL, tv_pubTopic, tv_subTopic;
    public EditText et_webviewURL, et_mqttURL, et_pubTopic, et_subTopic;
    public Button init_btn, disConnect_btn, connect_btn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set, container, false);
        System.out.println("--SettingFragment onCreateView()");

        mainActivity = (MainActivity) getActivity();

        tv_webviewURL = (TextView) v.findViewById(R.id.pre_tv_wbURL);
        tv_mqttURL = (TextView) v.findViewById(R.id.pre_tv_mqttURL);
        tv_pubTopic = (TextView) v.findViewById(R.id.pre_tv_pubTopic);
        tv_subTopic = (TextView) v.findViewById(R.id.pre_tv_subTopic);

        et_webviewURL = (EditText) v.findViewById(R.id.pre_et_wbURL);
        et_mqttURL = (EditText) v.findViewById(R.id.pre_et_mqttURL);
        et_pubTopic = (EditText) v.findViewById(R.id.pre_et_pubTopic);
        et_subTopic = (EditText) v.findViewById(R.id.pre_et_subTopic);

        init_btn = (Button) v.findViewById(R.id.pre_btn_init);
        disConnect_btn = (Button) v.findViewById(R.id.pre_btn_disConnect);
        connect_btn = (Button) v.findViewById(R.id.pre_btn_connect);

        init_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_webviewURL.setText(" ");
                et_mqttURL.setText(" ");
                et_pubTopic.setText(" ");
                et_subTopic.setText(" ");
                mainActivity.setStatus_data(et_webviewURL.getText().toString(), et_pubTopic.getText().toString(), et_subTopic.getText().toString(), et_mqttURL.getText().toString());
            }
        });
        disConnect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mainActivity.getConnectOK()) {
                    Toast.makeText(getActivity().getApplicationContext(), "현재 연결된 서버가 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    mainActivity.setWebViewURL("http://www.open-it.co.kr");
                    mainActivity.disConnection();
                }
            }
        });
        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.getConnectOK()) {
                    Toast.makeText(getActivity().getApplicationContext(), "서버와 연결중 입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    mainActivity.setUser_data(et_webviewURL.getText().toString(), et_mqttURL.getText().toString(), et_pubTopic.getText().toString(), et_subTopic.getText().toString());

                    mainActivity.setWebViewURL(mainActivity.getUser_data().webURL);
                    mainActivity.initConnection();
                }
            }
        });

        return v;
    }

    public void onStart() {
        super.onStart();
        System.out.println("--SettingFragment onStart()");
    }
    public void onStop() {
        super.onStop();
        System.out.println("--SettingFragment onStop()");
    }
    public void onResume() {
        super.onResume();
        System.out.println("--SettingFragment onResume()");
    }
}
