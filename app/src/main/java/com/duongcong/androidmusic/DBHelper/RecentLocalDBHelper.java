package com.duongcong.androidmusic.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duongcong.androidmusic.Model.SongModel;

import java.util.ArrayList;

public class RecentLocalDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "localrecent.db";
    public static final int DATABASE_VERSION = 1;

    // String create table
    private static final String DATABASE_CREATE_SONG_RECENT= "CREATE TABLE songRecent (" +
            "songId text," +
            "songName text NOT NULL," +
            "songArtist  text," +
            "songAlbum text," +
            "songPath text NOT NULL," +
            "songImg text," +
            "songCategory text," +
            "songDuration text," +
            "songType text NOT NULL," +
            "timeAdd DATETIME DEFAULT (datetime('now', 'localtime'))," +
            "PRIMARY KEY(songId,songPath))";

    public RecentLocalDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SONG_RECENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("UPGRADE DB from " + "oldVersion to " + newVersion);
    }

    // Add a song to database
    public void addSongToRecent(SongModel song){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("songId", song.getId());
        contentValues.put("songName", song.getName());
        contentValues.put("songArtist", song.getArtist());
        contentValues.put("songAlbum", song.getAlbum());
        contentValues.put("songPath", song.getPath());
        contentValues.put("songImg", song.getImage());
        contentValues.put("songCategory", song.getCategory());
        contentValues.put("songDuration", song.getDuration());
        contentValues.put("songType", song.getType());
        db.insert("songRecent", null, contentValues);
        // return true;
    }

    // Add or update song in recent
    public void addOrUpdateSongInRecent(SongModel song){
        // If song is added in recent list, update time
        if(!checkSongExistInRecent(song)){
            addSongToRecent(song);
        }
        // If not, add to list
        else {
            updateTimeRecent(song);
        }
    }

    // Update time
    public void updateTimeRecent(SongModel song){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE songRecent SET timeAdd = datetime('now', 'localtime') WHERE songType = '" + song.getType() + "' AND songPath = '" + song.getPath() + "'";
        db.execSQL(sql);
    }

    // Check song is added to recent or not
    public boolean checkSongExistInRecent(SongModel song){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM songRecent WHERE songType = '" + song.getType() + "' AND songPath = '" + song.getPath() + "'";
        Cursor res =  db.rawQuery( sql, null );

        if(res.getCount() >= 1){
            return true;
        }
        else {
            return false;
        }
    }

    // Clear recent list
    public void clearRecentList(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM songRecent;";
        db.execSQL(sql);
    }

    // Get list song in a playlist
    public ArrayList<SongModel> getRecentList() {
        ArrayList<SongModel> listSong = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "SELECT * FROM songRecent ORDER BY timeAdd DESC", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            // Get song data
            SongModel audioModel = new SongModel();
            String songId       = res.getString(res.getColumnIndexOrThrow("songId"));
            String songName     = res.getString(res.getColumnIndexOrThrow("songName"));
            String artist       = res.getString(res.getColumnIndexOrThrow("songArtist"));
            String album        = res.getString(res.getColumnIndexOrThrow("songAlbum"));
            String songPath     = res.getString(res.getColumnIndexOrThrow("songPath"));
            String songImg      = res.getString(res.getColumnIndexOrThrow("songImg"));
            String songCategory = res.getString(res.getColumnIndexOrThrow("songCategory"));
            String songDuration = res.getString(res.getColumnIndexOrThrow("songDuration"));
            String songType     = res.getString(res.getColumnIndexOrThrow("songType"));

            // Create a model
            audioModel.setId(songId);
            audioModel.setName(songName);
            audioModel.setArtist(artist);
            audioModel.setAlbum(album);
            audioModel.setPath(songPath);
            audioModel.setImage(songImg);
            audioModel.setCategory(songCategory);
            audioModel.setDuration(songDuration);
            audioModel.setType(songType);

            // Add to list
            listSong.add(audioModel);

            res.moveToNext();
        }

        return listSong;

    }

}
