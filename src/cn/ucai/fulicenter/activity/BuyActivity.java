package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.view.DisplayUtils;

public class BuyActivity extends Activity {
    BuyActivity mContext;
    EditText etOrderName,etOrderPhone,etOrderStreet;
    Spinner spinProvince;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =this;
        setContentView(R.layout.activity_buy);
        initView();
    }

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"填写收货地址");
        etOrderName = (EditText) findViewById(R.id.et_order_name);
        etOrderPhone = (EditText) findViewById(R.id.et_order_phone);
        etOrderStreet = (EditText) findViewById(R.id.et_order_street);
        spinProvince = (Spinner) findViewById(R.id.spin_order_province);
    }
}
