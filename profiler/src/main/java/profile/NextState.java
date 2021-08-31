package profile;

public class NextState {
    private final String board;
    private int frequency;

    public NextState(final String board, final int frequency) {
        this.board = board;
        this.frequency = frequency;
    }

    public String getBoard() {
        return board;
    }

    public int getFrequency() {
        return frequency;
    }

    public void incFrequency() {
        frequency++;
    }
}
