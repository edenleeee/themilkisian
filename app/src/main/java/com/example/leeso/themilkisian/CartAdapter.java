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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Cart removeCart;

    private List<Cart> cartList;
    private Context mContext;
    private FirebaseDatabase mDatabase;
    private DatabaseReference cartRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public CartAdapter(List<Cart> cartList, Context mContext) {
        this.cartList = cartList;
        this.mContext = mContext;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.cartProductPicture)
        ImageView cartProductPicture;
        @BindView(R.id.cartProductName)
        TextView cartProductName;
        @BindView(R.id.cartProductPrice)
        TextView cartProductPrice;
        @BindView(R.id.cartNumberBtn)
        ElegantNumberButton cartNumberBtn;
        @BindView(R.id.view_background)
        RelativeLayout view_background;
        @BindView(R.id.view_foreground)
        RelativeLayout view_foreground;

        public CartViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this,itemView);

        }
        public void bindCart(final Cart cart) {
            mDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            if(mUser == null){
                cartRef = mDatabase.getReference().child("anonymoususer").child(FirebaseInstanceId.getInstance().getToken()).child("cart");
            } else {
                cartRef = mDatabase.getReference().child("Users").child(mUser.getUid()).child("cart");
            }

            cartNumberBtn.setNumber(String.valueOf(cart.getQuantity()));
            cartProductName.setText(cart.getProduct().getName());
            cartProductPrice.setText("$" + String.valueOf(cart.getProduct().getPrice()*Integer.parseInt(cartNumberBtn.getNumber())));
            cartNumberBtn.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    cartProductPrice.setText("$" + String.valueOf(cart.getProduct().getPrice()*Integer.parseInt(cartNumberBtn.getNumber())));
                }
            });
            Picasso.with(mView.getContext()).load(cart.getProduct().getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.logo).into(cartProductPicture, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError() {
                    Picasso.with(mView.getContext()).load(cart.getProduct().getImage()).placeholder(R.drawable.logo).into(cartProductPicture);
                }
            });

        }
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_cart_layout, parent, false);
        CartViewHolder viewHolder = new CartViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bindCart(cartList.get(position));
    }
    @Override
    public int getItemCount() {
        return cartList.size();
    }
    public void removeItem(int position) {
        removeCart = cartList.get(position);
        cartRef.child(removeCart.getProduct().getName()).removeValue();
        cartList.remove(position);


        notifyItemRemoved(position);
    }
    public void restoreItem(Cart cart, int position) {
        cartRef.child(removeCart.getProduct().getName()).setValue(removeCart);

        cartList.add(position, cart);


        notifyItemInserted(position);
    }
}
