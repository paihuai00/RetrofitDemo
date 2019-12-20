package com.csx.retrofitdemo;

import java.util.List;

/**
 * create by cuishuxiang
 *
 * @date : 2019/1/15
 * @description:
 */
public class WeatherBean {

    private List<HeWeather5Bean> HeWeather5;

    public List<HeWeather5Bean> getHeWeather5() {
        return HeWeather5;
    }

    public void setHeWeather5(List<HeWeather5Bean> HeWeather5) {
        this.HeWeather5 = HeWeather5;
    }

    public static class HeWeather5Bean {
        /**
         * status : permission denied
         */

        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "HeWeather5Bean{" + "status='" + status + '\'' + '}';
        }
    }

    @Override
    public String toString() {
        return "WeatherBean{" + "HeWeather5=" + HeWeather5.toString() + '}';
    }
}
