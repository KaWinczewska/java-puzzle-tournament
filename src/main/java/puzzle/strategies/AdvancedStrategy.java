package puzzle.strategies;

import puzzle.Referee;
import puzzle.commands.FlipColumnCommand;
import puzzle.commands.SortRowCommand;

public final class AdvancedStrategy implements PlayerStrategy {
  private int row, column;
  private final GreedyStrategy finish = new GreedyStrategy();

  @Override
  public void prepare(int size) {
    row = 0;
    column = 0;
    finish.prepare(size);
  }

  @Override
  public void takeTurn(Referee referee) {
    int size = referee.size();
    if (row < size) {
      referee.execute(new SortRowCommand(row++));
      return;
    }
    while (column < size) {
      int col = column++, zeros = 0;
      for (int r = 0; r < size; r++) if (!referee.cell(r, col)) zeros++;
      if (zeros > size / 2) {
        referee.execute(new FlipColumnCommand(col));
        return;
      }
    }
    finish.takeTurn(referee);
  }
}
