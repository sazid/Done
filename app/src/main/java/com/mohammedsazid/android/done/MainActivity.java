package com.mohammedsazid.android.done;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


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

    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

        private View backgroundView;
        private TextView timerTextView;
        private Button toggleBtn;
        private ProgressBar progressBar;

        private ValueAnimator colorAnimator;
        private CounterClass counter;

        public PlaceholderFragment() {
        }

        private void bindViews(View rootView) {
            backgroundView = rootView.findViewById(R.id.background);
            timerTextView = (TextView) rootView.findViewById(R.id.timer_textView);
            toggleBtn = (Button) rootView.findViewById(R.id.button);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        }

        private void bindListeners() {
            toggleBtn.setOnClickListener(this);
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

            colorAnimator.setDuration(20 * 1000);

            counter = new CounterClass(20 * 1000, 1000, timerTextView, progressBar);

            return rootView;
        }
    }
}
