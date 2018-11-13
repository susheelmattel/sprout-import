/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by subram13 on 4/5/17.
 */

public interface LogEvents {

    //    onboarding
    String SIGN_IN = "signIn";//done
    String SIGN_UP = "signUp";//done
    String SPROUTLING_VIDEO = "sproutlingVideo";//done
    String SPROUTLING_VIDEO_CLOSE = "sproutlingVideoClose";//done
    String DONT_HAVE_SPROUTLING = "dontHaveSproutling";//done
    String SETUP_NEW_SPROUTLING = "setupNewSproutling";//done
    String JOIN_EXISTING_FAMILY = "joinExistingFamily";//done
    String LEGAL_BACK_BUTTON = "legalBackButton";//done
    String LEGAL_I_ACCEPT = "legalIAccept";//done
    String TERMS_OF_SERVICE = "termsOfService";//done
    String PRIVACY_STATEMENT = "privacyStatement";//done
    String LOGGED_IN = "loggedIn";//done
    String APP_IN_FOREGROUND = "appInForeground";//done
    String CARE_GIVER_REGISTERED = "caregiverRegistered";//done
    String GUARDIAN_REGISTERED = "guardianRegistered";//done
    String ADDED_CHILD = "addedChild";//done
    String UPDATED_CHILD = "updatedChild";//done
    String HUB_PAIRING_SUCCESS = "hubPairingSuccess";//done
    String HUB_WIFI_AP_COUNT = "hubWifiApCount";//done
    String HUB_CONNECTED_TO_WIFI = "hubConnectedToWifi";//done
    String HUB_CONNECT_TO_WIFI_FAIL = "hubConnectToWiFiFail";//done
    String HUB_UPDATED_WIFI_SETTINGS = "hubUpdatedWifiSettings";//done
    String HUB_REGISTERED = "hubRegistered";//done
    String USER_REFUSED_NOTIFICATION_IN_APP = "userRefusedNotificationsInApp";//done
    String USER_SAID_OK_TO_NOTIFICATION_IN_APP = "userSaidOkToNotificationsInApp";//done
    String USER_DISMISSED_ONBOARDING_TUTORIAL = "userDismissedOnboardingTutorial";//done
    String USER_STARTED_ONBOARDING_TUTORIAL = "userStartedOnboardingTutorial";//done
    String TIME_ZONE_SENT = "timezone_Sent";//done
    String PUTTING_ON_WEARABLE_VIDEO = "puttingOnWearableVideo";//done
    String PUTTING_ON_WEARABLE_VIDEO_CLOSE = "puttingOnWearableVideoClose";//done
    String PUTTING_ON_STEP_BY_STEP = "puttingOnStepByStep";//done
    String PUTTING_ON_CLOSE = "puttingOnCloseDialog";//done
    String PUTTING_ON_NEXT = "puttingOnNext";//done
    String HOW_TO_CHARGE_VIDEO = "howToChargeVideo";//done
    String HOW_TO_CHARGE_NEXT = "howToChargeNext";//done
    String HOW_TO_CHARGE_STEP_BY_STEP = "howToChargeStepByStep";//done
    String HOW_TO_CHARGE_VIDEO_CLOSE = "howToChargeVideoClose";//done
    String HOW_TO_CHARGE_CLOSE = "howToChargeCloseDialog";//done


