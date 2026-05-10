package vn.edu.nlu.fit.enums;
public enum GameMode {
    PVP ("Người vs Người"),
    PVE ("Người vs Máy");

    private final String label;

    GameMode(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
