package com.example.leeso.themilkisian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leeso.themilkisian.Sample.Address;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.addresslistrv)
    RecyclerView rv;
    @BindView(R.id.addAddressBtn)
    Button addAddressBtn;

    AddressAdapter addressAdapter;

    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference addressRef;
    List<Address> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        addressRef = mDatabase.getReference().child("Users").child(mUser.getUid()).child("address");
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
        addressAdapter = new AddressAdapter(addressList, AddressActivity.this);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(AddressActivity.this);
        rv.setLayoutManager(layoutManager);
        rv.setHasFixedSize(true);
        rv.setAdapter(addressAdapter);

        addressAdapter.notifyDataSetChanged();

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder createAddressBuilder = new AlertDialog.Builder(AddressActivity.this);
                View createAddressView = LayoutInflater.from(AddressActivity.this).inflate(R.layout.create_new_address_layout,null);



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
                            Toast.makeText(AddressActivity.this, "Please Input Information", Toast.LENGTH_SHORT).show();
                        } else {
                            final Address address = new Address(nameET.getText().toString(),phoneET.getText().toString(),addressET.getText().toString(),noteET.getText().toString());

                            final String[] i = new String[1];

                            addressRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    a = (int) dataSnapshot.getChildrenCount();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            addressRef.child("address " + String.valueOf(a)).setValue(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AddressActivity.this, "Sucessfully Create Address", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else
                                        Toast.makeText(AddressActivity.this, "Failed Create Address", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                });

                createAddressBuilder.show();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(AddressActivity.this, UserActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }    }
}
