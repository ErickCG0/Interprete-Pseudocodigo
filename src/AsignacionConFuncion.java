public class AsignacionConFuncion extends Tupla {
    Token variable;
    PseudoInterprete interprete;

    public AsignacionConFuncion(Token variable, int sv, int sf) {
        super(sv, sf);
        this.variable = variable;
    }

    public void setInterprete(PseudoInterprete interprete) {
        this.interprete = interprete;
    }

    public String toString() {
        return "( " + super.toString() + ", [ " + variable + " = VALOR_RETORNO ] )";
    }

    public int ejecutar(TablaSimbolos ts) {
        if (interprete != null && interprete.hayValorRetorno()) {
            Variable v = (Variable) ts.resolver(variable.getNombre());
            if (v != null) {
                float valorRetorno = interprete.getUltimoValorRetorno();
                v.setValor(valorRetorno);
                System.out.println("[INTERPRETE] Asignando " + variable.getNombre() + " = " + valorRetorno);
                interprete.limpiarValorRetorno();
            }
        }
        return saltoVerdadero;
    }

    public Token getVariable() {
        return variable;
    }
}