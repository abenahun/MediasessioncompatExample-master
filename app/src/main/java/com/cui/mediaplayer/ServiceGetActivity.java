package com.cui.mediaplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class ServiceGetActivity extends AppCompatActivity implements ServiceConnection {

    private MediaBrowserCompat mMediaBrowser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_serviceget);

        mMediaBrowser = new MediaBrowserCompat(this,
                new ComponentName("com.example.android.uamp", "com.example.android.uamp.MusicService"),
                mConnectionCallback, null);
        mMediaBrowser.connect();

        Log.e("RunTestT", "------mMediaBrowser客户端-------???" + mMediaBrowser.isConnected());

        Intent intent1 = new Intent();
        intent1.setComponent(new ComponentName("com.example.android.uamp", "com.example.android.uamp.MusicService"));
        intent1.putExtra("qwer", "我来自另一个应用");
        startService(intent1);
//        bindService(intent1, this,0);
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        Log.e("RunTestT", "----MediaBrowserCompat连接成功---000------???" + mMediaBrowser.getSessionToken());
//                        connectToSession(mMediaBrowser.getSessionToken());
                        String mediaId=mMediaBrowser.getRoot();
                        mMediaBrowser.unsubscribe(mediaId);
                        Log.e("RunTestT", "----发起订阅---------???");
                        mMediaBrowser.subscribe(mediaId,BrowserSubscriptionCallback);
                    } catch (Exception e) {
                        Log.e("RunTestT", "----连接出错---------???");
                    }
                }
            };

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.e("RunTestT", "-------连接服务回调------???");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("RunTestT", "-------解除连接服务回调------???");

    }

    private void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mediaController = new MediaControllerCompat(this, token);
//        MediaControllerCompat.setMediaController(this, mediaController);
        mediaController.registerCallback(mMediaControllerCallback);

    }

    // Callback that ensures that we are showing the controls
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    //这里根据播放状态的改变，本地ui做相应的改变，例如播放模式，播放、暂停，进度条等
                    Log.e("RunTestT", "--------播放状态改变-----???");
                }

                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {

                    Log.e("RunTest1", "---播放数据回调----------???" + metadata);
                    for (String string : metadata.keySet()) {
                        Log.e("RunTest3", "-------onMetadataChanged---001---???" + string);
                    }
                }
            };

    /**
     * 向媒体浏览器服务(MediaBrowserService)发起数据订阅请求的回调接口
     */
    private final MediaBrowserCompat.SubscriptionCallback BrowserSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback(){
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    Log.e("RunTestT","onChildrenLoaded----回调数据--");
                    //children 即为Service发送回来的媒体数据集合
                    //该数据由MediaBrowserServiceCompat提供,有的方法onLoadChildren(@NonNull final String parentMediaId,
                    //                                @NonNull final Result<List<MediaItem>> result)返回结果
                    for (MediaBrowserCompat.MediaItem item:children){
                        Log.e("RunTestT",item.getDescription().getTitle().toString());
                    }
                }
            };
}
