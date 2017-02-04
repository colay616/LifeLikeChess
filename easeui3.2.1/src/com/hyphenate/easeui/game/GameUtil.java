package com.hyphenate.easeui.game;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by hawk on 2016/12/3.
 */

public class GameUtil {
    public static void sendCMDMessage(String receipt, String action, Map<String,Object>attrs) {
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.addBody(cmdBody);
        cmdMsg.setReceipt(receipt);
        if(null!=attrs){
            Iterator<String>attrsKeyIterator=attrs.keySet().iterator();
            while(attrsKeyIterator.hasNext()){
                String key=attrsKeyIterator.next();
                Object value=attrs.get(key);
                if(value instanceof Integer){
                    cmdMsg.setAttribute(key,(Integer)attrs.get(key));
                }else if(value instanceof String){
                    cmdMsg.setAttribute(key,(String)attrs.get(key));
                }
            }
        }
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }
}
