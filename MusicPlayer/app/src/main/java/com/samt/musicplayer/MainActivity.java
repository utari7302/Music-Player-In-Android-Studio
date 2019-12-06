package com.samt.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

import hotchemi.android.rate.AppRate;

public class MainActivity extends AppCompatActivity {
    String[] items;
    ListView myListViewforSongs;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //This is used to give access of list view in java code.
        myListViewforSongs=(ListView) findViewById(R.id.mySongListView);
        AppRate.with(this)
                .setInstallDays(1)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
        runtimepermission();
        MobileAds.initialize(this,
                "ca-app-pub-6801364031849966~5219527776");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    //THIS FUNCTION IS USED FOR PERMISSION FROM USER.
    public void runtimepermission()
    {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    //THIS CODE IS USED TO FIND SONGS FROM EXTERNAL STORAGE USING ARRAY LIST AND FILES
    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList=new ArrayList<>();
        File[] files=file.listFiles();
        for(File singleFile:files){
            if(singleFile.isDirectory() && !singleFile.isHidden())
            {
                arrayList.addAll(findSong(singleFile));
            }
            else
            {
                if(singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav") ){
                    arrayList.add(singleFile);
                }
            }
        }
        return arrayList;
    }
    public void display(){
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        items=new String[mySongs.size()];
        for(int i=0;i<mySongs.size();i++){
            items[i]=mySongs.get(i).getName().toString().replace(".mp3","").replace(".wav", "");
        }
        ArrayAdapter<String> myAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        myListViewforSongs.setAdapter(myAdapter);

       myListViewforSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songname= myListViewforSongs.getItemAtPosition(i).toString();

                startActivity(new Intent(getApplicationContext(),PLAYER.class)
                .putExtra("songs",mySongs).putExtra("songname",songname)
                .putExtra("pos",i));
            }
        }
        );

    }

}
