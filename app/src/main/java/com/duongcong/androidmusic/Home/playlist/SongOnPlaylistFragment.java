package com.duongcong.androidmusic.Home.playlist;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.DBHelper.PlaylistLocalDBHelper;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SongOnPlaylistFragment extends Fragment {

    ArrayList<SongModel> arrSong;
    SongOnPlaylistAdapter songOnPlaylistAdapter;
    ListView lvSong;
    List<SongModel> audioList;

    private String thisPlaylistName;
    private String thisPlaylistType;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_song_on_playlist, container, false);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //
        } else {
            getListSong(getView());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        audioList = new ArrayList<>();
        lvSong = view.findViewById(R.id.listViewSongOnPlaylist);
        getListSong(view);

        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SongModel song = (SongModel) songOnPlaylistAdapter.getItem(position);

                String songArtist = song.getArtist();

                if(songArtist.equals("<unknown>")){
                    songArtist = "Unknown artist";
                }

                String songName = song.getName();

                Bundle bundle = new Bundle();
                bundle.putString("playType", "new play");
                bundle.putString("songPath", song.getPath());
                bundle.putString("songName",songName);
                bundle.putString("songArtist",songArtist);
                bundle.putString("songAlbum",song.getAlbum());

                ((MainActivity)getActivity()).playMusicFragment.setArguments(bundle);

                ((MainActivity)getActivity()).displayPlayMusicFragment();

                ((MainActivity)getActivity()).animImgSongPlaying.start();
                TextView txtSongPlayingName, txtSongPlayingArtist;
                txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
                txtSongPlayingArtist = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_artist);
                txtSongPlayingName.setText(songName);
                txtSongPlayingArtist.setText(songArtist);
                // Animation text
                txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                txtSongPlayingName.setSelected(true);
                txtSongPlayingName.setSingleLine(true);

            }
        });

    }


    private void getListSong(View view){
        Bundle bundle = this.getArguments();
        if(bundle != null){
            thisPlaylistName = bundle.getString("playlistName");
            thisPlaylistType = bundle.getString("type");

            System.out.println(thisPlaylistName + thisPlaylistType);

            // Show list song in local playlist
            if(Objects.equals(thisPlaylistType, "local")){
                PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                audioList = mydb.getPlaylistData(thisPlaylistName);

                showListSong(view, audioList);
            }
            // Show list song in online playlist
            else if(Objects.equals(thisPlaylistType, "online")){
                // System.out.println("OKKKKKK");
                // Get songs from cloud
                FirebaseDatabase database = FirebaseDatabase.getInstance();;
                DatabaseReference myFirebaseRef = database.getReference();
                myFirebaseRef.child("users").child(firebaseUser.getUid()).child("playlists").child(thisPlaylistName).child("songs").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            String songName   = (String) ds.child("name").getValue();
                            String songPath = (String) ds.child("path").getValue();
                            String songArtist = (String) ds.child("artist").getValue();
                            String songAlbum = (String) ds.child("album").getValue();
                            //System.out.println(songName + songPath +songArtist + songAlbum);

                            // Create a model
                            SongModel song = new SongModel();
                            song.setName(songName);
                            song.setPath(songPath);
                            song.setArtist(songArtist);
                            song.setAlbum(songAlbum);

                            // Add to list
                            audioList.add(song);

                        }
                    }
                    //
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //
                    }

                });

                // Wait for receive data from database and show
                final Handler handler = new Handler();
                for (int i=0; i<10000; i+=200){
                    final int a = i;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // System.out.println(a);
                            showListSong(view, audioList);
                        }
                    }, a);

                }



            }

            //

        }

        //

    }

    // Show list song
    private void showListSong(View view, List<SongModel> audioList){
        arrSong = new ArrayList<>();

        for (int i=0; i<audioList.size(); i++){
            arrSong.add(audioList.get(i));
        }

        songOnPlaylistAdapter = new SongOnPlaylistAdapter(arrSong, (MainActivity)getContext(), thisPlaylistName);
        lvSong.setAdapter(songOnPlaylistAdapter);
    }

}

// Class adapter
class SongOnPlaylistAdapter extends BaseAdapter {

    final ArrayList<SongModel> arrSong;
    private Context mContext;

    private String thisPlaylistName;

    SongOnPlaylistAdapter (ArrayList<SongModel> arrSong, Context context, String playlistName) {
        this.arrSong = arrSong;
        this.mContext = context;
        this.thisPlaylistName = playlistName;
    }

    @Override
    public int getCount() {
        return arrSong.size();
    }

    @Override
    public Object getItem(int position) {
        return arrSong.get(position);
    }

    @Override
    public long getItemId(int position) {
        // return arrSong.get(position).getaName();
        return 4;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewSong;
        if (convertView == null) {
            viewSong = View.inflate(parent.getContext(), R.layout.song_view, null);
        } else {
            viewSong = convertView;
        }

        //
        SongModel song = (SongModel) getItem(position);

        String songName = song.getName();
        String songArtist = song.getArtist();

        if(songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }

        if(songName.length() > 40){
            songName = songName.substring(0, 35) + "...";
        }

        ((TextView) viewSong.findViewById(R.id.textView_playlistName)).setText(songName);
        ((TextView) viewSong.findViewById(R.id.textView_songArtist)).setText(songArtist);

        // Display option menu when click to button
        ((ImageButton) viewSong.findViewById(R.id.btn_song_more_option)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {

                    ((MainActivity)mContext).displaySongMenuOptionFragment("local", thisPlaylistName, song.getName(), song.getArtist(), song.getAlbum(), song.getPath());

                }
            }
        });

        return viewSong;
    }
}