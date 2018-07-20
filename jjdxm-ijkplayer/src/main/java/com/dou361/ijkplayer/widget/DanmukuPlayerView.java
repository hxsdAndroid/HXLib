package com.dou361.ijkplayer.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dou361.ijkplayer.R;
import com.dou361.ijkplayer.adapter.DanamakuAdapter;
import com.dou361.ijkplayer.listener.OnDanmuPlayListener;
import com.dou361.ijkplayer.utils.BiliDanmukuParser;
import com.dou361.ijkplayer.utils.DensityUtil;

import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;

/**
 * Created by muwenlei on 2018/1/15.
 */

public class DanmukuPlayerView extends PlayerView {
    private BaseDanmakuParser mParser;//解析器对象
    private IDanmakuView mDanmakuView;//弹幕view
    private DanmakuContext mDanmakuContext;
    private long mDanmakuStartSeekPosition = -1;

    private boolean portrait = true;
    /**
     * 弹幕设置字体大小的三种样式（只支持三种）
     */
    private View llDanmu;
    private TextView txtDanmu1;
    private TextView txtDanmu2;
    private TextView txtDanmu3;

    /**
     * 弹幕下面的输入空间等
     */

    private View lldanmuCon;
    private ImageView imgdanmuSwitch;
    private EditText editdanmuContent;
    private ImageView imgdanmuSend;
    private LoadingView lvGift;

    private OnDanmuPlayListener onDanmuPlayListener;

    private int curDanmuTextSize = PlayStateParams.DANMU_TEXTSIZE_MEDIUM;

    private boolean mDanmaKuShow = true;

