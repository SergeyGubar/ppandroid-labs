package io.github.gubarsergey.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView image;

    private float currentDegree = 0f;

    TextView topText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        image = findViewById(R.id.imageViewCompass);
        topText = findViewById(R.id.tvHeading);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float degree = Math.round(event.values[0]);

        // Don't ask
//        degree -= 113;

        if (degree > 0) {
            topText.setText("Heading: " + degree + " degrees");
        } else {
            topText.setText("Heading: " + (360 + degree) + " degrees");
        }

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        image.startAnimation(ra);
        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
