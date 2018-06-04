package com.example.leeso.themilkisian;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;


public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide("https://image.ibb.co/dY0JrT/13701267_1772007883074629_4832193901035146762_o.jpg"); //best solution 800x600
                break;
            case 1:
                viewHolder.bindImageSlide("https://image.ibb.co/dY0JrT/13701267_1772007883074629_4832193901035146762_o.jpg");
                break;
            case 2:
                viewHolder.bindImageSlide("https://image.ibb.co/dY0JrT/13701267_1772007883074629_4832193901035146762_o.jpg");
                break;
        }
    }

}
