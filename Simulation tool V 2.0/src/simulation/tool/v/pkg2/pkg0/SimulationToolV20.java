package simulation.tool.v.pkg2.pkg0;
import javafx.application.Application;
import javafx.stage.Stage;
public class SimulationToolV20 extends Application {
    @Override
    public void start(Stage primaryStage) {
       GUI GUI = new GUI(primaryStage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}