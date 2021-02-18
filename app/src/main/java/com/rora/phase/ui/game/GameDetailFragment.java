package com.rora.phase.ui.game;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rora.phase.AppView;
import com.rora.phase.LimeLog;
import com.rora.phase.PcView;
import com.rora.phase.R;
import com.rora.phase.binding.PlatformBinding;
import com.rora.phase.binding.crypto.AndroidCryptoProvider;
import com.rora.phase.computers.ComputerManagerListener;
import com.rora.phase.computers.ComputerManagerService;
import com.rora.phase.grid.PcGridAdapter;
import com.rora.phase.grid.assets.DiskAssetLoader;
import com.rora.phase.model.Banner;
import com.rora.phase.model.Game;
import com.rora.phase.model.Screenshot;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.NvApp;
import com.rora.phase.nvstream.http.NvHTTP;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.nvstream.wol.WakeOnLanSender;
import com.rora.phase.preferences.AddComputerManually;
import com.rora.phase.preferences.GlPreferences;
import com.rora.phase.preferences.PreferenceConfiguration;
import com.rora.phase.preferences.StreamSettings;
import com.rora.phase.ui.AdapterFragment;
import com.rora.phase.ui.adapter.BannerVPAdapter;
import com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameInfoRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameMinInfoRecyclerViewAdapter;
import com.rora.phase.ui.adapter.GameVerticalRVAdapter;
import com.rora.phase.ui.adapter.PlatformRecyclerViewAdapter;
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.ui.viewmodel.HomeViewModel;
import com.rora.phase.utils.DateTimeHelper;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.HelpLauncher;
import com.rora.phase.utils.MediaHelper;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.ShortcutHelper;
import com.rora.phase.utils.UiHelper;
import com.rora.phase.utils.ui.ViewHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MIN_SIZE;

public class GameDetailFragment extends Fragment {

    private ImageView imvBanner;
    private RecyclerView rclvPlatform, rclvCategory, rclvScreenshot, rclvSeries, rclvSimilar;
    private TextView tvGameName, tvPayType, tvAgeRating, tvRelease, tvDesc;
    private ImageButton btnFavorite;

    private GameViewModel gameViewModel;
    private String gameId;

    public static final String KEY_GAME_ID = "gameId";


    //----------------------------------- LIFECYCLE ------------------------------------------

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        if (getArguments() != null) {
            gameId = getArguments().getString(KEY_GAME_ID);
        }

        View root = inflater.inflate(R.layout.fragment_game_detail, container, false);
        imvBanner = root.findViewById(R.id.game_banner_imv);

        rclvPlatform = root.findViewById(R.id.platform_rclv);
        rclvCategory = root.findViewById(R.id.category_game_rclv);
        rclvScreenshot = root.findViewById(R.id.screenshot_rclv);
        rclvSeries = root.findViewById(R.id.series_rclv);
        rclvSimilar = root.findViewById(R.id.similar_rclv);

        tvGameName = root.findViewById(R.id.game_name_tv);
        tvPayType = root.findViewById(R.id.pay_type_tv);
        tvAgeRating = root.findViewById(R.id.age_rating_tv);
        tvRelease = root.findViewById(R.id.release_tv);
        tvDesc = root.findViewById(R.id.game_desc_tv);

        btnFavorite = root.findViewById(R.id.favorite_btn);

        root.findViewById(R.id.back_btn).setOnClickListener(v -> getActivity().onBackPressed());
        root.findViewById(R.id.play_btn).setOnClickListener(v -> {
            gameViewModel.getComputerDetailsData();
        });

        btnFavorite.setOnClickListener(v -> {
            //gameViewModel.updateFavorite();
        });

        initView();
        initData();
        return root;
    }
    //---------------------------------------------------------------------------------------


    private void initView() {
        setupRecyclerView(rclvPlatform, new PlatformRecyclerViewAdapter(), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        setupRecyclerView(rclvCategory,new CategoryRecyclerViewAdapter( 0.11, MIN_SIZE, false, null), new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL , false));
        setupRecyclerView(rclvScreenshot, new BannerVPAdapter(), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        setupRecyclerView(rclvSeries, new GameMinInfoRecyclerViewAdapter(GameMinInfoRecyclerViewAdapter.VIEW_TYPE_EXPANDED, 0.8), new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rclvSimilar.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL , false));
        GameVerticalRVAdapter adapter = new GameVerticalRVAdapter(rclvSimilar);
        rclvSimilar.setAdapter(adapter);
    }

    private void initData() {

        gameViewModel.getGameData().observe(getViewLifecycleOwner(), game -> bindData(game));

        //STEP 1: Get computer details from server
        gameViewModel.getComputerDetails().observe(getViewLifecycleOwner(), computerDetails -> {
            //STEP 2: Pass computer data to loadingscreen
            Bundle bundle = new Bundle();
            bundle.putSerializable("COMPUTER_DATA", computerDetails);
            NavHostFragment.findNavController(GameDetailFragment.this).navigate(R.id.action_gameDetailFragment_to_loadingGameFragment, bundle);

        });

        gameViewModel.getGame(gameId);
    }

    private void bindData(Game game) {
        MediaHelper.loadImage(game.getBanner(), imvBanner);

        tvGameName.setText(game.getName());
        tvPayType.setText(game.getPayTypeName());
        tvAgeRating.setText(getResources().getString(R.string.age_rating_title) + " " + game.getPegiAge().toString() + "+");
        tvRelease.setText(getResources().getString(R.string.release_title) + " " + DateTimeHelper.format(game.getReleaseDate()));
        tvDesc.setText(game.getDesc());

        ((PlatformRecyclerViewAdapter)rclvPlatform.getAdapter()).bindData(game.getPlatforms());
        ((CategoryRecyclerViewAdapter)rclvCategory.getAdapter()).bindData(game.getTags());

        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner(game.getBanner()));
        for (Screenshot screenshot : game.getScreenshots()) {
            banners.add(new Banner(screenshot.getLink()));
        }
        ((BannerVPAdapter)rclvScreenshot.getAdapter()).bindData(banners);
        //((GameMinInfoRecyclerViewAdapter)rclvSeries.getAdapter()).bindData(game.getG);
        //((GameVerticalRVAdapter)rclvSimilar.getAdapter()).bindData(game.getPlatforms());
    }

    private void setupRecyclerView(RecyclerView view, RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
        view.setAdapter(adapter);
        view.setLayoutManager(layoutManager);
        view.setHasFixedSize(true);
    }

}