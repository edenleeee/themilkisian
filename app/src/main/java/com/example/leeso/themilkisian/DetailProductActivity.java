package com.example.leeso.themilkisian;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.leeso.themilkisian.Sample.Cart;
import com.example.leeso.themilkisian.Sample.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailProductActivity extends AppCompatActivity {

    @BindView(R.id.productImage)
    ImageView productImage;
    @BindView(R.id.detailNameTV)
    TextView detailNameTV;
    @BindView(R.id.detailPriceTV)
    TextView detailPriceTV;
    @BindView(R.id.detailNumberBtn)
    ElegantNumberButton detailNumberBtn;
    @BindView(R.id.describeTV)
    TextView describeTV;
    @BindView(R.id.detailAddBtn)
    Button detailAddBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String productName;
    String productType;


    FirebaseDatabase mDatabase;
    DatabaseReference productRef;
    DatabaseReference customerRef;


    Product product;

    FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;

    String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        mDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if(firebaseUser == null){
            customerRef = mDatabase.getReference().child("anonymoususer").child(android_id).child("cart");
        } else {
            customerRef = mDatabase.getReference().child("Users").child(firebaseUser.getUid()).child("cart");
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                productName= null;
                productType = null;
            } else {
                productName= extras.getString("name");
                productType= extras.getString("which");
            }
        } else {
            productName= (String) savedInstanceState.getSerializable("name");
            productType= (String) savedInstanceState.getSerializable("which");

        }
        Log.d("productName", productName);
        Log.d("productType", productType);



        productRef = mDatabase.getReference().child("product").child(productType).child(productName);

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                product = dataSnapshot.getValue(Product.class);
                Log.d("Product Details", product.toString());
                Picasso.with(DetailProductActivity.this).load(product.getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.logo).into(productImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(DetailProductActivity.this).load(product.getImage()).placeholder(R.drawable.logo).into(productImage);
                    }
                });
                detailNameTV.setText(product.getName());
                detailPriceTV.setText("$" + String.valueOf(product.getPrice()));
                describeTV.setText(product.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        detailAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(DetailProductActivity.this,"Please wait...","Processing",true);
                Cart cart        = new Cart(product,Integer.parseInt(detailNumberBtn.getNumber()));
                customerRef.child(productName).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(DetailProductActivity.this, "Successfully add to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DetailProductActivity.this, "Failed " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(DetailProductActivity.this,LoginActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(DetailProductActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
