package puzzle.strategies;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;
import puzzle.Referee;
import puzzle.commands.*;

public class RandomStrategy implements PlayerStrategy {
  protected final Random random;
  private final ArrayDeque<CompositeCommand> composites = new ArrayDeque<>();

  public RandomStrategy(long seed) {
    random = new Random(seed);
  }

  @Override
  public void prepare(int size) {
    composites.clear();
    // At most n*n composites can be requested. All are fixed before seeing cells.
    for (int i = 0; i < size * size; i++) {
      int first = random.nextInt(8);
      int second = (first + 1 + random.nextInt(7)) % 8;
      composites.add(new CompositeCommand(size, List.of(basic(first, size), basic(second, size))));
    }
  }

  @Override
  public void takeTurn(Referee referee) {
    int type = random.nextInt(9);
    referee.execute(type == 8 ? composites.remove() : basic(type, referee.size()));
  }

  private Command basic(int type, int size) {
    int row = random.nextInt(size), col = random.nextInt(size);
    switch (type) {
      case 0:
        return new SetCellCommand(row, col, random.nextBoolean());
      case 1:
        return new FlipRowCommand(row);
      case 2:
        return new FlipColumnCommand(col);
      case 3:
        return new SetDiagonalCommand(random.nextBoolean(), random.nextBoolean());
      case 4:
        return new SortRowCommand(row);
      case 5:
        return new SortColumnCommand(col);
      case 6:
        return new RotateRowCommand(row);
      case 7:
        return new RotateColumnCommand(col);
      default:
        throw new IllegalArgumentException("Unknown command type");
    }
  }
}
