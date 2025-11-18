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

        TablaSimbolos ts = new TablaSimbolos();
        PseudoGenerador generador = new PseudoGenerador(lexer.getTokens());
        PseudoParser parser = new PseudoParser(ts, generador);
        parser.analizar(lexer);

        System.out.println("\n***Tabla de simbolos ***\n");

        for (Simbolo s: ts.getSimbolos().values())
            System.out.println(s);

        System.out.println("\n*** Tuplas generadas ***\n");

        for (Tupla t: generador.getTuplas()) {
            System.out.println(t);
        }

        System.out.println("\n*** Ejecución del Programa ***\n");

        PseudoInterprete interprete = new PseudoInterprete(ts, generador.getTuplas(), generador.getDireccionesFunciones());
        interprete.interpretar();



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
