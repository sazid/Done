package com.mohammedsazid.android.done;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;


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

        private final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        private int timeoutDuration = 5 * 60 * 1000;

        private View backgroundView;
        private TextSwitcher timerTextSwitcher;
        private Button toggleBtn;
        private ProgressBar progressBar;
        private SeekBar timerSetSeekBar;

        private ValueAnimator colorAnimator;
        private CounterClass counter;

        public PlaceholderFragment() {
        }

        private void setTimeoutDuration(int min) {
            int oneSecond = 1000;
            int oneMinute = 60 * oneSecond;
            timeoutDuration = min * 5 * oneMinute;
        }

        private void bindViews(View rootView) {
            backgroundView = rootView.findViewById(R.id.background);
            toggleBtn = (Button) rootView.findViewById(R.id.button);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            timerTextSwitcher = (TextSwitcher) rootView.findViewById(R.id.timer_textSwitcher);
            timerSetSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        }

        private void bindListeners() {
            toggleBtn.setOnClickListener(this);
            timerTextSwitcher.setFactory(this);
            timerSetSeekBar.setOnSeekBarChangeListener(this);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//            Log.v(LOG_TAG, "Progress: " + progress + ", fromUser: " + fromUser);
            progress += 1;
            setTimeoutDuration(progress);
//            Log.v(LOG_TAG, "" + timeoutDuration);
//            Log.v(LOG_TAG, CounterClass.formatTime(timeoutDuration));
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

            if (id == R.id.button) {
                colorAnimator.start();
                counter.start();
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
//                    Log.v(LOG_TAG, String.valueOf((int) (animation.getAnimatedFraction() * 1000)));
                }
            });

            Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
            Animation out = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
            timerTextSwitcher.setInAnimation(in);
            timerTextSwitcher.setOutAnimation(out);

            colorAnimator.setDuration(20 * 1000);
            counter = new CounterClass(20 * 1000, 1000, timerTextSwitcher, progressBar);
            timerTextSwitcher.setText("00m:00s");

            return rootView;
        }

        @Override
        public View makeView() {
            TextView t = new TextView(getActivity());
            t.setTextSize(44);
            t.setTextColor(getResources().getColor(R.color.white));
            t.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            return t;
        }
    }
}
