package rottiedog.com.mysport;

/**
 * Created by dehuffm on 6/15/2017.
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by dehuffm on 11/22/2016.
 */

public class splashPageFragment extends Fragment {
    private Handler handlePics = new Handler();
    private static int cnt=0;
    private static ImageView pics;
    private View view = null;
    private GestureDetectorCompat mDetector;
    private int rolodex;
    private int activeCnt = 0;
    private Sports[] mySports;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.splash_page, container, false);
        rolodex = 0;
        mySports = ((mySportActivity)getActivity()).getActiveSports();
        activeCnt = mySports.length;
        mDetector = new GestureDetectorCompat(getActivity(), new SportGestureListener());
        enableTouchListener();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("rolodex", rolodex);
    }


   @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pics = (ImageView) getView().findViewById(R.id.splash_image);
        if (savedInstanceState != null){rolodex = savedInstanceState.getInt("rolodex");}
    }

    class SportGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
             return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            tripList(mySports[rolodex].getId());
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
             } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
    public void onSwipeRight() {
        rolodex = (++rolodex%activeCnt);
        pics.setImageResource(mySports[rolodex].getPicture());
    }

    public void onSwipeLeft() {
        rolodex = (--rolodex%activeCnt);
        pics.setImageResource(mySports[rolodex].getPicture());
    }

    public void tripList(int number)
    {
        ListFragment newFragment = null;
        switch (number) {
            case R.id.kayaking:
                newFragment = new kayakListFragment();
                break;
            case R.id.skiing:
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
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }
    public void showSlide(int index, int sportId)
    {
        switch (sportId){
            case R.id.kayaking:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.skiing:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.hiking:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.biking:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.fishing:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.golfing:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.archery:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.trailriding:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.running:
                pics.setImageResource(mySports[index].getPicture());
                break;
            case R.id.rockclimbing:
                pics.setImageResource(mySports[index].getPicture());
                break;
            default :
                break;
        }
    }
     private void enableTouchListener(){
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                ListFragment newFragment = null;
                mDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        //handlePics.removeCallbacks(runPicsThread);
    }

    @Override
    public void onResume() {
        super.onResume();
        //need to restore rolodex from savedInstanceState, do this in onCreate?
        showSlide(rolodex, mySports[rolodex].getId()); // this won't work I think w/o storing mySports
        //tripList(rolodex);
    }
}

