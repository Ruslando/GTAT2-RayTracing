package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Ebic Ray Tracer");
        primaryStage.setScene(new Scene(root, WIDTH, HEIGHT));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void cameraInfoDebug(Camera camera){
        System.out.println("Get L: " + camera.getL());
        System.out.println("Get R: " + camera.getR());
        System.out.println("Get T: " + camera.getT());
        System.out.println("Get B: " + camera.getB());
        System.out.println("Get World Position: " + camera.getWorldposition().toString());
        System.out.println("Get View vector: " + camera.getView().toString());
        System.out.println("Get UP vector: " + camera.getUP().toString());
        System.out.println("Get right vector: " + camera.getU().toString());
        System.out.println("Get get W (back): " + camera.getW().toString());
        System.out.println("Get w_d_redacted: " + camera.getW_d_negated().toString());

    }

}
