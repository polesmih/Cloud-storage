package model;

public enum CommandType { // класс команд, которые будут в дальнейшем использоваться в MessageHandler
    FILE_REQUEST,
    FILE_MESSAGE,
    LIST_REQUEST,
    LIST_MESSAGE,
    DELETE_REQUEST,
    PATH_UP,
    PATH_RESPONSE,
    PATH_IN_REQUEST
}
