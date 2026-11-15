package com.yerandis.meetingRoom.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, UUID id) {
        super(resource + " con id " + id + " no encontrado");
    }
}
