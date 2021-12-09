package storage.MysqlDaos;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import storage.constructors.Categories;
import storage.EntityNotFoundException;
import storage.constructors.Order;
import storage.daos.CategoriesDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MysqlCategoriesDAO  implements CategoriesDAO {
    private JdbcTemplate jdbcTemplate;

    public MysqlCategoriesDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Categories> getAll() {
        String sql = "SELECT * FROM categories order by idCategories asc;";
        return jdbcTemplate.query(sql, new RowMapper<Categories>() {
            @Override
            public Categories mapRow(ResultSet rs, int rowNum) throws SQLException {
                Long id = rs.getLong("idCategories");
                String category = rs.getString("categoria");
                return new Categories(id,category);
            }
        });
    }

    @Override
    public Categories save(Categories categories) {
        if (categories.getIdCategories() == null)//INSERT
        {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName("`categories`");
            insert.usingGeneratedKeyColumns("idCategories");
            insert.usingColumns("categoria");
            MapSqlParameterSource namedParameters = new MapSqlParameterSource();
            namedParameters.addValue("categoria",categories.getCategoria());
            try {
                Long Id = insert.executeAndReturnKey(namedParameters).longValue();
                    return new Categories(Id,categories.getCategoria());
            } catch (DuplicateKeyException e) {
               throw new EntityNotFoundException("categoria uz existuje");
            }
        } else {//update
            String sql = "UPDATE categories SET categoria = ? where idCategories = ?";
            int pocet = jdbcTemplate.update(sql, categories.getCategoria(),categories.getIdCategories());
            if (pocet >= 1) {
                return new Categories(categories.getIdCategories(),categories.getCategoria());
            } else {
                throw new EntityNotFoundException("categoria nie existuje");
            }
        }
    }

    @Override
    public Categories delete(Long id) {
        Categories catToDelete = getbyID(id);
        try {
            String sql = "DELETE from categories where idCategories =" + id;
            jdbcTemplate.update(sql);
        } catch (DataIntegrityViolationException e) {
             throw new EntityNotFoundException("nie je mozne zmazat categoriu");
        }
        return catToDelete;
    }

    @Override
    public Categories getbyID(Long id) throws EntityNotFoundException {
        try {
            String sql = "SELECT * FROM categories where idCategories = " + id;
            return jdbcTemplate.queryForObject(sql, new RowMapper<Categories>() {
                @Override
                public Categories mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Long id = rs.getLong("idCategories");
                    String category = rs.getString("categoria");
                    return new Categories(id,category);
                }
            });
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("categoria nie existuje");
        }
    }
}
