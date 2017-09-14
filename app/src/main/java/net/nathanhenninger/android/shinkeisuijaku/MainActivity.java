package net.nathanhenninger.android.shinkeisuijaku;

import android.support.v4.app.Fragment;

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
