package com.calhacks.echochamber;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Profile;

public class ProfileActivity extends Activity {
    private UserManager userManager;
    private NavigationDrawer navigationDrawer;
    private ImageView profilePic;
    private TextView profileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userManager = UserManager.getInstance();

        navigationDrawer = new NavigationDrawer(this, getWindow().getDecorView(), "Profile");
        navigationDrawer.init();

        Handler handler = new Handler();
        profilePic = (ImageView) findViewById(R.id.profile_page_pic);
        userManager.setProfilePicture(profilePic, handler);

        profileName = (TextView) findViewById(R.id.profile_page_name);
        profileName.setText(Profile.getCurrentProfile().getName());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        navigationDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        navigationDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (navigationDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
