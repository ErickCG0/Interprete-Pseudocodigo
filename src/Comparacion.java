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
        // Verificar si estamos comparando booleanos
        boolean esComparacionBooleana = false;
        boolean operando1Bool = false, operando2Bool = false;

        // Verificar valor1
        if (valor1.getTipo().getNombre().equals("VERDADERO") ||
                valor1.getTipo().getNombre().equals("FALSO")) {
            esComparacionBooleana = true;
            operando1Bool = valor1.getTipo().getNombre().equals("VERDADERO");
        } else if (valor1.getTipo().getNombre().equals("VARIABLE")) {
            Variable v1 = (Variable) ts.resolver(valor1.getNombre());
            if (v1 != null && v1.getValor() instanceof Boolean) {
                esComparacionBooleana = true;
                operando1Bool = (Boolean) v1.getValor();
            }
        }

        // Verificar valor2
        if (valor2.getTipo().getNombre().equals("VERDADERO") ||
                valor2.getTipo().getNombre().equals("FALSO")) {
            esComparacionBooleana = true;
            operando2Bool = valor2.getTipo().getNombre().equals("VERDADERO");
        } else if (valor2.getTipo().getNombre().equals("VARIABLE")) {
            Variable v2 = (Variable) ts.resolver(valor2.getNombre());
            if (v2 != null && v2.getValor() instanceof Boolean) {
                esComparacionBooleana = true;
                operando2Bool = (Boolean) v2.getValor();
            }
        }

        // Si es comparación booleana
        if (esComparacionBooleana) {
            switch (operador.getNombre()) {
                case "==": return operando1Bool == operando2Bool ? saltoVerdadero : saltoFalso;
                case "!=": return operando1Bool != operando2Bool ? saltoVerdadero : saltoFalso;
                default:
                    System.out.println("ERROR: Operador '" + operador.getNombre() +
                            "' no válido para comparación de booleanos");
                    return saltoFalso;
            }
        }

        // Comparación numérica (código original)
        float operando1 = 0, operando2 = 0;

        if (valor1.getTipo().getNombre().equals("NUMERO"))
            operando1 = Float.parseFloat(valor1.getNombre());
        else {
            Variable v1 = (Variable) ts.resolver(valor1.getNombre());
            if (v1 != null) {
                operando1 = v1.getValorFloat();
            }
        }

        if (valor2.getTipo().getNombre().equals("NUMERO"))
            operando2 = Float.parseFloat(valor2.getNombre());
        else {
            Variable v2 = (Variable) ts.resolver(valor2.getNombre());
            if (v2 != null) {
                operando2 = v2.getValorFloat();
            }
        }

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