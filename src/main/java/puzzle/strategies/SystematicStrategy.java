package puzzle.strategies;

import puzzle.Referee;
import puzzle.commands.SetCellCommand;

public final class SystematicStrategy implements PlayerStrategy {
  private int next;

  @Override
  public void prepare(int size) {
    next = 0;
  }

  @Override
  public boolean completesFullSchedule() {
    return true;
  }

  @Override
  public void takeTurn(Referee referee) {
    int row = next / referee.size(), col = next % referee.size();
    referee.execute(new SetCellCommand(row, col, true));
    next++;
  }
}
