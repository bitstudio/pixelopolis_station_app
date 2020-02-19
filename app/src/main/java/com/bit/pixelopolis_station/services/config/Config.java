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

package com.bit.pixelopolis_station.services.config;

import android.util.Pair;

import java.util.ArrayList;


public class Config {
    public static String APP_VERSION = "1.11";
    public static int SPINNER_TIME_OUT = 8000;

    private static final Config ourInstance = new Config();

    public static Config getInstance() {
        return ourInstance;
    }

    boolean isInDebugMode;
    int howToTryGraphicDelay;
    String latestAppVersion;
    String appDownloadPath;

    public String getLatestAppVersion() {
        return latestAppVersion;
    }

    public void setLatestAppVersion(String latestAppVersion) {
        this.latestAppVersion = latestAppVersion;
    }

    public String getAppDownloadPath() {
        return appDownloadPath;
    }

    public void setAppDownloadPath(String appDownloadPath) {
        this.appDownloadPath = appDownloadPath;
    }

    private ArrayList<Pair<Integer, Integer>> attractionNodeAndPathIds;

    public ArrayList<Pair<Integer, Integer>> getAttractionNodeAndPathIds() {
        return attractionNodeAndPathIds;
    }

    public void setAttractionNodeAndPathIds(ArrayList<Pair<Integer, Integer>> attractionNodeAndPathIds) {
        this.attractionNodeAndPathIds = attractionNodeAndPathIds;
    }

    public boolean isInDebugMode() {
        return isInDebugMode;
    }

    public void setInDebugMode(boolean inDebugMode) {
        isInDebugMode = inDebugMode;
    }

    public int getHowToTryGraphicDelay() {
        return howToTryGraphicDelay;
    }

    public void setHowToTryGraphicDelay(int howToTryGraphicDelay) {
        this.howToTryGraphicDelay = howToTryGraphicDelay;
    }

}
