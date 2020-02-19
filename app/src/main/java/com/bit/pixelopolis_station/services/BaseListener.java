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

import com.bit.pixelopolis_station.enums.ErrorStatus;
import com.bit.pixelopolis_station.enums.WarningStatus;

public interface BaseListener {
    void showWarning(WarningStatus warningStatus);
    void showError(ErrorStatus errorStatus);
    void setStationIdTextbox(String stationId);
    void showSpinner();
    void hideSpinner();
    void showDialog();
    void updateStateView(String string);
    void playAnimation();
    void goToNextActivity();
    void goToSetupActivity();
    int getBatteryPercentage();
}
