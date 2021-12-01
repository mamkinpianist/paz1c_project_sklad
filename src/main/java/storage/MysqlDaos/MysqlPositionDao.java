package storage.MysqlDaos;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import storage.DaoFactory;
import storage.EntityNotFoundException;
import storage.constructors.Position;
import storage.constructors.Product;
import storage.daos.PositionDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlPositionDao implements PositionDao {
    private JdbcTemplate jdbcTemplate;

    public MysqlPositionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Position> getAll() {
        String sql = "select positions.id,positions.floor,positions.positionNumber,positions.shelf,positions.height,positions.weight,positions.lenght,positions.BearingCapacity,prosuctonposition.count,products.idProduct,products.name from positions\n" +
                "left outer join prosuctonposition on positions.id = prosuctonposition.idPosition\n" +
                "left outer join products on prosuctonposition.idProduct = products.idProduct;";
        return jdbcTemplate.query(sql, new ResultSetExtractor<List<Position>>() {
            @Override
            public List<Position> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Position> positionList = new ArrayList<>();
                Position position = null;
                while (rs.next()) {
                    Long id = rs.getLong("id");
                    if (position == null || position.getIdPosiiton() != id) {
                        int floor = rs.getInt("floor");
                        int positonNumber = rs.getInt("positionNumber");
                        String shelf = rs.getString("shelf");
                        double height = rs.getDouble("height");
                        double weight = rs.getDouble("weight");
                        double lenght = rs.getDouble("lenght");
                        double BearingCapacity = rs.getDouble("BearingCapacity");
                        position = new Position(id, floor, positonNumber, shelf, height, weight, lenght, BearingCapacity);
                        positionList.add(position);
                    }
                    if (rs.getString("name") == null) {
                        continue;
                    }
                    Long idProduct = rs.getLong("idProduct");
                    int count = rs.getInt("count");
                    position.getProducts().put(DaoFactory.INSTANCE.getProductDao().getbyId(id), count);
                }
                return positionList;
            }
        });
    }

    @Override
    public Position save(Position position) throws EntityNotFoundException {
        Long posiiton1 = position.getIdPosiiton();
        if (posiiton1 == null)//INSERT
        {
            String sql = "INSERT INTO positions(floor,positionNumber,shelf,height,weight,lenght,BearingCapacity) VALUE(?,?,?,?,?,?,?)";
            int pocet = jdbcTemplate.update(sql, position.getFloor(), position.getPositionNumber(), position.getShelf(), position.getHeight(), position.getWeight(), position.getLength(), position.getBearingCapacity());
            if (pocet == 1) {
                return position;
            }
        } else {//update
            String sql = "UPDATE positions SET floor = ?,positionNumber = ?,shelf = ?,height = ?,weight = ?,lenght = ?,BearingCapacity = ? where id = ?";
            int pocet = jdbcTemplate.update(sql, position.getFloor(), position.getPositionNumber(), position.getShelf(), position.getHeight(), position.getWeight(), position.getLength(), position.getBearingCapacity(), position.getIdPosiiton());
            if (pocet >= 1) {
                return position;
            } else {
                throw new EntityNotFoundException("positia nie existuje");

            }
        }
        return position;
    }

    @Override
    public Position delete(Long id) throws EntityNotFoundException {
        Position posToDelete = getById(id);
        String sql = "DELETE from positions where id =" + id;
        int pocet = jdbcTemplate.update(sql);
        return posToDelete;
    }

    @Override
    public Position getById(Long id) throws EntityNotFoundException {
        try {
            String sql = "SELECT * FROM positions where id = " + id;
            return jdbcTemplate.queryForObject(sql, new RowMapper<Position>() {
                @Override
                public Position mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long id = rs.getLong("id");
                    int floor = rs.getInt("floor");
                    int positonNumber = rs.getInt("positionNumber");
                    String shelf = rs.getString("shelf");
                    double height = rs.getDouble("height");
                    double weight = rs.getDouble("weight");
                    double lenght = rs.getDouble("lenght");
                    double BearingCapacity = rs.getDouble("BearingCapacity");
                    return new Position(id, floor, positonNumber, shelf, height, weight, lenght, BearingCapacity);
                }
            });
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("posiitia nie existuje");
        }
    }

    @Override
    public Map<Position, Double> fullnessOfPositionV() {
        List<Position> positionList  = getAll();
        Map<Position, Double> positionDoubleMap = new HashMap<>();
        for (int i = 0; i < getAll().size(); i++) {
            double getСapacityOfPosition = getСapacityOfPositionV(positionList.get(i).getIdPosiiton());
            double VofProducts = 0;
            Map<Product,Integer> productIntegerMap = DaoFactory.INSTANCE.getProductDao().productOnPosiiton(positionList.get(i));
            for (Product product:productIntegerMap.keySet()) {
                double VofProducts1 = product.getHeight()*product.getLength()*product.getWidth();
                int count = productIntegerMap.get(product);
                VofProducts +=VofProducts1*count;
            }
            positionDoubleMap.put(positionList.get(i),100-((VofProducts/getСapacityOfPosition)*100));

        }
        return positionDoubleMap;
    }


    @Override
    public Double getСapacityOfPositionV(Long positionId) {
        Position position = DaoFactory.INSTANCE.getPositionDao().getById(positionId);
        return position.getHeight() * position.getWeight() * position.getLength();
    }

    @Override
    public Long getIdPositionByProduct(Product product, int pocet) {
        String sql = "SELECT * FROM prosuctonposition where idProduct = ? and count>=? limit 1";
        return jdbcTemplate.queryForObject(sql, new RowMapper<Long>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("idPosition");
                return id;
            }
        },product.getIdProduct(),pocet);
    }


}
