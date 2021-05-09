package com.rora.phase.ui.game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import com.rora.phase.R;
import com.rora.phase.model.MediaImage;
import com.rora.phase.ui.adapter.MediaViewerAdapter;
import com.rora.phase.utils.ui.CustomToolbar;

import java.util.List;

public class MediaViewerActivity extends AppCompatActivity {

    private CustomToolbar toolbar;
    private ViewPager2 mediaViewerVp;
    private String title;
    private List<MediaImage> mediaImageList;
    private int currentPosition;

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
        mediaViewerVp.setAdapter(new MediaViewerAdapter(mediaImageList));
        mediaViewerVp.setCurrentItem(currentPosition);
    }

    private void setupViews() {
        toolbar.showActionbar(title, true);
        mediaViewerVp.setAdapter(new MediaViewerAdapter(mediaImageList));
        mediaViewerVp.setOffscreenPageLimit(mediaImageList.size());
        mediaViewerVp.setCurrentItem(currentPosition, false);

        mediaViewerVp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
            }
        });
    }

}