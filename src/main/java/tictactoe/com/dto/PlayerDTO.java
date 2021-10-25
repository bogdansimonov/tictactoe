package tictactoe.com.dto;


import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDTO {

    @NotNull
    private String username;
}
