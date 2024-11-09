package com.example.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SongFetchReceiver extends BroadcastReceiver {

    private static final String API_URL = "https://media.itmo.ru/api_get_current_song.php";
    private static final String LOGIN = "4707login";
    private static final String PASSWORD = "4707pass";

    @Override
    public void onReceive(Context context, Intent intent) {
        new FetchSongTask(context).execute();
    }

    private static class FetchSongTask extends AsyncTask<Void, Void, String> {

        private Context context;

        public FetchSongTask(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String data = "login=" + LOGIN + "&password=" + PASSWORD;
                try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                    writer.write(data);
                    writer.flush();
                }

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    if ("success".equals(jsonObject.getString("result"))) {
                        String info = jsonObject.getString("info");

                        // Разделение строки info по символу " - " (короткое тире с пробелами)
                        String[] parts = info.split(" - ");
                        if (parts.length >= 2) {
                            String artist = parts[0].trim();
                            String trackTitle = parts[1].trim();

                            DatabaseHelper dbHelper = new DatabaseHelper(context);
                            Cursor cursor = dbHelper.getAllSongs();

                            // Проверка наличия данных в курсоре и определение, отличается ли новая песня
                            boolean isNewSong = true;
                            if (cursor != null && cursor.moveToLast()) {
                                int trackTitleIndex = cursor.getColumnIndex("track_title");
                                if (trackTitleIndex != -1) {
                                    String lastTrackTitle = cursor.getString(trackTitleIndex);
                                    isNewSong = !lastTrackTitle.equals(trackTitle);
                                }
                                cursor.close(); // Закрываем курсор после использования
                            }

                            // Вставка новой песни, если она отличается от последней
                            if (isNewSong) {
                                dbHelper.insertSong(artist, trackTitle, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                                showToast("Новая песня добавлена: " + trackTitle);
                            } else {
                                showToast("Та же песня воспроизводится.");
                            }
                        } else {
                            showToast("Ошибка: Невозможно разобрать информацию о песне.");
                        }
                    } else {
                        showToast("Ошибка: " + jsonObject.getString("info"));
                    }


                } else {
                    showToast("Ошибка сети: " + connection.getResponseCode());
                }

            } catch (Exception e) {
                e.printStackTrace();
                showToast("Ошибка: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        private void showToast(String message) {
            new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            );
        }

    }
}

