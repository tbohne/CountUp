package org.tbohne.countup;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CountUp extends AppCompatActivity {

    private TextView text;
    private Button stopSession;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private ArrayList<String> activities;

    private String currentActivity;

    private TextView actZeroTime;
    private TextView actOneTime;
    private TextView actTwoTime;
    private TextView actThreeTime;
    private TextView actFourTime;
    private TextView actFiveTime;

    private TextView actZeroSlot;
    private TextView actOneSlot;
    private TextView actTwoSlot;
    private TextView actThreeSlot;
    private TextView actFourSlot;
    private TextView actFiveSlot;

    private long actZeroDuration;
    private long actOneDuration;
    private long actTwoDuration;
    private long actThreeDuration;
    private long actFourDuration;
    private long actFiveDuration;

    private long actZeroStart;
    private long actOneStart;
    private long actTwoStart;
    private long actThreeStart;
    private long actFourStart;
    private long actFiveStart;

    /**
     * Called when the activity is started.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_up);

        Bundle bundle = getIntent().getExtras();
        this.activities = bundle.getStringArrayList("activities");

        this.text = findViewById(R.id.text);
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
        this.actZeroTime = findViewById(R.id.act0_time);
        this.actOneTime = findViewById(R.id.act1_time);
        this.actTwoTime = findViewById(R.id.act2_time);
        this.actThreeTime = findViewById(R.id.act3_time);
        this.actFourTime = findViewById(R.id.act4_time);
        this.actFiveTime = findViewById(R.id.act5_time);

        this.actZeroSlot = findViewById(R.id.act0);
        this.actOneSlot = findViewById(R.id.act1);
        this.actTwoSlot = findViewById(R.id.act2);
        this.actThreeSlot = findViewById(R.id.act3);
        this.actFourSlot = findViewById(R.id.act4);
        this.actFiveSlot = findViewById(R.id.act5);

        this.actZeroSlot.setText(this.activities.get(0));
        this.actOneSlot.setText(this.activities.get(1));
        this.actTwoSlot.setText(this.activities.get(2));
        this.actThreeSlot.setText(this.activities.get(3));
        this.actFourSlot.setText(this.activities.get(4));
        this.actFiveSlot.setText(this.activities.get(5));

        this.actZeroDuration = 0;
        this.actOneDuration = 0;
        this.actTwoDuration = 0;
        this.actThreeDuration = 0;
        this.actFourDuration = 0;
        this.actFiveDuration = 0;
    }

    /**
     * Terminates the activity that is currently active and computes its final duration.
     */
    private void terminateCurrentActivity() {
        switch (this.currentActivity) {
            case "Act0":
                this.actZeroDuration += System.nanoTime() - this.actZeroStart;
                this.actZeroTime.setText(
                        Math.round((double)this.actZeroDuration / 1000000000.0) + " s"
                );
                break;
            case "Act1":
                this.actOneDuration += System.nanoTime() - this.actOneStart;
                this.actOneTime.setText(
                        Math.round((double)this.actOneDuration / 1000000000.0) + " s"
                );
                break;
            case "Act2":
                this.actTwoDuration += System.nanoTime() - this.actTwoStart;
                this.actTwoTime.setText(
                        Math.round((double)this.actTwoDuration / 1000000000.0) + " s"
                );
                break;
            case "Act3":
                this.actThreeDuration += System.nanoTime() - this.actThreeStart;
                this.actThreeTime.setText(
                        Math.round((double)this.actThreeDuration / 1000000000.0) + " s"
                );
                break;
            case "Act4":
                this.actFourDuration += System.nanoTime() - this.actFourStart;
                this.actFourTime.setText(
                        Math.round((double)this.actFourDuration / 1000000000.0) + " s"
                );
                break;
            case "Act5":
                this.actFiveDuration += System.nanoTime() - this.actFiveStart;
                this.actFiveTime.setText(
                        Math.round((double)this.actFiveDuration / 1000000000.0) + " s"
                );
                break;
            default:
                // TODO
        }
    }

    /**
     * Starts the count-up process for the current activity by setting its start time.
     */
    private void startCountUp() {

        if (this.currentActivity.equals(this.activities.get(0))) {
            this.actZeroStart = System.nanoTime();
        } else if (this.currentActivity.equals(this.activities.get(1))) {
            this.actOneStart = System.nanoTime();
        } else if (this.currentActivity.equals(this.activities.get(2))) {
            this.actTwoStart = System.nanoTime();
        } else if (this.currentActivity.equals(this.activities.get(3))) {
            this.actThreeStart = System.nanoTime();
        } else if (this.currentActivity.equals(this.activities.get(4))) {
            this.actFourStart = System.nanoTime();
        } else if (this.currentActivity.equals(this.activities.get(5))) {
            this.actFiveStart = System.nanoTime();
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

            if (!this.currentActivity.equals(builder.toString().trim())) {
                this.terminateCurrentActivity();
                this.currentActivity = builder.toString().trim();
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
