package puzzle.commands;

import puzzle.Board;

public final class SortRowCommand extends AbstractCommand {
  private final int row;
  private boolean[] previous;

  public SortRowCommand(int row) {
    this.row = row;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(row, size);
  }

  @Override
  protected void apply(Board board) {
    previous = new boolean[board.size()];
    int zeros = 0;
    for (int i = 0; i < board.size(); i++) {
      previous[i] = board.get(row, i);
      if (!previous[i]) zeros++;
    }
    for (int i = 0; i < board.size(); i++) board.set(row, i, i >= zeros);
  }

  @Override
  protected void reverse(Board board) {
    for (int i = 0; i < board.size(); i++) board.set(row, i, previous[i]);
  }
}
