package com.duongcong.androidmusic.Home.songondevice;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
    SongOnDeviceAdapter songListViewAdapter;
    ListView lvSong;

    List<SongModel> audioList;

    ImageButton btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_song_on_device, container, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
        } else {
            audioList = getAllSongFromDevice(getActivity().getApplicationContext());
            showList();
        }
    }

    //
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Button back
        btnBack = view.findViewById(R.id.btn_song_on_device_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).homeFragment);
            }
        });
        // Get all audio file from local device
        audioList = getAllSongFromDevice(getActivity().getApplicationContext());
        lvSong = view.findViewById(R.id.listViewSongOnDevice);

        showList();

        // Event on click to a song in list
        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).playNewPlaylist(arrSong, position);
            }
        });


    }

    // Show list
    public void showList(){
        arrSong = new ArrayList<>();
        // Add list SongModel to Arraylist
        arrSong.addAll(audioList);

        // Create adapter
        songListViewAdapter = new SongOnDeviceAdapter(arrSong, (MainActivity)getContext());
        // Set adapter for listview
        lvSong.setAdapter(songListViewAdapter);

        TextView txtViewEmpty = getView().findViewById(R.id.txtViewSongOnDeviceEmpty);
        // If list song is not empty, show button play playlist
        if(arrSong.size() > 0){
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.VISIBLE);
            txtViewEmpty.setVisibility(View.INVISIBLE);
            ((MainActivity)getActivity()).btnPlayPlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).playNewPlaylist(arrSong, 0);
                }
            });
        }
        else {
            ((MainActivity)getActivity()).btnPlayPlaylist.setVisibility(View.INVISIBLE);
            txtViewEmpty.setVisibility(View.VISIBLE);
        }
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
                    SongModel songModel = new SongModel();
                    String album = c.getString(1);
                    String artist = c.getString(2);

                    // Get file name from path
                    String name = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);

                    songModel.setId("null");
                    songModel.setName(name);
                    songModel.setAlbum(album);
                    songModel.setArtist(artist);
                    songModel.setPath(path);
                    songModel.setImage("null");
                    songModel.setCategory("null");
                    songModel.setDuration("null");
                    songModel.setType("local");

                    tempAudioList.add(songModel);
                }


            }
            c.close();
        }

        return tempAudioList;
    }

}


// Listview adapter
class SongOnDeviceAdapter extends BaseAdapter {

    final ArrayList<SongModel> arrSong;
    private Context mContext;


    SongOnDeviceAdapter (ArrayList<SongModel> arrSong, Context context) {
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
                    songHashMap.put("songImg", song.getImage());
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