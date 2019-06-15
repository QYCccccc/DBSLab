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
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class employeeController implements Initializable {

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
            this.status = new SimpleStringProperty(status? "已归还": "租借中");
            this.pledge = new SimpleStringProperty(Integer.toString(pledge));
        }
    }

    private static final class HistoryRecord extends RecursiveTreeObject<HistoryRecord> {
        private StringProperty orderID;
        private StringProperty carID;
        private StringProperty carBrand;
        private StringProperty custID;
        private StringProperty custName;
        private StringProperty orderMoney;
        private StringProperty orderTime;
        private StringProperty orderStatus;
        private StringProperty eid;


        public HistoryRecord(String orderID, String carID, String carBrand,
                             String custID, String custName,
                             double orderMoney, Timestamp orderTime,
                             boolean orderStatus, String eid) {
            this.orderID = new SimpleStringProperty(orderID);
            this.carID = new SimpleStringProperty(carID);
            this.carBrand = new SimpleStringProperty(carBrand);
            this.custID = new SimpleStringProperty(custID);
            this.custName = new SimpleStringProperty(custName);
            this.orderMoney = new SimpleStringProperty(Double.toString(orderMoney));
            this.orderTime = new SimpleStringProperty(
                    orderTime.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            this.orderStatus = new SimpleStringProperty(orderStatus ? "结束" : "未还车");
            this.eid = new SimpleStringProperty(eid);
        }
    }


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
    private JFXComboBox<String> ComboBox_CarStatus;

    @FXML
    private JFXButton bt_search;

    @FXML
    private JFXButton bt_deleteCar;

    @FXML
    private JFXButton bt_refresh;

    @FXML
    private JFXButton bt_clear;

    @FXML
    private JFXButton bt_addCar;

    @FXML
    private JFXButton bt_modifCar;


    @FXML
    private JFXTreeTableView<CarInfo> table_car;

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
    private JFXTextField tabPerson_dept;

    @FXML
    private JFXTextField tabPerson_manager;

    @FXML
    private JFXButton tabPerson_bt_save;

    @FXML
    private Tab tab_order;

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
    private TreeTableColumn<HistoryRecord, String> tabHistory_custId;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_custName;

    @FXML
    private TreeTableColumn<HistoryRecord, String> tabHistory_eid;


    private ObservableList<HistoryRecord> orderList = FXCollections.observableArrayList();

    @FXML
    private JFXButton tabOrder_deleteOrder;

    @FXML
    private JFXButton tabOrder_finishOrder;

    public static String eid;
    public static String ename;
    public static int age;
    public static String dept;

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
            buttonSearchCliecked();
        });
        bt_search.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                buttonSearchCliecked();
            }
        });

        bt_deleteCar.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_deleteCar.setOnMouseClicked(e -> {
            bt_deleteCarClicked();
        });
        bt_deleteCar.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                bt_deleteCarClicked();
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

        bt_addCar.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_addCar.setOnMouseClicked(event -> addCarAction());
        bt_addCar.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addCarAction();
            }
        });

        bt_modifCar.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        bt_modifCar.setOnMouseClicked(event -> modifCarAction());
        bt_modifCar.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                modifCarAction();
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

        tabHistory_custId.setCellValueFactory(value -> value.getValue().getValue().custID);
        tabHistory_custId.setStyle("-fx-alignment: CENTER;");

        tabHistory_custName.setCellValueFactory(value -> value.getValue().getValue().custName);
        tabHistory_custName.setStyle("-fx-alignment: CENTER;");

        tabHistory_money.setCellValueFactory(value -> value.getValue().getValue().orderMoney);
        tabHistory_money.setStyle("-fx-alignment: CENTER;");

        tabHistory_time.setCellValueFactory(value -> value.getValue().getValue().orderTime);
        tabHistory_time.setStyle("-fx-alignment: CENTER;");

        tabHistory_status.setCellValueFactory(value -> value.getValue().getValue().orderStatus);
        tabHistory_status.setStyle("-fx-alignment: CENTER;");

        tabHistory_eid.setCellValueFactory(value-> value.getValue().getValue().eid);
        tabHistory_eid.setStyle("-fx-alignment: CENTER;");




        ComboBox_CarStatus.getItems().addAll("租借中", "已归还");

        tabOrder_deleteOrder.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        tabOrder_deleteOrder.setOnMouseClicked(event -> tabOrder_deleteOrderClicked());
        tabOrder_deleteOrder.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tabOrder_deleteOrderClicked();
            }
        });

        tabOrder_finishOrder.setStyle("-fx-text-fill:#5264AE;-fx-font-size:14px;");
        tabOrder_finishOrder.setOnMouseClicked(event -> tabOrder_finishOrderClicked());
        tabOrder_finishOrder.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                tabOrder_finishOrderClicked();
            }
        });


        TreeItem<HistoryRecord> rootRecordTable =
                new RecursiveTreeItem<>(orderList, RecursiveTreeObject::getChildren);
        table_history.setRoot(rootRecordTable);
        table_history.setShowRoot(false);
        //所有初始化动作完成后，显示当前tab的页面
        refreshTabView();

    }

    private void bt_deleteCarClicked() {
        if (table_car.getSelectionModel().getSelectedItem() == null) {
            popMsg(bt_deleteCar.getScene(), "您还未选择车辆！");
            return ;
        }

        CarInfo selectItem = table_car.getSelectionModel().getSelectedItem().getValue();

        boolean rtStatus = DBConnector.getInstance().deleteCarForEmployee(selectItem.carId.getValue());

        if (rtStatus) {
            popMsg(bt_deleteCar.getScene(), "删除成功");
            showTabCarInfoDefaultView();
        } else {
            popMsg(bt_deleteCar.getScene(), "删除失败");
        }
    }

    private void modifCarAction() {
        if (table_car.getSelectionModel().getSelectedItem() == null) {
            popMsg(bt_modifCar.getScene(), "您需要选择车辆!");
            return ;
        }

        CarInfo selectItem = table_car.getSelectionModel().getSelectedItem().getValue();
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("修改车辆信息");
        dialog.setHeaderText(null);

        ButtonType enterButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        JFXTextField carID = new JFXTextField();
        carID.setText(selectItem.carId.getValue());
        carID.setEditable(false);

        JFXTextField carBrand = new JFXTextField();
        carBrand.setText(selectItem.carBrand.getValue());

        JFXTextField carCost = new JFXTextField();
        carCost.setText(selectItem.cost.getValue());

        JFXTextField carPledge = new JFXTextField();
        carPledge.setText(selectItem.pledge.getValue());

        grid.add(new Label("车牌号："), 0, 0);
        grid.add(carID, 1, 0);

        grid.add(new Label("品牌："), 0, 1);
        grid.add(carBrand, 1, 1);

        grid.add(new Label("租金"), 0, 2);
        grid.add(carCost, 1, 2);

        grid.add(new Label("押金"), 0, 3);
        grid.add(carPledge, 1, 3);


        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> carBrand.requestFocus());

        dialog.setResultConverter(param -> {
            if (param == enterButtonType) {
                ArrayList<String> arr_CarInfo= new ArrayList<>();
                arr_CarInfo.add(carID.getText().trim());
                arr_CarInfo.add(carBrand.getText().trim());
                arr_CarInfo.add(carCost.getText().trim());
                arr_CarInfo.add(carPledge.getText().trim());
                return arr_CarInfo;
            }
            return null;
        });

        Optional<ArrayList<String>> carRecord = dialog.showAndWait();
        carRecord.ifPresent(inputcar -> {
            System.out.println("Input car record : ");
            for (String item : inputcar) {
                System.out.println(item);
            }


            boolean rtStatus = DBConnector.getInstance().updateCarInfoForEmployee(
                    inputcar.get(0), inputcar.get(1), inputcar.get(2), inputcar.get(3));

            if (rtStatus) {
                popMsg(bt_modifCar.getScene(), "修改成功!");
                showTabCarInfoDefaultView();
            } else  {
                popMsg(bt_clear.getScene(), "修改失败!");
            }
        });
    }

    private void addCarAction() {
        Dialog<ArrayList<String>> dialog = new Dialog<>();
        dialog.setTitle("添加车辆信息");
        dialog.setHeaderText(null);

        ButtonType enterButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(enterButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        JFXTextField carID = new JFXTextField();
        JFXTextField carBrand = new JFXTextField();
        JFXTextField carCost = new JFXTextField();
        JFXTextField carPledge = new JFXTextField();

        grid.add(new Label("车牌号："), 0, 0);
        grid.add(carID, 1, 0);

        grid.add(new Label("品牌："), 0, 1);
        grid.add(carBrand, 1, 1);

        grid.add(new Label("租金"), 0, 2);
        grid.add(carCost, 1, 2);

        grid.add(new Label("押金"), 0, 3);
        grid.add(carPledge, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> carID.requestFocus());

        dialog.setResultConverter(param -> {
            if (param == enterButtonType) {
                ArrayList<String> arr_CarInfo= new ArrayList<>();
                arr_CarInfo.add(carID.getText().trim());
                arr_CarInfo.add(carBrand.getText().trim());
                arr_CarInfo.add(carCost.getText().trim());
                arr_CarInfo.add(carPledge.getText().trim());
                return arr_CarInfo;
            }
            return null;
        });

        Optional<ArrayList<String>> carRecord = dialog.showAndWait();
        carRecord.ifPresent(inputcar -> {
            System.out.println("Input car record : ");
            for (String item : inputcar) {
                System.out.println(item);
            }
            boolean rtStatus = DBConnector.getInstance().addCarForEmployee(
                    inputcar.get(0), inputcar.get(1),inputcar.get(2), inputcar.get(3)
            );
            if (rtStatus) {
                popMsg(bt_addCar.getScene(), "添加成功!");
                showTabCarInfoDefaultView();
            } else {
                popMsg(bt_addCar.getScene(), "添加失败!");
            }
        });
    }

    private void tabOrder_finishOrderClicked() {
        if (table_history.getSelectionModel().getSelectedItem() == null) {
            popMsg(tabOrder_finishOrder.getScene(), "还未选择订单");
            return ;
        }

        HistoryRecord selectItem = table_history.getSelectionModel().getSelectedItem().getValue();

        boolean rtStatus = DBConnector.getInstance().updataOrderStatusForEmployee(
                selectItem.orderID.getValue(), selectItem.carID.getValue(), employeeController.eid
        );

        if (rtStatus) {
            popMsg(tabOrder_finishOrder.getScene(), "更新成功");
            showTabOrderDefaultView();
        } else  {
            popMsg(tabOrder_finishOrder.getScene(), "更新失败");
        }

    }

    private void tabOrder_deleteOrderClicked() {
        if (table_history.getSelectionModel().getSelectedItem() == null) {
            popMsg(tabOrder_deleteOrder.getScene(), "还未选择订单");
            return ;
        }

        HistoryRecord selectItem = table_history.getSelectionModel().getSelectedItem().getValue();

        boolean rtStatus = DBConnector.getInstance().deleteOrderForEmployee(selectItem.orderID.getValue());

        if (rtStatus) {
            popMsg(tabOrder_deleteOrder.getScene(), "删除成功");
            showTabOrderDefaultView();
        } else  {
            popMsg(tabOrder_deleteOrder.getScene(), "删除失败");
        }
    }

    private void TabPerson_bt_save_Clicked() {
        String newName = tabPerson_Name.getText().trim();
        String newAge = tabPerson_Age.getText().trim();
        boolean rtStatus = DBConnector.getInstance().updateEmployeeInfo(
                employeeController.eid, newName, newAge
        );

        if (rtStatus) {
            popMsg(tabPerson_bt_save.getScene(), "修改成功");
        } else {
            popMsg(tabPerson_bt_save.getScene(), "修改失败");
        }
        showTabPersonalInfoDefaultView();
    }


    private void buttonSearchCliecked() {
        filterDistance();
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

    private void refreshTabView() {
        if (rootTabPane.getSelectionModel().getSelectedItem() == tab_carInfo) {
            System.out.println("select car tab");
            showTabCarInfoDefaultView();
        } else if (rootTabPane.getSelectionModel().getSelectedItem() == tab_personalInfo) {
            System.out.println("select person tab");
            showTabPersonalInfoDefaultView();
        } else if (rootTabPane.getSelectionModel().getSelectedItem() == tab_order) {
            System.out.println("select order tab");
            showTabOrderDefaultView();
        }

    }

    private void showTabCarInfoDefaultView() {
        ResultSet resultSet = DBConnector.getInstance().getAllCarInfo();
        if (resultSet == null) {
            popMsg(table_car.getScene(), "读取数据库发生错误!");
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
    private void showTabOrderDefaultView() {

        ResultSet recordSet = DBConnector.getInstance().getOrderItemForEmployee();
        if(recordSet == null) {
            popMsg(table_car.getScene(), "读取数据库发生错误!");
            return ;
        }
        try {
            orderList.clear();
            while (recordSet.next()) {
                String orderID = recordSet.getString("orderid");
                String carID = recordSet.getString("carid");
                String carBrand = recordSet.getString("brand");
                String custid = recordSet.getString("custid");
                String custName = recordSet.getString("cname");
                double money = recordSet.getDouble("money");
                Timestamp time = recordSet.getTimestamp("time");
                boolean status = recordSet.getBoolean("status");
                String eid = recordSet.getString("eid");
                orderList.add(new HistoryRecord(
                        orderID, carID, carBrand, custid,
                        custName, money, time, status, eid
                ));
            }
            TreeItem<HistoryRecord> rootRecordTable =
                    new RecursiveTreeItem<>(orderList, RecursiveTreeObject::getChildren);
            table_history.setRoot(rootRecordTable);
            table_history.setShowRoot(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showTabPersonalInfoDefaultView() {
        ResultSet resultSet = DBConnector.getInstance().getEmployeeInfo(employeeController.eid);
        try {
            if (resultSet.next()) {
                employeeController.ename = resultSet.getString("ename");
                employeeController.age = resultSet.getInt("eage");
                employeeController.dept = resultSet.getString("depname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tabPerson_ID.setText(employeeController.eid);
        tabPerson_Name.setText(employeeController.ename);
        tabPerson_Age.setText(Integer.toString(employeeController.age));
        tabPerson_dept.setText(employeeController.dept);
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
