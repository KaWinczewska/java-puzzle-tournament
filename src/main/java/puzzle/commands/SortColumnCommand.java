package puzzle.commands;

import puzzle.Board;

public final class SortColumnCommand extends AbstractCommand {
  private final int col;
  private boolean[] previous;

  public SortColumnCommand(int col) {
    this.col = col;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(col, size);
  }

  @Override
  protected void apply(Board board) {
    previous = new boolean[board.size()];
    int zeros = 0;
    for (int i = 0; i < board.size(); i++) {
      previous[i] = board.get(i, col);
      if (!previous[i]) zeros++;
    }
    for (int i = 0; i < board.size(); i++) board.set(i, col, i >= zeros);
  }

  @Override
  protected void reverse(Board board) {
    for (int i = 0; i < board.size(); i++) board.set(i, col, previous[i]);
  }
}
