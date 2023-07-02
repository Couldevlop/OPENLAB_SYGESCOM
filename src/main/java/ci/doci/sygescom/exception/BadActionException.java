package ci.doci.sygescom.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BadActionException extends RuntimeException{
    private Object t;
    private Object t1;
    private String message;

}


