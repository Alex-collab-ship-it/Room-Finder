package com.example.roomfinder.Models;

import java.util.HashMap;

public class Advertisement {
    public String id;
    public String name;
    public String surname;
    public String address;
    public String description;
    public String distance;
    public String days;
    public String price;
//    public Bitmap author_img;
    public Advertisement(final HashMap<String, String> ad){
        this.id = ad.get("ad_id");
        this.name = ad.get("name");
        this.surname = ad.get("surname");
        this.address = ad.get("address");
        this.description = ad.get("description");
        this.distance = ad.get("distance");
        this.days = ad.get("days");
        this.price = ad.get("price");
    }
}
