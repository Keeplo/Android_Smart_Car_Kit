package kr.co.open_it.smart_car_kit.model;

/**
 * Created by yongwookim on 2017-02-09.
 */

public class User_Data {
    public String webURL;
    public String MqttURL;
    public String pubTopic;
    public String subTopic;

    public User_Data(String webURL, String MqttURL, String pubTopic, String subTopic) {
        this.webURL = webURL;
        this.MqttURL = MqttURL;
        this.pubTopic = pubTopic;
        this.subTopic = subTopic;
    }
}
