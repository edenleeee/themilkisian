package com.example.leeso.themilkisian;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leeso.themilkisian.Sample.Product;
import com.facebook.common.Common;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ss.com.bannerslider.Slider;

public class LoginActivity extends AppCompatActivity {

    @BindView( R.id.mainToolbar)
    Toolbar mainToolbar;
    @BindView(R.id.banner_slider1)
    Slider slider;
    @BindView(R.id.beginnerRecyclerView)
    RecyclerView productBeginnerList;
    @BindView(R.id.advancedRecyclerView)
    RecyclerView productAdvancedList;
    @BindView(R.id.signUpBtn)
    Button signUpBtn;
    @BindView(R.id.signInBtn)
    Button signInBtn;
    @BindView(R.id.signinupPart)
    RelativeLayout signinupPart;
    @BindView(R.id.cart_part)
    RelativeLayout cartPart;


    Drawer mainDrawer ;
    @BindView(R.id.badge)
    NotificationBadge notificationBadge;

    IProfile iProfile;

    private DatabaseReference mBeginnerProductReference;
    private DatabaseReference mAdvancedProductReference;
    private DatabaseReference cartRef;
    private DatabaseReference userRef;


    private String android_id;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(null);

         android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String strOut = android_id.substring(0,7);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        printKeyHash();

        if(firebaseUser != null){
            signinupPart.setVisibility(View.GONE);
            iProfile = new ProfileDrawerItem().withName(firebaseUser.getDisplayName()).withEmail(firebaseUser.getEmail()).withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460").withIdentifier(100);
            cartRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("cart");
        } else {
            iProfile = new ProfileDrawerItem();
            cartRef = FirebaseDatabase.getInstance().getReference().child("anonymoususer").child(android_id).child("cart");
        }

        countcart();

        /*userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.removeValue();*/

        cartPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent ( LoginActivity.this , CartActivity.class);
                startActivity(i);
            }
        });

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.logothemilkisign)
                .addProfiles(
                        iProfile
                )
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {
                        Intent i = new Intent(LoginActivity.this,UserActivity.class);
                        if(iProfile.getIdentifier() == 100){
                            startActivity(i);
                        }
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .build();




        mBeginnerProductReference = FirebaseDatabase.getInstance().getReference().child("product").child("beginnerproduct");
        mBeginnerProductReference.keepSynced(true);

        mAdvancedProductReference = FirebaseDatabase.getInstance().getReference().child("product").child("advancedproduct");
        mAdvancedProductReference.keepSynced(true);


        productBeginnerList.setHasFixedSize(true);
        productBeginnerList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        productAdvancedList.setHasFixedSize(true);
        productAdvancedList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignInUpActivity.class);
                startActivity(i);
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,SignInUpActivity.class);
                startActivity(i);
            }
        });

        Slider.init(new PicassoImageLoadingService(this));
        slider.postDelayed(new Runnable() {
            @Override
            public void run() {
                slider.setAdapter(new MainSliderAdapter());
                slider.setSelectedSlide(0);
            }
        }, 1500);
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("Quản lý tài khoản");


//create the drawer and remember the `Drawer` result object
        mainDrawer = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(mainToolbar)
                .addDrawerItems(
                        item1,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Hỗ trợ")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if(drawerItem != null){
                            Intent i ;
                            if(drawerItem.getIdentifier() == 100){
                               i = new Intent( LoginActivity.this,UserActivity.class);
                               startActivity(i);
                            }
                            if(drawerItem.getIdentifier() == 1){
                                i = new Intent( LoginActivity.this,UserActivity.class);
                                startActivity(i);
                            }
                        }
                        return true;
                    }
                })
                .build();
    }

    private void countcart() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Number of Cart", String.valueOf(dataSnapshot.getChildrenCount()));
                notificationBadge.setNumber((int) dataSnapshot.getChildrenCount());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.leeso.themilkisian",
                    PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBeginnerProductView();
        setupAdvancedProductView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupBeginnerProductView();
        setupAdvancedProductView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setupBeginnerProductView();
        setupAdvancedProductView();
    }

    private void setupBeginnerProductView (){
        mBeginnerProductReference.keepSynced(true);
        Query query = mBeginnerProductReference.limitToLast(50);
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product,MembersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, MembersViewHolder>(options) {
            @Override
            public MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_beginner_product, parent, false);

                return new MembersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(MembersViewHolder holder, int position, final Product model) {

                Log.d("a", "onBindViewHolder: " + model.toString());
                holder.setName(model.getName());
                holder.setPrice(model.getPrice());
                holder.setImage(model.getImage());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(LoginActivity.this,DetailProductActivity.class);
                        profileIntent.putExtra("Activity","MemberListActivity");
                        profileIntent.putExtra("which","beginnerproduct");
                        profileIntent.putExtra("name",model.getName());
                        startActivity(profileIntent);
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        Log.d("a", "onStart: " + query.toString());
        productBeginnerList.setAdapter(firebaseRecyclerAdapter);
    }

    private void setupAdvancedProductView (){
        mAdvancedProductReference.keepSynced(true);
        Query query = mAdvancedProductReference.limitToLast(50);
        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product.class)
                        .build();

        FirebaseRecyclerAdapter<Product,MembersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, MembersViewHolder>(options) {
            @Override
            public MembersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_beginner_product, parent, false);

                return new MembersViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(MembersViewHolder holder, int position, final Product model) {

                Log.d("a", "onBindViewHolder: " + model.toString());
                holder.setName(model.getName());
                holder.setPrice(model.getPrice());
                holder.setImage(model.getImage());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(LoginActivity.this,DetailProductActivity.class);
                        profileIntent.putExtra("Activity","MemberListActivity");
                        profileIntent.putExtra("which","advancedproduct");
                        profileIntent.putExtra("name",model.getName());
                        startActivity(profileIntent);
                    }
                });
            }
        };
        firebaseRecyclerAdapter.startListening();
        Log.d("a", "onStart: " + query.toString());
        productAdvancedList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class MembersViewHolder extends RecyclerView.ViewHolder{

        View mView;
        @BindView(R.id.beginnerProductName) TextView beginnerProductName;
        @BindView(R.id.beginnerProductPrice) TextView beginnerProductPrice;
        @BindView(R.id.beginnerProductPicture) ImageView beginnerProductPicture;

        public MembersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this,itemView);
        }
        public void setName(String name){
            beginnerProductName.setText(name);
        }
        public void setPrice(int price){
            String pricee = String.valueOf(price);
            beginnerProductPrice.setText("$" + pricee);

        }
        public void setImage(final String imageurl){

            Picasso.with(mView.getContext()).load(imageurl).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.logo).into(beginnerProductPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mView.getContext()).load(imageurl).placeholder(R.drawable.logo).into(beginnerProductPicture);
                }
            });
        }
    }
}
