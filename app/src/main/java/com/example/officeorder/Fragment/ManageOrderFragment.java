package com.example.officeorder.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.officeorder.Adapter.PagerAdapter;
import com.example.officeorder.R;
import com.google.android.material.tabs.TabLayout;


public class ManageOrderFragment extends Fragment {

   private ImageButton btn_back;
    public ManageOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_manage_order, container, false);

        TabLayout tabLayout = v.findViewById(R.id.tab_layout);
        ViewPager viewPager = v.findViewById(R.id.view_pager);

        btn_back = v.findViewById(R.id.backButton);

        btn_back.setOnClickListener( item -> {
            getActivity().onBackPressed();
        });
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        return v;
    }
}