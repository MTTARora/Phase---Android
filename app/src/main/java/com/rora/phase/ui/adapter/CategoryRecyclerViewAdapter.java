package com.rora.phase.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rora.phase.R;
import com.rora.phase.model.Tag;
import com.rora.phase.utils.callback.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<Tag> categoryList;
    private List<Boolean> categorySelectedState;
    private OnItemSelectedListener onItemSelectedListener;
    private double widthPercentage;
    private boolean minSize;

    public CategoryRecyclerViewAdapter(OnItemSelectedListener onItemSelectedListener, double widthPercentage, boolean minSize) {
        this.categoryList = new ArrayList<>();
        categorySelectedState = new ArrayList<>();
        this.onItemSelectedListener = onItemSelectedListener;
        this.widthPercentage = widthPercentage;
        this.minSize = minSize;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        if (widthPercentage != 0) {
            int width = ((AppCompatActivity)parent.getContext()).getWindowManager().getDefaultDisplay().getWidth();
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            layoutParams.width = (int) (width * widthPercentage);
            layoutParams.height = layoutParams.width / 2;
            if(minSize)
                layoutParams.setMarginEnd((int) parent.getContext().getResources().getDimension(R.dimen.min_space));
            root.setLayoutParams(layoutParams);
        }

        return new CategoryViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bindData(categoryList.get(position), onItemSelectedListener != null ? categorySelectedState.get(position) : false, minSize);

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

    public void bindData(Tag category, Boolean isSelected, boolean minSize) {
        btnCategory.setText(category.getTag());
        btnCategory.setBackgroundColor(context.getColor(isSelected ? R.color.green : R.color.dim));
        if (minSize) {
            cvFrame.setRadius(context.getResources().getDimension(R.dimen.medium_radius));
            btnCategory.setTextSize(context.getResources().getDimension(R.dimen.minn_text_size));
        }
    }

}
