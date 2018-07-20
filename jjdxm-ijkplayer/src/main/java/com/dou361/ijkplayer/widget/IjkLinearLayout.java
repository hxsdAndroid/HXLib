package com.dou361.ijkplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by zhangming on 2017/2/28.
 */

public class IjkLinearLayout extends LinearLayout {

    public IjkLinearLayout(Context context) {
        super(context);

    }

    public IjkLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public IjkLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        return true;
    }
}
