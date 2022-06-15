package com.duongcong.androidmusic;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

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
