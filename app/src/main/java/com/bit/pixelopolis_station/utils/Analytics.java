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

package com.bit.pixelopolis_station.utils;

import android.content.Context;
import android.os.Bundle;

import com.bit.pixelopolis_station.services.StationInformation;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics {

    public static void newSession(Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        StationInformation stationInformation = StationInformation.getInstance();

        Bundle params = new Bundle();
        params.putString("station_id", stationInformation.getStationId());
        params.putString("station_device_id", stationInformation.getDeviceId());
        params.putBoolean("new_session", true);
        firebaseAnalytics.logEvent("new_session", params);
        firebaseAnalytics.setUserProperty("station_id", stationInformation.getStationId());
        firebaseAnalytics.setUserProperty("station_device_id", stationInformation.getDeviceId());
    }

    public static void selectPlace(Context context, int placeNodeId) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        StationInformation stationInformation = StationInformation.getInstance();

        Bundle params = new Bundle();
        params.putString("station_id", stationInformation.getStationId());
        params.putString("station_device_id", stationInformation.getDeviceId());
        params.putInt("select_place_id", placeNodeId    );
        firebaseAnalytics.logEvent("select_place", params);
        firebaseAnalytics.setUserProperty("station_id", stationInformation.getStationId());
        firebaseAnalytics.setUserProperty("station_device_id", stationInformation.getDeviceId());
    }

    public static void startedPromoVideo(Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        StationInformation stationInformation = StationInformation.getInstance();

        Bundle params = new Bundle();
        params.putString("station_id", StationInformation.getInstance().getStationId());
        params.putString("station_device_id", stationInformation.getDeviceId());
        params.putBoolean("video_play", true);
        firebaseAnalytics.logEvent("started_video", params);
        firebaseAnalytics.setUserProperty("station_id", stationInformation.getStationId());
        firebaseAnalytics.setUserProperty("station_device_id", stationInformation.getDeviceId());
    }

    public static void finishedPromoVideo(Context context) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        StationInformation stationInformation = StationInformation.getInstance();

        Bundle params = new Bundle();
        params.putString("station_id", StationInformation.getInstance().getStationId());
        params.putString("station_device_id", stationInformation.getDeviceId());
        params.putBoolean("video_stop", true);
        firebaseAnalytics.logEvent("finished_video", params);
        firebaseAnalytics.setUserProperty("station_id", stationInformation.getStationId());
        firebaseAnalytics.setUserProperty("station_device_id", stationInformation.getDeviceId());
    }

}
