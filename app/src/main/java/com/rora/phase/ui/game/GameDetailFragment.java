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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.rora.phase.ui.viewmodel.GameViewModel;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.HelpLauncher;
import com.rora.phase.utils.ServerHelper;
import com.rora.phase.utils.ShortcutHelper;
import com.rora.phase.utils.UiHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameDetailFragment extends Fragment {

    private GameViewModel gameViewModel;

    //----------------------------------- LIFECYCLE ------------------------------------------


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        View root = inflater.inflate(R.layout.fragment_game_detail, container, false);

        root.findViewById(R.id.btn_play).setOnClickListener(v -> {
            gameViewModel.getComputerDetailsData();
        });

        initData();
        return root;
    }

    //---------------------------------------------------------------------------------------

    private void initData() {
        //STEP 1: Get computer details from server
        gameViewModel.getComputerDetails().observe(getViewLifecycleOwner(), new Observer<ComputerDetails>() {
            @Override
            public void onChanged(ComputerDetails computerDetails) {
                //STEP 2: Pass computer data to loadingscreen
                Bundle bundle = new Bundle();
                bundle.putSerializable("COMPUTER_DATA", computerDetails);
                NavHostFragment.findNavController(GameDetailFragment.this).navigate(R.id.action_gameDetailFragment_to_loadingGameFragment, bundle);

            }
        });
    }

}