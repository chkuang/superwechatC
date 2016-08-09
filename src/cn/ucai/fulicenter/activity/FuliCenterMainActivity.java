package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/8/1.
 */
public class FuliCenterMainActivity extends BaseActivity{
    private static final String TAG = FuliCenterMainActivity.class.getSimpleName();
    RadioButton rbNewGood,rbBoutique,rbCategory,rbCart,rbPersonalCenter;
    TextView tvCartHint;
    RadioButton[] mrbTabs;
    int index;
    int currentIndex;

    private static final int ACTION_LOGIN = 100;

    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    Fragment[] mFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulicenter_main);
        initView();
        initFragment();
        setListener();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,mNewGoodFragment)
                .add(R.id.fragment_container,mBoutiqueFragment)
                .add(R.id.fragment_container,mCategoryFragment)
                .hide(mBoutiqueFragment)
                .hide(mCategoryFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void setListener() {
        updateCartCountListener();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragment = new Fragment[5];
        mFragment[0] = mNewGoodFragment;
        mFragment[1] = mBoutiqueFragment;
        mFragment[2] = mCategoryFragment;
        mFragment[4] = mPersonalCenterFragment;
    }

    private void initView() {
        rbNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        rbBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        rbCategory = (RadioButton) findViewById(R.id.layout_category);
        rbCart = (RadioButton) findViewById(R.id.layout_cart);
        rbPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mrbTabs = new RadioButton[5];
        mrbTabs[0] = rbNewGood;
        mrbTabs[1] = rbBoutique;
        mrbTabs[2] = rbCategory;
        mrbTabs[3] = rbCart;
        mrbTabs[4] = rbPersonalCenter;
    }

    public void onCheckedChange(View view){
        switch (view.getId()){
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.layout_cart:
                index = 3;
                break;
            case R.id.layout_personal_center:
                if (DemoHXSDKHelper.getInstance().isLogined()){
                    index = 4;
                }else{
                    gotoLogin();
                }
                break;
        }
        Log.e(TAG,"index="+index+",currentIndex="+currentIndex);
        setFragment();
    }
    private void setFragment(){
        if (index!=currentIndex){
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragment[currentIndex]);
            if(!mFragment[index].isAdded()){
                trx.add(R.id.fragment_container,mFragment[index]);
            }
            trx.show(mFragment[index]).commit();
            setRadioButtonStatus(index);
            currentIndex = index;
        }
    }

    private void gotoLogin(){
        startActivityForResult(new Intent(this,LoginActivity.class),ACTION_LOGIN);
    }

    private void setRadioButtonStatus(int index) {
        for (int i=0;i<mrbTabs.length;i++){
            if (index==i){
                mrbTabs[i].setChecked(true);
            }else{
                mrbTabs[i].setChecked(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"onActivityResult");
        if(requestCode == ACTION_LOGIN){
            if(DemoHXSDKHelper.getInstance().isLogined()){
                index =4 ;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
        if (!DemoHXSDKHelper.getInstance().isLogined() && index==4){
            index=0;
        }
        setFragment();
        setRadioButtonStatus(currentIndex);
    }

     class UpdateCartNumReceiver extends BroadcastReceiver{
         @Override
         public void onReceive(Context context, Intent intent) {
             updateCartNum();
         }
     }
    UpdateCartNumReceiver mReceiver;
    private void updateCartCountListener(){
        mReceiver  = new UpdateCartNumReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        registerReceiver(mReceiver,filter);
    }

    private void updateCartNum() {
        int count = Utils.sumCartCount();
        if(!DemoHXSDKHelper.getInstance().isLogined() || count==0){
            tvCartHint.setText(String.valueOf(0));
            tvCartHint.setVisibility(View.GONE);
        }else{
            Log.e(TAG,"count ="+count);
            tvCartHint.setText(String.valueOf(count));
            tvCartHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }
}
