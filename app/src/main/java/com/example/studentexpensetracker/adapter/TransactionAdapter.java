package com.example.studentexpensetracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentexpensetracker.R;
import com.example.studentexpensetracker.models.Transaction;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private final List<Transaction> transactionList;

    private final OnTransactionClickListener listener;

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }

    public TransactionAdapter(
            List<Transaction> transactionList,
            OnTransactionClickListener listener) {

        this.transactionList = transactionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        Transaction transaction = transactionList.get(position);

        holder.txtCategory.setText(transaction.getCategory());
        holder.txtDescription.setText(transaction.getDescription());
        holder.txtDate.setText(transaction.getDate());

        DecimalFormat format = new DecimalFormat("$#,##0.00");
        holder.txtAmount.setText(format.format(transaction.getAmount()));

        if (transaction.getType().equals("Income")) {
            holder.txtAmount.setTextColor(
                    holder.itemView.getResources().getColor(R.color.income_green));
        } else {
            holder.txtAmount.setTextColor(
                    holder.itemView.getResources().getColor(R.color.expense_red));
        }

        holder.itemView.setOnClickListener(v ->
                listener.onTransactionClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView txtCategory;
        TextView txtDescription;
        TextView txtDate;
        TextView txtAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
        }
    }
}