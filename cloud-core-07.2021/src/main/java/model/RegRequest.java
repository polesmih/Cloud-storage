package model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegRequest extends AbstractCommand {
    private final String loginText;
    private final String passwordText;
    private final String nameText;

    @Override
    public CommandType getType() {
        return CommandType.REG_REQUEST;
    }
}
