import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {
    private Map<String, Simbolo> simbolos = new HashMap<String, Simbolo>();

    public void definir(Simbolo simbolo) throws SemanticException {
        if (simbolos.get(simbolo.getNombre()) == null)
            simbolos.put(simbolo.getNombre(), simbolo);
        else {
            throw new SemanticException("Error: El simbolo " +
                    simbolo.getNombre() +
                    " Ya fue declarado");
        }
    }

    public Simbolo resolver(String nombre) {
        return simbolos.get(nombre);
    }

    public Map<String, Simbolo> getSimbolos() {
        return simbolos;
    }

}
