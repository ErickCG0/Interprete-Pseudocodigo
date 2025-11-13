import java.util.ArrayList;

public class PseudoParser {
    private ArrayList<Token> tokens;
    private int indiceToken = 0;
    private SyntaxException ex;

    private TablaSimbolos ts;
    private TipoIncorporado real;

    private boolean enDeclaracion = false;
    private SemanticException semanticEx;
    private PseudoGenerador generador;

    public PseudoParser(TablaSimbolos ts, PseudoGenerador generador) {
        this.ts = ts;
        this.generador = generador;
    }

    public void analizar(PseudoLexer lexer) throws SyntaxException, SemanticException {
        tokens = lexer.getTokens();

        real = new TipoIncorporado("real");
        ts.definir(real);

        if (Programa()) {
            if (indiceToken == tokens.size()) {
                System.out.println("\nLa sintaxis del programa es correcta");
                if (semanticEx != null) {
                    throw semanticEx;
                }
                return;
            }
        }

        if (semanticEx != null) {
            throw semanticEx;
        }
        throw ex;
    }

    private boolean Programa() {
        if (match("INICIOPROGRAMA"))
            if (Enunciados())
                if (match("FINPROGRAMA")) {
                    generador.crearTuplaFinPrograma();
                    return true;
                }
        return false;
    }

