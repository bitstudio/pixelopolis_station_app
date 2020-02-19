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

public enum WarningStatus {
    NONE(""),
    CAR_PHONE_LOW_BATTERY("CAR_PHONE_LOW_BATTERY"),
    CAR_PHONE_VERY_LOW_BATTERY("CAR_PHONE_VERY_LOW_BATTERY"),
    DID_NOT_RECEIVE_VIDEO_STREAM("DID_NOT_RECEIVE_VIDEO_STREAM");

    private final String name;
    private WarningStatus(String name){
        this.name = name;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
