package co.company.android.activitycollector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button button1, button_email, button_deleteDatabase, button_stopService, button_startService;
    EditText text1;
    SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new SQLiteHelper(getApplicationContext());
        setContentView(R.layout.activity_main);
        button1 = (Button)findViewById(R.id.button1);
        button_email = (Button)findViewById(R.id.button2);
        button_deleteDatabase = (Button)findViewById(R.id.button_delDb);
        button_stopService = (Button)findViewById(R.id.button_stopService);
        button_startService = (Button)findViewById(R.id.button_startService);
        text1 = (EditText) findViewById(R.id.TextView1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked();
            }
        });
        button_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonEmailClicked();
            }
        });
        button_deleteDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {buttonDeleteDatabase();
            }
        });
        button_stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStopService();
            }
        });
        button_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartService();
            }
        });
    }

    void buttonStartService() {
        startService(new Intent(this, SensorService.class));
    }

    void buttonStopService() {
        stopService(new Intent(this, SensorService.class));
    }

    void buttonClicked(){
        text1.setText(dbHelper.getLastRow());
        text1.setMovementMethod(new ScrollingMovementMethod());
    }

    void buttonEmailClicked(){
        ArrayList<String> data = dbHelper.getAllRows();
        writeToFile(data, getApplicationContext());

        text1.append(" Data saved to device");
        text1.setMovementMethod(new ScrollingMovementMethod());
        //int counter = 0;
        //while (data.size() > 0) {
            //int len = Math.min(1000, data.size());
            //StringBuilder sb = new StringBuilder();
            //for (String s : data.subList(0, len))
            //{
            //    sb.append(s);
            //}
            //sendEmail(Integer.toString(counter), sb.toString());
            //data.subList(0, len).clear();
            //counter += 1;
        //}
    }

    void sendEmail(String part, String data){
        Intent i = new Intent(Intent.ACTION_SEND);

        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"email@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, part + " " +Build.MODEL);
        i.putExtra(Intent.EXTRA_TEXT   , data);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }

    final int REQUEST_WRITE_STORAGE = 112;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                } else
                {
                    Toast.makeText(MainActivity.this, "App not allowed to write file to storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void writeToFile(ArrayList<String> data, Context context) {

        boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }


        StringBuilder sb = new StringBuilder();
        for (String s : data)
        {
            sb.append(s);
        }
        String data_string = sb.toString();

        File file;
        FileOutputStream outputStream;
        try {
            text1.append(Environment.getExternalStorageDirectory().toString());
            file = new File(Environment.getExternalStorageDirectory(), "activityData"+Long.toString(System.currentTimeMillis())+".txt");

            outputStream = new FileOutputStream(file);
            outputStream.write(data_string.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("activityData"+Long.toString(System.currentTimeMillis())+".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data_string);
            outputStreamWriter.close();
            text1.append(MainActivity.this.getFilesDir().getAbsolutePath());
        }
        catch (IOException e) {
            Log.e("Exception", "Failed to write file: " + e.toString());
        }
    }

    void buttonDeleteDatabase(){
        this.deleteDatabase(SQLiteHelper.DATABASE_NAME);
        text1.append("Database deleted");
        text1.setMovementMethod(new ScrollingMovementMethod());
    }


}
