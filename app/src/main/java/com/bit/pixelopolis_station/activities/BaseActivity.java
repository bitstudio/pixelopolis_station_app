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

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bit.pixelopolis_station.services.StationInformation;
import com.bit.pixelopolis_station.R;
import com.bit.pixelopolis_station.data.BatteryInformation;
import com.bit.pixelopolis_station.enums.AppStatus;
import com.bit.pixelopolis_station.enums.ErrorStatus;
import com.bit.pixelopolis_station.enums.WarningStatus;
import com.bit.pixelopolis_station.services.BaseListener;
import com.bit.pixelopolis_station.services.StationController;
import com.bit.pixelopolis_station.services.config.Config;

// Here is the base activity for every activities, which provides fundamental stuffs that needed in every activities

public abstract class BaseActivity extends AppCompatActivity implements BaseListener {
    protected LinearLayout fullLayout;
    protected FrameLayout actContent;
    protected TextView stateTextView;
    protected ImageView warningImageView;
    protected ImageView errorImageView;
    protected TextView stationIdDebugTextView;
    protected View spinnerLayout;

    StationController stationController;
    BatteryInformation batteryInformation;
    View root;
    static int indexNode =0;
    private static int ERROR_CANNOT_COMMUNICATE_WITH_SERVER_IMAGE = R.drawable.error_301;
    private static int WARNING_DID_NOT_RECEIVE_VIDEO_STREAM_IMAGE = R.drawable.error_303;
    private static int WARNING_LOW_BATTERY_IMAGE = R.drawable.error_101;
    private static int WARNING_VERY_LOW_BATTERY_IMAGE = R.drawable.error_102;

    private static int DISCONNECT_BUTTON_CLICK_NUMBER = 5;
    int disconnectButtonClickCounter = 0;

    private static int HOLD_TIME_SECOND = 5;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        batteryInformation = new BatteryInformation(this);
        stationController = new StationController();

        super.onCreate(savedInstanceState);
        stationController.setBaseListener(this);
    }

    @Override
    public void setContentView(final int layoutResID) {
        // hide status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // base layout
        fullLayout= (LinearLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        fullLayout.setOrientation(LinearLayout.VERTICAL);
        actContent= (FrameLayout) fullLayout.findViewById(R.id.act_content);
        root = actContent.getRootView();

        // set content of layout to the act_content frame
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);

        warningImageView = findViewById(R.id.warning_image);
        errorImageView = findViewById(R.id.error_image);
        stateTextView = findViewById(R.id.state_textbox);
        stationIdDebugTextView = findViewById(R.id.station_id_debug_textbox);

        if(!Config.getInstance().isInDebugMode()) {
            stateTextView.setVisibility(View.GONE);
            stationIdDebugTextView.setVisibility(View.GONE);
        }
        spinnerLayout = fullLayout.findViewById(R.id.spinner_layout);
        showWarning(WarningStatus.NONE);
        showError(ErrorStatus.NONE);
        setStationIdTextbox(StationInformation.getInstance().getStationId());
        disconnectButtonClickCounter = 0;
    }

    protected void setStateTextView(String text) {
        if(stateTextView != null) {
            runOnUiThread(() -> stateTextView.setText(text));
        }
    }

    @Override
    public void setStationIdTextbox(String id) {
        stationIdDebugTextView.setText("ID " + id);
    }

    @Override
    public void showWarning(WarningStatus warningStatus){
        switch (warningStatus) {
            case NONE:
                runOnUiThread(() -> warningImageView.setVisibility(View.INVISIBLE));
                break;
            case CAR_PHONE_LOW_BATTERY:
                runOnUiThread(() -> warningImageView.setImageResource(WARNING_LOW_BATTERY_IMAGE));
                runOnUiThread(() -> warningImageView.setVisibility(View.VISIBLE));
                break;
            case CAR_PHONE_VERY_LOW_BATTERY:
                runOnUiThread(() -> warningImageView.setImageResource(WARNING_VERY_LOW_BATTERY_IMAGE));
                runOnUiThread(() -> warningImageView.setVisibility(View.VISIBLE));
                break;
            case DID_NOT_RECEIVE_VIDEO_STREAM:
                runOnUiThread(() -> warningImageView.setImageResource(WARNING_DID_NOT_RECEIVE_VIDEO_STREAM_IMAGE));
                runOnUiThread(() -> warningImageView.setVisibility(View.VISIBLE));
                break;
        }
    }

    @Override
    public void showError(ErrorStatus errorStatus){
        switch (errorStatus) {
            case NONE:
                runOnUiThread(() -> errorImageView.setVisibility(View.INVISIBLE));
                break;

            case CANNOT_COMMUNICATE_WITH_SERVER:
                runOnUiThread(() -> errorImageView.setImageResource(ERROR_CANNOT_COMMUNICATE_WITH_SERVER_IMAGE));
                runOnUiThread(() -> errorImageView.setVisibility(View.VISIBLE));
                break;
        }
    }

    @Override
    public void showSpinner() {
        spinnerLayout.setVisibility(View.VISIBLE);
        new CountDownTimer(Config.SPINNER_TIME_OUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                spinnerLayout.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    public void hideSpinner() {
        spinnerLayout.setVisibility(View.GONE);
    }

    @Override
    public void updateStateView(String string){
        setStateTextView(string);
    }

    @Override
    protected void onResume() {
        super.onResume();
        batteryInformation.resume();
        stationController.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        batteryInformation.pause();
        stationController.pause();
        cancelTimer();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        StationInformation.getInstance().setStatus(stationController.getAppStatus());
        stationController.destroy();
    }

    @Override
    public int getBatteryPercentage(){
        return batteryInformation.getBatteryPercentage();
    }

    public void setIndexNode(int i) {indexNode = i;}

    public void onClickWarningMessage(View view) {
        warningImageView.setVisibility(View.INVISIBLE);
    }

    public void onClickErrorMessage(View view) {
        errorImageView.setVisibility(View.INVISIBLE);
    }

    public void onClickDisconnectButton(View view) {
        disconnectButtonClickCounter++;

        if(disconnectButtonClickCounter >= DISCONNECT_BUTTON_CLICK_NUMBER) {
            disconnect();
        }
    }

    void disconnect(){
        AppStatus status = stationController.getAppStatus();
        if(status != AppStatus.WAIT_TO_CONNECT && status != AppStatus.CONNECT) {
            stationController.disconnect();
            goToSetupActivity();
        }
    }

    @Override
    public void playAnimation() {

    }

    @Override
    public void goToSetupActivity(){
        Intent intent = new Intent(this, SetupConfigActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void showDialog() {
        CharSequence[] choice = { "Disconnect" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Menu")
                .setCancelable(false)
                .setItems(choice, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(choice[which].equals("Disconnect")) {
                            disconnect();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        try {
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            if(Config.getInstance().isInDebugMode())
                Toast.makeText(this, "Dialog error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int maskedAction = event.getActionMasked();

        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
                startTimer();

                break;
            case MotionEvent.ACTION_UP:
                cancelTimer();
                break;
        }

        return true;
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(HOLD_TIME_SECOND * 1000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                showDialog();
            }
        };
        countDownTimer.start();
    }

    public void cancelTimer(){
        if(countDownTimer != null)
            countDownTimer.cancel();
    }
}
