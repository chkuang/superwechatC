package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/8/3.
 */
public class CartFragment extends Fragment {
    private final  static String TAG = CartFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;
    LinearLayoutManager mLinearLayoutManager;
    CartAdapter mAdapter;
    ArrayList<CartBean> mCartList;
    TextView tvHint;
    TextView tvSumPrice;
    TextView tvSavePrice;
    TextView tvBuy;
    TextView tvNothing;

    int pageId = 0;
    int action = I.ACTION_DOWNLOAD;

    int sumPrice=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = (FuliCenterMainActivity) getContext();
        View layout = View.inflate(mContext,R.layout.fragment_cart,null);
        mCartList = new ArrayList<CartBean>();
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setUpdateCartListener();
        setPullUpRefreshListener();
        setBuyListener();
    }

    private void setBuyListener() {
        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DemoHXSDKHelper.getInstance().isLogined()&& sumPrice>0){
                    startActivity(new Intent(mContext,BuyActivity.class));
                }
            }
        });
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a = RecyclerView.SCROLL_STATE_DRAGGING;
                int b = RecyclerView.SCROLL_STATE_IDLE;
                int c = RecyclerView.SCROLL_STATE_SETTLING;
                Log.e(TAG,"newState="+newState);
                if (newState==RecyclerView.SCROLL_STATE_IDLE && lastItemPosition==mAdapter.getItemCount()-1){
                    if (mAdapter.isMore()){
                        action = I.ACTION_PULL_UP;
                        pageId +=I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mLinearLayoutManager.findFirstVisibleItemPosition();
                int l = mLinearLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG,"f="+f+","+"l="+l);
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findLastVisibleItemPosition()==0);
                if (f==-1 || l == -1){
                    lastItemPosition = mAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                pageId=0;
                mSwipeRefreshLayout.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }

    private void initData() {
        List<CartBean> cartList = FuliCenterApplication.getInstance().getCartList();
        mCartList.clear();
        mCartList.addAll(cartList);
        mSwipeRefreshLayout.setRefreshing(false);
        tvHint.setVisibility(View.GONE);
        mAdapter.setMore(true);
        if (mCartList!=null && mCartList.size()>0){
            if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN){
                mAdapter.initItem(mCartList);
            }else{
                mAdapter.addItem(mCartList);
            }
            if (mCartList.size()<I.PAGE_SIZE_DEFAULT){
                mAdapter.setMore(false);
            }
            tvNothing.setVisibility(View.GONE);

        }else{
            mAdapter.setMore(false);
            tvNothing.setVisibility(View.VISIBLE);
        }
        sumPrice();
    }
    private void initView(View layout){
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_cart);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CartAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
        tvSumPrice = (TextView) layout.findViewById(R.id.tv_cart_sum_price);
        tvSavePrice = (TextView) layout.findViewById(R.id.tv_cart_save_price);
        tvBuy = (TextView) layout.findViewById(R.id.tv_cart_buy);
        tvNothing = (TextView) layout.findViewById(R.id.tv_nothing);
        tvNothing.setVisibility(View.VISIBLE);
    }

    class UpdateCartReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    UpdateCartReceiver mReceiver;
    private void setUpdateCartListener(){
        mReceiver = new UpdateCartReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        filter.addAction("update_user");
        mContext.registerReceiver(mReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver!=null){
            mContext.unregisterReceiver(mReceiver);
        }
    }

    private void sumPrice(){
        Log.e(TAG,"mCartList = "+mCartList.size());
        if(mCartList!=null && mCartList.size()>0){
            sumPrice=0;
            int rankPrice = 0;
            for (CartBean cart : mCartList){
                GoodDetailsBean good = cart.getGoods();
                Log.e(TAG,"goodCurrentPrice = "+good.getCurrencyPrice());
                if (good!=null && cart.isChecked()){
                    sumPrice+=convertPrice(good.getCurrencyPrice())*cart.getCount();
                    rankPrice+=convertPrice(good.getRankPrice())*cart.getCount();
                }
            }
            tvSumPrice.setText("合计：￥"+sumPrice);
            tvSavePrice.setText("节省：￥"+(sumPrice-rankPrice));
            Log.e(TAG,String.valueOf(sumPrice));
        }else{
            tvSavePrice.setText("合计：￥00.00");
            tvSavePrice.setText("节省:￥00.00");
        }
        Log.e(TAG,tvSumPrice.getText().toString());
    }
    private int convertPrice(String price){
        price = price.substring(1);
        return Integer.parseInt(price);
    }
}
