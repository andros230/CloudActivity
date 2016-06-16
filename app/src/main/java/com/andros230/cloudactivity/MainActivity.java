package com.andros230.cloudactivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;

import java.util.ArrayList;

public class MainActivity extends Activity implements CloudSearch.OnCloudSearchListener, AMap.OnMarkerClickListener {
    private MapView mMapView;
    private AMap mAMap;
    private CloudSearch mCloudSearch;
    private String mTableID = "53c4a222e4b0837614b6a8be";
    private String mId = "2"; // 用户table 行编号
    private String TAG = "MainActivity";
    private Marker mCloudIDMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    //初始化AMap对象
    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mCloudSearch = new CloudSearch(this);
        mCloudSearch.setOnCloudSearchListener(this);
        mAMap.setOnMarkerClickListener(this);
    }


    public void searchById(View view) {
        mCloudSearch.searchCloudDetailAsyn(mTableID, mId);
    }


    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int rCode) {
        if (rCode == 1000 && item != null) {
            if (mCloudIDMarker != null) {
                mCloudIDMarker.destroy();
            }
            mAMap.clear();
            LatLng position = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
            mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(position, 18, 0, 30)));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(position);
            markerOptions.title(item.getTitle());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mCloudIDMarker = mAMap.addMarker(markerOptions);
            Log.d(TAG, "_id" + item.getID());
            Log.d(TAG, "_location" + item.getLatLonPoint().toString());
            Log.d(TAG, "_name" + item.getTitle());
            Log.d(TAG, "_address" + item.getSnippet());
            Log.d(TAG, "_caretetime" + item.getCreatetime());
            Log.d(TAG, "_updatetime" + item.getUpdatetime());
            Log.d(TAG, "_distance" + item.getDistance());
        } else {
            Log.e(TAG, rCode + "");
        }
    }

    @Override
    public void onCloudSearched(CloudResult cloudResult, int rCode) {
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }
}
