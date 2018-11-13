/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.utils;

import android.util.Log;

import java.util.Calendar;

/**
 * Created by Simon on 2016/6/28.
 */
public class GrowthUtil {
    public static final String TAG = GrowthUtil.class.getSimpleName();

    public static final int TYPE_LENGTH = 1;
    public static final int TYPE_WEIGHT = 2;
    public static final int TYPE_HEAD = 3;

    public static final int GENDER_BOY = 1;
    public static final int GENDER_GIRL = 2;

    public static final int DURATION_INVALID = -1;
    public static final int DURATION_DAY = 1;
    public static final int DURATION_WEEK = 2;
    public static final int DURATION_MONTH = 3;

    public static final int PERCENTILE_2ND = 2;
    public static final int PERCENTILE_25TH = 25;
    public static final int PERCENTILE_50TH = 50;
    public static final int PERCENTILE_75TH = 75;
    public static final int PERCENTILE_98TH = 98;

    // if last data time - first time data > 13 weeks , use month data (in ms)
    public static final long DAY_IN_MS = 24 * 60 * 60 * 1000;

    public static final long DAY_WEEK_SWITCH_GAP_IN_DAY = 7;
    public static final long DAY_WEEK_SWITCH_GAP_IN_MS = DAY_WEEK_SWITCH_GAP_IN_DAY * DAY_IN_MS;

    public static final long WEEK_MONTH_SWITCH_GAP_IN_DAY = 7 * 12;//7*13
    public static final long WEEK_MONTH_SWITCH_GAP_IN_MS = WEEK_MONTH_SWITCH_GAP_IN_DAY * DAY_IN_MS;

    private static final float CM_2_IN = 0.39f;
    private static final float KG_2_LB = 2.20f;

    private static final float IN_2_CM = 2.54f;
    private static final float LB_2_KG = 0.45f;

    //WHO data here , length is cm, weight is kg
    //reference http://www.who.int/childgrowth/standards/technical_report/en/

    private static final int DAY_DATA_MAX = 7;//1-7
    private static final int WEEK_DATA_MAX = 13;//0-12 //14,0-13
    private static final int MONTH_DATA_MAX = 25;//0-24

    //day data interval : 0-7days
    private static final float BOY_DAY_LENGTH_3RD[] = {46.3f, 46.5f, 46.7f, 46.9f, 47.1f, 47.3f, 47.5f};
    private static final float BOY_DAY_LENGTH_97TH[] = {53.4f, 53.6f, 53.8f, 54.0f, 54.2f, 54.4f, 54.7f};

    private static final float BOY_DAY_WEIGHT_3RD[] = {2.5f, 2.516f, 2.532f, 2.548f, 2.564f, 2.580f, 2.6f};
    private static final float BOY_DAY_WEIGHT_97TH[] = {4.3f, 4.333f, 4.366f, 4.399f, 4.432f, 4.465f, 4.5f};

    private static final float GIRL_DAY_LENGTH_3RD[] = {45.6f, 45.8f, 46.0f, 46.2f, 46.4f, 46.6f, 46.8f};
    private static final float GIRL_DAY_LENGTH_97TH[] = {52.7f, 52.9f, 53.1f, 53.3f, 53.5f, 53.7f, 53.9f};

    private static final float GIRL_DAY_WEIGHT_3RD[] = {2.4f, 2.416f, 2.432f, 2.448f, 2.464f, 2.480f, 2.5f};
    private static final float GIRL_DAY_WEIGHT_97TH[] = {4.2f, 4.233f, 4.266f, 4.299f, 4.332f, 4.365f, 4.4f};

    //week data interval : 0-12weeks //0-13weeks
    private static final float BOY_WEEK_LENGTH_3RD[] = {46.3f, 47.5f, 48.8f, 49.8f, 50.7f, 51.7f, 52.5f, 53.4f, 54.1f, 54.9f, 55.6f, 56.3f, 56.9f, 57.6f};
    private static final float BOY_WEEK_LENGTH_97TH[] = {53.4f, 54.7f, 55.9f, 57.0f, 58.0f, 59.0f, 59.9f, 60.8f, 61.6f, 62.4f, 63.2f, 63.9f, 64.6f, 65.2f};

