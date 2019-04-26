package com.example.recordingapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    //Variables that need to be used
    private Button RecordButton;
    private Button StopButton;
    private Button SaveButton;
    private Button PlayButton;

    private EditText fileName;
    private String nameOfFile;

    private String savePath = "";
    private MediaPlayer mediaPlay;
    private MediaRecorder mediaRecorder;

    private static final String LOG_TAG = "AudioTest";

    //will be using this to request Run-time permission
    private final int REQUEST_PERMISSION_CODE = 1000;
    //private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordButton = (Button)findViewById(R.id.record_button);
        StopButton = (Button)findViewById(R.id.stop_button);
        SaveButton = (Button)findViewById(R.id.save_recording);
        PlayButton = (Button)findViewById(R.id.play_recording);

        fileName = (EditText)findViewById(R.id.file_name_text);
        nameOfFile = fileName.getText().toString();

        if(checkPermissionFromDevice()){

           RecordButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   boolean mStart = true;
                   //savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
                          // + UUID.randomUUID().toString()+ nameOfFile + ".mp3";
                   onRecord(mStart);
                   if(mStart == true){
                      //Toast.makeText(this, "Start recording", Toast.LENGTH_SHORT).show();
                   } else {
                       //Toast.makeText(this, "Sopped recording", Toast.LENGTH_SHORT).show();
                   }
                   mStart = !mStart;

                   PlayButton.setEnabled(false);
                   //StopButton.setEnabled(false);
               }
           });
           StopButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   mediaRecorder.stop();
                   PlayButton.setEnabled(true);
                   RecordButton.setEnabled(true);
                   StopButton.setEnabled(false);
               }
           });
           SaveButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   savePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"
                    + UUID.randomUUID().toString()+ nameOfFile + ".mp3";
               }
           });
           PlayButton.setOnClickListener(new View.OnClickListener() {
               boolean mstart = true;
               @Override
               public void onClick(View v) {
                   onPlay(mstart);
                   mstart = !mstart;
               }
           });
        }
        else
        {
            requestPermission();
        }
    }
    private void onRecord(boolean start){
        if(start){
            startRecording();
        } else{
            stopRecording();
        }
    }

    private void onPlay(boolean start){
        if(start){
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying(){
        mediaPlay = new MediaPlayer();
        try{
            mediaPlay.setDataSource(nameOfFile);
            mediaPlay.prepare();
            mediaPlay.start();
        }catch (IOException e){
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying(){
        mediaPlay.release();
        mediaPlay = null;
    }

    private void startRecording(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(nameOfFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            mediaRecorder.prepare();
        } catch (IOException e){
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopRecording(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }
    //get onRequestPermissionResult from methods(shortcut  Ctrl+o)
    //Checks for request permission from the android device then toast message to confirm or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int record_audio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio == PackageManager.PERMISSION_GRANTED;
    }
}
