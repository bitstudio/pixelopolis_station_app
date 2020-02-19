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

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.bit.pixelopolis_station.services.StationInformation;
import com.bit.pixelopolis_station.R;
import com.bit.pixelopolis_station.enums.AppStatus;
import com.bit.pixelopolis_station.services.api.ApiCommunicator;
import com.bit.pixelopolis_station.services.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// display live stream from car app

public class NavigateCarActivity extends BaseActivity {

    WebView webView;
    LottieAnimationView journeyStartAnimationView;
    LottieAnimationView arrivedAnimationView;
    ImageView destinationView;

    Button reloadWebviewButton;
    Button cancelPlaceSelectionButton;

    String[] arrivalAnimationPaths = {"portrait_bg.zip", "super_zoom_bg.zip", "night_sight_bg.zip", "google_len_bg.zip","dual_ev_bg.zip"};
    String journeyStartAnimationPath = "journey_start_bg.zip";
    int[] destinationImagePaths = {R.drawable.destination_01,R.drawable.destination_02,R.drawable.destination_03,R.drawable.destination_04,R.drawable.destination_05};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_car);

        reloadWebviewButton = (Button)findViewById(R.id.reload_button);
        cancelPlaceSelectionButton = (Button)findViewById(R.id.cancel_button);

        if(!Config.getInstance().isInDebugMode()){
            reloadWebviewButton.setVisibility(View.GONE);
            cancelPlaceSelectionButton.setVisibility(View.GONE);
        }

        String ip = StationInformation.getInstance().getCarIpAddress();
        webView = (WebView)findViewById(R.id.webView);
        String data =
        "" +
                "<html>" +
                "<head>" +
                "       <style>" +
                "        body {" +
                "            margin: 0;" +
                "        }" +
                "        .center-fit {" +
                "            max-width: 100vw;" +
                "            max-height: 100vh;" +
                "            margin: 0;" +
                //"            transform: translate(0,50%) rotate(90deg) scale(1.40);" +
                "        }" +
                "    </style>" +
                "</head>" +
                "   <body>" +
                "       <center>" +
                "           <img class=\"center-fit\" src=\""+
                "http://" + ip + ":8080/" +
                "\" alt=\"Stream\" align=\"middle\">" +
                "       </center>" +
                "   </body>" +
                "</html>";

        webView.setWebViewClient(new WebViewClient() {
            boolean loadingFinished = true;
            boolean redirect = false;
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                webView.loadData(data, "text/html", null);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                webView.loadData(data, "text/html", null);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingFinished = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!redirect) {
                    loadingFinished = true;
                    stationController.failToVideoStream(false);
                } else {
                    redirect = false;
                }
            }
        });
        webView.loadData(data, "text/html", null);
        webView.getSettings().setBuiltInZoomControls(false);

        destinationView = findViewById(R.id.destinationImage);
        destinationView.setImageResource(destinationImagePaths[indexNode]);
        destinationView.setVisibility(View.INVISIBLE);

        journeyStartAnimationView = findViewById(R.id.journey_start_animation_view);
        journeyStartAnimationView.setAnimation(journeyStartAnimationPath);
        journeyStartAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                journeyStartAnimationView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        arrivedAnimationView = findViewById(R.id.arrived_animation_view);
        arrivedAnimationView.setAnimation(arrivalAnimationPaths[indexNode]);
        arrivedAnimationView.setVisibility(View.INVISIBLE);
        arrivedAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ApiCommunicator.getInstance().videoStarted(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {

                    }
                });
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                stationController.setStationStatus(AppStatus.DISPLAY_PROMO_VIDEO);
                goToNextActivity();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void playAnimation() {
        arrivedAnimationView.setVisibility(View.VISIBLE);
        arrivedAnimationView.playAnimation();
    }

    @Override
    public void goToNextActivity() {
        Intent intent =  new Intent(NavigateCarActivity.this, PromoVideoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    public void onClickReloadButton(View view){
        if(webView != null)
            webView.reload();
    }

    public void onClickCancelButton(View view){
        ApiCommunicator.getInstance().cancelPlace(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response != null && response.body() != null) {
                    Log.d("", response.body().toString());
                    try {
                        JSONObject returnObject = new JSONObject(response.body());
                        if (returnObject.getBoolean("success")) {
                            Intent intent = new Intent(NavigateCarActivity.this, StandbyActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            stationController.setStationStatus(AppStatus.CANCEL_PLACE);
                            finish();
                        }
                    } catch(JSONException e){
                            e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        webView = null;
        super.onDestroy();
    }
}