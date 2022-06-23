package com.duongcong.androidmusic.Home.playlist;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.duongcong.androidmusic.DBHelper.PlaylistLocalDBHelper;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.Model.PlaylistModel;
import com.duongcong.androidmusic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class HomePlaylistFragment extends Fragment {

    ArrayList<PlaylistModel> arrPlaylist;
    PlaylistAdapter playlistListViewAdapter;
    ListView lvPlaylist;

    ConstraintLayout btnCreatePlaylist;

    EditText txtCreatePlaylistName;
    Button btnConfirmCreatePlaylist, btnCancelCreatePlaylist;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_playlist, container, false);

    }


    private void createPlaylistDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.create_playlist_dialog, null));
        AlertDialog dialog = builder.create();

        dialog.show();

        txtCreatePlaylistName = dialog.findViewById(R.id.playlist_name_create);

        btnConfirmCreatePlaylist = dialog.findViewById(R.id.btn_confirm_create_playlist);
        btnCancelCreatePlaylist = dialog.findViewById(R.id.btn_cancel_create_playlist);

        btnConfirmCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = txtCreatePlaylistName.getText().toString();

                // If signed in, get playlists from cloud
                if(firebaseUser!=null){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();;
                    DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("playlists").child(playlistName);
                    myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                txtCreatePlaylistName.setError("Danh sách phát online này đã tồn tại!");
                                txtCreatePlaylistName.requestFocus();
                            }
                            else {
                                myFirebaseRef.child("name").setValue(playlistName);
                                myFirebaseRef.child("type").setValue("online");
                                dialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                // Else not signed in, create local playlist
                else {
                    PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                    ArrayList<PlaylistModel> playlists = mydb.getPlaylist();
                    //
                    boolean isExist = false;
                    for(int i=0; i<playlists.size(); i++){
                        if(Objects.equals(playlists.get(i).getName(), playlistName)){
                            isExist = true;
                            break;
                        }
                    }
                    if(isExist){
                        txtCreatePlaylistName.setError("Danh sách phát này đã tồn tại!");
                        txtCreatePlaylistName.requestFocus();
                    }
                    else {
                        mydb.createPlaylist(playlistName, "local");
                        dialog.dismiss();
                    }

                }

                // Close dialog and refresh list
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPlaylist();
                    }
                }, 2000);
            }
        });

        // Click cancel button
        btnCancelCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        btnCreatePlaylist = view.findViewById(R.id.menu_option_btn_create_playlist);

        lvPlaylist = view.findViewById(R.id.listViewPlaylist);

        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlaylistDialog();

            }
        });

        getPlaylist();


    }

    // Show playlists
    private void showPlaylist(){
        playlistListViewAdapter = new PlaylistAdapter(arrPlaylist);

        lvPlaylist.setAdapter(playlistListViewAdapter);

        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaylistModel playlistModel = (PlaylistModel) playlistListViewAdapter.getItem(position);
                String playlistName = playlistModel.getName();
                String playlistType = playlistModel.getType();

                Bundle bundle = new Bundle();
                bundle.putString("playlistName", playlistName);
                bundle.putString("type", playlistType);

                System.out.println(playlistName + playlistType);

                ((MainActivity)getActivity()).songOnPlaylistFragment.setArguments(bundle);

                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).songOnPlaylistFragment);

            }
        });
    }

    // Get playlists
    private void getPlaylist(){
        //
        arrPlaylist = new ArrayList<>();

        // Get playlist from local
        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
        ArrayList<PlaylistModel> listPlaylist =  mydb.getPlaylist();
        for (int i=0; i<listPlaylist.size(); i++){
            arrPlaylist.add(listPlaylist.get(i));
        }

        // If signed in, get playlists from cloud
        if(firebaseUser!=null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();;
            DatabaseReference myFirebaseRef = database.getReference();
            myFirebaseRef.child("users").child(firebaseUser.getUid()).child("playlists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String playlistName = (String) ds.child("name").getValue();
                    String playlistType = (String) ds.child("type").getValue();

                    PlaylistModel playlistModel = new PlaylistModel();
                    playlistModel.setName(playlistName);
                    playlistModel.setType(playlistType);

                    arrPlaylist.add(playlistModel);
                }
            }
            //
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }

        });
        }

        // Wait for receive data from database and show
        for (int i=0; i<10000; i+=200){
            final int a = i;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPlaylist();
                }
            }, a);
        }




    }

}


// Class adapter
class PlaylistAdapter extends BaseAdapter {

    final ArrayList<PlaylistModel> arrPlaylist;

    PlaylistAdapter (ArrayList<PlaylistModel> arrPlaylist) {
        this.arrPlaylist = arrPlaylist;
    }

    @Override
    public int getCount() {
        return arrPlaylist.size();
    }

    @Override
    public Object getItem(int position) {
        return arrPlaylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public int getType(int position){
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewPlaylist;
        if (convertView == null) {
            viewPlaylist = View.inflate(parent.getContext(), R.layout.playlist_view, null);
        } else viewPlaylist = convertView;getItem(position);

        //Bind sữ liệu phần tử vào View
        PlaylistModel playlist = (PlaylistModel) getItem(position);
        String playlistName = (String) playlist.getName();


        TextView txtPlaylistName = viewPlaylist.findViewById(R.id.textView_playlistName);
        txtPlaylistName.setText(playlistName);

        return viewPlaylist;
    }
}