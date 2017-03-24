package kr.co.open_it.smart_car_kit.model;

/**
 * Created by yongwookim on 2017-02-09.
 */

public class Status_Data {
        public String temperature;
        public String humidity;
        public String brightness;
        public String distance;

        public Status_Data(String temperature, String humidity, String brightness, String subTopic) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.brightness = brightness;
            this.distance = subTopic;
        }
}