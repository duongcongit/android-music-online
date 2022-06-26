package com.duongcong.androidmusic.Home.recent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.duongcong.androidmusic.DBHelper.RecentLocalDBHelper;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.R;

import java.util.ArrayList;
import java.util.Objects;

public class HomeRecentFragment extends Fragment {

    // ArrayList<PlaylistModel> arrSongRecent;
    // SongOnRecentAdapter songOnPlaylistAdapter;
    ListView lvSongRecent;

    ConstraintLayout btnClearRecentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvSongRecent = view.findViewById(R.id.listview_recent);
        showListSongRecent(view);

        // Button clear recent list
        btnClearRecentList = view.findViewById(R.id.btn_clear_recent);
        btnClearRecentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecentLocalDBHelper mydb = new RecentLocalDBHelper(getActivity().getApplicationContext());
                mydb.clearRecentList();
                // Refresh list
                showListSongRecent(view);
            }
        });

        // Refresh list when show tab
        ((MainActivity)getActivity()).homeFragment.mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // System.out.println(position);
                showListSongRecent(view);
            }
        });


    }


    private void showListSongRecent(View view){
        RecentLocalDBHelper mydb = new RecentLocalDBHelper(getActivity().getApplicationContext());
        ArrayList<SongModel> arrSongRecent = mydb.getRecentList();

        // Create adapter
        SongOnRecentAdapter songOnPlaylistAdapter =new SongOnRecentAdapter(arrSongRecent, getContext());
        // Set adapter for listview
        lvSongRecent.setAdapter(songOnPlaylistAdapter);
        lvSongRecent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) requireActivity()).playNewPlaylist(arrSongRecent, position);
            }
        });


        }




}



// Class adapter
class SongOnRecentAdapter extends BaseAdapter {

    final ArrayList<SongModel> arrSong;
    private Context mContext;


    SongOnRecentAdapter (ArrayList<SongModel> arrSong, Context context) {
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
        // return arrSong.get(position).getName();
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

        String songName   = song.getName();
        String songArtist = song.getArtist();
        String songImg    = song.getImage();
        // Set artist if it is <unknown>
        if(songArtist == null || songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }
        // Set song name if it is too long
        if(songName.length() > 33){
            songName = songName.substring(0, 33) + "...";
        }
        // Set view for each item in list view
        // Set view for listview item
        // Set avatar
        ImageView songAvatar = (ImageView) viewSong.findViewById(R.id.imageView_ic_song_avatar);
        String imgUrl = songImg;
        if(Objects.equals(song.getType(), "online")){
            // Picasso.get().load(imgUrl).placeholder(R.drawable.ic_music).into(songAvatar);
            Glide.with(mContext).load(imgUrl).placeholder(R.drawable.ic_music).centerCrop().into(songAvatar);
        }

        ((TextView) viewSong.findViewById(R.id.textView_songName)).setText(songName);
        ((TextView) viewSong.findViewById(R.id.textView_songArtist)).setText(songArtist);


        return viewSong;
    }
}