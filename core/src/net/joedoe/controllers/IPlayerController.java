package net.joedoe.controllers;

public interface IPlayerController {

    void move(int direction);

    void attack(int direction);

    void changeWeapon(int choice);

    void reload();

    void endTurn();

    boolean isTurnOver();
}
