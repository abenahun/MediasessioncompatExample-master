package com.cui.mediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteController;
import android.media.session.MediaController;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.cui.mediaplayer.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ServiceConnection,
        SeekBar.OnSeekBarChangeListener, MediaPlayerHelper.MediaPlayerUpdateCallBack {
    //媒体控制
    private MediaControllerCompat mMediaController;
    private ActivityMainBinding binding;
    private AudioPlayerService musicService;
    private MediaPlayerHelper mMediaPlayerHelper;
    List<MusicEntity> list_music;


    private MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            Log.e("RunTest", "-------onPlaybackStateChanged--播放状态改变----???");
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_NONE://无任何状态
                    binding.imgPause.setImageResource(R.drawable.img_pause);
                    break;
                case PlaybackStateCompat.STATE_PLAYING:
                    binding.imgPause.setImageResource(R.drawable.img_pause);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    binding.imgPause.setImageResource(R.drawable.img_play);
                    break;
                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT://下一首
                    break;
                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS://上一首
                    break;
                case PlaybackStateCompat.STATE_FAST_FORWARDING://快进
                    break;
                case PlaybackStateCompat.STATE_REWINDING://快退
                    break;
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            Log.e("RunTest", "-------onMetadataChanged------???");
            binding.MusicTitle.setText(metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        }
    };
    private MediaBrowserCompat mMediaBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setViewOnclickListener(binding.imgLast, binding.imgNext, binding.imgPause);
        binding.progressSeek.setOnSeekBarChangeListener(this);

        list_music = getMusics();

        Intent intent = new Intent(this, AudioPlayerService.class);
        getApplication(). bindService(intent, this,0);


    }



    private void setViewOnclickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(MainActivity.this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_pause:
                if (mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                    mMediaController.getTransportControls().pause();
                } else if (mMediaController.getPlaybackState().getState() == PlaybackStateCompat.STATE_PAUSED) {
                    mMediaController.getTransportControls().play();
                } else {
                    mMediaController.getTransportControls().playFromSearch("", null);
                }
                Log.e("RunTest", "-----暂停--------???");
                break;
            case R.id.img_last:
                mMediaController.getTransportControls().skipToPrevious();
                Log.e("RunTest", "---------上一个----???");
                break;
            case R.id.img_next:
                mMediaController.getTransportControls().skipToNext();
                Log.e("RunTest", "---------下一个----???");
                break;
        }

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.e("RunTest4", "-------主页服务连接------???"+iBinder);
        if (iBinder instanceof AudioPlayerService.ServiceBinder) {
            try {
                //获取服务
                musicService = ((AudioPlayerService.ServiceBinder) iBinder).getService();
                //获取帮助类
                mMediaPlayerHelper = musicService.getMediaPlayerHelper();
                //设置媒体播放回键听
                mMediaPlayerHelper.setMediaPlayerUpdateListener(this);
                //设置数据源
                mMediaPlayerHelper.setPlayeData(list_music);
                //设置更新的seekBaar
                mMediaPlayerHelper.setSeekBar(binding.progressSeek);
                //设置媒体控制器,通过sessionToken获取媒体控制器,通过控制器获取信息
                mMediaController = new MediaControllerCompat(MainActivity.this,
                        musicService.getMediaSessionToken());
                //注册回调
                mMediaController.registerCallback(mMediaControllerCallback);

                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (mMediaController.getMetadata() != null) {
                                Set<String> set = ((MediaMetadataCompat) mMediaController.getMetadata()).keySet();
                                for (String strig : set) {
                                    Log.e("RunTest", "-------歌曲作者------???" + strig);
                                }
                            } else {
                                Log.e("RunTest", "-------数据集为空------???");
                            }

                        }

                    }
                }.start();
            } catch (Exception e) {
                Log.e(getClass().getName(), "serviceConnectedException==" + e.getMessage());
            }
        }
    }

    private List<MusicEntity> getMusics() {
        List<MusicEntity> list = new ArrayList<MusicEntity>();
        MusicEntity entity = new MusicEntity();
        entity.setUrl("http://96.ierge.cn/14/211/422309.mp3");
        entity.setAlbum("Stranger Under My Skin");
        entity.setMusicTitle("六月飞霜");
        entity.setSinger("陈奕迅");
        list.add(entity);
        MusicEntity entity1 = new MusicEntity();
        entity1.setUrl("http://96.ierge.cn/13/209/419229.mp3");
        entity1.setAlbum("上五楼的快乐生活");
        entity1.setMusicTitle("心的距离");
        entity1.setSinger("陈奕迅");
        list.add(entity1);
        return list;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    protected void onResume() {
        Log.e("RunTest4","----onResume--mMediaBrowser客户端-------???"+mMediaBrowser.isConnected());

        QQMusicReciver mbr = new QQMusicReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.metachanged");
        intentFilter.addAction("com.android.music.queuechanged");
        intentFilter.addAction("com.android.music.playbackcomplete");
        intentFilter.addAction("com.android.music.playstatechanged");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_RATE_DIALOG_ADD_LISTEN_COUNT");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_PLAY_STARTED.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_START_PLAYSONG.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_META_CHANGED.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_PLAYSONG_CHANGED.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_SONG_PLAY_STOPPING.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_PLAYLIST_CHANGED.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_BACKGROUND_FOREGROUND_STATE_CHANGED.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv");
        intentFilter.addAction("wns.heartbeat");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_SEARCH_BEGIN.QQMusicTV");
        intentFilter.addAction("com.tencent.qqmusictv.ACTION_SEARCH_FINISH.QQMusicTV");
        intentFilter.addAction("android.media.AUDIO_BECOMING_NOISY");
//        intentFilter.addAction("");
//        intentFilter.addAction("");
//        intentFilter.addAction("");
//        intentFilter.addAction("");
        registerReceiver(mbr, intentFilter);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioPlayerService.class);
        startService(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //更新时实播放进度
        binding.timeLeft.setText(turnTime(progress));

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //更新拖动进度
        mMediaPlayerHelper.getMediaPlayer().seekTo(
                seekBar.getProgress() * mMediaPlayerHelper.getMediaPlayer().getDuration()
                        / seekBar.getMax());
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //自动播放下一首
        mMediaController.getTransportControls().skipToNext();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        //设置二级缓冲显示位置。
        binding.progressSeek.setSecondaryProgress(percent);
//        int currentProgress = seekBar.getMax()
//                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
//        Log.e(getClass().getName(),currentProgress + "% play播放进度", percent + " buffer-缓冲进度");
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        int musicTime = mediaPlayer.getDuration() / 1000;
        int minute = musicTime / 60;
        int second = musicTime % 60;
        binding.timeRight.setText(minute + ":" + (second > 9 ? second : "0" + second));
    }

    /**
     * 秒转为分:秒
     *
     * @param second
     * @return
     */
    public String turnTime(int second) {
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }
        return (d > 0 ? d > 9 ? d : "0" + d : "00") + ":" + (s > 9 ? s : "0" + s);
    }

    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------





    RemoteController.OnClientUpdateListener mExternalClientUpdateListener =
            new RemoteController.OnClientUpdateListener() {
                @Override
                public void onClientChange(boolean clearing) {

                    // Log.e(TAG, "onClientChange()...");

                }

                @Override
                public void onClientPlaybackStateUpdate(int state) {

                    // Log.e(TAG, "onClientPlaybackStateUpdate()...");
                }

                @Override

                public void onClientPlaybackStateUpdate(int state, long stateChangeTimeMs, long currentPosMs, float speed) {

                    // Log.e(TAG, "onClientPlaybackStateUpdate()...");
                }

                @Override

                public void onClientTransportControlUpdate(int transportControlFlags) {

                    // Log.e(TAG, "onClientTransportControlUpdate()...");

                }

                @Override

                public void onClientMetadataUpdate(RemoteController.MetadataEditor metadataEditor) {

                    String artist = metadataEditor.

                            getString(MediaMetadataRetriever.METADATA_KEY_ARTIST, "null");

                    String album = metadataEditor.

                            getString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "null");

                    String title = metadataEditor.

                            getString(MediaMetadataRetriever.METADATA_KEY_TITLE, "null");

                    Long duration = metadataEditor.

                            getLong(MediaMetadataRetriever.METADATA_KEY_DURATION, -1);

                    Bitmap defaultCover = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass);

                    Bitmap bitmap = metadataEditor.

                            getBitmap(RemoteController.MetadataEditor.BITMAP_KEY_ARTWORK, defaultCover);


                    Log.e("结果为:", "artist:" + artist

                            + "album:" + album

                            + "title:" + title

                            + "duration:" + duration);
                }
            };

}