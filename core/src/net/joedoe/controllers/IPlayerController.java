package net.joedoe.controllers;

import net.joedoe.utils.Direction;

public interface IPlayerController {

    void move(Direction direction);

    void attack(Direction direction);

    void changeWeapon(int choice);

    void reload();

    void endTurn();

    boolean isTurnOver();
}
