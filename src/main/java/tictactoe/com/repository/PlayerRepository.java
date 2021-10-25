package tictactoe.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tictactoe.com.domain.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findOneByUsername(String username);
}
