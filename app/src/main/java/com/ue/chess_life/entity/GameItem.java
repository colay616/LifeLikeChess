package com.ue.chess_life.entity;

/**
 * Created by hawk on 2016/11/22.
 */

public class GameItem {
    private String gameName;
    private int gameFlag;
    private boolean isDoubleModeEnabled;
    private boolean isSingleModeEnabled;
    private boolean isOnlineModeEnabled;
    private boolean isInviteModeEnabled;
    private String groupId;

    public GameItem(String gameName,int gameFlag,boolean isDoubleModeEnabled,boolean isSingleModeEnabled,boolean isOnlineModeEnabled,boolean isInviteModeEnabled,String groupId){
        this.gameName=gameName;
        this.gameFlag=gameFlag;
        this.isDoubleModeEnabled=isDoubleModeEnabled;
        this.isSingleModeEnabled=isSingleModeEnabled;
        this.isOnlineModeEnabled=isOnlineModeEnabled;
        this.isInviteModeEnabled=isInviteModeEnabled;
        this.groupId=groupId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public int getGameFlag() {
        return gameFlag;
    }

    public void setGameFlag(int gameFlag) {
        this.gameFlag = gameFlag;
    }

    public boolean isDoubleModeEnabled() {
        return isDoubleModeEnabled;
    }

    public void setDoubleModeEnabled(boolean doubleModeEnabled) {
        isDoubleModeEnabled = doubleModeEnabled;
    }

    public boolean isSingleModeEnabled() {
        return isSingleModeEnabled;
    }

    public void setSingleModeEnabled(boolean singleModeEnabled) {
        isSingleModeEnabled = singleModeEnabled;
    }

    public boolean isOnlineModeEnabled() {
        return isOnlineModeEnabled;
    }

    public void setOnlineModeEnabled(boolean onlineModeEnabled) {
        isOnlineModeEnabled = onlineModeEnabled;
    }

    public boolean isInviteModeEnabled() {
        return isInviteModeEnabled;
    }

    public void setInviteModeEnabled(boolean inviteModeEnabled) {
        isInviteModeEnabled = inviteModeEnabled;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
