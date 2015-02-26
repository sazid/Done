package com.mohammedsazid.android.done;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.melnykov.fab.FloatingActionButton;
import com.mohammedsazid.android.done.data.DoneContract;
import com.mohammedsazid.android.done.data.DoneProvider;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.util.concurrent.TimeUnit;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

//import android.widget.ProgressBar;

/**
 * Created by MohammedSazid on 2/22/2015.
 */
public class MainPlaceholderFragment
        extends
        Fragment
        implements
        View.OnClickListener,
        ViewSwitcher.ViewFactory,
        SeekBar.OnSeekBarChangeListener,
        android.support.v4.app.LoaderManager.LoaderCallbacks {

    // Constants and others
//    private final String LOG_TAG = MainPlaceholderFragment.class.getSimpleName();
    static int TASK_FINISHED_NOTIFICATION = 0;
    private int DEFAULT_TIMEOUT_DURATION = 5 * 60 * 1000;
    private int timeoutDuration = DEFAULT_TIMEOUT_DURATION;
    private TimerToggle timerToggle = TimerToggle.SHOULD_START;
    private Handler handler;
    private SimpleCursorAdapter cursorAdapter;

    // Drawables & colors
    private int counterBackgroundColor;

    // Views
    private View countArea;
    private View revealArea;
    private TextView counterTextView;
    private FloatingActionButton toggleBtn;
    private SeekBar timerSetSeekBar;
    private ValueAnimator colorAnimator;
    private CounterClass counter;
    private ImageButton settingsButton;
    private ImageButton deleteButton;

    // Animations
    private Animation toggleBtnAnim;

    public MainPlaceholderFragment() {
    }

    private static String formatTime(long time) {
        String str;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(time)
        );

        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(time)
        );

        str = String.format("%02dm:%02ds", minutes, seconds);

        return str;
    }

    private void setTimeoutDuration(int min) {
        int oneSecond = 1000;
        int oneMinute = 60 * oneSecond;
        timeoutDuration = min * 5 * oneMinute;
    }

    private void bindViews(View rootView) {
        countArea = rootView.findViewById(R.id.countArea);
        revealArea = rootView.findViewById(R.id.revealArea);
        toggleBtn = (FloatingActionButton) rootView.findViewById(R.id.toggleButton);
        counterTextView = (TextView) rootView.findViewById(R.id.counterTextView);
        timerSetSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        settingsButton = (ImageButton) rootView.findViewById(R.id.settingsButton);
        deleteButton = (ImageButton) rootView.findViewById(R.id.deleteButton);

        toggleBtnAnim = AnimationUtils.loadAnimation(
                getActivity(), R.anim.toggle_button_anim);
        handler = new Handler();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Prepare the loader. Either, re-connect with an existing loader,
        // or create a new one
        getLoaderManager().initLoader(0, null, this);
    }

    private void bindListeners() {
        toggleBtn.setOnClickListener(this);
        timerSetSeekBar.setOnSeekBarChangeListener(this);
        settingsButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }

    private void animateToggleButton(boolean toggle) {
        toggleBtn.startAnimation(toggleBtnAnim);

        if (toggle) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_stop));
                }
            }, 200);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    toggleBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                }
            }, 200);
        }
    }

    private void startCountdown() {
        revealArea.setBackgroundColor(getResources().getColor(R.color.deep_purple_500));
        countArea.setBackgroundColor(getResources().getColor(R.color.red_500));
        toggleBtn.setColorNormal(getResources().getColor(R.color.deep_purple_500));

        counter.start();
        colorAnimator.start();

        animateToggleButton(true);

        timerToggle = TimerToggle.SHOULD_STOP;
        timerSetSeekBar.setVisibility(View.INVISIBLE);
        countArea.setBackgroundColor(getResources().getColor(R.color.red_500));

        // keep the screen on while the user is using the program
        getActivity()
                .getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text("Task countdown started!")
        );
    }

    private void cancelCountdown() {
        countArea.setBackgroundColor(getResources().getColor(R.color.deep_purple_500));
        revealArea.setBackgroundColor(counterBackgroundColor);
        toggleBtn.setColorNormal(getResources().getColor(R.color.red_500));

        counter.cancel();
        colorAnimator.cancel();

        animateToggleButton(false);

        counterTextView.setText(MainPlaceholderFragment.formatTime(timeoutDuration));
        timerSetSeekBar.setVisibility(View.VISIBLE);

        timerToggle = TimerToggle.SHOULD_START;

        // clear screen on flag
        getActivity()
                .getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SnackbarManager.show(
                Snackbar.with(getActivity())
                        .text("Task cancelled")
        );
    }

    private void setTimer() {
        colorAnimator.setDuration(timeoutDuration);
        counter = new CounterClass(timeoutDuration, 1000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        progress += 1;
        setTimeoutDuration(progress);
        counterTextView.setText(MainPlaceholderFragment.formatTime(timeoutDuration));

//            cancelCountdown();

        setTimer();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.toggleButton) {
            // get the center for the clipping circle
            int cx = (toggleBtn.getLeft() + toggleBtn.getRight()) / 2;
            int cy = (toggleBtn.getTop() + toggleBtn.getBottom()) / 2;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(countArea.getHeight(), countArea.getWidth());
            finalRadius += 20;

            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(countArea, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(400);

            switch (timerToggle) {
                case SHOULD_START:
                    startCountdown();

                    timerToggle = TimerToggle.SHOULD_STOP;
                    animator.start();
                    break;
                case SHOULD_STOP:
                    cancelCountdown();

                    timerToggle = TimerToggle.SHOULD_START;
                    animator.start();
                    break;
            }
        } else if (id == R.id.settingsButton) {
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Settings")
            );
        } else if (id == R.id.deleteButton) {
            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Delete")
            );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        bindViews(rootView);
        bindListeners();

        Integer colorFrom = getResources().getColor(R.color.red_500);
        Integer colorTo = getResources().getColor(R.color.green_500);

        colorAnimator = ValueAnimator.ofObject(
                new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                countArea.setBackgroundColor((Integer) animation.getAnimatedValue());
                counterBackgroundColor = (Integer) animation.getAnimatedValue();
            }
        });

