package com.example.pan.mobileplayer.activity.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.pan.mobileplayer.R;
import com.example.pan.mobileplayer.activity.domain.MediaItem;
import com.example.pan.mobileplayer.activity.utils.LogUtil;
import com.example.pan.mobileplayer.activity.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by pan on 2018/9/12.
 * 自定义系统播放器
 */
public class MyselfVideoPlayer extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1; //视频进度的更新
    private static final int HIDE_MEDIA_CONTROLLER = 2;//隐藏播放控制器面板
    private VideoView videoview;
    private Uri uri;
    private LinearLayout llTop;
    private LinearLayout llTopNtb;
    private TextView tvTopName;
    private ImageView ivTopBattery;
    private TextView tvTopTime;
    private LinearLayout llTopShenyin;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnPlayer;
    private LinearLayout llBottom;
    private TextView tvInitiate;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSiwchScreen;
    private Utils utils;
    private MyReceiver myReceiver;

    private ArrayList<MediaItem> mediaItems;//传进来的视频列表
    private int position;                   //要播放的列表中视频的具体位置
    private GestureDetector detector;    //定义手势识别器
    private RelativeLayout media_controller;  //实例化media_controller
    private boolean isShowMediacontroller = false;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-09-13 23:19:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        llTopNtb = (LinearLayout) findViewById(R.id.ll_top_ntb);
        tvTopName = (TextView) findViewById(R.id.tv_top_name);
        ivTopBattery = (ImageView) findViewById(R.id.iv_top_battery);
        tvTopTime = (TextView) findViewById(R.id.tv_top_time);
        llTopShenyin = (LinearLayout) findViewById(R.id.ll_top_shenyin);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnPlayer = (Button) findViewById(R.id.btn_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvInitiate = (TextView) findViewById(R.id.tv_initiate);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (Button) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSiwchScreen = (Button) findViewById(R.id.btn_video_siwch_screen);
        videoview = (VideoView) findViewById(R.id.videoview);
        media_controller = (RelativeLayout) findViewById(R.id.media_controller);


        btnVoice.setOnClickListener(this);
        btnPlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSiwchScreen.setOnClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-09-13 23:19:29 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
        } else if (v == btnPlayer) {
            // Handle clicks for btnPlayer
        } else if (v == btnExit) {
            // Handle clicks for btnExit  退出
            finish();
        } else if (v == btnVideoPre) {
            // Handle clicks for btnVideoPre
            PrePlayer();
        } else if (v == btnVideoStartPause) {
            setStartPause();
        } else if (v == btnVideoNext) {
            // Handle clicks for btnVideoNext
            NextPlayer();
        } else if (v == btnVideoSiwchScreen) {
            // Handle clicks for btnVideoSiwchScreen
        }
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER,4000);
    }

    private void setStartPause() {
        //设置开始--暂停
        if (videoview.isPlaying()) {
            videoview.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        } else {
            videoview.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个按钮
     */
    private void PrePlayer() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放上一个
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                tvTopName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());
                //设置按钮状态,变为不可按
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态,变为不可按
            setButtonState();
        }
    }

    /**
     * 播放下一个按钮
     */
    private void NextPlayer() {
        if (mediaItems != null && mediaItems.size() > 0) {
            //播放下一个
            position++;

            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                tvTopName.setText(mediaItem.getName());
                videoview.setVideoPath(mediaItem.getData());
                //设置按钮状态,变为不可按
                setButtonState();
            }
        } else if (uri != null) {
            //设置按钮状态,变为不可按
            setButtonState();
        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                ButtonSwitch(false);
            } else if (mediaItems.size() == 2) {
                if (position == 0) {
                    //刚播放第一个视频时上一个按钮不可点击,下一个可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                } else if (position == mediaItems.size() - 1) {
                    //播放到最后一个视频时下一个按钮不可点击，上一个按钮可点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            } else {
                if (position == 0) {
                    //刚播放第一个视频时上一个按钮不可点击
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {
                    // /播放到最后一个视频时下一个按钮不可点击
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                } else {
                    ButtonSwitch(true);//其余情况下都可点击
                }
            }

        } else if (uri != null) {
            //按钮设置灰色-不可按
            ButtonSwitch(false);
        }
    }

    private void ButtonSwitch(boolean isSwitch) {
        if (isSwitch == true) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        } else if ((isSwitch == false)) {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HIDE_MEDIA_CONTROLLER://隐藏控制面板
                    hideMediacontroller();
                    break;
                case PROGRESS:
                    //得到当前的视频播放进度
                    int currentPosition = videoview.getCurrentPosition();
                    //当前的进度
                    seekbarVideo.setProgress(currentPosition);

                    //更新文本进度
                    tvInitiate.setText(utils.stringForTime(currentPosition));

                    tvTopTime.setText(getSystemTime());

                    //每秒更新一次
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);//延迟1秒
                    break;

            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat DateFormat = new SimpleDateFormat("HH:mm:ss");//系统时间格式
        return DateFormat.format(new Date());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //初始化父类

        inintData();

        findViews();
        videoview = (VideoView) findViewById(R.id.videoview);

        setListener();
        //得到播放地址
        getData();
        setData();

