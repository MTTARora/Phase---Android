package com.rora.phase.ui.settings.wallet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rora.phase.R;
import com.rora.phase.model.Transaction;
import com.rora.phase.utils.DateTimeHelper;
import com.rora.phase.utils.ui.BaseRVAdapter;
import com.rora.phase.utils.ui.BaseRVViewHolder;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends BaseRVAdapter {

    private List<Transaction> transactionList;

    public TransactionAdapter() {
        this.transactionList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        super.onCreateViewHolder(parent, viewType);
        View root = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);

        return new TransactionVH(root);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRVViewHolder holder, int position) {
        holder.bindData(transactionList.get(position));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    @Override
    public <T> void bindData(T data) {
        transactionList = data == null ? new ArrayList<>() : new ArrayList<>((List<Transaction>) data);
        notifyDataSetChanged();
    }
}

class TransactionVH extends BaseRVViewHolder<Transaction> {

    private TextView tvAmount;
    private TextView tvId;
    private TextView tvDate, tvState;

    public TransactionVH(@NonNull View itemView) {
        super(itemView);

        tvAmount = itemView.findViewById(R.id.amount_transaction_item_tv);
        tvId = itemView.findViewById(R.id.id_transaction_item_tv);
        tvDate = itemView.findViewById(R.id.date_transaction_item_tv);
        tvState = itemView.findViewById(R.id.state_transaction_item_tv);
    }

    @Override
    public void bindData(Transaction data) {
        tvAmount.setText(data.getAmount() + "$");
        tvId.setText("#" + data.getId());
        tvDate.setText(DateTimeHelper.formatIncludeTime(data.getDate()));
        tvState.setText(data.getState());
    }

}