    //status card
    String STATUS_CARD_DETECTING = "statusCardDetecting";//done
    String STATUS_CARD_BATTERY_LEVEL = "statusCardBatteryLevel";//done
    String STATUS_CARD_HUB_OFFLINE = "statusCardHubOffline";//done
    String STATUS_CARD_HUB_ONLINE = "statusCardHubOnline";
    String STATUS_CARD_ASLEEP_NOTIFICATION = "statusCardAsleepNotification";//done
    String STATUS_CARD_AWAKE_NOTIFICATION = "statusCardAwakeNotification";//done
    String STATUS_CARD_STIRRING_NOTIFICATION = "statusCardStirringNotification";//done
    String STATUS_CARD_DISMISS_ROLLOVER = "statusCardDismissRollover";//done
    String STATUS_CARD_TURN_OFF_ROLLOVER_ALERTS = "statusCardTurnOffRolloverAlerts";//done
    String STATUS_CARD_UNUSUAL_HEART_RATE = "statusCardUnusualHeartRate";//done
    String STATUS_CARD_WEARABLE_CHARGING = "statusCardWearableCharging";//done
    String STATUS_CARD_WEARABLE_CHARGED = "statusCardWearableCharged";//done
    String STATUS_CARD_WEARABLE_FELL_OFF = "statusCardWearableFellOff";//done
    String STATUS_CARD_WEARABLE_NOT_FOUND = "statusCardWearableNotFound";//done
    String STATUS_CARD_WEARABLE_READY = "statusCardWearableReady";//done
    String STATUS_CARD_ROLLED_OVER = "statusCardRolledOver";//done
    String STATUS_CARD_WEARABLE_TOO_FAR_AWAY = "statusCardWearableTooFar";//done
    String STATUS_CARD_WEARABLE_OUT_OF_BATTERY = "statusCardWearableOutOfBattery";//done
    String STATUS_CARD_NOTIF_NO_SERVICE = "statusCardNotifNoService";//done
    String STATUS_CARD_NO_CONFIGURED_DEVICE = "statusCardNoConfiguredDevice";//done
    String STATUS_CARD_FIRMWARE_UPDATING = "statusCardFirmwareUpdating";//done
    String LEARNING_PERIOD_MODAL_CLOSE = "learningPeriodModalClose";//done

    //NAP
    String NAP_ENDED = "NAP Ended";//done
    String NAP_STARTED = "NAP Started";//done
    String NAP_RESET = "NAP Reset";//done
    String NAP_SAVED = "NAP Saved";//done
    String NAP_UPDATED = "napUpdated";//done
    String NAP_USER_TAPPED_CANCEL = "napUserTappedCancel";//done
    String NAP_USER_TAPPED_DISCARD = "napUserTappedDiscard";//done

    //Settings
    String SETTINGS_ACCOUNT_LOGGED_OUT = "settingsAccountLoggedOut";//done
    String SETTINGS_ACCOUNT_SAVED = "settingsAccountSaved";//done
    String SETTINGS_SELECTED = "settingsSelected";//done
    String SETTINGS_VIEWED = "settingsViewed";//done
    String SETTINGS_BLE_ALERT_UPDATE = "settingsBleAlertUpdate";//done
    String SETTINGS_WIFI_ALERT_UPDATE = "settingsWifiAlertUpdate";//done

    //Caregivers
    String CARE_GIVER_REMOVED = "caregiverRemoved";//done
    String CARE_GIVER_QR_CODE_EXPIRED = "caregiversQRCodeExpired";//done
    String CARE_GIVER_QR_CODE_GENERATED = "caregiversQRCodeGenerated";//done
    String CARE_GIVER_QR_CODE_PARSING_FAILED = "caregiversQRCodeParsingFailed";//done

    //LEARNING_PERIOD
    String LEARNING_PERIOD_STARTED = "Learning Period Started";//done
    String LEARNING_PERIOD_ABOUT = "learningPeriodAbout";//done
    String LEARNING_PERIOD_DURATION = "learningPeriodDuration";//done
    String LEARNING_PERIOD_ENDED = "learningPeriodEnded";//done
    String LEARNING_PERIOD_IMPROVING = "learningPeriodImproving";//done
    String LEARNING_PERIOD_SLEEP_PREDICTION = "learningPeriodSleepPrediction";//done
    String LEARNING_PERIOD_UNUSUAL_HEART_BEAT = "learningPeriodUnusualHeartbeat";//done
    String LEARNING_PERIOD_WHAT_TO_EXPECT = "learningPeriodWhatToExpect";//done