    public DanmukuPlayerView(final Activity activity, View rootView) {
        super(activity, rootView);

        if (rootView == null) {
            mDanmakuView = (IDanmakuView) mActivity.findViewById(R.id.danmaku_view);
            llDanmu = mActivity.findViewById(R.id.ll_danmu);
            txtDanmu1 = (TextView) mActivity.findViewById(R.id.txt_dmbig);
            txtDanmu2 = (TextView) mActivity.findViewById(R.id.txt_dmmedium);
            txtDanmu3 = (TextView) mActivity.findViewById(R.id.txt_dmsmall);
            lldanmuCon = mActivity.findViewById(R.id.ll_danmucon);
            imgdanmuSwitch = (ImageView) mActivity.findViewById(R.id.img_danmuswitch);
            editdanmuContent = (EditText) mActivity.findViewById(R.id.edit_content);
            imgdanmuSend = (ImageView) mActivity.findViewById(R.id.img_danmusend);
            lvGift = (LoadingView) mActivity.findViewById(R.id.input_gift);
        } else {
            mDanmakuView = (IDanmakuView) rootView.findViewById(R.id.danmaku_view);
            llDanmu = rootView.findViewById(R.id.ll_danmu);
            txtDanmu1 = (TextView) rootView.findViewById(R.id.txt_dmbig);
            txtDanmu2 = (TextView) rootView.findViewById(R.id.txt_dmmedium);
            txtDanmu3 = (TextView) rootView.findViewById(R.id.txt_dmsmall);

            lldanmuCon = rootView.findViewById(R.id.ll_danmucon);
            imgdanmuSwitch = (ImageView) rootView.findViewById(R.id.img_danmuswitch);
            editdanmuContent = (EditText) rootView.findViewById(R.id.edit_content);
            imgdanmuSend = (ImageView) rootView.findViewById(R.id.img_danmusend);
            lvGift = (LoadingView) rootView.findViewById(R.id.input_gift);
        }
        llDanmu.setVisibility(View.VISIBLE);
//        lldanmuCon.setVisibility(View.VISIBLE);
        //初始化弹幕显示
        initDanmaku();
        initDanClickViews();

        initBottomControl();
        editdanmuContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String temp = editdanmuContent.getText().toString().trim();
                    if (TextUtils.isEmpty(temp)) {
                        Toast.makeText(mContext, "请输入内容！", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if (onDanmuPlayListener != null) {
                        onDanmuPlayListener.onSendDanmu(temp);
                    }
                    editdanmuContent.setText("");
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editdanmuContent.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    /*******************************对外暴露方法 start******************************/

    /**
     * 设置发礼物的冷却时间
     *
     * @param time
     */
    public void setCoolingTime(int time) {
        lvGift.setCoolingTime(time);
    }

    /**
     * 设置送修为瓶开始冷却
     */
    public void setGiftCooing(){
        lvGift.restart();
    }

    /**
     * 添加文本弹幕数据
     */
    public void addDanmaku(String temp, boolean mySelf) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        if (!TextUtils.isEmpty(temp)) {
            if (mySelf){
                danmaku.text = "\u3000"+temp+"\u3000";
            }else{
                danmaku.text = temp;
            }

        } else {
            danmaku.text = "这是一条普通弹幕 ";
        }
        danmaku.padding = (int) (7*(mParser.getDisplayer().getDensity() - 0.6f));
        danmaku.priority = 4;  // 可能会被各种过滤器过滤并隐藏显示，所以提高等级
        danmaku.isLive = true;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 500);
        danmaku.textSize = curDanmuTextSize * (mParser.getDisplayer().getDensity() - 0.6f);
        if (mySelf) {
            danmaku.textColor = Color.parseColor("#F9CE7C");
            danmaku.textShadowColor=0;
            danmaku.borderColor = Color.parseColor("#F9CE7C");
            danmaku.rotationY=DensityUtil.dp2px(mContext,2);
            danmaku.rotationZ=DensityUtil.dp2px(mContext,2);
            danmaku.borderRaduis= (int) ((curDanmuTextSize+14) * (mParser.getDisplayer().getDensity() - 0.6f))+1;
        } else {
            danmaku.textColor = Color.parseColor("#999999");
            danmaku.textShadowColor =Color.parseColor("#7F000000");
            danmaku.borderColor = Color.TRANSPARENT;
            danmaku.borderRaduis=0;
        }

        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * 添加修为瓶弹幕
     */
    public void addGifDanmaku(String temp, boolean islive) {
        BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || mDanmakuView == null) {
            return;
        }
        String text = "\u2000[瓶子]";
        Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.danmu_xiuweiping);
//        drawable1.setBounds(DensityUtil.dp2px(mContext,3), -(int) (7*(mParser.getDisplayer().getDensity() - 0.6f)),(int) ((curDanmuTextSize+14) * (mParser.getDisplayer().getDensity() - 0.6f))+DensityUtil.dp2px(mContext,3), (int) ((curDanmuTextSize+7) * (mParser.getDisplayer().getDensity() - 0.6f)));
        drawable1.setBounds(0, -(int)spToPixels(mContext,curDanmuTextSize/2),(int)spToPixels(mContext,curDanmuTextSize+curDanmuTextSize), (int)spToPixels(mContext,curDanmuTextSize/2+curDanmuTextSize));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable1);
        spannableStringBuilder.setSpan(span, 1, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        if (!TextUtils.isEmpty(temp)) {
//            danmaku.text = temp;
//        } else {
//            danmaku.text = "这是一条修为瓶弹幕 ";
//        }
        String showTip="\u2000"+temp+"\u3000";
        spannableStringBuilder.append(showTip);

//        AbsoluteSizeSpan spanSize = new AbsoluteSizeSpan((int) spToPixels(mContext, 14+curDanmuTextSize));
//        spannableStringBuilder.setSpan(spanSize,0,text.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//
//        AbsoluteSizeSpan spanSize2 = new AbsoluteSizeSpan((int) spToPixels(mContext, curDanmuTextSize));
//        spannableStringBuilder.setSpan(spanSize2,text.length(),spannableStringBuilder.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);

//        danmaku.padding = (int) (6*(mParser.getDisplayer().getDensity() - 0.6f));
        danmaku.padding = 0;
        danmaku.priority = 1;  // 可能会被各种过滤器过滤并隐藏显示，所以提高等级
        danmaku.isLive = islive;
        danmaku.setTime(mDanmakuView.getCurrentTime() + 500);
//        danmaku.textSize = (int)spToPixels(mContext,curDanmuTextSize);
        danmaku.textSize = spToPixels(mContext, curDanmuTextSize);
        danmaku.textColor = Color.parseColor("#ffffff");
        danmaku.text = spannableStringBuilder;
        danmaku.background=Color.parseColor("#0A67D7");
        danmaku.textShadowColor = 0;
        danmaku.borderRaduis= (int) ((curDanmuTextSize+14) * (mParser.getDisplayer().getDensity() - 0.6f))+1;
        mDanmakuView.addDanmaku(danmaku);
    }

    /**
     * SP 转 Pixels
     *
     * @param context 上下文
     * @param sp      sp 字体大小
     * @return pixels
     */
    public static float spToPixels(Context context, float sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDensity;
    }

    /**
     * 设置当前弹幕字体大小
     *
     * @param curDanmuTextSize
     */
    public void setCurDanmuTextSize(int curDanmuTextSize) {
        this.curDanmuTextSize = curDanmuTextSize;
        refushDanmuSize();
    }

    /**
     * 设置弹幕库是否显示
     *
     * @param flag true 显示 false 不显示
     */
    public void setmDanmaKuShow(boolean flag) {
        this.mDanmaKuShow = flag;
        resolveDanmakuShow();
    }


    /**
     * 设置弹幕字体点击事件监听
     *
     * @return
     */
    public void setOnDanmuPlayListener(OnDanmuPlayListener onDanmuPlayListener) {
        this.onDanmuPlayListener = onDanmuPlayListener;
    }


    /*******************************对外暴露方法 end******************************/


    /********************************私有控制方法 start***********************************/
    /**
     * 弹幕的显示与关闭
     */
    private void resolveDanmakuShow() {
        rl_box.post(new Runnable() {
            @Override
            public void run() {
                if (portrait) {
                    if (getDanmakuView().isShown()) {
                        getDanmakuView().hide();
                    }
                } else {
                    if (mDanmaKuShow) {
                        if (!getDanmakuView().isShown())
                            getDanmakuView().show();
                    } else {
                        if (getDanmakuView().isShown()) {
                            getDanmakuView().hide();
                        }
                    }
                }

            }
        });
        refushDanmuSwitch();
    }

    /**
     * 开始播放弹幕
     */
    private void onPrepareDanmaku(DanmukuPlayerView danmakuPlayerView) {
        if (danmakuPlayerView.getDanmakuView() != null && !danmakuPlayerView.getDanmakuView().isPrepared()) {
            danmakuPlayerView.getDanmakuView().prepare(danmakuPlayerView.getParser(),
                    danmakuPlayerView.getDanmakuContext());
        }
    }

    /**
     * 弹幕偏移
     */
    private void resolveDanmakuSeek(DanmukuPlayerView danmakuPlayerView, long time) {
        if (currentUrl != null
                && danmakuPlayerView.getDanmakuView() != null && danmakuPlayerView.getDanmakuView().isPrepared()) {
            danmakuPlayerView.getDanmakuView().seekTo(time);
        }
    }

    /**
     * 释放弹幕控件
     */
    private void releaseDanmaku(DanmukuPlayerView danmakuVideoPlayer) {
        if (danmakuVideoPlayer != null && danmakuVideoPlayer.getDanmakuView() != null) {
            danmakuVideoPlayer.getDanmakuView().release();
        }
    }

    private BaseDanmakuParser getParser() {
        return mParser;
    }

    private DanmakuContext getDanmakuContext() {
        return mDanmakuContext;
    }

    private IDanmakuView getDanmakuView() {
        return mDanmakuView;
    }

    private long getDanmakuStartSeekPosition() {
        return mDanmakuStartSeekPosition;
    }

    private void setDanmakuStartSeekPosition(long danmakuStartSeekPosition) {
        this.mDanmakuStartSeekPosition = danmakuStartSeekPosition;
    }


    /********************************私有控制方法 end***********************************/

    /****************************界面控制 start***************************************/
    /**
     * 刷新弹幕选择
     */
    private void refushDanmuSize() {
        txtDanmu1.setSelected(false);
        txtDanmu2.setSelected(false);
        txtDanmu3.setSelected(false);
        if (curDanmuTextSize == PlayStateParams.DANMU_TEXTSIZE_BIG) {
            txtDanmu1.setSelected(true);
        } else if (curDanmuTextSize == PlayStateParams.DANMU_TEXTSIZE_MEDIUM) {
            txtDanmu2.setSelected(true);
        } else if (curDanmuTextSize == PlayStateParams.DANMU_TEXTSIZE_SMALL) {
            txtDanmu3.setSelected(true);
        }
    }

    /**
     * 刷新弹幕开关界面
     */
    private void refushDanmuSwitch() {
        imgdanmuSwitch.setSelected(mDanmaKuShow);
    }

    /****************************界面控制 end***************************************/


    /********************************初始化 start*************************/
    /**
     * 初始化聊天控件
     */
    private void initBottomControl() {
        refushDanmuSwitch();
        imgdanmuSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDanmuPlayListener != null) {
                    onDanmuPlayListener.onDanmuSwitch(!mDanmaKuShow);
                }
                setmDanmaKuShow(!mDanmaKuShow);
            }
        });

        imgdanmuSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = editdanmuContent.getText().toString().trim();
                if (TextUtils.isEmpty(temp)) {
                    Toast.makeText(mContext, "请输入内容！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (onDanmuPlayListener != null) {
                    onDanmuPlayListener.onSendDanmu(temp);
                }
                editdanmuContent.setText("");
            }
        });

        lvGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvGift.restart();
                if (onDanmuPlayListener != null) {
                    onDanmuPlayListener.onSendGift();
                }
            }
        });
        refushDanmuSwitch();
    }

    /**
     * 设置弹幕选择
     */
    private void initDanClickViews() {
        View.OnClickListener danmuClicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideStreamSelectView();
                if (v.getId() == txtDanmu1.getId()) {
                    curDanmuTextSize = PlayStateParams.DANMU_TEXTSIZE_BIG;
                } else if (v.getId() == txtDanmu2.getId()) {
                    curDanmuTextSize = PlayStateParams.DANMU_TEXTSIZE_MEDIUM;
                } else if (v.getId() == txtDanmu3.getId()) {
                    curDanmuTextSize = PlayStateParams.DANMU_TEXTSIZE_SMALL;
                }
                setCurDanmuTextSize(curDanmuTextSize);
                if (onDanmuPlayListener != null) {
                    onDanmuPlayListener.onSetDanmuTextSize(curDanmuTextSize);
                }
                refushDanmuSize();
            }
        };
        txtDanmu1.setOnClickListener(danmuClicklistener);
        txtDanmu2.setOnClickListener(danmuClicklistener);
        txtDanmu3.setOnClickListener(danmuClicklistener);
        refushDanmuSize();
    }

    private void initDanmaku() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        DanamakuAdapter danamakuAdapter = new DanamakuAdapter(mDanmakuView);
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_SHADOW,  DensityUtil.dp2px(mContext,1)).setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                .setCacheStuffer(new SpannedCacheStuffer(), danamakuAdapter) // 图文混排使用SpannedCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair).setDanmakuMargin(40);
        if (mDanmakuView != null) {
            //todo 替换成你的数据流
//            mParser = createParser(mContext.getResources().openRawResource(R.raw.comments));
            mParser = createParser(null);
            mDanmakuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    if (getDanmakuView() != null) {
                        getDanmakuView().start();
                        if (getDanmakuStartSeekPosition() != -1) {
                            resolveDanmakuSeek(DanmukuPlayerView.this, getDanmakuStartSeekPosition());
                            setDanmakuStartSeekPosition(-1);
                        }
                        resolveDanmakuShow();
                    }
                }
            });
            mDanmakuView.enableDanmakuDrawingCache(true);
        }
    }

    /**
     * 创建解析器对象，解析输入流
     *
     * @param stream
     * @return
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);
        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;

    }


    /********************************初始化 end*************************/


    /********************************方法的重写 start*************************/
    @Override
    public PlayerView onConfigurationChanged(Configuration newConfig) {
        portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        resolveDanmakuShow();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 横向
            lldanmuCon.setVisibility(View.VISIBLE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 纵向
            lldanmuCon.setVisibility(View.GONE);
        }
        return super.onConfigurationChanged(newConfig);
    }


    @Override
    public PlayerView startPlay() {
        onPrepareDanmaku(this);
        return super.startPlay();
    }

    @Override
    public PlayerView startPlayNoLoading() {
        onPrepareDanmaku(this);
        return super.startPlayNoLoading();
    }

    @Override
    public PlayerView onPause() {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
        return super.onPause();
    }

    @Override
    public PlayerView onResume() {
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }
        return super.onResume();
    }

    /********************************方法的重写 end*************************/

}