    private static final float BOY_WEEK_WEIGHT_3RD[] = {2.5f, 2.6f, 2.8f, 3.1f, 3.4f, 3.6f, 3.8f, 4.1f, 4.3f, 4.4f, 4.6f, 4.8f, 4.9f, 5.1f};
    private static final float BOY_WEEK_WEIGHT_97TH[] = {4.3f, 4.5f, 4.9f, 5.2f, 5.6f, 5.9f, 6.3f, 6.5f, 6.8f, 7.1f, 7.3f, 7.5f, 7.7f, 7.9f};

    private static final float GIRL_WEEK_LENGTH_3RD[] = {45.6f, 46.8f, 47.9f, 48.8f, 49.7f, 50.5f, 51.3f, 52.1f, 52.8f, 53.4f, 54.1f, 54.7f, 55.3f, 55.8f};
    private static final float GIRL_WEEK_LENGTH_97TH[] = {52.7f, 53.9f, 55.1f, 56.1f, 57.0f, 57.9f, 58.8f, 59.6f, 60.4f, 61.1f, 61.8f, 62.5f, 63.1f, 63.7f};

    private static final float GIRL_WEEK_WEIGHT_3RD[] = {2.4f, 2.5f, 2.7f, 2.9f, 3.1f, 3.3f, 3.5f, 3.7f, 3.9f, 4.1f, 4.2f, 4.3f, 4.5f, 4.6f};
    private static final float GIRL_WEEK_WEIGHT_97TH[] = {4.2f, 4.4f, 4.6f, 5.0f, 5.3f, 5.6f, 5.9f, 6.1f, 6.4f, 6.6f, 6.8f, 7.0f, 7.2f, 7.4f};

    //month data interval : 0-24month
    //there is a gap between table 0-24month and 24-60month , so use 0-24 month data only
    //new here ,from https://www.cdc.gov/growthcharts/who_charts.htm
    private static final float BOY_MONTH_LENGTH_3RD[] = {46.1f, 50.8f, 54.4f, 57.3f, 59.7f, 61.7f, 63.3f, 64.8f, 66.2f, 67.5f,
            68.7f, 69.9f, 71.0f, 72.0f, 73.1f, 74.1f, 75.0f, 76.0f, 76.9f, 77.7f,
            78.6f, 79.4f, 80.2f, 81.0f, 81.7f};

    private static final float BOY_MONTH_LENGTH_97TH[] = {53.7f, 58.6f, 62.4f, 65.5f, 68.0f, 70.1f, 71.9f, 73.5f, 75.0f, 76.5f, 77.9f, 79.2f, 80.5f,
            81.8f, 83.0f, 84.2f, 85.4f, 86.5f, 87.7f, 88.8f, 89.8f, 90.9f, 91.9f, 92.9f, 93.9f};

    private static final float BOY_MONTH_LENGTH_25TH[] = {48.6f, 53.4f, 57.1f, 60.1f, 62.5f, 64.5f, 66.2f, 67.7f, 69.1f, 70.5f, 71.7f, 73.0f, 74.1f,
            75.3f, 76.4f, 77.4f, 78.5f, 79.5f, 80.4f, 81.4f, 82.3f, 83.2f, 84.1f, 84.9f, 85.8f};

    private static final float BOY_MONTH_LENGTH_50TH[] = {49.9f, 54.7f, 58.4f, 61.4f, 63.9f, 65.9f, 67.6f, 69.2f, 70.6f, 72.0f, 73.3f, 74.5f, 75.7f,
            76.9f, 78.0f, 79.1f, 80.2f, 81.2f, 82.3f, 83.2f, 84.2f, 58.1f, 86.0f, 86.9f, 87.8f};

    private static final float BOY_MONTH_LENGTH_75TH[] = {51.2f, 56.0f, 59.8f, 62.8f, 65.3f, 67.3f, 69.0f, 70.6f, 72.0f, 73.5f, 74.8f, 76.1f, 77.4f,
            78.6f, 79.7f, 80.8f, 82.0f, 83.0f, 84.1f, 85.1f, 86.1f, 87.7f, 88.0f, 89.0f, 89.9f,};

