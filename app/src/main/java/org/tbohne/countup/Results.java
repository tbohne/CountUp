package org.tbohne.countup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class Results extends AppCompatActivity {

    private ArrayList<String> activities;
    private ArrayList<String> times;
    private ArrayList<TextView> activityViews;
    private ArrayList<TextView> activityTimeViews;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

        Bundle bundle = getIntent().getExtras();
        this.activities = bundle.getStringArrayList("activities");
        this.times = bundle.getStringArrayList("times");

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

        this.setTimes();
    }

    private void setActivities() {
        for (int i = 0; i < this.activityViews.size(); i++) {
            this.activityViews.get(i).setText(this.activities.get(i));
        }
    }

    private void setTimes() {
        for (int i = 0; i < this.activityTimeViews.size(); i++) {
            TextView currentView = this.activityTimeViews.get(i);
            int timeInSeconds = Integer.parseInt(this.times.get(i));
            int minutes = (int)Math.floor(timeInSeconds / 60);
            int seconds = timeInSeconds - minutes * 60;
            int hours = (int)Math.floor(timeInSeconds / 3600);
            String timeString = "";
            timeString = hours < 10 ? (timeString += "0" + hours) : timeString + hours;
            timeString = minutes < 10 ? (timeString += ":0" + minutes) : (timeString += ":" + minutes);
            timeString = seconds < 10 ? (timeString += ":0" + seconds) : (timeString += ":" + seconds);
            currentView.setText(timeString);
        }
    }
}
