/*
 * Copyright (C) 2016 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.settings.device;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.cyanogenmod.settings.device.utils.FileUtils;

import java.lang.Integer;

public class CMActionsSettings {
    private static final String TAG = "CMActions";

    // Preference keys
    private static final String TOUCHSCREEN_CAMERA_GESTURE_KEY = "touchscreen_gesture_camera";
    private static final String TOUCHSCREEN_MUSIC_GESTURE_KEY = "touchscreen_gesture_music";
    private static final String TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY =
            "touchscreen_gesture_flashlight";

    // Proc nodes
    public static final String TOUCHSCREEN_CAMERA_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/camera_enable";
    public static final String TOUCHSCREEN_MUSIC_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/music_enable";
    public static final String TOUCHSCREEN_FLASHLIGHT_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/flashlight_enable";

    // Key Masks
    private boolean mIsGesture_CAM_Enabled;
    private boolean mIsGesture_MUS_Enabled;
    private boolean mIsGesture_FLA_Enabled;

    private final Context mContext;

    public CMActionsSettings(Context context ) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        loadPreferences(sharedPrefs);
        sharedPrefs.registerOnSharedPreferenceChangeListener(mPrefListener);
        mContext = context;
    }

    public void loadPreferences(SharedPreferences sharedPreferences) {
        mIsGesture_CAM_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_CAMERA_GESTURE_KEY, false);
        FileUtils.writeLine(TOUCHSCREEN_CAMERA_NODE, mIsGesture_CAM_Enabled ? "1" : "0");
        mIsGesture_MUS_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_MUSIC_GESTURE_KEY, false);
        FileUtils.writeLine(TOUCHSCREEN_MUSIC_NODE, mIsGesture_MUS_Enabled ? "1" : "0");
        mIsGesture_FLA_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY, false);
        FileUtils.writeLine(TOUCHSCREEN_FLASHLIGHT_NODE,mIsGesture_FLA_Enabled ? "1" : "0");
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPrefListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (TOUCHSCREEN_CAMERA_GESTURE_KEY.equals(key)) {
                        mIsGesture_CAM_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_CAMERA_GESTURE_KEY, false);
	                FileUtils.writeLine(TOUCHSCREEN_CAMERA_NODE, mIsGesture_CAM_Enabled ? "1" : "0");
                    } else if (TOUCHSCREEN_MUSIC_GESTURE_KEY.equals(key)) {
                        mIsGesture_MUS_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_MUSIC_GESTURE_KEY, false);
 	                FileUtils.writeLine(TOUCHSCREEN_MUSIC_NODE, mIsGesture_MUS_Enabled ? "1" : "0");
                   } else if (TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY.equals(key)) {
                        mIsGesture_FLA_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_FLASHLIGHT_GESTURE_KEY, false);
	                FileUtils.writeLine(TOUCHSCREEN_FLASHLIGHT_NODE, mIsGesture_FLA_Enabled ? "1" : "0");
                    }
                }
            };
}
