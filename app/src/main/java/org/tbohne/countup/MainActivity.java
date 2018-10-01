package org.tbohne.countup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button startSession;
    private ArrayList<String> activities;
    private ArrayList<Integer> totalTimesInSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.activities = new ArrayList<>();
        this.totalTimesInSeconds = new ArrayList<>();

        if (!this.restoreActivities()) {
            for (int i = 0; i < 6; i++) {
                this.promptActivity(i);
                this.totalTimesInSeconds.add(0);
            }
        }

        startSession = findViewById(R.id.welcome);
        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CountUp.class);
                intent.putExtra("activities", activities);
                intent.putExtra("totalTimesInSeconds", totalTimesInSeconds);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences pref = this.getSharedPreferences("activities", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        for (int i = 0; i < this.activities.size(); i++) {
            editor.putString("activity_" + i, this.activities.get(i));
        }
        editor.apply();
    }

    /**
     *
     * @return
     */
    private boolean restoreActivities() {

        SharedPreferences prfs = getSharedPreferences("activities", MainActivity.MODE_PRIVATE);

        for (int i = 0; i < 6; i++) {
            if (prfs.contains("activity_" + i)) {
                String activity = prfs.getString("activity_" + i, "");
                int totalTime = prfs.getInt("time_" + i, 0);
                this.totalTimesInSeconds.add(totalTime);
                this.activities.add(activity);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param activityIdx
     * @param activity
     */
    private void setActivity(int activityIdx, String activity) {
        // this.activities.set(activityIdx, activity);
        this.activities.add(activity);
    }

    private void promptActivity(final int activityIdx) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Enter activity " + activityIdx);

        builder.setCancelable(false);

        // Set up the input
        final EditText input = new EditText(MainActivity.this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")) {
                    input.setText("ACT " + activityIdx);
                }
                setActivity(activityIdx, input.getText().toString());
            }
        });
        builder.show();
    }
}
