package puzzle.commands;

import puzzle.Board;

public final class SetCellCommand extends AbstractCommand {
  private final int row, col;
  private final boolean value;
  private boolean previous;

  public SetCellCommand(int row, int col, boolean value) {
    this.row = row;
    this.col = col;
    this.value = value;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(row, size);
    Board.checkIndex(col, size);
  }

  @Override
  protected void apply(Board board) {
    previous = board.get(row, col);
    board.set(row, col, value);
  }

  @Override
  protected void reverse(Board board) {
    board.set(row, col, previous);
  }
}
