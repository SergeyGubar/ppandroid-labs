package io.github.gubarsergey.lab;

import android.os.CountDownTimer;
import android.util.Log;

@FunctionalInterface
interface CountdownTickListener {
    void onTick(long elapsed);
}

public class Countdown {

    private static final String TAG = "CountDown";

    private long toCompletion;
    private long elapsed;
    private long tick;
    private long total;
    private CountdownTickListener listener;

    private CountDownTimer timer;

    public Countdown(long time, long tick, CountdownTickListener listener) {
        this.toCompletion = time;
        this.elapsed = 0;
        this.tick = tick;
        this.listener = listener;
        this.total = toCompletion;
        setupNewTimer();
    }

    public Countdown start() {
        if (timer != null) {
            timer.start();
        }
        return this;
    }

    public void resume() {
        Log.d(TAG, "resume" + toCompletion + " " + tick);
        setupNewTimer();
        timer.start();
    }

    public void cancel() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }


    public void updateProgress(int elapsed) {
        this.cancel();
        this.toCompletion = total - elapsed;
        this.elapsed = elapsed;
        this.resume();
    }


    private void setupNewTimer() {
        this.timer = new CountDownTimer(toCompletion, tick) {
            @Override
            public void onTick(long l) {
                toCompletion -= tick;
                elapsed += tick;
                listener.onTick(elapsed);
            }

            @Override
            public void onFinish() {
            }
        };
    }
}
