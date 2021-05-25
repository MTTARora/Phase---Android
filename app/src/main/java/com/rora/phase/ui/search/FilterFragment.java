package com.rora.phase.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Tag;
import com.rora.phase.model.ui.FilterParams;
import com.rora.phase.ui.adapter.CategoryRVAdapter;
import com.rora.phase.ui.viewmodel.SearchViewModel;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.BaseFragment;
import com.rora.phase.utils.ui.GridAutofitLayoutManager;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rora.phase.model.ui.FilterParams.SortType.NEWEST;
import static com.rora.phase.model.ui.FilterParams.SortType.NONE;
import static com.rora.phase.model.ui.FilterParams.SortType.NOW_PLAYING;
import static com.rora.phase.model.ui.FilterParams.SortType.RATING;
import static com.rora.phase.model.ui.FilterParams.SortType.RELEASE;

public class FilterFragment extends BaseFragment {

    private LinearLayout frameSortBy, framePlatforms, framePayTypes, framePlayTypes, frameAgeRating, frameNOP, frameFeature, frameGameQlt, frameTag;
    private RadioGroup rgOptionsSortBy, rgOptionsFeature;
    private CheckBox cbPlatformsSteam, cbPlatformsBattle, cbPlatformsEpic, cbPlatformsOrigin, cbPlatformsUbisoft, cbPlatformsGarena, cbPlatformsRiot;
    private CheckBox cbPayTypeFree, cbPayTypeRequireLicense, cbPayTypeInstancePlay, cbFreeWithYourAcc;
    private CheckBox cbPlayTypeMnK, cbPlayTypeController, cbPlayTypeJoyStick, cbPlayTypeOnscreenTouch;
    private CheckBox cbAgeRating3, cbAgeRating7, cbAgeRating12, cbAgeRating16, cbAgeRating18;
    private CheckBox cbNopSingle, cbNopCoop, cbNopMulti;
    private CheckBox cbGameQltHDR, cbGameQltRayTracing, cbGameQltDlss;
    private RecyclerView rclvTag;

    private SearchViewModel searchViewModel;
    private FilterParams filters;
    private FilterParams.Filter type;

    public static final String FILTER_TYPE_PARAM = "type";

    public static FilterFragment newInstance(FilterParams.Filter type) {
        Bundle agrs = new Bundle();
        agrs.putSerializable(FILTER_TYPE_PARAM, type);

        FilterFragment fragment = new FilterFragment();
        fragment.setArguments(agrs);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().getSerializable(FILTER_TYPE_PARAM) != null)
            type = (FilterParams.Filter) getArguments().getSerializable(FILTER_TYPE_PARAM);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        View root = inflater.inflate(R.layout.fragment_filter, container, false);
        frameSortBy = root.findViewById(R.id.frame_sort_by_filter);
        framePlatforms = root.findViewById(R.id.frame_platforms_filter);
        framePayTypes = root.findViewById(R.id.frame_pay_types_filter);
        framePlayTypes = root.findViewById(R.id.frame_play_types_filter);
        frameAgeRating = root.findViewById(R.id.frame_age_filter);
        frameNOP = root.findViewById(R.id.frame_nop_filter);
        frameFeature = root.findViewById(R.id.frame_feature_filter);
        frameGameQlt = root.findViewById(R.id.frame_game_qlt_filter);
        frameTag = root.findViewById(R.id.frame_tag_filter);

        rgOptionsSortBy = root.findViewById(R.id.options_sort_by_filter);

        cbPlatformsSteam = root.findViewById(R.id.platforms_steam_cb);
        cbPlatformsBattle = root.findViewById(R.id.platforms_battle_cb);
        cbPlatformsEpic = root.findViewById(R.id.platforms_epic_cb);
        cbPlatformsOrigin = root.findViewById(R.id.platforms_origin_cb);
        cbPlatformsUbisoft = root.findViewById(R.id.platforms_ubisoft_cb);
        cbPlatformsGarena = root.findViewById(R.id.platforms_garena_cb);
        cbPlatformsRiot = root.findViewById(R.id.platforms_riot_cb);

