package com.prodev.quickconnect;

import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.VideoCanvas;

public class VideoCallingActivity extends AppCompatActivity {


    private RtcEngine rtcEngine;  // Agora RTC engine
    private Button endCallButton;
    private Button muteButton;
    private boolean isMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_calling);

        // set up the insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize Agora and the UI
        initializeAgoraEngine();

        // Link buttons to the UI
        endCallButton = findViewById(R.id.btn_end_call);
        muteButton = findViewById(R.id.btn_mute);

        // Set up button listeners
        endCallButton.setOnClickListener(v -> endCall());
        muteButton.setOnClickListener(v -> toggleMute());


    }

    private void initializeAgoraEngine() {

        try {
            rtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), new IRtcEngineEventHandler() {
                @Override
                public void onUserJoined(int uid, int elapsed) {
                    runOnUiThread(() -> setupRemoteVideo(uid));
                }

                @Override
                public void onUserOffline(int uid, int reason) {
                    runOnUiThread(() -> removeRemoteVideo());
                }
            });
            rtcEngine.enableVideo();
            rtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_COMMUNICATION);  // One-to-one call
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Setup remote video view
    private void setupRemoteVideo(int uid) {
        SurfaceView remoteView = RtcEngine.CreateRendererView(getBaseContext());
        ((FrameLayout) findViewById(R.id.remote_video_view_container)).addView(remoteView);
        rtcEngine.setupRemoteVideo(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }

    // Remove remote video
    private void removeRemoteVideo() {
        ((FrameLayout) findViewById(R.id.remote_video_view_container)).removeAllViews();
    }



    // End the call
    private void endCall() {
        rtcEngine.leaveChannel();
        finish();  // End the activity
    }

    // Mute or unmute the microphone
    private void toggleMute() {
        isMuted = !isMuted;
        rtcEngine.muteLocalAudioStream(isMuted);
        muteButton.setText(isMuted ? "Unmute" : "Mute");
    }
}