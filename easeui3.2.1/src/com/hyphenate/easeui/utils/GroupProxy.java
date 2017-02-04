package com.hyphenate.easeui.utils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by hawk on 2016/11/23.
 */

public class GroupProxy {
    public static void joinGroup(final String groupId, final onJoinGroupListener onJoinGroupListener) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        boolean hasJoinedGroup = false;
                        List<EMGroup> grouplist = EMClient.getInstance().groupManager().getAllGroups();
                        if (null != grouplist && grouplist.size() > 0) {
                            for (EMGroup item : grouplist) {
                                if (item.getGroupId().equals(groupId)) {
                                    hasJoinedGroup = true;
                                    break;
                                }
                            }
                        }
                        if (hasJoinedGroup) {
                            onJoinGroupListener.onSuccess();
                            return;
                        }
                        try {
                            EMClient.getInstance().groupManager().joinGroup(groupId);
                            onJoinGroupListener.onSuccess();
                        } catch (final HyphenateException e) {
                            onJoinGroupListener.onFailure("加群失败:" + e.getMessage());
                        }
                    }
                }
        ).start();
    }

    public interface onJoinGroupListener {
        void onSuccess();
        void onFailure(String msg);
    }
}
