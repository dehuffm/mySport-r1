package rottiedog.com.mysport;

/**
 * Created by dehuffm on 11/22/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ORDERID = "OrderId";
    public static final String COLUMN_SPORT="Sport";
    public static final String COLUMN_TITLE="Title";
    public static final String COLUMN_DATE="Date";
    public static final String COLUMN_TIME="Time";
    public static final String COLUMN_LOCATION="Location";
    public static final String COLUMN_STARTAT="StartAt";
    public static final String COLUMN_SUMMARY="Summary";
    public static final String COLUMN_RESOURCE="Resource";
    public static final String COLUMN_SPORTPIC="SportPic";
    public static final String COLUMN_ACTIVE="Active";


    private static final String DB_NAME = "mySport.db";
    private static final int DB_VERSION = 1;
    private static DbHelper mySportTripsDb = null;
    private SQLiteDatabase db;
    public static final String DB_SPORTS_TABLE = "sports";
    private static final String CreateSportsTable = "create table "
            + DB_SPORTS_TABLE
            + "(ID integer primary key autoincrement, OrderId integer, Sport text, Resource int, SportPic int, Active integer DEFAULT 0); ";

    public static final String DB_SPORT_TRIPS_TABLE = "sportTrips";
    private static final String CreateSessionTable = "create table "
            + DB_SPORT_TRIPS_TABLE
            + "(ID integer primary key autoincrement, Sport text, Title text, Date text, Time text, Location text, StartAt text, Summary text); ";

    private DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        open();
    }

    public static DbHelper getInstance(Context context) {
        if (mySportTripsDb == null){
            mySportTripsDb = new DbHelper(context);
        }
        return mySportTripsDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateSportsTable);
        db.execSQL(CreateSessionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DB_SPORT_TRIPS_TABLE);
        db.execSQL("drop table if exists " + DB_SPORTS_TABLE);
        onCreate(db);
    }

    private void open() throws SQLiteException {
        db = getWritableDatabase();
    }
    public long addSport(int OrderId, String Sport, int Resource, int SportPic, int Active) {
        ContentValues values = new ContentValues();
        values.put("OrderId", OrderId);
        values.put("Sport", Sport);
        values.put("Resource", Resource);
        values.put("SportPic", SportPic);
        values.put("Active", Active);
        long rowId = db.insert(DB_SPORTS_TABLE, null, values);
        return rowId;
    }

    public Sports[] getActiveSports(){
        String[] asColmToRetrn = new String[] {COLUMN_ORDERID, COLUMN_SPORT, COLUMN_RESOURCE, COLUMN_SPORTPIC, COLUMN_ACTIVE};
        String whereClause = "Active = 1";
        Cursor cursor = db.query(DB_SPORTS_TABLE, asColmToRetrn, whereClause, null, null,
                null, null);
        Sports[] activeSports = new Sports[cursor.getCount()];
        int index = 0;
        if (cursor.moveToFirst()) {
            while (true) {
                Sports sport = new Sports(cursor.getInt(cursor.getColumnIndex("OrderId")),
                        cursor.getString(cursor.getColumnIndex("Sport")),
                        cursor.getInt(cursor.getColumnIndex("Resource")),
                        cursor.getInt(cursor.getColumnIndex("SportPic")),
                        (cursor.getInt(cursor.getColumnIndex("Active"))==1));
                activeSports[index] = sport;
                ++index;
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();
        return activeSports;
    }

    public Sports[] getSports(){
        String[] asColmToRetrn = new String[] {COLUMN_ORDERID, COLUMN_SPORT, COLUMN_RESOURCE, COLUMN_SPORTPIC, COLUMN_ACTIVE};
        Cursor cursor = db.query(DB_SPORTS_TABLE, asColmToRetrn, null, null, null,
                null, null);
        Sports[] sports = new Sports[cursor.getCount()];
        int index = 0;

        if (cursor.moveToFirst()) {
            while (true) {
                Sports sport = new Sports(cursor.getInt(cursor.getColumnIndex("OrderId")),
                        cursor.getString(cursor.getColumnIndex("Sport")),
                        cursor.getInt(cursor.getColumnIndex("Resource")),
                        cursor.getInt(cursor.getColumnIndex("SportPic")),
                        (cursor.getInt(cursor.getColumnIndex("Active"))==1));
                sports[index] = sport;
                ++index;
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();
        return sports;
    }

    public int updateSport(int OrderId, String Sport, int Resource, int Active){
        String whereClause = "OrderId = " + OrderId;
        ContentValues values = new ContentValues();
        values.put("Sport", Sport);
        values.put("Resource", Resource);
        values.put("SportPic", Resource);
        values.put("Active", Active);
        return db.update(DB_SPORTS_TABLE, values, whereClause, null);
    }

    public int updateSportStatus(int OrderId, int Active){
        String whereClause = "OrderId = " + OrderId;
        ContentValues values = new ContentValues();
        values.put("Active", Active);
        return db.update(DB_SPORTS_TABLE, values, whereClause, null);
    }

    public void deleteSports() {

        db.delete(DB_SPORTS_TABLE, null, null);
    }

    public int updateTrip(int db_key, String sport, String title, String date, String time, String location, String startAt, String summary)
    {
        String whereClause = "ID = " + db_key;
        ContentValues values = new ContentValues();
        values.put("Sport", sport);
        values.put("Title", title);
        values.put("Date", date);
        values.put("Time", time);
        values.put("Location", location);
        values.put("StartAt", startAt);
        values.put("Summary", summary);
        return db.update(DB_SPORT_TRIPS_TABLE, values, whereClause, null);
    }

    public long addTrip(String sport, String title, String date, String time, String location, String startAt, String summary) {
        ContentValues values = new ContentValues();
        values.put("Sport", sport);
        values.put("Title", title);
        values.put("Date", date);
        values.put("Time", time);
        values.put("Location", location);
        values.put("StartAt", startAt);
        values.put("Summary", summary);

        long rowId = db.insert(DB_SPORT_TRIPS_TABLE, null, values);
        return rowId;
    }

    public DbTrip getTrip(int db_key) {
        String[] asColmToRetrn = new String[] {COLUMN_SPORT, COLUMN_TITLE, COLUMN_DATE, COLUMN_TIME, COLUMN_LOCATION, COLUMN_STARTAT, COLUMN_SUMMARY};
        String whereClause = "ID = " + db_key;
        DbTrip trip = new DbTrip();
        Cursor cursor = db.query(DB_SPORT_TRIPS_TABLE, asColmToRetrn, whereClause, null, null,
                null, null);

        if (cursor.moveToFirst()) {
            while (true) {
                trip.setSport(cursor.getString(cursor.getColumnIndex("Sport")));
                trip.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                trip.setDate(cursor.getString(cursor.getColumnIndex("Date")));
                trip.setTime(cursor.getString(cursor.getColumnIndex("Time")));
                trip.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
                trip.setStartAt(cursor.getString(cursor.getColumnIndex("StartAt")));
                trip.setSummary(cursor.getString(cursor.getColumnIndex("Summary")));
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();
        return trip;
    }

    public DbTrip[] getTitles(String sport){
        String[] asColmToRetrn = new String[] {COLUMN_ID, COLUMN_SPORT, COLUMN_TITLE};
        //String whereClause = "Sport=?" + "'" +sport + "'";
        Cursor cursor = db.query(DB_SPORT_TRIPS_TABLE, asColmToRetrn, "Sport=?",  new String[] {sport}, null,
                null, null);
        DbTrip[] titles = new DbTrip[cursor.getCount()];
        int index = 0;

        if (cursor.moveToFirst()) {
            while (true) {
                DbTrip title = new DbTrip();
                title.setId(cursor.getLong(cursor.getColumnIndex("ID")));
                title.setSport(cursor.getString(cursor.getColumnIndex("Sport")));
                title.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                titles[index] = title;
                ++index;
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();
        return titles;
    }


    /*public void updateTask(long id, String name, String description)
    {
        ContentValues values = new ContentValues();

        values.put("Name", name);
        values.put("Description", description);
        db.update(DB_TASKS_TABLE, values, "ID=" + id, null);
    }
    SELECT FROM table_name
WHERE column LIKE '%XXXX%'
*/
    public void deleteTrip(long id) {
        db.delete(DB_SPORT_TRIPS_TABLE, "ID=" + id, null);
    }

    public void deleteTrips() {

        db.delete(DB_SPORT_TRIPS_TABLE, null, null);
    }
    public DbTrip[] searchTitles(String query){
        String[] asColmToRetrn = new String[] {COLUMN_ID, COLUMN_SPORT, COLUMN_TITLE};
        Cursor cursor = db.query(true, DB_SPORT_TRIPS_TABLE, asColmToRetrn, COLUMN_TITLE + " LIKE ?",
                new String[] {"%" + query + "%"}, null, null, null, null);
        int cnt = cursor.getCount();
        DbTrip[] titles = new DbTrip[cursor.getCount()];
        int index = 0;

        if (cursor.moveToFirst()) {
            while (true) {
                DbTrip title = new DbTrip();
                title.setId(cursor.getLong(cursor.getColumnIndex("ID")));
                title.setSport(cursor.getString(cursor.getColumnIndex("Sport")));
                title.setTitle(cursor.getString(cursor.getColumnIndex("Title")));
                titles[index] = title;
                ++index;
                if (!cursor.moveToNext()) {
                    break;
                }
            }
        }
        cursor.close();
        return titles;
    }
}
