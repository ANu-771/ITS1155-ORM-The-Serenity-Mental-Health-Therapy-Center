package lk.ijse.theserenitymentalhealththerapycenter.exception;

public class SchedulingConflictException extends RuntimeException {
    public SchedulingConflictException(String message) {
        super(message);
    }

    public SchedulingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
