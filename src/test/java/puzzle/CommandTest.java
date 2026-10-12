package puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import puzzle.commands.*;

class CommandTest {
  static Stream<Arguments> commands() {
    return Stream.of(
        Arguments.of(new SetCellCommand(0, 0, false), "0 0 1/0 1 0/1 0 0"),
        Arguments.of(new SetCellCommand(1, 0, true), "1 0 1/1 1 0/1 0 0"),
        Arguments.of(new FlipRowCommand(0), "0 1 0/0 1 0/1 0 0"),
        Arguments.of(new FlipColumnCommand(1), "1 1 1/0 0 0/1 1 0"),
        Arguments.of(new SetDiagonalCommand(true, false), "0 0 1/0 0 0/1 0 0"),
        Arguments.of(new SetDiagonalCommand(false, true), "1 0 1/0 1 0/1 0 0"),
        Arguments.of(new SetDiagonalCommand(false, false), "1 0 0/0 0 0/0 0 0"),
        Arguments.of(new SetDiagonalCommand(true, true), "1 0 1/0 1 0/1 0 1"),
        Arguments.of(new SortRowCommand(0), "0 1 1/0 1 0/1 0 0"),
        Arguments.of(new SortColumnCommand(0), "0 0 1/1 1 0/1 0 0"),
        Arguments.of(new RotateRowCommand(0), "1 1 0/0 1 0/1 0 0"),
        Arguments.of(new RotateColumnCommand(0), "1 0 1/1 1 0/0 0 0"));
  }

  static Board initial() {
    return new Board(
        new boolean[][] {{true, false, true}, {false, true, false}, {true, false, false}});
  }

  static String text(String rows) {
    return rows.replace("/", System.lineSeparator()) + System.lineSeparator();
  }

  @ParameterizedTest
  @MethodSource("commands")
  void executionAndUndo(Command command, String expected) {
    Board board = initial();
    String original = board.toString();
    command.execute(board);
    assertEquals(text(expected), board.toString());
    command.undo(board);
    assertEquals(original, board.toString());
    assertThrows(IllegalStateException.class, () -> command.undo(board));
    assertThrows(IllegalStateException.class, () -> command.execute(board));
  }

  @Test
  void lifecycleAndInvalidCoordinatesDoNotChangeBoard() {
    Board board = initial();
    String original = board.toString();
    Command fresh = new SetCellCommand(0, 0, false);
    assertThrows(IllegalStateException.class, () -> fresh.undo(board));
    assertThrows(IndexOutOfBoundsException.class, () -> new FlipRowCommand(3).execute(board));
    assertEquals(original, board.toString());
    fresh.execute(board);
    assertThrows(IllegalStateException.class, () -> fresh.undo(board.copy()));
    fresh.undo(board);
    assertEquals(original, board.toString());
  }

  @Test
  void compositeRunsPastTemporarySolutionAndUndoesInReverseOrder() {
    Board board = new Board(1);
    Command composite =
        new CompositeCommand(1, List.of(new SetCellCommand(0, 0, true), new FlipRowCommand(0)));
    composite.execute(board);
    assertFalse(board.isSolved());
    composite.undo(board);
    assertFalse(board.get(0, 0));
    Board other = initial();
    String before = other.toString();
    Command overlapping =
        new CompositeCommand(
            3, List.of(new SortRowCommand(0), new RotateRowCommand(0), new FlipColumnCommand(0)));
    overlapping.execute(other);
    overlapping.undo(other);
    assertEquals(before, other.toString());
  }

  @Test
  void compositeValidation() {
    assertThrows(IllegalArgumentException.class, () -> new CompositeCommand(3, List.of()));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CompositeCommand(3, List.of(new FlipRowCommand(0), new FlipRowCommand(1))));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CompositeCommand(
                3, List.of(new SetCellCommand(0, 0, true), new SetCellCommand(1, 1, false))));
    Command inner = new CompositeCommand(3, List.of(new FlipRowCommand(0)));
    assertThrows(IllegalArgumentException.class, () -> new CompositeCommand(3, List.of(inner)));
    assertThrows(
        IndexOutOfBoundsException.class,
        () -> new CompositeCommand(3, List.of(new RotateColumnCommand(3))));
    assertThrows(IllegalArgumentException.class, () -> inner.execute(new Board(2)));
    Command used = new FlipRowCommand(0);
    used.execute(new Board(3));
    assertThrows(IllegalStateException.class, () -> new CompositeCommand(3, List.of(used)));
  }

  @Test
  void compositeChecksAllComponentsBeforeMutating() {
    Command component = new FlipRowCommand(0);
    Command composite = new CompositeCommand(3, List.of(new FlipColumnCommand(0), component));
    component.execute(new Board(3));
    Board board = initial();
    String before = board.toString();
    assertThrows(IllegalStateException.class, () -> composite.execute(board));
    assertEquals(before, board.toString());
  }

  @Test
  void oneCellRotationAndSort() {
    for (Command command :
        List.of(
            new RotateRowCommand(0),
            new RotateColumnCommand(0),
            new SortRowCommand(0),
            new SortColumnCommand(0))) {
      Board board = new Board(1);
      command.execute(board);
      command.undo(board);
      assertFalse(board.get(0, 0));
    }
  }

  @Test
  void everyBasicCommandWorksOnBothSingleCellBoards() {
    for (boolean value : List.of(false, true)) {
      List<Command> commands =
          List.of(
              new SetCellCommand(0, 0, false),
              new SetCellCommand(0, 0, true),
              new FlipRowCommand(0),
              new FlipColumnCommand(0),
              new SetDiagonalCommand(true, false),
              new SetDiagonalCommand(true, true),
              new SetDiagonalCommand(false, false),
              new SetDiagonalCommand(false, true),
              new SortRowCommand(0),
              new SortColumnCommand(0),
              new RotateRowCommand(0),
              new RotateColumnCommand(0));
      boolean[] expected = {
        false, true, !value, !value, false, true, false, true, value, value, value, value
      };
      for (int i = 0; i < commands.size(); i++) {
        Board board = new Board(new boolean[][] {{value}});
        Command command = commands.get(i);
        command.execute(board);
        assertEquals(expected[i], board.get(0, 0), command.getClass().getSimpleName());
        assertEquals(expected[i], board.isSolved());
        command.undo(board);
        assertEquals(value, board.get(0, 0));
        assertEquals(value, board.isSolved());
      }
    }
  }

  @Test
  void allCoordinateBearingCommandsRejectInvalidIndicesWithoutMutation() {
    for (int index : List.of(-1, 3)) {
      List<Command> commands =
          List.of(
              new SetCellCommand(index, 0, true),
              new SetCellCommand(0, index, true),
              new FlipRowCommand(index),
              new FlipColumnCommand(index),
              new SortRowCommand(index),
              new SortColumnCommand(index),
              new RotateRowCommand(index),
              new RotateColumnCommand(index));
      for (Command command : commands) {
        Board board = initial();
        String original = board.toString();
        assertThrows(IndexOutOfBoundsException.class, () -> command.execute(board));
        assertEquals(original, board.toString());
        assertFalse(board.isSolved());
      }
    }
  }
}