    private static final float BOY_MONTH_WEIGHT_3RD[] = {2.46f, 3.39f, 4.32f, 5.02f, 5.56f, 6.00f, 6.35f, 6.65f, 6.91f, 7.14f,
            7.36f, 7.55f, 7.74f, 7.92f, 8.10f, 8.27f, 8.43f, 8.59f, 8.75f, 8.91f,
            9.07f, 9.22f, 9.37f, 9.52f, 9.67f};
    private static final float BOY_MONTH_WEIGHT_97TH[] = {4.42f, 5.80f, 7.09f, 8.02f, 8.75f, 9.34f, 9.85f, 10.29f, 10.68f, 11.04f,
            11.37f, 11.69f, 11.99f, 12.28f, 12.56f, 12.84f, 13.11f, 13.38f, 13.66f, 13.93f,
            14.19f, 14.46f, 14.74f, 15.01f, 15.28f};

    private static final float BOY_MONTH_WEIGHT_25TH[] = {3.02f, 4.08f, 5.12f, 5.89f, 6.48f, 6.97f, 7.37f, 7.7f, 8.0f, 8.27f, 8.51f, 8.74f, 8.96f,
            9.17f, 9.37f, 9.57f, 9.77f, 9.96f, 10.15f, 10.33f, 10.52f, 10.7f, 10.89f, 11.07f, 11.25f};

    private static final float BOY_MONTH_WEIGHT_50TH[] = {3.34f, 4.47f, 5.57f, 6.38f, 7.0f, 7.51f, 7.93f, 8.3f, 8.62f, 8.9f, 9.16f, 9.41f, 9.65f,
            9.87f, 10.1f, 10.31f, 10.52f, 10.73f, 10.94f, 11.14f, 11.35f, 11.55f, 11.75f, 11.95f, 12.15f};

    private static final float BOY_MONTH_WEIGHT_75TH[] = {3.69f, 4.89f, 6.05f, 6.9f, 7.55f, 8.1f, 8.54f, 8.93f, 9.27f, 9.58f, 9.86f, 10.13f, 10.38f,
            10.63f, 10.87f, 11.1f, 11.34f, 11.56f, 11.79f, 12.01f, 12.24f, 12.46f, 12.68f, 12.9f, 13.13f};

    private static final float BOY_MONTH_HEAD_2ND[] = {31.9f, 34.9f, 36.8f, 38.1f, 39.2f, 40.1f, 40.9f, 41.5f, 42.0f, 42.5f, 42.9f, 43.2f,
            43.5f, 43.8f, 44.0f, 44.2f, 44.4f, 44.6f, 44.7f, 44.9f, 45.0f, 45.2f, 45.3f, 45.4f, 45.5f};

    private static final float BOY_MONTH_HEAD_25TH[] = {33.6f, 36.5f, 38.3f, 39.7f, 40.8f, 41.7f, 42.5f, 43.1f, 43.7f, 44.2f, 44.6f, 44.9f,
            45.2f, 45.5f, 45.7f, 45.9f, 46.1f, 46.3f, 46.5f, 46.6f, 46.8f, 46.9f, 47.1f, 47.2f, 47.3f};

    private static final float BOY_MONTH_HEAD_50TH[] = {34.5f, 37.3f, 39.1f, 40.5f, 41.6f, 42.6f, 43.3f, 44.0f, 44.5f, 45.0f, 45.4f, 45.8f,
            46.1f, 46.3f, 46.6f, 46.8f, 47.0f, 47.2f, 47.4f, 47.5f, 47.7f, 47.8f, 48.0f, 48.1f, 48.3f};

    private static final float BOY_MONTH_HEAD_75TH[] = {35.3f, 38.0f, 39.9f, 41.3f, 42.4f, 43.4f, 43.4f, 44.2f, 44.8f, 45.4f, 46.3f, 46.6f, 46.9f,
            47.2f, 47.5f, 47.7f, 47.9f, 48.1f, 48.3f, 48.4f, 48.6f, 48.7f, 48.9f, 49.0f, 49.2f};

    private static final float BOY_MONTH_HEAD_98TH[] = {37.0f, 39.6f, 41.5f, 42.9f, 44.0f, 45.0f, 45.8f, 46.4f, 47.0f, 47.5f, 47.9f, 48.3f,
            48.6f, 48.9f, 49.2f, 49.4f, 49.6f, 49.8f, 50.0f, 50.2f, 50.4f, 50.5f, 50.7f, 50.8f, 51.0f};

