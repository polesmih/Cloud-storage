package model;

public enum CommandType { // класс команд, которые будут в дальнейшем использоваться в MessageHandler
    FILE_REQUEST,
    FILE_MESSAGE,
    LIST_REQUEST,
    LIST_MESSAGE,
    SIMPLE_MESSAGE,
    DELETE_REQUEST,
    PATH_UP,
    PATH_RESPONSE,
    PATH_IN_REQUEST,
    AUTH_REQUEST,
    AUTH_RESPONSE,
    REG_REQUEST,
    MKDIR_REQUEST
}
