package com.example.leeso.themilkisian;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeso.themilkisian.Sample.Address;
import com.example.leeso.themilkisian.Sample.Cart;
import com.example.leeso.themilkisian.Sample.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    FirebaseDatabase mDatabase;
    DatabaseReference cartRef;
    DatabaseReference orderRef;
    DatabaseReference hardwareRef;
    DatabaseReference userRef;
    DatabaseReference addressRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    List<Cart> listCart = new ArrayList<>();
    CartAdapter cartAdapter;


    @BindView(R.id.cartRV)
    RecyclerView cartRV;


    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;

    @BindView(R.id.cartSubmitBtn)
    Button cartSubmitBtn;

    @BindView(R.id.toolbar)
    Toolbar toolbar;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        hardwareRef = FirebaseDatabase.getInstance().getReference().child("hardware");
        if(mUser == null){
            cartRef = mDatabase.getReference().child("anonymoususer").child(FirebaseInstanceId.getInstance().getToken()).child("cart");

        } else {
            userRef = mDatabase.getReference().child("Users").child(mUser.getUid());
            cartRef = mDatabase.getReference().child("Users").child(mUser.getUid()).child("cart");
            orderRef = mDatabase.getReference().child("Users").child(mUser.getUid()).child("order");
            addressRef = mDatabase.getReference().child("Users").child(mUser.getUid()).child("address");
        }

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Cart cart = postSnapshot.getValue(Cart.class);
                    Log.d("Cart Lists", cart.toString());
                    listCart.add(cart);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d("List Cart Content", listCart.toString());
        cartAdapter = new CartAdapter(listCart, CartActivity.this);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(CartActivity.this);
        cartRV.setLayoutManager(layoutManager);
        cartRV.setHasFixedSize(true);
        cartRV.setItemAnimator(new DefaultItemAnimator());
        cartRV.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(cartRV);
        cartRV.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();


        cartSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser == null){
                    Toast.makeText(CartActivity.this, "Please Sign in to Place Order", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(CartActivity.this,SignInUpActivity.class);
                    startActivity(i);
                } else {
                    checkAddress();
                }
            }
        });
    }

    private void checkAddress() {


        final AlertDialog.Builder checkAddressBuilder = new AlertDialog.Builder(CartActivity.this);
        View checkAdressView = LayoutInflater.from(CartActivity.this).inflate(R.layout.cart_address_layout,null);

        Context mContext = checkAdressView.getContext();

        final TextView warningTV = checkAdressView.findViewById(R.id.warningTV);
        RecyclerView addressListRV= checkAdressView.findViewById(R.id.addressListRV);
        AddressAdapter addressAdapter;
        final List<Address> addressList = new ArrayList<>();

        addressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Address address = postSnapshot.getValue(Address.class);
                    addressList.add(address);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addressAdapter = new AddressAdapter(addressList, CartActivity.this);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(CartActivity.this);
        addressListRV.setLayoutManager(layoutManager);
        addressListRV.setHasFixedSize(true);
        addressListRV.addOnItemTouchListener(new RecyclerItemClickListener(mContext, addressListRV, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Order order = new Order(listCart,addressList.get(position));

                Random r = new Random();
                int i1 = r.nextInt(9999 - 1000) + 1000;

                orderRef.child("themilkisian_received").child(String.valueOf(i1)).setValue(order);


                /*cartRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                 Cart cart = postSnapshot.getValue(Cart.class);
                                *//*orderRef.child("themilkisian_received").child(cart.getProduct().getName()).setValue(cart).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            cartRef.child(cart.getProduct().getName()).removeValue();
                                            checkAddress();
                                        } else {
                                            Toast.makeText(CartActivity.this, "Failed Order", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                hardwareRef.child(cart.getProduct().getName()).setValue(cart.getQuantity());*//*
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        addressListRV.setAdapter(addressAdapter);

        addressAdapter.notifyDataSetChanged();

        userRef.child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    warningTV.setVisibility(View.VISIBLE);
                } else
                    warningTV.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final AlertDialog.Builder createAddressBuilder = new AlertDialog.Builder(CartActivity.this);
        View createAddressView = LayoutInflater.from(CartActivity.this).inflate(R.layout.create_new_address_layout,null);



        final EditText nameET = createAddressView.findViewById(R.id.nameET);
        final EditText phoneET= createAddressView.findViewById(R.id.phoneET);
        final EditText addressET = createAddressView.findViewById(R.id.addressET);
        final EditText noteET = createAddressView.findViewById(R.id.noteET);

        createAddressBuilder.setView(createAddressView);

        createAddressBuilder.setPositiveButton("Create Address", new DialogInterface.OnClickListener() {

            int a ;

            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if(nameET.getText().toString().equals("") || addressET.getText().toString().equals("") || addressET.getText().toString().equals("") || noteET.getText().toString().equals("") ){
                    Toast.makeText(CartActivity.this, "Please Input Information", Toast.LENGTH_SHORT).show();
                } else {
                    final Address address = new Address(nameET.getText().toString(),phoneET.getText().toString(),addressET.getText().toString(),noteET.getText().toString());

                    Random r = new Random();
                    int i1 = r.nextInt(9999 - 1000) + 1000;


                    userRef.child("address").child("address " + String.valueOf(i1)).setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CartActivity.this, "Sucessfully Create Address", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else
                                Toast.makeText(CartActivity.this, "Failed Create Address", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
        checkAddressBuilder.setView(checkAdressView);
        checkAddressBuilder.setNegativeButton("Create New Address", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                createAddressBuilder.show();
            }
        });
        checkAddressBuilder.setPositiveButton("Submit Address", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        checkAddressBuilder.show();
    }
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartAdapter.CartViewHolder) {
            // get the removed item name to display it in snack bar
            String name = listCart.get(viewHolder.getAdapterPosition()).getProduct().getName();

            // backup of removed item for undo purpose
            final Cart deletedItem = listCart.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            cartAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinator, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cartAdapter.restoreItem(deletedItem, deletedIndex);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }    }
}
