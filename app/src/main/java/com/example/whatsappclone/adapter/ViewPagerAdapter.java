package com.example.whatsappclone.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.whatsappclone.fragment.ContatosFragment;
import com.example.whatsappclone.fragment.ConversasFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ConversasFragment();
            case 1:
                return new ContatosFragment();
            default:
                return new ConversasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
