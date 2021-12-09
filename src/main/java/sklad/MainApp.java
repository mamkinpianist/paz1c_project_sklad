package sklad;

import storage.*;
import storage.constructors.Order;

import storage.constructors.Product;
import storage.constructors.User;
import storage.daos.*;

import java.util.*;


public class MainApp {
    public static void main(String[] args) {
       ProductDao productDao = DaoFactory.INSTANCE.getProductDao();
       UserDao userDao = DaoFactory.INSTANCE.getUserDao();
       RoleDao roleDao  =DaoFactory.INSTANCE.getRoleDao();
       CategoriesDAO categoriesDAO = DaoFactory.INSTANCE.getcategoriesDAO();
       OrderDao orderDao = DaoFactory.INSTANCE.getorderDao();
        Date date= java.util.Calendar.getInstance().getTime();
        PositionDao positionDao = DaoFactory.INSTANCE.getPositionDao();
        System.out.println(positionDao.getAll());

    }
}
