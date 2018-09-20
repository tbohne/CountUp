package org.tbohne.countup;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {

    private NdefMessageParser() {}

    /**
     * Parses an NdefMessage.
     *
     * @param message - the message to be parsed
     * @return the message's parsed records
     */
    public static List<TextRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    /**
     * Returns the text records filtered from the array of NdefRecords.
     *
     * @param records - the array of NdefRecords to be filtered
     * @return the text records
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
