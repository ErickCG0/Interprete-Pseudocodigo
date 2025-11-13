import java.util.ArrayList;

public class PseudoGenerador {
    private ArrayList<Tupla> tuplas = new ArrayList<>();
    ArrayList<Token> tokens;

    public PseudoGenerador(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void crearTuplaAsignacion(int indiceInicial, int indiceFinal) {
        if ( indiceFinal - indiceInicial == 3)
            tuplas.add(new Asignacion(tokens.get(indiceInicial),
                    tokens.get(indiceInicial+2),
                    tuplas.size()+1, tuplas.size()+1));
        else if (indiceFinal - indiceInicial == 5)
            tuplas.add(new Asignacion(tokens.get(indiceInicial),
                    tokens.get(indiceInicial+2),
                    tokens.get(indiceInicial+3),
                    tokens.get(indiceInicial+4),
                    tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaAsignacionRepite(int indiceVariable) {
        tuplas.add(new Asignacion(tokens.get(indiceVariable),
                tokens.get(indiceVariable + 2),
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaComparacionRepite(int indiceVariable) {
        Token operadorMenorIgual = new Token(new TipoToken(TipoToken.OPRELACIONAL, "<"), "<");
        tuplas.add(new Comparacion(tokens.get(indiceVariable),
                operadorMenorIgual,
                tokens.get(indiceVariable + 4),
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaAvanceRepite(int indiceVariable) {
        Token operadorSuma = new Token(new TipoToken(TipoToken.OPARITMETICO, "+"), "+");
        Token uno = new Token(new TipoToken(TipoToken.NUMERO, "-?[0-9]+(\\.[0-9]+)?"), "1");
        tuplas.add(new Asignacion(tokens.get(indiceVariable),
                tokens.get(indiceVariable),
                operadorSuma,
                uno,
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaLeer(int indiceInicial) {
        tuplas.add(new Leer(tokens.get(indiceInicial),
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaEscribir(int indiceInicial, int indiceFinal) {
        if (indiceFinal - indiceInicial == 1)
            tuplas.add(new Escribir(tokens.get(indiceInicial),
                    tuplas.size()+1, tuplas.size()+1));
        else if (indiceFinal - indiceInicial == 3)
            tuplas.add(new Escribir(tokens.get(indiceInicial),
                    tokens.get(indiceInicial+2),
                    tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaComparacion(int indiceInicial) {
        tuplas.add(new Comparacion(tokens.get(indiceInicial),
                tokens.get(indiceInicial+1),
                tokens.get(indiceInicial+2),
                tuplas.size()+1, tuplas.size()+1));
    }

    public void crearTuplaFinPrograma() {
        tuplas.add(new FinPrograma());
    }

    public void conectarSi(int tuplaInicial) {
        int tuplaFinal = tuplas.size()-1;

        if (tuplaInicial >= tuplas.size() || tuplaInicial >= tuplaFinal)
            return;

        tuplas.get(tuplaInicial).setSaltoFalso(tuplaFinal+1);
    }

    public void conectarMientras(int tuplaInicial) {
        int tuplaFinal = tuplas.size()-1;

        if (tuplaInicial >= tuplas.size() || tuplaInicial >= tuplaFinal)
            return;

        tuplas.get(tuplaInicial).setSaltoFalso(tuplaFinal + 1);
        tuplas.get(tuplaFinal).setSaltoVerdadero(tuplaInicial);
        tuplas.get(tuplaFinal).setSaltoFalso(tuplaInicial);

        for (int i = tuplaFinal; i > tuplaInicial; i--) {
            Tupla t = tuplas.get(i);

            if ( t instanceof Comparacion && t.getSaltoFalso() == tuplaFinal + 1)
                t.setSaltoFalso(tuplaInicial);
        }
    }

    public ArrayList<Tupla> getTuplas() {
        return tuplas;
    }
}
