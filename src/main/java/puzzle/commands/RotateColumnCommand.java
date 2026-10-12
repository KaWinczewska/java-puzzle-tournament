package puzzle.commands;

import puzzle.Board;

public final class RotateColumnCommand extends AbstractCommand {
  private final int col;

  public RotateColumnCommand(int col) {
    this.col = col;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(col, size);
  }

  @Override
  protected void apply(Board board) {
    boolean last = board.get(board.size() - 1, col);
    for (int i = board.size() - 1; i > 0; i--) board.set(i, col, board.get(i - 1, col));
    board.set(0, col, last);
  }

  @Override
  protected void reverse(Board board) {
    boolean first = board.get(0, col);
    for (int i = 0; i < board.size() - 1; i++) board.set(i, col, board.get(i + 1, col));
    board.set(board.size() - 1, col, first);
  }
}
