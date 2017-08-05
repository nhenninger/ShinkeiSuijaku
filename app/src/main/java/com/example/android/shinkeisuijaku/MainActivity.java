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
