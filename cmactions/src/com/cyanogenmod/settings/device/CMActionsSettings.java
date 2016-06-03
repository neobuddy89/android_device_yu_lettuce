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
    private static final String TOUCHSCREEN_MSG_GESTURE_KEY = "touchscreen_gesture_message";
    private static final String TOUCHSCREEN_EMAIL_GESTURE_KEY = "touchscreen_gesture_email";
    private static final String TOUCHSCREEN_CUSTOM_GESTURE_KEY = "touchscreen_gesture_custom";

    // Proc nodes
    public static final String TOUCHSCREEN_CAMERA_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/camera_enable";
    public static final String TOUCHSCREEN_MUSIC_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/music_enable";
    public static final String TOUCHSCREEN_FLASHLIGHT_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/flashlight_enable";
    public static final String TOUCHSCREEN_MESSAGE_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/message_enable";
    public static final String TOUCHSCREEN_EMAIL_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/email_enable";
    public static final String TOUCHSCREEN_W_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/custom_w_enable";
    public static final String TOUCHSCREEN_Z_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/custom_z_enable";
    public static final String TOUCHSCREEN_V_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/custom_v_enable";
    public static final String TOUCHSCREEN_S_NODE =
            "/sys/devices/soc.0/78b9000.i2c/i2c-5/5-0040/custom_s_enable";

    // Key Masks
    private boolean mIsGesture_CAM_Enabled;
    private boolean mIsGesture_MUS_Enabled;
    private boolean mIsGesture_FLA_Enabled;
    private boolean mIsGesture_MSG_Enabled;
    private boolean mIsGesture_EML_Enabled;
    private boolean mIsGesture_CUS_Enabled;

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
        FileUtils.writeLine(TOUCHSCREEN_FLASHLIGHT_NODE, mIsGesture_FLA_Enabled ? "1" : "0");
        mIsGesture_MSG_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_MSG_GESTURE_KEY, false);
        FileUtils.writeLine(TOUCHSCREEN_MESSAGE_NODE, mIsGesture_MSG_Enabled ? "1" : "0");
        mIsGesture_EML_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_EMAIL_GESTURE_KEY, false);
        FileUtils.writeLine(TOUCHSCREEN_EMAIL_NODE, mIsGesture_EML_Enabled ? "1" : "0");
        mIsGesture_CUS_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_CUSTOM_GESTURE_KEY, false);
        FileUtils.writeLine(TOUCHSCREEN_W_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
        FileUtils.writeLine(TOUCHSCREEN_Z_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
        FileUtils.writeLine(TOUCHSCREEN_V_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
        FileUtils.writeLine(TOUCHSCREEN_S_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
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
                   } else if (TOUCHSCREEN_MSG_GESTURE_KEY.equals(key)) {
                        mIsGesture_MSG_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_MSG_GESTURE_KEY, false);
	                FileUtils.writeLine(TOUCHSCREEN_MESSAGE_NODE, mIsGesture_MSG_Enabled ? "1" : "0");
                   } else if (TOUCHSCREEN_EMAIL_GESTURE_KEY.equals(key)) {
                        mIsGesture_EML_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_EMAIL_GESTURE_KEY, false);
	                FileUtils.writeLine(TOUCHSCREEN_EMAIL_NODE, mIsGesture_EML_Enabled ? "1" : "0");
                   } else if (TOUCHSCREEN_CUSTOM_GESTURE_KEY.equals(key)) {
                        mIsGesture_CUS_Enabled = sharedPreferences.getBoolean(TOUCHSCREEN_CUSTOM_GESTURE_KEY, false);
                        FileUtils.writeLine(TOUCHSCREEN_W_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
                        FileUtils.writeLine(TOUCHSCREEN_Z_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
                        FileUtils.writeLine(TOUCHSCREEN_V_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
                        FileUtils.writeLine(TOUCHSCREEN_S_NODE, mIsGesture_CUS_Enabled ? "1" : "0");
                    }
                }
            };
}
