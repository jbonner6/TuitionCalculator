package main.classItem;

import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by James on 8/26/2016.
 */
public class ModelSingleton {

    private static ModelSingleton SINGLETON = null;
    private ArrayList<DanceClass> allClasses;
    private ArrayList<ClassItem> danceClassPrices;
    private BorderPane mainBorderPane;
    private double currentTotal;
    private VBox classList;

    private ModelSingleton(){
        allClasses = new ArrayList<>();
        danceClassPrices = new ArrayList<>();
        currentTotal = 0.00;
    }

    public static ModelSingleton getInstance(){
        if (SINGLETON == null){
            SINGLETON = new ModelSingleton();
        }
        return SINGLETON;
    }

    public void updateTotalDisplayDiscount(boolean isSelected){
        Text total = (Text)((HBox) mainBorderPane.getBottom()).getChildren().get(3);
        if (isSelected) {
            total.setText(String.format("%.2f", currentTotal * .9));
        }
        else {
            total.setText(String.format("%.2f",currentTotal));
        }
    }


    public ArrayList<DanceClass> getAllClasses() {
        return allClasses;
    }

    public void setAllClasses(ArrayList<DanceClass> allClasses) {
        this.allClasses = allClasses;
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    public double getCurrentTotal() {
        return currentTotal;
    }

    public void setCurrentTotal(double currentTotal) {
        this.currentTotal = currentTotal;
    }

    public ArrayList<ClassItem> getDanceClassPrices() {
        return danceClassPrices;
    }

    public void setDanceClassPrices(ArrayList<ClassItem> danceClassPrices) {
        this.danceClassPrices = danceClassPrices;
    }

    public VBox getClassList() {
        return classList;
    }

    public void setClassList(VBox classList) {
        this.classList = classList;
    }
}
