package storage.daos;

import storage.EntityNotFoundException;
import storage.constructors.Position;
import storage.constructors.Product;

import java.util.List;
import java.util.Map;

public interface PositionDao {
    List<Position> getAll();
    Position save(Position position) throws EntityNotFoundException;
    Position delete(Long id ) throws EntityNotFoundException;
    Position getById(Long id) throws EntityNotFoundException;
    Map<Position,Double> fullnessOfPositionV();
    Double getСapacityOfPositionV(Long positionId);
    Long getIdPositionByProduct(Product product,int pocet);


}
