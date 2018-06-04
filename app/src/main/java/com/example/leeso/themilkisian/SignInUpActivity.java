package com.example.leeso.themilkisian;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInUpActivity extends AppCompatActivity {
    @BindView(R.id.backBtn)
    ImageView backBtn;
    @BindView(R.id.mainPicture)
    ImageView mainPicture;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.viewpagertab)
    SmartTabLayout viewpagertab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);
        ButterKnife.bind(this);

        Picasso.with(this).load("https://image.ibb.co/dY0JrT/13701267_1772007883074629_4832193901035146762_o.jpg").networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.logo).into(mainPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(SignInUpActivity.this).load("https://image.ibb.co/dY0JrT/13701267_1772007883074629_4832193901035146762_o.jpg").placeholder(R.drawable.logo).into(mainPicture);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignInUpActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("SignIn", SignInFragment.class)
                .add("SignUp",SignUpFragment.class)
                .create());

        viewpager.setAdapter(adapter);
        viewpagertab.setViewPager(viewpager);

    }
}
