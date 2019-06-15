package application;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //连接数据库
            DBConnector.getInstance().ConnectDataBase("localhost", 3306, "dbslab3", "root", "199727");


            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setTitle("汽车租借系统");
            primaryStage.setOnCloseRequest(WindowEvent -> {
                System.out.println("close");

                try {
                    DBConnector.getInstance().disconnectDB();
                } catch (SQLException e) {
                    System.out.println("关闭数据库时发生错误......");
                }
            });
        } catch (SQLException e) {
            JFXAlert alert = new JFXAlert();
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("Error : 无法连接数据库!"));
            JFXButton closeButton = new JFXButton("Exit");
            closeButton.setStyle("-fx-text-fill: #03A9F4;\n" +
                    "    -fx-font-weight: BOLD;\n" +
                    "    -fx-padding: 0.7em 0.8em;");
            closeButton.setOnAction(event -> alert.hideWithAnimation());
            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.showAndWait();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
