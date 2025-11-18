import java.util.ArrayList;

public class InicioFuncion extends Tupla {
    Token nombreFuncion;
    ArrayList<Token> parametros;

    public InicioFuncion(Token nombreFuncion, ArrayList<Token> parametros, int sv, int sf) {
        super(sv, sf);
        this.nombreFuncion = nombreFuncion;
        this.parametros = parametros;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ").append(super.toString()).append(", [ ")
                .append("INICIO_").append(nombreFuncion.getNombre()).append("(");

        for (int i = 0; i < parametros.size(); i++) {
            sb.append(parametros.get(i).getNombre());
            if (i < parametros.size() - 1) sb.append(", ");
        }
        sb.append(") ] )");
        return sb.toString();
    }

    public int ejecutar(TablaSimbolos ts) {
        return saltoVerdadero;
    }

    public Token getNombreFuncion() {
        return nombreFuncion;
    }

    public ArrayList<Token> getParametros() {
        return parametros;
    }
}