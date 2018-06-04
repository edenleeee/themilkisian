package com.example.leeso.themilkisian;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by User on 10/31/2017.
 */

public class FiltersActivity extends AppCompatActivity {

    private static final String TAG = "FiltersActivity";

    //widgets
    private Button mSave;
    private EditText mLevel, mTAG;
    private ImageView mBackArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mSave = (Button) findViewById(R.id.btnSave);
        mLevel = (EditText) findViewById(R.id.input_level);
        mTAG = (EditText) findViewById(R.id.input_tag);
        mBackArrow = (ImageView) findViewById(R.id.backArrow);

        init();

    }

    private void init(){

        getFilterPreferences();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving...");

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FiltersActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                Log.d(TAG, "onClick: level: " + mLevel.getText().toString());
                editor.putString("CITY", mLevel.getText().toString());
                editor.commit();

                Log.d(TAG, "onClick: TAG: " + mTAG.getText().toString());
                editor.putString("State", mTAG.getText().toString());
                editor.commit();
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back.");
                finish();
            }
        });
    }

    private void getFilterPreferences(){
        Log.d(TAG, "getFilterPreferences: retrieving saved preferences.");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String country = preferences.getString("Level", "");
        String state_province = preferences.getString("TAG", "");

        mLevel.setText(country);
        mTAG.setText(state_province);
    }
}
















