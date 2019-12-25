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
import android.os.Parcelable;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Arrays;
import java.util.Random;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private Countdown timer;
    private int resumePosition;
    private int playbackPosition = -1;

    private boolean isShuffleMode = false;
    private boolean isRepeatMode = false;
    private int notificationId = 14;

    private Track[] tracks;
    private static final String TAG = "AudioService";

    private static final String ACTION_INIT_TRACKS = "ACTION_INIT_TRACKS";
    private static final String ACTION_INIT_TRACKS_EXTRA = "ACTION_INIT_TRACKS";

    private static final String ACTION_PLAY = "ACTION_PLAY";
    private static final String ACTION_PLAY_EXTRA = "ACTION_PLAY_EXTRA";

    private static final String ACTION_PAUSE = "ACTION_PAUSE";
    private static final String ACTION_RESUME = "ACTION_RESUME";

    private static final String ACTION_NEXT = "ACTION_NEXT";
    private static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";

    private static final String ACTION_PROGRESS_CHANGED = "ACTION_PROGRESS_CHANGED";
    private static final String ACTION_PROGRESS_CHANGED_EXTRA = "ACTION_PROGRESS_CHANGED_EXTRA";

    private static final String ACTION_SHUFFLE_CHANGED = "ACTION_SHUFFLE_CHANGED";
    private static final String ACTION_SHUFFLE_CHANGED_EXTRA = "ACTION_SHUFFLE_CHANGED_EXTRA";

    private static final String ACTION_REPEAT_CHANGED = "ACTION_REPEAT_CHANGED";
    private static final String ACTION_REPEAT_CHANGED_EXTRA = "ACTION_REPEAT_CHANGED_EXTRA";

    private static final String DIE_MODE = "DIE_MODE";

    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    public static Intent makePlayTrackIntent(Context context, int trackPosition) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_PLAY);
        intent.putExtra(ACTION_PLAY_EXTRA, trackPosition);
        return intent;
    }

    public static Intent makeInitTracksIntent(Context context, Parcelable[] tracks) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_INIT_TRACKS);
        intent.putExtra(ACTION_INIT_TRACKS_EXTRA, tracks);
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

    public static Intent makeNextIntent(Context context) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_NEXT);
        return intent;
    }

    public static Intent makePreviousIntent(Context context) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_PREVIOUS);
        return intent;
    }

    public static Intent makeProgressChangedIntent(Context context, int progress) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_PROGRESS_CHANGED);
        intent.putExtra(ACTION_PROGRESS_CHANGED_EXTRA, progress);
        return intent;
    }

    public static Intent makeShuffleChangedIntent(Context context, boolean isShuffleMode) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_SHUFFLE_CHANGED);
        intent.putExtra(ACTION_SHUFFLE_CHANGED_EXTRA, isShuffleMode);
        return intent;
    }

    public static Intent makeRepeatChangedIntent(Context context, boolean isRepeatMode) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(ACTION_REPEAT_CHANGED);
        intent.putExtra(ACTION_REPEAT_CHANGED_EXTRA, isRepeatMode);
        return intent;
    }

    public static Intent makeDieIntent(Context context) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(DIE_MODE);
        return intent;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        requestAudioFocus();
        Notification notification = createNotification("Start", "Start", true);
        startForeground(notificationId, notification);
        initMediaPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");

        Log.d(TAG, "Handle intent");
        if (intent == null || intent.getAction() == null) {
            Log.e(TAG, "Error: Intent or action null");
            return START_NOT_STICKY;
        }
        switch (intent.getAction()) {
            case ACTION_INIT_TRACKS:
                Parcelable[] tracksFromIntent = intent.getParcelableArrayExtra(ACTION_INIT_TRACKS_EXTRA);
                Track[] tracks = new Track[tracksFromIntent.length];
                for (int i = 0; i < tracksFromIntent.length; i++) {
                    tracks[i] = (Track) tracksFromIntent[i];
                }
                loadTracks(tracks);
                break;
            case ACTION_NEXT:
                playNextTrack();
                break;
            case ACTION_REPEAT_CHANGED:
                boolean isRepeat = intent.getBooleanExtra(ACTION_REPEAT_CHANGED_EXTRA, false);
                this.isRepeatMode = isRepeat;
                break;
            case ACTION_SHUFFLE_CHANGED:
                boolean isShuffle = intent.getBooleanExtra(ACTION_SHUFFLE_CHANGED_EXTRA, false);
                this.isShuffleMode = isShuffle;
                break;
            case ACTION_PAUSE:
                pauseMedia();
                break;
            case ACTION_RESUME:
                resumeMedia();
                break;
            case ACTION_PLAY:
                int position = intent.getIntExtra(ACTION_PLAY_EXTRA, 0);
                playMedia(position);
                break;
            case ACTION_PREVIOUS:
                playPreviousTrack();
                break;
            case ACTION_PROGRESS_CHANGED:
                int progress = intent.getIntExtra(ACTION_PROGRESS_CHANGED_EXTRA, 0);
                updateProgress(progress);
                break;
            case DIE_MODE:
                stopForeground(true);
                stopSelf();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + intent.getAction());
        }

        return START_NOT_STICKY;
    }

    private void loadTracks(Track[] tracks) {
        Log.d(TAG, "Load tracks " + Arrays.toString(tracks));
        this.tracks = tracks;
    }

    private void updateProgress(int progress) {
        Log.d(TAG, "updateProgress " + progress);
        mediaPlayer.seekTo(progress);
        timer.updateProgress(progress);
    }

    private void playNextTrack() {
        Log.d(TAG, "playNextTrack isShuffle " + isShuffleMode + " isRepeat " + isRepeatMode);
        int newPlayback;

        if (isShuffleMode) {
            newPlayback = new Random().nextInt(tracks.length);
            playMedia(newPlayback);
            return;
        }

        if (isRepeatMode) {
            playMedia(playbackPosition);
            return;
        }

        if (this.playbackPosition + 1 > this.tracks.length - 1) {
            newPlayback = 0;
        } else {
            newPlayback = playbackPosition + 1;
        }
        playMedia(newPlayback);
    }

    private void playPreviousTrack() {
        int newPlayback;

        if (isShuffleMode) {
            newPlayback = new Random().nextInt(tracks.length);
            playMedia(newPlayback);
            return;
        }

        if (isRepeatMode) {
            playMedia(playbackPosition);
            return;
        }

        if (this.playbackPosition - 1 < 0) {
            newPlayback = tracks.length - 1;
        } else {
            newPlayback = playbackPosition - 1;
        }
        playMedia(newPlayback);

    }

    private void playMedia(int position) {
        try {
            Track track = tracks[position];

            mediaPlayer.reset();
            mediaPlayer.setDataSource(track.filepath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            this.playbackPosition = position;

            Intent trackIntent = new Intent();
            trackIntent.setAction(MainActivity.ACTION_SONG_UPDATED);
            trackIntent.putExtra(MainActivity.ACTION_SONG_UPDATED_EXTRA, track);
            sendBroadcast(trackIntent);

            Intent songStatusUpdateIntent = new Intent();
            songStatusUpdateIntent.setAction(MainActivity.ACTION_SONG_STATUS_UPDATED);
            songStatusUpdateIntent.putExtra(MainActivity.ACTION_SONG_TIME_UPDATED_EXTRA, true);
            sendBroadcast(songStatusUpdateIntent);

            if (timer != null) {
                timer.cancel();
                timer = null;
            }

            timer = new Countdown(track.totalTime, 1000, elapsed -> {
                Intent timeUpdateIntent = new Intent();
                timeUpdateIntent.setAction(MainActivity.ACTION_SONG_TIME_UPDATED);
                timeUpdateIntent.putExtra(MainActivity.ACTION_SONG_TIME_UPDATED_EXTRA, elapsed);
                sendBroadcast(timeUpdateIntent);
            }).start();
            updateNotification(track.name, track.author, false);
            sendSongStatusIntent(true);
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
            timer.cancel();
            Track track = tracks[playbackPosition];
            updateNotification(track.name, track.author, true);
            sendSongStatusIntent(false);
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            timer.resume();
            Track track = tracks[playbackPosition];
            updateNotification(track.name, track.author, false);
            sendSongStatusIntent(true);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setDescription("description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void updateNotification(String title, String text, boolean play) {
        Notification notification = createNotification(title, text, play);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, notification);
    }


    private void sendSongStatusIntent(boolean isPlaying) {
        Intent songStatusUpdateIntent = new Intent();
        songStatusUpdateIntent.setAction(MainActivity.ACTION_SONG_STATUS_UPDATED);
        songStatusUpdateIntent.putExtra(MainActivity.ACTION_SONG_STATUS_UPDATED_EXTRA, isPlaying);
        sendBroadcast(songStatusUpdateIntent);
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


    private Notification createNotification(String title, String text, boolean play) {

        createNotificationChannel();
        final Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        Intent nextIntent = new Intent(this, AudioService.class);
        nextIntent.setAction(ACTION_NEXT);

        Intent previousIntent = new Intent(this, AudioService.class);
        previousIntent.setAction(ACTION_PREVIOUS);

        Intent playIntent = new Intent(this, AudioService.class);
        playIntent.setAction(ACTION_PLAY);

        Intent resumeIntent = new Intent(this, AudioService.class);
        resumeIntent.setAction(ACTION_RESUME);

        Intent pauseIntent = new Intent(this, AudioService.class);
        pauseIntent.setAction(ACTION_PAUSE);


        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        PendingIntent nextPendingIntent =
                PendingIntent.getService(this, 0, nextIntent, 0);

        PendingIntent previousPendingIntent =
                PendingIntent.getService(this, 0, previousIntent, 0);

        PendingIntent playPendingIntent =
                PendingIntent.getService(this, 0, playIntent, 0);

        PendingIntent resumePendingIntent =
                PendingIntent.getService(this, 0, resumeIntent, 0);

        PendingIntent pausePendingIntent =
                PendingIntent.getService(this, 0, pauseIntent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_background, getString(R.string.next),
                        nextPendingIntent)
                .addAction(R.drawable.ic_launcher_background, getString(R.string.previous),
                        previousPendingIntent);

        if (play) {
            if (playbackPosition == -1) {
                notification.addAction(R.drawable.ic_launcher_background, getString(R.string.play),
                        playPendingIntent);
            } else {
                notification.addAction(R.drawable.ic_launcher_background, getString(R.string.play),
                        resumePendingIntent);
            }

        } else {
            notification.addAction(R.drawable.ic_launcher_background, getString(R.string.stop),
                    pausePendingIntent);
        }

        return notification.build();
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
        playNextTrack();
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
        Log.d(TAG, "onAudioFocusChange");
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d(TAG, "gain audiofocus");
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                mediaPlayer.setVolume(1.0f, 1.0f);
                timer.resume();
                sendSongStatusIntent(true);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d(TAG, "loss audiofocus");
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d(TAG, "loss transient audiofocus");
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                timer.cancel();
                sendSongStatusIntent(false);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.d(TAG, "loss audiofocus can duck");
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
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
        timer.cancel();
        removeAudioFocus();
    }

}
