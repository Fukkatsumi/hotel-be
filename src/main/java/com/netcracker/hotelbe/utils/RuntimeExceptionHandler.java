package com.netcracker.hotelbe.utils;

import com.google.common.base.Throwables;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RuntimeExceptionHandler {
    private static final Logger LOG = LogManager.getLogger("RuntimeExceptionHandler");

    public static ResponseEntity handlePSQLException(final RuntimeException runtimeException) {
        Throwable rootCause = Throwables.getRootCause(runtimeException);
        if (!(rootCause instanceof PSQLException)) {
            throw runtimeException;
        } else {
            String logMessage = rootCause.getMessage();
            String responseMessage = logMessage.split("Detail: ")[1];
            if (LOG.isEnabledFor(Priority.ERROR)) {
                LOG.error(logMessage);
            }

            return new ResponseEntity(responseMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
