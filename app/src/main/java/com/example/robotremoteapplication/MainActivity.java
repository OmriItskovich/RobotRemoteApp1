package com.example.robotremoteapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.net.HttpURLConnection;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private final String ARDUINO_IP="192.168.4.1";  //dont forget to change when i get the arduino
    private boolean lightOn=true,forwardCombination=false;
    private int seekBarSpeedProgress=0;


    private ImageView forwardImageButton,backwardsImageButton,leftImageButton,rightImageButton,hornImageButton,flashLightImageButton;
    private SeekBar speedSeekBar;
    private TextView speedTextView;
    private Button switchModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        findViewsById();

        setSpeedSeekBar();

        setOnClickListeners();

    }

    private void setSpeedSeekBar() {
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBarSpeedProgress=progress;
                speedTextView.setText("Speed Level Is: "+seekBarSpeedProgress);

                if(seekBarSpeedProgress==0)
                    speedTextView.setTextColor(Color.RED);
                else speedTextView.setTextColor(Color.BLACK);
                Log.d("banana","Speed Progress Is: "+seekBarSpeedProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handleSpeed();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnClickListeners() {

        forwardImageButton.setOnTouchListener((view, motionEvent) -> {
            handleMovingTouch(motionEvent,"F");

//            rightImageButton.setOnTouchListener((view1, motionEvent1) -> {
//                forwardCombination=true;
//                handleMovingTouch(motionEvent1,"I");
//                return true;
//            });
//            forwardCombination=false;

            return true;
        });

        backwardsImageButton.setOnTouchListener((view, motionEvent) -> {
            handleMovingTouch(motionEvent,"B");
            return true;
        });

        rightImageButton.setOnTouchListener((view, motionEvent) -> {
            handleMovingTouch(motionEvent,"R");
            return true;
        });

        leftImageButton.setOnTouchListener((view, motionEvent) -> {
            handleMovingTouch(motionEvent,"L");
            return true;
        });

        flashLightImageButton.setOnClickListener(view -> {
            handleFlashLightClick();
        });

        hornImageButton.setOnTouchListener((view, motionEvent) -> {
            handleHornTouch(motionEvent);
            return true;
        });

        switchModeButton.setOnClickListener(view -> {
            Intent joystickActivityIntent=new Intent(this,JoystickMoveActivity.class);
            startActivity(joystickActivityIntent);
        });

//        hornImageButton.setOnTouchListener((view, motionEvent) -> {
//
//            return true;
//        });

    }

    public void findViewsById() {
        forwardImageButton=findViewById(R.id.forwardArrowImageButton);
        backwardsImageButton=findViewById(R.id.backwordsArrowImageButton);
        leftImageButton=findViewById(R.id.leftArrowImageButton);
        rightImageButton=findViewById(R.id.rightArrowImageButton);
        flashLightImageButton=findViewById(R.id.flashLightImageButton);
        hornImageButton=findViewById(R.id.hornImageButton);
        speedSeekBar=findViewById(R.id.speedSeekBar);
        speedTextView=findViewById(R.id.speedTextView);
        switchModeButton=findViewById(R.id.switchModeButton);
    }

    public void handleMovingTouch(MotionEvent event, String command) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sendCommand(command);
                break;
            case MotionEvent.ACTION_UP:
//                if(forwardCombination) {
//                    sendCommand("F");
//                    forwardCombination=false;
//                }
//                else
                    sendCommand("S"); // Stop command
                break;
        }
    }
    public void handleFlashLightClick(){
        if(lightOn) {
            lightOn = false;
            sendCommand("w");
            flashLightImageButton.setBackgroundColor(Color.WHITE);
            flashLightImageButton.setImageResource(R.drawable.flashlight_off_drawable);
        }
        else{
            lightOn=true;
            sendCommand("W");
            flashLightImageButton.setBackgroundColor(Color.RED);
            flashLightImageButton.setImageResource(R.drawable.flash_light_on_drawable);
        }
    }
    public void handleHornTouch(MotionEvent event)
    {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sendCommand("V");
                hornImageButton.setBackgroundColor(Color.RED);
                break;
//            case MotionEvent.ACTION_UP:
//                sendCommand("S"); // Stop command
//                break;
            case MotionEvent.ACTION_UP:
                hornImageButton.setBackgroundColor(Color.WHITE);
                break;
        }
    }
    public void handleSpeed()
    {
        sendCommand(seekBarSpeedProgress+"");
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
}