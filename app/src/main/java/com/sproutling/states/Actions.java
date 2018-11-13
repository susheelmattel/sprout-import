/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

/**
 * Created by moi0312 on 2016/12/5.
 */

public class Actions {
    public static final int DATA_UPDATE = 100;

    public static final int STATUS_UPDATE = 200;
    public static final int DISMISS_ROLLOVER = 300;
    public static final int LIST_CHILD = 400;
    public static final int CALL_DIALOG = 500;
    public static final int CALL_NOTIFICATION = 600;
    public static final int RECONNECT_MQTT = 700;
    public static final int SCENE_EVENT = 800;
    public static final int SWITCH_SCENE = 801;

    public static final int GET_DEVICE_SERIAL = 888;

    public static final int GET_LEARNING_PERIOD_TIMESTAMP = 950;

    public static final int START_LEARNING_PERIOD = 960;

    public static final int SLEEP_PREDICTIONS = 1100;

    public static final int BAND_STATE = 1300;

    public static final int SLEEP_STATUS = 1400;

    public static final int HUB_PRESENCE = 1500;
    public static final int HUB_USER_CONFIG = 1600;

    public static final int BAND_ROLLOVER = 1700;

    public static final int HUB_CONTROL = 2000;
    public static final int SWITCH_NIGHT_LIGHT = 2002;
    public static final int SET_NIGHT_LIGHT_COLOR = 2003;
    public static final int SET_NIGHT_LIGHT_LEVEL = 2004;
    public static final int SWITCH_MUSIC_PLAY = 2005;
    public static final int SET_MUSIC_SONG = 2006;
    public static final int SET_MUSIC_VOL = 2007;

    public static final int TYPE_HUB_STATUS = 3000;

    public static class Key {
        public static final String SCENE = "scene";
        public static final String VIEW = "view";
        public static final String DIALOG = "dialog";
        public static final String NOTIFICATION = "notification";
        public static final String CHILD_ID = "childId";
        //		public static final String TIMESTAMP = "timeStamp";
        public static final String INDEX = "index";
        public static final String DISABLE_ROLLOVER_NOTIFICATION = "disableRolloverNotification";
        public static final String CONTEXT = "Context";

        public static final String IS_NIGHT_LIGHT_ON = "isNightLightOn";
        public static final String NIGHT_LIGHT_COLOR = "nightColor";
        public static final String NIGHT_LIGHT_COLOR_NAME = "nightColorName";
        public static final String NIGHT_LIGHT_LEVEL = "nightLightLevel";

        public static final String IS_MUSIC_PLAY = "isMusicPlay";
        public static final String MUSIC_SONG = "musicSong";
        public static final String MUSIC_SONG_NAME = "musicSongName";
        public static final String MUSIC_VOL = "musicVol";
    }
}

