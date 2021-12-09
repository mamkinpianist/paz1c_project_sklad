package storage.MysqlDaos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import storage.DaoFactory;
import storage.EntityNotFoundException;
import storage.constructors.Position;
import storage.constructors.Product;
import storage.daos.CategoriesDAO;
import storage.daos.PositionDao;
import storage.daos.ProductDao;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MysqlPositionDaoTest {
    PositionDao positionDao;
    Position testPosition;
    ProductDao productDao;
    CategoriesDAO categoriesDAO;
    boolean deletable = true;

    public MysqlPositionDaoTest() {
        DaoFactory.INSTANCE.testing();
        positionDao = DaoFactory.INSTANCE.getPositionDao();
        categoriesDAO = DaoFactory.INSTANCE.getcategoriesDAO();
        productDao = DaoFactory.INSTANCE.getProductDao();
    }

    @BeforeEach
    void setUp() {
        testPosition = new Position(1, 10, "c", 150.25, 130.25, 10, 5000);
    }

    @AfterEach
    void tearDown() {
        if (testPosition != null && deletable == true)
            positionDao.delete(testPosition.getIdPosiiton());

        deletable = true;
    }

    @Test
    void getAll() {
        List<Position> positionList = positionDao.getAll();
        assertTrue(positionList.size() >= 0);
        assertTrue(positionList != null);
        for (int i = 0; i < positionList.size(); i++) {
            assertNotNull(positionList.get(i));
        }
        deletable = false;
    }

    @Test
    void save() {
        Position savePosition = positionDao.save(testPosition);
        testPosition.setIdPosiiton(savePosition.getIdPosiiton());
        assertEquals(savePosition, testPosition);

        Long id = savePosition.getIdPosiiton();
        savePosition.setIdPosiiton(-1L);
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                positionDao.save(savePosition);
            }
        });
        savePosition.setIdPosiiton(id);
        savePosition.setPositionNumber(2);
        Position udpdated = positionDao.save(savePosition);
        assertEquals(savePosition, udpdated);
    }

    @Test
    void delete() {
        int size = positionDao.getAll().size();
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                positionDao.delete((long) size + 1);
            }
        });
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                positionDao.delete(-1L);
            }
        });
        Position saved = positionDao.save(testPosition);
        Position saved2 = positionDao.delete(saved.getIdPosiiton());
        assertEquals(saved, saved2);
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                positionDao.getById(saved.getIdPosiiton());
            }

            ;
        });
        deletable = false;
    }

    @Test
    void getById() {
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                positionDao.getById(-1L);
            }
        });

        Position save = positionDao.save(testPosition);
        testPosition.setIdPosiiton(save.getIdPosiiton());
        assertEquals(save, positionDao.getById(save.getIdPosiiton()));
    }

    @Test
    void fullnessOfPositionV() {
        Position position = new Position(1, 10, "c", 150.25, 130.25, 10, 5000);
        Product product = new Product("testProduct", "test", "fdsfsd", 130.25, null, 150.25, 10, 130.25, 1, categoriesDAO
                .getbyID(1L));
        position = positionDao.save(position);
        Map<Position, Double> map = positionDao.fullnessOfPositionV();
        for (Position position1: map.keySet())
        {
            if(position1.getIdPosiiton().equals(position))
            {
                assertEquals(map.get(position1),100);
            }
        }
        product = productDao.save(product);
        productDao.putProductOnPosition(position,product,1);
        map = positionDao.fullnessOfPositionV();
        for (Position position1: map.keySet())
        {
            if(position1.getIdPosiiton().equals(position))
            {
                assertEquals(map.get(position1),0);
            }
        }
        deletable = false;
    }

    @Test
    void getСapacityOfPositionV() {
        Position save = positionDao.save(testPosition);
        testPosition.setIdPosiiton(save.getIdPosiiton());
        double capacity = save.getHeight() * save.getWidth() * save.getLength();
        double capacityFromMethod = positionDao.getСapacityOfPositionV(save.getIdPosiiton());
        assertEquals(capacity, capacityFromMethod);
    }

    @Test
    void getIdPositionByProduct() {
        Position position = new Position(1, 10, "c", 150.25, 130.25, 10, 5000);
        position = positionDao.save(position);
        Product product = new Product("testProduct", "test", "fdsfsd", 10, null, 10, 10, 10, 1, categoriesDAO
                .getbyID(1L));
        product = productDao.save(product);
        productDao.putProductOnPosition(position, product, 10);
        List<Position> positionList = positionDao.getAll();
        Position position1 = null;
        for (int i = 0; i < positionList.size(); i++) {
            if (positionList.get(i).getIdPosiiton().equals(position.getIdPosiiton())) {
                position1 = positionList.get(i);
                break;
            }
        }
        Map<Product, Integer> productIntegerMap = position1.getProducts();
        Product geted = null;
        int pocetgeted = 0;
        for (Product product1 : productIntegerMap.keySet()) {
            if (product1.getIdProduct().equals(product.getIdProduct())) {
                geted = product1;
                pocetgeted = productIntegerMap.get(product1);
                break;
            }
        }
        assertEquals(product, geted);
        position.getProducts().put(geted, 10);
        assertEquals(position, position1);
        assertEquals(pocetgeted, 10);
        deletable = false;
    }
}