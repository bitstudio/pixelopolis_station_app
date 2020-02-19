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
import android.util.Pair;
import android.view.View;

import com.bit.pixelopolis_station.services.config.Config;
import com.bit.pixelopolis_station.R;
import com.bit.pixelopolis_station.utils.Analytics;

import java.util.ArrayList;


public class SelectPlaceActivity extends BaseActivity {

    private static String TAG = "SelectPlaceActivity";
    private static ArrayList<Pair<Integer, Integer>> attractionPlaceIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place);

        attractionPlaceIds = Config.getInstance().getAttractionNodeAndPathIds();
    }

    public void selectPlace(Pair<Integer, Integer> placeNodeAndPathId){
        int placeNodeId = placeNodeAndPathId.first;
        int placePathId = placeNodeAndPathId.second;
        stationController.selectPlace(placeNodeId, placePathId);
        Analytics.selectPlace(getApplicationContext(), placeNodeId);
    }

    @Override
    public void goToNextActivity(){
        Intent intent = new Intent(SelectPlaceActivity.this, NavigateCarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    public void onClickPlace01Button(View view) { setIndexNode(0);selectPlace(attractionPlaceIds.get(0));}
    public void onClickPlace02Button(View view) { setIndexNode(1);selectPlace(attractionPlaceIds.get(1));}
    public void onClickPlace03Button(View view) { setIndexNode(2);selectPlace(attractionPlaceIds.get(2));}
    public void onClickPlace04Button(View view) { setIndexNode(3);selectPlace(attractionPlaceIds.get(3));}
    public void onClickPlace05Button(View view) { setIndexNode(4);selectPlace(attractionPlaceIds.get(4));}
}
