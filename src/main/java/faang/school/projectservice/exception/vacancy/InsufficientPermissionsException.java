package faang.school.projectservice.exception.vacancy;

public class InsufficientPermissionsException extends RuntimeException {
    public InsufficientPermissionsException(String message) {
        super(message);
    }
}
