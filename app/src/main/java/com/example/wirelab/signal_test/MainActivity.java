package com.example.wirelab.signal_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SignalStrength      signalStrength;
    private TelephonyManager    telephonyManager;
    private final static String LTE_TAG             = "LTE_Tag";
    private final static String LTE_SIGNAL_STRENGTH = "getLteSignalStrength";
    String db_now;
    int sec = 100;

    Timer timer = new Timer(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer.schedule(new MyTimerTask(), 1000, 1000);

        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                sec = 0;
            }
        });


        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // Listener for the signal strength.
        final PhoneStateListener mListener = new PhoneStateListener()
        {
            @Override
            public void onSignalStrengthsChanged(SignalStrength sStrength)
            {
                signalStrength = sStrength;
                String ssignal = signalStrength.toString();
                String[] parts = ssignal.split(" ");
                TextView db = (TextView) findViewById(R.id.db);
                db.setText(parts[9]);
                db_now = parts[9];
/*
                try{
                    FileWriter fw = new FileWriter("/storage/emulated/0/signal_data.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
                    EditText ue = (EditText)findViewById(R.id.ue);
                    EditText tx = (EditText)findViewById(R.id.tx);
                    bw.write(ue.getText().toString().trim() + "," + tx.getText().toString().trim()+","+parts[9]);
                    bw.newLine();
                    bw.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
*/
                getLTEsignalStrength();

            }
        };

        // Register the listener for the telephony manager
        telephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    private void getLTEsignalStrength()
    {
        try
        {
            Method[] methods = android.telephony.SignalStrength.class.getMethods();

            for (Method mthd : methods)
            {
                if (mthd.getName().equals(LTE_SIGNAL_STRENGTH))
                {
                    int LTEsignalStrength = (Integer) mthd.invoke(signalStrength, new Object[] {});
                    Log.i(LTE_TAG, "signalStrength = " + LTEsignalStrength);
                    return;
                }
            }
        }
        catch (Exception e)
        {
            Log.e(LTE_TAG, "Exception: " + e.toString());
        }
    }


    private void writeToFile(File fout, String data) {
        FileOutputStream osw = null;
        try {
            osw = new FileOutputStream(fout);
            osw.write(data.getBytes());
            osw.flush();
        } catch (Exception e) {
            ;
        } finally {
            try {
                osw.close();
            } catch (Exception e) {
                ;
            }
        }
    }

    public class MyTimerTask extends TimerTask
    {
        public void run()
        {

            try{
                sec++;
                if (sec < 60){

                    FileWriter fw = new FileWriter("/storage/emulated/0/signal_data.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw); //將BufferedWeiter與FileWrite物件做連結
                    EditText ue = (EditText)findViewById(R.id.ue);
                    EditText tx = (EditText)findViewById(R.id.tx);
                    //EditText pos = (EditText)findViewById(R.id.pos);
                    bw.write(ue.getText().toString().trim() + "," + tx.getText().toString().trim()+","+db_now);
                    bw.newLine();
                    bw.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }

        }
    };
}
