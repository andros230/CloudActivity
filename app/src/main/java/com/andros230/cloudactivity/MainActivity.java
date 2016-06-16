package com.andros230.cloudactivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;

import java.util.List;

//周边搜索
public class MainActivity extends Activity implements CloudSearch.OnCloudSearchListener, AMap.OnMarkerClickListener {
    private MapView mMapView;
    private AMap mAMap;
    private CloudSearch mCloudSearch;
    private String mTableID = "53c4a222e4b0837614b6a8be";
    private String TAG = "MainActivity";
    private LatLonPoint mCenterPoint = new LatLonPoint(31.184609, 121.552734); // 周边搜索中心点
    private String mKeyWord = "上海"; // 搜索关键字
    private CloudSearch.Query mQuery;
    private List<CloudItem> mCloudItems;
    private CloudOverlay mPoiCloudOverlay;

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


    public void searchByBound(View view) {
        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(
                mCenterPoint.getLatitude(), mCenterPoint.getLongitude()), 4000);
        try {
            mQuery = new CloudSearch.Query(mTableID, mKeyWord, bound);
            mQuery.setPageSize(10);
            CloudSearch.Sortingrules sorting = new CloudSearch.Sortingrules("_id", false);
            mQuery.setSortingrules(sorting);
            mCloudSearch.searchCloudAsyn(mQuery);// 异步搜索
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCloudSearched(CloudResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(mQuery)) {
                    mCloudItems = result.getClouds();

                    if (mCloudItems != null && mCloudItems.size() > 0) {
                        mAMap.clear();
                        mPoiCloudOverlay = new CloudOverlay(mAMap, mCloudItems);
                        mPoiCloudOverlay.removeFromMap();
                        mPoiCloudOverlay.addToMap();
                        // mPoiCloudOverlay.zoomToSpan();
                        for (CloudItem item : mCloudItems) {
                            Log.d(TAG, "_id " + item.getID());
                            Log.d(TAG, "_location " + item.getLatLonPoint().toString());
                            Log.d(TAG, "_name " + item.getTitle());
                            Log.d(TAG, "_address " + item.getSnippet());
                            Log.d(TAG, "_caretetime " + item.getCreatetime());
                            Log.d(TAG, "_updatetime " + item.getUpdatetime());
                            Log.d(TAG, "_distance " + item.getDistance());
                        }
                        if (mQuery.getBound().getShape().equals(CloudSearch.SearchBound.BOUND_SHAPE)) {// 圆形
                            mAMap.addCircle(new CircleOptions().center(new LatLng(mCenterPoint.getLatitude(), mCenterPoint.getLongitude())).radius(5000).strokeColor(Color.RED).fillColor(Color.argb(50, 1, 1, 1)).strokeWidth(5));
                            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCenterPoint.getLatitude(), mCenterPoint.getLongitude()), 12));
                        }

                    } else {
                        Log.d(TAG, "没有搜索到相关数据！");
                    }
                }
            } else {
                Log.d(TAG, "没有搜索到相关数据！");
            }
        }
    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail item, int rCode) {
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }
}
