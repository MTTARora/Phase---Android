package com.rora.phase.ui.library;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rora.phase.R;
import com.rora.phase.utils.ui.BaseFragment;

public class FavoriteFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite, container, false);

        return root;
    }
}