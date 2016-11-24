package com.example.administrator.baidudemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity implements View.OnClickListener{
    private MapView mMapView = null;
    private BaiduMap baiduMap;
    private Button common,moon,mylocation;
    MapStatusUpdate msu;
    //定位相关
    public LocationClient mLocationClient;
    private MyLocationLister mLocationLister;
    private boolean isFirstIn = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        //初始化定位
        initLocation();
    }
    //初始化定位
    private void initLocation() {
        mLocationClient = new LocationClient(this);
        mLocationLister = new MyLocationLister();
        mLocationClient.registerLocationListener(mLocationLister);

        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    private void initView() {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.mapView);
        common = (Button) findViewById(R.id.btn_common);
        moon = (Button) findViewById(R.id.btn_moon);
        mylocation = (Button) findViewById(R.id.btn_mylocation);
        common.setOnClickListener(this);
        moon.setOnClickListener(this);
        mylocation.setOnClickListener(this);
        baiduMap = mMapView.getMap();
        msu = MapStatusUpdateFactory.zoomTo(15.0f);
        baiduMap.setMapStatus(msu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_common:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.btn_moon:
                baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.btn_mylocation:
                //开启定位
                baiduMap.setMyLocationEnabled(true);
                if(!mLocationClient.isStarted())
                    mLocationClient.start();
                break;
        }
    }
    //我的位置的监听
    private class MyLocationLister implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(data);

                if(isFirstIn) {
                    LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());//经纬度
                    msu = MapStatusUpdateFactory.newLatLng(latlng);
                    baiduMap.animateMapStatus(msu);
                    isFirstIn = false;
                }
        }
    }

    //下面的重写方法是为了让地图的生命周期和activity的生命周期保持一致

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        baiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }


}