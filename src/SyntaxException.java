public class SyntaxException extends Exception{
    public SyntaxException(String message){
        super(message);
    }

    public SyntaxException(String message1, String message2) {
        super("Se esperaba un token '" + message1 +  "' y se encontró '" + message2 + "'");
    }
}
