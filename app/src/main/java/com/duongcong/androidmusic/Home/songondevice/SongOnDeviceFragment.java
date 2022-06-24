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

import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.Model.SongModel;
import com.duongcong.androidmusic.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SongOnDeviceFragment extends Fragment {

    ArrayList<SongModel> arrSong;
    SongAdapter songListViewAdapter;
    ListView lvSong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_on_device, container, false);
    }

    //
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get all audio file from local device
        final List<SongModel> audioList = getAllSongFromDevice(getActivity().getApplicationContext());

        arrSong = new ArrayList<>();
        // Add list SongModel to Arraylist
        arrSong.addAll(audioList);

        // Create adapter
        songListViewAdapter = new SongAdapter(arrSong, (MainActivity)getContext());
        // Set adapter for listivew
        lvSong = view.findViewById(R.id.listViewSongOnDevice);
        lvSong.setAdapter(songListViewAdapter);

        // Event on click to a song in list
        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get song data
                SongModel song = (SongModel) songListViewAdapter.getItem(position);
                String songArtist = song.getArtist();
                // Set artist if artist is <unknown>
                if(songArtist.equals("<unknown>")){
                    songArtist = "Unknown artist";
                }
                String songName = song.getName();
                // Set bundle to send song data to play music fragment
                Bundle bundle = new Bundle();
                bundle.putString("playType", "new play");
                bundle.putString("songId", song.getId());
                bundle.putString("songName",songName);
                bundle.putString("songPath", song.getPath());
                bundle.putString("songArtist",songArtist);
                bundle.putString("songAlbum",song.getAlbum());
                bundle.putString("songCategory",song.getCategory());
                bundle.putString("songDuration",song.getDuration());
                bundle.putString("songType", song.getType());
                ((MainActivity)getActivity()).playMusicFragment.setArguments(bundle);

                // Display play music fragment
                ((MainActivity)getActivity()).displayPlayMusicFragment();

                // Set view and animation for playing bar
                ((MainActivity)getActivity()).animImgSongPlaying.start();
                TextView txtSongPlayingName, txtSongPlayingArtist;
                txtSongPlayingName = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_name);
                txtSongPlayingArtist = ((MainActivity)getActivity()).findViewById(R.id.txt_song_playing_artist);
                txtSongPlayingName.setText(songName);
                txtSongPlayingArtist.setText(songArtist);
                // Text animation while playing
                txtSongPlayingName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                txtSongPlayingName.setSelected(true);
                txtSongPlayingName.setSingleLine(true);

            }
        });

    }

    // Function get all audio file on device
    public List<SongModel> getAllSongFromDevice(final Context context) {
        final List<SongModel> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                if(path.endsWith(".mp3")){
                    SongModel audioModel = new SongModel();
                    String album = c.getString(1);
                    String artist = c.getString(2);

                    // Get file name from path
                    String name = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);

                    audioModel.setId("null");
                    audioModel.setName(name);
                    audioModel.setAlbum(album);
                    audioModel.setArtist(artist);
                    audioModel.setPath(path);
                    audioModel.setCategory("null");
                    audioModel.setDuration("null");
                    audioModel.setType("local");

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

    final ArrayList<SongModel> arrSong;
    private Context mContext;


    SongAdapter (ArrayList<SongModel> arrSong, Context context) {
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
        if(songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }
        // Set song name if is too long
        if(songName.length() > 40){
            songName = songName.substring(0, 35) + "...";
        }

        // Set view for listview item
        ((TextView) viewSong.findViewById(R.id.textView_playlistName)).setText(songName);
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