    private static final float GIRL_MONTH_LENGTH_3RD[] = {45.4f, 49.8f, 53.0f, 55.6f, 57.8f, 59.6f, 61.2f, 62.7f, 64.0f, 65.3f,
            66.5f, 67.7f, 68.9f, 70.0f, 71.0f, 72.0f, 73.0f, 74.0f, 74.9f, 75.8f,
            76.7f, 77.5f, 78.4f, 79.2f, 80.0f};

    private static final float GIRL_MONTH_LENGTH_97TH[] = {52.9f, 57.6f, 61.1f, 64.0f, 66.4f, 68.5f, 70.3f, 72.0f, 73.5f, 75.0f,
            76.4f, 77.8f, 79.2f, 80.5f, 81.7f, 83.0f, 84.2f, 85.4f, 86.5f, 87.6f,
            88.7f, 89.8f, 90.8f, 91.9f, 92.9f};

    private static final float GIRL_MONTH_LENGTH_25TH[] = {47.9f, 52.4f, 55.7f, 58.4f, 60.6f, 62.5f, 64.2f, 65.7f, 67.2f, 68.5f, 69.8f, 71.1f,
            72.3f, 73.4f, 74.6f, 75.7f, 76.7f, 77.7f, 78.7f, 79.7f, 80.7f, 81.6f, 82.5f, 83.4f, 84.2f};

    private static final float GIRL_MONTH_LENGTH_50TH[] = {49.1f, 53.7f, 57.1f, 59.8f, 62.1f, 64.0f, 65.7f, 67.3f, 68.7f, 70.1f, 71.5f, 72.8f,
            74.0f, 75.2f, 76.4f, 77.5f, 78.6f, 79.7f, 80.7f, 81.7f, 82.7f, 83.7f, 84.6f, 85.5f, 86.4f};

    private static final float GIRL_MONTH_LENGTH_75TH[] = {50.4f, 55.0f, 58.4f, 61.2f, 63.5f, 65.5f, 67.3f, 68.8f, 70.3f, 71.8f, 73.1f, 74.5f,
            75.8f, 77.0f, 78.2f, 79.4f, 80.5f, 81.6f, 82.7f, 83.7f, 84.7f, 85.7f, 86.7f, 87.7f, 88.6f};

    private static final float GIRL_MONTH_WEIGHT_3RD[] = {2.39f, 3.16f, 3.94f, 4.53f, 5.01f, 5.40f, 5.73f, 6.01f, 6.25f, 6.47f,
            6.67f, 6.86f, 7.04f, 7.22f, 7.39f, 7.56f, 7.73f, 7.89f, 8.06f, 8.22f,
            8.39f, 8.55f, 8.71f, 8.87f, 9.04f};

    private static final float GIRL_MONTH_WEIGHT_97TH[] = {4.23f, 5.48f, 6.63f, 7.51f, 8.23f, 8.83f, 9.34f, 9.78f, 10.18f, 10.55f,
            10.89f, 11.20f, 11.51f, 11.80f, 12.09f, 12.37f, 12.65f, 12.92f, 13.20f, 13.47f,
            13.74f, 14.02f, 14.29f, 14.57f, 14.85f};

    private static final float GIRL_MONTH_WEIGHT_25TH[] = {2.93f, 3.81f, 4.7f, 5.37f, 5.91f, 6.35f, 6.72f, 7.04f, 7.32f, 7.58f, 7.81f, 8.03f,
            8.24f, 8.45f, 8.65f, 8.84f, 9.04f, 9.23f, 9.42f, 9.62f, 9.81f, 10.0f, 10.19f, 10.38f, 10.57f};

    private static final float GIRL_MONTH_WEIGHT_50TH[] = {3.23f, 4.19f, 5.13f, 5.85f, 6.42f, 6.9f, 7.3f, 7.64f, 7.95f, 8.23f, 8.48f, 8.72f,
            8.95f, 9.17f, 9.39f, 9.6f, 9.81f, 10.02f, 10.23f, 10.44f, 10.65f, 10.85f, 11.06f, 11.27f, 11.48f};

