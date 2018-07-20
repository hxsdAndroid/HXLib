package com.netease.nim.uikit.business.session.helper;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.api.wrapper.MessageRevokeTip;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.Map;

/**
 * Created by hzxuwen on 2016/8/19.
 */
public class MessageHelper {

    public static MessageHelper getInstance() {
        return InstanceHolder.instance;
    }

    static class InstanceHolder {
        final static MessageHelper instance = new MessageHelper();
    }

    // 消息撤回
    public void onRevokeMessage(IMMessage item, String revokeAccount) {
        if (item == null) {
            return;
        }

        IMMessage message = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
        message.setContent(MessageRevokeTip.getRevokeTipContent(item, revokeAccount));
        message.setStatus(MsgStatusEnum.success);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, item.getTime());
    }

    public static String getIconRemoteExt(IMMessage message){
        final String KEY = "icon";
        Map<String, Object> ext = message.getRemoteExtension();
        if (ext !=null && ext.containsKey(KEY)){
            return String.valueOf(String.valueOf(ext.get(KEY)));
        }
        return "";
    }

}
