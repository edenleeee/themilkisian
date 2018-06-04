package com.example.leeso.themilkisian;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends AppCompatActivity {

    @BindView(R.id.personalName)
    TextView personalName;
    @BindView(R.id.personalEmail)
    TextView personalEmail;
    @BindView(R.id.personalPhone)
    TextView personalPhone;
    @BindView(R.id.billsTV)
    TextView billsTV;
    @BindView(R.id.productTV)
    TextView productTV;
    @BindView(R.id.totalTV)
    TextView totalTV;
    @BindView(R.id.orderManage)
    LinearLayout orderManage;
    @BindView(R.id.orderManage_received)
    LinearLayout orderManage_received;
    @BindView(R.id.orderManage_cancel)
    LinearLayout orderManage_cancel;
    @BindView(R.id.orderManage_delivery)
    LinearLayout orderManage_delivery;
    @BindView(R.id.orderManage_success)
    LinearLayout orderManage_success;
    @BindView(R.id.locationSaver)
    LinearLayout locationSaver;
    @BindView(R.id.myComment)
    LinearLayout myComment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @BindView(R.id.logoutBtn)
    Button logoutBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        locationSaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent ( UserActivity.this,AddressActivity.class);
                startActivity(i);
            }
        });

        intializeview();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent i = new Intent(UserActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

    }

    private void intializeview() {
        personalName.setText(firebaseUser.getDisplayName());
        personalEmail.setText(firebaseUser.getEmail());
        personalPhone.setText(firebaseUser.getPhotoUrl().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }    }
}
