package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.bean.GroupAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/21.
 */
public class DownloadGroupListTask {
    String username;
    Context mContext;

    public DownloadGroupListTask(Context context, String username) {
        mContext = context;
        this.username = username;
    }
    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main","s="+s);
                        Utils.getResultFromJson(s, UserAvatar.class);
                        Result result =  Utils.getListResultFromJson(s,GroupAvatar.class);
                        Log.i("main","result="+result);
                        List<GroupAvatar> list = (List<GroupAvatar>) result.getRetData();
                        if(list!=null && list.size()>0){
                            Log.i("main","List.Size="+list.size());
                            FuliCenterApplication.getInstance().setGroupList(list);
                            for(GroupAvatar g:list ){
                                FuliCenterApplication.getInstance().getGroupMap().put(g.getMGroupHxid(),g);
                            }
                            mContext.sendStickyBroadcast(new Intent("update_group_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main","error= "+error);
                    }
                });
    }
}
