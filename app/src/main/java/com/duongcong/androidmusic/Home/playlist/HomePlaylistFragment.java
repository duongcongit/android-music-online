package com.duongcong.androidmusic.Home.playlist;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

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

    // ArrayList<PlaylistModel> arrPlaylist;
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Button create playlist
        btnCreatePlaylist = view.findViewById(R.id.menu_option_btn_create_playlist);

        // Listview playlists
        lvPlaylist = view.findViewById(R.id.listViewPlaylist);
        // Button create playlist
        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlaylistDialog();

            }
        });

        // Get and show playlists
        getPlaylist();

        // Register context menu for listview playlist
        registerForContextMenu(lvPlaylist);

        // Refresh list when show tab
        ((MainActivity)getActivity()).homeFragment.mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // System.out.println(position);
                getPlaylist();
            }
        });

    }

    // Dialog create playlist
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

                // If signed in, create playlists in cloud
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

    // Show playlists
    public void showPlaylist(ArrayList<PlaylistModel> listPlaylist){
        playlistListViewAdapter = new PlaylistAdapter(listPlaylist);

        lvPlaylist.setAdapter(playlistListViewAdapter);

        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get playlist info
                PlaylistModel playlistModel = (PlaylistModel) playlistListViewAdapter.getItem(position);
                String playlistName = playlistModel.getName();
                String playlistType = playlistModel.getType();

                // Send playlist info
                Bundle bundle = new Bundle();
                bundle.putString("playlistName", playlistName);
                bundle.putString("type", playlistType);

                System.out.println(playlistName + playlistType);

                ((MainActivity)getActivity()).songOnPlaylistFragment.setArguments(bundle);

                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).songOnPlaylistFragment);

            }
        });
    }

    // Get and show playlists
    private void getPlaylist(){
        //
        ArrayList<PlaylistModel> arrPlaylist = new ArrayList<>();

        // Get playlist from local
        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
        ArrayList<PlaylistModel> listPlaylist =  mydb.getPlaylist();
        arrPlaylist.addAll(listPlaylist);

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
                showPlaylist(arrPlaylist);
            }
            //
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }

        });
        }
        else if(firebaseUser == null) {
            showPlaylist(arrPlaylist);
        }


    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu_playlist, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // If select delete playlist selected
        if (item.getItemId() == R.id.btn_delete_playlist) {
            // Get selected playlist data
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int listPosition = info.position;
            PlaylistModel playlistModel = (PlaylistModel) playlistListViewAdapter.getItem(listPosition);
            String playlistName = playlistModel.getName();
            String playlistType = playlistModel.getType();

            // Create and show dialog confirm delete playlist
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.delete_playlist_dialog_cofirm, null));
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            // Button
            Button btnConfirmDelete = dialog.findViewById(R.id.btn_confirm_delete_playlist);
            Button btnCancelDelete  = dialog.findViewById(R.id.btn_cancel_delete_playlist);
            // Button confirm delete
            btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Selected playlist is local playlist
                    if(Objects.equals(playlistType, "local")){
                        PlaylistLocalDBHelper mydb = new PlaylistLocalDBHelper(getActivity().getApplicationContext());
                        mydb.deletePlaylist(playlistName);
                        dialog.dismiss();
                        getPlaylist();
                    }
                    // Selected playlist is online playlist
                    else if (Objects.equals(playlistType, "online")){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();;
                        DatabaseReference myFirebaseRef = database.getReference().child("users").child(firebaseUser.getUid()).child("playlists").child(playlistName);
                        myFirebaseRef.removeValue();
                        dialog.dismiss();
                        getPlaylist();
                    }
                }
            });

            // Button cancel
            btnCancelDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            return true;
        }
        return super.onOptionsItemSelected(item);
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




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View viewPlaylist;
        if (convertView == null) {
            viewPlaylist = View.inflate(parent.getContext(), R.layout.playlist_view, null);
        } else viewPlaylist = convertView;getItem(position);

        //Bind sữ liệu phần tử vào View
        PlaylistModel playlist = (PlaylistModel) getItem(position);
        String playlistName = (String) playlist.getName();


        TextView txtPlaylistName = viewPlaylist.findViewById(R.id.textView_songName);
        txtPlaylistName.setText(playlistName);

        // Display option menu when click to button
        ImageButton btn_option = ((ImageButton) viewPlaylist.findViewById(R.id.btn_playlist_more_option));
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show context menu
                parent.showContextMenuForChild(v);
            }
        });

        return viewPlaylist;


    }
}