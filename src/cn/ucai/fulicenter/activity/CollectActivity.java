package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

public class CollectActivity extends Activity {
    private final  static String TAG = NewGoodFragment.class.getSimpleName();
    CollectActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    CollectAdapter mAdapter;
    List<CollectBean> mCollectList;

    TextView tvHint;
    int catId=0;

    int pageId = 0;
    int action = I.ACTION_DOWNLOAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        mContext = this;
        mCollectList = new ArrayList<CollectBean>();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
//        setPullDownRefreshListener();
        setPullUpRefreshListener();
        setUpdateCollectListListener();
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
                int f = mGridLayoutManager.findFirstVisibleItemPosition();
                int l = mGridLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG,"f="+f+","+"l="+l);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findLastVisibleItemPosition()==0);
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
      String userName = FuliCenterApplication.getInstance().getUserName();
        Log.e(TAG,"username = "+userName);
        if (userName.isEmpty())finish();
        try {
            findNewCollectList(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
                @Override
                public void onSuccess(CollectBean[] result) {
                    tvHint.setVisibility(View.GONE);
                    mAdapter.setMore(true);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mAdapter.setFooterString(getResources().getString(R.string.load_more));
                    Log.e(TAG, "result = " + result);
                    if (result != null) {
                        Log.e(TAG, "result.length = " + result.length);
                        ArrayList<CollectBean> collectArrayList = Utils.array2List(result);
                        if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                            mAdapter.initItem(collectArrayList);
                        } else {
                            mAdapter.addItem(collectArrayList);
                        }
                        if (collectArrayList.size() < I.PAGE_SIZE_DEFAULT) {
                            mAdapter.setMore(false);
                            mAdapter.setFooterString(getResources().getString(R.string.no_more));
                        }
                    } else {
                        mAdapter.setMore(false);
                        mAdapter.setFooterString(getResources().getString(R.string.no_more));
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void findNewCollectList(OkHttpUtils2.OnCompleteListener<CollectBean[]>listener){
        OkHttpUtils2<CollectBean[]> utils = new OkHttpUtils2<CollectBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CollectBean[].class)
                .execute(listener);

    }

    private void initView(){
        DisplayUtils.initBackWithTitle(mContext,"收藏的宝贝");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        String name = getIntent().getStringExtra(D.Boutique.KEY_NAME);
        DisplayUtils.initBackWithTitle(mContext,name);
        Log.e(TAG,"name = "+name);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_collect);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new CollectAdapter(mContext,mCollectList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView) findViewById(R.id.tv_refresh_hint);
    }

    class UpdateCollectListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
        }
    }
    UpdateCollectListReceiver mReceiver;
    private void setUpdateCollectListListener(){
        mReceiver = new UpdateCollectListReceiver();
        IntentFilter filter = new IntentFilter("update_collect_list");
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
