package co.company.android.activitycollector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {
    SQLiteHelper dbHelper;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer, sensorGyroscope;

    @Override
    public void onCreate(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        dbHelper = new SQLiteHelper(getApplicationContext());
        return START_STICKY;
    }

    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        //super.onDestroy();
        //this.stopSelf();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor theSensor = event.sensor;

        if (theSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            Log.e("Tag",Float.toString(x));

            long time = System.currentTimeMillis();

            dbHelper.insert(time, "accel_x", Float.toString(x));
            dbHelper.insert(time, "accel_y", Float.toString(y));
            dbHelper.insert(time, "accel_z", Float.toString(z));
        }
        if (theSensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long time = System.currentTimeMillis();

            dbHelper.insert(time, "gyro_x", Float.toString(x));
            dbHelper.insert(time, "gyro_y", Float.toString(y));
            dbHelper.insert(time, "gyro_z", Float.toString(z));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
