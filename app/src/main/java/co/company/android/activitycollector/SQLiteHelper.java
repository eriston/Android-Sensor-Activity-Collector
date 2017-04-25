package co.company.android.activitycollector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SQLiteDatabase.db";
    private SQLiteDatabase database;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String TABLE_NAME = "SensorValues";
    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TIMESTAMP = "Timestamp";
    public static final String COLUMN_SENSOR = "Sensor";
    public static final String COLUMN_VALUE = "Value";

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TIMESTAMP + " VARCHAR , " + COLUMN_SENSOR + " LONG, " + COLUMN_VALUE + " VARCHAR);");
    }

    private static SQLiteHelper instance;

    public static SQLiteHelper getInstance(Context context){
        if ( instance == null ){
            instance = new SQLiteHelper(context);
        }
        return instance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insert(long time, String sensor, String value){
        database = this.getReadableDatabase();
        database.execSQL("INSERT INTO SensorValues(Timestamp, Sensor, Value) VALUES ("+time+", '"+sensor+"', '"+value+"');");
        database.close();
    }

    public String getLastRow() {
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM SensorValues WHERE ID=(SELECT MAX(Id) FROM SensorValues);", null);


        String ret = "->";
        if(cursor.moveToFirst()){
            ret = cursor.getInt(0)+"|"+Long.toString(cursor.getLong(1))+"|"+cursor.getString(2)+"|"+cursor.getString(3)+"\n";
        }
        database.close();
        return ret;
    }

    public ArrayList<String> getAllRows() {
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM SensorValues;", null);

        ArrayList<String> ret = new ArrayList<String>();
        if(cursor.moveToFirst()){
            do {
                String line = cursor.getInt(0) + "|" + Long.toString(cursor.getLong(1)) + "|" + cursor.getString(2) + "|" + cursor.getString(3) + "\n";
                ret.add(line);
            } while (cursor.moveToNext());
        }
        database.close();
        return ret;
    }
}