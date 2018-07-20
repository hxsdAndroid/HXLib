package com.hyphenate.easeui.widget.chatrow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Direct;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.EaseChatMessageList;
import com.hyphenate.easeui.widget.EaseChatMessageList.MessageListItemClickListener;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;

import java.util.Date;

import static com.hyphenate.chat.EMMessage.Type.TXT;

public abstract class EaseChatRow extends LinearLayout {
    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected ImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView userTag;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected EMCallBack messageSendCallback;
    protected EMCallBack messageReceiveCallback;

    protected MessageListItemClickListener itemClickListener;

    public EaseChatRow(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.activity = (Activity) context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {
        onInflateView();
        timeStampView = (TextView) findViewById(R.id.timestamp);
        userAvatarView = (ImageView) findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        userTag = (TextView) findViewById(R.id.tv_usertag);
        usernickView = (TextView) findViewById(R.id.tv_userid);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * set property according message and postion
     * 
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
            EaseChatMessageList.MessageListItemClickListener itemClickListener) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;

        setUpBaseView();
        if (TextUtils.isEmpty(message.getStringAttribute("action",""))){
            onSetUpView();
        }
        setClickListener();
    }

    private void setUpBaseView() {
    	// set nickname, avatar and background of bubble
        TextView timestamp = (TextView) findViewById(R.id.timestamp);
        String action="";
        try {
            if(message.getType()==TXT){
                action=message.getStringAttribute("action","");
                View llTip=findViewById(R.id.ll_tip);
                TextView txtTipName= (TextView) findViewById(R.id.txt_tipname);
                RelativeLayout rlMes = (RelativeLayout) findViewById(R.id.rl_mes);
                if (!TextUtils.isEmpty(action)){
                    llTip.setVisibility(VISIBLE);
                    rlMes.setVisibility(GONE);
                    timestamp.setVisibility(GONE);
                    String nickName=message.getStringAttribute("nickname");
                    String tagStr=message.getStringAttribute("tagStr");
                    if ("UserEnter".equals(action)){
                        llTip.setVisibility(GONE);
                        rlMes.setVisibility(GONE);
                    }else if("popularityMsg".equals(action)){
                        txtTipName.setText(nickName);
                    }else{
                        llTip.setVisibility(GONE);
                        rlMes.setVisibility(VISIBLE);
                    }

                }else{
                    llTip.setVisibility(GONE);
                    rlMes.setVisibility(VISIBLE);
                }
            }
        } catch (HyphenateException e) {
            e.printStackTrace();

        }

        if (timestamp != null && TextUtils.isEmpty(action)) {
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
            	// show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        //set nickname and avatar
//        if(message.direct() == Direct.SEND){
//            EaseUserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
//        }else{
            EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
            String nickName=message.getStringAttribute("nickname","");
            String tagStr=message.getStringAttribute("tagStr","");
            int tag=message.getIntAttribute("tag",1);
            if (tag==0){
                userTag.setBackgroundResource(R.drawable.ease_txt_tag_four);
            }else if (tag==1){
                userTag.setBackgroundResource(R.drawable.ease_txt_tag_three);
            }else if(tag==2){
                userTag.setBackgroundResource(R.drawable.ease_txt_tag_two);
            }else if(tag==3){
                userTag.setBackgroundResource(R.drawable.ease_txt_tag_one);
            }else{
                userTag.setBackgroundResource(R.drawable.ease_txt_tag_one);
            }
            userTag.setText(tagStr);
            if (!TextUtils.isEmpty(nickName)){
                usernickView.setText(nickName);
            }else{
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            }
//        }
        if(deliveredView != null){
            if (message.isDelivered()) {
                deliveredView.setVisibility(View.INVISIBLE);
            } else {
                deliveredView.setVisibility(View.INVISIBLE);
            }
        }
        
        if(ackedView != null){
            if (message.isAcked()) {
                if (deliveredView != null) {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
                ackedView.setVisibility(View.INVISIBLE);
            } else {
                ackedView.setVisibility(View.INVISIBLE);
            }
        }
        

        if (adapter instanceof EaseMessageAdapter) {
            if (((EaseMessageAdapter) adapter).isShowAvatar()){
//                userAvatarView.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams=userAvatarView.getLayoutParams();
                layoutParams.width= (int) getResources().getDimension(R.dimen.size_avatar);
                userAvatarView.setLayoutParams(layoutParams);
            }else{
                ViewGroup.LayoutParams layoutParams=userAvatarView.getLayoutParams();
                layoutParams.width=0;
                userAvatarView.setLayoutParams(layoutParams);
//                userAvatarView.setVisibility(View.GONE);
            }
            if (usernickView != null) {
                if (((EaseMessageAdapter) adapter).isShowUserNick())
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (message.direct() == Direct.SEND) {
                if (((EaseMessageAdapter) adapter).getMyBubbleBg() != null) {
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                }
            } else if (message.direct() == Direct.RECEIVE) {
                if (((EaseMessageAdapter) adapter).getOtherBuddleBg() != null) {
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBuddleBg());
                }
            }
        }
    }

    /**
     * set callback for sending message
     */
    protected void setMessageSendCallback(){
        if(messageSendCallback == null){
            messageSendCallback = new EMCallBack() {
                
                @Override
                public void onSuccess() {
                    updateView();
                }
                
                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(percentageView != null)
                                percentageView.setText(progress + "%");

                        }
                    });
                }
                
                @Override
                public void onError(int code, String error) {
                    updateView(code, error);
                }
            };
        }
        message.setMessageStatusCallback(messageSendCallback);
    }
    
    /**
     * set callback for receiving message
     */
    protected void setMessageReceiveCallback(){
        if(messageReceiveCallback == null){
            messageReceiveCallback = new EMCallBack() {
                
                @Override
                public void onSuccess() {
                    updateView();
                }
                
                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if(percentageView != null){
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }
                
                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }
    
    
    private void setClickListener() {
        if(bubbleLayout != null){
            bubbleLayout.setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        if(!itemClickListener.onBubbleClick(message)){
                        	// if listener return false, we call default handling
                            onBubbleClick();
                        }
                    }
                }
            });
    
            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {
    
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(message);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onResendClick(message);
                    }
                }
            });
        }

        if(userAvatarView != null){
            userAvatarView.setOnClickListener(new OnClickListener() {
    
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarClick(message.getFrom());
                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {
                
                @Override
                public boolean onLongClick(View v) {
                    if(itemClickListener != null){
                        if (message.direct() == Direct.SEND) {
                            itemClickListener.onUserAvatarLongClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }


    protected void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (message.status() == EMMessage.Status.FAIL) {
                    Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                }

                onUpdateView();
            }
        });
    }

    protected void updateView(final int errorCode, final String desc) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (errorCode == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                    Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_invalid_content), Toast.LENGTH_SHORT).show();
                } else if (errorCode == EMError.GROUP_NOT_JOINED) {
                    Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.error_send_not_in_the_group), Toast.LENGTH_SHORT).show();
                } else if (errorCode == EMError.USER_MUTED) {
                    Toast.makeText(activity,"您已被禁言！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(activity,activity.getString(R.string.send_fail) + activity.getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                }
                onUpdateView();
            }
        });
    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh list view when message status change
     */
    protected abstract void onUpdateView();

    /**
     * setup view
     * 
     */
    protected abstract void onSetUpView();
    
    /**
     * on bubble clicked
     */
    protected abstract void onBubbleClick();

}
