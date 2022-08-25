package com.baidu.location.demo;

import android.app.ActionBar;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.demo.view.ChartUtil;
import com.baidu.location.demo.view.RoundProgressBar;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.bumptech.glide.Glide;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 单次定位
 */
public class MainActivity extends CheckPermissionsActivity {

    private TextView mProvince;
    private TextView mCity;
    private TextView mArea;
    private ImageView mbg;
    private LocationClient mLocClientOne = null;
    private TextView mpm10;
    private TextView mpm25;
    private TextView mno2;
    private TextView mso2;
    private TextView mo3;
    private TextView mco;
    private TextView mtemp;
    private TextView mweather;
    private TextView mtime;
    private TextView mweek;
    private String aqi;
    private String pm10;
    private String pm25;
    private String no2;
    private String so2;
    private String o3;
    private String co;
    private String temp;
    private String weather;
    private String time;
    private ArrayList<String> hum_list;
    private ArrayList<String> max_temp_list;
    private ArrayList<String> min_temp_list;
    private ArrayList<String> date_list;
    private List<String> xDataList1;
    private List<Entry> yDataList1;
    private List<String> xDataList2;
    private List<Entry> yDataList2;
    public String provinceStr;
    public String cityStr;
    public String areaStr;
    // 更新时期时间
    private Weektime week = new Weektime();
    private RoundProgressBar rpbAqi;
    //折线图
    private LineChart lineChart;// 声明图表控件
    private LineChart lineChart2;// 声明图表控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 隐藏状态栏
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        // 透明状态栏
        StatusBarUtil.transparencyBar(this);

