import java.util.HashMap;

public class InvalidFormException extends Exception {
    private HashMap<String, String> errors;

    public InvalidFormException(String msg, HashMap<String, String> errors) {
        super(msg);
        this.errors = errors;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }
}
