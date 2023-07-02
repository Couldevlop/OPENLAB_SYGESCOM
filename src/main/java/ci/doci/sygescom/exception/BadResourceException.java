package ci.doci.sygescom.exception;

public class BadResourceException extends RuntimeException {
    private String message;

    public BadResourceException(String message) {
        super(message);
    }

    public String addMessage(String err){
        return err;
    }
}
