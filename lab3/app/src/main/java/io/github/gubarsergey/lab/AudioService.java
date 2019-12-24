package io.github.gubarsergey.lab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int resumePosition;

    private static final String TAG = "AudioService";

    private static final String ACTION_PLAY = "ACTION_PLAY";
    private static final String ACTION_PLAY_EXTRA = "ACTION_PLAY_EXTRA";

    private static final String ACTION_PAUSE = "ACTION_PAUSE";
    private static final String ACTION_RESUME = "ACTION_RESUME";

    private static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    private static final String ACTION_NEXT = "ACTION_NEXT";

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    public static Intent makePlayTrackIntent(Context context, String track) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(ACTION_PLAY_EXTRA, track);
        return intent;
    }

    public static Intent makePauseIntent(Context context) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_PAUSE);
        return intent;
    }

    public static Intent makeResumeIntent(Context context) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_RESUME);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("Text")
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        initMediaPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");

        Log.d(TAG, "Handle intent");
        if (intent == null) {
            Log.e(TAG, "Error: Intent null");
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (action == null) {
            Log.e(TAG, "Error: action null");
            return START_NOT_STICKY;

        }
        switch (action) {
            case ACTION_NEXT:
                break;
            case ACTION_PAUSE:
                pauseMedia();
                break;
            case ACTION_RESUME:
                resumeMedia();
                break;
            case ACTION_PLAY:
                String track = intent.getStringExtra(ACTION_PLAY_EXTRA);
                playMedia(track);
                break;
            case ACTION_PREVIOUS:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }

        return START_NOT_STICKY;
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        //stop the service
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        if (audioManager == null) return false;
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        //Invoked when the audio focus of the system is updated.
    }

    private void playMedia(String filePath) {
        Log.d(TAG, "playMedia " + filePath);
        try {
            mediaPlayer.setDataSource(filePath);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
//        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            // Set the data source to the mediaFile location
//            mediaPlayer.setDataSource(mediaFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            stopSelf();
//        }
//        mediaPlayer.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }

}
