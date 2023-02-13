package model;


import database.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse extends AbstractCommand{
    private final String name;
    private final String login;
    private final String password;

    public AuthResponse(User user) {
        name = user.getName();
        login = user.getLogin();
        password = user.getPassword();

    }

    @Override
    public CommandType getType() {

        return CommandType.AUTH_RESPONSE;
    }
}
