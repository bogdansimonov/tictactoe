package tictactoe.com.dto;


import lombok.Getter;
import lombok.Setter;
import tictactoe.com.enums.TicToe;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class MoveDTO {

    @NotNull
    private Integer gameId;

    @NotNull
    private Integer rowIndex;

    @NotNull
    private Integer columnIndex;

    @NotNull
    private PlayerDTO player;

    @NotNull
    private TicToe type;
}
