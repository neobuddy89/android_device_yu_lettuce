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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import cyanogenmod.providers.CMSettings;

import org.cyanogenmod.internal.util.ScreenType;

public class TouchscreenGestureSettings extends PreferenceActivity {

    private static final String KEY_AMBIENT_DISPLAY_ENABLE = "ambient_display_enable";
    private static final String KEY_HAND_WAVE = "gesture_hand_wave";
    private static final String KEY_GESTURE_POCKET = "gesture_pocket";
    private static final String KEY_PROXIMITY_WAKE = "proximity_wake_enable";
    private static final String KEY_HAPTIC_FEEDBACK = "touchscreen_gesture_haptic_feedback";
    private static final String KEY_CUSTOM_GESTURE = "touchscreen_gesture_custom";
    private static final String KEY_W_INTENT = "touchscreen_gesture_w_intent";
    private static final String KEY_Z_INTENT = "touchscreen_gesture_z_intent";
    private static final String KEY_V_INTENT = "touchscreen_gesture_v_intent";
    private static final String KEY_S_INTENT = "touchscreen_gesture_s_intent";

    private static final int REQUEST_PICK_SHORTCUT = 100;
    private static final int REQUEST_CREATE_SHORTCUT = 101;

    private SwitchPreference mAmbientDisplayPreference;
    private SwitchPreference mHandwavePreference;
    private SwitchPreference mPocketPreference;
    private SwitchPreference mProximityWakePreference;
    private SwitchPreference mHapticFeedback;
    private SwitchPreference mCustomPreference;

    private ListPreference mWIntent;
    private ListPreference mZIntent;
    private ListPreference mVIntent;
    private ListPreference mSIntent;

