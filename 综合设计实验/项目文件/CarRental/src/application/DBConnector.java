package application;
import java.sql.*;

public class DBConnector {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    private static DBConnector instance = null;

    private Connection conn;
    private Connection transactionConnection;  //带有事务操作时，使用此对象进行连接
    private Statement stm;
    private Statement transactionStatement;

    private DBConnector() throws ClassNotFoundException {
        Class.forName(JDBC_DRIVER);
    }

    public static DBConnector getInstance() {
        if (instance == null) {
            try {
                instance = new DBConnector();
            } catch (ClassNotFoundException e) {
                System.out.println("Cannot load sql driver.");
                System.exit(1);
            }
        }
        return instance;
    }

    public void ConnectDataBase(String host, int port, String dbName,
                                String user, String pass) throws SQLException {
        String url = "jdbc:mysql://" + host + ":" + port +
                "/" + dbName +
                "?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=Asia/Shanghai";
        //连接数据库
        conn = DriverManager.getConnection(url, user, pass);
        stm = conn.createStatement();

        transactionConnection = DriverManager.getConnection(url, user, pass);
        transactionConnection.setAutoCommit(false);
        transactionStatement = transactionConnection.createStatement();
    }

    public void disconnectDB() throws SQLException {
        if (!conn.isClosed())
            conn.close();
        else
            System.out.println("Closed");

        if (!transactionConnection.isClosed())
            transactionConnection.close();
        else
            System.out.println("t closed");
    }

    public ResultSet getCustomerInfo(String id) {
        try {
            return stm.executeQuery(
                    "select * from customer where custid = " + id
            );
        } catch (SQLException e) {
            System.out.println("无法获取用户信息");
            return null;
        }
    }

    public ResultSet getEmployeeInfo(String id) {
        try {
            return stm.executeQuery(
        "select e.eid, e.ename, e.eage, e.epw, d.depname, d.depid\n" +
                "from employee e\n" +
                "inner join department d\n" +
                "    on e.depid = d.depid\n" +
                "where e.eid = " + id
            );
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("无法获取员工信息");
            return null;
        }
    }

