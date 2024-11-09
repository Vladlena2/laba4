package com.example.musicplayer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class SongCursorAdapter extends CursorAdapter {

    public SongCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_song, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView = view.findViewById(R.id.song_title);
        TextView artistTextView = view.findViewById(R.id.song_artist);
        TextView timestampTextView = view.findViewById(R.id.song_timestamp);

        String title = cursor.getString(cursor.getColumnIndexOrThrow("track_title")); // Replace "title" with your actual column name
        String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist")); // Replace "artist" with your actual column name
        String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp")); // Replace "timestamp" with your actual column name

        titleTextView.setText(title);
        artistTextView.setText(artist);
        timestampTextView.setText(timestamp);
    }
}
