package com.scimmia.mybus.ad;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scimmia.mybus.R;
import com.scimmia.mybus.utils.base.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ADFragment extends BaseFragment {

    public static ADFragment newInstance() {
        ADFragment fragment = new ADFragment();
        return fragment;
    }

    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ad, container, false);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_ad);
        mToolbar.setTitle("广告");
        return view;
    }

}