    private static final float GIRL_MONTH_WEIGHT_75TH[] = {3.55f, 4.59f, 5.6f, 6.36f, 6.98f, 7.5f, 7.93f, 8.3f, 8.63f, 8.93f, 9.21f, 9.48f,
            9.73f, 9.97f, 10.21f, 10.44f, 10.67f, 10.9f, 11.13f, 11.35f, 11.58f, 11.81f, 12.03f, 12.26f, 12.49f};

    private static final float GIRL_MONTH_HEAD_2ND[] = {31.5f, 34.2f, 35.8f, 37.0f, 38.1f, 38.9f, 39.6f, 40.2f, 40.7f, 41.2f, 41.5f, 41.9f,
            42.2f, 42.4f, 42.7f, 42.9f, 43.1f, 43.3f, 43.5f, 43.6f, 43.8f, 44.0f, 44.1f, 44.3f, 44.4f};

    private static final float GIRL_MONTH_HEAD_25TH[] = {33.1f, 35.8f, 37.4f, 38.7f, 39.7f, 40.6f, 41.3f, 41.9f, 42.5f, 42.9f, 43.3f, 43.7f,
            44.0f, 44.2f, 44.5f, 44.7f, 44.9f, 45.1f, 45.3f, 45.5f, 45.6f, 45.8f, 45.9f, 46.1f, 46.2f};

    private static final float GIRL_MONTH_HEAD_50TH[] = {33.9f, 36.5f, 38.3f, 39.5f, 40.6f, 41.5f, 42.2f, 42.8f, 43.4f, 43.8f, 44.2f, 44.6f,
            44.9f, 45.2f, 45.4f, 45.7f, 45.9f, 46.1f, 46.2f, 46.4f, 46.6f, 46.7f, 46.9f, 47.0f, 47.2f};

    private static final float GIRL_MONTH_HEAD_75TH[] = {34.7f, 37.3f, 39.1f, 40.4f, 41.4f, 42.3f, 43.1f, 43.7f, 44.3f, 44.7f, 45.1f, 45.5f,
            45.8f, 46.1f, 46.3f, 46.6f, 46.8f, 47.0f, 47.2f, 47.3f, 47.5f, 47.7f, 47.8f, 48.0f, 48.1f};

    private static final float GIRL_MONTH_HEAD_98TH[] = {36.2f, 38.9f, 40.7f, 42.0f, 43.1f, 44.0f, 44.8f, 45.5f, 46.0f, 46.5f, 46.9f, 47.3f,
            47.6f, 47.9f, 48.1f, 48.4f, 48.6f, 48.8f, 49.0f, 49.2f, 49.4f, 49.5f, 49.7f, 49.8f, 50.0f};

    public static final int TYPE_IMPERIAL = 1;
    public static final int TYPE_METRIC = 2;
    public static int sOutputType = TYPE_METRIC;

