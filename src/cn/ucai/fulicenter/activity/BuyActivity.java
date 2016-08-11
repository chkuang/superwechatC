package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.view.DisplayUtils;


public class BuyActivity extends FragmentActivity implements PaymentHandler{
    BuyActivity mContext;
    EditText etOrderName,etOrderPhone,etOrderStreet;
    Spinner spinProvince;
    Button btnBuy;

    private static String URL = "http://218.244.151.190/demo/charge";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =this;
        setContentView(R.layout.activity_buy);
        initView();
        setListener();
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});

        // 提交数据的格式，默认格式为json
        // PingppOne.CONTENT_TYPE = "application/x-www-form-urlencoded";
        PingppOne.CONTENT_TYPE = "application/json";

        PingppLog.DEBUG = true;
    }
    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"填写收货地址");
        etOrderName = (EditText) findViewById(R.id.et_order_name);
        etOrderPhone = (EditText) findViewById(R.id.et_order_phone);
        etOrderStreet = (EditText) findViewById(R.id.et_order_street);
        spinProvince = (Spinner) findViewById(R.id.spin_order_province);
        btnBuy = (Button) findViewById(R.id.btn_buy);
    }

    private void setListener() {
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etOrderName.getText().toString();
                if (TextUtils.isEmpty(name)){
                    etOrderName.setError("收货姓名不能为空");
                    etOrderName.requestFocus();
                    return;
                }
                String mobile = etOrderPhone.getText().toString();
                if (TextUtils.isEmpty(mobile)){
                    etOrderPhone.setError("手机号码不能为空");
                    etOrderPhone.requestFocus();
                    return;
                }
                if (!mobile.matches("[\\d]{11}")){
                    etOrderPhone.setError("手机号码格式有错误");
                    etOrderPhone.requestFocus();
                    return;
                }
                String area = spinProvince.getSelectedItem().toString();
                if (TextUtils.isEmpty(area)){
                    Toast.makeText(BuyActivity.this, "收货地区不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address = etOrderStreet.getText().toString();
                if (TextUtils.isEmpty(address)){
                    etOrderStreet.setError("街道地址不能为空");
                    etOrderStreet.requestFocus();
                    return;
                }
                gotoStatements();

            }
        });
    }
    private int convertPrice(String price){
        price = price.substring(1);
        return Integer.parseInt(price);
    }

    private void gotoStatements() {
        // 产生个订单号
        String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                .format(new Date());

        // 计算总金额（以分为单位）
        int amount = 0;
        JSONArray billList = new JSONArray();


        List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        for (CartBean cart:cartList){
            GoodDetailsBean goods = cart.getGoods();
            if (goods!=null &&cart.isChecked()){
                amount+=convertPrice(goods.getRankPrice())*cart.getCount();
                billList.put(goods.getGoodsName() + "x"+cart.getCount());
            }
        }
        // 构建账单json对象
        JSONObject bill = new JSONObject();
        // 自定义的额外信息 选填
        JSONObject extras = new JSONObject();
        try {
            extras.put("extra1", "extra1");
            extras.put("extra2", "extra2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            bill.put("order_no", orderNo);
            bill.put("amount", amount);
            bill.put("extras", extras);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //壹收款: 创建支付通道的对话框
        PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, this);
    }

    @Override
    public void handlePaymentResult(Intent intent) {

    }
}
