package com.netease.nim.uikit.plugin;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.business.chatroom.viewholder.ChatRoomViewHolderHelper;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;

import de.greenrobot.event.EventBus;

public class MsgViewHolderCourse extends MsgViewHolderBase {

    private TextView txtText;
    private TextView txtTitle;
    private ImageView imgIcon;

    public MsgViewHolderCourse(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_course;
    }

    @Override
    protected void inflateContentView() {
        txtText = findViewById(R.id.txt_text);
        txtTitle = findViewById(R.id.txt_title);
        imgIcon = findViewById(R.id.img_icon);
    }

    @Override
    protected void bindContentView() {
        CourseAttachment attachment = (CourseAttachment) message.getAttachment();
        if (TextUtils.isEmpty(attachment.getText())){
            txtText.setVisibility(View.GONE);
        }else{
            txtText.setVisibility(View.VISIBLE);
            txtText.setText(attachment.getText());
        }
        if (attachment.getInsertInfo()==null){
            return;
        }
        if (TextUtils.isEmpty(attachment.getInsertInfo().getCover_img())){
            imgIcon.setVisibility(View.GONE);
        }else{
            imgIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(attachment.getInsertInfo().getCover_img()).into(imgIcon);
        }

        if (TextUtils.isEmpty(attachment.getInsertInfo().getTitle())){
            txtTitle.setVisibility(View.GONE);
        }else{
            txtTitle.setVisibility(View.VISIBLE);
            txtTitle.setText(attachment.getInsertInfo().getTitle());
        }

    }

    @Override
    protected void onItemClick() {

        CourseAttachment attachment = (CourseAttachment) message.getAttachment();
        if (attachment.getInsertInfo()!=null && attachment.getInsertInfo().getAppJump()!=null){
            AppJumpEntity appJumpEntity=attachment.getInsertInfo().getAppJump();
            EventBus.getDefault().post(appJumpEntity);
           // msgCourseItemClickLister.onClick(appJumpEntity.getMsgAction(),appJumpEntity.getMsgActionParam());
        }
    }

}
