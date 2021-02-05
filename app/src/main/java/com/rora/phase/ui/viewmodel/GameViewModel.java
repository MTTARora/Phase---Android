package com.rora.phase.ui.viewmodel;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rora.phase.R;
import com.rora.phase.binding.PlatformBinding;
import com.rora.phase.computers.ComputerManagerService;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.nvstream.http.NvHTTP;
import com.rora.phase.nvstream.http.PairingManager;
import com.rora.phase.nvstream.jni.MoonBridge;
import com.rora.phase.repository.GameRepository;
import com.rora.phase.ui.game.GameDetailFragment;
import com.rora.phase.utils.Dialog;
import com.rora.phase.utils.ServerHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class GameViewModel extends AndroidViewModel {

    private GameRepository gameRepository;
    private MutableLiveData<ComputerDetails> computerDetails;


    //---------------------- BASIC METHODS -----------------------------

    public GameViewModel(Application application) {
        super(application);
        gameRepository = new GameRepository();

        computerDetails = gameRepository.getComputer();
    }

    public LiveData<ComputerDetails> getComputerDetails() {
        return computerDetails;
    }

    //------------------------------------------------------------------


    //====================== PLAYING GAME STEPS =========================

    /** STEP 1: Get computer ip from server
     *
     * STEP 2: Create computer data and handle in local
     *
     * STEP 3: Pair
     */

    public void getComputerDetailsData() {

        //STEP 1
        gameRepository.getComputerIPData();
    }

    /** STEP 2: Connect to pc
     *
     * Send paring pin to server
     *
     * */


    /** STEP 3: Start calculating playtime
     *
     *
     *
     * */

    public void startCalculatingPlaytime() {

    }


    //--------SUPPORT FUNCTIONS FOR PLAYING--------


    //--------------------------------------------

    //===================================================================

}
