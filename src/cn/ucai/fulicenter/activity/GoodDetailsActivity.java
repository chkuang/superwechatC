package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

public class GoodDetailsActivity extends Activity {
    private final static String TAG = GoodDetailsActivity.class.getSimpleName();
    GoodDetailsActivity mContext;
    GoodDetailsBean mGoodDetail;

    ImageView ivShare;
    ImageView ivCollect;
    ImageView ivCart;
    TextView tvCartCount;

    TextView tvGoodEnglishName;
    TextView tvGoodName;
    TextView tvGoodPriceCurrent;
    TextView tvGoodPriceShop;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView wvGoodBrief;
    int mGoodId;

    boolean isCollect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);
        mContext  = this;
        initView();
        initData();
        setLisetener();
    }

    private void setLisetener() {
        MyOnClickListener listener = new MyOnClickListener();
        ivCollect.setOnClickListener(listener);
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID,0);
        Log.e(TAG,"mGoodId = "+mGoodId);
        if(mGoodId>0){
            getGoodDetailsByGoodId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                @Override
                public void onSuccess(GoodDetailsBean result) {
                    Log.e(TAG,"result = "+result);
                    if(result!=null){
                        mGoodDetail = result;
                        showGoodDetails();
                    }
                }
                @Override
                public void onError(String error) {
                    Log.e(TAG,"error = "+error);
                    finish();
                    Toast.makeText(GoodDetailsActivity.this, "获取商品详情失败", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            finish();
            Toast.makeText(GoodDetailsActivity.this, "获取商品详情失败", Toast.LENGTH_SHORT).show();

        }
    }

    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetail.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetail.getGoodsName());
        tvGoodPriceCurrent.setText(mGoodDetail.getCurrencyPrice());
        tvGoodPriceShop.setText(mGoodDetail.getShopPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,getAlbumImageUrl(),getAlbumImageSize());
        wvGoodBrief.loadDataWithBaseURL(null,mGoodDetail.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private String[] getAlbumImageUrl() {
        String[] albumImageUrl = new String[]{};
        if (mGoodDetail.getPromotePrice()!= null&& mGoodDetail.getPromotePrice().length()>0){
            AlbumBean[] albums = mGoodDetail.getProperties()[0].getAlbums();
            albumImageUrl = new String[albums.length];
            for(int i=0;i<albumImageUrl.length;i++){
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return albumImageUrl;
    }

    private int getAlbumImageSize() {
        if (mGoodDetail.getPromotePrice()!= null&& mGoodDetail.getPromotePrice().length()>0){
            return mGoodDetail.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    public void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener){
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBack(mContext);
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        tvCartCount = (TextView) findViewById(R.id.tv_cart_count);
        tvGoodEnglishName = (TextView) findViewById(R.id.tv_good_name_english);
        tvGoodName = (TextView) findViewById(R.id.tv_good_name);
        tvGoodPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        tvGoodPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings =wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
    }

    private void initCollectStatus() {
        if(DemoHXSDKHelper.getInstance().isLogined()){
            String userName = FuliCenterApplication.getInstance().getUserName();
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
            utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                    .addParam(I.Collect.USER_NAME,userName)
                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG,"result = "+result);
                            if(result!=null && result.isSuccess()){
                                isCollect = true;
                            }else{
                                isCollect = false;
                            }
                            updateCollectStatus();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG,"error = "+error);
                        }
                    });
        }
    }
    
    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_good_collect:
                    goodCollect();
                    break;
            }
        }
    }

    private void goodCollect() {
        if (DemoHXSDKHelper.getInstance().isLogined()){
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
            utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                    .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG,"result = "+result);
                            if (result!=null){
                                new DownloadCollectCountTask(mContext,FuliCenterApplication.getInstance().getUserName()).execute();
                            }else{
                                Log.e(TAG,"delete fail");
                            }
                            updateCollectStatus();
                            Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });

        }else{
            startActivity(new Intent(mContext,LoginActivity.class));
        }
    }
    private void updateCollectStatus(){
        if (isCollect){
            ivCollect.setImageResource(R.drawable.bg_collect_out);
        }else{
            ivCollect.setImageResource(R.drawable.bg_collect_in);

        }
    }
}
