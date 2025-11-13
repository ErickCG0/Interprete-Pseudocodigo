public class Comparacion extends Tupla {
    Token valor1, valor2, operador;

    public Comparacion(Token valor1, Token operador, Token valor2, int sv, int sf) {
        super(sv, sf);
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.operador = operador;
    }

    public String toString() {
        return "( " + super.toString() + ", [ " + valor1 + ", " + operador + ", " + valor2 + " ] )";
    }

    public int ejecutar(TablaSimbolos ts) {
        float operando1 = 0, operando2 = 0;

        if (valor1.getTipo().getNombre().equals("NUMERO"))
            operando1 = Float.parseFloat(valor1.getNombre());
        else
            operando1 = ((Variable) ts.resolver(valor1.getNombre())).getValor();

        if (valor2.getTipo().getNombre().equals("NUMERO"))
            operando2 = Float.parseFloat(valor2.getNombre());
        else
            operando2 = ((Variable) ts.resolver(valor2.getNombre())).getValor();

        switch (operador.getNombre()) {
            case "<": return operando1 < operando2 ? saltoVerdadero : saltoFalso;
            case "<=": return operando1 <= operando2 ? saltoVerdadero : saltoFalso;
            case ">": return operando1 > operando2 ? saltoVerdadero : saltoFalso;
            case ">=": return operando1 >= operando2 ? saltoVerdadero : saltoFalso;
            case "==": return operando1 == operando2 ? saltoVerdadero : saltoFalso;
            case "!=": return operando1 != operando2 ? saltoVerdadero : saltoFalso;
        }

        return saltoVerdadero;
    }
}
