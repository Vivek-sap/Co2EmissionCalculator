package com.sap.emission.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandledServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public HandledServiceException(final String args) {
        super(args);
    }


}
