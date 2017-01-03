package com.example.maxenia.knowweather.impl;

import com.example.maxenia.knowweather.WeatherBean;
import com.example.maxenia.knowweather.util.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by maxenia on 2016/12/21.
 */
public interface RequestWeatherImpl {

    //https://free-api.heweather.com/  ----baseUrl
    //v5/weather?                      ----get地址
    //参数  city,  key
    @GET(Constant.GET_URL)
    Call<WeatherBean> getWeather(@Query("city") String city,@Query("key") String key);

}
