package storage.MysqlDaos;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import storage.EntityNotFoundException;
import storage.constructors.Roles;
import storage.daos.RoleDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MysqlRoleDao implements RoleDao {

    private JdbcTemplate jdbcTemplate;

    public MysqlRoleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Roles> getAll() {
        return jdbcTemplate.query("SELECT idRoles,role FROM roles", new RowMapper<Roles>() {
            public Roles mapRow(ResultSet rs, int rowNum) throws SQLException {
                long id = rs.getLong("idRoles");
                String role = rs.getString("role");
                return new Roles(id, role);
            }
        });
    }

    @Override
    public Roles getByid(Long id) throws EntityNotFoundException {
        try {
            String sql = "SELECT * FROM roles where idRoles = " + id;
            return jdbcTemplate.queryForObject(sql, new RowMapper<Roles>() {
                @Override
                public Roles mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Long id = rs.getLong("idRoles");
                    String name = rs.getString("role");
                    return new Roles(id,name);
                }
            });
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("role nie existuje");
        }
    }

}
