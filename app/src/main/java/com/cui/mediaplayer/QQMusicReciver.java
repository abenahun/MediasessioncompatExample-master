package com.cui.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import java.util.Set;

public class QQMusicReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            for (String s : bundle.keySet()) {
            }
        }


//        if ("com.tencent.qqmusictv.ACTION_META_CHANGEDQQMusicTV".equals(intent.getAction())) {
//            Bundle bundle=intent.getExtras();
//            if (bundle!=null){
//                songInfo(intent);
//                for (String s:bundle.keySet()) {
//                }
//            }
//
//        }
// if ("com.android.music.metachanged".equals(intent.getAction())) {
//            songInfo(intent);
//        }

//        if (("com.tencent.qqmusic.ACTION_META_CHANGED.QQMusicPhone").equals(intent.getAction())) {
//            songInfo(intent);
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
////                Set<String> set = bundle.keySet();
////                for (String s : set) {
////                }
//            }
//        }
    }

    public void songInfo(Intent intent) {
        //作者
        String artist = intent.getStringExtra("artist");
        //歌曲名
        String track = intent.getStringExtra("track");
        //专辑名
        String albumName = intent.getStringExtra("album");
        Log.e("RunTest", "-----数据改变---artist\n作者-----" + artist
                + "\n专辑名----" + albumName
                + "\n歌曲名----" + track
        );

    }
}
