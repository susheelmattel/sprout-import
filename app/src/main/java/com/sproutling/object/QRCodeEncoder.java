/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.object;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by bradylin on 1/31/17.
 */

public final class QRCodeEncoder {
    private static final int COLOR_BACKGROUND = 0xFFF7F7F7;//0xFFFFFFFF;
    private static final int COLOR_CONTENT = 0xFF2DA9A0;//0xFF000000;

    private int mDimension = Integer.MIN_VALUE;
    private String mContents = null;
    private String mDisplayContents = null;
    private String mTitle = null;
    private BarcodeFormat mFormat = null;
    private boolean mEncoded = false;

    public QRCodeEncoder(String data, Bundle bundle, String type, String format, int dimension) {
        mDimension = dimension;
        mEncoded = encodeContents(data, bundle, type, format);
    }

    public String getContents() {
        return mContents;
    }

    public String getDisplayContents() {
        return mDisplayContents;
    }

    public String getTitle() {
        return mTitle;
    }

    private boolean encodeContents(String data, Bundle bundle, String type, String formatString) {
        // Default to QR_CODE if no mFormat given.
        mFormat = null;
        if (formatString != null) {
            try {
                mFormat = BarcodeFormat.valueOf(formatString);
            } catch (IllegalArgumentException iae) {
                // Ignore it then
            }
        }
        if (mFormat == null || mFormat == BarcodeFormat.QR_CODE) {
            mFormat = BarcodeFormat.QR_CODE;
            encodeQRCodeContents(data, bundle, type);
        } else if (data != null && data.length() > 0) {
            mContents = data;
            mDisplayContents = data;
            mTitle = "Text";
        }
        return mContents != null && mContents.length() > 0;
    }

    private void encodeQRCodeContents(String data, Bundle bundle, String type) {
        switch (type) {
            case QRCodeContents.Type.TEXT:
                if (data != null && data.length() > 0) {
                    mContents = data;
                    mDisplayContents = data;
                    mTitle = "Text";
                }
                break;
            case QRCodeContents.Type.EMAIL:
                data = trim(data);
                if (data != null) {
                    mContents = "mailto:" + data;
                    mDisplayContents = data;
                    mTitle = "E-Mail";
                }
                break;
            case QRCodeContents.Type.PHONE:
                data = trim(data);
                if (data != null) {
                    mContents = "tel:" + data;
                    mDisplayContents = PhoneNumberUtils.formatNumber(data);
                    mTitle = "Phone";
                }
                break;
            case QRCodeContents.Type.SMS:
                data = trim(data);
                if (data != null) {
                    mContents = "sms:" + data;
                    mDisplayContents = PhoneNumberUtils.formatNumber(data);
                    mTitle = "SMS";
                }
                break;
            case QRCodeContents.Type.CONTACT:
                if (bundle != null) {
                    StringBuilder newContents = new StringBuilder(100);
                    StringBuilder newDisplayContents = new StringBuilder(100);

                    newContents.append("MECARD:");

                    String name = trim(bundle.getString(ContactsContract.Intents.Insert.NAME));
                    if (name != null) {
                        newContents.append("N:").append(escapeMECARD(name)).append(';');
                        newDisplayContents.append(name);
                    }

                    String address = trim(bundle.getString(ContactsContract.Intents.Insert.POSTAL));
                    if (address != null) {
                        newContents.append("ADR:").append(escapeMECARD(address)).append(';');
                        newDisplayContents.append('\n').append(address);
                    }

                    Collection<String> uniquePhones = new HashSet<String>(QRCodeContents.PHONE_KEYS.length);
                    for (int x = 0; x < QRCodeContents.PHONE_KEYS.length; x++) {
                        String phone = trim(bundle.getString(QRCodeContents.PHONE_KEYS[x]));
                        if (phone != null) {
                            uniquePhones.add(phone);
                        }
                    }
                    for (String phone : uniquePhones) {
                        newContents.append("TEL:").append(escapeMECARD(phone)).append(';');
                        newDisplayContents.append('\n').append(PhoneNumberUtils.formatNumber(phone));
                    }

                    Collection<String> uniqueEmails = new HashSet<String>(QRCodeContents.EMAIL_KEYS.length);
                    for (int x = 0; x < QRCodeContents.EMAIL_KEYS.length; x++) {
                        String email = trim(bundle.getString(QRCodeContents.EMAIL_KEYS[x]));
                        if (email != null) {
                            uniqueEmails.add(email);
                        }
                    }
                    for (String email : uniqueEmails) {
                        newContents.append("EMAIL:").append(escapeMECARD(email)).append(';');
                        newDisplayContents.append('\n').append(email);
                    }

                    String url = trim(bundle.getString(QRCodeContents.URL_KEY));
                    if (url != null) {
                        // escapeMECARD(url) -> wrong escape e.g. http\://zxing.google.com
                        newContents.append("URL:").append(url).append(';');
                        newDisplayContents.append('\n').append(url);
                    }

                    String note = trim(bundle.getString(QRCodeContents.NOTE_KEY));
                    if (note != null) {
                        newContents.append("NOTE:").append(escapeMECARD(note)).append(';');
                        newDisplayContents.append('\n').append(note);
                    }

                    // Make sure we've mEncoded at least one field.
                    if (newDisplayContents.length() > 0) {
                        newContents.append(';');
                        mContents = newContents.toString();
                        mDisplayContents = newDisplayContents.toString();
                        mTitle = "Contact";
                    } else {
                        mContents = null;
                        mDisplayContents = null;
                    }

                }
                break;
            case QRCodeContents.Type.LOCATION:
                if (bundle != null) {
                    // These must use Bundle.getFloat(), not getDouble(), it's part of the API.
                    float latitude = bundle.getFloat("LAT", Float.MAX_VALUE);
                    float longitude = bundle.getFloat("LONG", Float.MAX_VALUE);
                    if (latitude != Float.MAX_VALUE && longitude != Float.MAX_VALUE) {
                        mContents = "geo:" + latitude + ',' + longitude;
                        mDisplayContents = latitude + "," + longitude;
                        mTitle = "Location";
                    }
                }
                break;
        }
    }

    public Bitmap encodeAsBitmap() throws WriterException {
        if (!mEncoded) return null;

        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(mContents);
        if (encoding != null) {
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(mContents, mFormat, mDimension, mDimension, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? COLOR_CONTENT : COLOR_BACKGROUND;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private static String trim(String s) {
        if (s == null) {
            return null;
        }
        String result = s.trim();
        return result.length() == 0 ? null : result;
    }

    private static String escapeMECARD(String input) {
        if (input == null || (input.indexOf(':') < 0 && input.indexOf(';') < 0)) {
            return input;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == ':' || c == ';') {
                result.append('\\');
            }
            result.append(c);
        }
        return result.toString();
    }
}
