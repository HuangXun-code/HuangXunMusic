package edu.whut.huangxun.HuangXunMusic;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Song> songList;

    public static final int UPDATE_TEXT = 1;

    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");

    private SeekBar seekBar;

    private ImageView imageView;

    private Button  btnPlayOrPause, btnStop, btnQuit;

    private TextView musicTime, musicTotal, musicStatus;

    private String path;

    private int index;
    //private MediaPlayer mediaPlayer = new MediaPlayer();

    public Handler handler;

    private ObjectAnimator objectAnimator;//图片的动画效果

    private MusicService musicService;

    private ServiceConnection connection;

    private void bindServiceConnection() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicService = ((MusicService.MyBinder) (service)).getService();
                //musicTotal.setText(time.format(musicService.mediaPlayer.getDuration()));
                //musicTotal.setText(time.format(musicService.mediaPlayer.getDuration()));
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                connection = null;
            }
        };
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("songList", (Serializable)songList);
        intent.putExtra("index", index);
        startService(intent); //这里打成了startActivity()
        bindService(intent, connection, this.BIND_AUTO_CREATE);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        songList = (List<Song>) intent.getSerializableExtra("songList");
        index = intent.getIntExtra("index", 0);

        findViewid();
        bindServiceConnection();
        initMediaPlayer();

        objectAnimator= ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        objectAnimator.setDuration(10000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { //拖动进度条播放相应时间的音乐
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    musicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnPlayOrPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnQuit.setOnClickListener(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.obtainMessage(UPDATE_TEXT).sendToTarget();
                }
            }
        }).start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_TEXT:
                        musicTime.setText(time.format(musicService.mediaPlayer.getCurrentPosition()));
                        seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
                        seekBar.setMax(musicService.mediaPlayer.getDuration());
                        musicTotal.setText(time.format(musicService.mediaPlayer.getDuration()));
                         if (musicService.mediaPlayer.getCurrentPosition() == musicService.mediaPlayer.getDuration())
                             objectAnimator.end();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public void findViewid() {
        btnPlayOrPause = (Button) findViewById(R.id.BtnPlayorPause);
        btnStop = (Button) findViewById(R.id.BtnStop);
        btnQuit = (Button) findViewById(R.id.BtnQuit);
        musicTime = (TextView) findViewById(R.id.MusicTime);
        musicTotal = (TextView) findViewById(R.id.MusicTotal);
        imageView = (ImageView) findViewById(R.id.Image);
        musicStatus = (TextView) findViewById(R.id.MusicStatus);
        seekBar = (SeekBar) findViewById(R.id.MusicSeekBar);
    }


    public void initMediaPlayer() {
            try {
            //musicService.mediaPlayer.setDataSource(path);
            //musicService.mediaPlayer.prepare();
            btnPlayOrPause.setTag(0);//Tag:0未开始，1表示正在运行，2表示已被暂停
            musicStatus.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
                //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        }


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnPlayorPause:
                int tag = (Integer) btnPlayOrPause.getTag();
                if(tag == 0) { //此时表示第一次播放
                    musicStatus.setText("Playing");
                    //musicService.playOrPause();
                    objectAnimator.start();
                    btnPlayOrPause.setTag(1);
                    btnPlayOrPause.setText("PAUSED");
                } else if (tag == 1){ //此时表示需要暂停
                    musicStatus.setText("Paused");
                    //musicService.playOrPause();
                    objectAnimator.pause();
                    btnPlayOrPause.setText("PLAY");
                    btnPlayOrPause.setTag(2);
                } else { //此时表示第二次及多次播放
                    btnPlayOrPause.setTag(1);
                    btnPlayOrPause.setText("PAUSED");

                    objectAnimator.resume();
                    musicStatus.setText("Playing");
                }
                musicService.playOrPause();
                musicStatus.setVisibility(View.VISIBLE);
                break;
            case R.id.BtnStop:
                    musicService.stop();
                    initMediaPlayer();
                    btnPlayOrPause.setText("PLAY");
                    objectAnimator.end();
                    musicStatus.setVisibility(View.VISIBLE);
                    musicStatus.setText("Stopped");
                break;
            case R.id.BtnQuit:
                unbindService(connection);
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                stopService(intent);
                try {
                    MainActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(musicService.mediaPlayer != null) {
            musicService.mediaPlayer.stop();
           // mediaPlayer.release(); //导致无法回到上一个活动
        }
    }
}
