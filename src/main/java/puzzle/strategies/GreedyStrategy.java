package puzzle.strategies;

import puzzle.Referee;
import puzzle.commands.SetCellCommand;

public final class GreedyStrategy implements PlayerStrategy {
  private int next;

  @Override
  public void prepare(int size) {
    next = 0;
  }

  @Override
  public void takeTurn(Referee referee) {
    int size = referee.size();
    while (next < size * size) {
      int row = next / size, col = next % size;
      next++;
      if (!referee.cell(row, col)) {
        referee.execute(new SetCellCommand(row, col, true));
        return;
      }
    }
  }
}
