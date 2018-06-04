package com.example.leeso.themilkisian.Sample;

import java.util.List;

public class Order {
    List<Cart> cartList;
    Address address;

    public Order() {
    }

    public Order(List<Cart> cartList, Address address) {
        this.cartList = cartList;
        this.address = address;
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
