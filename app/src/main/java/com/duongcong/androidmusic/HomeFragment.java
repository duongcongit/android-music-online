package com.duongcong.androidmusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager2;


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



        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager2 = view.findViewById(R.id.view_pager2);

        ViewPagerAdapterHome viewPagerAdapterHome = new ViewPagerAdapterHome(getActivity().getSupportFragmentManager(), getLifecycle());

        mViewPager2.setAdapter(viewPagerAdapterHome);

        //new TabLayoutMediator(mTabLayout, mViewPager2, "OK").attach();

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
                Toast.makeText(getActivity().getApplicationContext(), "song", Toast.LENGTH_SHORT).show();

            }
        });

        //
        cardView_on_device = view.findViewById(R.id.on_device_card);
        cardView_on_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "on device", Toast.LENGTH_SHORT).show();


            }
        });

        //
        cardView_upload = view.findViewById(R.id.upload_card);
        cardView_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "upload", Toast.LENGTH_SHORT).show();


            }
        });

        //
        cardView_download = view.findViewById(R.id.download_card);
        cardView_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "download", Toast.LENGTH_SHORT).show();


            }
        });

        //
        cardView_album = view.findViewById(R.id.album_card);
        cardView_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "album", Toast.LENGTH_SHORT).show();


            }
        });




    }




}
