package com.duongcong.androidmusic.Home.songondevice;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.Model.LocalSongModel;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;

import java.util.ArrayList;
import java.util.List;

public class SongOnDeviceFragment extends Fragment {

    ArrayList<LocalSongModel> arrSong;
    SongAdapter songListViewAdapter;
    ListView lvSong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_on_device, container, false);
    }


    //
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final List<LocalSongModel> audioList = getAllSongFromDevice(getActivity().getApplicationContext());

        arrSong = new ArrayList<>();
        for (int i=0; i<audioList.size(); i++){
            // String name = audioList.get(i).getName();
            arrSong.add(audioList.get(i));
        }

        // Set adapter
        songListViewAdapter = new SongAdapter(arrSong, (MainActivity)getContext());
        lvSong = view.findViewById(R.id.listViewSongOnDevice);
        lvSong.setAdapter(songListViewAdapter);

        // On click a song in list
        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocalSongModel song = (LocalSongModel) songListViewAdapter.getItem(position);

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

    // Get all song on device
    public List<LocalSongModel> getAllSongFromDevice(final Context context) {

        final List<LocalSongModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                if(path.endsWith(".mp3")){
                    LocalSongModel audioModel = new LocalSongModel();
                    String album = c.getString(1);
                    String artist = c.getString(2);

                    String name = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);

                    audioModel.setName(name);
                    audioModel.setAlbum(album);
                    audioModel.setArtist(artist);
                    audioModel.setPath(path);

                    tempAudioList.add(audioModel);
                }


            }
            c.close();
        }

        return tempAudioList;
    }



}


// Listview adapter
class SongAdapter extends BaseAdapter {

    final ArrayList<LocalSongModel> arrSong;
    private Context mContext;


    SongAdapter (ArrayList<LocalSongModel> arrSong, Context context) {
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

        //
        LocalSongModel song = (LocalSongModel) getItem(position);

        String songName     = song.getName();
        String songArtist   = song.getArtist();
        String songAlbum    = song.getAlbum();
        String songPath     = song.getPath();

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

                    ((MainActivity)mContext).displaySongMenuOptionFragment("local", "null", song.getName(), song.getArtist(), song.getAlbum(), song.getPath());
                }
            }
        });

        return viewSong;
    }
}