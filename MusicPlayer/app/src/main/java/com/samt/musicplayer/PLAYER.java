package com.samt.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PLAYER extends AppCompatActivity {

    Button btn_next, btn_previous, btn_pause, btn_heart, btn_repeat;
    TextView tv, tvend;
    SeekBar sb;
    static MediaPlayer mymediaplayer;
    int position;
    ArrayList<File> mySongs;
    String sname;
    Thread updateseekbar;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btn_next = (Button) findViewById(R.id.next);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_heart = (Button) findViewById(R.id.heart);
        btn_repeat = (Button) findViewById(R.id.repeat);
        tv = (TextView) findViewById(R.id.songname);
        tvend = (TextView) findViewById(R.id.endtime);
        sb = (SeekBar) findViewById(R.id.seekbar);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        updateseekbar = new Thread() {
            @Override
            public void run() {
                int totalduration = mymediaplayer.getDuration();
                int currentposition = 0;
                while (currentposition < totalduration) {
                    try {
                        sleep(0);
                        currentposition = mymediaplayer.getCurrentPosition();
                        sb.setProgress(currentposition);

                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            }
        };

        if (mymediaplayer != null) {
            mymediaplayer.stop();
            mymediaplayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        sname = mySongs.get(position).getName().toString();
        String songname = i.getStringExtra("songname");
        tv.setText(songname);
        tv.setSelected(true);

        position = bundle.getInt("pos", 0);
        Uri u = Uri.parse(mySongs.get(position).toString());
        mymediaplayer = MediaPlayer.create(getApplicationContext(), u);
        String totTime = createTimeLable(mymediaplayer.getDuration());
        tvend.setText(totTime);
        mymediaplayer.start();
        sb.setMax(mymediaplayer.getDuration());

        updateseekbar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mymediaplayer.seekTo(seekBar.getProgress());

            }
        });
        mymediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mediaPlayer) {
                mymediaplayer.stop();
                mymediaplayer.release();
                position = ((position + 1) % mySongs.size());
                Uri u = Uri.parse(mySongs.get(position).toString());
                mymediaplayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName().toString();
                tv.setText(sname);
                String totTime = createTimeLable(mymediaplayer.getDuration());
                tvend.setText(totTime);
                sb.setProgress(mymediaplayer.getCurrentPosition());
                updateseekbar.start();
                mymediaplayer.start();
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sb.setMax(mymediaplayer.getDuration());
                if (mymediaplayer.isPlaying()) {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mymediaplayer.pause();
                } else {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mymediaplayer.start();
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mymediaplayer.stop();
                mymediaplayer.release();
                position = ((position + 1) % mySongs.size());

                Uri u = Uri.parse(mySongs.get(position).toString());

                mymediaplayer = MediaPlayer.create(getApplicationContext(), u);
                sname = mySongs.get(position).getName().toString();
                tv.setText(sname);
                mymediaplayer.start();

            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mymediaplayer.stop();
                mymediaplayer.release();

                position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);

                Uri u = Uri.parse(mySongs.get(position).toString());
                mymediaplayer = MediaPlayer.create(getApplicationContext(), u);

                sname = mySongs.get(position).getName().toString();
                tv.setText(sname);
                mymediaplayer.start();

            }
        });
        btn_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mymediaplayer.isPlaying()) {
                    btn_heart.setBackgroundResource(R.drawable.ic_sentiment_very_satisfied_black_24dp);
                }
            }
        });
        btn_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.repeat:
                        btn_repeat.setBackgroundResource(R.drawable.ic_repeat_one_black_24dp);
                        mymediaplayer.start();
                        mymediaplayer.setLooping(true);
                        sb.setProgress(mymediaplayer.getCurrentPosition());
                        updateseekbar.start();
                        break;
                }
            }
        });

    }

    public String createTimeLable(int duration) {
        String timeLable = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLable += min + ":";
        if (sec < 10) timeLable += "0";

        timeLable += sec;

        return timeLable;
    }


}