    //Timeline
    String TIMELINE_ADDED_NAP = "timelineAddedNap";//done
    String TIMELINE_COLLAPSED_WEEK_SUMMARY = "timelineCollapsedWeekSummary";//done
    String TIMELINE_EXPANDED_WEEK_SUMMARY = "timelineExpandedWeekSummary";//done
    String TIMELINE_NAP_SUMMARY_SCREEN_ROTATE = "timelineNapSummaryScreenRotate";//done
    String TIMELINE_NIGHT_SLEEP_SUMMARY_SCREEN_ROTATE = "timelineNightSleepSummaryScreenRotate";//done
    String TIMELINE_NO_DATA = "timelineNoData";//done
    String TIMELINE_SELECTED_TIP = "timelineSelectedTip";//done
    String TIMELINE_WHATIS = "timelineWhatIs";//done
    String TIMELINE_SELECTED_NAP = "timelineSelectedNap";//done

    //Hub Controls
    String HUB_CONTROLS_LIGHTS_OFF = "hubControlsLightOff";//done
    String HUB_CONTROLS_LIGHTS_ON = "hubControlsLightOn";//done
    String HUB_CONTROLS_MUSIC_PLAYING = "hubControlsMusicPlaying";//done
    String HUB_CONTROLS_MUSIC_STOPPED = "hubControlsMusicStopped";//done
    String HUB_CONTROLS_MUSIC_PAUSED = "hubControlsMusicPaused";
    String HUB_CONTROLS_MUSIC_VOLUME = "hubControlsMusicVolume";//done

    //Child Profile
    String CHILD_PROFILE_PHOTO_UPLOADED = "childprofilePhotoUploaded";//done

    String NO_PREDICTION_AVAILABLE = "noPredictionAvailable";//done


    String ONBOARDING_TIME_SPENT = "onBoardingTimeSpent";//done
    String TIP_CARD_CLICK = "tipCardClick";//done
    String EXPECTATIONS_TIME_SPENT = "expectationsTimeSpent";//done

    String INVALID_SERIAL_ID = "invalidSerialID";
    String START_SLEEP_TIMER = "startSleepTimer";
    String STOP_SLEEP_TIMER = "stopSleepTimer";
    String LOG_SLEEP_MANUALLY = "logSleepManually";
    String NAP_ADJUSTED = "napAdjusted";

