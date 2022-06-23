package com.duongcong.androidmusic.Home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.duongcong.androidmusic.Home.playlist.HomePlaylistFragment;
import com.duongcong.androidmusic.Home.recent.HomeRecentFragment;

public class ViewPagerAdapterHome extends FragmentStateAdapter {


    public ViewPagerAdapterHome(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new HomeRecentFragment();
            default:
                return new HomePlaylistFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }





}
