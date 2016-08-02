package cn.ucai.fulicenter.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodFragment extends Fragment {
    private final  static String TAG = NewGoodFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    GoodAdapter mAdapter;
    List<NewGoodBean> mGoodList;

    int pageId = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = (FuliCenterMainActivity) getContext();
        View layout = View.inflate(mContext,R.layout.fragment_new_good,null);
        mGoodList = new ArrayList<NewGoodBean>();
        initView(layout);
        initData();
        return layout;
    }

    private void initData() {
        findNewGoodList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                Log.e(TAG,"result = "+result);
                if(result != null){
                    Log.e(TAG,"result.length = "+result.length );
                    ArrayList<NewGoodBean> goodBeanArrayList = Utils.array2List(result);
                    mAdapter.initData(goodBeanArrayList);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
    private void findNewGoodList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]>listener){
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(I.CAT_ID))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);

    }

    private void initView(View layout){
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_new_good);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_new_good);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new GoodAdapter(mContext,mGoodList);
        mRecyclerView.setAdapter(mAdapter);
    }

}
