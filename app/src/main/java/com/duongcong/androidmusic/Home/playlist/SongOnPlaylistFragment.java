package com.duongcong.androidmusic.Home.playlist;

import android.content.Context;
import android.os.Bundle;
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
import com.duongcong.androidmusic.Model.LocalSongModel;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;

import java.util.ArrayList;
import java.util.List;

public class SongOnPlaylistFragment extends Fragment {

    ArrayList<LocalSongModel> arrSong;
    SongOnPlaylistAdapter songOnPlaylistAdapter;
    ListView lvSong;

    private String thisPlaylistName;


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

        Bundle bundle = this.getArguments();
        if(bundle != null){
            thisPlaylistName = bundle.getString("playlistName");
            PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
            final List<LocalSongModel> audioList = mydb.getPlaylistData(thisPlaylistName);

            arrSong = new ArrayList<>();

            for (int i=0; i<audioList.size(); i++){
                arrSong.add(audioList.get(i));
            }

            songOnPlaylistAdapter = new SongOnPlaylistAdapter(arrSong, (MainActivity)getContext(), thisPlaylistName);
            lvSong = view.findViewById(R.id.listViewSongOnPlaylist);
            lvSong.setAdapter(songOnPlaylistAdapter);
        }


        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocalSongModel song = (LocalSongModel) songOnPlaylistAdapter.getItem(position);

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
            PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
            final List<LocalSongModel> audioList = mydb.getPlaylistData(thisPlaylistName);

            arrSong = new ArrayList<>();

            for (int i=0; i<audioList.size(); i++){
                arrSong.add(audioList.get(i));
            }

            songOnPlaylistAdapter = new SongOnPlaylistAdapter(arrSong, (MainActivity)getContext(), thisPlaylistName);
            lvSong = view.findViewById(R.id.listViewSongOnPlaylist);
            lvSong.setAdapter(songOnPlaylistAdapter);
        }

        //

    }

}

class SongOnPlaylistAdapter extends BaseAdapter {

    final ArrayList<LocalSongModel> arrSong;
    private Context mContext;

    private String thisPlaylistName;

    SongOnPlaylistAdapter (ArrayList<LocalSongModel> arrSong, Context context, String playlistName) {
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
        LocalSongModel song = (LocalSongModel) getItem(position);

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