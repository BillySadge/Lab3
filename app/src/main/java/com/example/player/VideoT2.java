package com.example.player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class VideoT2 extends AppCompatActivity implements VideoRVAdapter.VideoClickInterface {

    private RecyclerView videoRV;
    private ArrayList<VideoRVModal> videoRVModalArrayList;
    private VideoRVAdapter videoRVAdapter;
    private static final int STORAGE_PERMISSION = 101;
    SwipeListener swipeListener;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_t2);
        relativeLayout = findViewById(R.id.relative_layout1);
        swipeListener = new SwipeListener(relativeLayout);

        videoRV = findViewById(R.id.idRVVideos);
        videoRVModalArrayList = new ArrayList<>();
        videoRVAdapter = new VideoRVAdapter(videoRVModalArrayList, this, this::onVideoClick);
        videoRV.setLayoutManager(new GridLayoutManager(this, 2));
        videoRV.setAdapter(videoRVAdapter);
        if(ContextCompat.checkSelfPermission(VideoT2.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(VideoT2.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION);
        }else{
            getVideos();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==STORAGE_PERMISSION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                getVideos();
            }else{
                Toast.makeText(this, "The app will not work without permission..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getVideos(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor!=null && cursor.moveToFirst()){
            do{
                @SuppressLint("Range") String videoTitle = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range") String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range") Bitmap videoThumbnail = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);
                videoRVModalArrayList.add(new VideoRVModal(videoTitle, videoPath,videoThumbnail));



            }while(cursor.moveToNext());

        }
        videoRVAdapter.notifyDataSetChanged();


    }

    @Override
    public void onVideoClick(int position) {
        Intent i = new Intent(VideoT2.this, VideoPlayerActivity.class);
        i.putExtra("videoName", videoRVModalArrayList.get(position).getVideoName());
        i.putExtra("videoPath", videoRVModalArrayList.get(position).getVideoPath());
        startActivity(i);
    }
    private class SwipeListener implements View.OnTouchListener{
        GestureDetector gestureDetector;
        SwipeListener(View view){
            int threshold = 100;
            int velocity_threshold = 100;

            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    float xDiff = e2.getX() - e1.getX();
                    float yDiff = e2.getY() - e1.getY();
                    try{
                        if(Math.abs(xDiff) > Math.abs(yDiff)){
                            if(Math.abs(xDiff) > threshold && Math.abs(velocityX) > velocity_threshold){
                                if(xDiff > 0){
                                    Intent i = new Intent(VideoT2.this, MainActivity.class);
                                    startActivity(i);
                                }else{
                                    Intent i = new Intent(VideoT2.this, MainActivity.class);
                                    startActivity(i);
                                }
                                return true;
                            }
                        }else{
                            if(Math.abs(yDiff) > threshold && Math.abs(velocityY) > velocity_threshold){
                                if(yDiff > 0){

                                }else{

                                }
                                return true;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            };
            gestureDetector = new GestureDetector(listener);
            view.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }
}