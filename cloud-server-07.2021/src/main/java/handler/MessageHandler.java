package handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Const;
import database.DataBaseHandler;
import database.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.*;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private Path currentPath;

//    public MessageHandler() throws IOException {

//        currentPath = Paths.get("server_dir");
//        if (!Files.exists(currentPath)) {
//            Files.createDirectory(currentPath);
//        }
//    }


//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(new ListResponse(currentPath));
//        ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand command) throws Exception {
        log.debug("received: {}", command.getType());
        switch (command.getType()) {

            case FILE_REQUEST:
                FileRequest fileRequest = (FileRequest) command;
                try {
                    ctx.writeAndFlush(new FileMessage(currentPath.resolve(fileRequest.getName())));
                }catch (Exception e) {
                    ctx.writeAndFlush(new Message("Sending error..."));
                }
//                FileMessage msg = new FileMessage(currentPath.resolve(fileRequest.getName()));
//                ctx.writeAndFlush(msg);
                break;
            case FILE_MESSAGE:
                FileMessage message = (FileMessage) command;
                try (FileOutputStream fos = new FileOutputStream(currentPath.resolve(message.getName()).toString())) {
                    fos.write(message.getData());
                    ctx.writeAndFlush(new ListResponse(currentPath));
                    ctx.writeAndFlush(new Message("File sending successfully!"));
                }//
//                Files.write(currentPath.resolve(message.getName()), message.getData());
//                ctx.writeAndFlush(new ListResponse(currentPath));
                break;

            case LIST_MESSAGE:
                try {
                    ctx.writeAndFlush(new Message("Server listview refreshing"));
                    ctx.writeAndFlush(new ListResponse(currentPath));
                    ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));


                } catch (IOException e) {
                    ctx.writeAndFlush(new Message("Sending error..."));
                }
                ctx.writeAndFlush(new Message("Server listview refreshed"));
                break;

            case PATH_UP:
                if (currentPath.getParent() != null) {
                    currentPath = currentPath.getParent();
                }
                ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;

            case LIST_REQUEST:
                ctx.writeAndFlush(new ListResponse(currentPath));
                break;

            case DELETE_REQUEST:
                DeleteRequest deleteRequest = (DeleteRequest) command;
                Path pathDel = currentPath.resolve(deleteRequest.getFileName());
                boolean isDeleted = pathDel.toFile().delete();
                try {
                    if (isDeleted) {
                        ctx.writeAndFlush(new ListResponse(currentPath));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case PATH_IN_REQUEST:
                PathInRequest request = (PathInRequest) command;
                Path newPath = currentPath.resolve(request.getDir());
                if (Files.isDirectory(newPath)) {
                    currentPath = newPath;
                    ctx.writeAndFlush(new PathUpResponse(currentPath.toString()));
                    ctx.writeAndFlush(new ListResponse(currentPath));
                }
                break;

            case AUTH_REQUEST:
                AuthRequest authRequest = (AuthRequest) command;
                User user = new User();
                user.setLogin(authRequest.getLoginText());
                user.setPassword(authRequest.getPasswordText());
                ResultSet resultSet = new DataBaseHandler().getUser(user);
                try {
                    resultSet.next();
                    user.setName(resultSet.getString(Const.USERS_NAME));
                    currentPath = Paths.get(user.getLogin());
                    if (!Files.exists(currentPath)) {
                        Files.createDirectory(currentPath);
                    }
                    ctx.writeAndFlush(new AuthResponse(user));
                }catch (SQLException throwable) {
                    log.error("Error {}", throwable.getClass());
                    ctx.writeAndFlush(new Message("Authentication failed"));
                }catch (IOException e) {
                    log.error("Error: {}", e.getClass());
                }
                break;

            case REG_REQUEST:
                RegRequest regRequest = (RegRequest) command;
                User newUser = new User(regRequest.getNameText(), regRequest.getLoginText(), regRequest.getPasswordText());
                new DataBaseHandler().registrationUser(newUser.getLogin(), newUser.getPassword(), newUser.getName());
                ctx.writeAndFlush(new Message("Registration successful!"));
                break;

//            case MKDIR_REQUEST:
//                MkdirRequest mkdirRequest = (MkdirRequest) command;
//                currentPath = currentPath.resolve(mkdirRequest.getDirName());
//                currentPath.toFile().mkdir();
//                currentPath = currentPath.getParent();
//                try {
//                    ctx.writeAndFlush(new ListResponse(currentPath));
//                    ctx.writeAndFlush(new Message("A new directory created"));
//                }catch (IOException e) {
//                    log.debug("message: {}", "Error creating directory to server");
//                    ctx.writeAndFlush(new Message("Error creating directory to server"));
//                }
//                break;


        }
    }
}