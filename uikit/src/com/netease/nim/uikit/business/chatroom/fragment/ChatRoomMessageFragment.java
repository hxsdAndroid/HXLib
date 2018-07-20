package com.netease.nim.uikit.business.chatroom.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.NimConfig;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.model.chatroom.ChatRoomSessionCustomization;
import com.netease.nim.uikit.business.ait.AitManager;
import com.netease.nim.uikit.business.chatroom.helper.ChatRoomHelper;
import com.netease.nim.uikit.business.chatroom.helper.ChatRoomNotificationHelper;
import com.netease.nim.uikit.business.chatroom.module.ChatRoomInputPanel;
import com.netease.nim.uikit.business.chatroom.module.ChatRoomMsgListPanel;
import com.netease.nim.uikit.business.chatroom.module.NimArrtiEntity;
import com.netease.nim.uikit.business.chatroom.viewholder.ChatRoomViewHolderHelper;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.impl.NimUIKitImpl;
import com.netease.nim.uikit.plugin.CustomAttachment;
import com.netease.nim.uikit.plugin.CustomAttachmentType;
import com.netease.nim.uikit.plugin.GiftAttachment;
import com.netease.nim.uikit.plugin.NimEvent_Msg;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.robot.model.NimRobotInfo;
import com.netease.nimlib.sdk.robot.model.RobotAttachment;
import com.netease.nimlib.sdk.robot.model.RobotMsgType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.netease.nimlib.sdk.msg.constant.NotificationType.ChatRoomMemberIn;

/**
 * 聊天室直播互动fragment
 * 可以直接集成到应用中
 */
public class ChatRoomMessageFragment extends TFragment implements ModuleProxy {
    private String roomId;
    protected View rootView;
    protected View llTopTip;
    protected TextView txtTopTag;
    protected TextView txtTopName;
    private ChatRoomMsgLister chatRoomMsgLister;

    private static ChatRoomSessionCustomization customization;
    protected NimArrtiEntity nimArrtiEntity;

    // modules
    protected ChatRoomInputPanel inputPanel;
    protected ChatRoomMsgListPanel messageListPanel;
    protected AitManager aitManager;

