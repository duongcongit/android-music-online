package com.duongcong.androidmusic.Home.Album;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumFragment extends Fragment {

    ArrayList<SongModel> arrSong;
    // SongAdapter songListViewAdapter;
    ListView lvSong;

    List<SongModel> audioList;

    ImageButton btnBack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
        } else {

        }
    }

    //
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {



    }


}


// Listview adapter
class SongsAdapter extends BaseAdapter {

    final ArrayList<SongModel> arrSong;
    private Context mContext;


    SongsAdapter (ArrayList<SongModel> arrSong, Context context) {
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
        if(songName.length() > 40){
            songName = songName.substring(0, 35) + "...";
        }

        // Set view for listview item
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
