package storage.daos;

import storage.constructors.Categories;
import storage.constructors.Position;
import storage.constructors.Product;

import java.util.List;
import java.util.Map;

public interface ProductDao {
    List<Product> getAll();
    Product save(Product product);
    Product delete(Long id);
    Product getbyId(Long id);
    List<Product> getbyCategory(Categories categories);
    Map<Product,Integer> productOnPosiiton(Long position);
    boolean СapacityСheckProduct(Position position,Product product,int count);
    void putProductOnPosition(Position position,Product product,int count);
    void transferProductOnAnotherPosition(Position positionfrom,Product product,Position positionfor,int count);
}
