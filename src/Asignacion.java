public class Asignacion extends Tupla {
    Token variable, valor1, valor2, operador;

    public Asignacion(Token variable, Token valor, int sv, int sf) {
        super(sv, sf);
        this.variable = variable;
        this.valor1 = valor;
    }

    public Asignacion(Token variable, Token valor1, Token operador, Token valor2, int sv, int sf) {
        super(sv, sf);
        this.variable = variable;
        this.valor1 = valor1;
        this.valor2 = valor2;
        this.operador = operador;
    }

    public String toString() {
        if (operador == null)
            return "( " + super.toString() + ", [ \"" + variable + ", " +
                    valor1 + "\" ] )";
        else
            return "( " + super.toString() + ", [ " + variable + ", " +
                    valor1 + ", " + operador + ", " + valor2 + " ] )";
    }

    public int ejecutar(TablaSimbolos ts) {
        Variable v = (Variable) ts.resolver(variable.getNombre());

        // Si es un valor booleano sin operador, asignar directamente
        if (operador == null) {
            if (valor1.getTipo().getNombre().equals("VERDADERO")) {
                v.setValor(true);
                return saltoVerdadero;
            } else if (valor1.getTipo().getNombre().equals("FALSO")) {
                v.setValor(false);
                return saltoVerdadero;
            } else if (valor1.getTipo().getNombre().equals("NUMERO")) {
                v.setValor(Float.parseFloat(valor1.getNombre()));
                return saltoVerdadero;
            } else if (valor1.getTipo().getNombre().equals("VARIABLE")) {
                Variable varOrigen = (Variable) ts.resolver(valor1.getNombre());
                v.setValor(varOrigen.getValor());
                return saltoVerdadero;
            }
        }

        // Operaciones aritméticas (solo con números)
        float operando1 = 0, operando2 = 0;

        if (valor1.getTipo().getNombre().equals("NUMERO"))
            operando1 = Float.parseFloat(valor1.getNombre());
        else if (valor1.getTipo().getNombre().equals("VARIABLE")) {
            Variable var1 = (Variable) ts.resolver(valor1.getNombre());
            operando1 = var1.getValorFloat();
        }

        if (valor2 != null) {
            if (valor2.getTipo().getNombre().equals("NUMERO"))
                operando2 = Float.parseFloat(valor2.getNombre());
            else if (valor2.getTipo().getNombre().equals("VARIABLE")) {
                Variable var2 = (Variable) ts.resolver(valor2.getNombre());
                operando2 = var2.getValorFloat();
            }
        }

        if (operador != null) {
            switch (operador.getNombre()) {
                case "+": v.setValor(operando1 + operando2);
                    break;
                case "-": v.setValor(operando1 - operando2);
                    break;
                case "*": v.setValor(operando1 * operando2);
                    break;
                case "/": if (operando2 != 0)
                    v.setValor(operando1 / operando2);
                else {
                    System.out.println("Error: División entre cero");
                    System.exit(1);
                }
                    break;
            }
        }

        return saltoVerdadero;
    }

}