package storage.MysqlDaos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;
import storage.DaoFactory;
import storage.EntityNotFoundException;
import storage.constructors.Categories;
import storage.daos.CategoriesDAO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlCategoriesDAOTest {

    private CategoriesDAO categoriesDAO;
    private Categories savedCategories;
    private boolean usedelete = true;
    public MysqlCategoriesDAOTest()
    {
        DaoFactory.INSTANCE.testing();
        categoriesDAO = DaoFactory.INSTANCE.getcategoriesDAO();
    }

    @org.junit.jupiter.api.Test
    void getAll() {
        List<Categories> categoriesList = DaoFactory.INSTANCE.getcategoriesDAO().getAll();
        assertTrue(categoriesList.size() >= 0);
        assertNotNull(categoriesList);
        for (int i = 0; i < categoriesList.size(); i++) {
            assertNotNull(categoriesList.get(i));
        }

    }
    @AfterEach
    void tearDown() throws Exception {
        if(savedCategories!=null && usedelete==true)
        categoriesDAO.delete(savedCategories.getIdCategories());

        usedelete=true;
    }

    @org.junit.jupiter.api.Test
    void save() {
        int size = categoriesDAO.getAll().size();
        savedCategories = new Categories("testovaciaCategoria1");
        Categories categories = categoriesDAO.save(savedCategories);
        savedCategories.setIdCategories(categories.getIdCategories());
        assertEquals(categories,savedCategories);
        assertEquals(size+1,categoriesDAO.getAll().size());
        savedCategories.setCategoria("testovaciaCategoria2");
        Categories updatedCategories = categoriesDAO.save(savedCategories);
        assertEquals(updatedCategories.getCategoria(),savedCategories.getCategoria());
        assertEquals(categoriesDAO.getbyID(updatedCategories.getIdCategories()).getIdCategories(),updatedCategories.getIdCategories());
    }

    @org.junit.jupiter.api.Test
    void delete() {
        List<Categories> categoriesList = DaoFactory.INSTANCE.getcategoriesDAO().getAll();
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                DaoFactory.INSTANCE.getcategoriesDAO().delete((long) (categoriesList.size()+1));
            }
        });
        Categories categoriesTodelete = new Categories("test");
        Categories saved = categoriesDAO.save(categoriesTodelete);
        Categories saved2 =  categoriesDAO.delete(saved.getIdCategories());
        assertEquals(saved,saved2);
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                categoriesDAO.delete(saved.getIdCategories());
            }
        });
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                categoriesDAO.delete(-1L);
            }
        });
        usedelete = false;
    }

    @org.junit.jupiter.api.Test
    void getbyID() {
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                DaoFactory.INSTANCE.getcategoriesDAO().getbyID(-1L);
            }
        });
        List<Categories> categoriesList = DaoFactory.INSTANCE.getcategoriesDAO().getAll();
        Categories categories = categoriesList.get(7);
        Categories categories1 =  DaoFactory.INSTANCE.getcategoriesDAO().getbyID(8L);
        assertEquals(categories.getIdCategories(), categories1.getIdCategories());
        assertEquals(categories.getCategoria(),categories1.getCategoria());
    }

}