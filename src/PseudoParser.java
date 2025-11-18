import java.util.ArrayList;

public class PseudoParser {
    private ArrayList<Token> tokens;
    private int indiceToken = 0;
    private SyntaxException ex;

    private Scope currentScope;
    private GlobalScope globalScope;

    private boolean enDeclaracion = false;
    private SemanticException semanticEx;
    private PseudoGenerador generador;
    private TablaSimbolos ts;  // <--- AÑADIR ESTA LÍNEA

    public PseudoParser(TablaSimbolos ts, PseudoGenerador generador) {
        this.generador = generador;
        this.ts = ts;

        // Inicializar alcances
        globalScope = new GlobalScope();
        currentScope = globalScope;

        // Definir tipos incorporados
        TipoIncorporado entero = new TipoIncorporado("entero");
        TipoIncorporado flotante = new TipoIncorporado("flotante");
        TipoIncorporado booleano = new TipoIncorporado("booleano");

        globalScope.define(entero);
        globalScope.define(flotante);
        globalScope.define(booleano);
    }

    public void analizar(PseudoLexer lexer) throws SyntaxException, SemanticException {
        tokens = lexer.getTokens();

        if (Programa()) {
            if (indiceToken == tokens.size()) {
                System.out.println("\nLa sintaxis del programa es correcta");
                System.out.println("\n=== Tabla de Símbolos ===");
                printSymbolTable();
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

    private void printSymbolTable() {
        System.out.println("\n--- Alcance Global ---");
        printScope(globalScope, 0);
    }

    private void printScope(Scope scope, int level) {
        String indent = "  ".repeat(level);

        if (scope instanceof GlobalScope) {
            GlobalScope gs = (GlobalScope) scope;
            for (Simbolo sym : gs.members.values()) {
                if (!(sym instanceof TipoIncorporado)) {
                    System.out.println(indent + sym);
                    if (sym instanceof FuncionSimbolo) {
                        FuncionSimbolo fs = (FuncionSimbolo) sym;
                        System.out.println(indent + "  Parámetros y variables locales:");
                        for (Simbolo localSym : fs.members.values()) {
                            System.out.println(indent + "    " + localSym);
                        }
                    }
                }
            }
        }
    }

    private boolean Programa() throws SemanticException {
        // Primero procesar todas las funciones
        while (Funcion());

        // Luego el programa principal
        if (match("INICIOPROGRAMA"))
            if (Enunciados())
                if (match("FINPROGRAMA")) {
                    generador.crearTuplaFinPrograma();
                    return true;
                }
        return false;
    }

    private boolean Funcion() throws SemanticException {
        int indiceAux = indiceToken;

        if (match("FUNCION")) {
            Token nombreFuncionToken = getCurrentToken();
            if (match("VARIABLE")) {
                String nombreFuncion = nombreFuncionToken.getNombre();

                if (match("PARENTESISIZQ")) {
                    // Obtener tipo de retorno y parámetros
                    ArrayList<String> parametrosNombres = new ArrayList<>();
                    ArrayList<Tipo> parametrosTipos = new ArrayList<>();
                    ArrayList<Token> parametrosTokens = new ArrayList<>();
                    Tipo tipoRetorno = null;

                    if (ListaParametros(parametrosNombres, parametrosTipos, parametrosTokens)) {
                        if (match("PARENTESISDER")) {
                            // Verificar que la función no esté ya definida
                            Simbolo existing = globalScope.resolve(nombreFuncion);
                            if (existing != null && existing instanceof FuncionSimbolo) {
                                if (semanticEx == null) {
                                    semanticEx = new SemanticException("DECLARACION DUPLICADA: La función '" + nombreFuncion + "' ya fue declarada");
                                }
                                System.out.println("ERROR: Función '" + nombreFuncion + "' ya declarada");
                            }

                            // Si hay parámetros, el tipo de retorno es el tipo del primer parámetro
                            if (parametrosTipos.size() > 0) {
                                tipoRetorno = parametrosTipos.get(0);
                            }

                            // Crear tupla de inicio de función
                            generador.crearTuplaInicioFuncion(nombreFuncionToken, parametrosTokens);

                            // Crear símbolo de función y agregarlo al alcance global
                            FuncionSimbolo funcionSimbolo = new FuncionSimbolo(nombreFuncion, tipoRetorno, currentScope);
                            globalScope.define(funcionSimbolo);

                            // Cambiar al alcance de la función
                            currentScope = funcionSimbolo;

                            System.out.println("[SEMANTIC] Función definida: " + nombreFuncion);

                            // Definir parámetros en el alcance de la función
                            for (int i = 0; i < parametrosNombres.size(); i++) {
                                String paramNombre = parametrosNombres.get(i);
                                Tipo paramTipo = parametrosTipos.get(i);
                                Variable paramVar = new Variable(paramNombre, paramTipo);
                                funcionSimbolo.define(paramVar);
                                System.out.println("[SEMANTIC] Parámetro definido: " + paramNombre + " : " + paramTipo.getNombre());
                            }

                            // Procesar el cuerpo de la función
                            if (Enunciados()) {
                                // Regresa es opcional (para procedimientos sin valor de retorno)
                                int indiceRegresa = indiceToken;
                                if (match("REGRESA")) {
                                    int indiceValor = indiceToken;
                                    if (Valor()) {
                                        // Crear tupla de retorno con valor
                                        generador.crearTuplaRegresa(indiceValor);
                                    } else {
                                        // Regresa sin valor
                                        generador.crearTuplaRegresa(-1);
                                    }
                                } else {
                                    // No hay regresa, crear uno implícito sin valor
                                    generador.crearTuplaRegresa(-1);
                                }

                                if (match("FINFUNCION")) {
                                    // Restaurar el alcance anterior
                                    currentScope = currentScope.getEnclosingScope();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean ListaParametros(ArrayList<String> nombres, ArrayList<Tipo> tipos, ArrayList<Token> tokens) throws SemanticException {
        int indiceAux = indiceToken;

        // Patrón: tipo: nombre (solo un parámetro por ahora en funciones simples)
        // Para "funcion PROMEDIO(entero: numeroDeElementos)" o "funcion APROBADO(flotante: promedio)"
        if (TipoVariable()) {
            Token tipoToken = this.tokens.get(indiceToken - 1);
            String nombreTipo = tipoToken.getNombre();
            Tipo tipo = (Tipo) globalScope.resolve(nombreTipo);

            if (tipo == null) {
                if (semanticEx == null) {
                    semanticEx = new SemanticException("Tipo '" + nombreTipo + "' no reconocido");
                }
            }

            if (match("PUNTOS")) {
                Token nombreToken = getCurrentToken();
                if (match("VARIABLE")) {
                    String nombreParam = nombreToken.getNombre();
                    nombres.add(nombreParam);
                    tipos.add(tipo);
                    tokens.add(nombreToken);
                    return true;
                }
            }
        }

        indiceToken = indiceAux;
        return true; // Lista vacía es válida (funciones sin parámetros)
    }

    private boolean TipoVariable() {
        return match("ENTERO") || match("FLOTANTE") || match("BOOLEANO");
    }

    private Token getCurrentToken() {
        if (indiceToken < tokens.size()) {
            return tokens.get(indiceToken);
        }
        return null;
    }

    private boolean Enunciados() throws SemanticException {
        int indiceAux = indiceToken;

        if (Enunciado()) {
            while (Enunciado());
            return true;
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Enunciado() throws SemanticException {
        int indiceAux = indiceToken;

        // Primero intentar llamada a función (antes que asignación)
        // porque ambas empiezan con VARIABLE
        if (LlamadaFuncion())
            return true;

        indiceToken = indiceAux;
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

    private boolean LlamadaFuncion() throws SemanticException {
        int indiceAux = indiceToken;

        Token nombreToken = getCurrentToken();
        if (match("VARIABLE")) {
            String nombreFuncion = nombreToken.getNombre();

            if (match("PARENTESISIZQ")) {
                // Verificar que la función existe
                Simbolo funcSymbol = globalScope.resolve(nombreFuncion);
                if (funcSymbol == null || !(funcSymbol instanceof FuncionSimbolo)) {
                    if (semanticEx == null) {
                        semanticEx = new SemanticException("USO DE FUNCION NO DECLARADA: La función '" + nombreFuncion + "' no ha sido declarada");
                    }
                    System.out.println("ERROR: Función '" + nombreFuncion + "' no declarada");
                }

                System.out.println("[SEMANTIC] Llamada a función: " + nombreFuncion);

                // Recolectar argumentos
                ArrayList<Token> argumentos = new ArrayList<>();
                int indiceArg = indiceToken;

                // Procesar argumentos
                if (ListaArgumentosTokens(argumentos)) {
                    if (match("PARENTESISDER")) {
                        // Crear tupla de llamada a función
                        generador.crearTuplaLlamadaFuncion(nombreToken, argumentos);
                        return true;
                    }
                }
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean ListaArgumentosTokens(ArrayList<Token> argumentos) throws SemanticException {
        int indiceAux = indiceToken;

        Token argToken = getCurrentToken();
        if (Valor()) {
            argumentos.add(argToken);

            while (match("COMA")) {
                argToken = getCurrentToken();
                if (!Valor()) {
                    indiceToken = indiceAux;
                    return false;
                }
                argumentos.add(argToken);
            }
            return true;
        }

        indiceToken = indiceAux;
        return true; // Lista vacía es válida
    }

    private boolean ListaArgumentos() throws SemanticException {
        int indiceAux = indiceToken;

        if (Valor()) {
            while (match("COMA")) {
                if (!Valor()) {
                    indiceToken = indiceAux;
                    return false;
                }
            }
            return true;
        }

        indiceToken = indiceAux;
        return true; // Lista vacía es válida
    }

    private boolean Repite() throws SemanticException {
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

    private boolean DeclararVariables() throws SemanticException {
        int indiceAux = indiceToken;

        if (match("DECLARARVARIABLES")) {
            if (match("PUNTOS")) {
                enDeclaracion = true;

                // Procesar declaraciones: tipo: var1, var2; tipo: var3
                if (DeclaracionConTipo()) {
                    while (match("SEPARADOR")) {
                        if (!DeclaracionConTipo()) {
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

    private boolean DeclaracionConTipo() throws SemanticException {
        int indiceAux = indiceToken;

        if (TipoVariable()) {
            Token tipoToken = tokens.get(indiceToken - 1);
            String nombreTipo = tipoToken.getNombre();
            Tipo tipo = (Tipo) globalScope.resolve(nombreTipo);

            if (tipo == null) {
                if (semanticEx == null) {
                    semanticEx = new SemanticException("Tipo '" + nombreTipo + "' no reconocido");
                }
            }

            if (match("PUNTOS")) {
                Token nombreToken = getCurrentToken();
                if (match("VARIABLE")) {
                    String nombreVar = nombreToken.getNombre();

                    // Colectar todas las variables de este tipo
                    ArrayList<Token> variablesDeclaradas = new ArrayList<>();
                    variablesDeclaradas.add(nombreToken);
                    defineVariable(nombreVar, tipo);

                    // Procesar más variables del mismo tipo separadas por coma
                    while (true) {
                        int checkPoint = indiceToken;
                        if (match("COMA")) {
                            nombreToken = getCurrentToken();
                            if (match("VARIABLE")) {
                                nombreVar = nombreToken.getNombre();
                                variablesDeclaradas.add(nombreToken);
                                defineVariable(nombreVar, tipo);
                            } else {
                                // No es una variable después de la coma, retroceder
                                indiceToken = checkPoint;
                                break;
                            }
                        } else {
                            // No hay coma, terminar el bucle
                            break;
                        }
                    }

                    // Si estamos en una función (no en scope global), crear tupla de declaración
                    if (currentScope != globalScope) {
                        generador.crearTuplaDeclaracion(tipoToken, variablesDeclaradas);
                        System.out.println("[PARSER] Tupla de declaración generada para función");
                    }

                    return true;
                }
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    private void defineVariable(String nombre, Tipo tipo) throws SemanticException {
        // Verificar si ya existe en el alcance actual
        if (isInCurrentScopeOnly(nombre)) {
            if (semanticEx == null) {
                semanticEx = new SemanticException("Variable '" + nombre + "' ya fue declarada en este alcance");
            }
            System.out.println("ERROR: Variable '" + nombre + "' ya declarada");
        } else {
            Variable v = new Variable(nombre, tipo);

            // 1. Definir en el Scope (para validación semántica durante el parsing)
            currentScope.define(v);
            System.out.println("[SEMANTIC] Variable definida en Scope: " + nombre + " : " + tipo.getNombre() + " en scope " + currentScope.getScopeName());

            // 2. Solo registrar variables GLOBALES en TablaSimbolos inmediatamente
            // Las variables locales de funciones se crearán mediante tuplas de declaración
            if (currentScope == globalScope) {
                ts.definirSilencioso(v);
                System.out.println("[PARSER->TS] Variable global registrada: " + nombre);
            } else {
                System.out.println("[PARSER] Variable local (se creará con tupla): " + nombre);
            }
        }
    }

    private boolean isInCurrentScopeOnly(String name) {
        if (currentScope instanceof GlobalScope) {
            return ((GlobalScope) currentScope).members.containsKey(name);
        } else if (currentScope instanceof LocalScope) {
            return ((LocalScope) currentScope).members.containsKey(name);
        } else if (currentScope instanceof FuncionSimbolo) {
            return ((FuncionSimbolo) currentScope).members.containsKey(name);
        }
        return false;
    }

    private void resolveVariable(String name) throws SemanticException {
        Simbolo sym = currentScope.resolve(name);
        if (sym == null || !(sym instanceof Variable)) {
            if (semanticEx == null) {
                semanticEx = new SemanticException("USO DE VARIABLE NO DECLARADA: La variable '" + name + "' no ha sido declarada");
            }
            System.out.println("ERROR: Variable '" + name + "' no declarada");
        } else {
            System.out.println("[SEMANTIC] Variable usada: " + name + " : " + sym.getTipo());
        }
    }

    private boolean Asignacion() throws SemanticException {
        int indiceAux = indiceToken;

        Token varToken = getCurrentToken();
        if (match("VARIABLE")) {
            resolveVariable(varToken.getNombre());
            if (match("IGUAL")) {
                // Verificar si es una llamada a función
                int indiceExpresion = indiceToken;
                Token posibleFuncion = getCurrentToken();

                if (posibleFuncion != null &&
                        posibleFuncion.getTipo().getNombre().equals("VARIABLE")) {
                    // Mirar adelante para ver si hay paréntesis
                    int indiceLookAhead = indiceToken + 1;
                    if (indiceLookAhead < tokens.size() &&
                            tokens.get(indiceLookAhead).getTipo().getNombre().equals("PARENTESISIZQ")) {
                        // Es una llamada a función
                        if (LlamadaFuncion()) {
                            // Crear tupla de asignación con función
                            generador.crearTuplaAsignacionConFuncion(varToken);
                            return true;
                        }
                    }
                }

                // No es una llamada a función, es una expresión normal
                indiceToken = indiceExpresion;
                if (Expresion()) {
                    generador.crearTuplaAsignacion(indiceAux, indiceToken);
                    return true;
                }
            }
        }
        indiceToken = indiceAux;
        return false;
    }

    private boolean Expresion() throws SemanticException {
        int indiceAux = indiceToken;

        if (Valor())
            if (match("OPARITMETICO"))
                if (Valor())
                    return true;

        indiceToken = indiceAux;
        if (Valor())
            return true;

        indiceToken = indiceAux;

        // Expresión puede ser una llamada a función
        if (LlamadaFuncion())
            return true;

        indiceToken = indiceAux;
        return false;
    }

    private boolean Valor() throws SemanticException {
        Token token = getCurrentToken();
        if (match("VARIABLE")) {
            resolveVariable(token.getNombre());
            return true;
        }
        return match("NUMERO");
    }

    private boolean Leer() throws SemanticException {
        int indiceAux = indiceToken;

        if(match("LEER")) {
            Token varToken = getCurrentToken();
            if (match("VARIABLE")) {
                resolveVariable(varToken.getNombre());
                generador.crearTuplaLeer(indiceAux+1);
                return true;
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Escribir() throws SemanticException {
        int indiceAux = indiceToken;

        if(match("ESCRIBIR"))
            if(match("CADENA"))
                if(match("COMA")) {
                    Token varToken = getCurrentToken();
                    if(match("VARIABLE")) {
                        resolveVariable(varToken.getNombre());
                        generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                        return true;
                    }
                }

        indiceToken = indiceAux;
        if (match("ESCRIBIR"))
            if (match("CADENA")) {
                generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                return true;
            }

        indiceToken = indiceAux;
        if(match("ESCRIBIR")) {
            Token varToken = getCurrentToken();
            if(match("VARIABLE")) {
                resolveVariable(varToken.getNombre());
                generador.crearTuplaEscribir(indiceAux+1, indiceToken);
                return true;
            }
        }

        indiceToken = indiceAux;
        return false;
    }

    private boolean Si() throws SemanticException {
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

    private boolean Mientras() throws SemanticException {
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

    private boolean Comparacion() throws SemanticException {
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
        if (indiceToken >= tokens.size()) {
            if (ex == null)
                ex = new SyntaxException(nombre, "END_OF_FILE");
            return false;
        }

        if (tokens.get(indiceToken).getTipo().getNombre().equals(nombre)) {
            System.out.println(nombre + ": " + tokens.get(indiceToken).getNombre());
            indiceToken++;
            return true;
        }

        if (ex == null)
            ex = new SyntaxException(nombre, tokens.get(indiceToken).getTipo().getNombre());

        return false;
    }
}