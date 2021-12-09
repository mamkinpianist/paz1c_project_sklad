package storage.MysqlDaos;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import storage.DaoFactory;
import storage.EntityNotFoundException;
import storage.constructors.User;
import storage.daos.RoleDao;
import storage.daos.UserDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class MysqlUserDaoTest {
    UserDao userDao;
    RoleDao roleDao;
    User testUser;
    boolean deletable = true;

    public MysqlUserDaoTest() {
        DaoFactory.INSTANCE.testing();
        userDao = DaoFactory.INSTANCE.getUserDao();
        roleDao = DaoFactory.INSTANCE.getRoleDao();
    }

    @BeforeEach
    void setUp() {
        deletable = true;
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date1 = formatter.format(date);
        try {
            date = formatter.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        testUser = new User("test", "User", date, "testlogin111", "testpass", roleDao.getAll().get(0));

    }

    @AfterEach
    void tearDown() {
        if (testUser != null && deletable == true)
            userDao.delete(testUser.getIdUser());
    }

    @Test
    void getAll() {
        List<User> userList = userDao.getAll();
        assertTrue(userList.size() >= 0);
        assertNotNull(userList);
        for (int i = 0; i < userList.size(); i++) {
            assertNotNull(userList.get(i));
        }
        deletable = false;
    }

    @Test
    void save() {
        List<User> userList = userDao.getAll();
        User savedUser = userDao.save(testUser);
        testUser.setIdUser(savedUser.getIdUser());
        assertEquals(testUser, savedUser);
        assertEquals(userList.size() + 1, userDao.getAll().size());
        savedUser.setName("testuuuu");
        User savedUser1 = userDao.save(savedUser);
        assertEquals(savedUser1, savedUser);
    }

    @Test
    void delete() {
        List<User> userList = userDao.getAll();
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDao.delete((long) (userList.size() + 1));
            }
        });
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDao.delete(-1L);
            }
        });
        User save = userDao.save(testUser);
        User delete = userDao.delete(save.getIdUser());
        assertEquals(save, delete);
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDao.delete(delete.getIdUser());
            }
        });
        deletable = false;
    }

    @Test
    void getByid() {
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDao.getByid(-1L);
            }
        });
        List<User> userList = userDao.getAll();
        assertThrows(EntityNotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                userDao.getByid((long) (userList.size() + 1));
            }
        });
        User saveduser = userDao.save(testUser);
        testUser.setIdUser(saveduser.getIdUser());
        User getbyID = userDao.getByid(saveduser.getIdUser());
        User deleteuser = userDao.delete(saveduser.getIdUser());
        assertEquals(getbyID, deleteuser);
        assertEquals(getbyID, saveduser);
        deletable = false;
    }

    @Test
    void getBySubstring() {
        User save = userDao.save(testUser);
        testUser.setIdUser(save.getIdUser());
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String date1 = formatter.format(date);
        try {
            date = formatter.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        User save2 = new User("test", "User", date, "testlogin1111", "testpass", roleDao.getAll().get(0));
        save2 = userDao.save(save2);
        List<User> userList = new ArrayList<>();
        userList.add(save);
        userList.add(save2);
        int size = 4;
        int a = 0;
        if (save.getName().length() <= save.getSurname().length()) {
            while (a == 0&&a!=save.getName().length()) {
                a = (int) (Math.random() * save.getName().length()-1);
            }
        } else {
            while (a == 0&&a!=save.getSurname().length()) {
                a = (int) (Math.random() * save.getSurname().length()-1);
            }
        }
        String nameSubstring = "";
        String surnameSubstring = "";
        nameSubstring += save.getName().charAt(a) + "" + save.getName().charAt(a + 1);
        surnameSubstring += save.getSurname().charAt(a) + "" + save.getSurname().charAt(a + 1);
        List<User> userList1 = userDao.getBySubstring(nameSubstring, surnameSubstring);
        assertEquals(userList.size(),userList1.size());
        for (int i = 0; i < userList1.size(); i++) {
            assertTrue(userList.contains(userList1.get(i)));
        }
        userDao.delete(save2.getIdUser());
    }
}