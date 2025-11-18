public class Regresa extends Tupla {
    Token valorRetorno;

    public Regresa(Token valorRetorno, int sv, int sf) {
        super(sv, sf);
        this.valorRetorno = valorRetorno;
    }

    public String toString() {
        if (valorRetorno != null) {
            return "( " + super.toString() + ", [ REGRESA " + valorRetorno + " ] )";
        }
        return "( " + super.toString() + ", [ REGRESA ] )";
    }

    public int ejecutar(TablaSimbolos ts) {
        return saltoVerdadero;
    }

    public Token getValorRetorno() {
        return valorRetorno;
    }
}