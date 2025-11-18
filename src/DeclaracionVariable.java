import java.util.ArrayList;

public class DeclaracionVariable extends Tupla {
    ArrayList<Token> variables;
    Token tipoToken;

    public DeclaracionVariable(Token tipoToken, ArrayList<Token> variables, int sv, int sf) {
        super(sv, sf);
        this.tipoToken = tipoToken;
        this.variables = variables;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("( ").append(super.toString()).append(", [ DECLARAR ");
        sb.append(tipoToken.getNombre()).append(": ");
        for (int i = 0; i < variables.size(); i++) {
            sb.append(variables.get(i).getNombre());
            if (i < variables.size() - 1) sb.append(", ");
        }
        sb.append(" ] )");
        return sb.toString();
    }

    public int ejecutar(TablaSimbolos ts) {
        for (Token varToken : variables) {
            Variable v = new Variable(varToken.getNombre(), null);
            v.setValor(0.0f);
            ts.definirSilencioso(v);
            System.out.println("[EJECUTANDO] Variable creada: " + varToken.getNombre() + " = 0.0");
        }
        return saltoVerdadero;
    }

    public ArrayList<Token> getVariables() {
        return variables;
    }

    public Token getTipoToken() {
        return tipoToken;
    }
}
