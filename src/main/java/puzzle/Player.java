package puzzle;

import java.util.Objects;
import puzzle.strategies.PlayerStrategy;

public final class Player {
  private final String id;
  private final PlayerStrategy strategy;

  public Player(String id, PlayerStrategy strategy) {
    if (id == null || id.isBlank())
      throw new IllegalArgumentException("Player needs an identifier");
    this.id = id;
    this.strategy = Objects.requireNonNull(strategy);
  }

  public String id() {
    return id;
  }

  public Referee play(Board initial) {
    strategy.prepare(initial.size());
    Referee referee = new Referee(initial, strategy.completesFullSchedule());
    while (!referee.isFinished()) {
      int before = referee.commandCount();
      strategy.takeTurn(referee);
      if (referee.commandCount() != before + 1) {
        throw new IllegalStateException("A strategy must perform exactly one action per turn");
      }
    }
    return referee;
  }
}
