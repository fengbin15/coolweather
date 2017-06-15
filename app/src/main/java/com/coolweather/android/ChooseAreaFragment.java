package com.coolweather.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.LogUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Drug on 2017/6/15.
 */

public class ChooseAreaFragment extends Fragment {
    //定义省市县三个级别
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    //进度对话框
    private ProgressDialog progressDialog;

    //标题显示控件
    private TextView titleText;

    private Button backButton;   //返回按钮

    private ListView listView;   //显示列表
    private ArrayAdapter<String> adapter;   //适配器
    private List<String> dataList = new ArrayList<>();  //数据列表
    private List<Province> provinceList;     //省列表
    private List<City> cityList;     //市列表
    private List<County> countyList;     //县列表

    private Province selectedProvince;   //选中的省
    private City selectedCity;     //选中的市

    private int currentLevel;    //当前选中的级别

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);  //添加碎片
        //获取布局中控件
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,
                dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel  == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
            }
        });
        //返回按钮的点击事件
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });

        queryProvince();
    }

    /**
     * 查询全国所有的省，优先从数据库中查询，没有再从服务器上查询
     */
    private void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class); // 从数据库中获取所有的省份
        if (provinceList.size() > 0){
            dataList.clear();   // 清除数据列表
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();  //更新适配器列表数据
            listView.setSelection(0);  //默认选中第一个
            currentLevel = LEVEL_PROVINCE;
        }else {  //如果数据库中没有数据，则从服务器上获取
            String address = "http://guolin.tech/api/china";  //访问地址
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询选中省中的所有的市，优先从数据库中查询，没有再从服务器上查询
     */
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.
                getId())).find(City.class);   //从数据库中获取选中省份的成是列表
        if (cityList.size() > 0){
            dataList.clear();   //清空数据列表
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china" + "/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
    /**
     * 查询选中市的所有县，优先从数据库中查询，没有再从服务器上查询
     */
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?", String.valueOf(
                selectedCity.getId())).find(County.class);
        LogUtil.d("TAG----", countyList.size() + "");
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china" + "/" + provinceCode
                    + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }
    /**
     * 根据传入的地址，从服务器上查询省市县的数据
     */
    private void queryFromServer(String address, final String type){
        //显示进度对话框
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程
                getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                        }
                    });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //获取返回的数据
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    //解析返回的数据
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type)){
                    LogUtil.d("ChooseFragment", responseText);
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                }else if ("county".equals(type)){
                    LogUtil.d("TAG-----", responseText);
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                //判断是否获取成功
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();   //关闭进度对话框
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());  //创建一个进度框
            progressDialog.setMessage("正在加载......");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度框
     */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}