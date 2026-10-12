package puzzle.commands;

import puzzle.Board;

public final class FlipColumnCommand extends AbstractCommand {
  private final int col;

  public FlipColumnCommand(int col) {
    this.col = col;
  }

  @Override
  public void validate(int size) {
    Board.checkIndex(col, size);
  }

  @Override
  protected void apply(Board board) {
    for (int i = 0; i < board.size(); i++) board.set(i, col, !board.get(i, col));
  }

  @Override
  protected void reverse(Board board) {
    apply(board);
  }
}
