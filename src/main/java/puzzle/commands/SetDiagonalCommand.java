package puzzle.commands;

import puzzle.Board;

public final class SetDiagonalCommand extends AbstractCommand {
  private final boolean mainDiagonal, value;
  private boolean[] previous;

  public SetDiagonalCommand(boolean mainDiagonal, boolean value) {
    this.mainDiagonal = mainDiagonal;
    this.value = value;
  }

  private int column(int row, int size) {
    return mainDiagonal ? row : size - 1 - row;
  }

  @Override
  public void validate(int size) {}

  @Override
  protected void apply(Board board) {
    previous = new boolean[board.size()];
    for (int row = 0; row < board.size(); row++) {
      int col = column(row, board.size());
      previous[row] = board.get(row, col);
      board.set(row, col, value);
    }
  }

  @Override
  protected void reverse(Board board) {
    for (int row = 0; row < board.size(); row++)
      board.set(row, column(row, board.size()), previous[row]);
  }
}
