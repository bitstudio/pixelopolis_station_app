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

package com.bit.pixelopolis_station.services;


import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.bit.pixelopolis_station.services.api.ApiCommunicator;
import com.bit.pixelopolis_station.services.config.Config;
import com.bit.pixelopolis_station.enums.AppStatus;
import com.bit.pixelopolis_station.enums.ErrorStatus;
import com.bit.pixelopolis_station.enums.WarningStatus;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.util.Log.d;
import static com.bit.pixelopolis_station.enums.AppStatus.DISPLAY_PLACE_OPTIONS;


public class StationController {

    Thread mThread;

    private static int UPDATE_INTERVAL = 1000;
    private static String TAG = "StationController";
    AppStatus appStatus;
    WarningStatus warningStatus = WarningStatus.NONE;
    ErrorStatus errorStatus = ErrorStatus.NONE;
    boolean didNotVideoStream = false;

    ReadWriteLock lock = new ReentrantReadWriteLock();
    boolean isFinished = false;
    boolean isPause = false;

    BaseListener baseListener;


    public  StationController()
    {
        setAppStatus(StationInformation.getInstance().getStatus());
        mThread = new Thread(new StationController.StationControllerThread());
        mThread.start();
    }

    public class StationControllerThread implements Runnable {

        public void run() {

            while (!isFinished) {
                if(!isPause) {
                    try {
                        AppStatus s = getAppStatus();
                        if(s != null && (s != AppStatus.WAIT_TO_CONNECT && s != AppStatus.CONNECT && s != AppStatus.WAIT_FOR_CAR_TO_CONNECT))
                            waitForCarDisconnect();
                        update();
                        if(baseListener != null) {
                            sendAlive();
                            baseListener.updateStateView(getAppStatus().toString());
                            checkWarnings();
                            //checkErrors();
                        }
                        Thread.sleep(UPDATE_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // flow //
    public void destroy() {
        isFinished = true;
        Log.e(TAG, "thread is destroyed");
    }

    public void pause() {
        isPause = true;
        Log.e(TAG, "thread is paused");
    }

    public void resume() {
        isPause = false;
        Log.e(TAG, "thread is resume");
    }

    private void checkWarnings(){
        if(baseListener == null) {
            return;
        }

        WarningStatus newWarningStatus;
        int batteryPercentage = baseListener.getBatteryPercentage();
        if(batteryPercentage <= 10){
            newWarningStatus = WarningStatus.CAR_PHONE_VERY_LOW_BATTERY;
        }
        else if(batteryPercentage <= 20){
            newWarningStatus = WarningStatus.CAR_PHONE_LOW_BATTERY;
        }
        else if(didNotVideoStream){
            newWarningStatus = WarningStatus.DID_NOT_RECEIVE_VIDEO_STREAM;
        }
        else{
            newWarningStatus = WarningStatus.NONE;
        }

        if(warningStatus != newWarningStatus) {
            warningStatus = newWarningStatus;
            baseListener.showWarning(warningStatus);
        }
    }

    private void checkErrors(){
// do nothing
    }

    private void update() {
        AppStatus currentStatus;

        lock.readLock().lock();
        currentStatus = appStatus;
        lock.readLock().unlock();

        switch (currentStatus){
            case WAIT_TO_CONNECT:
                break;
            case CONNECT:
                break;
            case WAIT_FOR_CAR_TO_CONNECT:
                waitForCarToConnect();
                break;
            case STANDBY:
                break;
            case DISPLAY_PLACE_OPTIONS:
                break;
            case SELECT_PLACE:
                break;
            case WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION:
                StationInformation info = StationInformation.getInstance();
                waitForCarToArriveAtDestination(info.getCarId(), info.getDestinationNodeId());
                break;
            case CAR_ARRIVE_AT_DESTINATION:
                break;
            case DISPLAY_PROMO_VIDEO:
                break;
            case CANCEL_PLACE:
                setStationStatus(AppStatus.STANDBY);
                break;
            case DISCONNECT:
                break;
            default:
                break;
        }
    }

    public void setStationStatus(AppStatus status)
    {
        AppStatus previousStatus = getAppStatus();
        switch (status){
            case WAIT_TO_CONNECT:
                setAppStatus(AppStatus.WAIT_TO_CONNECT);
                break;
            case CONNECT:
                if(previousStatus == AppStatus.WAIT_TO_CONNECT)
                    setAppStatus(AppStatus.CONNECT);
                break;
            case WAIT_FOR_CAR_TO_CONNECT:
                if(previousStatus == AppStatus.CONNECT)
                    setAppStatus(AppStatus.WAIT_FOR_CAR_TO_CONNECT);
                break;
            case STANDBY:
                if(previousStatus == AppStatus.WAIT_FOR_CAR_TO_CONNECT || previousStatus == AppStatus.DISPLAY_PROMO_VIDEO || previousStatus == AppStatus.CANCEL_PLACE)
                    setAppStatus(AppStatus.STANDBY);
                break;
            case DISPLAY_PLACE_OPTIONS:
                if(previousStatus == AppStatus.STANDBY){
                    setAppStatus(DISPLAY_PLACE_OPTIONS);
                }
                break;
            case SELECT_PLACE:
                if(previousStatus == DISPLAY_PLACE_OPTIONS){
                    setAppStatus(AppStatus.SELECT_PLACE);
                }
                break;
            case WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION:
                if(previousStatus == AppStatus.SELECT_PLACE){
                    setAppStatus(AppStatus.WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION);
                    baseListener.goToNextActivity();
                }
                break;
            case CAR_ARRIVE_AT_DESTINATION:
                if(previousStatus == AppStatus.WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION){
                    setAppStatus(AppStatus.CAR_ARRIVE_AT_DESTINATION);
                    baseListener.playAnimation();
                }
                break;
            case DISPLAY_PROMO_VIDEO:
                if(previousStatus == AppStatus.CAR_ARRIVE_AT_DESTINATION){
                    setAppStatus(AppStatus.DISPLAY_PROMO_VIDEO);
                }
                break;
            case CANCEL_PLACE:
                setAppStatus(AppStatus.CANCEL_PLACE);
                break;
            case DISCONNECT:
                setAppStatus(AppStatus.DISCONNECT);
                break;
            default:
                break;
        }

    }


    // thread related functions//
    protected void setAppStatus(AppStatus status) {
        lock.writeLock().lock();
        appStatus = status;
        lock.writeLock().unlock();
        StationInformation.getInstance().setStatus(status);
    }

    public AppStatus getAppStatus() {
        AppStatus currentStatus;
        lock.readLock().lock();
        currentStatus = appStatus;
        lock.readLock().unlock();
        return currentStatus;
    }

    protected void sendAlive(){
        AppStatus currentStatus = getAppStatus();
        if(currentStatus != null && currentStatus != AppStatus.WAIT_TO_CONNECT && currentStatus != AppStatus.CONNECT && currentStatus != AppStatus.WAIT_FOR_CAR_TO_CONNECT && currentStatus != AppStatus.DISCONNECT)
            sendAliveAPI(currentStatus.toString());
    }

    // apis //
    protected void sendAliveAPI(String appStatus){
        ApiCommunicator.getInstance().alive(appStatus, baseListener.getBatteryPercentage(), warningStatus, errorStatus, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void getConfigFromServer(String stationId, String serverUrl, String deviceId, String ipAddress, String appVersion){
        baseListener.showSpinner();
        ApiCommunicator apiCommunicator = ApiCommunicator.getInstance();
        apiCommunicator.initialRetrofit(serverUrl, deviceId, stationId, 0, ipAddress);
        apiCommunicator.getConfig(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.body() != null) {
                    Log.d("API_CALL", response.body());

                    try {
                        JSONObject returnObject = new JSONObject(response.body());

                        if (returnObject.getBoolean("success")) {
                            JSONArray configArray = returnObject.getJSONArray("config");

                            if(configArray.length() > 0){
                                JSONObject configObject = configArray.getJSONObject(0);

                                int howToTryGraphicDelay = 15;
                                try {
                                    howToTryGraphicDelay = configObject.getInt("how_to_try_graphic_delay");
                                }
                                catch (JSONException e) {
                                    baseListener.updateStateView("Error reading 'how_to_try_graphic_delay' config");
                                }


                                ArrayList<Pair<Integer, Integer>> attractionNodeAndPathIds = new ArrayList<Pair<Integer, Integer>>();
                                try {
                                    JSONArray attractionNodeIdsJsonArray = configObject.getJSONArray("attraction_node_ids");

                                    for (int i = 0; i < attractionNodeIdsJsonArray.length(); i++) {
                                        JSONObject attractionObject = attractionNodeIdsJsonArray.getJSONObject(i);
                                        int nodeId = attractionObject.getInt("node_id");
                                        int pathId = attractionObject.getInt("path_id");
                                        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(nodeId, pathId);
                                        attractionNodeAndPathIds.add(pair);
                                    }
                                }
                                catch (JSONException e) {
                                    baseListener.updateStateView("Error reading 'attraction_node_ids' config");
                                }

                                String appVersionString = "";
                                try {
                                    JSONObject appVersionObj = configObject.getJSONObject("app_version");
                                    if(appVersionObj.has("station")){
                                        appVersionString = appVersionObj.getString("station");
                                    }
                                }
                                catch (JSONException e) {
                                    baseListener.updateStateView("Error reading 'app_version' config");
                                }

                                String appDownloadPath = "";
                                try {
                                    JSONObject appDownloadPathObj = configObject.getJSONObject("app_download_path");
                                    if(appDownloadPathObj.has("station")){
                                        appDownloadPath = appDownloadPathObj.getString("station");
                                    }
                                }
                                catch (JSONException e) {
                                    baseListener.updateStateView("Error reading 'app_download_path' config");
                                }

                                Config config = Config.getInstance();
                                config.setAttractionNodeAndPathIds(attractionNodeAndPathIds);
                                config.setHowToTryGraphicDelay(howToTryGraphicDelay);
                                config.setLatestAppVersion(appVersionString);
                                config.setAppDownloadPath(appDownloadPath);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        if(Config.APP_VERSION.equals(Config.getInstance().getLatestAppVersion())) {
                            connectToServer();
                        }else{
                            baseListener.goToNextActivity(); // go to update app activity
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("API_CALL", t.getMessage());
                baseListener.hideSpinner();
                baseListener.showError(ErrorStatus.CANNOT_COMMUNICATE_WITH_SERVER);
            }
        });
    }

    public void connectToServer() {
        baseListener.showSpinner();
        ApiCommunicator apiCommunicator = ApiCommunicator.getInstance();
        setStationStatus(AppStatus.CONNECT);
        apiCommunicator.connectStation(new retrofit2.Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d("API_CALL", response.body());

                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
                            setStationStatus(AppStatus.WAIT_FOR_CAR_TO_CONNECT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("API_CALL", t.getMessage());
                baseListener.hideSpinner();
                baseListener.showError(ErrorStatus.CANNOT_COMMUNICATE_WITH_SERVER);
            }
        });
    }

    public void waitForCarToConnect(){
        ApiCommunicator.getInstance().waitForCarToConnect( new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());

                        if (returnObject.getBoolean("success")) {
                            if (returnObject.getBoolean("connected")) {

                                JSONObject carObject = returnObject.getJSONObject("car");
                                StationInformation.getInstance().setCarId(carObject.getString("car_id"));
                                StationInformation.getInstance().setCarIpAddress(carObject.getString("ip_address"));

                                setStationStatus(AppStatus.STANDBY);
                                baseListener.goToNextActivity();
                                baseListener.hideSpinner();
                            }
                        } else {
                            setStationStatus(AppStatus.WAIT_FOR_CAR_TO_CONNECT);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                baseListener.hideSpinner();
            }
        });
    }

    public void selectPlace(int placeNodeId, int placePathId){
        StationInformation.getInstance().setDestinationNodeId(placeNodeId);
        setStationStatus(AppStatus.SELECT_PLACE);
        ApiCommunicator.getInstance().selectPlace(placeNodeId, placePathId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
                            setStationStatus(AppStatus.WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("SELECT_PLACE", t.getMessage());
                baseListener.showError(ErrorStatus.CANNOT_COMMUNICATE_WITH_SERVER);
            }
        });
    }

    public void waitForCarToArriveAtDestination(String carId, int destinationNodeId){
        ApiCommunicator.getInstance().waitForCarToArriveAtDestination(carId, destinationNodeId, new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d(TAG , " : waitForCarToArriveAtDestination"+ response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());

                        if (returnObject.getBoolean("arrived")) {
                            setStationStatus(AppStatus.CAR_ARRIVE_AT_DESTINATION);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public void waitForCarDisconnect(){
        ApiCommunicator.getInstance().waitForCarDisconnect( new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d(TAG, response.body());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
                            setStationStatus(AppStatus.WAIT_TO_CONNECT);
                            baseListener.goToSetupActivity();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                d(TAG, t.getMessage());
            }
        });
    }

    public void disconnect() {
        setStationStatus(AppStatus.DISCONNECT);
        ApiCommunicator.getInstance().disconnectStation(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d(TAG, response.body());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
                           setStationStatus(AppStatus.WAIT_TO_CONNECT);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("DISCONNECT", t.getMessage());
                baseListener.showError(ErrorStatus.CANNOT_COMMUNICATE_WITH_SERVER);
            }
        });
    }

    //misc//
    public void setBaseListener(Context context){
        try {
            baseListener = (BaseListener) context;
        }
        catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public void failToVideoStream(boolean fail){
        didNotVideoStream = fail;
    }
}
