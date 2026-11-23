package com.juntavecinos.movimiento.exception;

/** Otro microservicio no respondio o respondio con un error. */
public class ServicioExternoException extends RuntimeException {
    public ServicioExternoException(String message, Throwable cause) {
        super(message, cause);
    }
}
