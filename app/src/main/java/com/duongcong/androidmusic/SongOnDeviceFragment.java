package com.duongcong.androidmusic;

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
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class SongOnDeviceFragment extends Fragment {

    ArrayList<AudioModel> arrSong;
    SongAdapter songListViewAdapter;
    ListView lvSong;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_on_device, container, false);
    }

    //



    public List<AudioModel> getAllAudioFromDevice(final Context context) {

        final List<AudioModel> tempAudioList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.AudioColumns.DATA, MediaStore.Audio.AudioColumns.ALBUM, MediaStore.Audio.ArtistColumns.ARTIST,};
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);

        if (c != null) {
            while (c.moveToNext()) {
                String path = c.getString(0);
                if(path.endsWith(".mp3")){
                    AudioModel audioModel = new AudioModel();
                    String album = c.getString(1);
                    String artist = c.getString(2);

                    String name = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);

                    audioModel.setaName(name);
                    audioModel.setaAlbum(album);
                    audioModel.setaArtist(artist);
                    audioModel.setaPath(path);

                    tempAudioList.add(audioModel);
                }


            }
            c.close();
        }

        return tempAudioList;
    }



    //



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final List<AudioModel> audioList = getAllAudioFromDevice(getActivity().getApplicationContext());

        arrSong = new ArrayList<>();

        for (int i=0; i<audioList.size(); i++){
            // String name = audioList.get(i).aName;
            arrSong.add(audioList.get(i));
        }

        songListViewAdapter = new SongAdapter(arrSong);
        lvSong = view.findViewById(R.id.listViewSongOnDevice);

        lvSong.setAdapter(songListViewAdapter);


        lvSong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AudioModel song = (AudioModel) songListViewAdapter.getItem(position);

                String songArtist = song.getaArtist();

                if(songArtist.equals("<unknown>")){
                    songArtist = "Unknown artist";
                }

                Bundle bundle = new Bundle();
                bundle.putString("playType", "new play");
                bundle.putString("songPath", song.getaPath());
                bundle.putString("songName",song.getaName());
                bundle.putString("songArtist",songArtist);
                bundle.putString("songAlbum",song.getaAlbum());

                ((MainActivity)getActivity()).playMusicActivity.setArguments(bundle);

                ((MainActivity)getActivity()).displayPlayMusicFragment();

            }
        });


    }


}


class SongAdapter extends BaseAdapter {

    final ArrayList<AudioModel> arrSong;

    SongAdapter (ArrayList<AudioModel> arrSong) {
        this.arrSong = arrSong;
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
        //convertView là View của phần tử ListView, nếu convertView != null nghĩa là
        //View này được sử dụng lại, chỉ việc cập nhật nội dung mới
        //Nếu null cần tạo mới

        View viewSong;
        if (convertView == null) {
            viewSong = View.inflate(parent.getContext(), R.layout.song_view, null);
        } else viewSong = convertView;

        //Bind sữ liệu phần tử vào View
        AudioModel song = (AudioModel) getItem(position);

        String songArtist = song.getaArtist();

        if(songArtist.equals("<unknown>")){
            songArtist = "Unknown artist";
        }

        ((TextView) viewSong.findViewById(R.id.textView_songName)).setText(song.getaName());
        ((TextView) viewSong.findViewById(R.id.textView_songArtist)).setText(songArtist);

        return viewSong;
    }
}