package application;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;


public class customerController implements Initializable {
    private static final class CarInfo extends RecursiveTreeObject<CarInfo> {
        private StringProperty carId;
        private StringProperty carBrand;
        private StringProperty cost;
        private StringProperty status;
        private StringProperty pledge;

        public CarInfo(String carId, String carBrand, int cost, boolean status, int pledge) {
            this.carId = new SimpleStringProperty(carId);
            this.carBrand = new SimpleStringProperty(carBrand);
            this.cost = new SimpleStringProperty(Integer.toString(cost));
            this.status = new SimpleStringProperty(status? "可用": "不可用");
            this.pledge = new SimpleStringProperty(Integer.toString(pledge));
        }
    }

    private static final class HistoryRecord extends RecursiveTreeObject<HistoryRecord> {
        private StringProperty orderID;
        private StringProperty carID;
        private StringProperty carBrand;
        private StringProperty orderMoney;
        private StringProperty orderTime;
        private StringProperty orderStatus;
        private StringProperty note;

        public HistoryRecord(String orderID, String carID, String carBrand, double orderMoney, Timestamp orderTime,
                             boolean orderStatus, String note) {
            this.orderID = new SimpleStringProperty(orderID);
            this.carID = new SimpleStringProperty(carID);
            this.carBrand = new SimpleStringProperty(carBrand);
            this.orderMoney = new SimpleStringProperty(Double.toString(orderMoney));
            this.orderTime = new SimpleStringProperty(
                    orderTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            this.orderStatus = new SimpleStringProperty(orderStatus ? "结束" : "未还车");
            this.note = new SimpleStringProperty(note);
        }
    }


    public static String cname;
    public static String cid;
    public static double balance;
    public static boolean isVip;
    public static int cage;

    @FXML
    private JFXTabPane rootTabPane;
    @FXML
    private Tab tab_carInfo;
    @FXML
    private JFXTextField input_carId;
    @FXML
    private JFXTextField input_brand;
    @FXML
    private JFXTextField input_cost;
    @FXML
    private JFXComboBox ComboBox_CarStatus;
    @FXML
    private JFXButton bt_search;
    @FXML
    private JFXButton bt_UseCar;
    @FXML
    private JFXButton bt_refresh;

    @FXML
    private JFXButton bt_clear;


//    <S> – The type of the TreeItem instances used in this TreeTableView.
    @FXML
    private JFXTreeTableView<CarInfo> table_car;

    /*
    <S> – The type of the TableView generic type (i.e. S == TableView<S>)
    <T> – The type of the content in all cells in this TableColumn.
     */
    @FXML
    private TreeTableColumn<CarInfo, String> columnCarId;

    @FXML
    private TreeTableColumn<CarInfo, String> columnCarBrand;

    @FXML
    private TreeTableColumn<CarInfo, String> ColumnCost;

    @FXML
    private TreeTableColumn<CarInfo, String> ColumnCarStatus;

    @FXML
    private TreeTableColumn<CarInfo, String> ColumnPledge;

    private ObservableList<CarInfo> carList = FXCollections.observableArrayList();

    @FXML
    private Tab tab_personalInfo;

    @FXML
    private JFXTextField tabPerson_Name;

    @FXML
    private JFXTextField tabPerson_ID;

    @FXML
    private JFXTextField tabPerson_Age;

    @FXML
    private JFXTextField tabPerson_Blance;

    @FXML
    private JFXTextField tabPerson_VIP;

    @FXML
    private JFXButton tabPerson_bt_Recharge;

    @FXML
    private JFXButton tabPerson_bt_VIP;

    @FXML
    private JFXButton tabPerson_bt_save;

    @FXML
    private Tab tab_history;

    @FXML
    private JFXTreeTableView<HistoryRecord> table_history;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_oderID;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_CarID;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_carBrand;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_money;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_time;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_status;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_note;

    private ObservableList<HistoryRecord> recordList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //为tab添加一个监听器,用于监听tab的变化
        rootTabPane.getSelectionModel().selectedItemProperty().addListener(listener -> refreshTabView());

        //设置列的单元格对应的属性
        columnCarId.setCellValueFactory( param ->
                param.getValue().getValue().carId);
        columnCarId.setStyle("-fx-alignment: CENTER;");

        columnCarBrand.setCellValueFactory( param ->
                param.getValue().getValue().carBrand);
        columnCarBrand.setStyle("-fx-alignment: CENTER;");

        ColumnCost.setCellValueFactory( param ->
                param.getValue().getValue().cost);
        ColumnCost.setStyle("-fx-alignment: CENTER;");