//        Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
//        Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
//        counterTextView.setInAnimation(in);
//        counterTextView.setOutAnimation(out);

        setTimer();
        counterTextView.setText(MainPlaceholderFragment.formatTime(timeoutDuration));

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (timerToggle == TimerToggle.SHOULD_STOP) {
            timeoutDuration = DEFAULT_TIMEOUT_DURATION;
            cancelCountdown();
            createNotification("Task cancelled!", "Oh, the task has been cancelled! :(");
            timerSetSeekBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View makeView() {
        TextView t = new TextView(getActivity());
        t.setTextSize(44);
        t.setTextColor(getResources().getColor(R.color.white));
        t.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        return t;
    }

    private void createNotification(String contentTitle, String contentText) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(contentTitle)
                        .setContentText(contentText);

        builder.setPriority(Notification.PRIORITY_MAX);

        // Set notification sound & vibration
        builder.setDefaults(
                Notification.DEFAULT_VIBRATE
                        | Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_LIGHTS
        );

        // Automatically cancel the notification when the user taps it
        builder.setAutoCancel(true);

        // The intent to call when the notification is tapped`
        Intent intent = new Intent(getActivity(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getActivity(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getActivity()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(MainPlaceholderFragment.TASK_FINISHED_NOTIFICATION, builder.build());
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // Since, we only have one content provider to deal with, we don't need to
        // manage multiple loaders with the 'id'
        Uri baseUri = DoneProvider.CONTENT_URI;

        // list of columns we want to retrieve values from
        String[] projection = {
                DoneContract.TasksTable.COLUMN_TASK_NAME,
                DoneContract.TasksTable.COLUMN_TASK_TIME,
                DoneContract.TasksTable.COLUMN_DATETIME,
                DoneContract.TasksTable.COLUMN_TASK_STATUS
        };

        return new CursorLoader(getActivity(), baseUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private enum TimerToggle {
        SHOULD_START, SHOULD_STOP
    }

    private class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String displayString = MainPlaceholderFragment.formatTime(millisUntilFinished);

            counterTextView.setText(displayString);
        }

        @Override
        public void onFinish() {
            counterTextView.setText("Great job! You finished the task!");
            animateToggleButton(false);

            createNotification("Done", "Now, go and take some rest :)");

            // clear screen on flag
            getActivity()
                    .getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            timerToggle = TimerToggle.SHOULD_START;

            SnackbarManager.show(
                    Snackbar.with(getActivity())
                            .text("Done!")
            );
        }
    }

}
