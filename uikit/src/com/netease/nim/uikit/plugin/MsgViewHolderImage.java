package com.netease.nim.uikit.plugin;

import android.app.Activity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.session.activity.WatchMessagePictureActivity;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderImage extends MsgViewHolderImageThumbBase {

    public MsgViewHolderImage(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }


    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_picture;
    }

    @Override
    protected void onItemClick() {
        ImageAttachment attachment = (ImageAttachment) message.getAttachment();
        List<LocalMedia> localMedias=new ArrayList<LocalMedia>();
        LocalMedia localMedia=new LocalMedia();
        String path=attachment.getImage().substring(0,attachment.getImage().indexOf("?"));
        localMedia.setCompressPath(path);
        localMedia.setPath(path);
        localMedias.add(localMedia);
        PictureSelector.create((Activity) context).themeStyle(R.style.picture_default_style).openExternalPreview(0, localMedias);
    }

    @Override
    protected String thumbFromSourceFile(String path) {
        return path;
    }

    @Override
    protected String getPath() {
        ImageAttachment attachment = (ImageAttachment) message.getAttachment();
        return attachment.getImage();
    }
}
