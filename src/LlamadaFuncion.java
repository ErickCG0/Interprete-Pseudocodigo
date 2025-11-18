import java.util.ArrayList;

public class LlamadaFuncion extends Tupla {
    Token nombreFuncion;
    ArrayList<Token> argumentos;
    int direccionRetorno;

    public LlamadaFuncion(Token nombreFuncion, ArrayList<Token> argumentos, int sv, int sf) {
        super(sv, sf);
        this.nombreFuncion = nombreFuncion;
        this.argumentos = argumentos;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ").append(super.toString()).append(", [ ")
                .append(nombreFuncion.getNombre()).append("(");

        for (int i = 0; i < argumentos.size(); i++) {
            sb.append(argumentos.get(i).getNombre());
            if (i < argumentos.size() - 1) sb.append(", ");
        }
        sb.append(") ] )");
        return sb.toString();
    }

    public int ejecutar(TablaSimbolos ts) {
        direccionRetorno = saltoVerdadero;

        return saltoVerdadero;
    }

    public Token getNombreFuncion() {
        return nombreFuncion;
    }

    public ArrayList<Token> getArgumentos() {
        return argumentos;
    }

    public int getDireccionRetorno() {
        return direccionRetorno;
    }
}