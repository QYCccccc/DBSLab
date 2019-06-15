package application;

import com.jfoenix.controls.*;
import com.sun.javafx.robot.impl.FXRobotHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class loginController implements Initializable {
    @FXML
    JFXTextField input_name;
    @FXML
    JFXPasswordField input_pwd;
    @FXML
    JFXButton bt_clogin;    //顾客登录按钮
    @FXML
    JFXButton bt_elogin;    //员工登录按钮
//    @FXML
//    JFXButton bt_mlogin;    //管理员登录按钮
    @FXML
    GridPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //将用户的键盘鼠标操作绑定到对应的事件处理函数上

        //当操作焦点回到输入框时，取消样式
        input_name.setOnKeyPressed(KeyEvent -> {
            input_name.setStyle("");
        });
        input_name.setOnMouseClicked(MouseEvent -> {
            input_name.setStyle("");
        });
        input_pwd.setOnKeyPressed(KeyEvent -> {
            input_pwd.setStyle("");
        });
        input_pwd.setOnMouseClicked(MouseEvent -> {
            input_pwd.setStyle("");
        });

        bt_clogin.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_clogin.setOnKeyReleased(KeyEvent -> {
            if (KeyEvent.getCode() == KeyCode.ENTER )
                customerLogin();
        });
        bt_clogin.setOnMouseClicked(MouseEvent -> {
            customerLogin();
        });

        bt_elogin.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_elogin.setOnKeyReleased(KeyEvent -> {
            if (KeyEvent.getCode() == KeyCode.ENTER)
                employeeLogin();
        });
        bt_elogin.setOnMouseClicked(MouseEvent -> {
            employeeLogin();
        });

//        bt_mlogin.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
//        bt_mlogin.setOnKeyReleased(KeyEvent -> {
//            if (KeyEvent.getCode() == KeyCode.ENTER)
//                managerLogin();
//        });
//        bt_mlogin.setOnMouseClicked(MouseEvent -> {
//            managerLogin();
//        });

//        rootPane.setStyle("-fx-background-image: url('/view/bg4.jpg')");
    }


    private boolean InputVaild() {
        if (input_name.getText().isEmpty()) {
            input_name.setStyle("-fx-background-color: pink;");
            return false;
        }
        if (input_pwd.getText().isEmpty()) {
            input_pwd.setStyle("-fx-background-color: pink;");
            return false;
        }
        return true;
    }

    void customerLogin() {
        System.out.println("customer enter");
        if(!InputVaild())
            return ;
        //根据用户账号在数据库中查找
        ResultSet resultSet = DBConnector.getInstance().getCustomerInfo(input_name.getText().trim());
        if(resultSet == null) {
            System.out.println("读取数据库错误...");
            return ;
        }

        try {
            if( !resultSet.next()) {
                input_name.setStyle("-fx-background-color: red;");
                popMsg(bt_clogin.getScene(), "不存在该用户");
                return;
            }
            else if (!resultSet.getString("cpw").equals(input_pwd.getText())){
                input_pwd.setStyle("-fx-background-color: red;");
                popMsg(bt_clogin.getScene(), "密码错误");
                return;
            }
            //保存用户基本信息
            String cname = resultSet.getString("cname");
            double balance = resultSet.getDouble("balance");
            String cid = resultSet.getString("custid");
            boolean isVip = resultSet.getBoolean("vip");
            int age = resultSet.getInt("cage");
            System.out.println(cname + " " + age + " " + cid + " " + isVip + " " + balance);

            customerController.cname = cname;
            customerController.balance = balance;
            customerController.cid = cid;
            customerController.isVip = isVip;
            customerController.cage  = age;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        //进入顾客界面
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(getClass().getResource("/view/customer.fxml")));
            FXRobotHelper.getStages().get(0).setScene(scene);
        } catch (IOException e) {
//            e.printStackTrace();
            popMsg(bt_clogin.getScene(), "无法加载界面!");
        }

    }
    void employeeLogin() {
        System.out.println("employee enter");
        if (!InputVaild())
            return;
        //根据用户账号在数据库中查找
        ResultSet resultSet = DBConnector.getInstance().getEmployeeInfo(input_name.getText().trim());
        if (resultSet == null) {
            System.out.println("读取数据库错误...");
            return;
        }
        //检查登录信息是否匹配
        try {
            if (!resultSet.next()) {
                input_name.setStyle("-fx-background-color: red;");
                popMsg(bt_elogin.getScene(), "不存在该用户!");
                return;
            } else if (!resultSet.getString("epw").equals(input_pwd.getText())) {
                input_pwd.setStyle("-fx-background-color: red;");
                popMsg(bt_elogin.getScene(), "密码错误!");
                return;
            }

            employeeController.eid = resultSet.getString("eid");
            employeeController.ename = resultSet.getString("ename");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //进入员工界面
        Scene scene = null;
        try {
            scene = new Scene(FXMLLoader.load(getClass().getResource("/view/employee.fxml")));
            FXRobotHelper.getStages().get(0).setScene(scene);
        } catch (IOException e) {
            popMsg(bt_elogin.getScene(), "无法加载界面!");
        }
    }


    private void popMsg(Scene scene, String s) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(s));
        JFXButton closeButton = new JFXButton("OK");
        closeButton.setStyle("-fx-text-fill: #03A9F4;\n" +
                "    -fx-font-weight: BOLD;\n" +
                "    -fx-padding: 0.7em 0.8em;");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.showAndWait();
        return ;
    }
}
