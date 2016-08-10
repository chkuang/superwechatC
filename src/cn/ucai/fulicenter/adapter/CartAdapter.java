package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/8/1.
 */
public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    List<CartBean> mCartList;
    CartViewHolder mCartViewHolder;
    boolean isMore;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CartAdapter(Context context, List<CartBean> list) {
        mContext = context;
        mCartList = new ArrayList<CartBean>();
        mCartList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        holder = new CartViewHolder(inflater.inflate(R.layout.ietm_cart,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CartViewHolder){
            mCartViewHolder = (CartViewHolder) holder;
            final CartBean cart = mCartList.get(position);
            mCartViewHolder.cbCart.setChecked(cart.isChecked());
            ImageUtils.setGoodThumb(mContext, mCartViewHolder.ivCartThumb,cart.getGoods().getGoodsThumb());
            mCartViewHolder.tvGoodName.setText(cart.getGoods().getGoodsName());
            mCartViewHolder.tvNum.setText("("+cart.getCount()+")");
            mCartViewHolder.tvPrice.setText(cart.getGoods().getCurrencyPrice());
        }
//        if (holder instanceof FooterViewHolder){
//            mFooterViewHolder = (FooterViewHolder) holder;
//            mFooterViewHolder.tvFooter.setText(footerString);
//        }
    }

    @Override
    public int getItemCount() {
//        return mBoutiqueList !=null? mBoutiqueList.size()+1:1;
        return mCartList !=null? mCartList.size():0;
    }

    public void initItem(ArrayList<CartBean> list) {
        if(mCartList !=null){
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();

    }

    public void addItem(ArrayList<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout layout;
        CheckBox cbCart;
        ImageView ivCartThumb;
        TextView tvGoodName;
        ImageView ivAdd;
        TextView tvNum;
        ImageView ivDel;
        TextView tvPrice;

        public CartViewHolder(View itemView) {
            super(itemView);
            layout= (RelativeLayout) itemView.findViewById(R.id.layout_cart_item);
            cbCart = (CheckBox) itemView.findViewById(R.id.cb_cart_selected);
            ivCartThumb = (ImageView) itemView.findViewById(R.id.iv_cart_thmub);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            ivAdd = (ImageView) itemView.findViewById(R.id.iv_cart_add);
            tvNum = (TextView) itemView.findViewById(R.id.tv_cart_count);
            ivDel = (ImageView) itemView.findViewById(R.id.iv_cart_del);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
        }
    }

}
