package kr.co.open_it.smart_car_kit.subView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import kr.co.open_it.smart_car_kit.R;

/**
 * Created by yongwookim on 2017-02-09.
 */

public class WebViewFragment extends Fragment {

    private WebView videoview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_web, container, false);
        System.out.println("--WebviewFragment onCreateView()");

        videoview = (WebView) v.findViewById(R.id.webview);
        videoview.getSettings().setLoadWithOverviewMode(true);
        videoview.getSettings().setUseWideViewPort(true);
        videoview.getSettings().setJavaScriptEnabled(true);
        videoview.loadUrl("http://www.open-it.co.kr");

        return v;
    }
    public void playWebView(String url) {
        videoview.loadUrl(url);
    }

    public void onStart() {
        super.onStart();
        System.out.println("--WebviewFragment onStart()");
    }
    public void onStop() {
        super.onStop();
        System.out.println("--WebviewFragment onStop()");
    }
    public void onResume() {
        super.onResume();
        System.out.println("--WebviewFragment onResume()");
    }
}
