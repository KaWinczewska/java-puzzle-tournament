package puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class Tournament {
  public record Match(
      int round, String first, String second, int firstScore, int secondScore, String winner) {}

  public record Round(int number, int boardSize, List<Match> matches, String bye) {}

  public record Result(Player winner, List<Round> rounds) {}

  private final List<Player> players = new ArrayList<>();
  private final Random random;

  public Tournament(long seed) {
    random = new Random(seed);
  }

  public void register(Player player) {
    Objects.requireNonNull(player);
    for (Player existing : players)
      if (existing.id().equals(player.id())) {
        throw new IllegalArgumentException("Duplicate player identifier");
      }
    players.add(player);
  }

  public Result run() {
    if (players.isEmpty()) throw new IllegalStateException("Register at least one player");
    List<Player> remaining = new ArrayList<>(players);
    List<Round> rounds = new ArrayList<>();
    int number = 1;
    while (remaining.size() > 1) {
      Board initial = Board.random(10 + random.nextInt(91), random);
      List<Player> winners = new ArrayList<>();
      List<Match> matches = new ArrayList<>();
      for (int i = 0; i + 1 < remaining.size(); i += 2) {
        Player first = remaining.get(i), second = remaining.get(i + 1);
        int a = first.play(initial).score(), b = second.play(initial).score();
        Player winner = a <= b ? first : second;
        winners.add(winner);
        matches.add(new Match(number, first.id(), second.id(), a, b, winner.id()));
      }
      String bye = null;
      if (remaining.size() % 2 != 0) {
        Player last = remaining.get(remaining.size() - 1);
        winners.add(last);
        bye = last.id();
      }
      rounds.add(new Round(number++, initial.size(), List.copyOf(matches), bye));
      remaining = winners;
    }
    return new Result(remaining.get(0), List.copyOf(rounds));
  }
}
