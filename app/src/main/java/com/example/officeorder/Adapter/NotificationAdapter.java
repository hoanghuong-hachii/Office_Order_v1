package com.example.officeorder.Adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Fragment.BillDetailFragment;
import com.example.officeorder.Fragment.CartFragment;
import com.example.officeorder.Fragment.ManageOrderFragment;
import com.example.officeorder.Model.Notification;
import com.example.officeorder.R;
import com.example.officeorder.Request.PostBillRequest;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notifications;
    private Context context;
    int idbill;
    private FragmentManager fragmentManager;
    public NotificationAdapter(Context context, List<Notification> notifications, FragmentManager fragmentManager) {
        this.context = context;
        this.notifications = notifications;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }
    public Notification getItem(int position) {
        return notifications.get(position);

    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.titleTextView.setText(notification.getTitle());
        holder.bodyTextView.setText(notification.getBody());

        holder.datetimeTextView.setText("   "+notification.getDatetime());


        holder.ln_layout.setOnClickListener( v->{
            Notification notificationx = notifications.get(position);
            Pattern pattern = Pattern.compile("#(\\d+)");

            Matcher matcher = pattern.matcher(notificationx.getBody());

            if (matcher.find()) {
                String number = matcher.group(1);
                idbill = Integer.parseInt(number);
                Log.e(TAG, "onBindViewHolder:Số: " + number);
            }
            BillDetailFragment fragment = new BillDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("bill_id", idbill);
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_Container, fragment,"bill");
            fragmentTransaction.addToBackStack("bill");
            fragmentTransaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView bodyTextView;
        TextView datetimeTextView;
        LinearLayout ln_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            ln_layout = itemView.findViewById(R.id.ln_layout);
            titleTextView = itemView.findViewById(R.id.title);
            bodyTextView = itemView.findViewById(R.id.body);
            datetimeTextView = itemView.findViewById(R.id.time);
        }
    }
}
