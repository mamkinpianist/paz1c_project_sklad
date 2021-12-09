package storage.MysqlDaos;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import storage.DaoFactory;
import storage.EntityNotFoundException;
import storage.constructors.Categories;
import storage.constructors.Roles;
import storage.daos.RoleDao;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlRoleDaoTest {
    private RoleDao roleDao;
    public MysqlRoleDaoTest() {
        DaoFactory.INSTANCE.testing();
        roleDao = DaoFactory.INSTANCE.getRoleDao();
    }

    @Test
    void getAll() {
        List<Roles> rolesList = roleDao.getAll();
        assertTrue(rolesList.size()==4);
        Boolean poleOfRoles[] = new Boolean[4];
        String poleOfRolesStrings[] = {"skladnik","admin","veduci","predajca"};
        for (int i = 0; i < rolesList.size(); i++) {
            String role = rolesList.get(i).getRole();
           switch (role){
                case "skladnik":
                    poleOfRoles[i] = true;
                    break;
                case "admin":
                    poleOfRoles[i] = true;
                    break;
                case "veduci":
                    poleOfRoles[i] = true;
                    break;
                case "predajca":
                    poleOfRoles[i] = true;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + rolesList.get(i));
            }
        }
        for (int i = 0; i < poleOfRoles.length; i++) {
            assertTrue(poleOfRoles[i]);
        }
    }
    @Test
    void getByid() {
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                DaoFactory.INSTANCE.getRoleDao().getByid(-1L);
            }
        });
        List<Roles> rolesList = DaoFactory.INSTANCE.getRoleDao().getAll();
        Roles roles = rolesList.get(2);
        Roles roles1 =  DaoFactory.INSTANCE.getRoleDao().getByid(3L);
        assertEquals(roles, roles1);
    }
}