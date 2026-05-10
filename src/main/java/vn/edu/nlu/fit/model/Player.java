package vn.edu.nlu.fit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.awt.*;

@Getter
@Setter
@EqualsAndHashCode
public abstract class Player {
    protected final int id;
    protected final String name;
    protected final Color color;

    public Player(int id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public abstract boolean isAI();

}