    private String preferenceKeyLastChangedShortcut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.touchscreen_panel);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean dozeEnabled = isDozeEnabled();
        boolean custEnabled;

        mAmbientDisplayPreference =
            (SwitchPreference) findPreference(KEY_AMBIENT_DISPLAY_ENABLE);
        // Read from DOZE_ENABLED secure setting
        mAmbientDisplayPreference.setChecked(dozeEnabled);
        mAmbientDisplayPreference.setOnPreferenceChangeListener(mAmbientDisplayPrefListener);
        mHandwavePreference =
            (SwitchPreference) findPreference(KEY_HAND_WAVE);
        mHandwavePreference.setEnabled(dozeEnabled);
        mHandwavePreference.setOnPreferenceChangeListener(mProximityListener);
        mPocketPreference =
            (SwitchPreference) findPreference(KEY_GESTURE_POCKET);
        mPocketPreference.setEnabled(dozeEnabled);
        mProximityWakePreference =
            (SwitchPreference) findPreference(KEY_PROXIMITY_WAKE);
        mProximityWakePreference.setOnPreferenceChangeListener(mProximityListener);

        mHapticFeedback = (SwitchPreference) findPreference(KEY_HAPTIC_FEEDBACK);
        mHapticFeedback.setOnPreferenceChangeListener(mHapticFeedbackListener);

        mCustomPreference = (SwitchPreference) findPreference(KEY_CUSTOM_GESTURE);
        mCustomPreference.setOnPreferenceChangeListener(mCustomPreferenceListener);
        custEnabled = sharedPrefs.getBoolean(KEY_CUSTOM_GESTURE, false);

        mWIntent = (ListPreference) findPreference(KEY_W_INTENT);
        mWIntent.setOnPreferenceChangeListener(mWIntentListener);
        mWIntent.setEnabled(custEnabled);

        mZIntent = (ListPreference) findPreference(KEY_Z_INTENT);
        mZIntent.setOnPreferenceChangeListener(mZIntentListener);
        mZIntent.setEnabled(custEnabled);

        mVIntent = (ListPreference) findPreference(KEY_V_INTENT);
        mVIntent.setOnPreferenceChangeListener(mVIntentListener);
        mVIntent.setEnabled(custEnabled);

        mSIntent = (ListPreference) findPreference(KEY_S_INTENT);
        mSIntent.setOnPreferenceChangeListener(mSIntentListener);
        mSIntent.setEnabled(custEnabled);

        new InitListTask().execute();

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private boolean enableDoze(boolean enable) {
        return Settings.Secure.putInt(getContentResolver(),
                Settings.Secure.DOZE_ENABLED, enable ? 1 : 0);
    }

    private boolean isDozeEnabled() {
        return Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.DOZE_ENABLED, 1) != 0;
    }

    private Preference.OnPreferenceChangeListener mAmbientDisplayPrefListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean enable = (boolean) newValue;
            boolean ret = enableDoze(enable);
            if (ret) {
                mHandwavePreference.setEnabled(enable);
                mPocketPreference.setEnabled(enable);
            }
            return ret;
        }
    };

    private Preference.OnPreferenceChangeListener mProximityListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if ((boolean) newValue) {
                if (preference.getKey().equals(KEY_HAND_WAVE)) {
                    mProximityWakePreference.setChecked(false);
                } else if (preference.getKey().equals(KEY_PROXIMITY_WAKE)) {
                    mHandwavePreference.setChecked(false);
                }
            }
            return true;
        }
    };

    private Preference.OnPreferenceChangeListener mHapticFeedbackListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_HAPTIC_FEEDBACK)) {
                final boolean value = (Boolean) newValue;
                CMSettings.System.putInt(getContentResolver(),
                        CMSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK, value ? 1 : 0);
                return true;
            }

            return false;
        }
    };

    private Preference.OnPreferenceChangeListener mCustomPreferenceListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_CUSTOM_GESTURE)) {
                final boolean value = (Boolean) newValue;
                mWIntent.setEnabled(value);
                mZIntent.setEnabled(value);
                mVIntent.setEnabled(value);
                mSIntent.setEnabled(value);
                return true;
            }

            return false;
        }
    };

    private Preference.OnPreferenceChangeListener mWIntentListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_W_INTENT)) {
                final String value = (String) newValue;
                if (value.equals("shortcut")) {
                    createShortcutPicked(KEY_W_INTENT);
                } else {
                    Settings.System.putString(getContentResolver(), KEY_W_INTENT, value);
                    reloadSummary();
                }
                return true;
            }

            return false;
        }
    };

    private Preference.OnPreferenceChangeListener mZIntentListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_Z_INTENT)) {
                final String value = (String) newValue;
                if (value.equals("shortcut")) {
                    createShortcutPicked(KEY_Z_INTENT);
                } else {
                    Settings.System.putString(getContentResolver(), KEY_Z_INTENT, value);
                    reloadSummary();
                }
                return true;
            }

            return false;
        }
    };

    private Preference.OnPreferenceChangeListener mVIntentListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_V_INTENT)) {
                final String value = (String) newValue;
                if (value.equals("shortcut")) {
                    createShortcutPicked(KEY_V_INTENT);
                } else {
                    Settings.System.putString(getContentResolver(), KEY_V_INTENT, value);
                    reloadSummary();
                }
                return true;
            }

            return false;
        }
    };

    private Preference.OnPreferenceChangeListener mSIntentListener =
        new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference.getKey().equals(KEY_S_INTENT)) {
                final String value = (String) newValue;
                if (value.equals("shortcut")) {
                    createShortcutPicked(KEY_S_INTENT);
                } else {
                    Settings.System.putString(getContentResolver(), KEY_S_INTENT, value);
                    reloadSummary();
                }
                return true;
            }

            return false;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_SHORTCUT) {
                startActivityForResult(data, REQUEST_CREATE_SHORTCUT);
            }
            if (requestCode == REQUEST_CREATE_SHORTCUT) {
                Intent intent = data.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
                intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, data.getStringExtra(
                        Intent.EXTRA_SHORTCUT_NAME));
                String uri = intent.toUri(Intent.URI_INTENT_SCHEME);
                if (preferenceKeyLastChangedShortcut != null) {
                    Settings.System.putString(getContentResolver(),
                            preferenceKeyLastChangedShortcut, uri);
                    reloadSummary();
                }
            }
        } else {
            Settings.System.putString(getContentResolver(),
                    preferenceKeyLastChangedShortcut, "default");
            reloadSummary();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createShortcutPicked(String key) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
        pickIntent.putExtra(Intent.EXTRA_INTENT, new Intent(Intent.ACTION_CREATE_SHORTCUT));
        pickIntent.putExtra(Intent.EXTRA_TITLE, "Select shortcut");
        startActivityForResult(pickIntent, REQUEST_PICK_SHORTCUT);
        preferenceKeyLastChangedShortcut = key;
    }

    private List<String> getPackageNames() {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        List<String> packageNameList = new ArrayList<String>();
        List<PackageInfo> packs = packageManager.getInstalledPackages(0);

        for (int i = 0; i < packs.size(); i++) {
            String packageName = packs.get(i).packageName;
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                packageNameList.add(packageName);
            }
        }
        return packageNameList;
    }

    private String getAppnameFromPackagename(String packagename) {
        if (packagename == null || "".equals(packagename)) {
            return getResources().getString(R.string.touchscreen_action_default);
        }
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packagename, 0);
        } catch (final Exception e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : packagename);
    }

    private String getSummary(String key) {
        String summary = Settings.System.getString(getContentResolver(), key);
        if (summary == null || summary.equals("default")) {
            return getResources().getString(R.string.touchscreen_action_default);
        } else if (summary.startsWith("intent:")) {
            return getResources().getString(R.string.touchscreen_action_shortcut);
        }
        return getAppnameFromPackagename(summary);
    }

    private void reloadSummary() {
        mWIntent.setSummary(getSummary(KEY_W_INTENT));
        mZIntent.setSummary(getSummary(KEY_Z_INTENT));
        mVIntent.setSummary(getSummary(KEY_V_INTENT));
        mSIntent.setSummary(getSummary(KEY_S_INTENT));
    }

     private class InitListTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<String> listPackageNames = getPackageNames();
            listPackageNames.add(0, "default");
            listPackageNames.add(1, "shortcut");
            final CharSequence[] packageNames =
                    listPackageNames.toArray(new CharSequence[listPackageNames.size()]);
            final CharSequence[] hrblPackageNames = new CharSequence[listPackageNames.size()];

            for (int i = 0; i < listPackageNames.size(); i++) {
                hrblPackageNames[i] = getAppnameFromPackagename(listPackageNames.get(i));
            }

            hrblPackageNames[0] = getResources().getString(R.string.touchscreen_action_default);
            hrblPackageNames[1] = getResources().getString(R.string.touchscreen_action_shortcut);

            mWIntent.setEntries(hrblPackageNames);
            mWIntent.setEntryValues(packageNames);

            mZIntent.setEntries(hrblPackageNames);
            mZIntent.setEntryValues(packageNames);

            mVIntent.setEntries(hrblPackageNames);
            mVIntent.setEntryValues(packageNames);

            mSIntent.setEntries(hrblPackageNames);
            mSIntent.setEntryValues(packageNames);

            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            reloadSummary();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHapticFeedback.setChecked(CMSettings.System.getInt(getContentResolver(),
                CMSettings.System.TOUCHSCREEN_GESTURE_HAPTIC_FEEDBACK, 1) != 0);

        // If running on a phone, remove padding around the listview
        if (!ScreenType.isTablet(this)) {
            getListView().setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
