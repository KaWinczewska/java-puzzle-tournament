package puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import puzzle.strategies.*;

class StrategyTest {
  @Test
  void systematicAlwaysUsesFullSchedule() {
    Player player = new Player("systematic", new SystematicStrategy());
    assertEquals(9, player.play(new Board(3)).score());
    assertEquals(1, player.play(new Board(new boolean[][] {{true}})).score());
    assertEquals(9, player.play(CommandTest.initial()).score());
  }

  @Test
  void greedyCountsOnlyZerosAndResets() {
    Player player = new Player("greedy", new GreedyStrategy());
    assertEquals(5, player.play(CommandTest.initial()).score());
    assertEquals(5, player.play(CommandTest.initial()).score());
    assertEquals(0, player.play(new Board(new boolean[][] {{true}})).score());
    assertEquals(9, player.play(new Board(3)).score());
  }

  @Test
  void advancedPhasesAndStrictMajority() {
    AdvancedStrategy strategy = new AdvancedStrategy();
    strategy.prepare(3);
    Referee referee = new Referee(CommandTest.initial());
    for (int i = 0; i < 3; i++) strategy.takeTurn(referee);
    assertEquals(CommandTest.text("0 1 1/0 0 1/0 0 1"), referee.boardCopy().toString());
    strategy.takeTurn(referee);
    assertEquals("FlipColumnCommand", referee.history().get(3).commandType());
    strategy.takeTurn(referee);
    assertEquals("FlipColumnCommand", referee.history().get(4).commandType());
    strategy.takeTurn(referee);
    assertTrue(referee.isSolved());
    assertEquals(6, referee.score());
    Board tied = new Board(new boolean[][] {{false, true}, {true, true}});
    Referee result = new Player("advanced", new AdvancedStrategy()).play(tied);
    assertTrue(result.isSolved());
    assertEquals(3, result.score());
    assertEquals("SetCellCommand", result.history().get(2).commandType());
  }

  @Test
  void advancedRespectsBudgetEvenWhenItFails() {
    Referee result = new Player("advanced", new AdvancedStrategy()).play(new Board(1));
    assertFalse(result.isSolved());
    assertEquals(1, result.commandCount());
    assertEquals(2, result.score());
  }

  @Test
  void randomAndCautiousAreReproducibleAndTerminate() {
    for (boolean cautious : List.of(false, true)) {
      PlayerStrategy a = cautious ? new CautiousStrategy(42) : new RandomStrategy(42);
      PlayerStrategy b = cautious ? new CautiousStrategy(42) : new RandomStrategy(42);
      Referee first = new Player("a", a).play(new Board(10));
      Referee second = new Player("b", b).play(new Board(10));
      assertEquals(first.score(), second.score());
      assertEquals(first.boardCopy().toString(), second.boardCopy().toString());
      assertTrue(first.commandCount() <= 100);
      assertTrue(first.isSolved() || first.commandCount() == 100);
      for (int i = 0; i < first.history().size(); i++) {
        assertEquals(first.history().get(i).commandType(), second.history().get(i).commandType());
        assertEquals(first.history().get(i).undone(), second.history().get(i).undone());
      }
      if (cautious) assertTrue(first.history().stream().anyMatch(Referee.HistoryEntry::undone));
      else assertFalse(first.history().stream().anyMatch(Referee.HistoryEntry::undone));
    }
  }

  @Test
  void cautiousUndoImmediatelyFollowsAnOrdinaryCommand() {
    CautiousStrategy strategy = new CautiousStrategy(42);
    strategy.prepare(10);
    Referee referee = new Referee(new Board(10));
    boolean previousWasUndo = false;
    while (referee.hasBudget()) {
      int entries = referee.history().size();
      strategy.takeTurn(referee);
      boolean undo = entries == referee.history().size();
      assertFalse(undo && previousWasUndo);
      previousWasUndo = undo;
    }
  }

  @Test
  void nonProgressingStrategyIsRejected() {
    PlayerStrategy broken =
        new PlayerStrategy() {
          public void prepare(int size) {}

          public void takeTurn(Referee referee) {}
        };
    assertThrows(
        IllegalStateException.class, () -> new Player("broken", broken).play(new Board(2)));
  }

  @Test
  void advancedTraversalResetsBetweenGames() {
    Player player = new Player("advanced", new AdvancedStrategy());
    for (int game = 0; game < 3; game++) {
      Referee result = player.play(CommandTest.initial());
      assertTrue(result.isSolved());
      assertEquals(6, result.score());
    }
  }

  @Test
  void randomStrategiesPrepareFreshCompositesAndHistoryForNewSizes() {
    for (boolean cautious : List.of(false, true)) {
      Player first =
          new Player("first", cautious ? new CautiousStrategy(17) : new RandomStrategy(17));
      Player second =
          new Player("second", cautious ? new CautiousStrategy(17) : new RandomStrategy(17));
      for (int size : List.of(2, 3, 1, 4)) {
        Referee a = first.play(new Board(size));
        Referee b = second.play(new Board(size));
        assertEquals(a.score(), b.score());
        assertEquals(a.commandCount(), b.commandCount());
        assertEquals(a.boardCopy().toString(), b.boardCopy().toString());
        assertTrue(a.isFinished());
        assertTrue(a.history().size() <= size * size);
        assertEquals(1, a.history().get(0).executedAt());
      }
    }
  }

  @Test
  void extraConsoleArgumentsAreRejected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Main.main(new String[] {"compare", "42", "unexpected"}));
  }
}
