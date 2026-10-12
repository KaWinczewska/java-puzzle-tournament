package puzzle.commands;

import puzzle.Board;

/** A command instance belongs to one execution and can be undone once. */
public abstract class AbstractCommand implements Command {
  private Board executedOn;
  private boolean undone;

  @Override
  public final void execute(Board board) {
    requireFresh();
    validate(board.size());
    apply(board);
    executedOn = board;
  }

  @Override
  public final void undo(Board board) {
    if (executedOn != board || undone)
      throw new IllegalStateException("Command is not undoable on this board");
    reverse(board);
    undone = true;
  }

  final void requireFresh() {
    if (executedOn != null) throw new IllegalStateException("Use a fresh command instance");
  }

  protected abstract void apply(Board board);

  protected abstract void reverse(Board board);
}
