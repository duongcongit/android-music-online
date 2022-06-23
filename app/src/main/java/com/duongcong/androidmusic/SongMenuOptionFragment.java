package com.duongcong.androidmusic;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.DBHelper.PlaylistLocalDBHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SongMenuOptionFragment extends Fragment {


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

    ArrayList<String> arrPlaylist;
    PlaylistToAddAdapter playlistListViewAdapter;
    ListView lvPlaylist;
    ConstraintLayout menuPlaylistToAdd;

    ConstraintLayout btnMenuCreatePlaylist;

    EditText txtCreatePlaylistName;
    Button btnConfirmCreatePlaylist, btnCancelCreatePlaylist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout =  inflater.inflate(R.layout.fragment_song_menu_option, container, false);

        // Hide bottom navigation bar and playing song bar
        ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
        ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_show);
            }
        }, 250);

        bundle = this.getArguments();

        //
        return rootLayout;


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //
            ((MainActivity)getActivity()).navigation.setVisibility(View.VISIBLE);
            if(((MainActivity)getActivity()).playMusicFragment.mediaPlayer.isPlaying()){
                ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.VISIBLE);
            }
        } else {

            bundle = this.getArguments();

            // Hide bottom navigation bar and playing song bar
            ((MainActivity)getActivity()).navigation.setVisibility(View.GONE);
            ((MainActivity)getActivity()).songPlayingBar.setVisibility(View.GONE);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_show);
                }
            }, 250);

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        //
        songMenuOption                      = view.findViewById(R.id.song_menu_option);

        // Menu container
        menuContainer = view.findViewById(R.id.song_menu_option_view_container);

        // Menu items
        songMenuOptionRemoveFromUpload      = view.findViewById(R.id.menu_option_rm_from_upload);
        songMenuOptionAddToPlaylist         = view.findViewById(R.id.menu_option_add_to_playlist);
        songMenuOptionRemoveFromPlaylist    = view.findViewById(R.id.menu_option_rm_from_playlist);
        songMenuOptionUpload                = view.findViewById(R.id.menu_option_upload);
        songMenuOptionDownload              = view.findViewById(R.id.menu_option_download);
        songMenuOptionDeleteFile            = view.findViewById(R.id.menu_option_delete_file);
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
                //
                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);

                //
            }
        });

        songMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        // ITEMS CLICK

        // Remove song from uploaded
        songMenuOptionRemoveFromUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionRemoveFromUpload.startAnimation(item_click);
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
                        String playlistName = playlistListViewAdapter.getItem(position);
                        if(bundle != null){
                            if(bundle.getString("type") == "local" && playlistName != "null"){
                                String songPath = bundle.getString("songPath");
                                String songName = bundle.getString("songName");
                                String songArtist = bundle.getString("songArtist");
                                String songAlbum = bundle.getString("songAlbum");

                                PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                                mydb.addSongToPlaylist(playlistName, songName, songArtist, songAlbum, songPath);

                                ((MainActivity)getActivity()).hideSongMenuOptionFragment();
                                songMenuOption.setBackgroundResource(R.drawable.menu_option_hide_area_background_hide);

                            }
                            else if(bundle.getString("online") == "local" && playlistName != "null"){
                                //
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

                    String playlistName = bundle.getString("playlistName");
                    if(bundle.getString("type") == "local" && playlistName != "null"){
                        String songPath = bundle.getString("songPath");
                        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                        mydb.deleteSongFromPlaylist(playlistName, songPath);
                    }
                    else if(bundle.getString("type") == "online"){
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
            }
        });

        // Delete a song in local
        songMenuOptionDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songMenuOptionDeleteFile.startAnimation(item_click);
            }
        });


    }


    // Get and show playlist
    private void getPlaylist(){

        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
        List<String> listPlaylist =  mydb.getPlaylistSongNotAdded(bundle.getString("songPath"));

        arrPlaylist = new ArrayList<>();

        for (int i=0; i<listPlaylist.size(); i++){
            arrPlaylist.add(listPlaylist.get(i));
            // System.out.println(listPlaylist.get(i));
        }

        playlistListViewAdapter = new PlaylistToAddAdapter(arrPlaylist);

        lvPlaylist.setAdapter(playlistListViewAdapter);
    }


    // Create playlist
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

        btnConfirmCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = txtCreatePlaylistName.getText().toString();

                PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                mydb.createPlaylist(playlistName, "local");
                dialog.dismiss();
                getPlaylist();
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


}


// Playlist to add Adapter
class PlaylistToAddAdapter extends BaseAdapter {

    final ArrayList<String> arrPlaylist;

    PlaylistToAddAdapter (ArrayList<String> arrPlaylist) {
        this.arrPlaylist = arrPlaylist;
    }

    @Override
    public int getCount() {
        return arrPlaylist.size();
    }

    @Override
    public String getItem(int position) {
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

        //Bind sữ liệu phần tử vào View
        String playlistName = (String) getItem(position);


        TextView txtPlaylistName = viewPlaylist.findViewById(R.id.textView_playlistName);
        txtPlaylistName.setText(playlistName);

        return viewPlaylist;
    }
}