    public static void setChatRoomSessionCustomization(ChatRoomSessionCustomization roomSessionCustomization) {
        customization = roomSessionCustomization;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nim_chat_room_message_fragment, container, false);
        llTopTip=rootView.findViewById(R.id.ll_top_tip);
        txtTopTag= (TextView) rootView.findViewById(R.id.ease_top_tag);
        txtTopName= (TextView) rootView.findViewById(R.id.ease_top_name);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle fragmentArgs = getArguments();
        if (fragmentArgs!=null && fragmentArgs.containsKey("attribute")){
            nimArrtiEntity= (NimArrtiEntity) fragmentArgs.getParcelable("attribute");
        }else{
            Toast.makeText(getContext(),"进入方式错误",Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (inputPanel != null) {
            inputPanel.onPause();
        }
        if (messageListPanel != null) {
            messageListPanel.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (messageListPanel != null) {
            messageListPanel.onResume();
        }
    }

    public boolean onBackPressed() {
        if (inputPanel != null && inputPanel.collapse(true)) {
            return true;
        }

        if (messageListPanel != null && messageListPanel.onBackPressed()) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);

        if (messageListPanel != null) {
            messageListPanel.onDestroy();
        }
        if (aitManager != null) {
            aitManager.reset();
        }
        if (timer!=null){
            timer.cancel();
            timer=null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (aitManager != null) {
            aitManager.onActivityResult(requestCode, resultCode, data);
        }

        inputPanel.onActivityResult(requestCode, resultCode, data);
    }

    public void onLeave() {
        if (inputPanel != null) {
            inputPanel.collapse(false);
        }
    }

    public void setCoolingTime(int time){
        if (inputPanel != null) {
            inputPanel.setCoolingTime(time);
        }
    }
    public void setGiftCooing(){
        if (inputPanel != null) {
            inputPanel.setGiftCooing();
        }
    }

    public void sendGift(){
        if (inputPanel != null) {
            sendMessage(inputPanel.createCustomMessage(new GiftAttachment()));
        }
    }

    public void sendTxtMsg(String content){
        if (inputPanel != null) {
            sendMessage(inputPanel.createTextMessage(content));
        }
    }

    public void init(String roomId) {
        this.roomId = roomId;
        registerObservers(true);
        findViews();
    }

    private void findViews() {
        Container container = new Container(getActivity(), roomId, SessionTypeEnum.ChatRoom, this);
        if (messageListPanel == null) {
            messageListPanel = new ChatRoomMsgListPanel(container, rootView);
        } else {
            messageListPanel.reload(container);
        }

        if (inputPanel == null) {
            inputPanel = new ChatRoomInputPanel(container, rootView, getActionList(), false);
        } else {
            inputPanel.reload(container, null);
        }

        if (NimUIKitImpl.getOptions().aitEnable && NimUIKitImpl.getOptions().aitChatRoomRobot) {
            if (aitManager == null) {
                aitManager = new AitManager(getContext(), null, true);
            }
            inputPanel.addAitTextWatcher(aitManager);
            aitManager.setTextChangeListener(inputPanel);
        }
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg =  new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {

            if (messages == null || messages.isEmpty()) {
                return;
            }
            List<ChatRoomMessage> addMessages=new ArrayList<>();
            for (ChatRoomMessage chatRoomMessage:messages){
                if (chatRoomMessage.getMsgType()==MsgTypeEnum.notification){
                    ChatRoomNotificationAttachment attachment=(ChatRoomNotificationAttachment)chatRoomMessage.getAttachment();
                    if (attachment.getType().equals(ChatRoomMemberIn)){
                        showTip("", ChatRoomNotificationHelper.getNotificationText(attachment));
                    }
                    return;
                }

                if (chatRoomMessage.getMsgType().equals(MsgTypeEnum.text)){
                    EventBus.getDefault().post(new NimEvent_Msg(chatRoomMessage.getContent(),ChatRoomViewHolderHelper.getNameText(chatRoomMessage), NimConfig.MSG_TEXT_TYPE,false));
                }else if (chatRoomMessage.getMsgType().equals(MsgTypeEnum.custom)){
                    if (chatRoomMessage.getAttachment()==null){
                        return;
                    }
                    CustomAttachment customAttachment=((CustomAttachment) chatRoomMessage.getAttachment());
                    if (customAttachment.getType()==CustomAttachmentType.SIGININ){
                        if (chatRoomMsgLister!=null){
                            chatRoomMsgLister.onSignInMsg();
                        }
                        return;
                    }else if(customAttachment.getType()==CustomAttachmentType.SIGINOUT){
                        if (chatRoomMsgLister!=null){
                            chatRoomMsgLister.onSignOutMsg();
                        }
                        return;
                    }else if(customAttachment.getType()==CustomAttachmentType.GIFT){
                        EventBus.getDefault().post(new NimEvent_Msg(ChatRoomViewHolderHelper.getNameText(chatRoomMessage)+"，送了一个修为瓶",ChatRoomViewHolderHelper.getNameText(chatRoomMessage), NimConfig.MSG_GIFT_TYPE,false));
                        if (chatRoomMsgLister!=null){
                            chatRoomMsgLister.onReceivedPopularity();
                        }
                    }
                }
                addMessages.add(chatRoomMessage);
            }
            messageListPanel.onIncomingMessage(addMessages);
        }
    };

    private void showTip(final String tag, final String name){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timer.cancel();
                txtTopName.setText(name);
                txtTopTag.setText(tag);
                llTopTip.setVisibility(View.VISIBLE);
                timer.start();
            }
        });
    }

    private CountDownTimer timer = new CountDownTimer(2000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            llTopTip.setVisibility(View.GONE);
        }
    };
    /************************** Module proxy ***************************/

    @Override
    public boolean sendMessage(IMMessage msg) {
        ChatRoomMessage message = (ChatRoomMessage) msg;
        Map<String,Object> stringMap=message.getRemoteExtension();
        if (stringMap==null){
            stringMap=new HashMap<>();
        }
        stringMap.put("tag", nimArrtiEntity.getProps().getTag());
        stringMap.put("tagName",nimArrtiEntity.getProps().getTagName());
        if (message.getMsgType().equals(MsgTypeEnum.text)){
            EventBus.getDefault().post(new NimEvent_Msg(msg.getContent(),nimArrtiEntity.getName(), NimConfig.MSG_TEXT_TYPE,true));
        }else if (message.getMsgType().equals(MsgTypeEnum.custom) && (CustomAttachment) message.getAttachment()!=null && ((CustomAttachment) message.getAttachment()).getType()== CustomAttachmentType.GIFT){
            EventBus.getDefault().post(new NimEvent_Msg(nimArrtiEntity.getName()+"，送了一个修为瓶",nimArrtiEntity.getName(), NimConfig.MSG_GIFT_TYPE,true));
            if (chatRoomMsgLister!=null){
                chatRoomMsgLister.onSendPopularityClick();
            }
        }
        message.setRemoteExtension(stringMap);
        // 检查是否转换成机器人消息
        message = changeToRobotMsg(message);
        ChatRoomHelper.buildMemberTypeInRemoteExt(message, roomId);
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == ResponseCode.RES_CHATROOM_MUTED) {
                            Toast.makeText(NimUIKit.getContext(), "用户被禁言", Toast.LENGTH_SHORT).show();
                        } else if (code == ResponseCode.RES_CHATROOM_ROOM_MUTED) {
                            Toast.makeText(NimUIKit.getContext(), "全体禁言", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NimUIKit.getContext(), "消息发送失败：code:" + code, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Toast.makeText(NimUIKit.getContext(), "消息发送失败！", Toast.LENGTH_SHORT).show();
                    }
                });
        messageListPanel.onMsgSend(message);
        if (aitManager != null) {
            aitManager.reset();
        }
        return true;
    }

    private ChatRoomMessage changeToRobotMsg(ChatRoomMessage message) {
        if (aitManager == null) {
            return message;
        }
        if (message.getMsgType() == MsgTypeEnum.robot) {
            return message;
        }
        String robotAccount = aitManager.getAitRobot();
        if (TextUtils.isEmpty(robotAccount)) {
            return message;
        }
        String text = message.getContent();
        String content = aitManager.removeRobotAitString(text, robotAccount);
        content = content.equals("") ? " " : content;
        message = ChatRoomMessageBuilder.createRobotMessage(roomId, robotAccount, text, RobotMsgType.TEXT, content, null, null);

        return message;
    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.scrollToBottom();
    }

    @Override
    public void shouldCollapseInputPanel() {
        inputPanel.collapse(false);
    }

    @Override
    public void onItemFooterClick(IMMessage message) {
        if (aitManager != null) {
            RobotAttachment attachment = (RobotAttachment) message.getAttachment();
            NimRobotInfo robot = NimUIKit.getRobotInfoProvider().getRobotByAccount(attachment.getFromRobotAccount());
            aitManager.insertAitRobot(robot.getAccount(), robot.getName(), inputPanel.getEditSelectionStart());
        }
    }

    @Override
    public boolean isLongClickEnabled() {
        return !inputPanel.isRecording();
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
        if (customization != null) {
            actions.addAll(customization.actions);
        }

        return actions;
    }

    public interface ChatRoomMsgLister{
        boolean onSendPopularityClick();
        void onReceivedPopularity();
        void onSignInMsg();
        void onSignOutMsg();
    }

    public void setChatRoomMsgLister(ChatRoomMsgLister chatRoomMsgLister) {
        this.chatRoomMsgLister = chatRoomMsgLister;
    }
}
