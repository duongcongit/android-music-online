package com.duongcong.androidmusic.Home.playlist;

import android.content.Context;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SongOnPlaylistFragment extends Fragment {

    ArrayList<SongModel> arrSong;
    SongOnPlaylistAdapter songOnPlaylistAdapter;
    ListView lvSong;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String thisPlaylistName;
    String thisPlaylistType;

    ImageButton btnBack;
    TextView txtViewPlaylistName;


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
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
        } else {
            // Get playlist info
            Bundle bundle = this.getArguments();
            if(bundle!=null){
                thisPlaylistName = bundle.getString("playlistName");
                thisPlaylistType = bundle.getString("type");
            }
            // Set textview playlist name
            txtViewPlaylistName.setText(thisPlaylistName);
            // Get and show list song in playlist
            getListSong(getView());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Button back
        btnBack = view.findViewById(R.id.btn_song_on_playlist_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).homeFragment);
            }
        });

        // Get playlist info
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            thisPlaylistName = bundle.getString("playlistName");
            thisPlaylistType = bundle.getString("type");
        }


        // Set textview playlist name
        txtViewPlaylistName = view.findViewById(R.id.playlistName_fragment);
        txtViewPlaylistName.setText(thisPlaylistName);

        // Get and show list song in playlist
        lvSong = view.findViewById(R.id.listViewSongOnPlaylist);
        getListSong(view);

        // On click a song
        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).playNewPlaylist(arrSong, position);
            }
        });


    }

    private void getListSong(View view){
        Bundle bundle = this.getArguments();
        if(bundle != null){
            // System.out.println(thisPlaylistName + thisPlaylistType);
            // Show list song in local playlist
            if(Objects.equals(thisPlaylistType, "local")){
                PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                List<SongModel> audioList = mydb.getPlaylistData(thisPlaylistName);
                showListSong(view, audioList);
            }
            // Show list song in online playlist
            else if(Objects.equals(thisPlaylistType, "online")){
                List<SongModel> audioList = new ArrayList<>();
                // Get songs from cloud
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("playlists").child(thisPlaylistName).child("songs");
                myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            // Get song info
                            for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                String songId       = (String) ds.child("id").getValue();
                                String songName     = (String) ds.child("name").getValue();
                                String songPath     = (String) ds.child("path").getValue();
                                String songAlbum    = (String) ds.child("album").getValue();
                                String songArtist   = (String) ds.child("artist").getValue();
                                String songCategory = (String) ds.child("category").getValue();
                                String songDuration = (String) ds.child("duration").getValue();
                                String songType     = (String) ds.child("type").getValue();
                                // System.out.println(songId);

                                // Create a model
                                SongModel song = new SongModel();
                                song.setId(songId);
                                song.setName(songName);
                                song.setPath(songPath);
                                song.setAlbum(songAlbum);
                                song.setArtist(songArtist);
                                song.setCategory(songCategory);
                                song.setDuration(songDuration);
                                song.setType(songType);

                                // Add to list
                                audioList.add(song);
                            }
                        }
                        // Show list song
                        showListSong(view, audioList);

                    }
                    //
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //
                    }
                });
            }

            //
        }
        //

    }

    // Show list song
    private void showListSong(View view, List<SongModel> audioList){
        arrSong = new ArrayList<>();

        // Add List SongModel to Arraylist
        arrSong.addAll(audioList);

        // Create adapter
        songOnPlaylistAdapter = new SongOnPlaylistAdapter(arrSong, (MainActivity)getContext(), thisPlaylistName, thisPlaylistType);
        // Set adapter for listview
        lvSong.setAdapter(songOnPlaylistAdapter);

        // songOnPlaylistAdapter.notifyDataSetChanged();

        TextView txtPlaylistEmpty = view.findViewById(R.id.txtViewPlaylistEmpty);
        // If list song is not empty, show button play playlist
        if(arrSong.size() > 0){
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.VISIBLE);
            txtPlaylistEmpty.setVisibility(View.INVISIBLE);
            ((MainActivity)getActivity()).btnPlayPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).playNewPlaylist(arrSong, 0);
                }
            });
        }
        else {
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
            txtPlaylistEmpty.setVisibility(View.VISIBLE);
        }

    }

}

// Class adapter
class SongOnPlaylistAdapter extends BaseAdapter {

    final ArrayList<SongModel> arrSong;
    private Context mContext;

    private String thisPlaylistName;
    private String thisPlaylistType;

    SongOnPlaylistAdapter (ArrayList<SongModel> arrSong, Context context, String playlistName, String playlistType) {
        this.arrSong = arrSong;
        this.mContext = context;
        this.thisPlaylistName = playlistName;
        this.thisPlaylistType = playlistType;
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

        // Get a song as Model
        SongModel song = (SongModel) getItem(position);

        String songName = song.getName();
        String songArtist = song.getArtist();
        // Set artist if it is <unknown>
        if(songArtist == null || songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }
        // Set song name if it is too long
        if(songName.length() > 33){
            songName = songName.substring(0, 33) + "...";
        }
        // Set view for each item in list view
        ((TextView) viewSong.findViewById(R.id.textView_songName)).setText(songName);
        ((TextView) viewSong.findViewById(R.id.textView_songArtist)).setText(songArtist);

        // Display option menu when click to button
        ((ImageButton) viewSong.findViewById(R.id.btn_song_more_option)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {
                    HashMap<String, String> songHashMap = new HashMap<>();
                    songHashMap.put("isInPlaylist", "yes");
                    songHashMap.put("playlistName", thisPlaylistName);
                    songHashMap.put("playlistType", thisPlaylistType);
                    songHashMap.put("songId", song.getId());
                    songHashMap.put("songName", song.getName());
                    songHashMap.put("songPath", song.getPath());
                    songHashMap.put("songAlbum", song.getAlbum());
                    songHashMap.put("songArtist", song.getArtist());
                    songHashMap.put("songCategory", song.getCategory());
                    songHashMap.put("songDuration", song.getDuration());
                    songHashMap.put("songType", song.getType());

                    // Call function from MainActivity
                    ((MainActivity)mContext).displaySongMenuOptionFragment(songHashMap);

                }
            }
        });

        return viewSong;
    }
}