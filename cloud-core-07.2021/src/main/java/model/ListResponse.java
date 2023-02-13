package model;

import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ToString
@Getter
public class ListResponse extends AbstractCommand {

    private final List<String> names;
    private boolean root;

    public ListResponse(Path path) throws IOException {
        if (path.getParent() == null) {
            root = true;
        }
        names = Arrays.asList(Objects.requireNonNull(path.toFile().list()));
    }
//        names = Files.list(path)
//                .map(p -> p.getFileName().toString())
//                .collect(Collectors.toList());
//
//    public List<String> getNames() {
//        return names;
//    }

    @Override
    public CommandType getType() {

        return CommandType.LIST_MESSAGE;
    }

}
