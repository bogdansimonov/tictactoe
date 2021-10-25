package tictactoe.com.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tictactoe.com.domain.Cell;
import tictactoe.com.domain.Game;
import tictactoe.com.domain.Move;
import tictactoe.com.domain.Player;
import tictactoe.com.dto.MoveDTO;
import tictactoe.com.dto.PlayerDTO;
import tictactoe.com.enums.Status;
import tictactoe.com.enums.TicToe;
import tictactoe.com.repository.GameRepository;
import tictactoe.com.repository.MoveRepository;
import tictactoe.com.repository.PlayerRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static tictactoe.com.enums.Status.*;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class GameService {

    public static final int BOARD_SIZE = 3;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final MoveRepository moveRepository;


    public Game createGame(PlayerDTO playerDTO) {
        log.info("Create a new game for the player = {}", playerDTO.getUsername());
        Player player = Player.builder().username(playerDTO.getUsername()).build();
        playerRepository.save(player);

        Game game = Game.builder()
                .firstPlayer(player)
                .status(NEW)
                .created(LocalDate.now())
                .build();

        gameRepository.save(game);
        return game;
    }

    public Optional<Game> joinGame(PlayerDTO secondPlayerDTO, Integer gameId) {
        log.info("Player {} is about to join the game = {}", secondPlayerDTO.getUsername(), gameId);
        Game game = gameRepository.findById(gameId).orElse(null);

        if (gameValidation(gameId, game == null, "Game = {} not found")) return Optional.empty();

        if (gameValidation(gameId, game.getSecondPlayer() != null, "Unable to join game = {}")) return Optional.empty();

        Player secondPlayer = Player.builder().username(secondPlayerDTO.getUsername()).build();
        playerRepository.save(secondPlayer);

        game.setSecondPlayer(secondPlayer);
        game.setStatus(IN_PROGRESS);
        gameRepository.save(game);
        return Optional.of(game);
    }

    public Optional<Game> joinToRandomGame(PlayerDTO secondPlayerDTO) {
        log.info("Player = {} is about to join a random game", secondPlayerDTO.getUsername());
        Optional<Game> randomGame = gameRepository.findByStatus(NEW).stream().findAny();
        if (randomGame.isEmpty()) {
            log.error("There is no game to join");
            return Optional.empty();
        }
        Game game = randomGame.get();
        Player secondPlayer = Player.builder().username(secondPlayerDTO.getUsername()).build();
        playerRepository.save(secondPlayer);

        game.setSecondPlayer(secondPlayer);
        game.setStatus(IN_PROGRESS);
        gameRepository.save(game);
        return Optional.of(game);
    }

    public Optional<Game> playerMove(MoveDTO moveDTO) {
        Game game = gameRepository.findById(moveDTO.getGameId()).orElse(null);
        if ((gameValidation(moveDTO.getGameId(), game == null, "Game = {} not found")) ||
                (gameValidation(moveDTO.getGameId(), !game.getStatus().equals(IN_PROGRESS), "Game = {} has not started yet or is already finished")))
            return Optional.empty();

        Player player = playerRepository.findOneByUsername(moveDTO.getPlayer().getUsername());
        if (player == null) {
            log.error("Player = {} not found", moveDTO.getPlayer().getUsername());
            return Optional.empty();
        }

        List<Move> moves = moveRepository.findByGame(game);
        if (isCellTaken(moveDTO, moves)) {
            log.error("The cell is already taken");
            return Optional.empty();
        }

        Move move = getMove(moveDTO, game, player);
        moveRepository.save(move);
        moves.add(move);

        checkWinner(game, moves);
        gameRepository.save(game);

        return Optional.of(game);
    }

    private void checkWinner(Game game, List<Move> moves) {
        if (isWinner(moves, game.getFirstPlayer())) {
            setWinnerAndStatus(game, TicToe.X, X_WON);
        } else if (isWinner(moves, game.getSecondPlayer())) {
            setWinnerAndStatus(game, TicToe.O, Y_WON);
        } else if (moves.size() == BOARD_SIZE * BOARD_SIZE) {
            game.setStatus(TIE);
        }
    }

    private void setWinnerAndStatus(Game game, TicToe ticToe, Status status) {
        game.setWinner(ticToe);
        game.setStatus(status);
    }

    private Move getMove(MoveDTO moveDTO, Game game, Player player) {
        return Move.builder()
                .game(game)
                .rowIndex(moveDTO.getRowIndex())
                .columnIndex(moveDTO.getColumnIndex())
                .player(player)
                .type(moveDTO.getType())
                .build();

    }

    private boolean gameValidation(Integer gameId, boolean condition, String message) {
        if (condition) {
            log.error(message, gameId);
            return true;
        }
        return false;
    }

    private boolean isCellTaken(MoveDTO moveDTO, List<Move> moves) {
        Move move = moves.stream().filter(item -> item.getRowIndex().equals(moveDTO.getRowIndex())
                && item.getColumnIndex().equals(moveDTO.getColumnIndex())).findFirst().orElse(null);
        return move != null && (move.getType().equals(TicToe.X) || move.getType().equals(TicToe.O));
    }

    private List<List<Cell>> getWinningCells() {
        List<List<Cell>> winCells = new ArrayList<>();
        winCells.add(asList(new Cell(0, 0), new Cell(1, 1), new Cell(2, 2)));
        winCells.add(asList(new Cell(2, 0), new Cell(1, 1), new Cell(0, 2)));
        winCells.add(asList(new Cell(0, 0), new Cell(0, 1), new Cell(0, 2)));
        winCells.add(asList(new Cell(1, 0), new Cell(1, 1), new Cell(1, 2)));
        winCells.add(asList(new Cell(2, 0), new Cell(2, 1), new Cell(2, 2)));
        winCells.add(asList(new Cell(0, 0), new Cell(1, 0), new Cell(2, 0)));
        winCells.add(asList(new Cell(0, 1), new Cell(1, 1), new Cell(2, 1)));
        winCells.add(asList(new Cell(0, 2), new Cell(1, 2), new Cell(2, 2)));
        return winCells;
    }

    private boolean isWinner(List<Move> moves, Player player) {
        List<Cell> cells = moves.stream().filter(item -> item.getPlayer().equals(player)).
                map(item -> new Cell(item.getRowIndex(), item.getColumnIndex())).collect(Collectors.toList());
        return getWinningCells().stream().anyMatch(cells::containsAll);
    }


}