package kr.co.open_it.smart_car_kit.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yongwookim on 2017-02-01.
 */

public class MqttHandler implements MqttCallbackExtended  {

    private static final String TAG = "MqttHandler";
    private MqttAndroidClient mMqttAndroidClient;

    private String mServerUri = null;
    private String mClientId = null;

    private String pTopic = null;
    private String sTopic = null;

    private Context mContext;

    public String distance = null;
    public String temperature = null;
    public String humidity = null;
    public String brightness = null;

    private Boolean updateOk = false;
    private Boolean connectOk = false;

    public MqttHandler(Context context) {
        mContext = context;
    }
    @SuppressLint("oHardwareIds")
    public void initConnection(String mqttURL, String pubTopic, String subTopic) {
        mServerUri = mqttURL;
        pTopic = pubTopic;
        sTopic = subTopic;

        mClientId = "";
        mMqttAndroidClient = new MqttAndroidClient(mContext, mServerUri, mClientId);
        mMqttAndroidClient.setCallback(this);
        connectToServer();
    }
    private void connectToServer() {
        try {
            IMqttToken token = mMqttAndroidClient.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "mqtt connect successfull. Now Try SUBSCRIBE Topic : " + sTopic);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (exception != null) {
                        Log.e(TAG, "mqtt connect failure with exception : " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    public void subscribeToTopic() {
        try {
            mMqttAndroidClient.subscribe(sTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Subscribed to topic : " + sTopic);
                    setConnectOk(true);
                    publishMessage("status");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "topic subscription failed for topic : " + sTopic);
                }
            });

        } catch (MqttException ex) {
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect) {
            Log.d(TAG, "reconnect is true . So subscribing to topic again");
            // Because Clean Session is true, we need to re-subscribe
            subscribeToTopic();
        } else {
            Log.d(TAG, "Connected to MQTT server");
        }
    }
    @Override
    public void connectionLost(Throwable cause) {
        if (cause != null) {
            Log.e(TAG, "Connection to MQtt is lost due to " + cause.getMessage());
        }
        //connectToServer();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "Message arrived : " + message + " from topic : " + topic);
        String text = message.toString();
        if (!TextUtils.isEmpty(text)) {
            handlerChatMessage(text, topic);
        }

    }
    private void handlerChatMessage(String message, String topic ) {
        try {
            message = "[" +message + "]";

            JSONArray jarray = new JSONArray(message);

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);
                distance = String.valueOf(jObject.getInt("distance"));
                temperature = String.valueOf(jObject.getInt("temperature"));
                humidity = String.valueOf(jObject.getInt("humidity"));
                brightness = String.valueOf(jObject.getInt("light"));
            }
            setUpdateOk(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            if (token != null && token.getMessage() != null) {
                Log.d(TAG, "Message : " + token.getMessage().toString() + " delivered");
            }
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void disConnectToServer() {
        try {
            IMqttToken token = mMqttAndroidClient.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "mqtt disConnect successfull.");
                    setConnectOk(false);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if (exception != null) {
                        Log.e(TAG, "disconnect failure with exception : " + exception.getMessage());
                        exception.printStackTrace();
                    }
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    public void publishMessage(String messageText) {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(messageText.getBytes());
            message.setQos(0);
            setUpdateOk(false);
            mMqttAndroidClient.publish(pTopic, message);
            if (mMqttAndroidClient.isConnected()) {
                System.err.println("Publishing OK : " + pTopic + " : " + messageText);
            }
        } catch (MqttException e) {
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getStatusData() {
        setUpdateOk(false);
        publishMessage("status");
    }
    public Boolean getConnectOK() {
        return connectOk;
    }
    public void setConnectOk(Boolean bool) {
        this.connectOk = bool;
    }
    public Boolean getUpdateOk() {
        return updateOk;
    }
    public void setUpdateOk(Boolean bool) {
        this.updateOk = bool;
    }
}