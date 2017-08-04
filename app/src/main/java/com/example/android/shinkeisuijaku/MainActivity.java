package com.example.android.shinkeisuijaku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Launcher and primary activity of ShinkeiSuijaku.
 *
 * Analagous to PhotoGalleryActivity in PhotoGallery.
 */
public class MainActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return ShinkeiSuijakuFragment.newInstance();
    }
}
//public class MainActivity extends AppCompatActivity
//        implements SharedPreferences.OnSharedPreferenceChangeListener {
//    public static final int NUMBER_OF_LESSONS = 23;
//    private static final String TAG = "MainActivity";
//
//    private int mCurrentLesson = 1;
//    private boolean mLatinTextVisible;
//    private List<Card> mCardList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setupSharedPreferences();
//        // TODO call load JSON code here
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PreferenceManager.getDefaultSharedPreferences(this)
//                .unregisterOnSharedPreferenceChangeListener(this);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                startActivity(new Intent(this, SettingsActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(getString(R.string.pref_lesson_key))) {
//            mCurrentLesson = sharedPreferences.getInt(getString(
//                    R.string.pref_lesson_key),
//                    getResources().getInteger(R.integer.pref_lesson_default));
//        } else if (key.equals(getString(R.string.pref_display_latin_text_key))) {
//            mLatinTextVisible = sharedPreferences.getBoolean(getString(
//                    R.string.pref_display_latin_text_key),
//                    getResources().getBoolean(R.bool.pref_display_latin_text_default));
//        }
//        // TODO call board setup method here
//    }
//
//    private void setupSharedPreferences() {
//        SharedPreferences sharedPreferences = PreferenceManager
//                .getDefaultSharedPreferences(this);
//        mCurrentLesson = sharedPreferences.getInt(getString(
//                R.string.pref_lesson_key),
//                getResources().getInteger(R.integer.pref_lesson_default));
//        mLatinTextVisible = sharedPreferences.getBoolean(getString(
//                R.string.pref_display_latin_text_key),
//                getResources().getBoolean(R.bool.pref_display_latin_text_default));
//        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
//    }
//
//    private class LoadJSONFromFile extends AsyncTask<Integer, Void, List<Card>> {
//
//        @Override
//        protected List<Card> doInBackground(Integer... params) {
//            try {
//                return new LessonLoader().loadCards(MainActivity.this, params[0]);
//            } catch (IOException ioe) {
//                Log.e(TAG, "doInBackground: Failed to load JSON");
//            }
//        }
//
//        @Override
//        protected void onPostExecute(List<Card> cards) {
//            super.onPostExecute(cards);
//            // TODO Reset the board call here
//        }
//    }
//}
