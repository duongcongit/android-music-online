package com.duongcong.androidmusic.Account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SongUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ProgressBar progressBar;
    private Bitmap bitmapDefault,bitmapImage=null;
    private Uri uri,uriImages;
    private StorageReference storageReference,storageReferenceImages;
    private StorageTask storageTask;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;
    private MediaMetadataRetriever mediaMetadataRetriever;
    private byte[] artBytes;
    private TextView textViewSong,title,artist,album,data_song,duration;
    private ImageView imageView;
    private String string_title,string_artist,song_categories,string_duration,string_albumArt="";
    private String titleInfo,albumInfo,artistInfo,uploadId;
    private Spinner spinner;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Uri urlUploadImg;


    private static final int PICK_IMAGE_REQUETS = 234;
    private static final int PICK_Song_REQUETS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_upload);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        textViewSong = (TextView) findViewById(R.id.textViewSongSelected);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        title = (EditText) findViewById(R.id.textViewSongTitleInfo);
        artist = (EditText) findViewById(R.id.textViewSongArtistInfo);
        album = (EditText) findViewById(R.id.textViewSongAlbumInfo);
        duration = (EditText) findViewById(R.id.textViewSongDurationInfo);
        imageView = (ImageView) findViewById(R.id.imgView_image);
        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);



        //
        mediaMetadataRetriever = new MediaMetadataRetriever();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        storageReference = FirebaseStorage.getInstance().getReference().child("songs");
        storageReferenceImages = FirebaseStorage.getInstance().getReference("images");

        //Set up
        List<String> categories = new ArrayList<>();
        categories.add("Nh???c Tr???");
        categories.add("Pop Ballad");
        categories.add("Nh???c Rock");
        categories.add("Nh???c Dance");
        categories.add("Nh???c Remix");
        categories.add("Th??? lo???i kh??c");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    //L???y d??? li???u t???u spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        song_categories = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Th??? lo???i: "+song_categories, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void openAudioFiles(View v){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent,PICK_Song_REQUETS);
    }

    public void openImageFiles(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Ch???n h??nh ???nh"),PICK_IMAGE_REQUETS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_Song_REQUETS:
                if(resultCode == RESULT_OK && data.getData()!= null){
                    uri = data.getData();
                    String fileName = getFileName(uri);
                    textViewSong.setText(fileName);

                    mediaMetadataRetriever.setDataSource(this,uri);

//                    artBytes = mediaMetadataRetriever.getEmbeddedPicture();
//                    if(artBytes != null)
//                    {
//                        InputStream is = new ByteArrayInputStream(mediaMetadataRetriever.getEmbeddedPicture());
//                        bitmapDefault = BitmapFactory.decodeStream(is);
//                        imageView.setImageBitmap(bitmapDefault);
//                    }
//                    else
//                    {
//                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_account_circle_24));
//                    }


                    string_duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    string_artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    string_title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    string_duration=millisecondsToTime(Long.parseLong(string_duration));

                    album.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                    duration.setText(string_duration);
                    title.setText(string_title);
                    artist.setText(string_artist);


                }
                break;

            case PICK_IMAGE_REQUETS:
                if(resultCode == RESULT_OK && data.getData()!= null){
                    uriImages = data.getData();
                    bitmapImage = null;
                    try {
                        bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(),uriImages);
                        imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),uriImages));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
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
        titleInfo = title.getText().toString();
        albumInfo = album.getText().toString();
        artistInfo = artist.getText().toString();

        if(titleInfo.equals("")){
            title.setError("Vui l??ng ??i???n th??ng tin");
            title.requestFocus();
        }
        else if (albumInfo.equals("")){
            album.setError("Vui l??ng ??i???n th??ng tin");
            album.requestFocus();
        }
        else if (artistInfo.equals("")){
            artist.setError("Vui l??ng ??i???n th??ng tin");
            artist.requestFocus();
        }
        else if(textViewSong.equals("Ch??a c?? file n??o ???????c ch???n")){
            Toast.makeText(this, "Xin vui l??ng ch???n files", Toast.LENGTH_SHORT).show();
        }
        else if(bitmapImage== null){
            Toast.makeText(this, "Xin vui l??ng ch???n ???nh", Toast.LENGTH_SHORT).show();
        }
        else{
            uploadFiles();
        }
    }

    //Upload len FIrebase , b??i h??t s??? ???????c up v??o storage,th??ng tin l??u trong realtimeDB
    private void uploadFiles() {
        if(uri != null && uriImages!= null){
            Toast.makeText(this, "Xin vui l??ng ?????i", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);

            uploadId = databaseReference.push().getKey();


            final StorageReference storageReference1 = storageReferenceImages.child(
                    uploadId+"."+getFileExtension(uriImages));

            //gs://androidmusic-3d470.appshot.com/...
            final StorageReference storageReference2 = storageReference.child(uploadId+"."+getFileExtension(uri));



            //Put song Image
            storageReference1.putFile(uriImages).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SongUploadActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            urlUploadImg = uri;
                        }
                    });
                }
            });

            //Put song
            storageTask = storageReference2.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            long currentTime = System.currentTimeMillis();
                            SongModel onlineSongModel = new SongModel(uploadId,titleInfo,uri.toString(),albumInfo,artistInfo,"online",string_duration,song_categories,urlUploadImg.toString(),currentTime+"");
                            databaseReference.child(firebaseUser.getUid()).child("songs").child(uploadId).setValue(onlineSongModel);
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                    if(progress==100.0){
                        Toast.makeText(SongUploadActivity.this, "T???i l??n b??i h??t th??nh c??ng", Toast.LENGTH_SHORT).show();
                    }
                }
            });





        }
        else{
            Toast.makeText(this, "Vui l??ng ch???n file/???nh ????? upload nh???c", Toast.LENGTH_SHORT).show();
        }
    }

    //L???y ?????nh d???ng ??u??i file. VD:mp3
    private String getFileExtension(Uri audioUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
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