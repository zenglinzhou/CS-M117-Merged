/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.example.android.bluetoothchat;

import android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";
    private BluetoothAdapter bta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bta = BluetoothAdapter.getDefaultAdapter();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void victim(View v) {

        Intent intent = new Intent (MainActivity.this, SMSActivity.class);
        startActivity(intent);


    }

    public void responder(View v) {
        Intent intent = new Intent(MainActivity.this, ResponderScreen.class);
        Random rand = new Random();
        int n = rand.nextInt(999999)+1;
        bta.setName("RESPONDER_" + Integer.toString(n));
        startActivity(intent);
        finish();
    }



}