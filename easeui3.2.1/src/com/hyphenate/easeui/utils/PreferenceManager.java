/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui.utils;

import com.ue.common.xsharedpref.XSharedPref;

public class PreferenceManager {
	private static String SETTING_NOTIFICATION = "setting_notification";
	private static String SETTING_SOUND = "setting_sound";
	private static String SETTING_VIBRATE = "setting_vibrate";
	private static String SETTING_SPEAKER = "setting_speaker";

	private static String CHATROOM_OWNER_LEAVE = "setting_chatroom_owner_leave";
    private static String DELETE_MESSAGES_WHEN_EXIT_GROUP = "setting_delete_messages_when_exit_group";
    private static String AUTO_ACCEPT_GROUP_INVITATION = "setting_auto_accept_group_invitation";
    private static String ADAPTIVE_VIDEO_ENCODE = "setting_adaptive_video_encode";
	private static String OFFLINE_PUSH_CALL = "setting_offline_push_call";

	private static String GROUPS_SYNCED = "GROUPS_SYNCED";
	private static String CONTACT_SYNCED = "CONTACT_SYNCED";
	private static String BALCKLIST_SYNCED = "BALCKLIST_SYNCED";

	private static String CURRENTUSER_USERNAME = "CURRENTUSER_USERNAME";
	private static String CURRENTUSER_NICK = "CURRENTUSER_NICK";
	private static String CURRENTUSER_AVATAR = "CURRENTUSER_AVATAR";

	private static String REST_SERVER = "REST_SERVER";
	private static String IM_SERVER = "IM_SERVER";
	private static String ENABLE_CUSTOM_SERVER = "ENABLE_CUSTOM_SERVER";
	private static String ENABLE_CUSTOM_APPKEY = "ENABLE_CUSTOM_APPKEY";
	private static String CUSTOM_APPKEY = "CUSTOM_APPKEY";
	public static String NEW_VER_CODE="newVerCode";
	public static String NEW_VER_NAME="newVerName";
	public static String NEW_VER_URL="newVerUrl";
	public static String NEW_VER_DESC ="newVerDesc";

	public static void clearVerInfo(){
		XSharedPref.remove(NEW_VER_CODE);
		XSharedPref.remove(NEW_VER_NAME);
		XSharedPref.remove(NEW_VER_URL);
		XSharedPref.remove(NEW_VER_DESC);
	}
	public static void saveNewVerCode(int newVerCode){
		XSharedPref.putInt(NEW_VER_CODE,newVerCode);
	}
	public static int getNewVerCode(){
		return XSharedPref.getInt(NEW_VER_CODE,-1);
	}
	public static void saveNewVerName(String newVerName){
		XSharedPref.putString(NEW_VER_NAME,newVerName);
	}
	public static String getNewVerName(){
		return XSharedPref.getString(NEW_VER_NAME,null);
	}
	public static void saveNewVerUrl(String newVerUrl){
		XSharedPref.putString(NEW_VER_URL,newVerUrl);
	}
	public static String getNewVerUrl(){
		return XSharedPref.getString(NEW_VER_URL,null);
	}
	public static void saveNewVerDesc(String newVerDescribe){
		XSharedPref.putString(NEW_VER_DESC,newVerDescribe);
	}
	public static String getNewVerDesc(){
		return XSharedPref.getString(NEW_VER_DESC,null);
	}
	public static void setSettingMsgNotification(boolean paramBoolean) {
		XSharedPref.putBoolean(SETTING_NOTIFICATION, paramBoolean);
	}

	public static boolean getSettingMsgNotification() {
		return XSharedPref.getBoolean(SETTING_NOTIFICATION, true);
	}

	public static void setSettingMsgSound(boolean paramBoolean) {
		XSharedPref.putBoolean(SETTING_SOUND, paramBoolean);
	}

	public static boolean getSettingMsgSound() {
		return XSharedPref.getBoolean(SETTING_SOUND, true);
	}

	public static void setSettingMsgVibrate(boolean paramBoolean) {
		XSharedPref.putBoolean(SETTING_VIBRATE, paramBoolean);
	}

	public static boolean getSettingMsgVibrate() {
		return XSharedPref.getBoolean(SETTING_VIBRATE, true);
	}

	public static void setSettingMsgSpeaker(boolean paramBoolean) {
		XSharedPref.putBoolean(SETTING_SPEAKER, paramBoolean);
	}

	public static boolean getSettingMsgSpeaker() {
		return XSharedPref.getBoolean(SETTING_SPEAKER, true);
	}

	public static void setSettingAllowChatroomOwnerLeave(boolean value) {
		XSharedPref.putBoolean(CHATROOM_OWNER_LEAVE, value);
    }

