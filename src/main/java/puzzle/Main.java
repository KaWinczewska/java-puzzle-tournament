package puzzle;

import puzzle.strategies.*;

public final class Main {
  private Main() {}

  public static void main(String[] args) {
    if (args.length > 2
        || (args.length > 0 && !args[0].equals("tournament") && !args[0].equals("compare"))) {
      throw new IllegalArgumentException("Usage: java -jar <jar> [tournament|compare] [seed]");
    }
    long seed = args.length > 1 ? Long.parseLong(args[1]) : 42;
    if (args.length > 0 && args[0].equals("compare")) {
      StrategyComparison.run(seed, 20, 10);
      return;
    }
    Tournament tournament = new Tournament(seed);
    tournament.register(new Player("Random", new RandomStrategy(seed + 1)));
    tournament.register(new Player("Cautious", new CautiousStrategy(seed + 2)));
    tournament.register(new Player("Systematic", new SystematicStrategy()));
    tournament.register(new Player("Greedy", new GreedyStrategy()));
    tournament.register(new Player("Advanced", new AdvancedStrategy()));
    Tournament.Result result = tournament.run();
    System.out.println("Tournament seed: " + seed);
    for (Tournament.Round round : result.rounds()) {
      System.out.printf(
          "Round %d — board %dx%d%n", round.number(), round.boardSize(), round.boardSize());
      for (Tournament.Match match : round.matches()) {
        System.out.printf(
            "  %s (%d) vs %s (%d) -> %s%n",
            match.first(), match.firstScore(), match.second(), match.secondScore(), match.winner());
      }
      if (round.bye() != null) System.out.println("  Bye: " + round.bye());
    }
    System.out.println("Winner: " + result.winner().id());
  }
}
