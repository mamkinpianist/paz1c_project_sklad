package storage.MysqlDaos;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import storage.DaoFactory;
import storage.EntityNotFoundException;

import storage.OrderError;
import storage.constructors.Order;

import storage.constructors.Position;
import storage.constructors.Product;

import storage.daos.OrderDao;
import storage.daos.UserDao;

import javax.xml.crypto.dsig.SignatureMethod;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MysqlOrderDao implements OrderDao {
    private JdbcTemplate jdbcTemplate;

    public MysqlOrderDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Order> getAll() {
        return jdbcTemplate.query("SELECT order.idOrder,order.Name,Summ,OrderStatus,SalesMan,DateTime,products.idProduct,products.name as product_name,productsinorder.count from `order` \n" +
                "left outer join productsinorder on order.idOrder =productsinorder.idOrder \n" +
                "left  outer join products on products.idProduct =  productsinorder.`idProducts` order by order.idOrder;\t", new ResultSetExtractor<List<Order>>() {
            @Override
            public List<Order> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Order> orderList = new ArrayList<>();
                Order order = null;
                while (rs.next()) {
                    Long id = rs.getLong("idOrder");
                    if (order == null || id != order.getIdOrder()) {
                        String name = rs.getString("Name");
                        double Summ = rs.getDouble("Summ");
                        String OrderStatus = rs.getString("OrderStatus");
                        Long SalesMan = rs.getLong("SalesMan");
                        Date DateTime = rs.getTimestamp("DateTime");
                        order = new Order(id, name, Summ, OrderStatus, DaoFactory.INSTANCE.getUserDao().getByid(SalesMan), DateTime);
                        orderList.add(order);
                    }

                    if (rs.getString("product_name") == null) {
                        continue;
                    } else {
                        Long idproduct = rs.getLong("idProduct");
                        int count = rs.getInt("count");
                        order.getProductsInOrder().put(DaoFactory.INSTANCE.getProductDao().getbyId(idproduct), count);

                    }
                }
                return orderList;
            }
        });
    }

    @Override
    public Order createOrder(Order order) {
        Long idOfOrder;
        if (OrderControl(order) == true) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName("`order`");
            insert.usingGeneratedKeyColumns("idOrder");
            insert.usingColumns("Name", "Summ", "OrderStatus", "SalesMan", "DateTime");
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("Name", order.getName());
            namedParameters.addValue("Summ", order.getSumm());
            namedParameters.addValue("OrderStatus", order.getOrderStatus());
            namedParameters.addValue("SalesMan", order.getSalesMan().getIdUser());
            namedParameters.addValue("DateTime", formatter.format(order.getDateTime()));
            idOfOrder = insert.executeAndReturnKey(namedParameters).longValue();
            insert = new SimpleJdbcInsert(jdbcTemplate);
            for (Product product : order.getProductsInOrder().keySet()) {
                Long position = DaoFactory.INSTANCE.getPositionDao().getIdPositionByProduct(product, order.getProductsInOrder().get(product));
                insert.withTableName("`productsinorder`");
                insert.usingColumns("idOrder", "idProducts", "count", "IdPosition");
                namedParameters = new MapSqlParameterSource();
                namedParameters.addValue("idOrder", idOfOrder);
                namedParameters.addValue("idProducts", product.getIdProduct());
                namedParameters.addValue("count", order.getProductsInOrder().get(product));
                namedParameters.addValue("IdPosition", position);
                insert.execute(namedParameters);
                String sqlcountInPosition = "select count from prosuctonposition where idProduct = ? and idPosition = ?";
                int countInPosition =jdbcTemplate.queryForObject(sqlcountInPosition, new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        int pocet = rs.getInt("count");
                        return pocet;
                    }
                },product.getIdProduct(),position);
                sqlcountInPosition = "UPDATE `prosuctonposition` SET  `count` = ? WHERE idProduct = ? and idPosition = ? ";
                jdbcTemplate.update(sqlcountInPosition,countInPosition-order.getProductsInOrder().get(product),product.getIdProduct(),position);
            }
        } else {
            throw new OrderError("Order creation error\n"+"Order:"+order+"\n"+"contact the warehouse manager");
        }
        return new Order(idOfOrder,order.getName(),order.getSumm(),order.getOrderStatus(),order.getSalesMan(),order.getDateTime(),order.getProductsInOrder());
    }


    @Override
    public Map<Product, Integer> getProductInOrder(Order order) {
        String sql = "SELECT * FROM paz1c_project.productsinorder where idOrder =" + order.getIdOrder();
        Map<Product, Integer> map = new HashMap<>();
        return jdbcTemplate.queryForObject(sql, new RowMapper<Map<Product, Integer>>() {
            @Override
            public Map<Product, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long idProducts = rs.getLong("idProducts");
                int count = rs.getInt("count");
                map.put(DaoFactory.INSTANCE.getProductDao().getbyId(idProducts), count);
                return map;
            }
        });
    }

    @Override
    public Order update(Order order) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm:ss", Locale.ENGLISH);
        String sql = "UPDATE `paz1c_project`.`order` SET `Name` = ?, `Summ` = ?, `OrderStatus` = ?, `SalesMan` = ?, `DateTime` = ?> WHERE `idOrder` = ?;";
        int pocet = jdbcTemplate.update(sql, order.getName(), order.getSumm(), order.getOrderStatus(), order.getSalesMan().getIdUser(), formatter.format(order.getDateTime()), order.getIdOrder());
        if (pocet == 1) {
            return order;
        } else {
            throw new EntityNotFoundException("chyba updatumu");
        }
    }

    @Override
    public Boolean OrderControl(Order order) {
        Boolean controla = true;
        Map<Product, Integer> orderProduct = order.getProductsInOrder();
        for (Product product : orderProduct.keySet()) {
            Product product1 = product;
            int pocet = orderProduct.get(product1);
            String sql = "Select count(*) as controla from prosuctonposition where idProduct = ? and count>=?";
            boolean resultfromdb = jdbcTemplate.queryForObject(sql, new RowMapper<Boolean>() {
                @Override
                public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    int controlaCount = rs.getInt("controla");
                    if (controlaCount >= 1) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }, product1.getIdProduct(), pocet);
            if (resultfromdb == false) {
                controla = false;
                break;
            }
        }
        return controla;
    }

    @Override
    public Order getById(Long id) {
        try {
            return jdbcTemplate.queryForObject("Select * from `order` where idOrder = ?", new RowMapper<Order>() {
                @Override
                public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Long id = rs.getLong("idOrder");
                    String name = rs.getString("Name");
                    double Summ = rs.getDouble("Summ");
                    String OrderStatus = rs.getString("OrderStatus");
                    Long SalesMan = rs.getLong("SalesMan");
                    Date DateTime = rs.getTimestamp("DateTime");
                    Order order = new Order(id, name, Summ, OrderStatus, DaoFactory.INSTANCE.getUserDao().getByid(SalesMan), DateTime);
                    order.setProductsInOrder(getProductInOrder(order));
                    return order;
                }
            }, id);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("Order nie existuje");
        }
    }

}
