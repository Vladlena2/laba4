package com.example.musicplayer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RadioActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_radio);

            dbHelper = new DatabaseHelper(this);

            Cursor cursor = dbHelper.getAllSongs();
            ListView listView = findViewById(R.id.song_list_view);
            SongCursorAdapter adapter = new SongCursorAdapter(this, cursor, 0);
            listView.setAdapter(adapter);
        }


}