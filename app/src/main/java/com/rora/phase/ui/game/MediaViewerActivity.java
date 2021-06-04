package com.rora.phase.ui.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.rora.phase.R;
import com.rora.phase.model.MediaImage;
import com.rora.phase.ui.adapter.MediaViewerAdapter;
import com.rora.phase.utils.ui.CustomToolbar;

import java.util.List;

public class MediaViewerActivity extends AppCompatActivity {

    private View decorView;
    private CustomToolbar toolbar;
    private ViewPager2 mediaViewerVp;

    private String title;
    private List<MediaImage> mediaImageList;
    private int currentPosition;
    private boolean isShowingSystemBars = false;

    public static String SCREEN_TITLE_PARAM = "title_param";
    public static String MEDIA_LIST_PARAM = "medias_param";
    public static String POSITION_PARAM = "position_param";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_viewer);

        toolbar = findViewById(R.id.toolbar);
        mediaViewerVp = findViewById(R.id.media_viewer_vp);

        if (getIntent() != null && getIntent().getExtras() != null) {
            title = getIntent().getExtras().getString(SCREEN_TITLE_PARAM);
            mediaImageList = (List<MediaImage>) getIntent().getExtras().getSerializable(MEDIA_LIST_PARAM);
            currentPosition = getIntent().getExtras().getInt(POSITION_PARAM);
        }

        setupViews();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mediaViewerVp.setAdapter(new MediaViewerAdapter(mediaImageList, 0));
        mediaViewerVp.setCurrentItem(currentPosition);
    }

    private void setupViews() {
        hideSystemUI();
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            // Move toolbar below status bar
            toolbar.setPadding(0, insets.getSystemWindowInsetTop() + (int)getResources().getDimension(R.dimen.min_space), 0, 0);
            return insets;
        });

        toolbar.showActionbar(title, true, null);
        mediaViewerVp.setAdapter(new MediaViewerAdapter(mediaImageList, 0));
        mediaViewerVp.setOffscreenPageLimit(mediaImageList.size());
        mediaViewerVp.setCurrentItem(currentPosition, false);

        mediaViewerVp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
            }
        });

        //decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
        //    if (isShowingSystemBars) {
        //        new Handler().postDelayed(this::hideSystemUI, 1000);
        //        isShowingSystemBars = false;
        //    } else {
        //        isShowingSystemBars = true;
        //    }
        //});
    }

    private void hideSystemUI() {
        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

}