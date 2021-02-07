package com.rora.phase.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rora.phase.model.Game;
import com.rora.phase.model.Tag;
import com.rora.phase.nvstream.http.ComputerDetails;
import com.rora.phase.utils.DataResultHelper;
import com.rora.phase.utils.network.BaseResponse;
import com.rora.phase.utils.network.PhaseService;
import com.rora.phase.utils.network.PhaseServiceHelper;
import com.rora.phase.utils.network.UserPhaseService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameRepository {

    private PhaseService gameServices;
    private PhaseService tagServices;
    private UserPhaseService userServices;
    private MutableLiveData<List<Game>> newGameList, recentPlayList, editorsChoiceList, hotGameList, trendingList, gameByCategoryList, gamesByPayTypeList;
    private MutableLiveData<List<Tag>> categoryList;
    private MutableLiveData<ComputerDetails> computer;
    private MutableLiveData<Game> selectedGame;

    private MutableLiveData<String> errMsg;

    public GameRepository() {
        newGameList = new MutableLiveData<>();
        recentPlayList = new MutableLiveData<>();
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();
        trendingList = new MutableLiveData<>();
        categoryList = new MutableLiveData<>();
        gameByCategoryList = new MutableLiveData<>();
        gamesByPayTypeList = new MutableLiveData<>();
        computer = new MutableLiveData<>();
        selectedGame = new MutableLiveData<>();

        errMsg = new MutableLiveData<>();

        PhaseServiceHelper phaseServiceHelper = new PhaseServiceHelper();
        gameServices = phaseServiceHelper.getGamePhaseService();
        tagServices = phaseServiceHelper.getPhaseService();
        userServices = phaseServiceHelper.getUserPhaseService();
    }


    //---------- GET SET ----------

    public LiveData<List<Game>> getNewGameList() {
        return newGameList;
    }

    public MutableLiveData<List<Game>> getRecentPlayList() {
        return recentPlayList;
    }

    public MutableLiveData<List<Game>> getEditorsChoiceList() {
        return editorsChoiceList;
    }

    public MutableLiveData<List<Game>> getHotGameList() {
        return hotGameList;
    }

    public MutableLiveData<List<Game>> getTrendingList() {
        return trendingList;
    }

    public MutableLiveData<List<Tag>> getCategoryList() {
        return categoryList;
    }

    public MutableLiveData<List<Game>> getGameByCategoryList() {
        return gameByCategoryList;
    }

    public MutableLiveData<List<Game>> getGamesByPayTypeList() {
        return gamesByPayTypeList;
    }

    public MutableLiveData<ComputerDetails> getComputer() {
        return computer;
    }

    public MutableLiveData<Game> getSelectedGame() {
        return selectedGame;
    }

    //----------------------------

    public void reset() {
        newGameList = new MutableLiveData<>();
        recentPlayList = new MutableLiveData<>();
        editorsChoiceList = new MutableLiveData<>();
        hotGameList = new MutableLiveData<>();
        trendingList = new MutableLiveData<>();
        categoryList = new MutableLiveData<>();
        gameByCategoryList = new MutableLiveData<>();
        gamesByPayTypeList = new MutableLiveData<>();
        selectedGame = new MutableLiveData<>();
    }

    public void getNewGameListData(int page, int pageSize) {
        gameServices.getNewGames(page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    newGameList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get new game - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    newGameList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get new game - " + t.getMessage());
                newGameList.postValue(new ArrayList<>());
            }
        });
    }

    public void getRecentPlayListData(int page, int pageSize) {
        userServices.getRecentPlay(page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    recentPlayList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get recent play - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    recentPlayList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get new game - " + t.getMessage());
                recentPlayList.postValue(new ArrayList<>());
            }
        });
    }

    public void getEditorsChoiceListData(int page, int pageSize) {
        gameServices.getEditorsChoice(page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    editorsChoiceList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get editor choice - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    editorsChoiceList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get editor choice - " + t.getMessage());
                editorsChoiceList.postValue(new ArrayList<>());
            }
        });
    }

    public void getHotGameListData(int page, int pageSize) {
        gameServices.getHotGames(page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    hotGameList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get hot game - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    hotGameList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get hot game - " + t.getMessage());
                hotGameList.postValue(new ArrayList<>());
            }
        });
    }

    public void getTrendingData(int page, int pageSize) {
        gameServices.getTrending(page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    trendingList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get trending game - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    trendingList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get trending game - " + t.getMessage());
                trendingList.postValue(new ArrayList<>());
            }
        });
    }

    public void getCategoryListData() {
        tagServices.getCategoryList().enqueue(new Callback<BaseResponse<List<Tag>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Tag>>> call, Response<BaseResponse<List<Tag>>> response) {
                List<Tag> list = BaseResponse.getResult(response.body());
                list = list == null ? new ArrayList<>() : list;
                categoryList.postValue(list);
                if(list.size() != 0)
                    getGamesByCategoryData(list.get(0).getTag() != null ? list.get(0).getTag() : "", 1, 20);
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Tag>>> call, Throwable t) {
                Log.e(this.getClass().getSimpleName(), t.getMessage());
                categoryList.postValue(new ArrayList<>());
            }
        });
    }

    public void getGamesByCategoryData(String tagName, int page, int pageSize) {
        gameServices.getGameByCategoryList(tagName, page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    gameByCategoryList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get games by category - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    gameByCategoryList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get games by category - " + t.getMessage());
                gameByCategoryList.postValue(new ArrayList<>());
            }
        });
    }

    public void getGamesByPayTypeData(String payType, int page, int pageSize) {
        gameServices.getGameByPayType(payType, page, pageSize).enqueue(new Callback<BaseResponse<List<Game>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Game>>> call, Response<BaseResponse<List<Game>>> response) {
                DataResultHelper<BaseResponse<List<Game>>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    gamesByPayTypeList.postValue(new ArrayList<>());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get games by pay type - " + dataResponse.getErrMsg());
                } else {
                    List<Game> listGame = BaseResponse.getResult(dataResponse.getData());
                    listGame = listGame == null ? new ArrayList<>() : listGame;
                    gamesByPayTypeList.postValue(listGame);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Game>>> call, Throwable t) {
                Log.e("Request API failed", "Get games by pay type - " + t.getMessage());
                gamesByPayTypeList.postValue(new ArrayList<>());
            }
        });
    }

    public void getComputerIPData() {
        ComputerDetails computerDetails = new ComputerDetails();
        computerDetails.manualAddress = "171.247.39.92";
        computer.postValue(computerDetails);
        //gameServices.getComputerIP().enqueue(new Callback<BaseResponse<String>>() {
        //    @Override
        //    public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
        //        String ip = BaseResponse.getResult(response.body());
        //        ip = ip == null ? "" : ip;
        //        if (ip.equals("")) {
        //            computer.postValue(null);
        //        } else {
        //            ComputerDetails computerDetails = new ComputerDetails();
        //            computerDetails.manualAddress = ip;
        //            computer.postValue(computerDetails);
        //        }
        //    }
        //
        //    @Override
        //    public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
        //        Log.e(this.getClass().getSimpleName(), t.getMessage());
        //        computer.postValue(null);
        //    }
        //});
    }

    public void getGameData(String gameId) {

        gameServices.getGame(gameId).enqueue(new Callback<BaseResponse<Game>>() {
            @Override
            public void onResponse(Call<BaseResponse<Game>> call, Response<BaseResponse<Game>> response) {
                DataResultHelper<BaseResponse<Game>> dataResponse = PhaseServiceHelper.handleResponse(response);

                if (dataResponse.getErrMsg() != null) {
                    selectedGame.postValue(new Game());
                    errMsg.postValue(dataResponse.getErrMsg());
                    Log.e("Request API failed", "Get games by pay type - " + dataResponse.getErrMsg());
                } else {
                    Game game = BaseResponse.getResult(dataResponse.getData());
                    game = game == null ? new Game() : game;
                    selectedGame.postValue(game);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Game>> call, Throwable t) {
                Log.e("Request API failed", "Get games by pay type - " + t.getMessage());
                gamesByPayTypeList.postValue(new ArrayList<>());
            }
        });
    }

}
