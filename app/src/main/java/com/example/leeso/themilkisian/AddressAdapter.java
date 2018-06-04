package com.example.leeso.themilkisian;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.leeso.themilkisian.Sample.Address;
import com.example.leeso.themilkisian.Sample.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Address address;

    private List<Address> addressList;
    private Context mContext;
    private FirebaseDatabase mDatabase;
    private DatabaseReference addressRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public AddressAdapter(List<Address> addressList, Context mContext) {
        this.addressList = addressList;
        this.mContext = mContext;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.addressTV)
        TextView addressTV;
        @BindView(R.id.phoneTV)
        TextView phoneTV;
        @BindView(R.id.noteTV)
        TextView noteTV;


        public AddressViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this,itemView);

        }
        public void bindCart(final Address address) {
            mDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            addressRef = mDatabase.getReference().child("Users").child(mUser.getUid()).child("address");
            nameTV.setText(address.getName());
            addressTV.setText(address.getAddress());
            phoneTV.setText(address.getPhone());
            noteTV.setText(address.getNote());

        }
    }
    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_singleview_layout, parent, false);
        AddressViewHolder viewHolder = new AddressViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        holder.bindCart(addressList.get(position));
    }
    @Override
    public int getItemCount() {
        return addressList.size();
    }
}
