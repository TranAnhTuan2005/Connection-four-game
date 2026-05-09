package vn.edu.nlu.fit.heuristic;

import vn.edu.nlu.fit.model.Board;

public interface Heuristic {
    int evaluate(Board state);
}
