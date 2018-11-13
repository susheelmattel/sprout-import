package com.sproutling.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by subram13 on 5/15/17.
 */

public class LoggerService extends IntentService {
    private static String TAG = "LoggerService";

    public enum LogMod {
        silent,
        active
    }

    private static LogMod sMode = LogMod.active;

    public static final String ACTION_LOG = "LOGGER_SERVICE_ACTION_LOG";
    public static final String ACTION_SET_MODE = "LOGGER_SERVICE_ACTION_SET_MODE";
    public static final String ACTION_CLEANUP = "LOGGER_SERVICE_ACTION_CLEANUP"; // Set by an alarm for daily old log files cleanup

    public static final String EXTRA_LOG = "EXTRA_LOG";
    public static final String EXTRA_MODE = "EXTRA_MODE";

//    private static final String LOGSTASH_SERVER_URL = "https://logs-dev-us.sproutlingcloud.com";
    private static final String LOGSTASH_SERVER_URL = "10.0.1.149";
    private static final int LOGSTASH_UDP_JSON_PORT = 5044;
    private static final String LOGSTASH_FILE_PREFIX = "logstash_";
    private static final int MAX_LOG_DAYS = 7;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final int DAY = 24 * 60 * 60 * 1000; // in milliseconds

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setCleanupWakeAlarm(DAY);
    }

    /**
     * Start this service to perform a writing action with the given parameters. If
     * the service is already performing a task this action will be queued.*
     *
     * @param context
     * @param log     the log row to be written
     */
    public static void logMessage(Context context, String log) {
        Intent intent = new Intent(context, LoggerService.class);
        intent.setAction(ACTION_LOG);
        intent.putExtra(EXTRA_LOG, log);

        context.startService(intent);
    }

    /**
     * Start this service to change the way the service behaves. If
     * the service is already performing a task this action will be queued.
     *
     * @param context
     * @param newMode the new mode ordinal to be set
     */
    public static void changeMode(Context context, LogMod newMode) {
        Intent intent = new Intent(context, LoggerService.class);
        intent.setAction(ACTION_SET_MODE);
        intent.putExtra(EXTRA_MODE, newMode.ordinal());

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (action != null) {
            if (action.equalsIgnoreCase(ACTION_LOG)) {
                String log = intent.getStringExtra(EXTRA_LOG);
                if (TextUtils.isEmpty(log)) return;
                Log.d(TAG, "mode:" + sMode + ". got log:" + log);

                switch (sMode) {
                    case silent:
                        writeLogToFile(log);
                        break;
                    case active:
                        sendLogToServer(log);
                        break;
                    default:
                        break;
                }
            } else if (action.equalsIgnoreCase(ACTION_SET_MODE)) {
                int newMode = intent.getIntExtra(EXTRA_MODE, LogMod.silent.ordinal());
                setLogMode(LogMod.values()[newMode]);
            } else if (action.equalsIgnoreCase(ACTION_CLEANUP)) {
                // delete old log file if needed. only keep 7 days of logs
                deleteOldLogFile();
            }
        }
    }

    private void sendLogToServer(String logStr) {
        if (logStr == null) return;
        DatagramSocket socket;
        InetAddress host;
        try {
            socket = new DatagramSocket();
            if (socket == null) return;
//            host = InetAddress.getByName(new URL(LOGSTASH_SERVER_URL).getHost());
//            host = LOGSTASH_SERVER_URL;
            host= InetAddress.getByName(LOGSTASH_SERVER_URL);
        } catch (SocketException | UnknownHostException e) {
            Log.d(TAG, "couldn't send log:" + e.toString());
            return;
        }
//        catch (MalformedURLException e) {
//            Log.d(TAG, "couldn't send log:" + e.toString());
//            return;
//        }
        int msg_length = logStr.length();
        byte[] message = logStr.getBytes();
        if (host != null) {
            DatagramPacket p = new DatagramPacket(message, msg_length, host, LOGSTASH_UDP_JSON_PORT);
            try {
                socket.send(p);
            } catch (IOException e) {
                Log.d(TAG, "couldn't send:" + e.toString());
            }
        }
    }

    private void writeLogToFile(String log) {
        String dateStr = dateFormat.format(new Date());
        String fileName = LOGSTASH_FILE_PREFIX + dateStr;
        BufferedWriter bw = null;
        try {
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_APPEND);
            DataOutputStream out = new DataOutputStream(outputStream);
            bw = new BufferedWriter(new OutputStreamWriter(out));
            bw.write(log);
            bw.newLine();
        } catch (IOException e) {
            Log.d(TAG, "couldn't write log:" + e.toString());
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    Log.d(TAG, "failed to close BufferedWriter:" + e.toString());
                }
            }
        }
    }

    private void setLogMode(LogMod newMode) {
        if (newMode == sMode) return;
        LogMod oldMode = sMode;
        sMode = newMode;
        if (oldMode == LogMod.silent && newMode == LogMod.active) {
            // activating the logging, send all the accumulated logs
            flushLogsToServer();
        }
    }

    private void deleteOldLogFile() {
        // get the date of MAX_LOG_DAYS days ago
        String dateStr = getDayString(-MAX_LOG_DAYS);

        // delete the old (week ago) file
        String fileName = LOGSTASH_FILE_PREFIX + dateStr;
        deleteFile(fileName);

        // schedule the logs deletion to occur once a day
        setCleanupWakeAlarm(DAY);
    }

    private String getDayString(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, offset);
        Date newDate = calendar.getTime();
        String dateStr = dateFormat.format(newDate);
        return dateStr;
    }

    private void flushLogsToServer() {
        // send log file one by one (each log file is a day of logs)
        for (int i = MAX_LOG_DAYS; i >= 0; i--) {
            String dateStr = getDayString(-i);
            String fileName = LOGSTASH_FILE_PREFIX + dateStr;
            sendLogFile(fileName);
            // delete the log file
            deleteFile(fileName);
        }
    }

    /**
     * Sends a log file to the server, line by line - each line is a separate log.
     *
     * @param fileName log file name
     */
    private void sendLogFile(String fileName) {
        FileInputStream fstream = null;
        try {
            fstream = openFileInput(fileName);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "couldn't open log file" + e.toString());
            return;
        }
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String log = "";
            while ((log = br.readLine()) != null) {
                sendLogToServer(log);
            }
        } catch (IOException e) {
            Log.d(TAG, "couldn't send log to server:" + e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Log.d(TAG, "Failed to close BufferedReader:" + e.toString());
            }
        }
    }

    private void setCleanupWakeAlarm(long interval) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval,
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_CLEANUP), 0));
    }
}
