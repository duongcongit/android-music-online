package com.duongcong.androidmusic.Account;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.duongcong.androidmusic.FavouriteListActivity;
import com.duongcong.androidmusic.Model.OnlineSongModel;
import com.duongcong.androidmusic.R;
import com.duongcong.androidmusic.ShowAllSongActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ProgressBar progressBar;
    private Uri uri;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private byte[] artBytes;
    private TextView textViewSong,title,artist,album,data_song,duration;
    private ImageView imageView;
    private String string_title,string_artist,song_categories,string_duration,string_albumArt="";
    private Spinner spinner;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        textViewSong = (TextView) findViewById(R.id.textViewSongSelected);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        title = (TextView) findViewById(R.id.textViewSongTitle);
        artist = (TextView) findViewById(R.id.textViewSongArtist);
        album = (TextView) findViewById(R.id.textViewSongAlbum);
        data_song = (TextView) findViewById(R.id.textViewSongData);
        duration = (TextView) findViewById(R.id.textViewSongDuration);
        imageView = (ImageView) findViewById(R.id.imgView_image);
        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);



        //
        mediaMetadataRetriever = new MediaMetadataRetriever();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        storageReference = FirebaseStorage.getInstance().getReference().child("songs");

        //Set up
        List<String> categories = new ArrayList<>();
        categories.add("Nhạc Trẻ");
        categories.add("Pop Ballad");
        categories.add("Nhạc Rock");
        categories.add("Nhạc Dance");
        categories.add("Nhạc Remix");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        song_categories = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Thể loại: "+song_categories, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openAudioFiles(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK && data.getData()!= null){
            uri = data.getData();
            String fileName = getFileName(uri);
            textViewSong.setText(fileName);

            mediaMetadataRetriever.setDataSource(this,uri);

            artBytes = mediaMetadataRetriever.getEmbeddedPicture();
            if(artBytes != null)
            {
                InputStream is = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
                Bitmap bm = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bm);
            }
            else
            {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_account_circle_24));
            }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
//            imageView.setImageBitmap(bitmap);


            string_duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            string_artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            string_title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            string_duration=millisecondsToTime(Long.parseLong(string_duration));

            album.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            duration.setText(string_duration);
            title.setText(string_title);
            artist.setText(string_artist);
            data_song.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));




        }
    }

    //Lay ten file
    private String getFileName(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try{
                if(cursor!=null && cursor.moveToFirst()){
                    int total = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if(total>=0){
                        result = cursor.getString(total);
                    }
                }
            }
            finally {
                cursor.close();
            }
        }

        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut+1);
            }
        }

        return result;
    }

    public void uploadFileToFireBase(View v){
        if(textViewSong.equals("Chưa có file nào được chọn")){
            Toast.makeText(this, "Xin vui lòng chọn hình ảnh", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadFiles();
        }
    }

    //Upload len FIrebase , bài hát sẽ được up vào storage,thông tin lưu trong realtimeDB
    private void uploadFiles() {
        if(uri != null){
            Toast.makeText(this, "Xin vui lòng đợi", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);

            //gs://androidmusic-3d470.appshot.com/...
            final StorageReference storageReference1 = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uri));

            storageTask = storageReference1.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String uploadId = databaseReference.push().getKey();
                            OnlineSongModel onlineSongModel = new OnlineSongModel(uploadId,string_title,uri.toString(),string_albumArt,string_artist,"online",string_duration,song_categories,firebaseUser.getUid());
                            databaseReference.child(firebaseUser.getUid()).child("songs").child(uploadId).setValue(onlineSongModel);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        }
        else{
            Toast.makeText(this, "Vui lòng chọn file để upload nhạc", Toast.LENGTH_SHORT).show();
        }
    }

    //Lấy định dạng đuôi file. VD:mp3
    private String getFileExtension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }


    public void showAllSongs(View view) {
        Intent i = new Intent(SongUploadActivity.this, ShowAllSongActivity.class);
        startActivity(i);
    }


    //Convert milis to minutes:second
    private String millisecondsToTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        String secondsStr = Long.toString(seconds);
        String secs;
        if (secondsStr.length() >= 2) {
            secs = secondsStr.substring(0, 2);
        } else {
            secs = "0" + secondsStr;
        }

        return minutes + ":" + secs;
    }

}