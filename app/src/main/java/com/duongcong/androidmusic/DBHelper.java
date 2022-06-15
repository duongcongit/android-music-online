package com.duongcong.androidmusic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Login.db";
    private static final int    DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, "Login.db", null, DATABASE_VERSION);
    }

    //Tạo bảng
    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        String queryCreateTable = "CREATE TABLE users " + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username VARCHAR (255) NOT NULL, " +
                "password TEXT NOT NULL" +
                ")";
        MyDB.execSQL(queryCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("DROP TABLE IF EXISTS users");
        //Tiến hành tạo bảng mới
        onCreate(MyDB);
    }

    //Chèn người dùng mới
    public Boolean insertData(String username,String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",username);
        contentValues.put("password",password);

        long result = MyDB.insert("users",null,contentValues);
        MyDB.close();
        if(result==-1){
            return false;
        }
        else {
            return true;
        }
    }



    //Xoá người dùng khỏi DB
    public void deleteProductByID(int ID) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        MyDB.execSQL("DELETE FROM users where id = ?", new String[]{String.valueOf(ID)});
    }

    //Kiểm tra tài khoản đã tồn tại chưa
    public Boolean checkUserName(String username){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * from users where username = ?",
                new String[]{username + ""});

        if(cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }

    }

    //Kiểm tra đăng nhập
    public Boolean checkUserNamePassWord(String username,String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * from users where username = ? and password = ?",
                new String[]{username,password+ ""});

        if(cursor.getCount()>0){
            return true;
        }
        else{
            return false;
        }

    }
}