	public static boolean getSettingAllowChatroomOwnerLeave() {
		return XSharedPref.getBoolean(CHATROOM_OWNER_LEAVE, true);
    }

    public static void setDeleteMessagesAsExitGroup(boolean value){
		XSharedPref.putBoolean(DELETE_MESSAGES_WHEN_EXIT_GROUP, value);
    }

    public static boolean isDeleteMessagesAsExitGroup() {
		return XSharedPref.getBoolean(DELETE_MESSAGES_WHEN_EXIT_GROUP, true);
    }

    public static void setAutoAcceptGroupInvitation(boolean value) {
		XSharedPref.putBoolean(AUTO_ACCEPT_GROUP_INVITATION, value);
    }

    public static boolean isAutoAcceptGroupInvitation() {
        return XSharedPref.getBoolean(AUTO_ACCEPT_GROUP_INVITATION, true);
    }

    public static void setAdaptiveVideoEncode(boolean value) {
        XSharedPref.putBoolean(ADAPTIVE_VIDEO_ENCODE, value);
    }

    public static boolean isAdaptiveVideoEncode() {
        return XSharedPref.getBoolean(ADAPTIVE_VIDEO_ENCODE, false);
    }

	public static void setPushCall(boolean value) {
		XSharedPref.putBoolean(OFFLINE_PUSH_CALL, value);
	}

	public static boolean isPushCall() {
		return XSharedPref.getBoolean(OFFLINE_PUSH_CALL, false);
	}
    
	public static void setGroupsSynced(boolean synced){
	    XSharedPref.putBoolean(GROUPS_SYNCED, synced);
	}

	public static boolean isGroupsSynced(){
	    return XSharedPref.getBoolean(GROUPS_SYNCED, false);
	}

	public static void setContactSynced(boolean synced){
        XSharedPref.putBoolean(CONTACT_SYNCED, synced);
    }

    public static boolean isContactSynced(){
        return XSharedPref.getBoolean(CONTACT_SYNCED, false);
    }

    public static void setBlacklistSynced(boolean synced){
        XSharedPref.putBoolean(BALCKLIST_SYNCED, synced);
    }

    public static boolean isBacklistSynced(){
        return XSharedPref.getBoolean(BALCKLIST_SYNCED, false);
    }

	public static void saveUserInfo(String userName, String jsonStr){
		XSharedPref.putString(userName, jsonStr);
	}

	public static String getUserInfo(String userName){
		return XSharedPref.getString(userName, null);
	}

	public static void setCurrentUserNick(String nick) {
		XSharedPref.putString(CURRENTUSER_NICK, nick);
	}

	public static void setCurrentUserAvatar(String avatar) {
		XSharedPref.putString(CURRENTUSER_AVATAR, avatar);
	}

	public static String getCurrentUserNick() {
		return XSharedPref.getString(CURRENTUSER_NICK, getCurrentUsername());
	}

	public static String getCurrentUserAvatar() {
		return XSharedPref.getString(CURRENTUSER_AVATAR, "noAvatar");
	}

	public static void setCurrentUserName(String username){
		XSharedPref.putString(CURRENTUSER_USERNAME, username);
	}

	public static String getCurrentUsername(){
		return XSharedPref.getString(CURRENTUSER_USERNAME, null);
	}

	public static void setRestServer(String restServer){
		XSharedPref.putString(REST_SERVER, restServer);//.commit();
	}

	public static String getRestServer(){
		return XSharedPref.getString(REST_SERVER, null);
	}

	public static void setIMServer(String imServer){
		XSharedPref.putString(IM_SERVER, imServer);
	}

	public static String getIMServer(){
		return XSharedPref.getString(IM_SERVER, null);
	}

	public static void enableCustomServer(boolean enable){
		XSharedPref.putBoolean(ENABLE_CUSTOM_SERVER, enable);
	}

	public static boolean isCustomServerEnable(){
		return XSharedPref.getBoolean(ENABLE_CUSTOM_SERVER, false);
	}

	public static void enableCustomAppkey(boolean enable) {
		XSharedPref.putBoolean(ENABLE_CUSTOM_APPKEY, enable);
	}

	public static boolean isCustomAppkeyEnabled() {
		return XSharedPref.getBoolean(ENABLE_CUSTOM_APPKEY, false);
	}

	public static String getCustomAppkey() {
		return XSharedPref.getString(CUSTOM_APPKEY, "");
	}

	public static void setCustomAppkey(String appkey) {
		XSharedPref.putString(CUSTOM_APPKEY, appkey);
	}

	public static void removeCurrentUserInfo() {
		XSharedPref.remove(CURRENTUSER_NICK);
		XSharedPref.remove(CURRENTUSER_AVATAR);
	}
}
