package vn.edu.nlu.fit.model;

import java.awt.*;

public class HumanPlayer extends Player{
    public HumanPlayer(int id, String name, Color color) {
        super(id, name, color);
    }

    @Override
    public boolean isAI() {
        return false;
    }
}
