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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CallWebService {
    @Headers("Content-Type: application/json")
    @GET("/get_config")
    Call<String> getConfig();

    @Headers("Content-Type: application/json")
    @POST("/connect_station")
    Call<String> connectStation(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/wait_for_car_to_connect")
    Call<String> waitForCarToConnect(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/select_start")
    Call<String> selectStart(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/alive")
    Call<String> alive(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/wait_for_car_to_arrive_at_destination")
    Call<String> waitForCarToArriveAtDestination(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/select_place")
    Call<String> selectPlace(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/cancel_place")
    Call<String> cancelPlace(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/wait_for_car_disconnect")
    Call<String> waitForCarDisconnect(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/disconnect_station")
    Call<String> disconnectStation(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/video_started")
    Call<String> videoStarted(@Body String body);

    @Headers("Content-Type: application/json")
    @POST("/video_finished")
    Call<String> videoFinished(@Body String body);

}
