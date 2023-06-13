package com.s1gawron.rentalservice.user.exception;

public class WorkerRegisteredByNonAdminUserException extends RuntimeException {

    private WorkerRegisteredByNonAdminUserException(String message) {
        super(message);
    }

    public static WorkerRegisteredByNonAdminUserException create() {
        return new WorkerRegisteredByNonAdminUserException("Worker cannot be registered by non admin user! Please contact your administrator.");
    }
}
