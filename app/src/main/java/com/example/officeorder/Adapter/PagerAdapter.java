package com.example.officeorder.Adapter;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.officeorder.Fragment.DestroyFragment;
import com.example.officeorder.Fragment.FinishFragment;
import com.example.officeorder.Fragment.ProcessingFragment;
import com.example.officeorder.Fragment.WaitFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;
    private static final String[] PAGE_TITLES = {"Chờ xác nhận", "Đang giao hàng", "Đã giao","Đã hủy"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new WaitFragment();
            case 1:
                return new ProcessingFragment();
            case 2:
                return new FinishFragment();
            case 3:
                return new DestroyFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SpannableString spannableString = new SpannableString(PAGE_TITLES[position]);
        spannableString.setSpan(new RelativeSizeSpan(0.8f), 0, spannableString.length(), 0);
        return spannableString;
    }
}
