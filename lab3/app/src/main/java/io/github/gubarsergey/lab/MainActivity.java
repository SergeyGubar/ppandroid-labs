package io.github.gubarsergey.lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private RecyclerView trackRecycler;
    private Button nextSongButton;
    private Button previousSongButton;
    private Button playButton;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 3334;
    private static final String TAG = "MainActivity";
    private PlayerState playerState = PlayerState.STOPPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        initViews();
        startService();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Loading files..");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            initTracks();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initTracks();
        }
    }

    private void initViews() {
        trackRecycler = findViewById(R.id.recycler);
        nextSongButton = findViewById(R.id.button_next);
        previousSongButton = findViewById(R.id.button_previous);
        playButton = findViewById(R.id.button_play);
        playButton.setOnClickListener(v -> {
            if (playerState == PlayerState.STOPPED) {
                setPlayerState(PlayerState.PLAYING);
                resumeTrack();
            } else {
                setPlayerState(PlayerState.STOPPED);
                pauseTrack();
            }
        });

    }

    private void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        switch (this.playerState) {
            case PLAYING:
                playButton.setText(R.string.stop);
                break;
            case STOPPED:
                playButton.setText(R.string.start);
                break;
        }
    }

    private void initTracks() {
        List<Track> tracks = new ArrayList<>();

        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/Music";
            Log.d(TAG, "Path: " + path);
            File directory = new File(path);
            File[] files = directory.listFiles();

            Objects.requireNonNull(files);

            Log.d(TAG, "Loading files..");
            for (File file : files) {
                Log.d(TAG, file.getName());
            }

            for (int i = 0; i < Math.min(files.length, 50); i++) {
                tracks.add(Track.fromFile(files[i].getAbsolutePath()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        trackRecycler.setLayoutManager(new LinearLayoutManager(this));
        trackRecycler.setAdapter(new TracksAdapter(tracks, track -> {
            setPlayerState(PlayerState.PLAYING);
            playTrack(track);
        }));
    }

    private void playTrack(Track track) {
        Log.d(TAG, "Play track " + track);
        startService(AudioService.makePlayTrackIntent(this, track.filepath));
    }

    private void pauseTrack() {
        Log.d(TAG, "Pause track");
        startService(AudioService.makePauseIntent(this));
    }

    private void resumeTrack() {
        Log.d(TAG, "Resume track");
        startService(AudioService.makeResumeIntent(this));
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }
}
