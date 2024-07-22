package com.example.robotremoteapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class Joystick extends View {

    private static final float DEFAULT_JOYSTICK_RADIUS = 200; // Default size of joystick
    private static final float DEFAULT_BUTTON_RADIUS = 100;

    private float centerX;
    private float centerY;
    private float joystickRadius;
    private float buttonRadius;

    private Paint paintJoystick;
    private Paint paintButton;

    private PointF joystickPosition;

    public Joystick(Context context) {
        super(context);
        init(null);
    }

    public Joystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Joystick(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PointF getJoystickPosition() {
        return joystickPosition;
    }

    private void init(AttributeSet attrs) {
        paintJoystick = new Paint();
        paintJoystick.setColor(Color.GRAY);
        paintJoystick.setStyle(Paint.Style.FILL);

        paintButton = new Paint();
        paintButton.setColor(Color.RED);
        paintButton.setStyle(Paint.Style.FILL);

        joystickPosition = new PointF(0, 0);

        if (attrs != null) {
            // Handle any custom attributes if needed
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        joystickRadius = DEFAULT_JOYSTICK_RADIUS;
        buttonRadius = DEFAULT_BUTTON_RADIUS;

        joystickPosition.set(centerX, centerY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw joystick base
        canvas.drawCircle(centerX, centerY, joystickRadius, paintJoystick);

        // Draw joystick button
        canvas.drawCircle(joystickPosition.x, joystickPosition.y, buttonRadius, paintButton);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                updateJoystickPosition(x, y);
                invalidate(); // Redraw the view
                return true;
            case MotionEvent.ACTION_UP:
                // Reset joystick to center
                resetJoystickPosition();
                invalidate(); // Redraw the view
                return true;
        }

        return super.onTouchEvent(event);
    }

    private void updateJoystickPosition(float x, float y) {
        // Limit the joystick button's position to within the joystick base
        float distance = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
        if (distance <= joystickRadius - buttonRadius) {
            joystickPosition.set(x, y);
        } else {
            float ratio = (joystickRadius - buttonRadius) / distance;
            float constrainedX = centerX + (x - centerX) * ratio;
            float constrainedY = centerY + (y - centerY) * ratio;
            joystickPosition.set(constrainedX, constrainedY);
        }
    }

    private void resetJoystickPosition() {
        joystickPosition.set(centerX, centerY);
    }
}
