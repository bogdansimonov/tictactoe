package tictactoe.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tictactoe.com.domain.Game;
import tictactoe.com.enums.Status;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
    List<Game> findByStatus(Status status);
}
