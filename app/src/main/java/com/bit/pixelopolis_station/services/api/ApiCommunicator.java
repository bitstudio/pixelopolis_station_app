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

package com.bit.pixelopolis_station.services.api;

import android.util.Log;

import com.bit.pixelopolis_station.enums.ErrorStatus;
import com.bit.pixelopolis_station.enums.WarningStatus;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiCommunicator {

    private static final ApiCommunicator ourInstance = new ApiCommunicator();

    public static ApiCommunicator getInstance() {
        return ourInstance;
    }

    Retrofit retrofit;

    String deviceId;
    String stationId;
    int stationNodeId;
    String ipAddress;

    private final String appType = "station";

    private ApiCommunicator()
    {

    }

    public void initialRetrofit(String serverUrl, String deviceId, String stationId, int stationNodeId, String ipAddress)
    {
        this.deviceId = deviceId;
        this.stationId = stationId;
        this.stationNodeId = stationNodeId;
        this.ipAddress = ipAddress;

        retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getConfig(Callback<String> callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        Call<String> call = service.getConfig();
        call.enqueue(callback);
    }

    public void connectStation(final Callback<String> callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);
            paramObject.put("station_node_id", stationNodeId);
            paramObject.put("ip_address", ipAddress);
            Call<String> call = service.connectStation(paramObject.toString());

            Log.d("API_CALL", "connectStation : " + paramObject.toString());
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void waitForCarToConnect(Callback callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.waitForCarToConnect(paramObject.toString());

            Log.d("API_CALL", "waitForCarToConnect : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectStart(Callback callback) {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.selectStart(paramObject.toString());

            Log.e("API_CALL", "selectStart : " + paramObject.toString());
            call.enqueue(callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void alive(String appStatus, int batteryPercentage, WarningStatus warning, ErrorStatus error, final Callback<String> callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);
        JSONObject deviceInfoObject = new JSONObject();
        JSONObject paramObject = new JSONObject();
        try {
            deviceInfoObject.put("device_id", deviceId);
            deviceInfoObject.put("app_type", appType);
            deviceInfoObject.put("battery_percentage", batteryPercentage);

            paramObject.put("device_info", deviceInfoObject);
            paramObject.put("station_id", stationId);
            paramObject.put("app_status", appStatus);
            paramObject.put("warning", warning.toString());
            paramObject.put("error", error.toString());

            Call<String> call = service.alive(paramObject.toString());

            Log.e("API_CALL", "alive : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void selectPlace(int placeNodeId, int placePathId, Callback callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);
            paramObject.put("place_node_id", placeNodeId);
            paramObject.put("place_path_id", placePathId);

            Call<String> call = service.selectPlace(paramObject.toString());

            Log.e("API_CALL", "selectPlace : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cancelPlace(Callback<String> callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.cancelPlace(paramObject.toString());

            Log.e("API_CALL", "cancelPlace : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void waitForCarToArriveAtDestination(String carId, int destinationNodeId, Callback callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);
            paramObject.put("car_id", carId);
            paramObject.put("destination_node_id", destinationNodeId);

            Call<String> call = service.waitForCarToArriveAtDestination(paramObject.toString());

            Log.d("API_CALL", "waitForCarToArriveAtDestination : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void waitForCarDisconnect(Callback callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.waitForCarDisconnect(paramObject.toString());

            Log.d("API_CALL", "waitForCarDisconnect : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disconnectStation(Callback callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.disconnectStation(paramObject.toString());

            Log.d("API_CALL", "disconnectStation : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void videoStarted(Callback callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.videoStarted(paramObject.toString());

            Log.d("API_CALL", "videoStarted : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void videoFinished(Callback<String> callback)
    {
        CallWebService service = retrofit.create(CallWebService.class);

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("app_type", appType);
            paramObject.put("device_id", deviceId);
            paramObject.put("station_id", stationId);

            Call<String> call = service.videoFinished(paramObject.toString());

            Log.d("API_CALL", "videoFinished : " + paramObject.toString());
            call.enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
