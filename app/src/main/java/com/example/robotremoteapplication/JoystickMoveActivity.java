package com.example.robotremoteapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.HttpURLConnection;
import java.net.URL;

public class JoystickMoveActivity extends AppCompatActivity {
    private Joystick joystick;
    private Button switchModeButton;
    private TextView speedLevelTextView;
    private SeekBar speedSeekBar;
    private ImageButton flashlightImageButton;
    private PointF joystickPosition;

    private boolean lightOn=true;
    private int counter=0;
    private int seekbarProgress=1;
    private final String ARDUINO_IP="192.168.4.1";
    private float currentX=0;
    private float currentY=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_joystick_move);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        findViewsById();

        setOnClickListeners();

        //joystick.setOnMoveListener()
    }

    public void findViewsById() {
        joystick=findViewById(R.id.joystick);
        switchModeButton=findViewById(R.id.joystickSwitchModeButton);
        speedLevelTextView=findViewById(R.id.joystickSpeedTextView);
        speedSeekBar=findViewById(R.id.joystickSpeedSeekBar);
        flashlightImageButton=findViewById(R.id.joystickFlashLightImageButton);
    }
//    public void setOnJoystickMove()
//    {
//        currentX=joystick.getX();
//        currentY=joystick.getY();
//
//        joystick.setOnTouchListener((view, motionEvent) -> {
//
//            currentX=joystick.getX();
//            currentY=joystick.getY();
//            Log.d("banana","X "+currentX+"Y "+currentY);
//            return true;
//        });
//        //onJoystickMove();
//
//
//        Log.d("banana","X "+x+"Y "+y);
//    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnClickListeners() {

        switchModeButton.setOnClickListener(view -> {
            Intent regularModeIntent=new Intent(this,MainActivity.class);
            startActivity(regularModeIntent);
        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekbarProgress=progress;
                speedLevelTextView.setText("Speed Level Is: "+seekbarProgress);

                if(seekbarProgress==0)
                    speedLevelTextView.setTextColor(Color.RED);
                else speedLevelTextView.setTextColor(Color.BLACK);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendCommand(seekbarProgress+"");
            }
        });

        flashlightImageButton.setOnClickListener(view -> {
            handleFlashlightClick();
        });

//        joystick.setOnTouchListener((view, motionEvent) -> {
//            PointF joystickPosition=joystick.getJoystickPosition();
//            currentX= joystickPosition.x;
//            currentY= joystickPosition.y;
//            Log.d("banana","X "+currentX+" Y "+currentY);
//            return true;
//        });

        joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        counter++;
                        PointF currentLocation=joystick.getJoystickPosition();
                        float currentX = currentLocation.x;
                        float currentY = currentLocation.y;
                        if(counter==50) {
                            Log.d("banana", "X " + currentX + " Y " + currentY);
                            counter=0;
                        }
                        // Handle joystick movement or updates here
                        return true; // Consume the event
                    case MotionEvent.ACTION_UP:
                        // Reset or handle joystick release here if needed
                        return true; // Consume the event
                }
                return false;
            }
        });

    }

    public void handleFlashlightClick() {
        if(lightOn) {
            lightOn = false;
            sendCommand("w");
            flashlightImageButton.setBackgroundColor(Color.WHITE);
            flashlightImageButton.setImageResource(R.drawable.flashlight_off_drawable);
        }
        else{
            lightOn=true;
            sendCommand("W");
            flashlightImageButton.setBackgroundColor(Color.RED);
            flashlightImageButton.setImageResource(R.drawable.flash_light_on_drawable);
        }
    }
    public void sendCommand(String command) {

        Log.d("banana","The Current Move Is: "+command);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlString = "http://" + ARDUINO_IP + "/?State=" + command;
                    Log.d("banana","Url Is: "+urlString);
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK)
                        Log.d("banana", "Command sent successfully");


                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("banana", "Error sending command: " + e.getMessage());
                }
            }
        }).start();
    }
    public void sendJoyStickMoveCommand()
    {

    }


}