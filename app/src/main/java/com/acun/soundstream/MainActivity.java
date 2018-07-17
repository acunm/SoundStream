package com.acun.soundstream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.rtsp.RtspServer;

public class MainActivity extends AppCompatActivity implements Session.Callback{

    private Session session;
    private Button start, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(RtspServer.KEY_PORT, String.valueOf(1234));
        editor.apply();

        session = SessionBuilder.getInstance()
                .setCallback(this)
                .setVideoEncoder(SessionBuilder.VIDEO_NONE)
                .setContext(getApplicationContext())
                .setAudioEncoder(SessionBuilder.AUDIO_AAC)
                .setAudioQuality(new AudioQuality(16000, 32000))
                .setDestination("192.168.3.10")
                .build();

        startService(new Intent(this, RtspServer.class));

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!session.isStreaming())
                    session.configure();

            }
        });



        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(session.isStreaming())
                    session.stop();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        session.release();
    }

    @Override
    public void onBitrateUpdate(long bitrate) {
        Log.d("MyLog", "onBitrateUpdate = " + bitrate);
        Log.d("MyLog", "onBitrateUpdate = " + session.getDestination() + " " + session.getSessionDescription());
    }

    @Override
    public void onSessionError(int reason, int streamType, Exception e) {
        Log.d("MyLog", "onSessionError = " + reason + " " + streamType + " " + e.getMessage());
    }

    @Override
    public void onPreviewStarted() {
        Log.d("MyLog", "onPreviewStarted");
    }

    @Override
    public void onSessionConfigured() {
        Log.d("MyLog", "onSessionConfigured");

        session.start();
    }

    @Override
    public void onSessionStarted() {
        Log.d("MyLog", "onSessionStarted");
    }

    @Override
    public void onSessionStopped() {
        Log.d("MyLog", "onSessionStopped");
    }
}
