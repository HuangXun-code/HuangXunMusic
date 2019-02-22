package edu.whut.huangxun.HuangXunMusic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ListActivity extends AppCompatActivity {

    //定义一个歌曲数组，作为ListView的数据源
    private List<Song> songArray = new ArrayList<Song>();

    //为动态注册实例化一个自定义的广播接收器和一个IntentFilter
    private IntentFilter intentFilter;;
    private HeadsetPlugReceiver headsetPlugReceiver;;
    SongAdapter sa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Button bt = (Button) findViewById(R.id.btn_back);
        TextView tv = (TextView) findViewById(R.id.txt_welcome);
        String uname=this.getIntent().getStringExtra("uname");
        tv.setText("欢迎您，"+uname+"!");

        //动态注册广播接收器
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        headsetPlugReceiver = new HeadsetPlugReceiver();
        registerReceiver(headsetPlugReceiver, intentFilter);


        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //监听器要做的事情
                Intent i=new Intent(ListActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        //调用initSong()之前需先动态获取权限
        //initSongs();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
        } else {
            initSongs();
        }

        sa = new SongAdapter(ListActivity.this, R.layout.song_item, songArray);
        ListView lv = findViewById(R.id.list_song);
        lv.setAdapter(sa);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Song song = songArray.get(position);
                //String path = song.getsongPath();

                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("songList", (Serializable)songArray);
                //intent.putExtras(bundle);
                intent.putExtra("songList", (Serializable)songArray);
                intent.putExtra("index", position);
                startActivity(intent);
            }
        });
    }

    //在onDestroy()方法中通过调用unregisterReceiver()方法来取消广播接收器的注册
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(headsetPlugReceiver);
    }

    //运行时权限申请的处理
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initSongs();
                } else {
                    Toast.makeText(this, "未授权，功能无法实现", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

   /*
    private void initSongs(){
        Song ChengDu = new Song("成都", R.drawable.chengdu, "赵雷");
        songArray.add(ChengDu);
        Song LiLiAn = new Song("莉莉安", R.drawable.lilian, "宋东野");
        songArray.add(LiLiAn);
        Song NanShanNan = new Song("南山南", R.drawable.nanshannan, "马頔");
        songArray.add(NanShanNan);
        Song QiMiaoNengLiGe = new Song("奇妙能力歌", R.drawable.qimiaonenglige, "陈粒");
        songArray.add(QiMiaoNengLiGe);
    }
    */

    private void initSongs() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // 获取专辑图片
                    //cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    // 获取歌手信息
                    String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    //获取歌曲名称
                    String disName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    //获取文件路径
                    String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

                    Song song = new Song(disName, R.drawable.default_cover, artist, url);
                    songArray.add(song);
                }
            }
            sa.notifyDataSetChanged(); //页面更新
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor != null)
            {
                cursor.close();
            }
        }
    }

    //创建一个广播接收器
    class HeadsetPlugReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase("android.intent.action.HEADSET_PLUG")){
                if(intent.hasExtra("state")){
                    if (intent.getIntExtra("state", 0) == 1){
                        Toast.makeText(context, "耳机已连接", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context, "耳机已断开", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}

