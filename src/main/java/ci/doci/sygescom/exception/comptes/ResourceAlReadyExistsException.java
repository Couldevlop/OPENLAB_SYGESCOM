package ci.doci.sygescom.exception.comptes;

public class ResourceAlReadyExistsException extends RuntimeException{
    public ResourceAlReadyExistsException() {
    }

    public ResourceAlReadyExistsException(String message) {
        super(message);
    }

    public ResourceAlReadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