        cbPayTypeFree = root.findViewById(R.id.pay_type_free_cb);
        cbPayTypeRequireLicense = root.findViewById(R.id.pay_type_required_license_cb);
        cbPayTypeInstancePlay = root.findViewById(R.id.pay_type_instance_play_cb);
        cbFreeWithYourAcc = root.findViewById(R.id.pay_type_free_with_user_acc_cb);

        cbPlayTypeMnK = root.findViewById(R.id.play_type_mnk_rb);
        cbPlayTypeController = root.findViewById(R.id.play_type_controller_rb);
        cbPlayTypeJoyStick = root.findViewById(R.id.play_type_joystick_rb);
        cbPlayTypeOnscreenTouch = root.findViewById(R.id.play_type_ost_rb);

        cbAgeRating3 = root.findViewById(R.id.age_rating_3_cb);
        cbAgeRating7 = root.findViewById(R.id.age_rating_7_cb);
        cbAgeRating12 = root.findViewById(R.id.age_rating_12_cb);
        cbAgeRating16 = root.findViewById(R.id.age_rating_16_cb);
        cbAgeRating18 = root.findViewById(R.id.age_rating_18_cb);

        cbNopSingle = root.findViewById(R.id.nop_single_cb);
        cbNopCoop = root.findViewById(R.id.nop_coop_cb);
        cbNopMulti = root.findViewById(R.id.nop_multi_cb);

        rgOptionsFeature = root.findViewById(R.id.options_feature_filter);

        cbGameQltHDR = root.findViewById(R.id.game_qlt_HDR_cb);
        cbGameQltRayTracing = root.findViewById(R.id.game_qlt_ray_tracing_cb);
        cbGameQltDlss = root.findViewById(R.id.game_qlt_dlss_cb);

        rclvTag = root.findViewById(R.id.filter_tag_rclv);

        root.findViewById(R.id.apply_filter_btn).setOnClickListener(v -> applyFilters());

