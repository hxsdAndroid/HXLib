package com.netease.nim.uikit.business.chatroom.viewholder;

import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.chatroom.helper.ChatRoomHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.plugin.GiftAttachment;

import java.util.Map;

/**
 * Created by hzliuxuanlin on 17/9/15.
 */
public class ChatRoomMsgViewHolderGift extends ChatRoomMsgViewHolderBase {

    private TextView sendName;

    public ChatRoomMsgViewHolderGift(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.gift_item;
    }

    @Override
    protected void inflateContentView() {
        sendName = findViewById(com.netease.nim.uikit.R.id.txt_tipname);
    }

    @Override
    protected void bindContentView() {
        if (message.getAttachment() == null) {
            return;
        }
        sendName.setText(ChatRoomViewHolderHelper.getNameText(message));
    }


    @Override
    protected int leftBackground() {
        return com.netease.nim.uikit.R.color.transparent;
    }

    @Override
    protected int rightBackground() {
        return com.netease.nim.uikit.R.color.transparent;
    }

    @Override
    protected void onItemClick() {

    }

    @Override
    protected boolean onItemLongClick() {
        return true;
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

    @Override
    protected boolean isShowHeadImage() {
        return false;
    }

    @Override
    protected boolean isShowBubble() {
        return false;
    }


    @Override
    protected boolean shouldDisplayNick() {
        return false;
    }
}