    public static float[] getDataFor3rd(int type, int gender, int duration, int count) {
        float[] dataForReturn = null;
        int returnLength = count;
        float multiplier = 1;

        if (sOutputType == TYPE_IMPERIAL) {
            if (type == TYPE_LENGTH) {
                multiplier = CM_2_IN;
            } else if (type == TYPE_WEIGHT) {
                multiplier = KG_2_LB;
            } else {
                multiplier = 1;
            }
        } else {
            multiplier = 1;
        }

        if (duration == DURATION_DAY) {
            dataForReturn = new float[7];

            if (type == TYPE_LENGTH) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = BOY_DAY_LENGTH_3RD[i] * multiplier;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = GIRL_DAY_LENGTH_3RD[i] * multiplier;
                    }
                } else {
                    return null;
                }
            } else if (type == TYPE_WEIGHT) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = BOY_DAY_WEIGHT_3RD[i] * multiplier;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = GIRL_DAY_WEIGHT_3RD[i] * multiplier;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (duration == DURATION_WEEK) {
            if (count > WEEK_DATA_MAX) {
                returnLength = WEEK_DATA_MAX;
            }
            dataForReturn = new float[returnLength];

            if (type == TYPE_LENGTH) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = BOY_WEEK_LENGTH_3RD[i] * multiplier;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = GIRL_WEEK_LENGTH_3RD[i] * multiplier;
                    }
                } else {
                    return null;
                }
            } else if (type == TYPE_WEIGHT) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = BOY_WEEK_WEIGHT_3RD[i] * multiplier;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = GIRL_WEEK_WEIGHT_3RD[i] * multiplier;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else

            //--------------------------------------
            if (duration == DURATION_MONTH) {
                if (count > MONTH_DATA_MAX) {
                    returnLength = MONTH_DATA_MAX;
                }
                dataForReturn = new float[returnLength];

                if (type == TYPE_LENGTH) {
                    if (gender == GENDER_BOY) {
                        for (int i = 0; i < returnLength; i++) {
                            dataForReturn[i] = BOY_MONTH_LENGTH_3RD[i] * multiplier;
                        }
                    } else if (gender == GENDER_GIRL) {
                        for (int i = 0; i < returnLength; i++) {
                            dataForReturn[i] = GIRL_MONTH_LENGTH_3RD[i] * multiplier;
                        }
                    } else {
                        return null;
                    }
                } else if (type == TYPE_WEIGHT) {
                    if (gender == GENDER_BOY) {
                        for (int i = 0; i < returnLength; i++) {
                            dataForReturn[i] = BOY_MONTH_WEIGHT_3RD[i] * multiplier;
                        }
                    } else if (gender == GENDER_GIRL) {
                        for (int i = 0; i < returnLength; i++) {
                            dataForReturn[i] = GIRL_MONTH_WEIGHT_3RD[i] * multiplier;
                        }
                    } else {
                        return null;
                    }

                } else if (type == TYPE_HEAD) {
                    if (gender == GENDER_BOY) {
                        for (int i = 0; i < returnLength; i++) {
                            dataForReturn[i] = BOY_MONTH_HEAD_2ND[i] * multiplier;
                        }
                    } else if (gender == GENDER_GIRL) {
                        for (int i = 0; i < returnLength; i++) {
                            dataForReturn[i] = GIRL_MONTH_HEAD_2ND[i] * multiplier;
                        }
                    } else {
                        return null;
                    }

                } else {
                    return null;
                }
            } else {
                return null;
            }

        return dataForReturn;
    }

    public static float[] getDataFor97th(int type, int gender, int duration, int count) {
        float[] dataForReturn = null;
        int returnLength = count;
        float multipler = 1;

        if (sOutputType == TYPE_IMPERIAL) {
            if (type == TYPE_LENGTH) {
                multipler = CM_2_IN;
            } else if (type == TYPE_WEIGHT) {
                multipler = KG_2_LB;
            } else {
                multipler = 1;
            }
        } else {
            multipler = 1;
        }

        if (duration == DURATION_DAY) {
            dataForReturn = new float[7];

            if (type == TYPE_LENGTH) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = BOY_DAY_LENGTH_97TH[i] * multipler;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = GIRL_DAY_LENGTH_97TH[i] * multipler;
                    }
                } else {
                    return null;
                }
            } else if (type == TYPE_WEIGHT) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = BOY_DAY_WEIGHT_97TH[i] * multipler;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < 7; i++) {
                        dataForReturn[i] = GIRL_DAY_WEIGHT_97TH[i] * multipler;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (duration == DURATION_WEEK) {
            if (count > WEEK_DATA_MAX) {
                returnLength = WEEK_DATA_MAX;
            }
            dataForReturn = new float[returnLength];

            if (type == TYPE_LENGTH) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = BOY_WEEK_LENGTH_97TH[i] * multipler;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = GIRL_WEEK_LENGTH_97TH[i] * multipler;
                    }
                } else {
                    return null;
                }
            } else if (type == TYPE_WEIGHT) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = BOY_WEEK_WEIGHT_97TH[i] * multipler;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = GIRL_WEEK_WEIGHT_97TH[i] * multipler;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (duration == DURATION_MONTH) {
            if (count > MONTH_DATA_MAX) {
                returnLength = MONTH_DATA_MAX;
            }
            dataForReturn = new float[returnLength];

            if (type == TYPE_LENGTH) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = BOY_MONTH_LENGTH_97TH[i] * multipler;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = GIRL_MONTH_LENGTH_97TH[i] * multipler;
                    }
                } else {
                    return null;
                }
            } else if (type == TYPE_WEIGHT) {
                if (gender == GENDER_BOY) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = BOY_MONTH_WEIGHT_97TH[i] * multipler;
                    }
                } else if (gender == GENDER_GIRL) {
                    for (int i = 0; i < returnLength; i++) {
                        dataForReturn[i] = GIRL_MONTH_WEIGHT_97TH[i] * multipler;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }

        return dataForReturn;
    }

    public static int getDuration(Calendar birthday, Calendar today) {
        //set to 00:00 at first
        birthday.set(Calendar.HOUR_OF_DAY, 0);
        birthday.set(Calendar.MINUTE, 0);
        birthday.set(Calendar.SECOND, 0);
        birthday.set(Calendar.MILLISECOND, 0);

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        //born in future is impossible
        if (!today.after(birthday)) {
            return DURATION_INVALID;
        }

        //check interval
        int dayCount = getDayCount(birthday, today);
        if (dayCount <= DAY_WEEK_SWITCH_GAP_IN_DAY) {
            return DURATION_DAY;
        } else if (dayCount <= WEEK_MONTH_SWITCH_GAP_IN_DAY) {
            return DURATION_WEEK;
        } else if (dayCount > WEEK_MONTH_SWITCH_GAP_IN_DAY) {
            return DURATION_MONTH;
        } else {
            //never go here
            return DURATION_INVALID;
        }
    }

    public static int getCount(Calendar birthday, Calendar today) {
        //set to 00:00 at first
        birthday.set(Calendar.HOUR_OF_DAY, 0);
        birthday.set(Calendar.MINUTE, 0);
        birthday.set(Calendar.SECOND, 0);
        birthday.set(Calendar.MILLISECOND, 0);

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        //born in future is impossible
        if (!today.after(birthday)) {
            return 0;
        }

        //check day interval
        int dayCount = getDayCount(birthday, today);
        if (dayCount <= DAY_WEEK_SWITCH_GAP_IN_DAY) {
            return 7;//always show 7 data in day mode
        } else if (dayCount <= WEEK_MONTH_SWITCH_GAP_IN_DAY) {
            int returnVal = 4;
            while (7 * returnVal < dayCount) {
                returnVal += 4;
            }
            if (returnVal > 12) {
                returnVal = 12;
            }

            return returnVal + 1;
        } else if (dayCount > WEEK_MONTH_SWITCH_GAP_IN_DAY) {
            int monthCount = getMonthCount(birthday, today);
            int returnVal = 4;
            while (returnVal < monthCount) {
                returnVal += 4;
            }
            if (returnVal > 24) {
                returnVal = 24;
            }

            return returnVal + 1;
        } else {
            //never go here
            return 0;
        }
    }

    private static int getDayCount(Calendar start, Calendar end) {
        int count = 0;
        Calendar temp = (Calendar) start.clone();
        while (temp.before(end)) {
            temp.add(Calendar.DAY_OF_MONTH, 1);
            count++;
        }
        return count;
    }

    private static int getMonthCount(Calendar start, Calendar end) {
        int count = 0;
        Calendar temp = (Calendar) start.clone();
        while (temp.before(end)) {
            temp.add(Calendar.MONTH, 1);
            count++;
        }
        return count;
    }

    public static float[] getData(int type, int gender, int count, int Percentiles) {
        float[] dataForReturn = null;
        int returnLength = count;
        float multipler = 1;

        if (sOutputType == TYPE_IMPERIAL) {
            if (type == TYPE_LENGTH) {
                multipler = CM_2_IN;
            } else if (type == TYPE_WEIGHT) {
                multipler = KG_2_LB;
            } else if (type == TYPE_HEAD) {
                multipler = CM_2_IN;
            } else {
                multipler = 1;
            }
        } else {
            multipler = 1;
        }

        if (count > MONTH_DATA_MAX) {
            returnLength = MONTH_DATA_MAX;
        }
        dataForReturn = new float[returnLength];

        if (type == TYPE_LENGTH) {
            if (gender == GENDER_BOY) {
                for (int i = 0; i < returnLength; i++) {
                    if (Percentiles == PERCENTILE_2ND) {
                        dataForReturn[i] = BOY_MONTH_LENGTH_3RD[i] * multipler;
                        Log.e("SOS", " 2nd ");
                    } else if (Percentiles == PERCENTILE_25TH) {
                        dataForReturn[i] = BOY_MONTH_LENGTH_25TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_50TH) {
                        dataForReturn[i] = BOY_MONTH_LENGTH_50TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_75TH) {
                        dataForReturn[i] = BOY_MONTH_LENGTH_75TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_98TH) {
                        dataForReturn[i] = BOY_MONTH_LENGTH_97TH[i] * multipler;
                        Log.e("SOS", " 98th ");
                    }
                }
            } else if (gender == GENDER_GIRL) {
                for (int i = 0; i < returnLength; i++) {
                    if (Percentiles == PERCENTILE_2ND) {
                        dataForReturn[i] = GIRL_MONTH_LENGTH_3RD[i] * multipler;
                    } else if (Percentiles == PERCENTILE_25TH) {
                        dataForReturn[i] = GIRL_MONTH_LENGTH_25TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_50TH) {
                        dataForReturn[i] = GIRL_MONTH_LENGTH_50TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_75TH) {
                        dataForReturn[i] = GIRL_MONTH_LENGTH_75TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_98TH) {
                        dataForReturn[i] = GIRL_MONTH_LENGTH_97TH[i] * multipler;
                    }
                }
            } else {
                return null;
            }
        } else if (type == TYPE_WEIGHT) {
            if (gender == GENDER_BOY) {
                for (int i = 0; i < returnLength; i++) {
                    if (Percentiles == PERCENTILE_2ND) {
                        dataForReturn[i] = BOY_MONTH_WEIGHT_3RD[i] * multipler;
                    } else if (Percentiles == PERCENTILE_25TH) {
                        dataForReturn[i] = BOY_MONTH_WEIGHT_25TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_50TH) {
                        dataForReturn[i] = BOY_MONTH_WEIGHT_50TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_75TH) {
                        dataForReturn[i] = BOY_MONTH_WEIGHT_75TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_98TH) {
                        dataForReturn[i] = BOY_MONTH_WEIGHT_97TH[i] * multipler;
                    }
                }
            } else if (gender == GENDER_GIRL) {
                for (int i = 0; i < returnLength; i++) {
                    if (Percentiles == PERCENTILE_2ND) {
                        dataForReturn[i] = GIRL_MONTH_WEIGHT_3RD[i] * multipler;
                    } else if (Percentiles == PERCENTILE_25TH) {
                        dataForReturn[i] = GIRL_MONTH_WEIGHT_25TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_50TH) {
                        dataForReturn[i] = GIRL_MONTH_WEIGHT_50TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_75TH) {
                        dataForReturn[i] = GIRL_MONTH_WEIGHT_75TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_98TH) {
                        dataForReturn[i] = GIRL_MONTH_WEIGHT_97TH[i] * multipler;
                    }
                }
            } else {
                return null;
            }
        } else if (type == TYPE_HEAD) {
            if (gender == GENDER_BOY) {
                for (int i = 0; i < returnLength; i++) {
                    if (Percentiles == PERCENTILE_2ND) {
                        dataForReturn[i] = BOY_MONTH_HEAD_2ND[i] * multipler;
                    } else if (Percentiles == PERCENTILE_25TH) {
                        dataForReturn[i] = BOY_MONTH_HEAD_25TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_50TH) {
                        dataForReturn[i] = BOY_MONTH_HEAD_50TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_75TH) {
                        dataForReturn[i] = BOY_MONTH_HEAD_75TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_98TH) {
                        dataForReturn[i] = BOY_MONTH_HEAD_98TH[i] * multipler;
                    }
                }
            } else if (gender == GENDER_GIRL) {
                for (int i = 0; i < returnLength; i++) {
                    if (Percentiles == PERCENTILE_2ND) {
                        dataForReturn[i] = GIRL_MONTH_HEAD_2ND[i] * multipler;
                    } else if (Percentiles == PERCENTILE_25TH) {
                        dataForReturn[i] = GIRL_MONTH_HEAD_25TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_50TH) {
                        dataForReturn[i] = GIRL_MONTH_HEAD_50TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_75TH) {
                        dataForReturn[i] = GIRL_MONTH_HEAD_75TH[i] * multipler;
                    } else if (Percentiles == PERCENTILE_98TH) {
                        dataForReturn[i] = GIRL_MONTH_HEAD_98TH[i] * multipler;
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
        return dataForReturn;
    }
}
