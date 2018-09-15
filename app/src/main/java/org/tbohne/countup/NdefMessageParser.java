package org.tbohne.countup;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {

    private NdefMessageParser() {}

    /**
     *
     * @param message
     * @return
     */
    public static List<TextRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    /**
     *
     * @param records
     * @return
     */
    public static List<TextRecord> getRecords(NdefRecord[] records) {
        List<TextRecord> elements = new ArrayList<>();

        for (final NdefRecord record : records) {
            if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            }
        }
        return elements;
    }
}
