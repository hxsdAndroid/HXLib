package com.dou361.ijkplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.Display;

/**
 * Created by qiye on 2017/1/20.
 */

public class DensityUtil {
    private DensityUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static int getScreenWidth(Context context) {
        Display display = ((Activity) context).getWindowManager()
                .getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        if (context==null){
            return 0;
        }
        if( ((Activity) context).getWindowManager()==null){
            return 0;
        }
        Display display = ((Activity) context).getWindowManager()
                .getDefaultDisplay();
        if (display==null){
            return 0;
        }
        return display.getHeight();
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        if (context==null){
            return 0;
        }
        if (context.getResources()==null){
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(Context context, float spVal) {
        if (context==null){
            return 0;
        }
        if (context.getResources()==null){
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     */
    public static float px2dp(Context context, float pxVal) {
        if (context==null){
            return 0;
        }
        if (context.getResources()==null){
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     */
    public static float px2sp(Context context, float pxVal) {
        if (context==null){
            return 0;
        }
        if (context.getResources()==null){
            return 0;
        }
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }


}