    @StringDef({ADDED_CHILD, CARE_GIVER_REGISTERED, CARE_GIVER_QR_CODE_EXPIRED, CARE_GIVER_QR_CODE_GENERATED, CARE_GIVER_QR_CODE_PARSING_FAILED,
            CHILD_PROFILE_PHOTO_UPLOADED, GUARDIAN_REGISTERED, HUB_CONNECTED_TO_WIFI, STATUS_CARD_NOTIF_NO_SERVICE, HUB_PAIRING_SUCCESS,
            HUB_REGISTERED, HUB_WIFI_AP_COUNT, HUB_CONTROLS_LIGHTS_OFF, HUB_CONTROLS_LIGHTS_ON, HUB_CONTROLS_MUSIC_PLAYING, HUB_CONTROLS_MUSIC_STOPPED, HUB_CONTROLS_MUSIC_PAUSED, HUB_CONTROLS_MUSIC_VOLUME,
            HUB_UPDATED_WIFI_SETTINGS, LEARNING_PERIOD_STARTED, LEARNING_PERIOD_ABOUT, LEARNING_PERIOD_DURATION, LEARNING_PERIOD_ENDED, LEARNING_PERIOD_IMPROVING,
            LEARNING_PERIOD_SLEEP_PREDICTION, LEARNING_PERIOD_UNUSUAL_HEART_BEAT, LEARNING_PERIOD_WHAT_TO_EXPECT, LOGGED_IN, NAP_ENDED, NAP_STARTED, NAP_RESET, NAP_SAVED,
            NAP_UPDATED, NAP_USER_TAPPED_CANCEL, NAP_USER_TAPPED_DISCARD, SETTINGS_ACCOUNT_LOGGED_OUT, SETTINGS_ACCOUNT_SAVED, SETTINGS_SELECTED, SETTINGS_VIEWED, STATUS_CARD_BATTERY_LEVEL,
            STATUS_CARD_HUB_OFFLINE, STATUS_CARD_HUB_ONLINE, STATUS_CARD_ASLEEP_NOTIFICATION, STATUS_CARD_AWAKE_NOTIFICATION, STATUS_CARD_STIRRING_NOTIFICATION, STATUS_CARD_DISMISS_ROLLOVER,
            STATUS_CARD_TURN_OFF_ROLLOVER_ALERTS, STATUS_CARD_UNUSUAL_HEART_RATE, STATUS_CARD_WEARABLE_CHARGING, STATUS_CARD_WEARABLE_CHARGED, STATUS_CARD_WEARABLE_FELL_OFF,
            STATUS_CARD_WEARABLE_NOT_FOUND, STATUS_CARD_WEARABLE_READY, TIMELINE_ADDED_NAP, TIMELINE_COLLAPSED_WEEK_SUMMARY, TIMELINE_NAP_SUMMARY_SCREEN_ROTATE,
            TIMELINE_NIGHT_SLEEP_SUMMARY_SCREEN_ROTATE, TIMELINE_NO_DATA, TIMELINE_SELECTED_TIP, TIMELINE_WHATIS, TIMELINE_SELECTED_NAP, UPDATED_CHILD, USER_REFUSED_NOTIFICATION_IN_APP,
            USER_SAID_OK_TO_NOTIFICATION_IN_APP, USER_DISMISSED_ONBOARDING_TUTORIAL, USER_STARTED_ONBOARDING_TUTORIAL, CARE_GIVER_REMOVED, HUB_CONNECT_TO_WIFI_FAIL,
            STATUS_CARD_DETECTING, STATUS_CARD_ROLLED_OVER, STATUS_CARD_WEARABLE_TOO_FAR_AWAY, STATUS_CARD_WEARABLE_OUT_OF_BATTERY, TIMELINE_EXPANDED_WEEK_SUMMARY, APP_IN_FOREGROUND, TIME_ZONE_SENT,
            SETTINGS_BLE_ALERT_UPDATE, SETTINGS_WIFI_ALERT_UPDATE, STATUS_CARD_NO_CONFIGURED_DEVICE, STATUS_CARD_FIRMWARE_UPDATING, ONBOARDING_TIME_SPENT, NO_PREDICTION_AVAILABLE, EXPECTATIONS_TIME_SPENT,
            TIP_CARD_CLICK, SIGN_IN, SIGN_UP, SPROUTLING_VIDEO, DONT_HAVE_SPROUTLING, SETUP_NEW_SPROUTLING, JOIN_EXISTING_FAMILY, LEGAL_BACK_BUTTON, LEGAL_I_ACCEPT, TERMS_OF_SERVICE, PRIVACY_STATEMENT,
            PUTTING_ON_WEARABLE_VIDEO, PUTTING_ON_STEP_BY_STEP, PUTTING_ON_CLOSE, PUTTING_ON_NEXT, SPROUTLING_VIDEO_CLOSE, PUTTING_ON_WEARABLE_VIDEO_CLOSE, HOW_TO_CHARGE_VIDEO, HOW_TO_CHARGE_NEXT,
            HOW_TO_CHARGE_STEP_BY_STEP, HOW_TO_CHARGE_VIDEO_CLOSE, HOW_TO_CHARGE_CLOSE, INVALID_SERIAL_ID, LEARNING_PERIOD_MODAL_CLOSE, START_SLEEP_TIMER, STOP_SLEEP_TIMER, LOG_SLEEP_MANUALLY, NAP_ADJUSTED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Events {

    }
}
