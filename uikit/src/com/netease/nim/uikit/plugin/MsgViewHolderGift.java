package com.netease.nim.uikit.plugin;

import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.chatroom.helper.ChatRoomHelper;
import com.netease.nim.uikit.business.chatroom.viewholder.ChatRoomViewHolderHelper;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

public class MsgViewHolderGift extends MsgViewHolderBase {

    private TextView sendName;

    public MsgViewHolderGift(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.gift_item;
    }

    @Override
    protected void inflateContentView() {
        sendName = findViewById(R.id.txt_tipname);
    }

    @Override
    protected void bindContentView() {
        GiftAttachment attachment = (GiftAttachment) message.getAttachment();
        sendName.setText(ChatRoomViewHolderHelper.getNameText((ChatRoomMessage) message));
    }

    @Override
    protected int leftBackground() {
        return R.color.transparent;
    }

    @Override
    protected int rightBackground() {
        return R.color.transparent;
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
    protected boolean shouldDisplayReceipt() {
        return false;
    }

    @Override
    protected boolean shouldDisplayNick() {
        return false;
    }
}
