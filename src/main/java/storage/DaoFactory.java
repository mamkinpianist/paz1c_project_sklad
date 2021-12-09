package storage;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import storage.MysqlDaos.*;
import storage.daos.*;

public enum DaoFactory {
    INSTANCE;
    private JdbcTemplate jdbcTemplate;
    private RoleDao roleDao;
    private storage.daos.PositionDao PositionDao;
    private CategoriesDAO categoriesDAO;
    private ProductDao productDao;
    private UserDao userDao;
    private OrderDao orderDao;
    private boolean test = false;
    public void testing() {
        test = true;
    }

    public OrderDao getorderDao()
    {
        if(orderDao ==null)
        {
            orderDao = new MysqlOrderDao(getJdbcTemplate());
        }
        return orderDao;
    }
    public UserDao getUserDao()
    {
        if(userDao ==null)
        {
            userDao = new MysqlUserDao(getJdbcTemplate());
        }
        return userDao;
    }
    public ProductDao getProductDao()
    {
        if(productDao ==null)
        {
            productDao = new MysqlProductDao(getJdbcTemplate());
        }
        return productDao;
    }
    public CategoriesDAO getcategoriesDAO()
    {
        if(categoriesDAO ==null)
        {
            categoriesDAO = new MysqlCategoriesDAO(getJdbcTemplate());
        }
        return categoriesDAO;
    }

    public RoleDao getRoleDao()
    {
        if(roleDao ==null)
        {
            roleDao = new MysqlRoleDao(getJdbcTemplate());
        }
        return roleDao;
    }
    public PositionDao getPositionDao()
    {
        if(PositionDao ==null)
        {
            PositionDao = new MysqlPositionDao(getJdbcTemplate());
        }
        return PositionDao;
    }
    private JdbcTemplate getJdbcTemplate() {

            if (jdbcTemplate == null) {
                MysqlDataSource dataSource = new MysqlDataSource();
                dataSource.setUser("sklad_paz1c");
                dataSource.setPassword("paz1c0000");
                if (!test)
                dataSource.setUrl("jdbc:mysql://localhost/paz1c_project?serverTimezone=Europe/Bratislava");
                else
                    dataSource.setUrl("jdbc:mysql://localhost/paz1c_project_test?serverTimezone=Europe/Bratislava");

                jdbcTemplate = new JdbcTemplate(dataSource);
            }
        return jdbcTemplate;
    }
}
