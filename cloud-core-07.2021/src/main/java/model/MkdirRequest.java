package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MkdirRequest extends AbstractCommand {
    private final String dirName;

    @Override
    public CommandType getType() {
        return CommandType.MKDIR_REQUEST;
    }
}