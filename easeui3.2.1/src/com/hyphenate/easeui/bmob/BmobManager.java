package com.hyphenate.easeui.bmob;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.easeui.domain.EaseUser;
import com.ue.common.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by hawk on 2016/12/5.
 */

public class BmobManager {
    public static final String TAG=BmobManager.class.getSimpleName();
    public static final String FUNC_GET_PROFILE ="LAC_Get_Profile";
    public static final String FUNC_UPDATE_AVATAR ="LAC_Update_Avatar";
    public static final String FUNC_UPDATE_NICKNAME ="LAC_Update_Nickname";
    public static final String FUNC_UPDATE_EMAIL ="LAC_Update_Email";
    public static final String FUNC_UPDATE_SEX ="LAC_Update_Sex";

    public static void curdProfile(String funcName, Map<String,Object>paramsMap, OnResultListener onResultListener){
        try {
            JSONObject params=new JSONObject();

            Iterator<String> iterator=paramsMap.keySet().iterator();
            while(iterator.hasNext()){
                String key=iterator.next();
                params.put(key,paramsMap.get(key));
            }
            String result=Bmob.callFunction(funcName,params.toString());
//            LogUtil.i(TAG,String.format("curdProfile,funcName=%s,result=%s",funcName,result));
            result = result.replace("\"{", "{").replace("}\"", "}").replace("\\n", "").replace("\\", "");
//            LogUtil.i(TAG,"result**="+result);
            JSONObject jsonResult = new JSONObject(result);
            JSONObject resultObj = jsonResult.optJSONObject("result");
            onResultListener.onResult(resultObj.optInt("code"),resultObj.optString("msg"),resultObj.optJSONObject("data"));
        } catch (JSONException e) {
            LogUtil.i(TAG,"curdProfile,json exception");
            onResultListener.onResult(119,"json parse error:"+e.getMessage(),null);
        }
    }

    public void getContactInfos(List<String> usernames, final EMValueCallBack<List<EaseUser>> callback) {

    }

    public interface OnResultListener{
        void onResult(int code,String msg,JSONObject data);
    }
}
