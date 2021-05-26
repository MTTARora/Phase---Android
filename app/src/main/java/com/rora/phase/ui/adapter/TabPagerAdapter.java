package com.rora.phase.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rora.phase.R;
import com.rora.phase.model.enums.PayTypeEnum;
import com.rora.phase.ui.game.GameListFragment;
import com.rora.phase.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentStateAdapter {

    private List<Page> pageList;

    public TabPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);

        pageList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pageList.get(position).page;
    }

    @Override
    public int getItemCount() {
        return pageList.size();
    }

    public void addPage(String title, Fragment view) {
        TabPagerAdapter.Page page = new TabPagerAdapter.Page(title, view);

        pageList.add(page);
    }

    public String getTitle(int position) {
        return pageList.get(position).title;
    }

    public void removePage(int position) {
        pageList.remove(position);
        notifyItemRemoved(position);
    }


    public class Page {
        String title;
        Fragment page;

        public Page(String title, Fragment page) {
            this.title = title == null ? "" : title;
            this.page = page;
        }
    }

}