        ColumnCarStatus.setCellValueFactory( param ->
                param.getValue().getValue().status);
        ColumnCarStatus.setStyle("-fx-alignment: CENTER;");

        ColumnPledge.setCellValueFactory( param ->
                param.getValue().getValue().pledge);
        ColumnPledge.setStyle("-fx-alignment: CENTER;");



        bt_search.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_search.setOnMouseClicked(e -> {
            System.out.println("button search clicked");
            buttonSearchCliecked();
        });
        bt_search.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                System.out.println("button search clicked");
                buttonSearchCliecked();
            }
        });

        bt_UseCar.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_UseCar.setOnMouseClicked(e -> {
            System.out.println("button UseCar clicked");
            buttonUseCarClicked();
        });
        bt_UseCar.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                System.out.println("button UseCar clicked");
                buttonUseCarClicked();
            }
        });

        bt_refresh.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_refresh.setOnMouseClicked(event -> showTabCarInfoDefaultView());
        bt_refresh.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                showTabCarInfoDefaultView();
            }
        });

        bt_clear.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_clear.setOnMouseClicked(event -> {
            input_carId.setText("");
            input_brand.setText("");
            input_cost.setText("");
            ComboBox_CarStatus.setValue(null);
        });
        bt_clear.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                input_carId.setText("");
                input_brand.setText("");
                input_cost.setText("");
                ComboBox_CarStatus.setValue(null);
            }
        });

        tabPerson_bt_Recharge.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        tabPerson_bt_Recharge.setOnMouseClicked(event -> TabPerson_bt_Recharge_Clicked());
        tabPerson_bt_Recharge.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                TabPerson_bt_Recharge_Clicked();
            }
        });

        tabPerson_bt_VIP.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        tabPerson_bt_VIP.setOnMouseClicked(event -> TabPerson_bt_VIP_Clicked());
        tabPerson_bt_VIP.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                TabPerson_bt_VIP_Clicked();
            }
        });

        tabPerson_bt_save.setStyle("-fx-padding: 0.7em 0.57em;\n" +
                "    -fx-font-size: 14.0px;\n" +
                "    -jfx-button-type: RAISED;\n" +
                "    -fx-background-color: rgb(102.0, 153.0, 102.0);\n" +
                "    -fx-pref-width: 200.0;\n" +
                "    -fx-text-fill: WHITE;");
        tabPerson_bt_save.setOnMouseClicked(event -> TabPerson_bt_save_Clicked());
        tabPerson_bt_save.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                TabPerson_bt_save_Clicked();
            }
        });

        tabHistory_oderID.setCellValueFactory(value -> value.getValue().getValue().orderID);
        tabHistory_oderID.setStyle("-fx-alignment: CENTER;");

        tabHistory_CarID.setCellValueFactory(value -> value.getValue().getValue().carID);
        tabHistory_CarID.setStyle("-fx-alignment: CENTER;");

        tabHistory_carBrand.setCellValueFactory(value -> value.getValue().getValue().carBrand);
        tabHistory_carBrand.setStyle("-fx-alignment: CENTER;");

        tabHistory_money.setCellValueFactory(value -> value.getValue().getValue().orderMoney);
        tabHistory_money.setStyle("-fx-alignment: CENTER;");

        tabHistory_time.setCellValueFactory(value -> value.getValue().getValue().orderTime);
        tabHistory_time.setStyle("-fx-alignment: CENTER;");

        tabHistory_status.setCellValueFactory(value -> value.getValue().getValue().orderStatus);
        tabHistory_status.setStyle("-fx-alignment: CENTER;");

        tabHistory_note.setCellValueFactory(value -> value.getValue().getValue().note);
        tabHistory_note.setStyle("-fx-alignment: CENTER;");

        TreeItem<HistoryRecord> rootRecordTable =
                new RecursiveTreeItem<>(recordList, RecursiveTreeObject::getChildren);
        table_history.setRoot(rootRecordTable);
        table_history.setShowRoot(false);

        ComboBox_CarStatus.getItems().addAll("可用", "不可用");


        //所有初始化动作完成后，显示当前tab的页面
        refreshTabView();
    }

    private void refreshTabView() {
        if (rootTabPane.getSelectionModel().getSelectedItem() == tab_carInfo) {
            System.out.println("select car tab");
            showTabCarInfoDefaultView();
        } else if (rootTabPane.getSelectionModel().getSelectedItem() == tab_personalInfo) {
            System.out.println("select person tab");
            showTabPersonalInfoDefaultView();
        } else if (rootTabPane.getSelectionModel().getSelectedItem() == tab_history) {
            System.out.println("select histony tab");
            showTabHistoryDefaultView();
        }
    }

    private void showTabCarInfoDefaultView() {
        ResultSet resultSet = DBConnector.getInstance().getAllCarInfo();
        if (resultSet == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("读取数据库时发生错误....");
            alert.showAndWait();
            return ;
        }
        try {
            carList.clear();
            while (resultSet.next()) {
                carList.add(new CarInfo(
                        resultSet.getString("carid"),
                        resultSet.getString("brand"),
                        resultSet.getInt("cost"),
                        resultSet.getBoolean("status"),
                        resultSet.getInt("pledge")
                ));
            TreeItem<CarInfo> rootCarTable = new RecursiveTreeItem<>(carList, RecursiveTreeObject::getChildren);
            table_car.setRoot(rootCarTable);
            table_car.setShowRoot(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void showTabPersonalInfoDefaultView() {
        ResultSet resultSet = DBConnector.getInstance().getCustomerInfo(customerController.cid);
        try {
            if(resultSet.next()) {
                customerController.cname = resultSet.getString("cname");
                customerController.cage = resultSet.getInt("cage");
                customerController.balance = resultSet.getDouble("balance");
                customerController.isVip = resultSet.getBoolean("vip");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tabPerson_Name.setText(customerController.cname);
        tabPerson_Age.setText(Integer.toString(customerController.cage));
        tabPerson_ID.setText(customerController.cid);
        tabPerson_Blance.setText(Double.toString(customerController.balance));
        tabPerson_VIP.setText(customerController.isVip ? "是" : "否");
    }
    private void showTabHistoryDefaultView() {
        ResultSet recordSet = DBConnector.getInstance().getHistoryRecord(customerController.cid);
        if(recordSet == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText(null);
            alert.setContentText("读取数据库时发生错误....");
            alert.showAndWait();
            return ;
        }
        try {
            recordList.clear();
            while (recordSet.next()) {
                String orderid = recordSet.getString("orderid");
                String carid = recordSet.getString("carid");
                String carBrand = recordSet.getString("brand");
                double money = recordSet.getDouble("money");
                boolean status = recordSet.getBoolean("status");
                Timestamp oTime = recordSet.getTimestamp("time");
                double pmoney = recordSet.getDouble("pmoney");
                Timestamp pTime = recordSet.getTimestamp("ptime");
                String detail = recordSet.getString("detail");
                String note = "";
                if (pTime != null) {
                    note = pTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                            " " + detail + pmoney;
                }
                System.out.println(orderid + carid + carBrand + money + status);
                recordList.add(new HistoryRecord(orderid, carid, carBrand, money, oTime, status, note));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterDistance() {
        if(input_carId.getText().trim().isEmpty() && input_brand.getText().trim().isEmpty()
            && (ComboBox_CarStatus.getSelectionModel().getSelectedIndex() == -1)
            && input_cost.getText().isEmpty()) {
            showTabCarInfoDefaultView();
            System.out.println("table is update, nothing to do");
            return;
        }

        ObservableList<CarInfo> filterList = carList;

        ObservableList<CarInfo> filterByCarIdList = FXCollections.observableArrayList();
        if(!input_carId.getText().trim().isEmpty()) {
            for (CarInfo item : filterList) {
                if (item.carId.getValue().contains(input_carId.getText().trim())) {
                    filterByCarIdList.add(item);
                }
            }
            filterList = filterByCarIdList;
        }

        ObservableList<CarInfo> filterByBrandList = FXCollections.observableArrayList();;
        if(!input_brand.getText().trim().isEmpty()) {
            for (CarInfo item : filterList) {
                if (item.carBrand.getValue().contains(input_brand.getText().trim())) {
                    filterByBrandList.add(item);
                }
            }
            filterList = filterByBrandList;
        }

        ObservableList<CarInfo> filterByCostList = FXCollections.observableArrayList();;
        if(!input_cost.getText().trim().isEmpty()) {
            for (CarInfo item : filterList) {
                if (item.cost.getValue().equals(input_cost.getText().trim())) {
                    filterByCostList.add(item);
                }
            }
            filterList = filterByCostList;
        }

        ObservableList<CarInfo> filterByStatusList = FXCollections.observableArrayList();
        if(ComboBox_CarStatus.getSelectionModel().getSelectedIndex() != -1) {
            for (CarInfo item : filterList) {
                if (item.status.getValue().equals(ComboBox_CarStatus.getValue())) {
                    filterByStatusList.add(item);
                }
            }
            filterList = filterByStatusList;
        }

        TreeItem<CarInfo> rootFilter = new RecursiveTreeItem<>(filterList, RecursiveTreeObject::getChildren);
        table_car.setRoot(rootFilter);
        table_car.setShowRoot(false);
    }

    private void buttonSearchCliecked() {
        filterDistance();
    }

    private void buttonUseCarClicked() {
        if (table_car.getSelectionModel().getSelectedItem() == null) {
            popMsg(bt_UseCar.getScene(), "您还未选择车辆");
            return ;
        }

        CarInfo selecetItem = table_car.getSelectionModel().getSelectedItem().getValue();
        String carID = selecetItem.carId.getValue();
        System.out.println("selece carID: " + carID);

        double carCost = Double.parseDouble(selecetItem.cost.getValue());

        if (customerController.balance < carCost) {
            popBalance(bt_UseCar);
        }

        boolean rtStatus = DBConnector.getInstance().tryUseCar(carID, customerController.cid, Double.toString(carCost));

        if (rtStatus) {
            //更新用户的余额
            customerController.balance = customerController.balance - carCost;

            tabPerson_Blance.setText(Double.toString(customerController.balance));

            DBConnector.getInstance().subFromCustomerBalance(customerController.cid, selecetItem.cost.getValue());

            popMsg(bt_UseCar.getScene(), "租借成功!");
            showTabCarInfoDefaultView();
        }
        else {
            JFXAlert alert = new JFXAlert((Stage) bt_UseCar.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("租借失败！"));
            JFXButton closeButton = new JFXButton("Exit");
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

    private void TabPerson_bt_Recharge_Clicked() {
        System.out.println("TabPerson_bt_Recharge_Clicked");
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("充 值");

        dialog.setHeaderText(null);

        ButtonType enterButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        JFXTextField inputMoney = new JFXTextField();

        grid.add(new Label("充值金额："), 0, 0);
        grid.add(inputMoney, 1, 0);

        Node enterButton = dialog.getDialogPane().lookupButton(enterButtonType);
        enterButton.setDisable(true);

        inputMoney.textProperty().addListener((observable, oldValue, newValue) -> {
            enterButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> inputMoney.requestFocus());

        dialog.setResultConverter(param -> {
            if (param == enterButtonType) {
                return new String(inputMoney.getText().trim());
            }
            return null;
        });

        Optional<String> rt_result = dialog.showAndWait();

        rt_result.ifPresent(s -> {
            boolean rt_status = DBConnector.getInstance().addToCustomerBalance(customerController.cid, s);
            if (rt_status) {
                popMsg(tabPerson_bt_Recharge.getScene(), "充值成功");
                showTabPersonalInfoDefaultView();
            } else {
              popMsg(tabPerson_bt_Recharge.getScene(), "充值失败");
            }
        });

    }
    private void TabPerson_bt_VIP_Clicked() {
        System.out.println("TabPerson_bt_VIP_Clicked");

        if (customerController.isVip) {
            popMsg(tabPerson_bt_VIP.getScene(), "您已经是会员，不需重复操作");
            return;
        }
        if (customerController.balance < 100) {
            popBalance(tabPerson_bt_VIP);
            return;
        }

        String cost = Double.toString(100);
        boolean rtStatus = DBConnector.getInstance().joinVIP(
                customerController.cid, cost
        );

        if (rtStatus) {
            customerController.isVip = true;
            showTabPersonalInfoDefaultView();
            //tabPerson_VIP.setText("是");
        } else {
            popMsg(tabPerson_bt_VIP.getScene(), "加入失败!");
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

    private void popBalance(JFXButton bt) {
        JFXAlert alert = new JFXAlert((Stage) bt.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("余额不足!"));
        JFXButton closeButton = new JFXButton("ACCEPT");
        closeButton.setStyle("-fx-text-fill: #03A9F4;\n" +
                "    -fx-font-weight: BOLD;\n" +
                "    -fx-padding: 0.7em 0.8em;");
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.showAndWait();
        return ;
    }

    private void TabPerson_bt_save_Clicked() {
        System.out.println("TabPerson_bt_save_Clicked");
        String newName= tabPerson_Name.getText().trim();
        int newAge = Integer.parseInt(tabPerson_Age.getText().trim());
        boolean rtStatus = DBConnector.getInstance().updateCustomerInfo(
                customerController.cid, newName, Integer.toString(newAge));

        if (rtStatus) {
            popMsg(tabPerson_bt_save.getScene(), "修改成功");
        } else {
            popMsg(tabPerson_bt_save.getScene(), "修改失败");
        }
        showTabPersonalInfoDefaultView();
    }
}
