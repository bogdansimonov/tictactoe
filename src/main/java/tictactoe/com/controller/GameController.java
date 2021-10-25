package tictactoe.com.controller;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tictactoe.com.domain.Game;
import tictactoe.com.domain.Move;
import tictactoe.com.dto.JoinRequest;
import tictactoe.com.dto.MoveDTO;
import tictactoe.com.dto.PlayerDTO;
import tictactoe.com.exception.GameException;
import tictactoe.com.service.GameService;

import java.util.Optional;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/tictactoe")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @ApiOperation(value = "Create a new game", response = Game.class)
    @PostMapping("/create")
    public ResponseEntity<Game> create(@RequestBody PlayerDTO playerDTO) {
        return ResponseEntity.ok(gameService.createGame(playerDTO));
    }

    @ApiOperation(value = "Join the game", response = Game.class)
    @PostMapping("/join")
    public ResponseEntity<Game> join(@RequestBody JoinRequest request) throws GameException {
        return gameService.joinGame(request.getPlayer(), request.getGameId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Join a random game ", response = Game.class)
    @PostMapping("/join/random")
    public ResponseEntity<Game> joinRandom(@RequestBody PlayerDTO playerDTO) {
        return gameService.joinToRandomGame(playerDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Send date about the move", response = Game.class)
    @PostMapping("/playermove")
    public ResponseEntity<Game> playerMove(@RequestBody MoveDTO moveDTO) {
        Optional<Game> gameOptional = gameService.playerMove(moveDTO);
        if (gameOptional.isPresent()) {
            Game game = gameOptional.get();
            simpMessagingTemplate.convertAndSend("/topic/move/" + game.getId(), game);
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.notFound().build();

    }
}