package tictactoe.com.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tictactoe.com.enums.Status;
import tictactoe.com.enums.TicToe;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "first_player_id", nullable = false)
    Player firstPlayer;

    @ManyToOne
    @JoinColumn(name = "second_player_id", nullable = true)
    Player secondPlayer;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created", nullable = false)
    private LocalDate created;

    @Enumerated(EnumType.STRING)
    private TicToe winner;
}
