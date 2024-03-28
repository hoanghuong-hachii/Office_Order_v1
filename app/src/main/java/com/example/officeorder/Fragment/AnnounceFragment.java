package com.example.officeorder.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.officeorder.Adapter.NotificationAdapter;
import com.example.officeorder.Config.NotificationDatabaseHelper;
import com.example.officeorder.Model.Notification;
import com.example.officeorder.R;

import java.util.List;


public class AnnounceFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private NotificationDatabaseHelper db;

    public AnnounceFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_announce, container, false);


        recyclerView = view.findViewById(R.id.rvItemsInCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        db = new NotificationDatabaseHelper(getContext());
        List<Notification> notifications = db.getAllNotifications();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        adapter = new NotificationAdapter(getContext(),notifications, fragmentManager);
        recyclerView.setAdapter(adapter);

        return view;
    }
}