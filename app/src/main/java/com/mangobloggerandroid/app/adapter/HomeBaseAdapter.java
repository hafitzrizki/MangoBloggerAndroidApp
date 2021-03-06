package com.mangobloggerandroid.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mangobloggerandroid.app.R;
import com.mangobloggerandroid.app.activity.HomeActivity;
import com.mangobloggerandroid.app.fragment.FirebaseListFragment;
import com.mangobloggerandroid.app.fragment.WebFragment;
import com.mangobloggerandroid.app.helper.GravitySnapHelper;
import com.mangobloggerandroid.app.model.HomeGroup;
import com.mangobloggerandroid.app.model.HomeItem;

import java.util.ArrayList;
/**
 * Created by ujjawal on 8/10/17.
 *
 */

public class HomeBaseAdapter extends RecyclerView.Adapter<HomeBaseAdapter.ViewHolder> implements GravitySnapHelper.SnapListener {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    public static final int CARD_SIZE_SMALL = 0;
    public static final int CARD_SIZE_MEDIUM = 1;
    public static final int CARD_SIZE_PAGER = 2;

    private Context mContext;
    private ArrayList<HomeGroup> mSnaps;

    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

//    public HomeBaseAdapter() {
//        mSnaps = new ArrayList<>();
//    }
    public HomeBaseAdapter(Context context) {
        mContext = context;
        mSnaps = new ArrayList<>();
    }
    public void addSnap(HomeGroup snap) {
        mSnaps.add(snap);
    }

    @Override
    public int getItemViewType(int position) {
        return HORIZONTAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_home, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HomeGroup snap = mSnaps.get(position);
        holder.snapTextView.setText(snap.getText());

        if (snap.getGravity() == Gravity.START || snap.getGravity() == Gravity.END) {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setOnFlingListener(null);
            new GravitySnapHelper(snap.getGravity(), false, this).attachToRecyclerView(holder.recyclerView);
        } else if (snap.getGravity() == Gravity.CENTER_HORIZONTAL) {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), snap.getGravity() == Gravity.CENTER_HORIZONTAL ?
                    LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
            holder.recyclerView.setOnFlingListener(null);
            new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
        } else if (snap.getGravity() == Gravity.CENTER) { // Pager snap
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setOnFlingListener(null);
            new PagerSnapHelper().attachToRecyclerView(holder.recyclerView);
        } else { // Top / Bottom
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
                    .recyclerView.getContext()));
            holder.recyclerView.setOnFlingListener(null);
            new GravitySnapHelper(snap.getGravity()).attachToRecyclerView(holder.recyclerView);
        }

        HomeItemAdapter itemAdapter = new HomeItemAdapter(mContext, snap.getCardSize(), snap.getApps());
        holder.recyclerView.setAdapter(itemAdapter);
        itemAdapter.setOnItemClickListener(new HomeItemAdapter.OnItemClickListener() {
            @Override
            public void itemClick(HomeItem item, int Position) {
                if(mContext instanceof HomeActivity) {
                    if(item.isWebView()) {
                        ((HomeActivity) mContext).attachFragment(WebFragment.newInstance(item.getUrl(), item.getName()), true);
                    } else {
                        ((HomeActivity) mContext).attachFragment(FirebaseListFragment.newInstance(item.getUrl(), item.getExtra()), true);
                    }
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return mSnaps.size();
    }

    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView snapTextView;
        RecyclerView recyclerView;

         ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.snapTextView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }

    }

}

