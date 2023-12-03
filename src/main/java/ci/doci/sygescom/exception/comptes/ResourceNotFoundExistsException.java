package ci.doci.sygescom.exception.comptes;

public class ResourceNotFoundExistsException extends RuntimeException{
    public ResourceNotFoundExistsException() {
    }

    public ResourceNotFoundExistsException(String message) {
        super(message);
    }

    public ResourceNotFoundExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
