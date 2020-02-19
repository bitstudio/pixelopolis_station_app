/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bit.pixelopolis_station.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.bit.pixelopolis_station.enums.AppStatus;

import com.bit.pixelopolis_station.R;
import com.bit.pixelopolis_station.services.api.ApiCommunicator;
import com.bit.pixelopolis_station.services.config.Config;
import com.bit.pixelopolis_station.utils.Analytics;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromoVideoActivity extends BaseActivity {

    LottieAnimationView animationView;
    private VideoView videoView;
    private MediaController mediaController;
    private ImageView imageView;
    private ImageButton goBackButton;

    int animationIndex = 0;
    String[] animationPaths = {"portrait_bg.zip", "super_zoom_bg.zip", "night_sight_bg.zip", "google_len_bg.zip","dual_ev_bg.zip"};
    int[] promoVideo = {R.raw.video_portrait_mode,R.raw.video_super_res_zoom,R.raw.video_night_sight,R.raw.video_google_lens,R.raw.video_dual_ev};
    int[] imagePaths = {R.drawable.destination01_explain,R.drawable.destination02_explain,R.drawable.destination03_explain,R.drawable.destination04_explain,R.drawable.destination05_explain};
    boolean isTimerStarted = false;
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_video);

        if(Config.getInstance().isInDebugMode())
            Toast.makeText(this, stationController.getAppStatus().toString(), Toast.LENGTH_SHORT).show();

        videoView = (VideoView)findViewById(R.id.videoView);
        goBackButton = (ImageButton)findViewById(R.id.imageButton);

        String videoPath = "android.resource://" + getPackageName() + "/" +promoVideo[indexNode];
        if(Config.getInstance().isInDebugMode())
            Toast.makeText(this, videoPath, Toast.LENGTH_SHORT).show();
        Uri uri =Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        isTimerStarted = false;

        imageView = findViewById(R.id.explanation);
        imageView.setImageResource(imagePaths[indexNode]);

        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();

        mediaController.setVisibility(View.GONE);
        animationView = findViewById(R.id.promo_animation_view);
        animationView.setAnimation(animationPaths[indexNode]);
        animationView.setRepeatCount(LottieDrawable.INFINITE);
        animationView.playAnimation();

        goBackButton.setEnabled(false);

        videoView.setOnPreparedListener(mediaPlayer -> {
            try {
                Analytics.startedPromoVideo(this);
                videoView.start();
                if(Config.getInstance().isInDebugMode())
                    Toast.makeText(getApplicationContext(), "start video", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                if(Config.getInstance().isInDebugMode())
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            
        });

        videoView.setOnCompletionListener(mediaPlayer -> {
            if(Config.getInstance().isInDebugMode())
                Toast.makeText(getApplicationContext(), "finish video", Toast.LENGTH_SHORT).show();

            sendFinishedVideoToServer();
        });

        animationIndex = 0;
    }

    @Override
    protected void onResume() {
        videoView.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(Config.getInstance().isInDebugMode())
            Toast.makeText(getApplicationContext(), "on destroy activity", Toast.LENGTH_SHORT).show();
        videoView.stopPlayback();
        cancelTimer();
        super.onDestroy();
    }

    @Override
    public void goToNextActivity() {
        if(Config.getInstance().isInDebugMode())
            Toast.makeText(getApplicationContext(), "Go to next activity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(PromoVideoActivity.this, StandbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stationController.setStationStatus(AppStatus.STANDBY);
        cancelTimer();
        finish();
    }

    @Override
    public void goToSetupActivity(){
        cancelTimer();
        super.goToSetupActivity();
    }

    public void startTimer(){
        if(Config.getInstance().isInDebugMode())
            Toast.makeText(getApplicationContext(), "Start timer", Toast.LENGTH_SHORT).show();

        cTimer = new CountDownTimer(Config.getInstance().getHowToTryGraphicDelay() * 1000,1000) { //default config = 15 sec

            @Override
            public void onTick(long l) {
                if(Config.getInstance().isInDebugMode()) {
                    String text = "changing activity in : " + Long.toString(l / 1000);
                    updateStateView(text);
                }
            }

            @Override
            public void onFinish() {
                if(Config.getInstance().isInDebugMode())
                    Toast.makeText(getApplicationContext(), "Finish timer", Toast.LENGTH_SHORT).show();

                Analytics.newSession(getApplicationContext());
                goToNextActivity();
            }
        };
        cTimer.start();
    }

    public void cancelTimer(){
        if(cTimer != null)
            cTimer.cancel();
    }

    public void onClickTimerAnimation(View view) {
        goToNextActivity();
    }

    protected void sendFinishedVideoToServer() {
        Analytics.finishedPromoVideo(this);
        ApiCommunicator.getInstance().videoFinished(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    Log.d("API_RESPONSE", "video_finished" + response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
                            startTimer();
                            videoView.stopPlayback();
                            videoView.setVisibility(View.INVISIBLE);
                            goBackButton.setEnabled(true);

                        } else {
                            sendFinishedVideoToServer();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                sendFinishedVideoToServer();
            }
        });
    }

}
