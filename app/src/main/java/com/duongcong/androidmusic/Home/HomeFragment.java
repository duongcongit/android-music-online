package com.duongcong.androidmusic.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.duongcong.androidmusic.Account.LoginActivity;
import com.duongcong.androidmusic.MainActivity;
import com.duongcong.androidmusic.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment {

    private TabLayout mTabLayout;
    public ViewPager2 mViewPager2;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootLayout =  inflater.inflate(R.layout.fragment_home, container, false);

        BottomNavigationView navBar = getActivity().findViewById(R.id.navigation);
        navBar.setVisibility(View.VISIBLE);

        //
        return rootLayout;


    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager2 = view.findViewById(R.id.view_pager2);

        ViewPagerAdapterHome viewPagerAdapterHome = new ViewPagerAdapterHome(getActivity().getSupportFragmentManager(), getLifecycle());

        mViewPager2.setAdapter(viewPagerAdapterHome);

        new TabLayoutMediator(mTabLayout, mViewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if(position == 0)
                    tab.setText("Playlist");
                else
                    tab.setText("Gần đây");
            }
        }).attach();




        CardView cardView_song, cardView_on_device, cardView_upload, cardView_download, cardView_album;

        //
        cardView_song = view.findViewById(R.id.song_card);
        cardView_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check user firebase
                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser!=null){
                    ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).songsFragment);
                }
                else{
                    Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        //
        //
        cardView_on_device = view.findViewById(R.id.on_device_card);
        cardView_on_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).songOnDeviceFragment);

            }
        });


        //
        cardView_download = view.findViewById(R.id.download_card);
        cardView_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).downloadFragment);


            }
        });

        //
        cardView_album = view.findViewById(R.id.album_card);
        cardView_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).displayFragment(((MainActivity)getActivity()).albumFragment);


            }
        });




    }




}
