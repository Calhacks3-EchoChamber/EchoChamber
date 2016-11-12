package com.calhacks.echochamber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.calhacks.echochamber.Conversation.ConversationListActivity;
import com.facebook.Profile;
import com.facebook.login.LoginManager;

/**
 * Created by Drake on 11/12/2016.
 */

public class NavigationDrawer {
    private static final String TAG = "NavigationDrawer";
    private UserManager userManager;
    private Activity activity;
    private View view;
    private String[] drawerItems = new String[]{"Topics", "Conversations", "Settings", "Log Out"};
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout profileSquare;
    private ImageView profilePic;
    private TextView profileName;
    private String currentPage;

    public NavigationDrawer(final Context context, View view, final String currentPage) {
        this.activity = (Activity) context;
        this.view = view;
        this.currentPage = currentPage;

        userManager = UserManager.getInstance();
        activity.getActionBar().setTitle(currentPage);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawerList = (ListView) view.findViewById(R.id.nav_drawer);

        // Adapter for Nav Drawer ListView
        drawerList.setAdapter(new ArrayAdapter<>(activity,
                R.layout.drawer_list_item, drawerItems));

        profileSquare = (RelativeLayout) view.findViewById(R.id.profile_square);
        profileSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage.equals("Profile")) {
                    drawerLayout.closeDrawers();
                    return;
                }
                Intent intent = new Intent(context, ProfileActivity.class);
                context.startActivity(intent);
            }
        });

        // Set list click listener
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "Clicked: " + drawerItems[i]);
                if (drawerItems[i].equals(currentPage)) {
                    drawerLayout.closeDrawers();
                    return;
                }

                if (drawerItems[i].equals("Log Out")) {
                    logOut();
                } else if (drawerItems[i].equals("Conversations")) {
                    Intent intent = new Intent(activity, ConversationListActivity.class);
                    activity.startActivity(intent);
                } else if (drawerItems[i].equals("Topics")) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    activity.startActivity(intent);
                }
            }
        });

        drawerToggle = new ActionBarDrawerToggle(
                activity,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                activity.getActionBar().setTitle(currentPage);
            }

            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                activity.getActionBar().setTitle("Navigation");
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        activity.getActionBar().setHomeButtonEnabled(true);
        activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);
    }

    public void init() {
        profileName = (TextView) view.findViewById(R.id.profile_name);
        String proName = Profile.getCurrentProfile().getName();
        profileName.setText(proName);

        profilePic = (ImageView) view.findViewById(R.id.profile_pic);
        Handler handler = new Handler();
        userManager.setProfilePicture(profilePic, handler);
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    private void logOut() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }
}
