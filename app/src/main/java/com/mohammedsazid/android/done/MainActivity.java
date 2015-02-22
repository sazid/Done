package com.mohammedsazid.android.done;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.concurrent.TimeUnit;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public static class PlaceholderFragment extends Fragment
            implements View.OnClickListener, ViewSwitcher.ViewFactory, SeekBar.OnSeekBarChangeListener {

        //        private final String LOG_TAG = PlaceholderFragment.class.getSimpleName();\
        static int TASK_FINISHED_NOTIFICATION = 0;
        private int DEFAULT_TIMEOUT_DURATION = 5 * 60 * 1000;
        private int timeoutDuration = DEFAULT_TIMEOUT_DURATION;
        private TimerToggle timerToggle = TimerToggle.SHOULD_START;
        private View backgroundView;
        private TextSwitcher timerTextSwitcher;
        private Button toggleBtn;
        private ProgressBar progressBar;
        private SeekBar timerSetSeekBar;
        private ValueAnimator colorAnimator;
        private CounterClass counter;

        public PlaceholderFragment() {
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
            backgroundView = rootView.findViewById(R.id.background);
            toggleBtn = (Button) rootView.findViewById(R.id.toggleButton);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            timerTextSwitcher = (TextSwitcher) rootView.findViewById(R.id.timer_textSwitcher);
            timerSetSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        }

        private void bindListeners() {
            toggleBtn.setOnClickListener(this);
            timerTextSwitcher.setFactory(this);
            timerSetSeekBar.setOnSeekBarChangeListener(this);
        }

        private void startCountdown() {
            counter.start();
            colorAnimator.start();

            toggleBtn.setText(getResources().getString(R.string.toggleButtonStop));
            timerToggle = TimerToggle.SHOULD_STOP;
            timerSetSeekBar.setVisibility(View.INVISIBLE);

            // keep the screen on while the user is using the program
            getActivity()
                    .getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        private void cancelCountdown() {
            counter.cancel();
            colorAnimator.cancel();

            toggleBtn.setText(getResources().getString(R.string.toggleButtonStart));
            timerTextSwitcher.setText(PlaceholderFragment.formatTime(timeoutDuration));
            progressBar.setProgress(0);
            timerSetSeekBar.setVisibility(View.VISIBLE);

            timerToggle = TimerToggle.SHOULD_START;

            // clear screen on flag
            getActivity()
                    .getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        private void setTimer() {
            colorAnimator.setDuration(timeoutDuration);
            counter = new CounterClass(timeoutDuration, 1000);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progress += 1;
            setTimeoutDuration(progress);
            timerTextSwitcher.setText(PlaceholderFragment.formatTime(timeoutDuration));

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
                switch (timerToggle) {
                    case SHOULD_START:
                        startCountdown();

                        timerToggle = TimerToggle.SHOULD_STOP;
                        break;
                    case SHOULD_STOP:
                        cancelCountdown();

                        timerToggle = TimerToggle.SHOULD_START;
                        break;
                }
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
                    backgroundView.setBackgroundColor((Integer) animation.getAnimatedValue());
                    toggleBtn.setTextColor((Integer) animation.getAnimatedValue());
                    progressBar.setProgress((int) (animation.getAnimatedFraction() * 1000));
                }
            });

            Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
            Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
            timerTextSwitcher.setInAnimation(in);
            timerTextSwitcher.setOutAnimation(out);

            setTimer();
            timerTextSwitcher.setText(PlaceholderFragment.formatTime(timeoutDuration));

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
            manager.notify(PlaceholderFragment.TASK_FINISHED_NOTIFICATION, builder.build());
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
                String displayString = PlaceholderFragment.formatTime(millisUntilFinished);

                timerTextSwitcher.setCurrentText(displayString);

                progressBar.setProgress((int) (100 / millisUntilFinished));
            }

            @Override
            public void onFinish() {
                timerTextSwitcher.setText("Great job! You finished the task!");
                toggleBtn.setText(getResources().getString(R.string.toggleButtonStart));

                createNotification("Task finished", "Now, go and take some rest :)");

                // clear screen on flag
                getActivity()
                        .getWindow()
                        .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                timerToggle = TimerToggle.SHOULD_START;
            }
        }

    }
}
