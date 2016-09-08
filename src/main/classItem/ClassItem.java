package main.classItem;

import javafx.scene.layout.HBox;

/**
 * Created by James on 8/27/2016.
 */
public class ClassItem extends HBox {

    private double total;

    public ClassItem(){
        super();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
