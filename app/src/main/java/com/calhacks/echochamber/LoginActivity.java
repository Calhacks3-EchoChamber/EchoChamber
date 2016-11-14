package com.calhacks.echochamber;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.loopj.android.http.RequestParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    LoginButton facebookLogin;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        facebookLogin = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLogin.setReadPermissions("public_profile", "user_friends", "user_location");

        Log.d(TAG, "Registering Callback");
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // TODO: Success
                Log.d(TAG, "Success!");
                String userID = loginResult.getAccessToken().getUserId();
                if (userID == null || userID.isEmpty()) return;
                loginUser(userID);
            }

            @Override
            public void onCancel() {
                // TODO: Canceled
                Log.d(TAG, "Canceled!");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "Error logging in user!");
                Toast.makeText(getApplicationContext(),
                        "Failed to login to Facebook, try again later", Toast.LENGTH_SHORT).show();
            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && accessToken.getUserId() != null) {
            Log.d(TAG, "User already logged in!");
            loginUser(accessToken.getUserId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loginUser(String userID) {
        Log.d(TAG, "Logging in user with ID: " + userID);
        RequestParams params = new RequestParams();
        params.put("uid", userID);
        NetworkManager.userLogin(params);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
