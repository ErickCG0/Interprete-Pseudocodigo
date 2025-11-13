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
            float valor = v.getValor();
            System.out.println(valor);
        } else if (variable == null) {
            System.out.println(cadena.getNombre());
        } else {
            Variable v = (Variable) ts.resolver(variable.getNombre());
            float valor = v.getValor();
            System.out.println(cadena.getNombre() + valor);
        }

        return saltoVerdadero;
    }

}
