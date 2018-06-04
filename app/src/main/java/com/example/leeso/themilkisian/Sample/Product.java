package com.example.leeso.themilkisian.Sample;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    String image;
    String name;
    int price;
    int quantitysold;
    String description;

    public Product() {
    }

    public Product(String image, String name, int price, int quantitysold, String description) {
        this.image = image;
        this.name = name;
        this.price = price;
        this.quantitysold = quantitysold;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantitysold() {
        return quantitysold;
    }

    public void setQuantitysold(int quantitysold) {
        this.quantitysold = quantitysold;
    }

    @Override
    public String toString() {
        return "Product{" +
                "image='" + image + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantitysold=" + quantitysold +
                ", description='" + description + '\'' +
                '}';
    }

    public static Creator<Product> getCREATOR() {
        return CREATOR;
    }

    protected Product(Parcel in) {
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
