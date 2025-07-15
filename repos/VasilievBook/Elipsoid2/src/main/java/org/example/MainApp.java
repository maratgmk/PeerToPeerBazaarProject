package org.example;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.example.visulisation.IntersectionVisualizer;


public class MainApp extends Application{

    private double semiMajorAxis = 2.0; // Полуось эллипсоида
    private double semiMinorAxis = 1.5; // Полуось эллипсоида
    private double planeOffset = 1.0;    // Смещение плоскости

    private IntersectionVisualizer visualizer; // Визуализатор

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);

        // Настройка камеры
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-10);
        camera.setTranslateY(-2);
        camera.setRotationAxis(Rotate.Y_AXIS);
        camera.setRotate(20);
        scene.setCamera(camera);

        // Создаем визуализацию
        visualizer = new IntersectionVisualizer(semiMajorAxis, semiMinorAxis, 1.0, planeOffset);
        root.getChildren().add(visualizer);

        // Создаем элементы управления
        VBox controls = new VBox(10);
        TextField majorAxisField = new TextField(String.valueOf(semiMajorAxis));
        TextField minorAxisField = new TextField(String.valueOf(semiMinorAxis));
        TextField planeOffsetField = new TextField(String.valueOf(planeOffset));
        Button applyButton = new Button("Применить");

        // Обработчик нажатия на кнопку
        applyButton.setOnAction(e -> {
            try {
                semiMajorAxis = Double.parseDouble(majorAxisField.getText());
                semiMinorAxis = Double.parseDouble(minorAxisField.getText());
                planeOffset = Double.parseDouble(planeOffsetField.getText());
                updateVisualizer(); // Обновляем визуализацию
            } catch (NumberFormatException ex) {
                // Обработка ошибок ввода
                System.out.println("Введите корректные числа!");
            }
        });
        controls.getChildren().addAll(
                new Label("Большая полуось:"), majorAxisField,
                new Label("Малая полуось:"), minorAxisField,
                new Label("Смещение плоскости:"), planeOffsetField,
                applyButton
        );

        // Добавляем элементы управления в корень
        root.getChildren().add(controls);

        primaryStage.setTitle("Эллипсоид и плоскость");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void updateVisualizer() {
        // Обновляем визуализацию с новыми параметрами
        visualizer.getChildren().clear(); // Очищаем старую визуализацию
        visualizer = new IntersectionVisualizer(semiMajorAxis, semiMinorAxis, 1.0, planeOffset);
        // Добавляем новую визуализацию
        ((Group) visualizer.getParent()).getChildren().add(visualizer);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
