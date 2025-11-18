import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PseudoGenerador {
    private ArrayList<Tupla> tuplas = new ArrayList<>();
    ArrayList<Token> tokens;
    private Map<String, Integer> direccionesFunciones = new HashMap<>();

    public PseudoGenerador(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void crearTuplaInicioFuncion(Token nombreFuncion, ArrayList<Token> parametros) {
        // Guardar la dirección donde comienza la función
        direccionesFunciones.put(nombreFuncion.getNombre(), tuplas.size());

        tuplas.add(new InicioFuncion(nombreFuncion, parametros,
                tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaRegresa(int indiceValor) {
        Token valorRetorno = null;
        if (indiceValor >= 0 && indiceValor < tokens.size()) {
            valorRetorno = tokens.get(indiceValor);
        }
        tuplas.add(new Regresa(valorRetorno, tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaLlamadaFuncion(Token nombreFuncion, ArrayList<Token> argumentos) {
        Integer direccionFuncion = direccionesFunciones.get(nombreFuncion.getNombre());
        int saltoFuncion = (direccionFuncion != null) ? direccionFuncion : tuplas.size() + 1;

        LlamadaFuncion llamada = new LlamadaFuncion(nombreFuncion, argumentos,
                saltoFuncion, tuplas.size() + 1);
        tuplas.add(llamada);
    }

    public void actualizarDireccionRetorno(int indiceTupla, int direccion) {
        if (indiceTupla >= 0 && indiceTupla < tuplas.size()) {
            Tupla t = tuplas.get(indiceTupla);
            if (t instanceof LlamadaFuncion) {
                ((LlamadaFuncion) t).setSaltoVerdadero(direccion);
            }
        }
    }

    public void crearTuplaAsignacionConFuncion(Token variable) {
        tuplas.add(new AsignacionConFuncion(variable, tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaAsignacion(int indiceInicial, int indiceFinal) {
        if (indiceFinal - indiceInicial == 3)
            tuplas.add(new Asignacion(tokens.get(indiceInicial),
                    tokens.get(indiceInicial + 2),
                    tuplas.size() + 1, tuplas.size() + 1));
        else if (indiceFinal - indiceInicial == 5)
            tuplas.add(new Asignacion(tokens.get(indiceInicial),
                    tokens.get(indiceInicial + 2),
                    tokens.get(indiceInicial + 3),
                    tokens.get(indiceInicial + 4),
                    tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaAsignacionRepite(int indiceVariable) {
        tuplas.add(new Asignacion(tokens.get(indiceVariable),
                tokens.get(indiceVariable + 2),
                tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaComparacionRepite(int indiceVariable) {
        Token operadorMenorIgual = new Token(new TipoToken(TipoToken.OPRELACIONAL, "<="), "<=");
        tuplas.add(new Comparacion(tokens.get(indiceVariable),
                operadorMenorIgual,
                tokens.get(indiceVariable + 4),
                tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaAvanceRepite(int indiceVariable) {
        Token operadorSuma = new Token(new TipoToken(TipoToken.OPARITMETICO, "+"), "+");
        Token uno = new Token(new TipoToken(TipoToken.NUMERO, "-?[0-9]+(\\.[0-9]+)?"), "1");
        tuplas.add(new Asignacion(tokens.get(indiceVariable),
                tokens.get(indiceVariable),
                operadorSuma,
                uno,
                tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaLeer(int indiceInicial) {
        tuplas.add(new Leer(tokens.get(indiceInicial),
                tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaEscribir(int indiceInicial, int indiceFinal) {
        if (indiceFinal - indiceInicial == 1)
            tuplas.add(new Escribir(tokens.get(indiceInicial),
                    tuplas.size() + 1, tuplas.size() + 1));
        else if (indiceFinal - indiceInicial == 3)
            tuplas.add(new Escribir(tokens.get(indiceInicial),
                    tokens.get(indiceInicial + 2),
                    tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaComparacion(int indiceInicial) {
        tuplas.add(new Comparacion(tokens.get(indiceInicial),
                tokens.get(indiceInicial + 1),
                tokens.get(indiceInicial + 2),
                tuplas.size() + 1, tuplas.size() + 1));
    }

    public void crearTuplaFinPrograma() {
        tuplas.add(new FinPrograma());
    }

    public void conectarSi(int tuplaInicial) {
        int tuplaFinal = tuplas.size() - 1;

        if (tuplaInicial >= tuplas.size() || tuplaInicial >= tuplaFinal)
            return;

        tuplas.get(tuplaInicial).setSaltoFalso(tuplaFinal + 1);
    }

    public void conectarMientras(int tuplaInicial) {
        int tuplaFinal = tuplas.size() - 1;

        if (tuplaInicial >= tuplas.size() || tuplaInicial >= tuplaFinal)
            return;

        tuplas.get(tuplaInicial).setSaltoFalso(tuplaFinal + 1);
        tuplas.get(tuplaFinal).setSaltoVerdadero(tuplaInicial);
        tuplas.get(tuplaFinal).setSaltoFalso(tuplaInicial);

        for (int i = tuplaFinal; i > tuplaInicial; i--) {
            Tupla t = tuplas.get(i);

            if (t instanceof Comparacion && t.getSaltoFalso() == tuplaFinal + 1)
                t.setSaltoFalso(tuplaInicial);
        }
    }

    public ArrayList<Tupla> getTuplas() {
        return tuplas;
    }

    public Map<String, Integer> getDireccionesFunciones() {
        return direccionesFunciones;
    }

    public void crearTuplaDeclaracion(Token tipoToken, ArrayList<Token> variables) {
        tuplas.add(new DeclaracionVariable(tipoToken, variables,
                tuplas.size() + 1, tuplas.size() + 1));
        System.out.println("[GENERADOR] Tupla de declaración creada para: " + variables);
    }
}