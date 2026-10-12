package puzzle;

import java.util.Random;

public final class Board {
  private final boolean[][] cells;
  private int zeros;

  public Board(int size) {
    if (size <= 0 || (long) size * size >= Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Size must be positive and n*n+1 must fit in int");
    }
    cells = new boolean[size][size];
    zeros = size * size;
  }

  public Board(boolean[][] values) {
    this(values.length);
    for (int row = 0; row < size(); row++) {
      if (values[row] == null || values[row].length != size()) {
        throw new IllegalArgumentException("Board must be square");
      }
      cells[row] = values[row].clone();
      for (boolean value : cells[row]) {
        if (value) zeros--;
      }
    }
  }

  public static Board random(int size, Random random) {
    Board board = new Board(size);
    for (int row = 0; row < size; row++) {
      for (int col = 0; col < size; col++) board.set(row, col, random.nextBoolean());
    }
    return board;
  }

  public int size() {
    return cells.length;
  }

  public void checkIndex(int index) {
    checkIndex(index, size());
  }

  public static void checkIndex(int index, int size) {
    if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index: " + index);
  }

  public boolean get(int row, int col) {
    checkIndex(row);
    checkIndex(col);
    return cells[row][col];
  }

  public void set(int row, int col, boolean value) {
    checkIndex(row);
    checkIndex(col);
    if (cells[row][col] != value) {
      zeros += value ? -1 : 1;
      cells[row][col] = value;
    }
  }

  public boolean isSolved() {
    return zeros == 0;
  }

  public Board copy() {
    return new Board(cells);
  }

  @Override
  public String toString() {
    StringBuilder text = new StringBuilder();
    for (boolean[] row : cells) {
      for (int col = 0; col < size(); col++) {
        if (col > 0) text.append(' ');
        text.append(row[col] ? '1' : '0');
      }
      text.append(System.lineSeparator());
    }
    return text.toString();
  }
}
