package com.example.maxenia.knowweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.maxenia.knowweather.WeatherBean.HeWeather5Bean;
import com.example.maxenia.knowweather.impl.RequestWeatherImpl;
import com.example.maxenia.knowweather.ui.SettingActivity;
import com.example.maxenia.knowweather.ui.SuggestActivity;
import com.example.maxenia.knowweather.util.Constant;
import com.example.maxenia.knowweather.util.GetImageFromNet;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnChartGestureListener, OnChartValueSelectedListener {
    // Content View Elements
    private ImageView mIV_refresh;
    private TextView mTV_refresh_time;
    private LinearLayout mLL_refresh;
    private TextView mTV_city;
    private TextView mTV_date;
    private TextView mTV_MR_MS;
    private ImageView mIV_weather_icon;
    private TextView mTV_temperature;
    private TextView mTV_weather;
    private LinearLayout mLL_today;
    private TextView mTV_wind_direct;
    private TextView mTV_wind_power;
    private TextView mTV_nowTemp;
    private TextView mTV_PM;
    private TextView[] mTV_dates = new TextView[5];
    private int[] mTV_dates_id = {R.id.TV_date0, R.id.TV_date1, R.id.TV_date2, R.id.TV_date3, R.id.TV_date4};
    //初始化LocationClient类 和定位监听器
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private ProgressDialog mDialog;
    private String mCity = "北京市";   //默认显示北京市
    private boolean loc_success = false;
    private Handler mHandler = new Handler();

    private Retrofit mRetrofit;
    private RequestWeatherImpl mRequestImpl;

    private HeWeather5Bean weather5Bean;     // 天气Gson对象
    private TextView mTV_futureDate;        //未来日期
    private ImageView mIV_futureIcon;       //未来天气图标
    private TextView mTV_futureTemp;        //未来天气温度
    private TextView mTV_futureWeather;     //未来天气
    private View futureView;                //未来天气视图

    private LineChart mTempChart;               //折线图
    private ArrayList<Entry> mChartMaxTemps = new ArrayList<>();  //折线图数据集 最高温
    private ArrayList<Entry> mChartMinTemps = new ArrayList<>();//折线图数据集   最低温

    private ArrayList<String> mSuggestList = new ArrayList<>(); //保存指标建议页面列表数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        bindView();
        initLocation();     //定位客户端参数设置
        initRetrofit();
        initDialog();       //定位过程中显示对话框
        initLineChart();
        //Log.i(Constant.TAG, "开始定位");
        //开启定位
        mLocationClient.start();

    }

    private void bindView() {

        mIV_refresh = (ImageView) findViewById(R.id.IV_refresh);
        mTV_refresh_time = (TextView) findViewById(R.id.TV_refresh_time);
        mLL_refresh = (LinearLayout) findViewById(R.id.LL_refresh);
        mLL_refresh.setOnClickListener(this);

        mTV_city = (TextView) findViewById(R.id.TV_city);
        mTV_date = (TextView) findViewById(R.id.TV_date);
        mTV_MR_MS = (TextView) findViewById(R.id.TV_mr_ms);
        mIV_weather_icon = (ImageView) findViewById(R.id.IV_weather_icon);
        mTV_temperature = (TextView) findViewById(R.id.TV_temperature);
        mTV_weather = (TextView) findViewById(R.id.TV_weather);
        mLL_today = (LinearLayout) findViewById(R.id.LL_today);
        mLL_today.setOnClickListener(this);
        mTV_wind_direct = (TextView) findViewById(R.id.TV_wind_direct);
        mTV_wind_power = (TextView) findViewById(R.id.TV_wind_power);
        mTV_nowTemp = (TextView) findViewById(R.id.TV_NOW_TEMP);
        mTV_PM = (TextView) findViewById(R.id.TV_PM);
        //天气预报每天日期,并设置监听点击
        for (int i = 0; i < mTV_dates.length; i++) {
            mTV_dates[i] = (TextView) findViewById(mTV_dates_id[i]);
            mTV_dates[i].setOnClickListener(this);
        }
        bindFutureView();

    }

    private void bindFutureView() {
        //未来几天天气显示数据
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        futureView = layoutInflater.inflate(R.layout.future_day, null);
        mTV_futureDate = (TextView) futureView.findViewById(R.id.TV_future_date);
        mIV_futureIcon = (ImageView) futureView.findViewById(R.id.IV_future_icon);
        mTV_futureTemp = (TextView) futureView.findViewById(R.id.TV_future_temp);
        mTV_futureWeather = (TextView) futureView.findViewById(R.id.TV_future_weather);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LL_refresh:
                //设置旋转动画
                RotateAnimation rotate = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                        RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(500);        //转一圈0.5s
                rotate.setRepeatCount(3);       //重复三次
                mIV_refresh.startAnimation(rotate);
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mTV_refresh_time.setText(getNowTime());
                        //重新获取对应城市的网络数据
                        requestWeatherData(mCity.replace("市", ""));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                break;
            case R.id.LL_today:
                StringBuilder strBuilder = new StringBuilder();
                for (String suggest : mSuggestList) {
                    strBuilder.append(suggest).append("\n");
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(mCity + getString(R.string.today_suggestion))
                        .setIcon(mIV_weather_icon.getDrawable())
                        .setMessage(strBuilder)
                        .create()
                        .show();
                break;
            default:        //除此之外，点击的是未来几天天气情况
                AlertDialog.Builder futureDialog = new AlertDialog.Builder(this);
                futureDialog.setView(null);
                bindFutureView();

                for (int i = 0; i < mTV_dates_id.length; i++) {
                    if (view.getId() == mTV_dates_id[i]) {
                        //日期
                        String futureDate = weather5Bean.getDaily_forecast().get(i).getDate();
                        mTV_futureDate.setText(futureDate);
                        //天气图标
                        String code_d = weather5Bean.getDaily_forecast().get(i).getCond().getCode_d();
                        setWeatherIcon(mIV_futureIcon, code_d);
                        //天气温度范围
                        String maxTemp = weather5Bean.getDaily_forecast().get(i).getTmp().getMax();
                        String minTemp = weather5Bean.getDaily_forecast().get(i).getTmp().getMin();
                        String temp = maxTemp + "°/" + minTemp + "°";
                        mTV_futureTemp.setText(temp);
                        //天气
                        String weather = weather5Bean.getDaily_forecast().get(i).getCond().getTxt_d();
                        mTV_futureWeather.setText(weather);
                        break;
                    }
                }

                futureDialog.setTitle(mCity)
                        .setIcon(mIV_futureIcon.getDrawable())
                        .setView(futureView)
                        .create().show();

        }
    }

    //定位过程中显示对话框
    private void initDialog() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.getting_location) + "...");
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //设置屏幕外点击无效
        mDialog.setCancelable(false);
        mDialog.show();
    }

    //初始化retrofit
    private void initRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                //增加返回值为Gson的支持(以实体类返回)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mRequestImpl = mRetrofit.create(RequestWeatherImpl.class);//这里采用的是Java的动态代理模式
    }

    //初始化折线图控件
    private void initLineChart() {
        mTempChart = (LineChart) findViewById(R.id.LC_temperature);

        mTempChart.setOnChartGestureListener(this);     //手势滑动事件
        mTempChart.setOnChartValueSelectedListener(this);   //数值选择监听
        mTempChart.setDrawGridBackground(false);        //设置后台绘制

        // 不需要文本描述
        mTempChart.getDescription().setEnabled(false);

        // 支持触摸事件
        mTempChart.setTouchEnabled(true);

        // 设置缩放和滑动
        mTempChart.setDragEnabled(true);
        mTempChart.setScaleEnabled(true);
        mTempChart.setPinchZoom(true);
        //X轴
        XAxis xAxis = mTempChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(5);    //X轴最大值
        xAxis.setAxisMinimum(1);    //X轴最小值
        xAxis.setGranularity(1);    //间隔1

        LimitLine ll1 = new LimitLine(30, getString(R.string.high_temp));        //高温线
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(15f);
        ll1.setTextColor(Constant.mainColor);

        LimitLine ll2 = new LimitLine(0, getString(R.string.low_temp));
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(15f);
        ll2.setTextColor(Constant.mainColor);

        YAxis leftAxis = mTempChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(50f);
        leftAxis.setAxisMinimum(-20f);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mTempChart.getAxisRight().setEnabled(false);

        mTempChart.animateY(2500);          //设置Y轴数据动画，2.5秒
    }

    //定位客户端参数设置
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 0;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        loc_success = false;
    }

    //定义位置监听器类
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mDialog.dismiss();
            //Receive Location
            switch (location.getLocType()) {
                case BDLocation.TypeGpsLocation:    // GPS定位成功
                case BDLocation.TypeNetWorkLocation: // 网络定位成功
                case BDLocation.TypeOffLineLocation:    // 离线定位成功
                    if (!loc_success) {                 //避免重复定位
                        mCity = location.getCity();
                        mTV_city.setText(mCity);
                        //Log.i(Constant.TAG, "定位成功，所在城市：" + mCity);
                        //定位成功，请求对应城市的网络数据
                        requestWeatherData(mCity.replace("市", ""));
                    }
                    loc_success = true;
                    break;
                case BDLocation.TypeServerError:
                case BDLocation.TypeNetWorkException:
                case BDLocation.TypeCriteriaException:
                    // Log.i(Constant.TAG, "定位失败");
                    loc_success = false;
                    //定位失败，默认获取北京的天气数据
                    mTV_city.setText(mCity);
                    requestWeatherData(mCity.replace("市", ""));  //定位失败，默认北京
                    break;
            }
        }
    }

    //请求天气数据，响应返回
    public void requestWeatherData(String city) {
        Call<WeatherBean> call = mRequestImpl.getWeather(city, Constant.WEATHER_KEY);
        call.enqueue(new Callback<WeatherBean>() {
            @Override
            public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                //请求天气数据成功
                weather5Bean = response.body().getHeWeather5().get(0);
                //日期
                mTV_date.setText(weather5Bean.getDaily_forecast().get(0).getDate());
                //日出日落时间
                String sr = weather5Bean.getDaily_forecast().get(0).getAstro().getSr();
                String ss = weather5Bean.getDaily_forecast().get(0).getAstro().getSs();
                String srss = getString(R.string.sunrise_time) + sr + "\n" +
                        getString(R.string.sunset_time) + ss;
                mTV_MR_MS.setText(srss);
                //设置天气对应的网络图片
                String code_d = weather5Bean.getDaily_forecast().get(0).getCond().getCode_d();
                setWeatherIcon(mIV_weather_icon, code_d);
                //当前实时气温
                String nowTemp = weather5Bean.getNow().getTmp() + "°";
                mTV_nowTemp.setText(nowTemp);
                //设置温度
                String maxTemp = weather5Bean.getDaily_forecast().get(0).getTmp().getMax();
                String minTemp = weather5Bean.getDaily_forecast().get(0).getTmp().getMin();
                String temp = maxTemp + "°/" + minTemp + "°";
                mTV_temperature.setText(temp);
                //空气污染物（注意，有的城市没有这个信息，需做判断）
                String qlty = "";      //空气指数
                if (weather5Bean.getAqi() != null) {
                    qlty = "|" + "空气" + weather5Bean.getAqi().getCity().getQlty();
                    String pm25 = weather5Bean.getAqi().getCity().getPm25();
                    String pm10 = weather5Bean.getAqi().getCity().getPm10();
                    mTV_PM.setText("PM25: " + pm25 + "\nPM10: " + pm10);
                }
                //天气，空气指数
                String txt_d = weather5Bean.getDaily_forecast().get(0).getCond().getTxt_d();
                String txt_qlty = txt_d + qlty;
                mTV_weather.setText(txt_qlty);
                //风向
                String direct = "风向" + " | " + weather5Bean.getDaily_forecast().get(0).getWind().getDir();
                mTV_wind_direct.setText(direct);
                //风力
                String power = "风力" + " | " + weather5Bean.getDaily_forecast().get(0).getWind().getSc()
                        + "级";
                mTV_wind_power.setText(power);

                //添加折线图数据
                int days = weather5Bean.getDaily_forecast().size();
                Log.i(Constant.TAG, days + "");
                for (int i = 0; i < days; i++) {
                    temp = weather5Bean.getDaily_forecast().get(i).getTmp().getMax();
                    mChartMaxTemps.add(new Entry(i + 1, Integer.parseInt(temp)));
                    temp = weather5Bean.getDaily_forecast().get(i).getTmp().getMin();
                    mChartMinTemps.add(new Entry(i + 1, Integer.parseInt(temp)));
                    mTV_dates[i].setText(weather5Bean.getDaily_forecast().get(i).getDate());

                }
                setData(mChartMaxTemps, mChartMinTemps);

                //各方面指标建议
                mSuggestList.clear();
                mSuggestList.add("空气: " + weather5Bean.getSuggestion().getAir());
                mSuggestList.add("感觉: " + weather5Bean.getSuggestion().getComf());
                mSuggestList.add("洗车: " + weather5Bean.getSuggestion().getCw());
                mSuggestList.add("感冒: " + weather5Bean.getSuggestion().getFlu());
                mSuggestList.add("运动: " + weather5Bean.getSuggestion().getSport());

            }

            @Override
            public void onFailure(Call<WeatherBean> call, Throwable t) {

            }
        });
    }

    //设置天气对应的网络图片
    private void setWeatherIcon(final ImageView iView, String code) {
        final String imgUrl = "http://files.heweather.com/cond_icon/" + code + ".png";
        new Thread() {
            @Override
            public void run() {
                final Bitmap map = GetImageFromNet.getBitmapFromUrl(imgUrl);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iView.setImageBitmap(map);
                    }
                });
            }
        }.start();
    }

    //获取当前时间
    private String getNowTime() {
        SimpleDateFormat simple = new SimpleDateFormat(getString(R.string.date_format), Locale.CHINESE);
        return simple.format(new Date());
    }

    private void setData(ArrayList<Entry> maxValues, ArrayList<Entry> minValues) {

        LineDataSet maxDataSet;
        LineDataSet minDataSet;

        if (mTempChart.getData() != null &&
                mTempChart.getData().getDataSetCount() > 0) {
            maxDataSet = (LineDataSet) mTempChart.getData().getDataSetByIndex(0);
            minDataSet = (LineDataSet) mTempChart.getData().getDataSetByIndex(1);
            maxDataSet.setValues(maxValues);
            minDataSet.setValues(minValues);
            mTempChart.getData().notifyDataChanged();
            mTempChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            maxDataSet = new LineDataSet(maxValues, getString(R.string.highest_temp));
            setLineDataSet(maxDataSet, Constant.mainColor);
            minDataSet = new LineDataSet(minValues, getString(R.string.lowest_temp));
            setLineDataSet(minDataSet, Color.BLUE);
            minDataSet.setFillColor(Color.WHITE);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(maxDataSet); // add the datasets
            dataSets.add(minDataSet); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mTempChart.setData(data);
        }
    }

    private void setLineDataSet(LineDataSet set, int color) {

        // set the line to be drawn like this "- - - - - -"
        set.enableDashedLine(10f, 5f, 0f);
        set.enableDashedHighlightLine(10f, 5f, 0f);
        set.setColor(color);
        set.setCircleColor(color);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        set.setFormLineWidth(1f);
        set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        set.setFormSize(15.f);
        set.setFillColor(color);
        set.setFillAlpha(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.MN_suggest:
                //选择菜单中指标建议选项
                Intent intent = new Intent(this, SuggestActivity.class);
                intent.putStringArrayListExtra("suggest", mSuggestList);
                startActivity(intent);
                break;
            case R.id.MN_setting:
                //选择菜单中设置选项
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}



