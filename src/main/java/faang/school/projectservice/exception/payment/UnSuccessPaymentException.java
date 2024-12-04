package faang.school.projectservice.exception.payment;

public class UnSuccessPaymentException extends RuntimeException {
    public UnSuccessPaymentException(String message) {
        super(String.format(message));
    }
}
