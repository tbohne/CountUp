package org.tbohne.countup;

import android.annotation.SuppressLint;
import android.nfc.NdefRecord;
import android.support.v4.util.Preconditions;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class TextRecord {

    private final String text;

    /**
     * TextRecord constructor
     *
     * @param text - the text record's content
     */
    @SuppressLint("RestrictedApi")
    public TextRecord(String text) {
        this.text = Preconditions.checkNotNull(text);
    }

    /**
     * Returns the content of the text record.
     *
     * @return the text record's content
     */
    public String str() {
        return this.text;
    }

    /**
     * Parses the NdefRecord and creates the text record.
     *
     * @param record - the record to be parsed
     * @return the created text record
     */
    @SuppressLint("RestrictedApi")
    public static TextRecord parse(NdefRecord record) {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_TEXT));
        try {
            byte[] payload = record.getPayload();
            String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0077;
            String text = new String(
                    payload,
                    languageCodeLength + 1,
                    payload.length - languageCodeLength - 1,
                    textEncoding
            );
            return new TextRecord(text);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns whether the NdefRecord is parsable as text.
     *
     * @param record - the NdefRecord to be parsed
     * @return true --> parsable as text / false --> not parsable as text
     */
    public static boolean isText(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
