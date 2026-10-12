package puzzle.commands;

import puzzle.Board;

public final class FlipRowCommand extends AbstractCommand {
  private final int row;

  public FlipRowCommand(int row) {
    this.row = row;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(row, size);
  }

  @Override
  protected void apply(Board board) {
    for (int i = 0; i < board.size(); i++) board.set(row, i, !board.get(row, i));
  }

  @Override
  protected void reverse(Board board) {
    apply(board);
  }
}
