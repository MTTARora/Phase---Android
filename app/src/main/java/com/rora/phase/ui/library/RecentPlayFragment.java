package com.rora.phase.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.ui.adapter.CategoryRVAdapter;
import com.rora.phase.ui.adapter.RecentPlayVPAdapter;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.ui.viewmodel.UserViewModel;
import com.rora.phase.utils.DateTimeHelper;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.CustomViewPagerTransformer;
import com.rora.phase.utils.ui.HorizontalMarginItemDecoration;

import static com.rora.phase.ui.adapter.CategoryRVAdapter.MEDIUM_SIZE;

public class RecentPlayFragment extends BaseFragment {

    private ViewPager2 vpGameList;
    private TextView tvName, tvPayType, tvAge, tvReleasedDate;
    private ImageButton imbFavorite;
    private RecyclerView rclvCategory;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);

        View root = inflater.inflate(R.layout.fragment_recent_play, container, false);
        vpGameList = root.findViewById(R.id.recent_play_vp);
        tvName = root.findViewById(R.id.game_name_tv);
        tvPayType = root.findViewById(R.id.pay_type_tv);
        tvAge = root.findViewById(R.id.age_rating_tv);
        tvReleasedDate = root.findViewById(R.id.release_tv);
        imbFavorite = root.findViewById(R.id.favorite_recent_play_btn);
        rclvCategory = root.findViewById(R.id.category_recent_play_rclv);

        initData();
        setupViews();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupViews() {
        RecentPlayVPAdapter recentPlayAdapter = new RecentPlayVPAdapter();
        vpGameList.setAdapter(recentPlayAdapter);
        vpGameList.setOffscreenPageLimit(2);
        vpGameList.setPageTransformer(new CustomViewPagerTransformer());
        vpGameList.addItemDecoration(new HorizontalMarginItemDecoration());
        vpGameList.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (userViewModel.getRecentPlayList().getValue() == null || userViewModel.getRecentPlayList().getValue().size() == 0)
                    return;

                Game game = userViewModel.getRecentPlayList().getValue().get(position);

                tvName.setText(game.getName());
                tvPayType.setText(game.getPayTypeName());
                tvAge.setText(game.getPegiAge() + "+");
                tvReleasedDate.setText(DateTimeHelper.format(game.getReleaseDate()));
                ((CategoryRVAdapter)rclvCategory.getAdapter()).bindData(game.getTags());
                userViewModel.setCurrentRecentPlay(game);
                imbFavorite.setImageResource(game.isFavorited() ? R.drawable.ic_favorite : R.drawable.ic_unfavorite);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        rclvCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        rclvCategory.setAdapter(new CategoryRVAdapter(MEDIUM_SIZE, false));
        rclvCategory.setHasFixedSize(true);

        recentPlayAdapter.setOnItemSelectedListener(selectedItem -> moveTo(GameDetailFragment.newInstance((Game) selectedItem), GameDetailFragment.class.getSimpleName()));
        recentPlayAdapter.setOnChildItemClickListener(selectedItem -> Toast.makeText(getContext(), "Coming soon", Toast.LENGTH_SHORT).show());
    }

    private void initData() {
        userViewModel.getRecentPlayList().observe(getViewLifecycleOwner(), games -> ((RecentPlayVPAdapter)vpGameList.getAdapter()).bindData(games));
        userViewModel.getRecentPlayData();
    }

}