        //绑定信息
        mProvince = findViewById(R.id.province);
        mCity = findViewById(R.id.city);
        mArea = findViewById(R.id.area);
        rpbAqi = findViewById(R.id.rpb_aqi);
        mpm10 = findViewById(R.id.tv_pm10);
        mpm25 = findViewById(R.id.tv_pm25);
        mno2 = findViewById(R.id.tv_no2);
        mso2 = findViewById(R.id.tv_so2);
        mo3 = findViewById(R.id.tv_o3);
        mco = findViewById(R.id.tv_co);
        mtemp = findViewById(R.id.tv_temperature);
        mweather = findViewById(R.id.tv_info);
        mtime = findViewById(R.id.tv_old_time);
        mweek = findViewById(R.id.tv_week);
        mbg = findViewById(R.id.bg);
        lineChart = findViewById(R.id.lineChart);
        lineChart2 = findViewById(R.id.lineChart2);
        //加载背景图片
        Glide.with(this).load(R.drawable.bg).into(mbg);
        //获取日期
        mweek.setText("星期"+ Weektime.StringData());
        startOneLocaton();

    }

    //获取今天的空气质量（精确到市）
    private void get_air(String city) {
        //使用Get异步请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //拼接访问地址
                .url("https://free-api.heweather.net/s6/air/now?key=3086e91d66c04ce588a7f538f917c7f4&location=" + city)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("调用失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    //回调的方法执行在子线程
                    JSONObject result_json= null;
                    try {
                        result_json = JSON.parseObject(response.body().string());
                        JSONArray jsonArray = result_json.getJSONArray("HeWeather6");
                        JSONObject object = jsonArray.getJSONObject(0);
                        String tem = object.getString("air_now_city");
                        try {
                            JSONObject jb = JSON.parseObject(tem);
                            aqi = jb.getString("aqi");
                            pm10 = jb.getString("pm10");
                            pm25 = jb.getString("pm25");
                            no2 = jb.getString("no2");
                            so2 = jb.getString("so2");
                            o3 = jb.getString("o3");
                            co = jb.getString("co");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mpm10.setText(pm10);
                        mpm25.setText(pm25);
                        mno2.setText(no2);
                        mso2.setText(so2);
                        mo3.setText(o3);
                        mco.setText(co);
                        showAirBasicData(aqi);
                    }
                });
            }
        });
    }

    //获取今天的天气和温度
    private void get_weather(String area) {
        //使用Get异步请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //拼接访问地址
                .url("https://free-api.heweather.net/s6/weather/now?key=3086e91d66c04ce588a7f538f917c7f4&location=" + area)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("调用失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    JSONObject result_json2= null;
                    try {
                        result_json2 = JSON.parseObject(response.body().string());
                        JSONArray jsonArray2 = result_json2.getJSONArray("HeWeather6");
                        JSONObject object2 = jsonArray2.getJSONObject(0);
                        // 天气和温度
                        String now_weather = object2.getString("now");
                        JSONObject json3 = JSON.parseObject(now_weather);
                        weather = json3.getString("cond_txt");
                        temp = json3.getString("tmp");
                        // 更新时间
                        String up_time = object2.getString("update");
                        JSONObject json4 = JSON.parseObject(up_time);
                        time = json4.getString("loc");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mweather.setText(weather);
                        mtemp.setText(temp);
                        mtime.setText("最近更新时间: " + time);
                    }
                });
            }
        });
    }
    //获取一周温湿度数据
    private void get_forecast(String area) {
        //使用Get异步请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //拼接访问地址
                .url("https://free-api.heweather.net/s6/weather/forecast?key=3086e91d66c04ce588a7f538f917c7f4&location=" + area)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("调用失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){//回调的方法执行在子线程。
                    JSONObject result_json3= null;
                    try {
                        result_json3 = JSON.parseObject(response.body().string());
                        JSONArray jsonArray3 = result_json3.getJSONArray("HeWeather6");
                        JSONObject object3 = jsonArray3.getJSONObject(0);
                        JSONArray forecast = object3.getJSONArray("daily_forecast");
//                        System.out.println((forecast));
                        hum_list = new ArrayList<String>();
                        max_temp_list = new ArrayList<String>();
                        min_temp_list = new ArrayList<String>();
                        date_list = new ArrayList<String>();
                        for(int i=0;i<forecast.size();i++){
                            JSONObject hum = forecast.getJSONObject(i);
                            hum_list.add(hum.get("hum").toString());
                            max_temp_list.add(hum.get("tmp_max").toString());
                            min_temp_list.add(hum.get("tmp_min").toString());
                            date_list.add(hum.get("date").toString().substring(6)); // 截取月日
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        plot_temp();
                        plot_hum();
                    }
                });
            }
        });
    }

    /**
     * 绘制温度曲线图
     */
    private void plot_temp(){
        xDataList1 = new ArrayList<>();// x轴数据源
        yDataList1 = new ArrayList<>();// y轴数据数据源
        for (int i = 0; i < 7; i++) {
            // x轴显示的数据
            xDataList1.add(date_list.get(i));
            // y轴显示的数据(平均温度)
            Float a1 = Float.parseFloat(max_temp_list.get(i));
            Float a2 = Float.parseFloat(min_temp_list.get(i));
            Float value = (a1 + a2) / 2;
            yDataList1.add(new Entry(value, i));
        }
        //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
        ChartUtil.showChart(MainActivity.this, lineChart, xDataList1, yDataList1, "", "平均温度","℃");
    }

    /**
     * 绘制湿度曲线图
     */
    private void plot_hum(){
        xDataList2 = new ArrayList<>();// x轴数据源
        yDataList2 = new ArrayList<>();// y轴数据数据源
        for (int i = 0; i < 7; i++) {
            // x轴显示的数据
            xDataList2.add(date_list.get(i));
            // y轴显示的数据
            yDataList2.add(new Entry(Float.parseFloat(hum_list.get(i)), i));
        }
        //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
        ChartUtil.showChart(MainActivity.this, lineChart2, xDataList2, yDataList2, "", "湿度","%");
    }
    /**
     * 启动单次定位
     */
    private void startOneLocaton() {
        mLocClientOne = new LocationClient(this);
        mLocClientOne.registerLocationListener(oneLocationListener);
        LocationClientOption locationClientOption = new LocationClientOption();
        // 可选，设置定位模式，默认高精度 LocationMode.Hight_Accuracy：高精度；
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置返回经纬度坐标类型，默认GCJ02
        locationClientOption.setCoorType("bd09ll");
        // 如果设置为0，则代表单次定位，即仅定位一次，默认为0
        // 如果设置非0，需设置1000ms以上才有效
        locationClientOption.setScanSpan(0);
        // 设置是否进行单次定位，单次定位时调用start之后会默认返回一次定位结果
        locationClientOption.setOnceLocation(true);
        //可选，设置是否使用gps，默认false
        locationClientOption.setOpenGps(true);
        // 可选，是否需要地址信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的地址信息，此处必须为true
        locationClientOption.setIsNeedAddress(true);
        // 设置定位参数
        mLocClientOne.setLocOption(locationClientOption);
        // 开启定位
        mLocClientOne.start();
    }


    /*****
     *
     * 单次定位回调监听
     *
     */
    public BDAbstractLocationListener oneLocationListener = new BDAbstractLocationListener() {
        /**
         * 定位请求回调函数
         * @param location 定位结果
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location) {
                return;
            }
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(latLng);
            StringBuffer sb = new StringBuffer(256);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("离线定位成功");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("服务端网络定位失败");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("无法获取有效定位依据导致定位失败，请开启GPS和网络后重启App");
            }

            provinceStr = location.getProvince();
            cityStr = location.getCity();
            areaStr = location.getDistrict();
            if (null != provinceStr) {
                mProvince.setText(provinceStr);
            }
            if (null != cityStr) {
                mCity.setText(cityStr);
            }
            if (null != areaStr) {
                mArea.setText(areaStr);
            }
            // 未打开GPS或者网络异常情况
            if (null == provinceStr) {
                mProvince.setText(sb.toString());
            }

            get_air(cityStr);
            get_weather(areaStr);
            get_forecast(areaStr);



        }
    };


    /**
     * 展示基础数据
     */
    private void showAirBasicData(String data) {
        rpbAqi.setMaxProgress(300);//最大进度，用于计算
        rpbAqi.setMinText("0");//设置显示最小值
        rpbAqi.setMinTextSize(32f);
        rpbAqi.setMaxText("300");//设置显示最大值
        rpbAqi.setMaxTextSize(32f);
        rpbAqi.setProgress(Float.parseFloat(data));//当前进度
        rpbAqi.setArcBgColor(getResources().getColor(R.color.arc_bg_color));//圆弧的颜色
        rpbAqi.setProgressColor(getResources().getColor(R.color.arc_progress_color));//进度圆弧的颜
        rpbAqi.setFirstText(data);//空气质量值
        rpbAqi.setFirstTextSize(64f);//第二行文本的字体大小
        rpbAqi.setMinText("0");
        rpbAqi.setMinTextColor(getResources().getColor(R.color.arc_progress_color));
    }




}
