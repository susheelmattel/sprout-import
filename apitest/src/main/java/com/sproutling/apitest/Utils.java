package com.sproutling.apitest;

import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by bradylin on 11/22/16.
 */

public class Utils {

    public static final String SHARED_PREFERENCES_FILE = "account";

    public static String getTimeZoneInShort() {
        return TimeZone.getDefault().getDisplayName(TimeZone.getDefault().useDaylightTime(), TimeZone.SHORT);
    }

    public static String getLanguageWithCountry() {
        return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
    }
}
