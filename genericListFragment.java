package rottiedog.com.mysport;

/**
 * Created by dehuffm on 2/25/2017.
 */

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

public class genericListFragment extends ListFragment implements OnItemClickListener {
    private Context globalContext = null;
    DbHelper db = null;
    private DbTrip[] titles;
    Button button;
    View view = null;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.genericlist_fragment, container, false);
        setRetainInstance(true); //added for rotate
        // test for listview
        globalContext = this.getActivity();
        db = DbHelper.getInstance(globalContext);
        // end test
        addCreateListener();
        addHomeListener();
        return view;
    }

    public void addCreateListener(){
        button = (Button) view.findViewById(R.id.createTrip);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Bundle key_data = new Bundle();
                key_data.putBoolean("new_trip", true);
                genericTripFragment newFragment = new genericTripFragment();
                newFragment.setArguments(key_data);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }

        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titles = db.getTitles("Generic");
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, titles);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        hikeTripFragment newFragment = new hikeTripFragment();
        DbTrip  itemValue  = ((DbTrip) getListAdapter().getItem(position));

        Bundle key_data = new Bundle();
        key_data.putBoolean("new_trip", false);
        key_data.putInt("db_key", (int)itemValue.getId());
        key_data.putString("trip", itemValue.getTitle());
        newFragment.setArguments(key_data);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
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
}