    private boolean Enunciados() {
        int indiceAux = indiceToken;

        if (Enunciado()) {
            while (Enunciado());
            return true;
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Enunciado() {
        int indiceAux = indiceToken;

        if (tokens.get(indiceToken).getTipo().getNombre().equals("VARIABLE"))
            if(Asignacion())
                return true;

        indiceToken = indiceAux;
        if (tokens.get(indiceToken).getTipo().getNombre().equals("LEER"))
            if(Leer())
                return true;

        indiceToken = indiceAux;
        if (tokens.get(indiceToken).getTipo().getNombre().equals("ESCRIBIR"))
            if(Escribir())
                return true;

        indiceToken = indiceAux;
        if (tokens.get(indiceToken).getTipo().getNombre().equals("SI"))
            if(Si())
                return true;

        indiceToken = indiceAux;
        if (tokens.get(indiceToken).getTipo().getNombre().equals("MIENTRAS"))
            if(Mientras())
                return true;

        indiceToken = indiceAux;
        if (tokens.get(indiceToken).getTipo().getNombre().equals("DECLARARVARIABLES"))
            if(DeclararVariables())
                return true;

        indiceToken = indiceAux;
        if (tokens.get(indiceToken).getTipo().getNombre().equals("REPITE"))
            if(Repite())
                return true;

        indiceToken = indiceAux;
        return false;
    }

    private boolean Repite() {
        int indiceAux = indiceToken;
        int indiceTupla = generador.getTuplas().size() + 1;

        if (match("REPITE"))
            if (match("PARENTESISIZQ"))
                if (match("VARIABLE"))
                    if (match("COMA"))
                        if (Valor())
                            if (match("COMA"))
                                if (Valor())
                                    if (match("PARENTESISDER")) {
                                        generador.crearTuplaAsignacionRepite(indiceAux + 2);
                                        generador.crearTuplaComparacionRepite(indiceAux + 2);
                                        if (Enunciados())
                                            if (match("FINREPITE")) {
                                                generador.crearTuplaAvanceRepite(indiceAux + 2);
                                                generador.conectarMientras(indiceTupla);
                                                return true;
                                            }
                                    }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Bucle() {
        int indiceAux = indiceToken;

        if (match("PARENTESISIZQ"))
            if(Valor())
                if(match("COMA"))
                    if(Valor())
                        if(match("COMA"))
                            if(Valor())
                                if(match("PARENTESISDER"))
                                    return true;
        indiceToken = indiceAux;
        return false;
    }

    private boolean DeclararVariables() {
        int indiceAux = indiceToken;

        if (match("DECLARARVARIABLES")) {
            if (match("PUNTOS")) {
                enDeclaracion = true;
                if (match("VARIABLE")) {
                    while (match("COMA")) {
                        if (!match("VARIABLE")) {
                            enDeclaracion = false;
                            indiceToken = indiceAux;
                            return false;
                        }
                    }
                    enDeclaracion = false;
                    return true;
                }
                enDeclaracion = false;
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Asignacion() {
        int indiceAux = indiceToken;

        if (match("VARIABLE"))
            if (match("IGUAL"))
                if (Expresion()) {
                    generador.crearTuplaAsignacion(indiceAux, indiceToken);
                    return true;
                }
        indiceToken = indiceAux;
        return false;
    }

    private boolean Expresion() {
        int indiceAux = indiceToken;

        if (Valor())
            if (match("OPARITMETICO"))
                if (Valor())
                    return true;

        indiceToken = indiceAux;
        if (Valor())
            return true;

        indiceToken = indiceAux;
        return false;
    }

    private boolean Valor() {
        if (match("VARIABLE") || match("NUMERO"))
            return true;

        return false;
    }

    private boolean Leer() {
        int indiceAux = indiceToken;

        if(match("LEER"))
            if (match("VARIABLE")) {
                generador.crearTuplaLeer(indiceAux+1);
                return true;
            }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Escribir() {
        int indiceAux = indiceToken;

        if(match("ESCRIBIR"))
            if(match("CADENA"))
                if(match("COMA"))
                    if(match("VARIABLE")) {
                        generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                        return true;
                    }

        indiceToken = indiceAux;
        if (match("ESCRIBIR"))
            if (match("CADENA")) {
                generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                return true;
            }

        indiceToken = indiceAux;
        if(match("ESCRIBIR"))
            if(match("VARIABLE")) {
                generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                return true;
            }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Si() {
        int indiceAux = indiceToken;
        int indiceTupla = generador.getTuplas().size();

        if (match("SI"))
            if(Comparacion())
                if(match("ENTONCES"))
                    if (Enunciados())
                        if (match("FINSI")) {
                            generador.conectarSi(indiceTupla);
                            return true;
                        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Mientras() {
        int indiceAux = indiceToken;
        int indiceTupla = generador.getTuplas().size();

        if (match("MIENTRAS"))
            if(Comparacion())
                if(Enunciados())
                    if(match("FINMIENTRAS")) {
                        generador.conectarMientras(indiceTupla);
                        return true;
                    }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Comparacion() {
        int indiceAux = indiceToken;

        if(match("PARENTESISIZQ"))
            if(Valor())
                if(match("OPRELACIONAL"))
                    if(Valor())
                        if(match("PARENTESISDER")) {
                            generador.crearTuplaComparacion(indiceAux+1);
                            return true;
                        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean match(String nombre) {
        Variable v;

        if (tokens.get(indiceToken).getTipo().getNombre().equals(nombre)) {
            System.out.println(nombre + ": " + tokens.get(indiceToken).getNombre());

            if (nombre.equals(TipoToken.VARIABLE)) {
                String nombreVariable = tokens.get(indiceToken).getNombre();
                v = (Variable) ts.resolver(nombreVariable);

                if (enDeclaracion) {
                    if (v != null) {
                        if (semanticEx == null) {
                            semanticEx = new SemanticException("Variable '" + nombreVariable + "' ya fue declarada anteriormente");
                        }
                        System.out.println("ERROR: Variable '" + nombreVariable + "' ya declarada");
                    } else {
                        v = new Variable(nombreVariable, real);
                        try {
                            ts.definir(v);
                            System.out.println("Variable '" + nombreVariable + "' declarada exitosamente");
                        } catch (Exception e) {
                            if (semanticEx == null) {
                                semanticEx = new SemanticException("Error al declarar variable '" + nombreVariable + "': " + e.getMessage());
                            }
                        }
                    }
                } else {
                    if (v == null) {
                        if (semanticEx == null) {
                            semanticEx = new SemanticException("Variable '" + nombreVariable + "' no ha sido declarada");
                        }
                        System.out.println("ERROR: Variable '" + nombreVariable + "' no declarada");
                    } else {
                        System.out.println("Variable '" + nombreVariable + "' resuelta correctamente");
                    }
                }
            }

            indiceToken++;
            return true;
        }

        if (ex == null)
            ex = new SyntaxException(nombre, tokens.get(indiceToken).getTipo().getNombre());

        return false;
    }
}

