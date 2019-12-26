package io.github.gubarsergey.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private Sensor lightSensor;
    private SensorManager lightSensorManager;
    private TextView lightTextView;
    private Button goToCompassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lightTextView = findViewById(R.id.light_text_view);
        goToCompassButton = findViewById(R.id.compass_button);
        goToCompassButton.setOnClickListener(v -> startActivity(new Intent(this, CompassActivity.class)));

        lightSensorManager = (SensorManager) getSystemService(
                Context.SENSOR_SERVICE);

        lightSensor = lightSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            lightTextView.setText("No sensor");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lightSensor != null) {
            lightSensorManager.registerListener(this, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        lightSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float currentValue = sensorEvent.values[0] - 90;
        lightTextView.setText("Light value (lux): " + currentValue);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}