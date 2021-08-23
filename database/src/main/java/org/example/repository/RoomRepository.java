package org.example.repository;

import org.example.models.base.RoomState;
import org.example.models.game.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    Page<Room> findAll(Pageable pageable);

    List<Room> findAll();

    Page<Room> findAllByState(RoomState state, Pageable pageable);
}
