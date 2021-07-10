package com.dev.food_colorie_counter.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.dev.food_colorie_counter.R;
import com.dev.food_colorie_counter.utils.History;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> historyList;
    private LayoutInflater mInflater;
    private Context mContext;
    private History history;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public HistoryAdapter(Context context, List<History> historyList, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.historyList = historyList;
        this.mContext = context;
        this.mClickListener = mClickListener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.cell_history, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        history = historyList.get(position);

        holder.cell_timestatue.setText(history.getTimestatue());
        holder.cell_calorie.setText(String.format("%.1f", Double.valueOf(history.getCalorie()) * Double.valueOf(history.getServingWeight())) + " Cal");
        holder.cell_foodname.setText(history.getFoodname());
        holder.cell_foodgroup.setText(history.getFoodgroup());
        Picasso.with(mContext).load(history.getFoodimage()).into(holder.cell_foodimage);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView cell_foodimage, btn_detail, btn_add;
        TextView cell_timestatue, cell_calorie, cell_foodname, cell_foodgroup;

        ViewHolder(View itemView) {
            super(itemView);

            cell_foodimage = itemView.findViewById(R.id.cell_foodimage);
            cell_timestatue = itemView.findViewById(R.id.cell_timestatue);
            cell_calorie = itemView.findViewById(R.id.cell_calorie);
            cell_foodname = itemView.findViewById(R.id.cell_foodname);
            cell_foodgroup = itemView.findViewById(R.id.cell_foodgroup);
            btn_detail = itemView.findViewById(R.id.btn_detail);
            btn_add = itemView.findViewById(R.id.btn_add);

            btn_detail.setOnClickListener(this);
            btn_add.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

