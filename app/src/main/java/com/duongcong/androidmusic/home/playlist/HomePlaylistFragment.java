package com.duongcong.androidmusic.home.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;

import java.util.ArrayList;
import java.util.List;

public class HomePlaylistFragment extends Fragment {

    ArrayList<String> arrPlaylist;
    PlaylistAdapter playlistListViewAdapter;
    ListView lvPlaylist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_playlist, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
        List<String> listPlaylist =  mydb.getPlaylist();

        arrPlaylist = new ArrayList<>();

        for (int i=0; i<listPlaylist.size(); i++){
            arrPlaylist.add(listPlaylist.get(i));
            System.out.println(listPlaylist.get(i));
        }

        playlistListViewAdapter = new PlaylistAdapter(arrPlaylist);
        lvPlaylist = view.findViewById(R.id.listViewPlaylist);

        lvPlaylist.setAdapter(playlistListViewAdapter);

        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String playlistName = playlistListViewAdapter.getItem(position);
                System.out.println(playlistName);
                Bundle bundle = new Bundle();
                bundle.putString("playlistName", playlistName);

                ((MainActivity)getActivity()).songOnPlaylistFragment.setArguments(bundle);

                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).songOnPlaylistFragment);

            }
        });

    }


}


class PlaylistAdapter extends BaseAdapter {

    final ArrayList<String> arrPlaylist;

    PlaylistAdapter (ArrayList<String> arrPlaylist) {
        this.arrPlaylist = arrPlaylist;
    }

    @Override
    public int getCount() {
        return arrPlaylist.size();
    }

    @Override
    public String getItem(int position) {
        return arrPlaylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewPlaylist;
        if (convertView == null) {
            viewPlaylist = View.inflate(parent.getContext(), R.layout.playlist_view, null);
        } else viewPlaylist = convertView;

        //Bind sữ liệu phần tử vào View
        String playlistName = (String) getItem(position);


        TextView txtPlaylistName = viewPlaylist.findViewById(R.id.textView_playlistName);
        txtPlaylistName.setText(playlistName);

        return viewPlaylist;
    }
}