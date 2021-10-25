package tictactoe.com.dto;

import lombok.Data;

@Data
public class JoinRequest {

    private Integer gameId;
    private PlayerDTO player;
}
