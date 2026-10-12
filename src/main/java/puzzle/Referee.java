package puzzle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import puzzle.commands.Command;

public final class Referee {
  /** Retains execution and subsequent undo status without exposing a mutable command. */
  public static final class HistoryEntry {
    private final Command command;
    private final int executedAt;
    private int undoneAt;

    private HistoryEntry(Command command, int executedAt) {
      this.command = command;
      this.executedAt = executedAt;
    }

    public String commandType() {
      return command.getClass().getSimpleName();
    }

    public boolean undone() {
      return undoneAt != 0;
    }

    public int executedAt() {
      return executedAt;
    }

    public int undoneAt() {
      return undoneAt;
    }
  }

  private final Board board;
  private final int limit;
  private final boolean fullSchedule;
  private int count;
  private final List<HistoryEntry> history = new ArrayList<>();
  private final ArrayDeque<HistoryEntry> active = new ArrayDeque<>();

  public Referee(Board initial) {
    this(initial, false);
  }

  // Only the systematic player continues after solving, to perform exactly n*n sets.
  Referee(Board initial, boolean fullSchedule) {
    board = initial.copy();
    limit = board.size() * board.size();
    this.fullSchedule = fullSchedule;
  }

  public int size() {
    return board.size();
  }

  public boolean cell(int row, int col) {
    return board.get(row, col);
  }

  public boolean isSolved() {
    return board.isSolved();
  }

  public int commandCount() {
    return count;
  }

  public int limit() {
    return limit;
  }

  public boolean hasBudget() {
    return count < limit;
  }

  public boolean isFinished() {
    return !hasBudget() || (!fullSchedule && isSolved());
  }

  public boolean canUndo() {
    return !active.isEmpty();
  }

  public List<HistoryEntry> history() {
    return List.copyOf(history);
  }

  public Board boardCopy() {
    return board.copy();
  }

  public int score() {
    return isSolved() ? count : limit + 1;
  }

  public void execute(Command command) {
    requireActiveGame();
    Objects.requireNonNull(command).execute(board);
    count++;
    HistoryEntry entry = new HistoryEntry(command, count);
    history.add(entry);
    active.push(entry);
  }

  public void undo() {
    requireActiveGame();
    if (!canUndo()) throw new IllegalStateException("Nothing to undo");
    active.peek().command.undo(board);
    count++;
    active.pop().undoneAt = count;
  }

  private void requireActiveGame() {
    if (isFinished()) throw new IllegalStateException("Game has finished");
  }
}
