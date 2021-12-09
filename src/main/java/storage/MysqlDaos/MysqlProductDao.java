package storage.MysqlDaos;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import storage.constructors.Categories;
import storage.DaoFactory;
import storage.EntityNotFoundException;
import storage.constructors.Position;
import storage.constructors.Product;
import storage.daos.CategoriesDAO;
import storage.daos.ProductDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlProductDao implements ProductDao {
    private JdbcTemplate jdbcTemplate;

    public MysqlProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> getAll() {
        return jdbcTemplate.query("SELECT * FROM products", new RowMapper<Product>() {
            @Override
            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("idProduct");
                String name = rs.getString("name");
                String manufacture = rs.getString("manufacture");
                String EAN = rs.getString("EAN");
                Double weight = rs.getDouble("weight");
                String taste = rs.getString("taste");
                Double height = rs.getDouble("height");
                Double length = rs.getDouble("length");
                Double width = rs.getDouble("width");
                int piecesInPackage = rs.getInt("piecesInPackage");
                CategoriesDAO categoriesDAO = DaoFactory.INSTANCE.getcategoriesDAO();
                Categories cat = categoriesDAO.getbyID(rs.getLong("Categories"));
                return new Product(id, name, manufacture, EAN, weight, taste, height, length, width, piecesInPackage, cat);
            }
        });
    }

    @Override
    public Product save(Product product) {
        if (product.getIdProduct() == null)//Insert
        {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName("products");
            insert.usingGeneratedKeyColumns("idProduct");
            insert.usingColumns("name","manufacture","EAN","weight","taste","height","length","width","piecesInPackage","Categories");
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("name",product.getName());
            namedParameters.addValue("manufacture",product.getManufacture());
            namedParameters.addValue("EAN",product.getEAN());
            namedParameters.addValue("weight",product.getWeight());
            namedParameters.addValue("taste",product.getTaste());
            namedParameters.addValue("height",product.getHeight());
            namedParameters.addValue("length",product.getLength());
            namedParameters.addValue("width",product.getWidth());
            namedParameters.addValue("piecesInPackage",product.getPiecesInPackage());
            namedParameters.addValue("Categories",product.getCategories().getIdCategories());
            Long id = insert.executeAndReturnKey(namedParameters).longValue();
            return new Product(id,product.getName(), product.getManufacture(), product.getEAN(), product.getWeight(), product.getTaste(), product.getHeight(), product.getLength(), product.getWidth(), product.getPiecesInPackage(), product.getCategories());
        } else {
            String sql = "UPDATE products p SET name =?, manufacture = ?, EAN = ?, weight = ?, taste = ?, height = ?, length = ?, width = ?, piecesInPackage = ?, Categories = ? WHERE idProduct = ?";
            int pocet = jdbcTemplate.update(sql, product.getName(), product.getManufacture(), product.getEAN(),
                    product.getWeight(), product.getTaste(), product.getHeight(), product.getLength(),
                    product.getWidth(), product.getPiecesInPackage(), product.getCategories().getIdCategories(), product.getIdProduct());
            if (pocet >= 1) {
                return new Product(product.getIdProduct(),product.getName(), product.getManufacture(), product.getEAN(), product.getWeight(), product.getTaste(), product.getHeight(), product.getLength(), product.getWidth(), product.getPiecesInPackage(), product.getCategories());
            } else {
                throw new EntityNotFoundException("product nie existuje");
            }
        }
    }

    @Override
    public Product delete(Long id) {
        Product product = getbyId(id);
        String sql = "DELETE from products where idProduct =" + id;
        int count = jdbcTemplate.update(sql);
        if (count == 1) {
            return product;
        } else {
            throw new EntityNotFoundException("product nie existuje");
        }
    }

    @Override
    public Product getbyId(Long id) {
        try {
            String sql = "SELECT * FROM products where idProduct = " + id;
            return jdbcTemplate.queryForObject(sql, new RowMapper<Product>() {
                @Override
                public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Long id = rs.getLong("idProduct");
                    String name = rs.getString("name");
                    String manufacture = rs.getString("manufacture");
                    String EAN = rs.getString("EAN");
                    Double weight = rs.getDouble("weight");
                    String taste = rs.getString("taste");
                    Double height = rs.getDouble("height");
                    Double length = rs.getDouble("length");
                    Double width = rs.getDouble("width");
                    int piecesInPackage = rs.getInt("piecesInPackage");
                    CategoriesDAO categoriesDAO = DaoFactory.INSTANCE.getcategoriesDAO();
                    Categories cat = categoriesDAO.getbyID(rs.getLong("Categories"));
                    return new Product(id, name, manufacture, EAN, weight, taste, height, length, width, piecesInPackage, cat);
                }
            });
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("product nie existuje");
        }
    }

    @Override
    public List<Product> getbyCategory(Categories categories) {
        System.out.println("SELECT * FROM products where Categories =" + categories.getIdCategories());
        return jdbcTemplate.query("SELECT * FROM products where Categories =" + categories.getIdCategories(), new RowMapper<Product>() {
            @Override
            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("idProduct");
                String name = rs.getString("name");
                String manufacture = rs.getString("manufacture");
                String EAN = rs.getString("EAN");
                Double weight = rs.getDouble("weight");
                String taste = rs.getString("taste");
                Double height = rs.getDouble("height");
                Double length = rs.getDouble("length");
                Double width = rs.getDouble("width");
                int piecesInPackage = rs.getInt("piecesInPackage");
                CategoriesDAO categoriesDAO = DaoFactory.INSTANCE.getcategoriesDAO();
                Categories cat = categoriesDAO.getbyID(rs.getLong("Categories"));
                return new Product(id, name, manufacture, EAN, weight, taste, height, length, width, piecesInPackage, cat);
            }
        });
    }

    @Override
    public Map<Product, Integer> productOnPosiiton(Long position) {
        return jdbcTemplate.query("Select * from prosuctonposition where idPosition = " + position, new ResultSetExtractor<Map<Product, Integer>>() {
            @Override
            public Map<Product, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Product, Integer> productIntegerMap = new HashMap<>();
                while (rs.next()) {
                    Long idProduct = rs.getLong("idProduct");
                    int count = rs.getInt("count");
                    productIntegerMap.put(DaoFactory.INSTANCE.getProductDao().getbyId(idProduct), count);
                }
                return productIntegerMap;
            }
        });
    }

    @Override
    public boolean СapacityСheckProduct(Position position, Product product, int count) {
        double productCapacity = product.getHeight() * product.getWidth() * product.getLength() * count;
        double productCapacityM = product.getWeight() * count;
        double positionCapacity = DaoFactory.INSTANCE.getPositionDao().getСapacityOfPositionV(position.getIdPosiiton());
        double positionCapacityM = DaoFactory.INSTANCE.getPositionDao().getById(position.getIdPosiiton()).getWidth();
        List<Position> positionList = DaoFactory.INSTANCE.getPositionDao().getAll();
        Map<Product, Integer> allProductOnPosition = new HashMap<>();
        for (int i = 0; i < positionList.size(); i++) {
            if (positionList.get(i).getIdPosiiton() == position.getIdPosiiton()) {
                allProductOnPosition = positionList.get(i).getProducts();
                break;
            }
        }
        double fullnestOfPosition = 0;
        double fullnestOfPositionM = 0;
        for (Product product1 : allProductOnPosition.keySet()) {
            fullnestOfPosition += (product1.getHeight() * product1.getWidth() * product1.getLength()) * allProductOnPosition.get(product1);
            fullnestOfPositionM += product1.getWeight() * allProductOnPosition.get(product1);
        }
        if (positionCapacity - fullnestOfPosition >= productCapacity && positionCapacityM - fullnestOfPositionM >= productCapacityM) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void putProductOnPosition(Position position, Product product, int count) {
        if (СapacityСheckProduct(position, product, count)) {
            List<Position> positionList = DaoFactory.INSTANCE.getPositionDao().getAll();
            Map<Product, Integer> allProductOnPosition = new HashMap<>();
            for (int i = 0; i < positionList.size(); i++) {
                if (positionList.get(i).getIdPosiiton() == position.getIdPosiiton()) {
                    allProductOnPosition = positionList.get(i).getProducts();
                    break;
                }
            }
            int countOnPosition = Integer.MIN_VALUE;
            for (Product product1 : allProductOnPosition.keySet()) {
                if (product1.getIdProduct() == product.getIdProduct()) {
                    countOnPosition = allProductOnPosition.get(product1);
                    break;
                }
            }
            if (countOnPosition == Integer.MIN_VALUE) {
                String sql = "INSERT INTO prosuctonposition (`idProduct`, `idPosition`, `count`) VALUES (?,?,?);";
                jdbcTemplate.update(sql, product.getIdProduct(), position.getIdPosiiton(), count);
            } else {
                String sql = "UPDATE prosuctonposition SET count = ? WHERE idProduct = ? ;";
                jdbcTemplate.update(sql, countOnPosition + count, product.getIdProduct());
            }
        } else {
            throw new EntityNotFoundException("produkt nie je mozne vllozit na poziciu");
        }
    }

    @Override
    public void transferProductOnAnotherPosition(Position positionfrom, Product product, Position positionfor, int count) {
        if (СapacityСheckProduct(positionfor, product, count)) {
            List<Position> positionList = DaoFactory.INSTANCE.getPositionDao().getAll();
            Map<Product, Integer> allProductOnPosition = new HashMap<>();
            for (int i = 0; i < positionList.size(); i++) {
                if (positionList.get(i).getIdPosiiton() == positionfor.getIdPosiiton()) {
                    allProductOnPosition = positionList.get(i).getProducts();
                    break;
                }
            }
            int countOnPositionfor = Integer.MIN_VALUE;
            for (Product product1 : allProductOnPosition.keySet()) {
                if (product1.getIdProduct() == product.getIdProduct()) {
                    countOnPositionfor = allProductOnPosition.get(product1);
                    break;
                }
            }
            for (int i = 0; i < positionList.size(); i++) {
                if (positionList.get(i).getIdPosiiton() == positionfrom.getIdPosiiton()) {
                    allProductOnPosition = positionList.get(i).getProducts();
                    break;
                }
            }
            int countOnPositionfrom = Integer.MIN_VALUE;
            for (Product product1 : allProductOnPosition.keySet()) {
                if (product1.getIdProduct() == product.getIdProduct()) {
                    countOnPositionfrom = allProductOnPosition.get(product1);
                    break;
                }
            }
            if(countOnPositionfor == Integer.MIN_VALUE)//insert
            {
                String sql = "INSERT INTO `paz1c_project`.`prosuctonposition` (`idProduct`, `idPosition`, `count`) VALUES (?,?,?);";
                jdbcTemplate.update(sql,product.getIdProduct(),positionfor.getIdPosiiton(),count);
                sql = "UPDATE `paz1c_project`.`prosuctonposition` SET  `count` = ? WHERE idPosition = ? and idProduct = ? ;";
                jdbcTemplate.update(sql,countOnPositionfrom-count,positionfrom.getIdPosiiton(),product.getIdProduct());
            }
            else {//Update
                String sql = "UPDATE `paz1c_project`.`prosuctonposition` SET `count` = ? WHERE idPosition = ? and idProduct = ? ;";
                jdbcTemplate.update(sql,countOnPositionfor+count,positionfor.getIdPosiiton(),product.getIdProduct());
                sql = "UPDATE `paz1c_project`.`prosuctonposition` SET  `count` = ? WHERE idPosition = ? and idProduct = ? ;";
                jdbcTemplate.update(sql,countOnPositionfrom-count,positionfrom.getIdPosiiton(),product.getIdProduct());
            }

        } else {
        throw new EntityNotFoundException("na vybranu poziciu nie je mozne preskldanit product");
        }
    }

}
