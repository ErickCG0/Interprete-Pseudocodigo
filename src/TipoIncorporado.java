public class TipoIncorporado extends Simbolo implements Tipo {
    public TipoIncorporado(String nombre) {
        super(nombre);
    }

    public String toString() {
        return getNombre();
    }
}
