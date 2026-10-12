package puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import puzzle.strategies.*;

class TournamentTest {
  private static class ObservingStrategy implements PlayerStrategy {
    private final List<String> boards = new ArrayList<>();
    private final GreedyStrategy delegate = new GreedyStrategy();
    private boolean firstTurn;

    public void prepare(int size) {
      delegate.prepare(size);
      firstTurn = true;
    }

    public void takeTurn(Referee referee) {
      if (firstTurn) {
        boards.add(referee.boardCopy().toString());
        firstTurn = false;
      }
      delegate.takeTurn(referee);
    }
  }

  @Test
  void sharedStartingBoardsTiesByesAndWinner() {
    Tournament tournament = new Tournament(42);
    List<ObservingStrategy> strategies = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      ObservingStrategy strategy = new ObservingStrategy();
      strategies.add(strategy);
      tournament.register(new Player("p" + i, strategy));
    }
    Tournament.Result result = tournament.run();
    assertEquals("p0", result.winner().id());
    assertEquals(3, result.rounds().size());
    Tournament.Round first = result.rounds().get(0);
    assertEquals("p4", first.bye());
    assertEquals(2, first.matches().size());
    assertEquals("p0", first.matches().get(0).first());
    assertEquals("p1", first.matches().get(0).second());
    assertEquals("p2", first.matches().get(1).first());
    assertEquals("p3", first.matches().get(1).second());
    for (int i = 1; i < 4; i++)
      assertEquals(strategies.get(0).boards.get(0), strategies.get(i).boards.get(0));
    for (Tournament.Round round : result.rounds()) {
      assertTrue(round.boardSize() >= 10 && round.boardSize() <= 100);
      for (Tournament.Match match : round.matches()) {
        assertEquals(match.firstScore(), match.secondScore());
        assertEquals(match.first(), match.winner());
      }
    }
    assertEquals("p4", result.rounds().get(1).bye());
  }

  @Test
  void lowerScoreAdvances() {
    Tournament tournament = new Tournament(5);
    tournament.register(new Player("systematic", new SystematicStrategy()));
    tournament.register(new Player("greedy", new GreedyStrategy()));
    Tournament.Result result = tournament.run();
    assertEquals("greedy", result.winner().id());
    assertTrue(
        result.rounds().get(0).matches().get(0).secondScore()
            < result.rounds().get(0).matches().get(0).firstScore());
  }

  @Test
  void emptySingleAndDuplicateRegistration() {
    Tournament tournament = new Tournament(42);
    assertThrows(IllegalStateException.class, tournament::run);
    Player player = new Player("only", new GreedyStrategy());
    tournament.register(player);
    assertThrows(
        IllegalArgumentException.class,
        () -> tournament.register(new Player("only", new SystematicStrategy())));
    assertSame(player, tournament.run().winner());
    assertTrue(tournament.run().rounds().isEmpty());
    assertThrows(IllegalArgumentException.class, () -> new Player(" ", new GreedyStrategy()));
  }

  @Test
  void tournamentSeedReproducesResults() {
    Tournament first = new Tournament(7), second = new Tournament(7);
    for (String id : List.of("a", "b", "c")) {
      first.register(new Player(id, new GreedyStrategy()));
      second.register(new Player(id, new GreedyStrategy()));
    }
    assertEquals(first.run().rounds(), second.run().rounds());
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 5, 8})
  void bracketOrderAndSharedBoardsInEveryRound(int count) {
    Tournament tournament = new Tournament(42);
    List<ObservingStrategy> strategies = new ArrayList<>();
    List<String> remaining = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      ObservingStrategy strategy = new ObservingStrategy();
      strategies.add(strategy);
      remaining.add("p" + i);
      tournament.register(new Player("p" + i, strategy));
    }
    Tournament.Result result = tournament.run();
    int[] gamesSeen = new int[count];
    int matches = 0;
    for (Tournament.Round round : result.rounds()) {
      List<String> winners = new ArrayList<>();
      String sharedBoard = null;
      for (int i = 0; i < round.matches().size(); i++) {
        Tournament.Match match = round.matches().get(i);
        assertEquals(remaining.get(2 * i), match.first());
        assertEquals(remaining.get(2 * i + 1), match.second());
        assertEquals(match.first(), match.winner());
        for (String id : List.of(match.first(), match.second())) {
          int index = Integer.parseInt(id.substring(1));
          String board = strategies.get(index).boards.get(gamesSeen[index]++);
          if (sharedBoard == null) sharedBoard = board;
          assertEquals(sharedBoard, board);
        }
        winners.add(match.winner());
        matches++;
      }
      if (remaining.size() % 2 == 1) {
        assertEquals(remaining.get(remaining.size() - 1), round.bye());
        winners.add(round.bye());
      } else {
        assertNull(round.bye());
      }
      remaining = winners;
    }
    assertEquals(count - 1, matches);
    assertEquals(List.of("p0"), remaining);
    assertEquals("p0", result.winner().id());
  }
}
