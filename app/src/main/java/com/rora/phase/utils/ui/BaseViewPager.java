package com.rora.phase.utils.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.rora.phase.ui.adapter.TabPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class BaseViewPager extends FragmentStateAdapter {

    private List<Page> pageList;

    public BaseViewPager(@NonNull Fragment fragment) {
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
        Page page = new Page(title, view);

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
