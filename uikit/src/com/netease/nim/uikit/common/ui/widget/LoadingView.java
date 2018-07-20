package com.netease.nim.uikit.common.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.netease.nim.uikit.R;


/**
 * 类似于 音乐播放器的圆形图片 周边的进度条
 * 只需要设置图片  然后设置进度就行
 * 圆角显示图片  来自CircleImageView
 * 在基础上修改
 * Created by Daemon on 2015/12/17.
 */
public class LoadingView extends ImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.RED;
    private static final int DEFAULT_BORDER_COLOR_BG = Color.TRANSPARENT;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private final Paint mBorderPaint_bg = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderColor_bg = DEFAULT_BORDER_COLOR_BG;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private int mAcImage ;
    private int mAcImageNoClick ;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mBorderRadius;
    private float mDrawableRadius;

    private boolean mReady;


    private boolean mSetupPending;
    private float newAngle;
    private int coolingTime=60000;
    ValueAnimator valueAnimator;


    public LoadingView(Context context) {
        super(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GifLoadingView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.GifLoadingView_AcTextSize, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.GifLoadingView_AcTextColor, DEFAULT_BORDER_COLOR);
        mBorderColor_bg = a.getColor(R.styleable.GifLoadingView_AcTextBgColor, DEFAULT_BORDER_COLOR_BG);
        mAcImage=a.getResourceId(R.styleable.GifLoadingView_imageSrc,R.drawable.bottle_normal);
        mAcImageNoClick=a.getResourceId(R.styleable.GifLoadingView_imageSrcNoClick,R.drawable.bottle_unable);
        a.recycle();
        setImageResource(mAcImage);
        mReady = true;
        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    /**
     * 取消倒计时
     */
    public void oncancel() {
        if (valueAnimator!=null){
            valueAnimator.cancel();
            setProgress(0);
        }
    }

    /**
     * 开始倒计时
     */
    public void restart() {
        onStart();
        setEnabled(false);
        setClickable(false);
        setImageResource(mAcImageNoClick);
    }

    public int getCoolingTime() {
        return coolingTime;
    }

    public void setCoolingTime(int coolingTime) {
        this.coolingTime = coolingTime;
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format(
                    "ScaleType %s not supported.", scaleType));
        }
    }


    private void  onStart(){
        valueAnimator=ValueAnimator.ofFloat(0,360);
        valueAnimator.setTarget(this);
        valueAnimator.setDuration(coolingTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setProgress(0);
                setEnabled(true);
                setClickable(true);
                setImageResource(mAcImage);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                setProgress(0);
                setEnabled(true);
                setClickable(true);
                setImageResource(mAcImage);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((Float) animation.getAnimatedValue());
            }

        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null) {
            return;
        }
        if (mBorderWidth != 0) {
            //周边底部颜色 一般为白色
            canvas.drawArc(mBorderRect, -90, 360, false, mBorderPaint_bg);
            //设置了周边弧度的宽度 每次重新绘制都要画上边上的弧度
            canvas.drawArc(mBorderRect, -90, newAngle, false, mBorderPaint);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }


    public void setProgress(float progress) {
        this.newAngle = progress;
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
                        COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                // 为0就自己加上需要的 要改不 就 传值 变化 或者 这里可以先测量一下？
                if (drawable.getIntrinsicWidth() <= 0) {
                    int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                    measure(w, h);
                    int height = getMeasuredHeight();
                    int width = getMeasuredWidth();
                    bitmap = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
                } else {
                    bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
                }
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBorderPaint_bg.setStyle(Paint.Style.STROKE);
        mBorderPaint_bg.setAntiAlias(true);
        mBorderPaint_bg.setColor(mBorderColor_bg);
        mBorderPaint_bg.setStrokeWidth(mBorderWidth);


        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(mBorderWidth, mBorderWidth, getWidth() - mBorderWidth, getHeight() - mBorderWidth);

        mDrawableRect.set(mBorderWidth/2, mBorderWidth/2, mBorderRect.width()
                - mBorderWidth/2, mBorderRect.height() - mBorderWidth/2);

        mDrawableRadius = Math.min(mDrawableRect.height() / 2,
                mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width()
                * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((dx + 0.5f) + mBorderWidth,
                (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}