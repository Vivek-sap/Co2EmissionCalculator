package com.sap.emission.Exceptions;


public class ParseException extends HandledServiceException {
    private static final long serialVersionUID = 1L;

    public ParseException( final String args) {
        super(args);
    }
}
