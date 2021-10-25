package tictactoe.com.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameException extends Exception {
    private final String message;
}
