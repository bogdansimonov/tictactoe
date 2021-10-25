package tictactoe.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tictactoe.com.domain.Game;
import tictactoe.com.domain.Move;
import tictactoe.com.domain.Player;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Integer> {
    List<Move> findByGame(Game game);
}
