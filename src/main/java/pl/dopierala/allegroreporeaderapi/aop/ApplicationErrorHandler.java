package pl.dopierala.allegroreporeaderapi.aop;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.dopierala.allegroreporeaderapi.exceptions.ParseToJsonNotPossibleException;
import pl.dopierala.allegroreporeaderapi.exceptions.UserNotFoundException;

@ControllerAdvice
public class ApplicationErrorHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity handleUserNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ParseToJsonNotPossibleException.class)
    public ResponseEntity handleParseToJsonNotPossibleException(){
        return ResponseEntity.notFound().build();
    }
}
