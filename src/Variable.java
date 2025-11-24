public class Variable extends Simbolo {
    private Object valor = 0.0f;  // Puede ser Float o Boolean

    public Variable(String nombre, Tipo tipo) {
        super(nombre, tipo);
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Object getValor() {
        return valor;
    }
    
    public float getValorFloat() {
        if (valor instanceof Number) {
            return ((Number) valor).floatValue();
        }
        return 0.0f;
    }
}