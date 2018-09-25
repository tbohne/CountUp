package org.tbohne.countup;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CountUp extends AppCompatActivity {

    private Button stopSession;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private ArrayList<Chronometer> chronometers;

    private long timeWhenStoppedChronometer0 = 0;
    private long timeWhenStoppedChronometer1 = 0;
    private long timeWhenStoppedChronometer2 = 0;
    private long timeWhenStoppedChronometer3 = 0;
    private long timeWhenStoppedChronometer4 = 0;
    private long timeWhenStoppedChronometer5 = 0;

    private ArrayList<String> activities;
    private ArrayList<TextView> views;

    private String currentActivity;

    /**
     * Called when the activity is started.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_up);

        this.chronometers = new ArrayList<>();

        this.chronometers.add((Chronometer) findViewById(R.id.act0_time));
        this.chronometers.add((Chronometer) findViewById(R.id.act1_time));
        this.chronometers.add((Chronometer) findViewById(R.id.act2_time));
        this.chronometers.add((Chronometer) findViewById(R.id.act3_time));
        this.chronometers.add((Chronometer) findViewById(R.id.act4_time));
        this.chronometers.add((Chronometer) findViewById(R.id.act5_time));

        for (Chronometer chronometer : this.chronometers) {
            chronometer.setFormat("%s");
            chronometer.setBase(SystemClock.elapsedRealtime());
        }

        Bundle bundle = getIntent().getExtras();
        this.activities = bundle.getStringArrayList("activities");

        this.stopSession = findViewById(R.id.stop);

        stopSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terminateCurrentActivity();
                ViewGroup parentView = (ViewGroup) v.getParent();
                parentView.removeView(v);
            }
        });

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        this.initializeActivities();
        this.currentActivity = "";

        if (this.nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            this.pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(this, this.getClass()).addFlags(
                            Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
            );
        }
    }

    /**
     * Called when the activity restarts.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled()) {
                showWirelessSettings();
            }
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    /**
     * Instead of a new instance of the activity being started, this method will be called on
     * the existing instance with the Intent that was used to re-launch it.
     * @param intent - the intent that was used for the re-launch
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    /**
     * Resolves the given intent.
     * If the nfc connection to the tag is established successfully,
     * the transmitted messages get displayed.
     *
     * @param intent - the operation to be performed (nfc action)
     */
    private void resolveIntent(Intent intent) {

        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable[] rawMessages = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES
            );
            NdefMessage[] messages;

            if (rawMessages != null) {
                messages = new NdefMessage[rawMessages.length];

                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage)rawMessages[i];
                }
            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                byte[] payload = "No data received".getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                messages = new NdefMessage[] {msg};
            }
            displayMessages(messages);
        }
    }

    /**
     *
     */
    private void initializeActivities() {
        this.views = new ArrayList<>();
        views.add((TextView) findViewById(R.id.act0));
        views.add((TextView) findViewById(R.id.act1));
        views.add((TextView) findViewById(R.id.act2));
        views.add((TextView) findViewById(R.id.act3));
        views.add((TextView) findViewById(R.id.act4));
        views.add((TextView) findViewById(R.id.act5));

        for (int i = 0; i < this.views.size(); i++) {
            this.views.get(i).setText(this.activities.get(i));
        }
    }

    /**
     *
     */
    private void pauseCurrentActivity() {
        if (this.currentActivity.equals(this.activities.get(0))) {
            this.timeWhenStoppedChronometer0 = this.chronometers.get(0).getBase() - SystemClock.elapsedRealtime();
            this.chronometers.get(0).stop();
        } else if (this.currentActivity.equals(this.activities.get(1))) {
            this.timeWhenStoppedChronometer1 = this.chronometers.get(1).getBase() - SystemClock.elapsedRealtime();
            this.chronometers.get(1).stop();
        } else if (this.currentActivity.equals(this.activities.get(2))) {
            this.timeWhenStoppedChronometer2 = this.chronometers.get(2).getBase() - SystemClock.elapsedRealtime();
            this.chronometers.get(2).stop();
        } else if (this.currentActivity.equals(this.activities.get(3))) {
            this.timeWhenStoppedChronometer3 = this.chronometers.get(3).getBase() - SystemClock.elapsedRealtime();
            this.chronometers.get(3).stop();
        } else if (this.currentActivity.equals(this.activities.get(4))) {
            this.timeWhenStoppedChronometer4 = this.chronometers.get(4).getBase() - SystemClock.elapsedRealtime();
            this.chronometers.get(4).stop();
        } else if (this.currentActivity.equals(this.activities.get(5))) {
            this.timeWhenStoppedChronometer5 = this.chronometers.get(5).getBase() - SystemClock.elapsedRealtime();
            this.chronometers.get(5).stop();
        }
    }

    /**
     * Terminates the activity that is currently active and computes its final duration.
     */
    private void terminateCurrentActivity() {
        if (this.currentActivity.equals(this.activities.get(0))) {
            this.chronometers.get(0).stop();
        } else if (this.currentActivity.equals(this.activities.get(1))) {
            this.chronometers.get(1).stop();
        } else if (this.currentActivity.equals(this.activities.get(2))) {
            this.chronometers.get(2).stop();
        } else if (this.currentActivity.equals(this.activities.get(3))) {
            this.chronometers.get(3).stop();
        } else if (this.currentActivity.equals(this.activities.get(4))) {
            this.chronometers.get(4).stop();
        } else if (this.currentActivity.equals(this.activities.get(5))) {
            this.chronometers.get(5).stop();
        }
    }

    /**
     * Starts the count-up process for the current activity by setting its start time.
     */
    private void startCountUp() {
        if (this.currentActivity.equals(this.activities.get(0))) {
            this.chronometers.get(0).setBase(SystemClock.elapsedRealtime() + this.timeWhenStoppedChronometer0);
            this.chronometers.get(0).start();
        } else if (this.currentActivity.equals(this.activities.get(1))) {
            this.chronometers.get(1).setBase(SystemClock.elapsedRealtime() + this.timeWhenStoppedChronometer1);
            this.chronometers.get(1).start();
        } else if (this.currentActivity.equals(this.activities.get(2))) {
            this.chronometers.get(2).setBase(SystemClock.elapsedRealtime() + this.timeWhenStoppedChronometer2);
            this.chronometers.get(2).start();
        } else if (this.currentActivity.equals(this.activities.get(3))) {
            this.chronometers.get(3).setBase(SystemClock.elapsedRealtime() + this.timeWhenStoppedChronometer3);
            this.chronometers.get(3).start();
        } else if (this.currentActivity.equals(this.activities.get(4))) {
            this.chronometers.get(4).setBase(SystemClock.elapsedRealtime() + this.timeWhenStoppedChronometer4);
            this.chronometers.get(4).start();
        } else if (this.currentActivity.equals(this.activities.get(5))) {
            this.chronometers.get(5).setBase(SystemClock.elapsedRealtime() + this.timeWhenStoppedChronometer5);
            this.chronometers.get(5).start();
        }
    }

    /**
     * Displays the current nfc message in case it is not the same as before.
     * Handles the start and termination of the different activities.
     *
     * @param messages - the nfc messages to be parsed
     */
    private void displayMessages(NdefMessage[] messages) {
        if (messages != null && messages.length != 0) {

            StringBuilder builder = new StringBuilder();
            List<TextRecord> records = NdefMessageParser.parse(messages[0]);
            final int size = records.size();

            for (int i = 0; i < size; i++) {
                TextRecord record = records.get(i);
                String str = record.str();
                builder.append(str).append("\n");
            }

            int currentActivityIdx = this.activities.indexOf(this.currentActivity);
            String currentTag = "Act" + currentActivityIdx;

            if (!currentTag.equals(builder.toString().trim())) {

                this.pauseCurrentActivity();
                int activityIdx = Integer.parseInt(builder.toString().trim().replace("Act", ""));

                if (currentActivityIdx != -1) {
                    this.views.get(currentActivityIdx).setTextColor(Color.parseColor("#b9c7dd"));
                }

                this.currentActivity = this.activities.get(activityIdx);

                this.views.get(activityIdx).setTextColor(Color.parseColor("#FF9D21"));

                this.startCountUp();
            }
        }
    }

    /**
     * Shows the wireless settings in case that nfc is not enabled on the target device.
     */
    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

}
