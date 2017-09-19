package rottiedog.com.mysport;

/**
 * Created by dehuffm on 3/2/2017.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by dehuffm on 11/22/2016.
 * http://stackoverflow.com/questions/9884202/custom-circle-button
 */

public class hikeTripFragment extends Fragment {
    private Context globalContext = null;
    private DbHelper db = null;
    private View view = null;
    private boolean newTrip = true;
    private Bundle key_data = null;
    private boolean affirm = false;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.hike_trip, container, false);
        key_data = this.getArguments();
        globalContext = this.getActivity();
        db = DbHelper.getInstance(globalContext);

        addSaveListener();
        addCancelListener();
        addDeleteListener();
        addHomeListener();

        newTrip = key_data.getBoolean("new_trip", true);

        if (newTrip) {
            createTrip();
        }
        else {
            retrieveTrip(key_data.getInt("db_key", -1));
        }

        /*
        Button buttonOne = (Button) view.findViewById(R.id.trip_save);
        buttonOne.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "starting camera", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), myCamera.class);
                startActivity(intent);
            }
        });
        */
        return view;
    }
    public void addSaveListener(){
        Button button = (Button) view.findViewById(R.id.trip_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((mySportActivity)getActivity())
                        .PostUserMessage("Saved " + ((EditText) view.findViewById(R.id.trip_title)).getText().toString());
                saveTripEdits();
            }

        });

    }
    public void saveTripEdits(){
        if (newTrip){
            db.addTrip("Hiking",
                    ((EditText) view.findViewById(R.id.trip_title)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_date)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_time)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_location)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_trailhead)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_summary)).getText().toString());
        } else {
            db.updateTrip(key_data.getInt("db_key", -1),
                    "Hiking",
                    ((EditText) view.findViewById(R.id.trip_title)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_date)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_time)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_location)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_trailhead)).getText().toString(),
                    ((EditText) view.findViewById(R.id.trip_summary)).getText().toString());
        }
    }
    public void addCancelListener(){
        Button button = (Button) view.findViewById(R.id.trip_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack ();
            }

        });

    }
    public void addDeleteListener(){
        Button button = (Button) view.findViewById(R.id.trip_delete);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AffirmDelete();
            }

        });

    }
    public void addHomeListener(){
        Button button = (Button) view.findViewById(R.id.trip_home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack(fm.getBackStackEntryAt(0).getId(), 0);
            }

        });

    }
    public void createTrip(){
        ((EditText)view.findViewById(R.id.trip_location)).setText("");
        ((EditText)view.findViewById(R.id.trip_trailhead)).setText("");
        ((EditText)view.findViewById(R.id.trip_summary)).setText("");
        ((EditText)view.findViewById(R.id.trip_title)).setText("");
        ((EditText)view.findViewById(R.id.trip_date)).setText("");
        ((EditText)view.findViewById(R.id.trip_time)).setText("");
    }
    public void retrieveTrip(int key){
        DbTrip tripData = db.getTrip(key);

        String location = tripData.getLocation();
        String trailhead = tripData.getStartAt();
        String summary = tripData.getSummary();
        String title = tripData.getTitle();
        String date = tripData.getDate();
        String time = tripData.getTime();
        ((EditText)view.findViewById(R.id.trip_location)).setText(location);
        ((EditText)view.findViewById(R.id.trip_trailhead)).setText(trailhead);
        ((EditText)view.findViewById(R.id.trip_summary)).setText(summary);
        ((EditText)view.findViewById(R.id.trip_title)).setText(title);
        ((EditText)view.findViewById(R.id.trip_date)).setText(date);
        ((EditText)view.findViewById(R.id.trip_time)).setText(time);
    }
    //   public void startCamera(View v){
    //       Toast.makeText(getActivity(), "starting camera", Toast.LENGTH_LONG).show();
    //   }
    //public boolean AffirmDelete() {
    public void AffirmDelete() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Delete will permanately remove the trip log, do you wish to continue?")
                .setTitle("Delete Trip Log");

        // 3. Add the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                db.deleteTrip(key_data.getInt("db_key", -1));
                ((mySportActivity)getActivity())
                        .PostUserMessage("Successfully deleted " + ((EditText) view.findViewById(R.id.trip_title)).getText().toString());
                FragmentManager fm = getFragmentManager();
                fm.popBackStack ();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        // 4. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();

    }
}

