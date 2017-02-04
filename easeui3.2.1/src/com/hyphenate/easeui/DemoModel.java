package com.hyphenate.easeui;

import android.content.Context;

import com.hyphenate.easeui.db.EaseUserDao;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoModel {
    EaseUserDao dao = null;
    protected Context context = null;
    protected Map<Key,Object> valueCache = new HashMap<Key,Object>();
    
    public DemoModel(Context ctx){
        context = ctx;
//        PreferenceManager.init(context);
    }
    
    public boolean saveContactList(List<EaseUser> contactList) {
        EaseUserDao dao = new EaseUserDao(context);
        dao.saveContactList(contactList);
        return true;
    }

    public Map<String, EaseUser> getContactList() {
        EaseUserDao dao = new EaseUserDao(context);
        return dao.getContactList();
    }
    
    public void saveContact(EaseUser user){
        EaseUserDao dao = new EaseUserDao(context);
        dao.saveContact(user);
    }
    
    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        PreferenceManager.setCurrentUserName(username);
    }

    public String getCurrentUserName(){
        return PreferenceManager.getCurrentUsername();
    }
    
//    public Map<String, RobotUser> getRobotList(){
//        EaseUserDao dao = new EaseUserDao(context);
//        return dao.getRobotUser();
//    }

//    public boolean saveRobotList(List<RobotUser> robotList){
//        EaseUserDao dao = new EaseUserDao(context);
//        dao.saveRobotUser(robotList);
//        return true;
//    }
    
    public void setSettingMsgNotification(boolean paramBoolean) {
        PreferenceManager.setSettingMsgNotification(paramBoolean);
        valueCache.put(Key.VibrateAndPlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgNotification() {
        Object val = valueCache.get(Key.VibrateAndPlayToneOn);

        if(val == null){
            val = PreferenceManager.getSettingMsgNotification();
            valueCache.put(Key.VibrateAndPlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        PreferenceManager.setSettingMsgSound(paramBoolean);
        valueCache.put(Key.PlayToneOn, paramBoolean);
    }

    public boolean getSettingMsgSound() {
        Object val = valueCache.get(Key.PlayToneOn);

        if(val == null){
            val = PreferenceManager.getSettingMsgSound();
            valueCache.put(Key.PlayToneOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        PreferenceManager.setSettingMsgVibrate(paramBoolean);
        valueCache.put(Key.VibrateOn, paramBoolean);
    }

    public boolean getSettingMsgVibrate() {
        Object val = valueCache.get(Key.VibrateOn);

        if(val == null){
            val = PreferenceManager.getSettingMsgVibrate();
            valueCache.put(Key.VibrateOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }

    public void setSettingMsgSpeaker(boolean paramBoolean) {
        PreferenceManager.setSettingMsgSpeaker(paramBoolean);
        valueCache.put(Key.SpakerOn, paramBoolean);
    }

    public boolean getSettingMsgSpeaker() {        
        Object val = valueCache.get(Key.SpakerOn);

        if(val == null){
            val = PreferenceManager.getSettingMsgSpeaker();
            valueCache.put(Key.SpakerOn, val);
        }
       
        return (Boolean) (val != null?val:true);
    }


    public void setDisabledGroups(List<String> groups){
        if(dao == null){
            dao = new EaseUserDao(context);
        }
        
        List<String> list = new ArrayList<String>();
        list.addAll(groups);
        for(int i = 0; i < list.size(); i++){
            if(EaseAtMessageHelper.get().getAtMeGroups().contains(list.get(i))){
                list.remove(i);
                i--;
            }
        }

        dao.setDisabledGroups(list);
        valueCache.put(Key.DisabledGroups, list);
    }
    
    public List<String> getDisabledGroups(){
        Object val = valueCache.get(Key.DisabledGroups);

        if(dao == null){
            dao = new EaseUserDao(context);
        }
        
        if(val == null){
            val = dao.getDisabledGroups();
            valueCache.put(Key.DisabledGroups, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setDisabledIds(List<String> ids){
        if(dao == null){
            dao = new EaseUserDao(context);
        }
        
        dao.setDisabledIds(ids);
        valueCache.put(Key.DisabledIds, ids);
    }
    
    public List<String> getDisabledIds(){
        Object val = valueCache.get(Key.DisabledIds);
        
        if(dao == null){
            dao = new EaseUserDao(context);
        }

        if(val == null){
            val = dao.getDisabledIds();
            valueCache.put(Key.DisabledIds, val);
        }

        //noinspection unchecked
        return (List<String>) val;
    }
    
    public void setGroupsSynced(boolean synced){
        PreferenceManager.setGroupsSynced(synced);
    }
    
    public boolean isGroupsSynced(){
        return PreferenceManager.isGroupsSynced();
    }
    
    public void setContactSynced(boolean synced){
        PreferenceManager.setContactSynced(synced);
    }
    
    public boolean isContactSynced(){
        return PreferenceManager.isContactSynced();
    }
    
    public void setBlacklistSynced(boolean synced){
        PreferenceManager.setBlacklistSynced(synced);
    }
    
    public boolean isBacklistSynced(){
        return PreferenceManager.isBacklistSynced();
    }
    
    public void allowChatroomOwnerLeave(boolean value){
        PreferenceManager.setSettingAllowChatroomOwnerLeave(value);
    }
    
    public boolean isChatroomOwnerLeaveAllowed(){
        return PreferenceManager.getSettingAllowChatroomOwnerLeave();
    }
   
    public void setDeleteMessagesAsExitGroup(boolean value) {
        PreferenceManager.setDeleteMessagesAsExitGroup(value);
    }
    
    public boolean isDeleteMessagesAsExitGroup() {
        return PreferenceManager.isDeleteMessagesAsExitGroup();
    }
    
    public void setAutoAcceptGroupInvitation(boolean value) {
        PreferenceManager.setAutoAcceptGroupInvitation(value);
    }
    
    public boolean isAutoAcceptGroupInvitation() {
        return PreferenceManager.isAutoAcceptGroupInvitation();
    }
    

    public void setAdaptiveVideoEncode(boolean value) {
        PreferenceManager.setAdaptiveVideoEncode(value);
    }
    
    public boolean isAdaptiveVideoEncode() {
        return PreferenceManager.isAdaptiveVideoEncode();
    }

    public void setPushCall(boolean value) {
        PreferenceManager.setPushCall(value);
    }

    public boolean isPushCall() {
        return PreferenceManager.isPushCall();
    }

    public void setRestServer(String restServer){
        PreferenceManager.setRestServer(restServer);
    }

    public String getRestServer(){
        return  PreferenceManager.getRestServer();
    }

    public void setIMServer(String imServer){
        PreferenceManager.setIMServer(imServer);
    }

    public String getIMServer(){
        return PreferenceManager.getIMServer();
    }

    public void enableCustomServer(boolean enable){
        PreferenceManager.enableCustomServer(enable);
    }

    public boolean isCustomServerEnable(){
        return PreferenceManager.isCustomServerEnable();
    }

    public void enableCustomAppkey(boolean enable) {
        PreferenceManager.enableCustomAppkey(enable);
    }

    public boolean isCustomAppkeyEnabled() {
        return PreferenceManager.isCustomAppkeyEnabled();
    }

    public void setCustomAppkey(String appkey) {
        PreferenceManager.setCustomAppkey(appkey);
    }

    public String getCutomAppkey() {
        return PreferenceManager.getCustomAppkey();
    }

    enum Key{
        VibrateAndPlayToneOn,
        VibrateOn,
        PlayToneOn,
        SpakerOn,
        DisabledGroups,
        DisabledIds
    }
}
