package com.example.itemslist.model;

import android.text.Editable;

import java.util.Arrays;

public class Item {
    String name, weight;
    int quantity, size, id;
    float price;
    byte[] image;


    public Item(String name, int quantity, String weight, int size, byte[] image, float price, int id) {
        this.name = name;
        this.weight = weight;
        this.quantity = quantity;
        this.size = size;
        this.image = image;
        this.price = price;
        this.id = id;
    }

    public Item(String name, int quantity, String weight, int size, byte[] image, float price) {
        this.name = name;
        this.weight = weight;
        this.quantity = quantity;
        this.size = size;
        this.image = image;
        this.price = price;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setColor(String weight) {
        this.weight = weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", weight='" + weight + '\'' +
                ", quantity=" + quantity +
                ", size=" + size +
                ", id=" + id +
                ", price=" + price +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
