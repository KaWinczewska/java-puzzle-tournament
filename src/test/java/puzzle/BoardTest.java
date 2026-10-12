package puzzle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class BoardTest {
  @Test
  void initializationAndCopies() {
    Board empty = new Board(2);
    assertFalse(empty.get(0, 0));
    assertFalse(empty.isSolved());
    boolean[][] values = {{true, false}, {true, true}};
    Board board = new Board(values);
    values[0][0] = false;
    assertTrue(board.get(0, 0));
    Board copy = board.copy();
    copy.set(0, 1, true);
    assertFalse(board.get(0, 1));
    assertTrue(copy.isSolved());
    assertEquals("1 0" + System.lineSeparator() + "1 1" + System.lineSeparator(), board.toString());
  }

  @Test
  void invalidSizesAndCoordinates() {
    assertThrows(IllegalArgumentException.class, () -> new Board(0));
    assertThrows(IllegalArgumentException.class, () -> new Board(-1));
    assertThrows(IllegalArgumentException.class, () -> new Board(50000));
    assertThrows(
        IllegalArgumentException.class, () -> new Board(new boolean[][] {{true}, {false}}));
    assertThrows(IllegalArgumentException.class, () -> new Board(new boolean[][] {null}));
    Board board = new Board(2);
    assertThrows(IndexOutOfBoundsException.class, () -> board.get(-1, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> board.set(0, 2, true));
  }

  @Test
  void randomBoardsAreReproducible() {
    assertEquals(
        Board.random(10, new Random(42)).toString(), Board.random(10, new Random(42)).toString());
  }

  @Test
  void solvedCountTracksNoOpsChangesCopiesAndRandomBoards() {
    Board board = new Board(new boolean[][] {{true, true}, {true, true}});
    assertTrue(board.isSolved());
    board.set(1, 1, true);
    assertTrue(board.isSolved());
    board.set(1, 1, false);
    board.set(1, 1, false);
    assertFalse(board.isSolved());
    Board copy = board.copy();
    copy.set(1, 1, true);
    assertTrue(copy.isSolved());
    assertFalse(board.isSolved());
    board.set(1, 1, true);
    assertTrue(board.isSolved());
    Random random = new Random(42);
    for (int size = 1; size <= 5; size++) {
      Board generated = Board.random(size, random);
      boolean allOnes = true;
      for (int row = 0; row < size; row++) {
        for (int col = 0; col < size; col++) {
          if (!generated.get(row, col)) allOnes = false;
        }
      }
      assertEquals(allOnes, generated.isSolved());
    }
  }
}
