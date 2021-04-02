package com.rora.phase.ui.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.rora.phase.R;
import com.rora.phase.model.Game;
import com.rora.phase.utils.MediaHelper;

public class QueueView extends ConstraintLayout {
    //private ConstraintLayout frameQueueMain, frameQueue;

    private ImageView imvQueueMain, imvQueue;
    private TextView tvQueueMain, tvQueue;

    public QueueView(@NonNull Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_play_queue, this, true);
        //frameQueueMain = getChildAt(0)findViewById(R.id.frame_queue_main);
        //frameQueue = findViewById(R.id.frame_queue);
        imvQueueMain = findViewById(R.id.game_banner_queue_main_imv);
        imvQueue = findViewById(R.id.game_banner_queue_imv);
        tvQueueMain = findViewById(R.id.queue_main_tv);
        tvQueue = findViewById(R.id.queue_tv);
        imvQueueMain = (ImageView) getChildAt(0);
        imvQueue = findViewById(R.id.game_banner_queue_imv);
        tvQueueMain = findViewById(R.id.queue_main_tv);
        tvQueue = findViewById(R.id.queue_tv);
    }

    public void bindData(int total, int currentPos, Game game) {
        if (total == 0) {
            tvQueueMain.setText("--/0");
            MediaHelper.loadImage(imvQueueMain, null);
            tvQueue.setText("--/0");
            MediaHelper.loadImage(imvQueue, null);
        } else {
            tvQueueMain.setText(currentPos + "/" + total);
            MediaHelper.loadImage(imvQueueMain, game.getBanner());
            tvQueue.setText(currentPos + "/" + total);
            MediaHelper.loadImage(imvQueue, game.getBanner());
        }
    }
}
