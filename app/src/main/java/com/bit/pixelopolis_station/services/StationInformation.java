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

import android.util.Log;

import com.bit.pixelopolis_station.enums.AppStatus;

public class StationInformation {
    private static final StationInformation ourInstance = new StationInformation();

    public static StationInformation getInstance() {
        return ourInstance;
    }

    private AppStatus status;
    private String stationId;
    private String deviceId;
    private String carId;
    private String carIpAddress;
    int destinationNodeId;

    public int getDestinationNodeId() {
        return destinationNodeId;
    }

    public void setDestinationNodeId(int destinationNodeId) {
        this.destinationNodeId = destinationNodeId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCarIpAddress() {
        return carIpAddress;
    }

    public void setCarIpAddress(String carIpAddress) {
        this.carIpAddress = carIpAddress;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public AppStatus getStatus() {

        if(status == null)
            status = AppStatus.WAIT_TO_CONNECT;
        Log.e("pye", "load save" + status.toString());
        return status;
    }

    public void setStatus(AppStatus status) {
        Log.e("pye", "save" + status.toString());
        this.status = status;
    }
}
