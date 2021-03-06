package com.duongcong.androidmusic;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.DBHelper.PlaylistLocalDBHelper;
import com.duongcong.androidmusic.Model.PlaylistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class SongMenuOptionFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private StorageReference storageRef;

    protected ConstraintLayout songMenuOptionHideArea;

    private ConstraintLayout songMenuOption;

    private ConstraintLayout songMenuOptionRemoveFromUpload;
    private ConstraintLayout songMenuOptionAddToPlaylist;
    private ConstraintLayout songMenuOptionRemoveFromPlaylist;
    private ConstraintLayout songMenuOptionUpload;
    private ConstraintLayout songMenuOptionDownload;
    private ConstraintLayout songMenuOptionDeleteFile;

    ConstraintLayout menuContainer;

    private Animation item_click;

    private Bundle bundle;

    ArrayList<PlaylistModel> arrPlaylist;
    PlaylistToAddAdapter playlistListViewAdapter;
    ListView lvPlaylist;
    ConstraintLayout menuPlaylistToAdd;

    ConstraintLayout btnMenuCreatePlaylist;

    EditText txtCreatePlaylistName;
    Button btnConfirmCreatePlaylist, btnCancelCreatePlaylist;

    String isInPlaylist, playlistName, playlistType; // Get data if song in playlist
    String songId, songName, songPath, songImg, songAlbum, songArtist, songCategory, songDuration, songType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout =  inflater.inflate(R.layout.fragment_song_menu_option, container, false);

        // Hide bottom navigation bar and playing song bar
        ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
        ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);
        ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_show);
            }
        }, 250);

        bundle = this.getArguments();
        getBundleData();

        //
        return rootLayout;


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //
            ((MainActivity)getActivity()).navigation.setVisibility(View.VISIBLE);
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.VISIBLE);
            if(((MainActivity)getActivity()).mediaPlayer.isPlaying()){
                ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.VISIBLE);
            }
        } else {
            bundle = this.getArguments();
            getBundleData();

            // Hide bottom navigation bar and playing song bar
            ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
            ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_show);
                }
            }, 250);

        }
    }

    // Get data
    private void getBundleData(){
        if(bundle!=null){
            // Get song data
            songId          = bundle.getString("songId");
            songName        = bundle.getString("songName");
            songPath        = bundle.getString("songPath");
            songImg         = bundle.getString("songImg");
            songAlbum       = bundle.getString("songAlbum");
            songArtist      = bundle.getString("songArtist");
            songCategory    = bundle.getString("songCategory");
            songDuration    = bundle.getString("songDuration");
            songType        = bundle.getString("songType");
            // Get data if song in playlist
            isInPlaylist    = bundle.getString("isInPlaylist");
            playlistName    = bundle.getString("playlistName");
            playlistType    = bundle.getString("playlistType");
        }
    }

    // Display or hide menu items by song
    private void setMenuItemView(){
        songMenuOptionRemoveFromUpload.setVisibility(View.VISIBLE);
        songMenuOptionUpload.setVisibility(View.GONE);
        songMenuOptionAddToPlaylist.setVisibility(View.VISIBLE);
        songMenuOptionRemoveFromPlaylist.setVisibility(View.VISIBLE);
        songMenuOptionDownload.setVisibility(View.VISIBLE);
        songMenuOptionUpload.setVisibility(View.VISIBLE);

        // If is local/offline song, hide option remove from upload and option download
        if(Objects.equals(songType, "local")){
            songMenuOptionRemoveFromUpload.setVisibility(View.GONE);
            songMenuOptionDownload.setVisibility(View.GONE);
        }
        // Check if online song is downloaded
        String path = ((MainActivity)getActivity()).appExternalStoragePath + "/" + songName + ".mp3";
        File file = new File(path);
        if(Objects.equals(songType, "online") && file.exists()){
            songMenuOptionDownload.setVisibility(View.GONE);
        }
        // otherwise in a playlist, hide option remove from playlist
        if(Objects.equals(isInPlaylist, "no")){
            songMenuOptionRemoveFromPlaylist.setVisibility(View.GONE);
        }
        // If it is cloud song
        if(Objects.equals(songType, "online")){
            songMenuOptionUpload.setVisibility(View.GONE);
            songMenuOptionDeleteFile.setVisibility(View.GONE);
        }

        // Check if this song is own, show option remove from cloud
        if(firebaseUser!=null){
            // System.out.println("???? ????ng nh???p");
            FirebaseDatabase database = FirebaseDatabase.getInstance();;
            DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("songs");
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isMySong = false;
                    for(DataSnapshot ds : snapshot.getChildren()) { // Browse each song
                        String songIdCloud = (String) ds.child("id").getValue();
                        if(songIdCloud.equals(songId)){
                            songMenuOptionRemoveFromUpload.setVisibility(View.VISIBLE);
                            break;
                        }
                        else {
                            songMenuOptionRemoveFromUpload.setVisibility(View.GONE);
                        }
                    }
                }
                //
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //
                }

            });
        }
        else {
            songMenuOptionRemoveFromUpload.setVisibility(View.GONE);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference();

        // Parent fragment
        songMenuOption    = view.findViewById(R.id.song_menu_option);

        // Menu container
        menuContainer = view.findViewById(R.id.song_menu_option_view_container);

        // Song info
        TextView songNameView   = (TextView) view.findViewById(R.id.txt_menu_songName);
        TextView songArtistView = (TextView) view.findViewById(R.id.txt_menu_songArtist);
        songNameView.setText(songName);
        songNameView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songNameView.setSelected(true);
        songNameView.setSingleLine(true);
        songArtistView.setText(songArtist);


        // Menu items
        songMenuOptionRemoveFromUpload      = view.findViewById(R.id.menu_option_rm_from_upload);
        songMenuOptionAddToPlaylist         = view.findViewById(R.id.menu_option_add_to_playlist);
        songMenuOptionRemoveFromPlaylist    = view.findViewById(R.id.menu_option_rm_from_playlist);
        songMenuOptionUpload                = view.findViewById(R.id.menu_option_upload);
        songMenuOptionDownload              = view.findViewById(R.id.menu_option_download);
        songMenuOptionDeleteFile            = view.findViewById(R.id.menu_option_delete_file);

        // Set display or hide menu items
        setMenuItemView();

        // Animation click
        item_click = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.menu_option_item_click);

        // Menu playlist
        menuPlaylistToAdd = view.findViewById(R.id.menu_playlist_to_add);
        lvPlaylist = view.findViewById(R.id.listView_playlist_to_add);
        menuPlaylistToAdd.setVisibility(View.GONE);
        btnMenuCreatePlaylist = view.findViewById(R.id.menu_option_btn_create_playlist);

        // Hide menu when click to outside area
        songMenuOptionHideArea = view.findViewById(R.id.songMenuOptionHideArea);
        songMenuOptionHideArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);
                //
            }
        });

        //
        songMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        // ========= ITEMS CLICK
        // Remove song from uploaded
        songMenuOptionRemoveFromUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionRemoveFromUpload.startAnimation(item_click);
                FirebaseDatabase database = FirebaseDatabase.getInstance();;
                DatabaseReference songDataDelRef = database.getReference().child("users").child(firebaseUser.getUid()).child("songs").child(songId);

                String songFileName = URLUtil.guessFileName(songPath, null, null);
                String songImgName  = URLUtil.guessFileName(songImg, null, null);
                // System.out.println(songFileName);
                // System.out.println(songImgName);
                StorageReference delSongRef = storageRef.child("songs/" + songFileName);
                StorageReference delImgRef  = storageRef.child("images/" + songImgName);

                delSongRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        delImgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                songDataDelRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "???? x??a kh???i upload!", Toast.LENGTH_SHORT).show();
                                        // Reload list song
                                        ((MainActivity)getActivity()).songsFragment.onHiddenChanged(false);
                                    }
                                });
                            }
                        });
                    }
                });

                // Hide option menu
                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);


            }
        });

        // Add song to playlist
        songMenuOptionAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionAddToPlaylist.startAnimation(item_click);

                // Hide menu item
                menuContainer.setVisibility(View.INVISIBLE);
                // Show playlists available to add container
                menuPlaylistToAdd.setVisibility(View.VISIBLE);

                // Event click button add playlist
                btnMenuCreatePlaylist = view.findViewById(R.id.menu_option_btn_create_playlist);
                btnMenuCreatePlaylist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createPlaylistDialog();
                    }
                });

                // Show playlists available to add
                getPlaylist();

                // Event when choose a playlist to add
                lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Get playlist data
                        PlaylistModel playlistModel = (PlaylistModel) playlistListViewAdapter.getItem(position);
                        String playlistNameSelected = playlistModel.getName();
                        String playlistSelectedType = playlistModel.getType();
                        if(bundle != null){
                            // If add to local playlist
                            if(Objects.equals(playlistSelectedType, "local")){
                                PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                                mydb.addSongToPlaylist(playlistNameSelected, songId, songName, songArtist, songAlbum, songPath, songImg, songCategory, songDuration, songType);

                                Toast.makeText( getContext(),"???? th??m th??nh c??ng!", Toast.LENGTH_SHORT).show();
                                // Hide menu option
                                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);

                            }
                            // If add to online playlist
                            else if(Objects.equals(playlistSelectedType, "online")){
                                String tmpId = songId;
                                // Generate random song id if song type is local (id is null)
                                if(Objects.equals(songType, "local")){
                                    tmpId = "local" + UUID.randomUUID().toString().replaceAll("-", "");
                                }
                                //
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid())
                                        .child("playlists").child(playlistNameSelected).child("songs").child(tmpId);
                                // Insert data to firebase
                                myFirebaseRef.child("id").setValue(tmpId);
                                myFirebaseRef.child("name").setValue(songName);
                                myFirebaseRef.child("path").setValue(songPath);
                                myFirebaseRef.child("image").setValue(songImg);
                                myFirebaseRef.child("album").setValue(songAlbum);
                                myFirebaseRef.child("artist").setValue(songArtist);
                                myFirebaseRef.child("category").setValue(songCategory);
                                myFirebaseRef.child("duration").setValue(songDuration);
                                myFirebaseRef.child("type").setValue(songType).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText( getContext(),"???? th??m th??nh c??ng!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                // Hide menu option
                                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);

                            }

                        }


                    }
                });

            }
        });

        //Remove song from playlist
        songMenuOptionRemoveFromPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionRemoveFromPlaylist.startAnimation(item_click);
                if(bundle != null){
                    if(Objects.equals(playlistType, "local")){
                        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                        mydb.deleteSongFromPlaylist(playlistName, songPath);
                        Toast.makeText( getContext(),"???? x??a th??nh c??ng!", Toast.LENGTH_SHORT).show();
                        // Hide menu option
                        ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                        songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);
                        // Refresh list song in playlist
                        ((MainActivity)getActivity()).songOnPlaylistFragment.onHiddenChanged(false);
                    }
                    else if(Objects.equals(playlistType, "online")){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();;
                        DatabaseReference myFirebaseRef = database.getReference();
                        myFirebaseRef.child("users").child(firebaseUser.getUid()).child("playlists")
                                .child(playlistName).child("songs").child(songId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText( getContext(),"???? x??a th??nh c??ng!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        // Hide menu option
                        ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                        songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);
                        // Refresh list song in playlist
                        ((MainActivity)getActivity()).songOnPlaylistFragment.onHiddenChanged(false);
                    }

                }


            }
        });

        // Upload song to cloud
        songMenuOptionUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionUpload.startAnimation(item_click);
            }
        });

        // Download song to device
        songMenuOptionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionDownload.startAnimation(item_click);
                downloadSong(songPath, songName+".mp3");
                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);
            }
        });

        // Delete a song in local
        songMenuOptionDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionDeleteFile.startAnimation(item_click);
                File file = new File(songPath);
                file.delete();
                ((MainActivity)getActivity()).songOnDeviceFragment.onHiddenChanged(false);
                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);
            }
        });

    }

    // Get and display playlists to which the selected song has not been added
    private void getPlaylist(){
        //
        arrPlaylist = new ArrayList<>();

        String songPath = bundle.getString("songPath");

        // Get playlist which the selected song has not been added from local
        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
        ArrayList<PlaylistModel> listPlaylist =  mydb.getPlaylistSongNotAdded(songPath);
        arrPlaylist.addAll(listPlaylist);

        // If signed in, get playlists which the selected song has not been added from cloud
        if(firebaseUser!=null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();;
            DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("playlists");
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()) { // Browse each playlist
                        boolean isAdded = false;
                        for (DataSnapshot dsSong : ds.getChildren()){ // Browse each child item of playlist
                            if(Objects.equals(dsSong.getKey(), "songs")){ // If meet item named song
                                for (DataSnapshot dsListSong : dsSong.getChildren()){ // Browse each song in item
                                    String tmpPath = (String) dsListSong.child("path").getValue(); // Get path of song in database
                                    if(Objects.equals(tmpPath, songPath)){ // Check if path of song in database not equal path of song is choosed
                                        isAdded = true;                    // Mean song is added in this playlist
                                    }
                                }
                            }
                        }
                        // if song is not added, add this playlist to list
                        if(!isAdded){
                            PlaylistModel pl = new PlaylistModel();
                            pl.setName((String) ds.child("name").getValue());
                            pl.setType((String) ds.child("type").getValue());
                            arrPlaylist.add(pl);
                        }

                    }
                    // Show list playlist
                    playlistListViewAdapter = new PlaylistToAddAdapter(arrPlaylist);
                    lvPlaylist.setAdapter(playlistListViewAdapter);
                }
                //
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //
                }

            });
        }
        // Show list playlist
        playlistListViewAdapter = new PlaylistToAddAdapter(arrPlaylist);
        lvPlaylist.setAdapter(playlistListViewAdapter);

    }

    // Create playlist dialog
    private void createPlaylistDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.create_playlist_dialog, null));
        AlertDialog dialog = builder.create();

        dialog.show();

        txtCreatePlaylistName = dialog.findViewById(R.id.playlist_name_create);

        btnConfirmCreatePlaylist = dialog.findViewById(R.id.btn_confirm_create_playlist);
        btnCancelCreatePlaylist = dialog.findViewById(R.id.btn_cancel_create_playlist);

        // If confirm create playlist
        btnConfirmCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = txtCreatePlaylistName.getText().toString();

                // If signed in, create playlists in cloud
                if(firebaseUser!=null){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();;
                    DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("playlists").child(playlistName);
                    myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                txtCreatePlaylistName.setError("Danh s??ch ph??t online n??y ???? t???n t???i!");
                                txtCreatePlaylistName.requestFocus();
                            }
                            else {
                                myFirebaseRef.child("name").setValue(playlistName);
                                myFirebaseRef.child("type").setValue("online");
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                // Else not signed in, create local playlist
                else {
                    PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                    ArrayList<PlaylistModel> playlists = mydb.getPlaylist();
                    //
                    boolean isExist = false;
                    for(int i=0; i<playlists.size(); i++){
                        if(Objects.equals(playlists.get(i).getName(), playlistName)){
                            isExist = true;
                            break;
                        }
                    }
                    if(isExist){
                        txtCreatePlaylistName.setError("Danh s??ch ph??t n??y ???? t???n t???i!");
                        txtCreatePlaylistName.requestFocus();
                    }
                    else {
                        mydb.createPlaylist(playlistName, "local");
                        dialog.dismiss();
                    }

                }

                // Close dialog and refresh list
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPlaylist();
                    }
                }, 2000);
            }
        });

        // Click cancel button
        btnCancelCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    // Download song
    public void downloadSong(String url, String fileName){
        //
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(fileName);
        request.setDescription("??ang t???i nh???c...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName);

        DownloadManager downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        if(downloadManager != null){
            request.allowScanningByMediaScanner();
            downloadManager.enqueue(request);
        }

    }

}


// Playlist to add Adapter
class PlaylistToAddAdapter extends BaseAdapter {

    final ArrayList<PlaylistModel> arrPlaylist;

    PlaylistToAddAdapter (ArrayList<PlaylistModel> arrPlaylist) {
        this.arrPlaylist = arrPlaylist;
    }

    @Override
    public int getCount() {
        return arrPlaylist.size();
    }

    @Override
    public Object getItem(int position) {
        return arrPlaylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewPlaylist;
        if (convertView == null) {
            viewPlaylist = View.inflate(parent.getContext(), R.layout.playlist_view, null);
        } else viewPlaylist = convertView;

        //Bind s??? li???u ph???n t??? v??o View
        PlaylistModel playlist = (PlaylistModel) getItem(position);
        String playlistName = (String) playlist.getName();


        TextView txtPlaylistName = viewPlaylist.findViewById(R.id.textView_songName);
        txtPlaylistName.setText(playlistName);

        return viewPlaylist;
    }
}
