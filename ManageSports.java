package rottiedog.com.mysport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

public class ManageSports extends AppCompatActivity {
    MyCustomAdapter dataAdapter = null;
    private Context globalContext = null;
    private DbHelper db = null;
    Sports[] sports = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sports);
        globalContext = this;
        db = DbHelper.getInstance(globalContext);
        //Generate list View from ArrayList
        displayListView();
        checkButtonClick();
    }

    private void displayListView() {
         //Array list of countries
        ArrayList<Sports> sportsList = new ArrayList<Sports>();
        sports = db.getSports();
        for (int i=0; i<sports.length;++i ){
            sportsList.add(sports[i]);
        }
        //create an ArrayAdapter from Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.sportsinfo, sportsList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Sports sport = (Sports) parent.getItemAtPosition(position);
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Sports> {

        private ArrayList<Sports> sportsList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Sports> sportsList) {
            super(context, textViewResourceId, sportsList);
            this.sportsList = new ArrayList<Sports>();
            this.sportsList.addAll(sportsList);
        }

        private class ViewHolder {
            //TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.sportsinfo, null);

                holder = new ViewHolder();
                //holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Sports sport = (Sports) cb.getTag();
                        sport.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Sports sport = sportsList.get(position);
            //holder.code.setText(" (" +  sport.getId() + ")");
            holder.name.setText(sport.getName());
            holder.name.setChecked(sport.isSelected());
            holder.name.setTag(sport);

            return convertView;

        }

    }

    private void checkButtonClick() {


        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent returnIntent = null;
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");

                ArrayList<Sports> sportsList = dataAdapter.sportsList;
                for(int i=0;i<sportsList.size();i++){

                    Sports sport = sportsList.get(i);
                    if(sport.isSelected()){
                        db.updateSportStatus(sport.getId(),1);
                     }
                    else {
                        db.updateSportStatus(sport.getId(),0);
                    }
                }
                returnIntent = new Intent();
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }

}
