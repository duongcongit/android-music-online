package com.duongcong.androidmusic.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.Model.PlaylistModel;

import java.util.ArrayList;
import java.util.List;


public class PlaylistLocalDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "localplaylist.db";
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_PLAYLIST = "CREATE TABLE playlist(" +
            "playlistName text NOT NULL PRIMARY KEY," +
            "type text NOT NULL);";

    // String create table
    private static final String DATABASE_CREATE_SONG_PLAYLIST = "CREATE TABLE songPlaylist (" +
            "playlistName text NOT NULL," +
            "songName text NOT NULL," +
            "artist  text," +
            "album text," +
            "songPath text NOT NULL," +
            "PRIMARY KEY(playlistName,songName)," +
            "FOREIGN KEY(playlistName) REFERENCES playlist(playlistName) );";

    public PlaylistLocalDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_PLAYLIST);
        db.execSQL(DATABASE_CREATE_SONG_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("UPGRADE DB from " + "oldVersion to " + newVersion);
    }

    // Create a playlist
    public boolean createPlaylist(String playlistName, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("playlistName", playlistName);
        contentValues.put("type", type);
        db.insert("playlist", null, contentValues);
        return true;
    }

    // Delete a playlist
    public boolean deletePlaylist(String playlistName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("songPlaylist", "playlistName=?", new String[] {playlistName});
        db.delete("playlist", "playlistName=?", new String[] {playlistName});
        return true;
    }

    // Update playlist
    public void updatePlaylist(String playlistName, String newPlaylistName, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("playlistName", newPlaylistName);
        contentValues.put("type", type);
        db.update("playlist", contentValues, "playlistName=?", new String[] {playlistName});
    }


    // Insert a song into database
    public boolean addSongToPlaylist (String playlistName, String songName, String artist, String album, String songPath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("playlistName", playlistName);
        contentValues.put("songName", songName);
        contentValues.put("artist", artist);
        contentValues.put("album", album);
        contentValues.put("songPath", songPath);
        db.insert("songPlaylist", null, contentValues);
        return true;
    }

    // Delete a song
    public void deleteSongFromPlaylist(String playlistName, String songPath){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("songPlaylist", "playlistName=? and songPath=?", new String[] {playlistName, songPath});
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        // int numRows = (int) DatabaseUtils.queryNumEntries(db, "tbtest");
        return 3;
    }

    // Get list local playlist
    public ArrayList<PlaylistModel> getPlaylist() {
        ArrayList<PlaylistModel> playlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT DISTINCT playlistName " +
                "FROM playlist " +
                "ORDER BY playlistName ASC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            PlaylistModel playlistModel = new PlaylistModel();
            playlistModel.setName(res.getString(res.getColumnIndexOrThrow("playlistName")));
            playlistModel.setType("local");

            playlist.add(playlistModel);
            res.moveToNext();
        }

        return playlist;
    }

    // Get list local playlist
    public ArrayList<PlaylistModel> getPlaylistSongNotAdded(String songPath) {
        ArrayList<PlaylistModel> playlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT DISTINCT playlistName FROM playlist WHERE playlistName NOT IN (SELECT playlist.playlistName FROM playlist, songPlaylist WHERE playlist.playlistName = songPlaylist.playlistName AND songPath = \"" + songPath + "\")";
        Cursor res =  db.rawQuery( sql, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            PlaylistModel playlistModel = new PlaylistModel();
            playlistModel.setName(res.getString(res.getColumnIndexOrThrow("playlistName")));
            playlistModel.setType("local");

            playlist.add(playlistModel);
            res.moveToNext();
        }

        return playlist;
    }

    // Get list song in a playlist
    public List<SongModel> getPlaylistData(String playlistName) {
        List<SongModel> listSong = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "SELECT * FROM songPlaylist WHERE playlistName = \"" + playlistName + "\";", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            SongModel audioModel = new SongModel();

            String songName = res.getString(res.getColumnIndexOrThrow("songName"));
            String artist = res.getString(res.getColumnIndexOrThrow("artist"));
            String album = res.getString(res.getColumnIndexOrThrow("album"));
            String songPath = res.getString(res.getColumnIndexOrThrow("songPath"));

            audioModel.setName(songName);
            audioModel.setArtist(artist);
            audioModel.setAlbum(album);
            audioModel.setPath(songPath);

            listSong.add(audioModel);

            res.moveToNext();
        }

        return listSong;

    }



}
