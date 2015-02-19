package com.mohammedsazid.android.done;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by MohammedSazid on 2/19/2015.
 */
public class CounterClass extends CountDownTimer {

    private TextSwitcher counterTextView;
    private ProgressBar progressBar;

    public CounterClass(
            long millisInFuture,
            long countDownInterval,
            TextSwitcher view,
            ProgressBar bar
    ) {
        super(millisInFuture, countDownInterval);
        counterTextView = view;
        progressBar = bar;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long millis = millisUntilFinished;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millis)
        );

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millis)
        );

        String displayString = String.format("%02dm:%02ds", minutes, seconds);

        counterTextView.setCurrentText(displayString);

        progressBar.setProgress( (int) (100 / millis) );
    }

    @Override
    public void onFinish() {
        counterTextView.setText("Great job! You finished the task!");
    }
}
