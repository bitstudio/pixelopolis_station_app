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
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bit.pixelopolis_station.R;
import com.bit.pixelopolis_station.enums.AppStatus;
import com.bit.pixelopolis_station.services.api.ApiCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StandbyActivity extends BaseActivity {
    private static String TAG = "StandbyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standby);
        stationController.setStationStatus(AppStatus.STANDBY);
        sendFinishedVideoToServer();
    }

    @Override
    public void goToNextActivity() {
        Intent intent =new Intent(StandbyActivity.this, SelectPlaceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stationController.setStationStatus(AppStatus.DISPLAY_PLACE_OPTIONS);
        finish();
    }

    public void onClickStartButton(View view) {
        selectStart();
    }

    public void selectStart() {
        ApiCommunicator.getInstance().selectStart(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    Log.e(TAG, response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());

                        if (returnObject.getBoolean("success")) {
                            goToNextActivity();
                        }

                    } catch (JSONException e) {

                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }

    protected void sendFinishedVideoToServer() {
       ApiCommunicator.getInstance().videoFinished(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    Log.d("API_RESPONSE", "video_finished" + response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
//                            startTimer();

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
