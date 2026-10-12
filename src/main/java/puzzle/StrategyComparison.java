package puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import puzzle.strategies.*;

/** Small descriptive experiment, not a microbenchmark. */
public final class StrategyComparison {
  private StrategyComparison() {}

  public static void run(long seed, int boardCount, int size) {
    if (boardCount <= 0) throw new IllegalArgumentException("Board count must be positive");
    Random random = new Random(seed);
    List<Board> boards = new ArrayList<>();
    for (int i = 0; i < boardCount; i++) boards.add(Board.random(size, random));
    List<Player> players =
        List.of(
            new Player("Random", new RandomStrategy(seed)),
            new Player("Cautious", new CautiousStrategy(seed)),
            new Player("Systematic", new SystematicStrategy()),
            new Player("Greedy", new GreedyStrategy()),
            new Player("Advanced", new AdvancedStrategy()));
    System.out.printf("Comparison: seed=%d, boards=%d, size=%d%n", seed, boardCount, size);
    System.out.println("Strategy     Average score   Solved     Average ms");
    for (Player player : players) {
      long totalScore = 0, elapsed = 0;
      int solved = 0;
      for (Board board : boards) {
        long start = System.nanoTime();
        Referee result = player.play(board);
        elapsed += System.nanoTime() - start;
        totalScore += result.score();
        if (result.isSolved()) solved++;
      }
      System.out.printf(
          java.util.Locale.ROOT,
          "%-12s %12.2f %8.1f%% %12.3f%n",
          player.id(),
          (double) totalScore / boardCount,
          100.0 * solved / boardCount,
          elapsed / 1_000_000.0 / boardCount);
    }
  }
}
