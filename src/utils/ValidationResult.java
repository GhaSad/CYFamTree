package utils;

public class ValidationResult {
    private final boolean valide;
    private final String message;

    public ValidationResult(boolean valide, String message) {
        this.valide = valide;
        this.message = message;
    }

    public boolean isValide() {
        return valide;
    }

    public String getMessage() {
        return message;
    }
}
