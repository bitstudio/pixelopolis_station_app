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

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bit.pixelopolis_station.services.StationInformation;
import com.bit.pixelopolis_station.R;
import com.bit.pixelopolis_station.enums.AppStatus;
import com.bit.pixelopolis_station.services.config.Config;
import com.bit.pixelopolis_station.utils.Utils;

// Load and save configs (id, server url, ..) , request permission, then connect to server

public class SetupConfigActivity extends BaseActivity {
    private static final int REQUEST_WRITE_PERMISSION = 786;
    EditText stationIdView;
    EditText serverUrlView;
    CheckBox debugModeCheckBox;
    TextView versionTextView;

    String deviceId;
    String ipAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_config);
        stationIdView = findViewById(R.id.station_id_textbox);
        serverUrlView = findViewById(R.id.server_url_textbox);
        debugModeCheckBox = findViewById(R.id.debug_mode_checkbox);
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        stationController.setStationStatus(AppStatus.WAIT_TO_CONNECT);
        ipAddress = Utils.getIPAddress(true);
        versionTextView = findViewById(R.id.version_text_view);
        versionTextView.setText("Station Application " + Config.APP_VERSION);

        // load config
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("DATA",Context.MODE_PRIVATE);
        String savedStationId = sharedPreferences.getString("STATION_ID",null);
        String savedServerUrl = sharedPreferences.getString("SERVER_URL",null);
        boolean savedIsInDebugMode = sharedPreferences.getBoolean("IS_IN_DEBUG_MODE", false);

        if(savedStationId != null) {
            stationIdView.setText(savedStationId);
        }

        if(savedServerUrl != null)
            serverUrlView.setText(savedServerUrl);

        debugModeCheckBox.setChecked(savedIsInDebugMode);

        requestPermission();
    }

    @Override
    public void goToNextActivity() {
        Intent intent;
        if(Config.APP_VERSION.equals(Config.getInstance().getLatestAppVersion())){
            intent = new Intent(SetupConfigActivity.this, StandbyActivity.class);
        }
        else{
            intent = new Intent(SetupConfigActivity.this, UpdateAppActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    protected void saveConfig(String stationId, String serverUrl, boolean isInDebugMode)
    {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("STATION_ID",stationId).apply();
        sharedPreferences.edit().putString("SERVER_URL",serverUrl).apply();
        sharedPreferences.edit().putBoolean("IS_IN_DEBUG_MODE", isInDebugMode).apply();
    }

    public void onClickConnectToServer(View view) {
        String stationId = stationIdView.getText().toString();
        String serverUrl = serverUrlView.getText().toString();

        // save config
        boolean isInDebugMode = debugModeCheckBox.isChecked();
        Config.getInstance().setInDebugMode(isInDebugMode);
        saveConfig(stationId, serverUrl, isInDebugMode);
        StationInformation.getInstance().setStationId(stationId);
        StationInformation.getInstance().setDeviceId(deviceId);
        stationController.getConfigFromServer(stationId, serverUrl, deviceId, ipAddress, "0.0");
    }
}