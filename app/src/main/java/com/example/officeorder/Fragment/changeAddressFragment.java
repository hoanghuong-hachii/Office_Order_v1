package com.example.officeorder.Fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.officeorder.R;

public class changeAddressFragment extends Fragment {

    public changeAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_change_address, container, false);

        Intent i = getActivity().getIntent();
        if(i != null) {
            String sdt = i.getStringExtra("address");
            if(sdt != null)
                Log.e(TAG, "onCreateView: "+ sdt );
        }
        return v;
    }
}