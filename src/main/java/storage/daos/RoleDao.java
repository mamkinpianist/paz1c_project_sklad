package storage.daos;

import storage.EntityNotFoundException;
import storage.constructors.Roles;

import java.util.List;

public interface RoleDao {
    List<Roles> getAll();
    Roles saveUpdate(Roles roles) throws EntityNotFoundException;
    Roles getByid(Long id)throws EntityNotFoundException;
}
