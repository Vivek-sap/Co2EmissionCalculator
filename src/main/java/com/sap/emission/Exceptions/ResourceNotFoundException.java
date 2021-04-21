package com.sap.emission.Exceptions;


public class ResourceNotFoundException extends HandledServiceException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException( final String args) {
        super(args);
    }
}
