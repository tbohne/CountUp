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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CountUp extends AppCompatActivity {

    private TextView text;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private String actOne;
    private String actTwo;

    private TextView actOneTime;
    private TextView actTwoTime;

    private long actOneDuration;
    private long actTwoDuration;

    private long actOneStart;
    private long actTwoStart;

    private String currentActivity;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.count_up);

        this.text = findViewById(R.id.text);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        this.actOneTime = findViewById(R.id.act1_time);
        this.actTwoTime = findViewById(R.id.act2_time);

        // TODO: shouldn't be hard coded!
        this.actOne = "Act0";
        this.actTwo = "Act1";
        this.actOneDuration = 0;
        this.actTwoDuration = 0;
        this.currentActivity = "";

        if (this.nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            this.pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, this.getClass())
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
    }

    /**
     *
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
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    /**
     *
     * @param intent
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

    private void startCountUp() {
        if (this.currentActivity.equals(this.actOne)) {
            this.actOneStart = System.nanoTime();
        } else if (this.currentActivity.equals(this.actTwo)) {
            this.actTwoStart = System.nanoTime();
        }
    }

    /**
     *
     * @param messages
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

                if (this.currentActivity.equals(this.actOne)) {
                    long duration = System.nanoTime() - this.actOneStart;
                    this.actOneDuration += duration;
                    double seconds = Math.round((double)this.actOneDuration / 1000000000.0);
                    this.actOneTime.setText(seconds + "");
                } else if (this.currentActivity.equals(this.actTwo)) {
                    long duration = System.nanoTime() - this.actTwoStart;
                    this.actTwoDuration += duration;
                    double seconds = Math.round((double)this.actTwoDuration / 1000000000.0);
                    this.actTwoTime.setText(seconds + "");
                }

                this.currentActivity = builder.toString().trim();
                this.text.setText(builder.toString().trim());
                this.startCountUp();
            } else {
                this.text.setText("still the same");
            }
        }
    }

    /**
     *
     */
    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

}
