package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/8/4.
 */
public class CategoryAdapter extends BaseExpandableListAdapter{
    Context mContext;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>>mChildList;

    public CategoryAdapter(Context mContext,
                           List<CategoryGroupBean> mGroupList,
                           List<ArrayList<CategoryChildBean>> mChildList) {
        this.mContext = mContext;
        this.mGroupList = new ArrayList<CategoryGroupBean>();
        mGroupList.addAll(mGroupList);
        this.mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mChildList.addAll(mChildList);
    }

    @Override
    public int getGroupCount() {
        return mGroupList !=null?mGroupList.size():0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        if (mGroupList!=null)return mGroupList.get(groupPosition);
        return null;
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        if(mChildList.get(groupPosition)!=null
                &&mChildList.get(groupPosition).get(childPosition)!=null)
            return mChildList.get(groupPosition).get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View converView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if(converView==null){
            converView = View.inflate(mContext, R.layout.item_category_group,null);
            holder = new GroupViewHolder();
            holder.ivGroupThumb = (ImageView) converView.findViewById(R.id.iv_group_thumb);
            holder.tvGroupName = (TextView) converView.findViewById(R.id.tv_group_name);
            holder.ivIndicator = (ImageView) converView.findViewById(R.id.iv_indicator);
            converView.setTag(holder);
        }else{
            holder = (GroupViewHolder) converView.getTag();
        }
        CategoryGroupBean group = getGroup(groupPosition);
        ImageUtils.setGroupCategoryImage(mContext,holder.ivGroupThumb,group.getImageUrl());
        holder.tvGroupName.setText(group.getName());
        if(isExpanded){
            holder.ivIndicator.setImageResource(R.drawable.expand_off);
        }else{
            holder.ivIndicator.setImageResource(R.drawable.expand_on);
        }
        return converView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View converView, ViewGroup parent) {
        ChildViewHolder holder;
        if (converView==null){
            converView = View.inflate(mContext,R.layout.item_cateogry_child,null);
            holder = new ChildViewHolder();
            holder.layoutCategoryChild = (RelativeLayout) converView.findViewById(R.id.layout_category_child);
            holder.ivCategoryChildThumb = (ImageView) converView.findViewById(R.id.iv_category_child_thumb);
            holder.tvCategoryChildName = (TextView) converView.findViewById(R.id.tv_category_child_name);
            converView.setTag(holder);
        }else{
            holder = (ChildViewHolder) converView.getTag();
        }
        CategoryChildBean child = getChild(groupPosition,childPosition);
        if(child !=null){
            ImageUtils.setChildCategoryImage(mContext,holder.ivCategoryChildThumb,child.getImageUrl());
            holder.tvCategoryChildName.setText(child.getName());
        }
        return converView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void addAll(List<CategoryGroupBean> mGroupList, List<ArrayList<CategoryChildBean>> mChildList) {
        this.mGroupList.clear();
        this.mGroupList.addAll(mGroupList);
        this.mChildList.clear();
        this.mChildList.addAll(mChildList);
        notifyDataSetChanged();
    }

    class  GroupViewHolder{
        ImageView ivGroupThumb;
        TextView tvGroupName;
        ImageView ivIndicator;
    }

    class ChildViewHolder{
        RelativeLayout layoutCategoryChild;
        ImageView ivCategoryChildThumb;
        TextView tvCategoryChildName;
    }

}
