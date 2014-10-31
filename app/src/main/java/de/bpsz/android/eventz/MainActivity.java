package de.bpsz.android.eventz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

import java.util.Hashtable;


public class MainActivity extends FragmentActivity {

    // Email, password edittext
    EditText txtUsername, txtPassword;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;

    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS + 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private Hashtable<Integer, String> fragmentTitles = new Hashtable<Integer, String>();

    private boolean isResumed = false;
    private UiLifecycleHelper uiHelper;

    DrawerLayout drawerLayout;
    ListView drawerList;
    ActionBarDrawerToggle drawerToggle;
    String title = "";

    private Session.StatusCallback callback =
            new Session.StatusCallback() {
                @Override
                public void call(Session session,
                                 SessionState state, Exception exception) {
                    onSessionStateChange(session, state, exception);
                }
            };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        txtUsername = (EditText) findViewById(R.id.login_username);
        txtPassword = (EditText) findViewById(R.id.login_password);

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        // Login button
        btnLogin = (Button) findViewById(R.id.login_button_without_facebook);

        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Check if username, password is filled
                if (username.trim().length() > 0 && password.trim().length() > 0) {
                    // For testing puspose username, password is checked with sample data
                    // username = test
                    // password = test
                    if (username.equals("test") && password.equals("test")) {

                        // Creating user login session
                        // For testing i am strong name, email as follow
                        // Use user real data
                        session.createLoginSession("Calvin Paciner", "cpaciner@gmail.com");

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), LoginSuccessfulActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        // username / password doesn't match
                        alert.showAlertDialog(MainActivity.this, "Login failed..", "Username/Password is incorrect", false);
                    }
                } else {
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(MainActivity.this, "Login failed..", "Please enter username and password", false);
                }
            }
        });

        initNavDrawer();

        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragmentTitles.put(SPLASH, getString(R.string.splash_fragment));
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        fragmentTitles.put(SELECTION, getString(R.string.selection_fragment));
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
        fragmentTitles.put(SETTINGS, getString(R.string.logout_fragment));

        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
    }

    /**
     * initializes the navigation Drawer
     */
    private void initNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                getActionBar().setTitle(title);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getBaseContext(), R.layout.drawer_list_item, getResources()
                .getStringArray(R.array.menupoints));
        drawerList.setAdapter(adapter);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                drawerLayout.closeDrawer(drawerList);
            }

        };
        drawerList.setOnItemClickListener(listener);
        drawerToggle.setDrawerIndicatorEnabled(true);

        // getActionBar().setHomeButtonEnabled(true);
        // getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void selectItem(int position) {
        switch(position) {
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
            case 3:
                showFragment(SETTINGS, false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
                title = fragmentTitles.get(i);
                getActionBar().setTitle(title);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            // Get the number of entries in the back stack
            int backStackSize = manager.getBackStackEntryCount();
            // Clear the back stack
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            if (state.isOpened()) {
                // If the session state is open:
                // Show the authenticated fragment
                lockNavDrawer(false);
                showFragment(SELECTION, false);

            } else if (state.isClosed()) {
                // If the session state is closed:
                // Show the login fragment
                lockNavDrawer(true);
                showFragment(SPLASH, false);
            }
        }
    }

    /**
     * Locks the Navigation Drawer and makes the drawerToggle invisible
     * @param lock boolean, whether nav drawer should be locked or not
     */
    private void lockNavDrawer(boolean lock) {
        if (lock) {
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(false);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                drawerToggle.setDrawerIndicatorEnabled(false);
            }
        } else {
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                drawerToggle.setDrawerIndicatorEnabled(true);
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // if the session is already open,
            // try to show the selection fragment
            lockNavDrawer(false);
            showFragment(SELECTION, false);
        } else {
            // otherwise present the splash screen
            // and ask the person to login.
            lockNavDrawer(true);
            showFragment(SPLASH, false);
        }
    }

    @Override
    public void onBackPressed() {
        Session session = Session.getActiveSession();
        // if pressed back while facebook logout fragment is visible and already pressed logout,
        // the user should be navigated back to splash fragment.
        if (fragments[SETTINGS].isVisible() && (session == null || !session.isOpened())) {
            clearBackStack();
            lockNavDrawer(true);
            showFragment(SPLASH, false);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * completly clears the backstack
     */
    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        // Get the number of entries in the back stack
        int backStackSize = manager.getBackStackEntryCount();
        // Clear the back stack
        for (int i = 0; i < backStackSize; i++) {
            manager.popBackStack();
        }
    }

    // switch on the MainScreenActivity to test it without logging in
    public void switchMainScreen (View view) {
        Intent intent = new Intent(this, MainScreenActivity.class );
        startActivity(intent);
    }

    //switches to RegistryActivity
    public void switchRegistry (View view) {
        Intent intent = new Intent(this, RegistryActivity.class);
        startActivity(intent);
    }
}