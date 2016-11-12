package com.calhacks.echochamber;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.Profile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Drake on 11/12/2016.
 */

public class UserManager {
    private static final String TAG = "UserManager";
    private static UserManager userManager;
    private Bitmap profilePicture = null;

    protected UserManager() {
        // Enforce Singleton
    }

    public static UserManager getInstance() {
        if (userManager == null) {
            userManager = new UserManager();
        }
        return userManager;
    }

    public void setProfilePicture(final ImageView profilePic, final Handler handler) {
        if (profilePicture != null) {
            Log.d(TAG, "Profile picture already downloaded, using cached version");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    profilePic.setImageBitmap(profilePicture);
                    profilePic.invalidate();
                }
            });
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri proPicUri = Profile.getCurrentProfile().getProfilePictureUri(200, 200);
                    profilePicture = getImageBitmap(proPicUri.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            profilePic.setImageBitmap(profilePicture);
                            profilePic.invalidate();
                        }
                    });

                }
            }).start();
        }
    }

    private Bitmap getImageBitmap(String url) {
        Log.d(TAG, "Downloading profile picture from: " + url);
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }
}
