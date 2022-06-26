package com.duongcong.androidmusic.Discovery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duongcong.androidmusic.DBHelper.PlaylistLocalDBHelper;
import com.duongcong.androidmusic.DBHelper.RecentLocalDBHelper;
import com.duongcong.androidmusic.Discovery.my_interface.DiscoveryItemClickListener;
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

public class DiscoveryFragment extends Fragment {

    private SongDiscoveryAdapter songDiscoveryAdapter ;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            // System.out.println("hidden");
        }
        else {
            getListSong(getView(), "Nhạc Remix");
            getListSong(getView(), "Nhạc Trẻ");
            getListSong(getView(), "Pop Ballad");
            getListSong(getView(), "Nhạc Rock");
            getListSong(getView(), "Thể loại khác");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Get and show songs by category
        getListSong(view, "Nhạc Remix");
        getListSong(view, "Nhạc Trẻ");
        getListSong(view, "Pop Ballad");
        getListSong(view, "Nhạc Rock");
        getListSong(view, "Thể loại khác");


    }


    /// Get list song from cloud by category and show
    private void getListSong(View view, String category){
        ArrayList<SongModel> audioList = new ArrayList<>();
        // Get songs from cloud
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myFirebaseRef = database.getReference().child("users");
        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dsUsers : dataSnapshot.getChildren()) {
                        for (DataSnapshot dsUserChildItems : dsUsers.getChildren()){
                            if(Objects.equals(dsUserChildItems.getKey(), "songs")){ // If meet item named song
                                for (DataSnapshot dsListSong : dsUserChildItems.getChildren()){ // Browse each song in item
                                    String thisSongCategory = (String) dsListSong.child("category").getValue();
                                    if(Objects.equals(thisSongCategory, category)){
                                        // System.out.println(dsListSong.child("name").getValue());
                                        String songId       = (String) dsListSong.child("id").getValue();
                                        String songName     = (String) dsListSong.child("name").getValue();
                                        String songPath     = (String) dsListSong.child("path").getValue();
                                        String songImg      = (String) dsListSong.child("image").getValue();
                                        String songAlbum    = (String) dsListSong.child("album").getValue();
                                        String songArtist   = (String) dsListSong.child("artist").getValue();
                                        String songCategory = (String) dsListSong.child("category").getValue();
                                        String songDuration = (String) dsListSong.child("duration").getValue();
                                        String songType     = (String) dsListSong.child("type").getValue();
                                        String timeUpload   = (String) dsListSong.child("timeUpload").getValue();

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
                                        song.setTimeUpload(timeUpload);

                                        // Add to list
                                        audioList.add(song);

                                        // System.out.println(songName);
                                        // System.out.println(timeUpload);
                                    }
                                }
                            }
                        }


                    }

                    // Show list song
                    showListSong(view, category, audioList);
                }
            }
            //
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });


    }

    // Func
    private void showListSong(View view, String category, ArrayList<SongModel> listSong){

        songDiscoveryAdapter = new SongDiscoveryAdapter(getContext(), listSong, new DiscoveryItemClickListener() {
            @Override
            public void onClickItemListener(ArrayList<SongModel> arrSong, int position) {
                ((MainActivity) requireActivity()).playNewPlaylist(arrSong, position);
            }
        });
        //
        RecyclerView recyclerSong = view.findViewById(R.id.recycleView_discovery_remix);

        if(category.equals("Nhạc Remix") || category.equals("Nhạc Dance")){
            // EDM remix
            recyclerSong = view.findViewById(R.id.recycleView_discovery_remix);
        }
        else if (category.equals("Nhạc Trẻ")){
            // Nhạc trẻ
            recyclerSong = view.findViewById(R.id.recycleView_discovery_song);

        }
        else if (category.equals("Pop Ballad")){
            recyclerSong = view.findViewById(R.id.recycleView_discovery_pop_ballad);

        }
        else if (category.equals("Nhạc Rock")){
            recyclerSong = view.findViewById(R.id.recycleView_discovery_rock);

        }
        else if (category.equals("Thể loại khác")){
            recyclerSong= view.findViewById(R.id.recycleView_discovery_rock);
        }

        //
        recyclerSong.setAdapter(songDiscoveryAdapter);
        recyclerSong.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // On item click

    }



}

// Adapter class
class SongDiscoveryAdapter extends RecyclerView.Adapter<SongDiscoveryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SongModel> arrSong;

    private DiscoveryItemClickListener discoveryItemClickListener;

    public SongDiscoveryAdapter(Context mContext, ArrayList<SongModel> arrSong, DiscoveryItemClickListener listener) {
        this.mContext = mContext;
        this.arrSong = arrSong;
        this.discoveryItemClickListener = listener;
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

        int songPos = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoveryItemClickListener.onClickItemListener(arrSong, songPos);
            }
        });
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
