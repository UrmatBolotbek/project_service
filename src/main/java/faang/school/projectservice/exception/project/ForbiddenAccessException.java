package faang.school.projectservice.exception.project;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException(String message, Object... args) {
        super(String.format(message, args));
    }
}
