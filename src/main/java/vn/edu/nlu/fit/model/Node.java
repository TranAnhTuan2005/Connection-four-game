package vn.edu.nlu.fit.model;

import java.util.List;

public class Node {
        private int row, col;
        private Board state;
        private List<Node> children;
        private int score;

        public Node(int row, int col, Board state, List<Node> children, int score) {
            this.row = row;
            this.col = col;
            this.state = state;
            this.children = children;
            this.score = score;
        }
}
