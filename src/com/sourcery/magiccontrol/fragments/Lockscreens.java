
package com.sourcery.magiccontrol.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.CalendarContract.Calendars;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import com.sourcery.magiccontrol.SettingsPreferenceFragment;
import com.sourcery.magiccontrol.R;
import com.sourcery.magiccontrol.MagicControlActivity;
import com.sourcery.magiccontrol.fragments.LockscreenTargets;
	



public class Lockscreens extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "Lockscreens";
    private static final boolean DEBUG = true;

    private static final String PREF_MENU = "pref_lockscreen_menu_unlock";
    private static final String PREF_USER_OVERRIDE = "lockscreen_user_timeout_override";
    private static final String PREF_LOCKSCREEN_LAYOUT = "pref_lockscreen_layout";
    private static final String PREF_LOCKSCREEN_TEXT_COLOR = "lockscreen_text_color";
    private static final String PREF_VOLUME_WAKE = "volume_wake";
    private static final String PREF_VOLUME_MUSIC = "volume_music_controls"; 
    private static final String PREF_NUMBER_OF_TARGETS = "number_of_targets";

    private static final String PREF_LOCKSCREEN_BATTERY = "lockscreen_battery";
    
    
    private static final String PREF_SHOW_LOCK_BEFORE_UNLOCK = "show_lock_before_unlock";

    public static final int REQUEST_PICK_WALLPAPER = 199;
    public static final int REQUEST_PICK_CUSTOM_ICON = 200;
    public static final int SELECT_ACTIVITY = 2;
    public static final int SELECT_WALLPAPER = 3;

    private static final String WALLPAPER_NAME = "lockscreen_wallpaper.jpg";

    Preference mLockscreenWallpaper;
    Preference mLockscreenTargets;


    CheckBoxPreference menuButtonLocation;
    /*CheckBoxPreference mLockScreenTimeoutUserOverride;
    ListPreference mLockscreenOption;*/
    CheckBoxPreference mVolumeWake;
    CheckBoxPreference mVolumeMusic;
    /*CheckBoxPreference mLockscreenLandscape;*/
    CheckBoxPreference mLockscreenBattery;
    CheckBoxPreference mShowLockBeforeUnlock;
    ColorPickerPreference mLockscreenTextColor;
    ListPreference mTargetNumber;

    
    private int currentIconIndex;
    private Preference mCurrentCustomActivityPreference;
    private String mCurrentCustomActivityString;

    /*private ShortcutPickerHelper mPicker;*/

    ArrayList<String> keys = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*keys.add(Settings.System.LOCKSCREEN_HIDE_NAV);
        keys.add(Settings.System.LOCKSCREEN_LANDSCAPE);*/
        keys.add(Settings.System.LOCKSCREEN_QUICK_UNLOCK_CONTROL);
        /*keys.add(Settings.System.ENABLE_FAST_TORCH);*/

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_lockscreens);

        menuButtonLocation = (CheckBoxPreference) findPreference(PREF_MENU);
        menuButtonLocation.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_ENABLE_MENU_KEY, 1) == 1);

       /* mLockScreenTimeoutUserOverride = (CheckBoxPreference) findPreference(PREF_USER_OVERRIDE);
        mLockScreenTimeoutUserOverride.setChecked(Settings.Secure.getInt(getActivity()
                .getContentResolver(), Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE, 0) == 1);

        mLockscreenOption = (ListPreference) findPreference(PREF_LOCKSCREEN_LAYOUT);
        mLockscreenOption.setOnPreferenceChangeListener(this);
        mLockscreenOption.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_LAYOUT, 0) + "");*/

        mLockscreenBattery = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_BATTERY);
        mLockscreenBattery.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_BATTERY, 0) == 1);
        
      
        
        mShowLockBeforeUnlock = (CheckBoxPreference) findPreference(PREF_SHOW_LOCK_BEFORE_UNLOCK);
        mShowLockBeforeUnlock.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SHOW_LOCK_BEFORE_UNLOCK, 0) == 1);

        mVolumeWake = (CheckBoxPreference) findPreference(PREF_VOLUME_WAKE);
        mVolumeWake.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);

        mVolumeMusic = (CheckBoxPreference) findPreference(PREF_VOLUME_MUSIC);
        mVolumeMusic.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.VOLUME_MUSIC_CONTROLS, 0) == 1);

       

       /* mPicker = new ShortcutPickerHelper(this, this);*/

        for (String key : keys) {
            try {
                ((CheckBoxPreference) findPreference(key)).setChecked(Settings.System.getInt(
                        getActivity().getContentResolver(), key) == 1);
            } catch (SettingNotFoundException e) {
            }
        }

       /* ((PreferenceGroup) findPreference("advanced_cat"))
                .removePreference(findPreference(Settings.System.LOCKSCREEN_HIDE_NAV));*/
        
        mLockscreenTextColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_TEXT_COLOR);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);

         mTargetNumber = (ListPreference) findPreference(PREF_NUMBER_OF_TARGETS);
        mTargetNumber.setOnPreferenceChangeListener(this);
        mTargetNumber.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.LOCKSCREEN_TARGET_AMOUNT,
                2)));

        mLockscreenTargets = findPreference("lockscreen_targets");
        
        mLockscreenWallpaper = findPreference("wallpaper");

        setHasOptionsMenu(true);
    }
       
   @Override
     public void onResume() {
        super.onResume();
}

 @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	if (preference == mShowLockBeforeUnlock) {
           Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHOW_LOCK_BEFORE_UNLOCK,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
	}

        if (preference == menuButtonLocation) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_ENABLE_MENU_KEY,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
            
       /* } else if (preference == mLockScreenTimeoutUserOverride) {
            Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.LOCK_SCREEN_LOCK_USER_OVERRIDE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;*/
            
        } else if (preference == mShowLockBeforeUnlock) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SHOW_LOCK_BEFORE_UNLOCK,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;

        } else if (preference == mLockscreenBattery) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_BATTERY,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;

         } else if (preference == mVolumeWake) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLUME_WAKE_SCREEN,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenTargets) {
            Intent i = new Intent(getActivity(), MagicControlActivity.class)
                   .setAction("com.sourcery.magiccontrol.START_NEW_FRAGMENT")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("sourcery_fragment_name", LockscreenTargets.class.getName());
            getActivity().startActivity(i);
            return true;
       } else if (preference == mVolumeMusic) {

            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.VOLUME_MUSIC_CONTROLS,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
       } else if (preference == mLockscreenWallpaper) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            int width = display.getWidth();
            int height = display.getHeight();
            Rect rect = new Rect();
            Window window = getActivity().getWindow();
            window.getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;
            int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int titleBarHeight = contentViewTop - statusBarHeight;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getLockscreenExternalUri());

            if (mTablet) {
                width = getActivity().getWallpaperDesiredMinimumWidth();
                height = getActivity().getWallpaperDesiredMinimumHeight();
                float spotlightX = (float)display.getWidth() / width;
                float spotlightY = (float)display.getHeight() / height;
                intent.putExtra("aspectX", width);
                intent.putExtra("aspectY", height);
                intent.putExtra("outputX", width);
                intent.putExtra("outputY", height);
                intent.putExtra("spotlightX", spotlightX);
                intent.putExtra("spotlightY", spotlightY);
            } else {
                boolean isPortrait = getResources()
                        .getConfiguration().orientation ==
                        Configuration.ORIENTATION_PORTRAIT;
                intent.putExtra("aspectX", isPortrait ? width : height - titleBarHeight);
                intent.putExtra("aspectY", isPortrait ? height - titleBarHeight : width);
            }

            startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
            return true;
        } else if (keys.contains(preference.getKey())) {
            Log.e("RC_Lockscreens", "key: " + preference.getKey());
            return Settings.System.putInt(getActivity().getContentResolver(), preference.getKey(),
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreens, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.remove_wallpaper:
                File f = new File(mContext.getFilesDir(), WALLPAPER_NAME);
                Log.e(TAG, mContext.deleteFile(WALLPAPER_NAME) + "");
                Log.e(TAG, mContext.deleteFile(WALLPAPER_NAME) + "");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private Uri getLockscreenExternalUri() {
        File dir = mContext.getExternalCacheDir();
        File wallpaper = new File(dir, WALLPAPER_NAME);

        return Uri.fromFile(wallpaper);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean handled = false;
        if (preference == mLockscreenTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, intHex);
            if (DEBUG) Log.d(TAG, String.format("new color hex value: %d", intHex));
            return true;
         } else if (preference == mTargetNumber) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_TARGET_AMOUNT, val);
            return true;
        }
        return false;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {

                FileOutputStream wallpaperStream = null;
                try {
                    wallpaperStream = mContext.openFileOutput(WALLPAPER_NAME,
                            Context.MODE_WORLD_READABLE);
                } catch (FileNotFoundException e) {
                    return; // NOOOOO
                }

                Uri selectedImageUri = getLockscreenExternalUri();
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, wallpaperStream);
            }
        }
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
