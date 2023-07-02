package ci.doci.sygescom.exception;

public class NullableException extends RuntimeException{
    String message;

    public NullableException(String message) {
        this.message = message;
    }

    public NullableException(String message, String message1) {
        super(message);
        this.message = message1;
    }
}
