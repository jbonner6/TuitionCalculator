package main;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.classItem.ClassItem;
import main.classItem.DanceClass;
import main.classItem.ModelSingleton;
import main.classItem.NumberTextField;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main extends Application {

    private double width = 800;
    private double height = 497;
    int selectIndex = -1;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainOutline.fxml"));
        primaryStage.setTitle("Tuition Calculator");
        primaryStage.setScene(new Scene(root, width, height));

        loadClasses();

        BorderPane borderPane = (BorderPane)primaryStage.getScene().lookup("#borderPane");
        ModelSingleton.getInstance().setMainBorderPane(borderPane);

        defCenterPanel(borderPane);
        defLeftPanel(borderPane);
        defBottomPanel(borderPane);

        primaryStage.show();
    }

    private void defBottomPanel(BorderPane borderPane){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 20, 10, 20));
        hbox.setSpacing(0);
        hbox.setStyle("-fx-background-color: #333333; vertical-align: middle;");

        CheckBox upFront = new CheckBox("Paid Up Front");
        upFront.setStyle("-fx-font: 14 arial;");
        upFront.setTextFill(Color.WHITESMOKE);

        upFront.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ModelSingleton.getInstance().updateTotalDisplayDiscount(upFront.isSelected());
            }
        });

        Pane spacer = new Pane();
        Text total = new Text("Total:\t$");
        total.setFill(Color.WHITESMOKE);
        total.setStyle("-fx-font: 20 arial;");
        Text amount = new Text(String.format("%.2f",ModelSingleton.getInstance().getCurrentTotal()));
        amount.setStyle("-fx-font: 20 arial;");
        amount.setFill(Color.WHITESMOKE);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        hbox.getChildren().addAll(upFront, spacer, total, amount);


        borderPane.setBottom(hbox);
    }

    private void defLeftPanel(BorderPane borderPane){
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(20, 10, 20, 10));
        vBox.setSpacing(20);
        vBox.setStyle("-fx-background-color: #4d90b8; horizontal-align: middle;");
        vBox.setAlignment(Pos.TOP_CENTER);
        vBox.setPrefWidth(width/3);

        ChoiceBox<DanceClass> choiceBox = new ChoiceBox<>();
        choiceBox.getItems().addAll(ModelSingleton.getInstance().getAllClasses());
        choiceBox.setPrefWidth(Double.MAX_VALUE);
        choiceBox.setTooltip(new Tooltip("Select a Class"));
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                 setSelectIndex(newValue.intValue());
            }
        });

        HBox monthsHolder = new HBox();
        monthsHolder.setStyle("vertical-align: middle;");
        monthsHolder.setAlignment(Pos.CENTER_LEFT);
        double holderHeight = 20;
        monthsHolder.setPrefHeight(holderHeight);
        Text months = new Text("Number of Months:\t");
        months.setStyle("-fx-font: 16 arial; vertical-align: middle;");
        final NumberTextField numText = new NumberTextField();
        numText.setAlignment(Pos.CENTER);
        numText.setPrefHeight(holderHeight);
        HBox.setHgrow(numText, Priority.ALWAYS);
        monthsHolder.getChildren().addAll(months, numText);


        Image image = new Image("file:src/images/add.png");
        ImageView addImage = new ImageView(image);
        addImage.setFitWidth(15);
        addImage.setFitHeight(15);
        Button addButton = new Button("Add", addImage);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = getSelectIndex();
                String name = ModelSingleton.getInstance().getAllClasses().get(index).getName();
                double pricePerMonth = ModelSingleton.getInstance().getAllClasses().get(index).getPrice();
                createNewClassPrice(name, pricePerMonth, Integer.valueOf(numText.getText()));
                updateClassList();
            }
        });
        /*
        * 3 and on class are 20 off / month
        * $5 off per month for 1 class for 2nd or later student in the family
        */

        Pane spacer = new Pane();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Pane divider  = new Pane();
        divider.setPrefWidth(Double.MAX_VALUE);
        divider.setPrefHeight(2);
        divider.setStyle("-fx-background-color: #333333");


        HBox extraStudents = new HBox();
        extraStudents.setStyle("vertical-align: middle;");
        extraStudents.setAlignment(Pos.CENTER_LEFT);
        holderHeight = 20;
        extraStudents.setPrefHeight(holderHeight);
        Text students = new Text("Number of Extra Students:\t");
        students.setStyle("-fx-font: 16 arial; vertical-align: middle;");
        final NumberTextField numStudents = new NumberTextField();
        numStudents.setAlignment(Pos.CENTER);
        numStudents.setPrefHeight(holderHeight);
        HBox.setHgrow(numStudents, Priority.ALWAYS);
        extraStudents.getChildren().addAll(students, numStudents);



        Image clearImageLoc = new Image("file:src/images/reload.png");
        ImageView clearImage = new ImageView(clearImageLoc);
        clearImage.setFitWidth(15);
        clearImage.setFitHeight(15);
        Button clearAll = new Button("CLEAR ALL", clearImage);
        clearAll.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteAllClassItems();
            }
        });

        vBox.getChildren().addAll(choiceBox, monthsHolder, addButton, spacer, divider, extraStudents, clearAll);

        borderPane.setLeft(vBox);
    }

    private void defCenterPanel(BorderPane borderPane){
        VBox classPriceList = new VBox();
        classPriceList.setPadding(new Insets(10, 10, 10, 10));
        classPriceList.setPrefWidth(2*width / 3);
        classPriceList.setPrefHeight(Double.MAX_VALUE);
        classPriceList.setStyle("-fx-background-color: whitesmoke");
        classPriceList.setSpacing(10);
        ModelSingleton.getInstance().setClassList(classPriceList);

        ScrollPane scrollPane = new ScrollPane(classPriceList);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        borderPane.setCenter(scrollPane);
    }



    private void createNewClassPrice(String name, double pricePerMonth, int months){
        System.out.println(name + "\t" + String.valueOf(pricePerMonth) + "\t" + String.valueOf(months));
        final ClassItem hBox = new ClassItem();
        hBox.setTotal(pricePerMonth * months);
        hBox.setPadding(new Insets(10, 20, 10, 20));
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setStyle("-fx-background-color: #333333; " +
                "vertical-align: middle; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 2, 2);" +
                "-fx-border-width: 2px;" +
                "-fx-background-radius: 5px;");

        Image image = new Image("file:src/images/delete.png");
        ImageView deleteImage = new ImageView(image);
        deleteImage.setFitWidth(15);
        deleteImage.setFitHeight(15);
        deleteImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                deleteClassItem(hBox);
            }
        });

        CheckBox fiveDiscount = new CheckBox();
        fiveDiscount.setSelected(false);


        Text className = new Text(name + " - $" + String.valueOf(pricePerMonth) +"/mo x " + String.valueOf(months) +
                                    " months");
        className.setFill(Color.WHITESMOKE);
        className.setStyle("-fx-font: 14 arial;");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text classTotal = new Text("$" + String.format("%.2f", hBox.getTotal()));
        classTotal.setFill(Color.WHITESMOKE);
        classTotal.setStyle("-fx-font: 14 arial;");

        fiveDiscount.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean isSelected = fiveDiscount.isSelected();
                Text total = (Text)hBox.getChildren().get(4);
                if (isSelected){
                    hBox.setTotal((pricePerMonth - 5) * months);
                    classTotal.setText(name + " - $" + String.valueOf(pricePerMonth - 5) +
                            "/mo x " + String.valueOf(months) + " months");
                }
                else {
                    hBox.setTotal(pricePerMonth * months);
                    classTotal.setText(name + " - $" + String.valueOf(pricePerMonth) +
                            "/mo x " + String.valueOf(months) + " months");
                }
                total.setText("$" + String.format("%.2f", hBox.getTotal()));
            }
        });

        hBox.getChildren().addAll(deleteImage, fiveDiscount, className, spacer, classTotal);
        ModelSingleton.getInstance().getDanceClassPrices().add(hBox);
    }

    private void deleteClassItem(ClassItem classItem){
        ModelSingleton.getInstance().getDanceClassPrices().remove(classItem);
        updateClassList();
    }

    private void deleteAllClassItems(){
        ModelSingleton.getInstance().getDanceClassPrices().clear();
        updateClassList();
    }

    private void updateClassList(){
        VBox vBox = ModelSingleton.getInstance().getClassList();
        vBox.getChildren().clear();
        vBox.getChildren().addAll(ModelSingleton.getInstance().getDanceClassPrices());

        double total = 0;
        for (ClassItem temp : ModelSingleton.getInstance().getDanceClassPrices()){
            total += temp.getTotal();
        }
        ModelSingleton.getInstance().setCurrentTotal(total);
        CheckBox cBox = (CheckBox)((HBox)ModelSingleton.getInstance()
                .getMainBorderPane().getBottom()).getChildren().get(0);
        ModelSingleton.getInstance().updateTotalDisplayDiscount(cBox.isSelected());
    }

    private void loadClasses(){
        try {
            File dir = new File(".");
            File fin = new File(dir.getCanonicalPath() + File.separator + "DanceClasses.csv");
            FileReader fr = new FileReader(fin);
            Scanner scanner = new Scanner(fr);
            scanner.nextLine();
            while (scanner.hasNext()){
                String line = scanner.nextLine();
                String[] items = line.split(",");
                if (items.length == 2) {
                    String name = items[0];
                    double price = Double.valueOf(items[1]);
                    System.out.println(name + "\t" + String.valueOf(price));
                    ModelSingleton.getInstance().getAllClasses().add(new DanceClass(name, price));
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }
}
