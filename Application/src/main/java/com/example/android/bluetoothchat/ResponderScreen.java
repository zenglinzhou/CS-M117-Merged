package com.example.android.bluetoothchat;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ExpandableListAdapter;
import android.widget.Spinner;
import android.widget.ViewFlipper;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;
import com.example.android.common.activities.SampleActivityBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by omart on 12/3/2017.
 */

public class ResponderScreen extends SampleActivityBase{
    private FragmentTransaction transaction;

    private ResponderChat fragment;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.victim_layout);
        if (savedInstanceState == null) {
            transaction = getSupportFragmentManager().beginTransaction();
            fragment = new ResponderChat();
            fragment.setContext(this);
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        //expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data

        //listAdapter = new ExpandableListViewAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        //expListView.setAdapter(listAdapter);
    }
}
