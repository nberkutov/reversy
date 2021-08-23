package org.example.repository;

import org.example.models.board.ArrayBoard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends CrudRepository<ArrayBoard, Long> {
}
