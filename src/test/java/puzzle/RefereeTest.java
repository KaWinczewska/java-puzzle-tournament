package puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import puzzle.commands.*;

class RefereeTest {
  @Test
  void emptyUndoAndInvalidCommandDoNotCount() {
    Referee referee = new Referee(new Board(2));
    assertThrows(IllegalStateException.class, referee::undo);
    assertThrows(
        IndexOutOfBoundsException.class, () -> referee.execute(new SetCellCommand(2, 0, true)));
    assertEquals(0, referee.commandCount());
    assertTrue(referee.history().isEmpty());
  }

  @Test
  void undoUsesStackAndKeepsHistory() {
    Referee referee = new Referee(new Board(3));
    referee.execute(new SetCellCommand(0, 0, true));
    referee.execute(new SetCellCommand(0, 1, true));
    referee.undo();
    referee.undo();
    assertFalse(referee.cell(0, 0));
    assertFalse(referee.cell(0, 1));
    assertFalse(referee.canUndo());
    assertEquals(4, referee.commandCount());
    assertEquals(2, referee.history().size());
    assertTrue(referee.history().get(0).undone());
    assertTrue(referee.history().get(1).undone());
    assertEquals(1, referee.history().get(0).executedAt());
    assertEquals(2, referee.history().get(1).executedAt());
    assertEquals(3, referee.history().get(1).undoneAt());
    assertEquals(4, referee.history().get(0).undoneAt());
    assertThrows(UnsupportedOperationException.class, () -> referee.history().clear());
  }

  @Test
  void scoringLimitAndIndependentBoard() {
    Board board = new Board(1);
    Referee referee = new Referee(board);
    referee.execute(new SetCellCommand(0, 0, true));
    assertEquals(1, referee.score());
    assertFalse(board.isSolved());
    assertThrows(IllegalStateException.class, () -> referee.execute(new FlipRowCommand(0)));
    assertThrows(IllegalStateException.class, referee::undo);
    Referee failure = new Referee(board);
    failure.execute(new SetCellCommand(0, 0, false));
    assertEquals(2, failure.score());
    assertEquals(0, new Referee(new Board(new boolean[][] {{true}})).score());
  }

  @Test
  void compositeCountsOnce() {
    Referee referee = new Referee(new Board(2));
    referee.execute(
        new CompositeCommand(2, List.of(new FlipRowCommand(0), new FlipColumnCommand(0))));
    assertEquals(1, referee.commandCount());
    referee.undo();
    assertEquals(new Board(2).toString(), referee.boardCopy().toString());
    assertEquals(2, referee.commandCount());
  }

  @Test
  void solvedGamesRejectFurtherExecutionAndUndo() {
    Board initial = new Board(new boolean[][] {{false, true}, {true, true}});
    Referee referee = new Player("greedy", new puzzle.strategies.GreedyStrategy()).play(initial);
    assertTrue(referee.isFinished());
    assertEquals(1, referee.score());
    String finalBoard = referee.boardCopy().toString();
    assertThrows(IllegalStateException.class, () -> referee.execute(new FlipRowCommand(0)));
    assertThrows(IllegalStateException.class, referee::undo);
    assertEquals(finalBoard, referee.boardCopy().toString());
    assertEquals(1, referee.commandCount());
    assertEquals(1, referee.history().size());
    assertFalse(referee.history().get(0).undone());
    Referee alreadySolved = new Referee(new Board(new boolean[][] {{true}}));
    assertTrue(alreadySolved.isFinished());
    assertThrows(IllegalStateException.class, () -> alreadySolved.execute(new FlipRowCommand(0)));
    assertEquals(0, alreadySolved.score());
  }

  @Test
  void undoAtLastBudgetUnitAndRepeatedEmptyUndo() {
    Referee referee = new Referee(new Board(2));
    referee.execute(new SetCellCommand(0, 0, true));
    referee.execute(new SetCellCommand(0, 1, true));
    referee.execute(new SetCellCommand(1, 0, true));
    referee.undo();
    assertEquals(4, referee.commandCount());
    assertTrue(referee.isFinished());
    assertFalse(referee.cell(1, 0));
    assertTrue(referee.history().get(2).undone());
    assertEquals(5, referee.score());
    assertThrows(IllegalStateException.class, referee::undo);
    assertThrows(IllegalStateException.class, () -> referee.execute(new FlipRowCommand(0)));
    Referee empty = new Referee(new Board(2));
    empty.execute(new SetCellCommand(0, 0, true));
    empty.undo();
    assertThrows(IllegalStateException.class, empty::undo);
    assertEquals(2, empty.commandCount());
    assertEquals(1, empty.history().size());
  }
}