    public ResultSet getManagerInfo(String id) {
        try {
            return stm.executeQuery(
                    "select * from employee " +
                            "where eid = (select mid from manager" +
                            "where mid = " + id + ")"
            );
        } catch (SQLException e) {
            System.out.println("无法获取管理员信息");
            return null;
        }
    }
    public ResultSet getAllCarInfo() {
        try {
            return stm.executeQuery("select * from car");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getHistoryRecord(String custid) {
        try {
            return stm.executeQuery(
                    "select o.orderid, c.carid, c.brand, o.money, o.status, o.time, " +
                            "p.money pmoney, p.time ptime, p.detail \n" +
                            "from orderitem as o\n" +
                            "inner join car as c\n" +
                            "    on o.carid = c.carid\n" +
                            "left outer join punishment p on o.orderid = p.orderid\n" +
                            "where o.custid = " + custid +
                            " order by o.orderid desc ");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ResultSet getWholeTable(String tableName) {
        try {
            return stm.executeQuery(
                    "select * from " + tableName
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean tryUseCar(String carID, String custID, String money) {
        try {
            ResultSet resultStatus = transactionStatement.executeQuery("select status from car" +
                                                                " where carid = '" + carID + "'");
            //判断车辆是否已被租用，由于数据库中的数据已更新而与显示的数据造成冲突
            if(!resultStatus.next()) {
                boolean isUsing = resultStatus.getBoolean("status");
                if (isUsing)
                    return false;
            }

            //生成订单号
            ResultSet resultOrderID = transactionStatement.executeQuery("select orderid from orderitem\n" +
                    "order by orderid desc limit 1");

            int curMaxOrderID;
            if(!resultOrderID.next())
                curMaxOrderID = 0;
            else
                curMaxOrderID = Integer.parseInt(resultOrderID.getString("orderid")) + 1;

            System.out.println("curMaxOrderID = " + curMaxOrderID);

            //修改车辆状态为使用中
            transactionStatement.executeUpdate("update car\n" +
                    "set status = 0\n" +
                    "where carid = '" + carID + "'");

            /*
            问题：在用户进行租车操作时，如何将经手员工与订单联系起来？
             */

            String eID = "";
            //向订单表中插入一个表项
            transactionStatement.executeUpdate(
                    "insert into orderitem(orderid, custid, eid, carid, status, time, money)\n" +
                    "values (" + String.format("\"%03d\", \"%s\", \"%s\", \"%s\", false, current_timestamp, \"%s\"",
                            curMaxOrderID, custID, eID, carID, money)  + ")");
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            //执行过程出现错误则回滚
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateCustomerInfo(String custID, String custName, String age) {
        try {
            transactionStatement.executeUpdate("update customer\n" +
                    "set cname = '"  + custName +  "'," +
                    "    cage = " + age +
                    " where custid = '" + custID + "'");
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    public boolean addToCustomerBalance(String custid, String money) {
        try {
            transactionStatement.executeUpdate("update customer set balance = balance + " + money +
                    " where custid = '" +custid + "'");
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    public boolean subFromCustomerBalance(String custid, String cost) {
        try {
            transactionStatement.executeUpdate("update customer set balance = balance - " + cost +
                    " where custid = '" +custid + "'");
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    public boolean joinVIP(String custid, String cost) {
        try {
            System.out.println("update customer " +
                    "set balance = balance - " + cost + " , vip = true" +
                    " where custid = '" + custid + "'");
            transactionStatement.executeUpdate("update customer " +
                    "set balance = balance - " + cost + " , vip = true" +
                    " where custid = '" + custid + "'");
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getOrderItemForEmployee() {
        try {
            return stm.executeQuery(
                    "select o.orderid, o.eid,c.carid,c.brand,\n" +
                            "       c2.custid, c2.cname, o.money, o.time, o.status\n" +
                            "from orderitem o\n" +
                            "inner join car c\n" +
                            "    on o.carid = c.carid\n" +
                            "inner join customer c2\n" +
                            "    on o.custid = c2.custid " +
                            "order by o.orderid desc"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateEmployeeInfo(String eid, String name, String age) {
        try {
            transactionStatement.executeUpdate("update employee\n" +
                    "set ename = '" + name + "'," +
                    "    eage = " + age +
                    " where eid = " + eid);

            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean deleteCarForEmployee(String carId) {
        try {
            stm.executeUpdate(
                    "delete from car where carid = '" + carId + "'" );
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrderForEmployee(String orderid) {
        try {
            stm.executeUpdate(
                "delete from orderitem where orderid = '" + orderid + "'"
            );
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean updataOrderStatusForEmployee(String orderid, String carId, String eid) {
        try {
            transactionStatement.executeUpdate(
                    "update orderitem\n" +
                            "set status = true,\n" +
                            " eid = '" + eid + "'" +
                            "where orderid = '" + orderid + "'");
            transactionStatement.executeUpdate(
                    "update car\n" +
                            "set status = true\n" +
                            "where carid = '" + carId + "'"
            );
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    public boolean addCarForEmployee(
            String carid, String brand, String cost, String pledge) {
        try {
            transactionStatement.executeUpdate(
                    "insert into car(carid, brand, cost, status, pledge) VALUES (" +
                     "'"+ carid + "'" + ", '"+ brand + "'" + ", '"+ cost + "'" + ", false" + ", '"+ pledge + "'" +
                            ")"
            );
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateCarInfoForEmployee(
            String carid, String brand, String cost, String pledge) {
        try {
            transactionStatement.executeUpdate(
                    "update car\n" +
                            "set brand = '" + brand + "'" + ", " +
                            "    cost = '" +  cost + "', " +
                            "    pledge = '" + pledge + "'" +
                            "where carid = '" + carid + "'"
            );
            transactionConnection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                transactionConnection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
