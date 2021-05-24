package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.rora.phase.R;
import com.rora.phase.model.Tag;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.AUTO_SIZE;
import static com.rora.phase.ui.adapter.CategoryRVAdapter.NORMAL_SIZE;

public class CategoryRVAdapter extends BaseRVAdapter {

    private List<Tag> categoryList;
    private List<Tag> selectedTagList;
    private int size;
    private boolean hasBackground;
    private int selectionType;

    public static final int AUTO_SIZE = 0;
    public static final int MEDIUM_SIZE = 1;
    public static final int NORMAL_SIZE = 2;

    public static final int NONE_SELECT = 3;
    public static final int SINGLE_SELECT = 4;
    public static final int MULTI_SELECT = 5;

    public CategoryRVAdapter(int size, boolean hasBackground, int selectionType) {
        this.categoryList = new ArrayList<>();
        selectedTagList = new ArrayList<>();
        this.size = size;
        this.hasBackground = hasBackground;
        this.selectionType = selectionType;
    }


    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        switch (size) {
            case AUTO_SIZE:
                ViewHelper.setSize(root, WRAP_CONTENT, WRAP_CONTENT);
                ((ViewGroup.MarginLayoutParams) root.getLayoutParams()).setMarginEnd((int) parent.getContext().getResources().getDimension(R.dimen.min_space));
                break;
            case MEDIUM_SIZE:
                ViewHelper.setSizePercentageWithScreenAndItSelf(root, 0.22, 0, 3);
                break;
            case NORMAL_SIZE:
                ViewHelper.setSizePercentageWithScreenAndItSelf(root, 0.25, 0, 2);
                break;
        }

        return new CategoryViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        Tag currentTag = categoryList.get(position);
        ((CategoryViewHolder)holder).bindData(currentTag, selectedTagList != null && selectedTagList.contains(currentTag), size, hasBackground);

        if (onItemSelectedListener != null)
            ((CategoryViewHolder)holder).btnCategory.setOnClickListener(v -> {
                switch (selectionType) {
                    case SINGLE_SELECT:
                        if (selectedTagList.contains(currentTag)) {
                            selectedTagList.clear();
                        }
                        else {
                            selectedTagList.clear();
                            selectedTagList.add(currentTag);
                        }
                        break;
                    case MULTI_SELECT:
                        if (selectedTagList.contains(currentTag))
                            selectedTagList.remove(currentTag);
                        else
                            selectedTagList.add(currentTag);
                        break;
                    default:
                        break;
                }
                onItemSelectedListener.onSelected(position, currentTag);

                notifyDataSetChanged();
            });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void bindData(List<Tag> categoryList, @Nullable List<Tag> previousState) {
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();
        if (previousState != null && previousState.size() != 0)
            this.selectedTagList = new ArrayList<>(previousState);
        else {
            if (onItemSelectedListener == null)
                selectedTagList.clear();
            else if (selectionType == SINGLE_SELECT)
                selectedTagList.add(categoryList.size() != 0 ? categoryList.get(0) : null);
        }

        notifyDataSetChanged();
    }
}

class CategoryViewHolder extends BaseRVViewHolder {

    public TextView btnCategory;
    private CardView cvFrame;

    private Context context;

    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
        btnCategory = itemView.findViewById(R.id.btn_category);
        cvFrame = itemView.findViewById(R.id.category_frame);

        this.context = itemView.getContext();
    }

    public void bindData(Tag category, Boolean isSelected, int size, boolean hasBackground) {
        btnCategory.setText(category.getTag());

        if (!hasBackground)
            btnCategory.setBackgroundColor(context.getColor(isSelected ? R.color.colorPrimary : R.color.colorPrimaryDark));
        else
            btnCategory.setBackgroundColor(context.getColor(R.color.colorPrimary));

        switch (size) {
            case AUTO_SIZE:
                cvFrame.setRadius(context.getResources().getDimension(R.dimen.medium_radius));
                btnCategory.setTextSize(context.getResources().getDimension(R.dimen.minnnn_text_size));
                break;
            case MEDIUM_SIZE:
                //cvFrame.setRadius(context.getResources().getDimension(R.dimen.maxx_radius));
                break;
            case NORMAL_SIZE:
                btnCategory.setTextSize(context.getResources().getDimension(R.dimen.minnn_text_size));
                break;
            default: break;
        }
    }

}
