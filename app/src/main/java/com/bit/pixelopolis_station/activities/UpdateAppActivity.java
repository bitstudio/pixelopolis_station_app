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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bit.pixelopolis_station.services.config.Config;
import com.bit.pixelopolis_station.R;

public class UpdateAppActivity extends BaseActivity {

    TextView versionView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);
        versionView = findViewById(R.id.version_view);
        versionView.setText("Your application is outdated!\n\nCurrent Version : " + Config.APP_VERSION + "\nLatest Version : " + Config.getInstance().getLatestAppVersion());
    }

    public void onClickContinueConnectToServer(View view) {
        stationController.connectToServer();
    }

    public void updateApplication(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(Config.getInstance().getAppDownloadPath()));
        startActivity(intent);
    }
    @Override
    public void goToNextActivity() {
        Intent intent = new Intent(UpdateAppActivity.this, StandbyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
