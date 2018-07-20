package com.netease.nim.uikit.business.session.viewholder;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.activity.WatchMessagePictureActivity;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.plugin.GiftAttachment;
import com.netease.nim.uikit.plugin.ImageAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderPicture extends MsgViewHolderThumbBase {

    public MsgViewHolderPicture(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_picture;
    }

    @Override
    protected void onItemClick() {
        WatchMessagePictureActivity.start(context, message);
    }

    @Override
    protected String thumbFromSourceFile(String path) {
        ImageAttachment attachment = (ImageAttachment) message.getAttachment();
        return attachment.getImage();
    }
}
