package org.example.models.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.models.board.Point;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "move")
public class Move implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @Enumerated(EnumType.STRING)
    private PlayerColor color;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "move_point_id", referencedColumnName = "id")
    private Point point;

    public Move(final PlayerColor color, final Point point) {
        this.color = color;
        this.point = point;
    }

    public static Move create(final PlayerColor color, final Point point) {
        return new Move(color, point);
    }
}
