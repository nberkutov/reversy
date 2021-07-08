package models;

import services.MoveService;
import exception.GameException;
import models.base.PlayerColor;

public abstract class Player {
    protected final long id;
    protected final PlayerColor color;
    protected final MoveService moveService;

    public Player(long id, PlayerColor color, MoveService moveService) {
        this.id = id;
        this.color = color;
        this.moveService = moveService;
    }

    public PlayerColor getColor() {
        return color;
    }

    public long getId() {
        return id;
    }

    public abstract void nextMove() throws GameException;
}
