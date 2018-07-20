package com.dou361.ijkplayer.listener;

import com.dou361.ijkplayer.widget.PlayerView;

/**
 * Created by muwenlei on 2018/1/18.
 * 弹幕播放器回调
 */

public interface OnDanmuPlayListener {
    /**
     * 设置弹幕字体
     * @param textSize
     */
    void onSetDanmuTextSize(int textSize);

    /**
     * 设置弹幕开关
     * @param flag true 开  false 关
     */
    void onDanmuSwitch(boolean flag);

    /**
     * 发送弹幕
     * @param content
     */
    void onSendDanmu(String content);

    /**
     * 发送修为瓶
     */
    void onSendGift();
}
