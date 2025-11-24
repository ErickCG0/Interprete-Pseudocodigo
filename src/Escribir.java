public class Escribir extends Tupla {
    Token cadena, variable;

    public Escribir(Token variableCadena, int sv, int sf) {
        super(sv, sf);

        if (variableCadena.getTipo().getNombre().equals("CADENA"))
            cadena = variableCadena;
        else
            variable = variableCadena;
    }

    public Escribir(Token cadena, Token variable, int sv, int sf) {
        super(sv, sf);
        this.cadena = cadena;
        this.variable = variable;
    }

    public String toString() {
        if (variable == null)
            return "( " + super.toString() + ", [ " + cadena + " ] )";
        if (cadena == null)
            return "( " + super.toString() + ", [ " + variable + " ] )";

        return "( " + super.toString() + ", [ " + cadena + ", " + variable + " ] )";
    }

    public int ejecutar(TablaSimbolos ts) {
        if (cadena == null) {
            Variable v = (Variable) ts.resolver(variable.getNombre());
            Object valor = v.getValor();

            if (valor instanceof Boolean) {
                System.out.println((Boolean) valor ? "verdadero" : "falso");
            } else {
                System.out.println(valor);
            }
        } else if (variable == null) {
            System.out.println(cadena.getNombre());
        } else {
            Variable v = (Variable) ts.resolver(variable.getNombre());
            Object valor = v.getValor();

            if (valor instanceof Boolean) {
                System.out.println(cadena.getNombre() + ((Boolean) valor ? "verdadero" : "falso"));
            } else {
                System.out.println(cadena.getNombre() + valor);
            }
        }

        return saltoVerdadero;
    }

}