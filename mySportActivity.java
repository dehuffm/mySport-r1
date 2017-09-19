package rottiedog.com.mysport;

import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Date;

import static android.graphics.Color.WHITE;

public class mySportActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private FragmentManager fm = getFragmentManager();
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    CallbackManager mCallbackManager; //facebook callback manager
    protected ShareDialog shareDialog; //facebook dialog
    NavigationView navigationView = null;
    private Sports[] sports = null;
    boolean splashActive = false;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
        }
        else{
            splashActive = true;
        }
        setContentView(R.layout.activity_my_sport);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
         };

        drawer.setDrawerListener(toggle);
        toggle.syncState();
//
        FragmentManager fm = getFragmentManager();
        int fragCnt = fm.getBackStackEntryCount();
        //fm.findFragmentByTag("foo");
        //if (mDataFragment == null)
//
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //facebook
        mCallbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        //end of facebook
        createLocationRequest();
        loadDb();
        loadSports();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } catch (SecurityException e) {
                PostUserMessage("GPS is not available");// lets the user know there is a problem with the gps
            }
            if (mCurrentLocation == null) {
                PostUserMessage("mCurrentLocation is null!!!");
            } else {
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        PostUserMessage("Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart()
    {
        DbHelper db = DbHelper.getInstance(this);
        mGoogleApiClient.connect();
        sports = db.getSports();
        if(splashActive){showSplashPage();}
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_sport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_weather_report) {
            if (mCurrentLocation != null) {
                Intent intent = new Intent(this, WeatherReport.class);
                intent.putExtra("latitude", mCurrentLocation.getLatitude());
                intent.putExtra("longitude", mCurrentLocation.getLongitude());
                startActivity(intent);
            }
            else{
                PostUserMessage("Location not known, check that GPS is enabled");
            }
        }
        if (id == R.id.action_where_am_i) {
            if (mCurrentLocation != null){
                String latLong = "Lat: "+mCurrentLocation.getLatitude()+" Long: "+ mCurrentLocation.getLongitude();
                final Snackbar location = Snackbar.make(findViewById(R.id.drawer_layout), latLong, Snackbar.LENGTH_INDEFINITE);
                location.setActionTextColor(WHITE);
                location.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        location.dismiss();                            }
                });
                location.show();}
            else {PostUserMessage("Location not known, check that GPS is enabled");}
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ListFragment newFragment = null;
        switch (id) {
            case R.id.kayaking:
                // Create new fragment and transaction
                newFragment = new kayakListFragment();
                break;
            case R.id.skiing:
                // Create new fragment and transaction
                newFragment = new skiListFragment();
                break;
            case R.id.hiking:
                newFragment = new hikeListFragment();
                break;
            case R.id.biking:
                newFragment = new bikeListFragment();
                break;
            case R.id.fishing:
                newFragment = new genericListFragment();
                break;
            case R.id.golfing:
                newFragment = new genericListFragment();
                break;
            case R.id.archery:
                newFragment = new genericListFragment();
                break;
            case R.id.trailriding:
                newFragment = new genericListFragment();
                break;
            case R.id.running:
                newFragment = new genericListFragment();
                break;
            case R.id.rockclimbing:
                newFragment = new genericListFragment();
                break;
            case R.id.manage_pics:
                Intent imageIntent = new Intent(this, ManageImages.class);
                startActivityForResult(imageIntent, 101);
                break;
            case R.id.facebook:
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("Post from App to Facebook")
                            .setContentDescription("This is a post from myApp")
                            .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                            .build();
                    shareDialog.show(linkContent);
                }
                break;
            case R.id.manage_sports:
                //navigationView.getMenu().removeGroup(R.id.group_sports);
                //createSportChoice(706, 10, "Rock-climbing",R.drawable.rock_climbing_24);
                Intent intent = new Intent(this, ManageSports.class);
                startActivityForResult(intent, 1);
                break;
        }
        if (newFragment != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void PostUserMessage(String msg){
        Snackbar fb = Snackbar.make(findViewById(R.id.drawer_layout), msg, Snackbar.LENGTH_LONG);
        fb.show();
    }

    private void showSplashPage()
    {
        splashPageFragment newFragment = new splashPageFragment();
        FragmentTransaction transaction = fm.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
    private void loadDb(){
        Context globalContext = this;
        DbHelper db = DbHelper.getInstance(globalContext);
        db.deleteTrips();
        db.addTrip("Kayaking", "Ocean trip", "6/30/2016", "11:29am", "Ocean Shores, WA",
                "Boat launch is wide-open sand beach", "The weather was 70 degrees, perfect weather for kayaking.  We saw many bird species, including an American Bittern.\n" +
                        "Watch for seals and pelicans");
        db.addTrip("Kayaking", "Lake Meridan", "9/21/2016", "1:45pm", "Lake Meridan, Kent, WA",
                "Boat launch with sand/pebble surface must be shared with power boats", "The weather was 58 degrees and overcast.  We tried fishing at the lilypads on the East end.\n" +
                        "Watch for power boats");
        db.addTrip("Kayaking", "Hood Canal", "8/1/2016", "10:00am", "Twanoh State Park, Union, WA",
                "Boat launch is off of surface level dock", "The weather was 78 degrees, perfect weather for kayaking but sunscreen was a must.  We saw many bird species, including several Bald Eagles.\n" +
                        "Found oysters at low tide!");
        db.addTrip("Hiking", "Hancock Forest Management Tree Farm", "6/13/2016", "11:29am", "White River, WA",
                "Gate 18, Hwy 410", "The weather was 73 degrees, perfect weather for hiking.  We saw many bird species, including a King Fisher.\n" +
                        "Water quality is good on the feeder streams.");
        db.addTrip("Biking", "Sammamish Slough Trail", "10/22/2016", "1:29pm", "Marymoor Park, Redmond, WA",
                "Enter slough side of soccer field", "The weather was 63 degrees, with light overcast.  We went all the way to Woodinville.\n" +
                        "We had hamburgers at Molbak's cafe and they have a great burger!");
        db.addTrip("Skiing", "Snoqualmie Pass Skiing", "2/30/2016", "8:29am", "Summit Central, Snoqualmie, WA",
                "Silver Fir Ski Lift", "The temp was 32 degrees, Swix LF8 Red was effective wax for the humidity.  Lift lines were very light.\n" +
                        "Powder on treeline between Silver Fir and Central Express");
        db.addTrip("Generic", "Coming Soon", "2/30/2016", "8:29am", "Summit Central, Snoqualmie, WA",
                "Silver Fir Ski Lift", "The temp was 32 degrees, Swix LF8 Red was effective wax for the humidity.  Lift lines were very light.\n" +
                        "Powder on treeline between Silver Fir and Central Express");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean state = super.onPrepareOptionsMenu(menu);
        initNavDrawList();
        return state;
    }

    protected void initNavDrawList(){
        Context globalContext = this;
        DbHelper db = DbHelper.getInstance(globalContext);
        navigationView.getMenu().removeGroup(R.id.group_sports);
        sports = db.getSports();
        for (int i=0; i<sports.length;++i ){
            if (sports[i].isSelected()) {
                //int id = R.drawable.image1
                createSportChoice( sports[i].getId(), Menu.NONE, sports[i].getName(),sports[i].getIcon());
            }
        }
    }

    protected void createSportChoice(int id, int order, String title, int iconRes){

        // Check the menu item already added or not

        if (navigationView.getMenu().findItem(id) == null) {
            // If it not exists then add the menu item to menu
            MenuItem sportItem = navigationView.getMenu().add(
                    R.id.group_sports, // groupId
                    id, // itemId
                    order, // order
                    title // title
            );
            sportItem.setVisible(true);
            sportItem.setIcon(iconRes);
            //fishing.setIcon(R.drawable.fishingboat_24);
            sportItem.setShowAsActionFlags(
                    MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

    }
    private void loadSports(){
        Context globalContext = this;
        DbHelper db = DbHelper.getInstance(globalContext);
        db.deleteSports();

        db.addSport(R.id.kayaking, "Kayaking", R.drawable.kayak_24,R.drawable.kayak2, 1);
        db.addSport(R.id.skiing, "Skiing", R.drawable.skiing_24, R.drawable.carrie_ski, 1);
        db.addSport(R.id.hiking, "Hiking", R.drawable.hiking_24, R.drawable.hike2, 1);
        db.addSport(R.id.biking, "Biking", R.drawable.cycling_road_24, R.drawable.bike, 1);
        db.addSport(R.id.fishing, "Fishing", R.drawable.fish_24, R.drawable.fishing, 1);
        db.addSport(R.id.golfing, "Golfing", R.drawable.golf_24, R.drawable.golf, 1);
        db.addSport(R.id.archery, "Archery", R.drawable.archers_arrow_24, R.drawable.archery, 1);
        db.addSport(R.id.trailriding, "Trail-riding", R.drawable.horseback_riding_24, R.drawable.trailriding, 1);
        db.addSport(R.id.running, "Running", R.drawable.running_24,  R.drawable.running, 1);
        db.addSport(R.id.rockclimbing, "Rock-climbing", R.drawable.rock_climbing_24, R.drawable.rockclimbing, 1);
    }
    public Sports[] getActiveSports()
    {
        Context globalContext = this;
        DbHelper db = DbHelper.getInstance(globalContext);

        return db.getActiveSports();
    }
    //following method for facebook interaction
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        else {
            //we are calling Facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}// end brace of mySportActivity
