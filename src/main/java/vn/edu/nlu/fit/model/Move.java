package vn.edu.nlu.fit.model;

import lombok.Getter;

@Getter
public class  Move {
    private final int row;
    private final int col;
    private final Player player;

    public Move(int row, int col, Player player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    @Override
    public String toString() {
        return player.getName() + " đánh tại (" + row + ", " + col + ")";
    }
}
