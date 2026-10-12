package puzzle.strategies;

import puzzle.Referee;

public interface PlayerStrategy {
  /** Called with the size only, before any cell contents are revealed. */
  void prepare(int size);

  /** Performs one ordinary command or undo through the referee. */
  void takeTurn(Referee referee);

  default boolean completesFullSchedule() {
    return false;
  }
}
