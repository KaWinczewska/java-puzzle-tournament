package puzzle.commands;

import puzzle.Board;

public interface Command {
  void validate(int size);

  void execute(Board board);

  void undo(Board board);
}
