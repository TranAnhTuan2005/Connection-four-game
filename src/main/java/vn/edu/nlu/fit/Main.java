package vn.edu.nlu.fit;
import vn.edu.nlu.fit.controller.ConnectFourController;
import vn.edu.nlu.fit.model.ConnectFourGame;

import vn.edu.nlu.fit.model.ConnectFourWinChecker;
import vn.edu.nlu.fit.view.ConnectFourView;

public class Main {
    public static void main(String[] args) {
        ConnectFourGame model = new ConnectFourGame(6, 7, new ConnectFourWinChecker(4));

        ConnectFourView view = new ConnectFourView(model.getRows(), model.getCols());

        ConnectFourController controller = new ConnectFourController(model, view);
    }
}