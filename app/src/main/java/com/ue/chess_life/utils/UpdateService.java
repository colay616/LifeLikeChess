package com.ue.chess_life.utils;

import android.app.IntentService;
import android.content.Intent;

import com.hyphenate.easeui.bmob.Bmob;
import com.hyphenate.easeui.utils.PreferenceManager;
import com.ue.common.util.LogUtil;
import com.ue.common.util.PackageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.hyphenate.chat.EMGCMListenerService.TAG;

/**
 * Created by hawk on 2016/12/29.
 */

public class UpdateService extends IntentService{
    public UpdateService() {
        super("checkVerUpdate");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            JSONObject params=new JSONObject();
            params.put("curVerCode", PackageUtil.getVersionCode(UpdateService.this));
            String result= Bmob.callFunction("LAC_Has_Update",params.toString());
            LogUtil.i(TAG,"checkUpdateInfo,result1="+result);
            result = result.replace("\"{", "{").replace("}\"", "}").replace("\\n", "").replace("\\", "");
            LogUtil.i(TAG,"checkUpdateInfo,result2="+result);
            JSONObject resultObj = new JSONObject(result).optJSONObject("result");
            if(resultObj.optInt("code",-1)==100){
                if(resultObj.optBoolean("hasUpdate",false)){
                    JSONObject updateInfo=resultObj.optJSONObject("updateInfo");
                    PreferenceManager.saveNewVerCode(updateInfo.optInt(PreferenceManager.NEW_VER_CODE,-1));
                    PreferenceManager.saveNewVerName(updateInfo.optString(PreferenceManager.NEW_VER_NAME,""));
                    PreferenceManager.saveNewVerUrl(updateInfo.optString(PreferenceManager.NEW_VER_URL,""));
                    PreferenceManager.saveNewVerDesc(updateInfo.optString(PreferenceManager.NEW_VER_DESC,""));
                }else{
                    PreferenceManager.clearVerInfo();
                }
            }else{
                PreferenceManager.clearVerInfo();
            }
        } catch (JSONException e) {
            LogUtil.i(TAG,"json parse exception,error="+e.getMessage());
        }
    }
}
