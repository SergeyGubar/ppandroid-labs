package io.github.gubarsergey.lab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 3334;
    private static final String TAG = "MainActivity";

    private RecyclerView trackRecycler;
    private Button nextSongButton;
    private Button previousSongButton;
    private Button playButton;
    private CheckBox isShuffleCheckbox;
    private CheckBox isRepeatCheckbox;
    private TextView timeTextView;
    private SeekBar progressSeekbar;
    private TextView currentTrackTextView;
    private Track currentTrack;

    private PlayerState playerState = PlayerState.STOPPED;

    private BroadcastReceiver songUpdateReceiver;
    private BroadcastReceiver songTimeUpdateReceiver;
    private BroadcastReceiver songStatusUpdateReceiver;

    public static final String ACTION_SONG_UPDATED = "ACTION_SONG_NAME_UPDATED";
    public static final String ACTION_SONG_UPDATED_EXTRA = "ACTION_SONG_UPDATED_EXTRA";
    public static final String ACTION_SONG_TIME_UPDATED = "ACTION_SONG_TIME_UPDATED";
    public static final String ACTION_SONG_TIME_UPDATED_EXTRA = "ACTION_SONG_TIME_UPDATED_EXTRA";
    public static final String ACTION_SONG_STATUS_UPDATED = "ACTION_SONG_STATUS_UPDATED";
    public static final String ACTION_SONG_STATUS_UPDATED_EXTRA = "ACTION_SONG_STATUS_UPDATED_EXTRA";

    public static final String SONG_INFO_EXTRA_TRACK = "SONG_INFO_EXTRA_TRACK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceivers();
        initViews();
        startAudioService();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Loading files..");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            initTracks();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentTrack != null) {
            outState.putParcelable(SONG_INFO_EXTRA_TRACK, currentTrack);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Track track = savedInstanceState.getParcelable(SONG_INFO_EXTRA_TRACK);
        if (track != null) {
            this.currentTrack = track;
            setPlayerState(PlayerState.PLAYING);
            progressSeekbar.setMax(track.totalTime);
            currentTrackTextView.setText(track.name + " by " + track.author);
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
        progressSeekbar = findViewById(R.id.progress_seekbar);
        currentTrackTextView = findViewById(R.id.current_text_view);
        timeTextView = findViewById(R.id.song_time_text_view);
        isRepeatCheckbox = findViewById(R.id.repeat_checkbox);
        isShuffleCheckbox = findViewById(R.id.shuffle_checkbox);

        isRepeatCheckbox.setOnCheckedChangeListener((compoundButton, b) -> startService(AudioService.makeRepeatChangedIntent(this, b)));
        isShuffleCheckbox.setOnCheckedChangeListener((compoundButton, b) -> startService(AudioService.makeShuffleChangedIntent(this, b)));

        playButton.setOnClickListener(v -> {
            if (currentTrack == null) return;
            if (playerState == PlayerState.STOPPED) {
                resumeTrack();
            } else {
                pauseTrack();
            }
        });

        nextSongButton.setOnClickListener(v -> {
            setPlayerState(PlayerState.PLAYING);
            playNextTrack();
        });

        previousSongButton.setOnClickListener(v -> {
            setPlayerState(PlayerState.PLAYING);
            playPreviousTrack();
        });

        progressSeekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            startService(AudioService.makeProgressChangedIntent(MainActivity.this, progress));
                            progressSeekbar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }

    private void registerReceivers() {
        IntentFilter songUpdateFilter = new IntentFilter();
        songUpdateFilter.addAction(ACTION_SONG_UPDATED);
        songUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Track track = intent.getParcelableExtra(ACTION_SONG_UPDATED_EXTRA);
                MainActivity.this.currentTrack = track;
                Log.d(TAG, "onReceive: song update " + track);
                currentTrackTextView.setText(track.name + " by " + track.author);
                progressSeekbar.setProgress(0);
                progressSeekbar.setMax(track.totalTime);
            }
        };

        IntentFilter songTimeUpdateFilter = new IntentFilter();
        songTimeUpdateFilter.addAction(ACTION_SONG_TIME_UPDATED);
        songTimeUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int songTime = (int) intent.getLongExtra(ACTION_SONG_TIME_UPDATED_EXTRA, 0);
                progressSeekbar.setProgress(songTime);

                String timeLabel;
                int min = songTime / 1000 / 60;
                int sec = songTime / 1000 % 60;

                timeLabel = min + ":";
                if (sec < 10) timeLabel += "0";
                timeLabel += sec;

                timeTextView.setText(timeLabel);
            }
        };

        IntentFilter songStatusUpdateFilter = new IntentFilter();
        songStatusUpdateFilter.addAction(ACTION_SONG_STATUS_UPDATED);
        songStatusUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isPlaying = intent.getBooleanExtra(ACTION_SONG_STATUS_UPDATED_EXTRA, false);
                if (isPlaying) {
                    setPlayerState(PlayerState.PLAYING);
                } else {
                    setPlayerState(PlayerState.STOPPED);
                }
            }
        };

        registerReceiver(songUpdateReceiver, songUpdateFilter);
        registerReceiver(songTimeUpdateReceiver, songTimeUpdateFilter);
        registerReceiver(songStatusUpdateReceiver, songStatusUpdateFilter);
    }

    private void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
        switch (this.playerState) {
            case PLAYING:
                playButton.setText(R.string.stop);
                progressSeekbar.setEnabled(true);
                break;
            case STOPPED:
                playButton.setText(R.string.start);
                progressSeekbar.setEnabled(false);
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
            for (int i = 0; i < Math.min(files.length, 50); i++) {
                tracks.add(Track.fromFile(files[i].getAbsolutePath()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Track[] tracksArray = new Track[tracks.size()];
        for (int i = 0; i < tracks.size(); i++) {
            tracksArray[i] = tracks.get(i);
        }
        startService(AudioService.makeInitTracksIntent(this, tracksArray));


        trackRecycler.setLayoutManager(new LinearLayoutManager(this));
        trackRecycler.setAdapter(new TracksAdapter(tracks, (track, position) -> {
            setPlayerState(PlayerState.PLAYING);
            playTrack(track, position);
        }));
    }

    private void playNextTrack() {
        Log.d(TAG, "Play next track");
        startService(AudioService.makeNextIntent(this));
    }

    private void playPreviousTrack() {
        Log.d(TAG, "Play previous track");
        startService(AudioService.makePreviousIntent(this));
    }

    private void playTrack(Track track, int position) {
        Log.d(TAG, "Play track " + track);
        startService(AudioService.makePlayTrackIntent(this, position));
    }

    private void pauseTrack() {
        Log.d(TAG, "Pause track");
        startService(AudioService.makePauseIntent(this));
    }

    private void resumeTrack() {
        Log.d(TAG, "Resume track");
        startService(AudioService.makeResumeIntent(this));
    }

    private void startAudioService() {
        Intent serviceIntent = new Intent(this, AudioService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (songUpdateReceiver != null) {
            unregisterReceiver(songUpdateReceiver);
            songUpdateReceiver = null;
        }

        if (songTimeUpdateReceiver != null) {
            unregisterReceiver(songTimeUpdateReceiver);
            songTimeUpdateReceiver = null;
        }

        if (songStatusUpdateReceiver != null) {
            unregisterReceiver(songStatusUpdateReceiver);
            songStatusUpdateReceiver = null;
        }

        startService(AudioService.makeDieIntent(this));
    }
}
