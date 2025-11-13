import java.io.FileReader;
import java.io.IOException;

public class PruebaTablaSimbolos {
    public static void main(String[] arg) throws LexicalException, SyntaxException, SemanticException {
        String entrada = leerPrograma("src/parser.txt");
        PseudoLexer lexer = new PseudoLexer();
        lexer.analizar(entrada);

        System.out.println("*** Análisis léxico ***\n");

        for (Token t: lexer.getTokens()) {
            System.out.println(t);
        }

        System.out.println("*** Análisis sintáctico ***\n");

        PseudoParser parser = new PseudoParser();
        parser.analizar(lexer);



    }

    private static String leerPrograma(String nombre) {
        String entrada = "";

        try {
            FileReader reader = new FileReader(nombre);
            int character;

            while ((character = reader.read()) != -1)
                entrada += (char) character;

            reader.close();
            return entrada;
        } catch (IOException e) {
            return "";
        }
    }
}
