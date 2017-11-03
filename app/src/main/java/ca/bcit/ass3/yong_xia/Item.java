package ca.bcit.ass3.yong_xia;

/**
 * Created by Alex on 01/11/2017.
 */

public class Item {
    private String name;
    private String unit;
    private int quantity;

    public Item(String name, String unit, int quantity) {
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }
}
