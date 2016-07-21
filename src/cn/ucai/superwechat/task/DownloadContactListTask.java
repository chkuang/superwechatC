package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by Administrator on 2016/7/21.
 */
public class DownloadContactListTask {
    String username;
    Context mContext;

    public DownloadContactListTask(Context context,String username) {
        mContext = context;
        this.username = username;
    }
    public void execute(){
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main","s="+s);
                        Utils.getResultFromJson(s, UserAvatar.class);
                        Result result =  Utils.getListResultFromJson(s,UserAvatar.class);
                        Log.i("main","result="+result);
                        List<UserAvatar> list = (List<UserAvatar>) result.getRetData();
                        if(list!=null && list.size()>0){
                            Log.i("main","List.Size="+list.size());
                            SuperWeChatApplication.getInstance().setUserList(list);
                            mContext.sendStickyBroadcast(new Intent("update_contact_list"));
                            Map<String ,UserAvatar> userMap = SuperWeChatApplication.getInstance().getUserMap();
                            for(UserAvatar u : list){
                                userMap.put(u.getMUserName(),u);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main","error= "+error);
                    }
                });
    }
}
