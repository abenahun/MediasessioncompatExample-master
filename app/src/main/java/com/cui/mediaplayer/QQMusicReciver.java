package com.cui.mediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Set;

public class QQMusicReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RunTest", "-----接收到QQ音乐广播--------???" + intent.getAction());
//        if ("com.android.music.metachanged".equals(intent.getAction())) {
//            songInfo(intent);
//        }
//
//        if (("com.tencent.qqmusic.ACTION_META_CHANGED.QQMusicPhone").equals(intent.getAction())) {
//            Log.e("RunTest","-数据改变----QQ音乐广播---开始-----???");
//            songInfo(intent);
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                Log.e("RunTest", "-----数据改变---bundle-----???" + bundle.toString());
//                Log.e("RunTest", "-----数据改变-intent-------???" + intent.toString());
////                Set<String> set = bundle.keySet();
////                for (String s : set) {
////                    Log.e("RunTest", "-----数据改变--------???" + s);
////                    Log.e("RunTest", "-----数据改变--------???" + bundle.get(s));
////                }
//            }
//            Log.e("RunTest","-数据改变----QQ音乐广播------结束--???");
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
