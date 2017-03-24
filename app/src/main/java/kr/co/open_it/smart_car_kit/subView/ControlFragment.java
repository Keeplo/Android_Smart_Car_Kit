package kr.co.open_it.smart_car_kit.subView;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.open_it.smart_car_kit.MainActivity;
import kr.co.open_it.smart_car_kit.R;

/**
 * Created by yongwookim on 2017-02-09.
 */

public class ControlFragment extends Fragment {
    private static final String TAG = "Control";

    MainActivity mainActivity;

    private ImageButton btnForward;//앞
    private ImageButton btnBack;//뒤
    private ImageButton btnLeft;//좌
    private ImageButton btnRight;//우

    //카메라 서보 모터
    private ImageButton cbtnU;//위로
    private ImageButton cbtnD;//아래로
    private ImageButton cbtnM;//중앙

    TextView tv_temperature, tv_humidity, tv_brightness, tv_distance;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_con, container, false);
        System.out.println("--ControlFragment onCreateView()");

        mainActivity = (MainActivity) getActivity();

        //상태 표기
        tv_temperature = (TextView) v.findViewById(R.id.status_temperature);
        tv_humidity = (TextView) v.findViewById(R.id.status_humidity);
        tv_brightness = (TextView) v.findViewById(R.id.status_brightness);
        tv_distance = (TextView) v.findViewById(R.id.status_distance);

        btnForward = (ImageButton) v.findViewById(R.id.f_b);
        btnBack = (ImageButton) v.findViewById(R.id.b_b);
        btnLeft = (ImageButton) v.findViewById(R.id.l_b);
        btnRight = (ImageButton) v.findViewById(R.id.r_b);

        //카메라 상하좌우
        cbtnU = (ImageButton) v.findViewById(R.id.u_b);
        cbtnD = (ImageButton) v.findViewById(R.id.d_b);
        cbtnM = (ImageButton) v.findViewById(R.id.m_b);
        //cbtnR = (ImageButton) this.findViewById(R.id.btR);

        btnForward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "MF", "전");
                return true;
            }
        });
        btnBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "MB", "후");
                return true;
            }
        });

        btnLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "ML", "좌");
                return true;
            }
        });
        btnRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "MR", "우");
                return true;
            }
        });

        //---------------카메라-----------------------------------//
        cbtnU.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "C_U", "상");

                return true;

            }
        });
        cbtnD.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "C_D", "하");

                return true;
            }
        });
        cbtnM.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchHandle(event, "C_C", "중앙");
                return true;
            }
        });

        return v;
    }
    private void touchHandle(MotionEvent event, String orderStr, String tips) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                MainActivityOrder(orderStr, tips);
                break;

            case MotionEvent.ACTION_UP:
                MainActivityOrder("MS", "정지");
                break;

            default:
                break;
        }
    }
    private void MainActivityOrder(String orderStr, String tips) {
        if(mainActivity.getConnectOK()) {
            mainActivity.publishMessage(orderStr);
            Log.w(TAG, tips + "/" + orderStr + tips);
        } else {
            if(orderStr.equals("MS")) {
                Toast.makeText(getActivity().getApplicationContext(), "서버와 연결이 되어있지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void changeStatus() {
        tv_temperature.setText(mainActivity.getStatus_data().temperature);
        tv_humidity.setText(mainActivity.getStatus_data().humidity);
        tv_brightness.setText(mainActivity.getStatus_data().brightness);
        tv_distance.setText(mainActivity.getStatus_data().distance);
    }


    public void onStart() {
        super.onStart();
        System.out.println("--ControlFragment onStart()");
    }
    public void onStop() {
        super.onStop();
        System.out.println("--ControlFragment onStop()");
    }
    public void onResume() {
        super.onResume();
        System.out.println("--ControlFragment onResume()");
    }
}