package com.codingcat.catlib.lang;

public class UnknownLanguageException extends RuntimeException {

    public UnknownLanguageException(String langName) {
        super(String.format("Cannot find language \"%s\"", langName));
    }
}
