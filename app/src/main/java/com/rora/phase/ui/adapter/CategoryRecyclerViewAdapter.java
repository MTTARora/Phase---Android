package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Tag;
import com.rora.phase.utils.callback.OnItemSelectedListener;
import com.rora.phase.utils.ui.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MEDIUM_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.MIN_SIZE;
import static com.rora.phase.ui.adapter.CategoryRecyclerViewAdapter.NORMAL_SIZE;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<Tag> categoryList;
    private List<Boolean> categorySelectedState;
    private OnItemSelectedListener onItemSelectedListener;
    private double widthPercentage;
    private int size;
    private boolean hasBackground;

    public static final int MIN_SIZE = 0;
    public static final int MEDIUM_SIZE = 1;
    public static final int NORMAL_SIZE = 2;

    public CategoryRecyclerViewAdapter(double widthPercentage, int size, boolean hasBackground, OnItemSelectedListener onItemSelectedListener) {
        this.categoryList = new ArrayList<>();
        categorySelectedState = new ArrayList<>();
        this.onItemSelectedListener = onItemSelectedListener;
        this.widthPercentage = widthPercentage;
        this.size = size;
        this.hasBackground = hasBackground;
    }


    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        if (widthPercentage != 0) {

            if(size == MIN_SIZE) {
                ViewHelper.setSize(root, WRAP_CONTENT, WRAP_CONTENT);
                ((ViewGroup.MarginLayoutParams) root.getLayoutParams()).setMarginEnd((int) parent.getContext().getResources().getDimension(R.dimen.min_space));
            } else {
                ViewHelper.setSizePercentageWithScreenAndItSelf(root, widthPercentage, 0, 2);
            }
        }

        return new CategoryViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bindData(categoryList.get(position), onItemSelectedListener != null ? categorySelectedState.get(position) : false, size, hasBackground);

        if (onItemSelectedListener != null)
            holder.btnCategory.setOnClickListener(v -> {
                onItemSelectedListener.onSelected(categoryList.get(position).getTag());

                for (int i = 0; i < categorySelectedState.size(); i++) {
                    categorySelectedState.set(i, i == position);
                }
                notifyDataSetChanged();
            });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void bindData(List<Tag> categoryList) {
        this.categoryList = categoryList != null ? categoryList : new ArrayList<>();

        if (onItemSelectedListener != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if (i == 0)
                    categorySelectedState.add(true);
                else
                    categorySelectedState.add(false);
            }
        }

        notifyDataSetChanged();
    }

}

class CategoryViewHolder extends RecyclerView.ViewHolder {

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
            btnCategory.setBackgroundColor(context.getColor(isSelected ? R.color.red : R.color.dim));
        else
            btnCategory.setBackgroundColor(context.getColor(R.color.colorPrimary));

        switch (size) {
            case MIN_SIZE:
                cvFrame.setRadius(context.getResources().getDimension(R.dimen.medium_radius));
                btnCategory.setTextSize(context.getResources().getDimension(R.dimen.minnnn_text_size));
                break;
            case MEDIUM_SIZE:
                cvFrame.setRadius(context.getResources().getDimension(R.dimen.maxx_radius));
                break;
            case NORMAL_SIZE:
                btnCategory.setTextSize(context.getResources().getDimension(R.dimen.minnn_text_size));
                break;
            default: break;
        }
    }

}
