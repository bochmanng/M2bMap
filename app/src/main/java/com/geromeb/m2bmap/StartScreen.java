package com.geromeb.m2bmap;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class StartScreen extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng berlin = new LatLng(52.5167, 13.3833);
    private LatLng munich = new LatLng(48.1333, 11.5667);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This method creates two markers on startup and realizes the camera animation to the Berlin marker.
     * Finally, it instantiates a listener for marker clicks.
     * */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(berlin));
        mMap.addMarker(new MarkerOptions().position(munich));
        moveToNewLocation(berlin);

        mMap.setOnMarkerClickListener(
        // Create onClickListener as anonymous class
            new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    if (marker.getPosition().equals(berlin)) {
                        countToLocation(munich);
                        return true;
                    } else if (marker.getPosition().equals(munich)) {
                        countToLocation(berlin);
                        return true;
                    }

                    marker.setTitle("Something went wrong.");

                    return false;
                }
            }
        );


    }

    /**
     * This method mediates the camera movement. It's just a shortcut.
     * */


    private void moveToNewLocation(LatLng newLocation) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 10), 5000, null);

    }

    /**
     * This method implements the counter and the subsequent camera animation.
     * */

    private void countToLocation(final LatLng newLocation) {

        // Access the TextView we will use to represent the counter. Set the counter to 6, and make the TextView visible.
        final TextView cdText = (TextView) findViewById(R.id.cdText);
        final int[] t = {6};
        cdText.setVisibility(View.VISIBLE);


        // This handler makes sure the countdown is displayed smoothly.
        final Handler mHandler = new Handler(Looper.getMainLooper());
        // This runnable is instantiated as an anonymous class. It calls its run method every 1000ms.
        // The run method counts backwards from 6 to 0 and sends the current number to cdText.
        Runnable mUpdateTimeTask = new Runnable() {
            @Override
            public void run() {

                t[0] -= 1;
                cdText.setText(Integer.toString(t[0]) + "...");

                if (t[0] == 0) {
                    cdText.setVisibility(View.INVISIBLE);
                    moveToNewLocation(newLocation);
                    // Make sure there are no callbacks to mUpdateTimeTask left.
                    mHandler.removeCallbacks(this);
                } else {
                    mHandler.postDelayed(this, 1000);
                }
            }
        };

        // Make sure there are no callbacks to mUpdateTimeTask left and then start the countdown.
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.post(mUpdateTimeTask);


        // This variant of the countdown works but sometimes numbers are skipped.
/*        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                cdText.setText(Long.toString(millisUntilFinished / 1000) + "...");
            }

            @Override
            public void onFinish() {
                cdText.setText("");
                moveToNewLocation(newLocation);
            }
        }.start();*/
    }
}
