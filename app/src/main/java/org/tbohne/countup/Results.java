package org.tbohne.countup;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class Results extends AppCompatActivity {

    private ArrayList<String> activities;
    private ArrayList<String> times;
    private ArrayList<TextView> activityViews;
    private ArrayList<TextView> activityTimeViews;
    private ArrayList<Integer> totalTimesInSeconds;

    private Button toggleResults;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Results.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        if (isFinishing()) {
            updateSharedPrefs();
        }
        startActivity(intent);
    }

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        this.setActionBarTitle();

        Bundle bundle = getIntent().getExtras();
        this.activities = bundle.getStringArrayList("activities");
        this.times = bundle.getStringArrayList("times");
        this.totalTimesInSeconds = bundle.getIntegerArrayList("totalTimesInSeconds");

        this.toggleResults = findViewById(R.id.toggle_results);

        this.toggleResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView)v).getText().equals("total durations")) {
                    setTimes(true);
                    ((TextView)v).setText("current durations");
                } else {
                    setTimes(false);
                    ((TextView)v).setText("total durations");
                }
            }
        });

        this.activityViews = new ArrayList<>();
        this.activityViews.add((TextView) findViewById(R.id.act0));
        this.activityViews.add((TextView) findViewById(R.id.act1));
        this.activityViews.add((TextView) findViewById(R.id.act2));
        this.activityViews.add((TextView) findViewById(R.id.act3));
        this.activityViews.add((TextView) findViewById(R.id.act4));
        this.activityViews.add((TextView) findViewById(R.id.act5));

        this.setActivities();

        this.activityTimeViews = new ArrayList<>();
        this.activityTimeViews.add((TextView) findViewById(R.id.act0_time));
        this.activityTimeViews.add((TextView) findViewById(R.id.act1_time));
        this.activityTimeViews.add((TextView) findViewById(R.id.act2_time));
        this.activityTimeViews.add((TextView) findViewById(R.id.act3_time));
        this.activityTimeViews.add((TextView) findViewById(R.id.act4_time));
        this.activityTimeViews.add((TextView) findViewById(R.id.act5_time));

        this.setTimes(false);
    }

    @Override
    protected void onStop(){
        super.onStop();
        updateSharedPrefs();
    }

    private void setActionBarTitle() {
        TextView tv = new TextView(getApplicationContext());
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(lp);
        tv.setText("Summary");
        tv.setTextSize(24);
        tv.setTextColor(Color.parseColor("#000000"));
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/digital-7.ttf");
        tv.setTypeface(tf, Typeface.BOLD);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(tv);
    }

    private void updateSharedPrefs() {
        SharedPreferences pref = this.getSharedPreferences("activities", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        for (int i = 0; i < this.activities.size(); i++) {
            editor.putInt("time_" + i, this.totalTimesInSeconds.get(i));
        }
        editor.apply();
    }

    private void setActivities() {
        for (int i = 0; i < this.activityViews.size(); i++) {
            this.activityViews.get(i).setText(limitActivityLength(this.activities.get(i)));
        }
    }

    private String limitActivityLength(String activity) {
        if (activity.length() > 7) {
            StringBuilder str = new StringBuilder(activity);
            str.delete(6, activity.length());
            return str + ".";
        }
        return activity;
    }

    private void setTimes(boolean total) {
        for (int i = 0; i < this.activityTimeViews.size(); i++) {
            TextView currentView = this.activityTimeViews.get(i);
            int timeInSeconds = total ? this.totalTimesInSeconds.get(i) : Integer.parseInt(this.times.get(i));

            int hours = timeInSeconds / 3600;
            int minutes = (timeInSeconds % 3600) / 60;
            int seconds = (timeInSeconds % 3600) % 60;

            String timeString = "";
            timeString = hours < 10 ? (timeString + "0" + hours) : timeString + hours;
            timeString = minutes < 10 ? (timeString + ":0" + minutes) : (timeString + ":" + minutes);
            timeString = seconds < 10 ? (timeString + ":0" + seconds) : (timeString + ":" + seconds);
            currentView.setText(timeString);
        }
    }
}
