package puzzle.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import puzzle.Board;

public final class CompositeCommand extends AbstractCommand {
  private final int size;
  private final List<Command> commands;

  public CompositeCommand(int size, List<Command> commands) {
    if (size <= 0) throw new IllegalArgumentException("Size must be positive");
    this.size = size;
    this.commands = List.copyOf(commands);
    if (commands.isEmpty()) throw new IllegalArgumentException("Composite must not be empty");
    Set<Class<?>> types = new HashSet<>();
    for (Command command : this.commands) {
      if (!isBasic(command) || !types.add(command.getClass())) {
        throw new IllegalArgumentException("Use distinct basic command types without nesting");
      }
      ((AbstractCommand) command).requireFresh();
      command.validate(size);
    }
  }

  private boolean isBasic(Command command) {
    return command instanceof SetCellCommand
        || command instanceof FlipRowCommand
        || command instanceof FlipColumnCommand
        || command instanceof SetDiagonalCommand
        || command instanceof SortRowCommand
        || command instanceof SortColumnCommand
        || command instanceof RotateRowCommand
        || command instanceof RotateColumnCommand;
  }

  @Override
  public void validate(int size) {
    if (size != this.size) throw new IllegalArgumentException("Wrong board size");
    for (Command command : commands) {
      ((AbstractCommand) command).requireFresh();
      command.validate(size);
    }
  }

  @Override
  protected void apply(Board board) {
    for (Command command : commands) command.execute(board);
  }

  @Override
  protected void reverse(Board board) {
    for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo(board);
  }
}