        showActionbar(root, "Filters", true);
        initData();
        return root;
    }

    private void initData() {
        searchViewModel.getFilters().observe(getViewLifecycleOwner(), filterParams -> {
            filters = new FilterParams(filterParams);

            setupViews();
        });
    }

    private void setupViews() {
        if (type == null || filters == null) {
            frameSortBy.setVisibility(View.GONE);
            framePayTypes.setVisibility(View.GONE);
            framePlayTypes.setVisibility(View.GONE);
            frameAgeRating.setVisibility(View.GONE);
            frameNOP.setVisibility(View.GONE);
            frameFeature.setVisibility(View.GONE);
            frameGameQlt.setVisibility(View.GONE);
            frameTag.setVisibility(View.GONE);
            return;
        }

        frameSortBy.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.SORT_BY ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.SORT_BY) {
            switch (filters.getSortBy()) {
                case NEWEST:
                    rgOptionsSortBy.check(R.id.sort_by_added_date_rb);
                    break;
                case RELEASE:
                    rgOptionsSortBy.check(R.id.sort_by_released_rb);
                    break;
                case RATING:
                    rgOptionsSortBy.check(R.id.sort_by_rating_rb);
                    break;
                case NOW_PLAYING:
                    rgOptionsSortBy.check(R.id.sort_by_now_playing_rb);
                    break;
                default:
                    rgOptionsSortBy.check(R.id.sort_by_none_rb);
                    break;
            }
        }

        frameFeature.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.BY_FEATURE ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.BY_FEATURE) {
            switch (filters.getFeatureGamesType()) {
                case HOT:
                    rgOptionsFeature.check(R.id.feature_hot_rb);
                    break;
                case EDITOR_CHOICE:
                    rgOptionsFeature.check(R.id.feature_editor_choice_rb);
                    break;
                case TRENDING:
                    rgOptionsFeature.check(R.id.feature_trending_rb);
                    break;
                case RECOMMENDED:
                    rgOptionsFeature.check(R.id.feature_recommend_rb);
                    break;
                default:
                    rgOptionsFeature.check(R.id.feature_none_rb);
            }
        }

        framePlatforms.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.PLATFORMS ? VISIBLE : GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.PLATFORMS) {
            if (!filters.getPlatforms().all) {
                cbPlatformsSteam.setChecked(filters.getPlatforms().steam);
                cbPlatformsBattle.setChecked(filters.getPlatforms().battle);
                cbPlatformsEpic.setChecked(filters.getPlatforms().epic);
                cbPlatformsOrigin.setChecked(filters.getPlatforms().origin);
                cbPlatformsUbisoft.setChecked(filters.getPlatforms().ubisoft);
                cbPlatformsGarena.setChecked(filters.getPlatforms().garena);
                cbPlatformsRiot.setChecked(filters.getPlatforms().riot);
            }
        }

        framePayTypes.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.PAY_TYPES ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.PAY_TYPES) {
            if (!filters.getPayTypes().all) {
                cbPayTypeFree.setChecked(filters.getPayTypes().free);
                cbPayTypeRequireLicense.setChecked(filters.getPayTypes().licenceRequired);
                cbPayTypeInstancePlay.setChecked(filters.getPayTypes().instancePlay);
                cbFreeWithYourAcc.setChecked(filters.getPayTypes().userAccount);
            }
        }

        framePlayTypes.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.PLAY_TYPES ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.PLAY_TYPES) {
            if (!filters.getPlayTypes().all) {
                cbPlayTypeMnK.setChecked(filters.getPlayTypes().mnk);
                cbPlayTypeController.setChecked(filters.getPlayTypes().controller);
                cbPlayTypeJoyStick.setChecked(filters.getPlayTypes().joystick);
                cbPlayTypeOnscreenTouch.setChecked(filters.getPlayTypes().oscTouch);
            }
        }

        frameAgeRating.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.PEGI ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.PEGI) {
            if (!filters.getAgeRatings().all) {
                cbAgeRating3.setChecked(filters.getAgeRatings()._3);
                cbAgeRating7.setChecked(filters.getAgeRatings()._7);
                cbAgeRating12.setChecked(filters.getAgeRatings()._12);
                cbAgeRating16.setChecked(filters.getAgeRatings()._16);
                cbAgeRating18.setChecked(filters.getAgeRatings()._18);
            }
        }

        frameNOP.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.NUMBER_OF_PLAYER ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.NUMBER_OF_PLAYER) {
            if (!filters.getNumberOfPlayers().all) {
                cbNopSingle.setChecked(filters.getNumberOfPlayers().single);
                cbNopCoop.setChecked(filters.getNumberOfPlayers().coop);
                cbNopMulti.setChecked(filters.getNumberOfPlayers().multi);
            }
        }

        frameGameQlt.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.GAME_QLT ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.GAME_QLT) {
            cbGameQltHDR.setChecked(filters.isOnlyHdr());
            cbGameQltRayTracing.setChecked(filters.isOnlyRayTracing());
            cbGameQltDlss.setChecked(filters.isOnlyDlss());
        }

        frameTag.setVisibility(type == FilterParams.Filter.ALL || type == FilterParams.Filter.TAG ? VISIBLE : View.GONE);
        if (type == FilterParams.Filter.ALL || type == FilterParams.Filter.TAG) {
            rclvTag.setLayoutManager(new GridLayoutManager(getContext(), 3));
            rclvTag.setAdapter(new CategoryRVAdapter(CategoryRVAdapter.AUTO_SIZE, false, CategoryRVAdapter.MULTI_SELECT));
            rclvTag.setHasFixedSize(true);

            searchViewModel.getTagList().observe(getViewLifecycleOwner(), tags -> ((CategoryRVAdapter)rclvTag.getAdapter()).bindData(tags, filters.getTags()));
            ((CategoryRVAdapter)rclvTag.getAdapter()).setOnItemSelectedListener((OnItemSelectedListener<Tag>) (position, selectedItem) -> filters.addTag(selectedItem));
        }

        rgOptionsSortBy.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.sort_by_added_date_rb:
                    filters.setSortBy(NEWEST);
                    break;
                case R.id.sort_by_released_rb:
                    filters.setSortBy(RELEASE);
                    break;
                case R.id.sort_by_rating_rb:
                    filters.setSortBy(RATING);
                    break;
                case R.id.sort_by_now_playing_rb:
                    filters.setSortBy(NOW_PLAYING);
                    break;
                default:
                    filters.setSortBy(NONE);
                    break;
            }
        });


        // HANDLE CHANGE

        cbPlatformsSteam.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setSteam(isChecked));
        cbPlatformsBattle.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setBattle(isChecked));
        cbPlatformsEpic.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setEpic(isChecked));
        cbPlatformsOrigin.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setOrigin(isChecked));
        cbPlatformsUbisoft.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setUbisoft(isChecked));
        cbPlatformsGarena.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setGarena(isChecked));
        cbPlatformsRiot.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlatforms().setRiot(isChecked));

        cbPayTypeFree.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPayTypes().setFree(isChecked));
        cbPayTypeRequireLicense.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPayTypes().setLicenceRequired(isChecked));
        cbPayTypeInstancePlay.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPayTypes().setInstancePlay(isChecked));
        cbFreeWithYourAcc.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPayTypes().setUserAccount(isChecked));

        cbPlayTypeMnK.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlayTypes().setMnk(isChecked));
        cbPlayTypeController.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlayTypes().setController(isChecked));
        cbPlayTypeJoyStick.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlayTypes().setJoystick(isChecked));
        cbPlayTypeOnscreenTouch.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getPlayTypes().setOscTouch(isChecked));

        cbAgeRating3.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getAgeRatings().set_3(isChecked));
        cbAgeRating7.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getAgeRatings().set_7(isChecked));
        cbAgeRating12.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getAgeRatings().set_12(isChecked));
        cbAgeRating16.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getAgeRatings().set_16(isChecked));
        cbAgeRating18.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getAgeRatings().set_18(isChecked));

        cbNopSingle.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getNumberOfPlayers().setSingle(isChecked));
        cbNopCoop.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getNumberOfPlayers().setCoop(isChecked));
        cbNopMulti.setOnCheckedChangeListener((buttonView, isChecked) -> filters.getNumberOfPlayers().setMulti(isChecked));

        rgOptionsFeature.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.feature_hot_rb:
                    filters.setFeatureGamesType(FilterParams.FeatureGamesType.HOT);
                    break;
                case R.id.feature_editor_choice_rb:
                    filters.setFeatureGamesType(FilterParams.FeatureGamesType.EDITOR_CHOICE);
                    break;
                case R.id.feature_trending_rb:
                    filters.setFeatureGamesType(FilterParams.FeatureGamesType.TRENDING);
                    break;
                case R.id.feature_recommend_rb:
                    filters.setFeatureGamesType(FilterParams.FeatureGamesType.RECOMMENDED);
                    break;
                default:
                    filters.setFeatureGamesType(FilterParams.FeatureGamesType.ALL);
                    break;
            }
        });

        cbGameQltHDR.setOnCheckedChangeListener((buttonView, isChecked) -> filters.setOnlyHdr(isChecked));
        cbGameQltRayTracing.setOnCheckedChangeListener((buttonView, isChecked) -> filters.setOnlyRayTracing(isChecked));
        cbGameQltDlss.setOnCheckedChangeListener((buttonView, isChecked) -> filters.setOnlyDlss(isChecked));
    }

    private void applyFilters() {
        searchViewModel.setFilters(filters);
        getActivity().onBackPressed();
    }

}
