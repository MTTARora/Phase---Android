package com.rora.phase.utils.callback;

import com.rora.phase.model.Game;

public interface OnItemSelectedListener<T> {
    void onSelected(int position, T selectedItem);
}
