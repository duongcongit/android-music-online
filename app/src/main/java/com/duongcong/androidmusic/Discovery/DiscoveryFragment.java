package com.duongcong.androidmusic.Discovery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duongcong.androidmusic.DBHelper.PlaylistLocalDBHelper;
import com.duongcong.androidmusic.DBHelper.RecentLocalDBHelper;
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

public class DiscoveryFragment extends Fragment {

    private ArrayList<SongModel> arrSong;
    private RecyclerView mRecyclerSong;
    private SongDiscoveryAdapter songDiscoveryAdapter ;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        // mRecyclerSong = view.findViewById(R.id.recycleView_discovery_remix);
        // arrSong = new ArrayList<>();

        RecentLocalDBHelper mydb = new RecentLocalDBHelper(getContext());
        // arrSong = mydb.getRecentList();

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        songDiscoveryAdapter = new SongDiscoveryAdapter(getContext(), arrSong);

        //Test

        // remix

        getListSong(view, "EDM Remix");
        getListSong(view, "Nhac tre");
        getListSong(view, "Nhạc chill");
        getListSong(view, "Rock");


    }




    /// Get list song===========================
    private void getListSong(View view, String playlistName){
        ArrayList<SongModel> audioList = new ArrayList<>();
        // Get songs from cloud
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("playlists").child(playlistName).child("songs");
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // Get song info
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        String songId       = (String) ds.child("id").getValue();
                        String songName     = (String) ds.child("name").getValue();
                        String songPath     = (String) ds.child("path").getValue();
                        String songImg      = (String) ds.child("image").getValue();
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
                        song.setImage(songImg);
                        song.setAlbum(songAlbum);
                        song.setArtist(songArtist);
                        song.setCategory(songCategory);
                        song.setDuration(songDuration);
                        song.setType(songType);

                        // Add to list
                        audioList.add(song);
                        // arrSong.add(song);

                        System.out.println(songName);
                    }

                    showListSong(view, playlistName, audioList);

                    // arrSong = audioList;
                }
                // Show list song


            }
            //
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });

    }


    private void showListSong(View view, String playlistName, ArrayList<SongModel> listSong){


        if(playlistName.equals("EDM Remix")){
            System.out.println("Nhac ======= REMIX");
            // remix
            songDiscoveryAdapter = new SongDiscoveryAdapter(getContext(), listSong);
            mRecyclerSong = view.findViewById(R.id.recycleView_discovery_remix);
            mRecyclerSong.setAdapter(songDiscoveryAdapter);
            mRecyclerSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        else if (playlistName.equals("Nhac tre")){
            System.out.println("Nhac ======= Tre");

            // Trẻ
            songDiscoveryAdapter = new SongDiscoveryAdapter(getContext(), listSong);
            RecyclerView a = view.findViewById(R.id.recycleView_discovery_song);
            a.setAdapter(songDiscoveryAdapter);
            a.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        }
        else if (playlistName.equals("Nhạc chill")){
            System.out.println("Nhac ======= Chill");
            // Chill
            songDiscoveryAdapter = new SongDiscoveryAdapter(getContext(), listSong);
            RecyclerView b = view.findViewById(R.id.recycleView_discovery_pop_ballad);
            b.setAdapter(songDiscoveryAdapter);
            b.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        }
        else if (playlistName.equals("Rock")){
            System.out.println("Nhac ======= ROCK");
            // Rock
            songDiscoveryAdapter = new SongDiscoveryAdapter(getContext(), listSong);
            RecyclerView c = view.findViewById(R.id.recycleView_discovery_rock);
            c.setAdapter(songDiscoveryAdapter);
            c.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        }


    }



}










// Adapter class
class SongDiscoveryAdapter extends RecyclerView.Adapter<SongDiscoveryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SongModel> arrSong;

    public SongDiscoveryAdapter(Context mContext, ArrayList<SongModel> arrSong) {
        this.mContext = mContext;
        this.arrSong = arrSong;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View songView = inflater.inflate(R.layout.song_view_discovery, parent, false);
        ViewHolder viewHolder = new ViewHolder(songView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongModel song = arrSong.get(position);
        Glide.with(mContext).load(song.getImage()).placeholder(R.drawable.ic_music).centerCrop().into(holder.imgSong);
        holder.txtSongName.setText(song.getName());
    }

    @Override
    public int getItemCount() {
        return arrSong.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgSong;
        private TextView txtSongName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.img_song_discovery);
            txtSongName = itemView.findViewById(R.id.txt_song_name_discovery);
        }
    }
}
