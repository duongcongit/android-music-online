package com.duongcong.androidmusic.Home.Songs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
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

public class SongsFragment extends Fragment {

    ArrayList<SongModel> arrSong;
    // MySongsAdapter songListViewAdapter;
    ListView lvSong;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    ImageButton btnBack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songs, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
        } else {
            getListSong(getView());
        }
    }

    //
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Button back
        btnBack = view.findViewById(R.id.btnMySongBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).homeFragment);
            }
        });

        // Get and show list song in playlist
        lvSong = view.findViewById(R.id.listViewMySongs);
        getListSong(view);

        // On click a song
        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).playNewPlaylist(arrSong, position);
            }
        });


    }

    // Get list song
    private void getListSong(View view){
        List<SongModel> audioList = new ArrayList<>();
        // Get songs from cloud
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("songs");
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

    // Show list song
    private void showListSong(View view, List<SongModel> audioList){
        arrSong = new ArrayList<>();

        // Add List SongModel to Arraylist
        arrSong.addAll(audioList);

        // Create adapter
        MySongsAdapter mySongsAdapter = new MySongsAdapter(arrSong, (MainActivity)getContext());
        // Set adapter for listview
        lvSong.setAdapter(mySongsAdapter);

        // songOnPlaylistAdapter.notifyDataSetChanged();

        // TextView txtPlaylistEmpty = view.findViewById(R.id.txtViewPlaylistEmpty);
        // If list song is not empty, show button play playlist
        if(arrSong.size() > 0){
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.VISIBLE);
            // txtPlaylistEmpty.setVisibility(View.INVISIBLE);
            ((MainActivity)getActivity()).btnPlayPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).playNewPlaylist(arrSong, 0);
                }
            });
        }
        else {
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
            // txtPlaylistEmpty.setVisibility(View.VISIBLE);
        }

    }

}



// Listview adapter
class MySongsAdapter extends BaseAdapter {

    final ArrayList<SongModel> arrSong;
    private Context mContext;


    MySongsAdapter (ArrayList<SongModel> arrSong, Context context) {
        this.arrSong = arrSong;
        this.mContext = context;
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

        // Get song data
        SongModel song = (SongModel) getItem(position);
        String songName     = song.getName();
        String songArtist   = song.getArtist();

        // Set artist if artist is <unknown>
        if(songArtist == null || songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }
        // Set song name if is too long
        if(songName.length() > 33){
            songName = songName.substring(0, 33) + "...";
        }

        // Set view for listview item
        ImageView songAvatar = (ImageView) viewSong.findViewById(R.id.imageView_ic_song_avatar);
        String url = "https://firebasestorage.googleapis.com/v0/b/androidmusic-3d470.appspot.com/o/images%2F1656180919139.jpg?alt=media&token=ef06db58-de11-43e4-b1ba-091f5f530c34";
        // Picasso.get().load(url).into(songAvatar);

        Glide.with(mContext).load(R.raw.ic_audio_wave_unscreen).into(songAvatar);

        ((TextView) viewSong.findViewById(R.id.textView_songName)).setText(songName);
        ((TextView) viewSong.findViewById(R.id.textView_songArtist)).setText(songArtist);

        // Display option menu when click to button
        ((ImageButton) viewSong.findViewById(R.id.btn_song_more_option)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof MainActivity) {
                    HashMap<String, String> songHashMap = new HashMap<>();
                    songHashMap.put("isInPlaylist", "no");
                    songHashMap.put("playlistName", "null");
                    songHashMap.put("playlistType", "null");
                    songHashMap.put("songId", song.getId());
                    songHashMap.put("songName", song.getName());
                    songHashMap.put("songPath", song.getPath());
                    songHashMap.put("songAlbum", song.getAlbum());
                    songHashMap.put("songArtist", song.getArtist());
                    songHashMap.put("songCategory", song.getCategory());
                    songHashMap.put("songDuration", song.getDuration());
                    songHashMap.put("songType", song.getType());

                    ((MainActivity)mContext).displaySongMenuOptionFragment(songHashMap);
                }
            }
        });

        return viewSong;
    }
}
