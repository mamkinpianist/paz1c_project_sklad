package storage.daos;

import storage.constructors.Order;
import storage.constructors.Product;

import java.util.List;
import java.util.Map;

public interface OrderDao {
    List<Order> getAll();
    Order createOrder(Order order);
    Map<Product, Integer> getProductInOrder(Order order);
    Order update(Order order);
    Boolean OrderControl(Order order);
    Order getById(Long id);
}
