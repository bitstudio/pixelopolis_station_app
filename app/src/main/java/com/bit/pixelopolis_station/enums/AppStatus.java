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

package com.bit.pixelopolis_station.enums;

public enum AppStatus {
    WAIT_TO_CONNECT ("WAIT_TO_CONNECT"),
    CONNECT("CONNECT"),
    WAIT_FOR_CAR_TO_CONNECT ("WAIT_FOR_CAR_TO_CONNECT"),
    STANDBY("STANDBY"),
    DISPLAY_PLACE_OPTIONS("DISPLAY_PLACE_OPTIONS"),
    SELECT_PLACE("SELECT_PLACE"),
    WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION("WAIT_FOR_CAR_TO_ARRIVE_AT_DESTINATION"),
    CAR_ARRIVE_AT_DESTINATION("CAR_ARRIVE_AT_DESTINATION"),
    DISPLAY_PROMO_VIDEO("DISPLAY_PROMO_VIDEO"),
    CANCEL_PLACE("CANCEL_PLACE"),
    DISCONNECT("DISCONNECT");

    private final String name;
    AppStatus(String name){
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
