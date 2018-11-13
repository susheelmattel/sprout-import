/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

import android.content.Context;

import com.fuhu.states.State;
import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateReducer;
import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.ui.fragment.status.DrawerLayoutFragment;

import java.util.ArrayList;

import sproutling.Hub;

/**
 * Created by moi0312 on 2017/1/25.
 */

public class MusicAndLightReducer implements IStateReducer {
    @Override
    public IState reduce(Action action, IState state) {
        Payload actPayload = (Payload) action.payload(); //action's payload
        final Payload statePayload = (Payload) state.data(); //state's paypoad
        Payload resultPayload = new Payload();

        Object dispatcher = action.getDispatcher();
        Context context = null;

        if (dispatcher instanceof StatusActivity) {
            context = (StatusActivity) dispatcher;
        } else if (dispatcher instanceof DrawerLayoutFragment) {
            context = ((DrawerLayoutFragment) dispatcher).getActivity();
        }

        int nightLightColor;
        int nightLightBrightness;
        int musicVolume;
        boolean isMusicPlay;

        switch (action.getType()) {

            case Actions.SWITCH_NIGHT_LIGHT:
                //get data from payload
                nightLightColor = actPayload.getInt(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.WHITE);
                nightLightBrightness = actPayload.getInt(Actions.Key.NIGHT_LIGHT_LEVEL, 1000);
                boolean switchCheck = actPayload.getBoolean(Actions.Key.IS_NIGHT_LIGHT_ON, false);

                //return a state with changed data
                resultPayload.put(Actions.Key.IS_NIGHT_LIGHT_ON, switchCheck);

                if (context != null) {
                    if (context instanceof StatusActivity) {

                        if (switchCheck) {
                            ((StatusActivity) context).publishTurnOnLight(nightLightColor, nightLightBrightness);
                        } else {
                            ((StatusActivity) context).publishTurnOffLight();
                        }
                    }
                }
                return new State(resultPayload);//new State(new Payload().put(States.Key.IS_NIGHT_LIGHT_ON, switchCheck))

            case Actions.SET_NIGHT_LIGHT_COLOR:
                //get data from payload
                nightLightColor = actPayload.getInt(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.WHITE);
                nightLightBrightness = actPayload.getInt(Actions.Key.NIGHT_LIGHT_LEVEL, 1000);
                String nightLightColorName = actPayload.getString(Actions.Key.NIGHT_LIGHT_COLOR_NAME);

                //return a state with changed data
                resultPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, nightLightColor);
                resultPayload.put(Actions.Key.NIGHT_LIGHT_COLOR_NAME, nightLightColorName);

                if (context != null) {
                    if (context instanceof StatusActivity) {
                        ((StatusActivity) context).publishTurnOnLight(nightLightColor, nightLightBrightness);
                    }
                }
                return new State(resultPayload);

            case Actions.SET_NIGHT_LIGHT_LEVEL:
                //get data from payload
                nightLightColor = actPayload.getInt(Actions.Key.NIGHT_LIGHT_COLOR, States.NightLightValue.WHITE);
                nightLightBrightness = actPayload.getInt(Actions.Key.NIGHT_LIGHT_LEVEL, 1000);

                //return a state with changed data
                resultPayload.put(Actions.Key.NIGHT_LIGHT_COLOR, nightLightColor);
                resultPayload.put(Actions.Key.NIGHT_LIGHT_LEVEL, nightLightBrightness);

                if (context != null) {
                    if (context instanceof StatusActivity) {
                        ((StatusActivity) context).publishTurnOnLight(nightLightColor, nightLightBrightness);
                    }
                }
                return new State(resultPayload);

            case Actions.SWITCH_MUSIC_PLAY:
                //get data from payload
                isMusicPlay = actPayload.getBoolean(Actions.Key.IS_MUSIC_PLAY, false);

                //return a state with changed data
                resultPayload.put(Actions.Key.IS_MUSIC_PLAY, isMusicPlay);

                if (!isMusicPlay) { //This music play status is after switch change.
                    if (context != null) {
                        if (context instanceof StatusActivity) {
                            ((StatusActivity) context).publishStopMusic();
                        }
                    }
                } else {
                    Payload mPayload = new Payload();
//                    mPayload.put(Actions.Key.MUSIC_SONG_NAME, context.getString(R.string.hsl_twinkle_twinkle_little_star));
                    mPayload.put(Actions.Key.MUSIC_VOL, actPayload.getInt(Actions.Key.MUSIC_VOL, 57));
                    ArrayList<String> songs = new ArrayList<String>() {{
                        add(States.SongValue.TWINKLE_TWINKLE_LITTLE_STAR);
                    }};
                    ArrayList<String> selectedSongs = (ArrayList<String>) actPayload.get(Actions.Key.MUSIC_SONG);
                    if (selectedSongs != null && !selectedSongs.isEmpty()) {
                        songs = selectedSongs;
                    }
                    mPayload.put(Actions.Key.MUSIC_SONG, songs);
                    App.getInstance().dispatchAction(Actions.SET_MUSIC_SONG, mPayload, context);
                }
                return new State(resultPayload);

            case Actions.SET_MUSIC_SONG:
                //get data from payload
                ArrayList<String> musicSongs = (ArrayList<String>) actPayload.get(Actions.Key.MUSIC_SONG);
                String musicSongName = actPayload.getString(Actions.Key.MUSIC_SONG_NAME);
                musicVolume = actPayload.getInt(Actions.Key.MUSIC_VOL, 40);

                //return a state with changed data
                resultPayload.put(Actions.Key.MUSIC_SONG, musicSongs);
                resultPayload.put(Actions.Key.MUSIC_SONG_NAME, musicSongName);
                resultPayload.put(Actions.Key.MUSIC_VOL, musicVolume);
                resultPayload.put(Actions.Key.IS_MUSIC_PLAY, true);

                if (context != null) {
                    if (context instanceof StatusActivity) {
                        ((StatusActivity) context).publishPlayMusic(musicSongs, musicVolume);
                    }
                }
                return new State(resultPayload);

            case Actions.SET_MUSIC_VOL:
                //get data from payload
                musicVolume = actPayload.getInt(Actions.Key.MUSIC_VOL, 40);
                isMusicPlay = actPayload.getBoolean(Actions.Key.IS_MUSIC_PLAY, false);
                //return a state with changed data
                resultPayload.put(Actions.Key.MUSIC_VOL, musicVolume);
                resultPayload.put(Actions.Key.IS_MUSIC_PLAY, isMusicPlay);

                if (isMusicPlay) {
                    if (context != null) {
                        if (context instanceof StatusActivity) {
                            ((StatusActivity) context).publishUpdateVolume(musicVolume);
                        }
                    }
                } else {
                    Payload mPayload = new Payload();
                    ArrayList<String> songs = new ArrayList<String>() {{
                        add(States.SongValue.TWINKLE_TWINKLE_LITTLE_STAR);
                    }};
                    ArrayList<String> selectedSongs = (ArrayList<String>) actPayload.get(Actions.Key.MUSIC_SONG);
                    if (selectedSongs != null && !selectedSongs.isEmpty()) {
                        songs = selectedSongs;
                    }
                    mPayload.put(Actions.Key.MUSIC_VOL, musicVolume);
                    mPayload.put(Actions.Key.MUSIC_SONG, songs);
                    App.getInstance().dispatchAction(Actions.SET_MUSIC_SONG, mPayload, context);
                }
                return new State(resultPayload);


            case Actions.TYPE_HUB_STATUS:
                //get data from payload
                Hub.HubStatus hubStatus = (Hub.HubStatus) actPayload.get(States.Key.MQTT_HUB_STATUS);

                //return a state with changed data
                resultPayload.put(States.Key.MQTT_HUB_STATUS, hubStatus);

                return new State(resultPayload);

            case Actions.HUB_CONTROL:
                //get data from payload
                Hub.HubControl hubControl = (Hub.HubControl) actPayload.get(States.Key.MQTT_HUB_CONTROL);

                //return a state with changed data
                resultPayload.put(States.Key.MQTT_HUB_CONTROL, hubControl);

                return new State(resultPayload);
            default:
                //if no data need to be change, return null
                return null;
        }
    }
}