//        videoview.setMediaController(new MediaController(this));    //调用系统的控制面板
    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            tvTopName.setText(mediaItem.getName());   //设置视频的名称
            videoview.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            tvTopName.setText(uri.toString());
            videoview.setVideoURI(uri);
        } else {
            Toast.makeText(MyselfVideoPlayer.this, "没有数据传递！！！!", Toast.LENGTH_SHORT).show();
        }
        setButtonState();
    }

    private void getData() {
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videolist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void inintData() {
        utils = new Utils();
        //注册电量广播
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量发生变化时发送广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myReceiver, intentFilter);

        //实例化手势识别器，重写方法手势- 长按 - 单击 - 双击 -
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            //双击手势
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                return super.onDoubleTap(e);
            }

            //长按手势
            @Override
            public void onLongPress(MotionEvent e) {
                setStartPause();
                super.onLongPress(e);
            }

            //单击手势
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediacontroller) {
                    hideMediacontroller();
                    //发消息移除隐藏
                    handler.removeMessages(HIDE_MEDIA_CONTROLLER);
                } else {
                    ShowMediacontroller();
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0); //电量等级0-100
            setBattery(level);

        }
    }

    public void setBattery(int level) {
        if (level <= 0) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivTopBattery.setImageResource(R.drawable.ic_battery_100);
        } else {
            ivTopBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    private void setListener() {
        //准备好的监听
        videoview.setOnPreparedListener(new MyOnPreparedListener());
        //播放出错了的监听
        videoview.setOnErrorListener(new MyOnErrorListener());
        //播放完成了的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());
        //设置SeeKbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
    }

    class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                videoview.seekTo(i);
            }
        }
        /**
         * 手指触碰后回调这个方法
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }
        /**
         *手指离开后回调这个方法
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            /**
             * 开始播放
             */
            videoview.start();
//            mediaPlayer.getDuration();
            //视频的总时长,关联总长度
            int duration = videoview.getDuration();
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));
            hideMediacontroller();//默认隐藏控制面板

            //发消息
            handler.sendEmptyMessage(PROGRESS);
        }
    }

    class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
            Toast.makeText(MyselfVideoPlayer.this, "播放出错了", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //    Toast.makeText(MyselfVideoPlayer.this, "播放结束了" + uri, Toast.LENGTH_SHORT).show();
            NextPlayer();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("onRestart--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("onStart--");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("onResume--");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("onPause--");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("onStop--");
    }

    @Override
    protected void onDestroy() {
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        super.onDestroy();
        LogUtil.e("onDestroy--");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);  //把事件传递到手势识别器
        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void ShowMediacontroller() {
        media_controller.setVisibility(View.VISIBLE);
        isShowMediacontroller = true;
    }
    /**
     * 隐藏控制面板
     */
    private void hideMediacontroller() {
        media_controller.setVisibility(View.GONE);
        isShowMediacontroller = false;
    }
}
