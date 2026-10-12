package puzzle.strategies;

import puzzle.Referee;

public final class CautiousStrategy extends RandomStrategy {
  private boolean undoNext;

  public CautiousStrategy(long seed) {
    super(seed);
  }

  @Override
  public void prepare(int size) {
    super.prepare(size);
    undoNext = false;
  }

  @Override
  public void takeTurn(Referee referee) {
    if (undoNext && referee.canUndo()) {
      referee.undo();
      undoNext = false;
    } else {
      super.takeTurn(referee);
      undoNext = random.nextDouble() < 0.20;
    }
  }
}
