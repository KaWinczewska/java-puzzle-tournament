package puzzle.commands;

import puzzle.Board;

public final class RotateRowCommand extends AbstractCommand {
  private final int row;

  public RotateRowCommand(int row) {
    this.row = row;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(row, size);
  }

  @Override
  protected void apply(Board board) {
    boolean last = board.get(row, board.size() - 1);
    for (int i = board.size() - 1; i > 0; i--) board.set(row, i, board.get(row, i - 1));
    board.set(row, 0, last);
  }

  @Override
  protected void reverse(Board board) {
    boolean first = board.get(row, 0);
    for (int i = 0; i < board.size() - 1; i++) board.set(row, i, board.get(row, i + 1));
    board.set(row, board.size() - 1, first);
  }
}
