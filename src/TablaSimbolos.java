import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TablaSimbolos {
    private Map<String, Simbolo> global = new HashMap<>();

    private Stack<Map<String, Simbolo>> pilaScopes = new Stack<>();

    private Map<String, Simbolo> scopeActual = global;

    public void definir(Simbolo simbolo) throws SemanticException {
        if (scopeActual.get(simbolo.getNombre()) != null) {
            throw new SemanticException("Error: El simbolo " +
                    simbolo.getNombre() +
                    " Ya fue declarado");
        }
        scopeActual.put(simbolo.getNombre(), simbolo);
        System.out.println("[TABLA_SIMBOLOS] Definiendo: " + simbolo.getNombre() + " en scope actual");
    }

    public Simbolo resolver(String nombre) {
        Simbolo simbolo = scopeActual.get(nombre);

        if (simbolo == null && !pilaScopes.isEmpty()) {
            for (int i = pilaScopes.size() - 1; i >= 0; i--) {
                simbolo = pilaScopes.get(i).get(nombre);
                if (simbolo != null) {
                    return simbolo;
                }
            }
        }

        if (simbolo == null && scopeActual != global) {
            simbolo = global.get(nombre);
        }

        return simbolo;
    }

    public Map<String, Simbolo> getSimbolos() {
        return global;
    }

    public void pushScope() {
        pilaScopes.push(scopeActual);
        scopeActual = new HashMap<>();
        System.out.println("[TABLA_SIMBOLOS] Creando nuevo scope local. Profundidad: " + pilaScopes.size());
    }

    public void popScope() {
        if (!pilaScopes.isEmpty()) {
            scopeActual = pilaScopes.pop();
            System.out.println("[TABLA_SIMBOLOS] Restaurando scope anterior. Profundidad: " + pilaScopes.size());
        }
    }

    public boolean enScopeLocal() {
        return scopeActual != global;
    }

    public void definirEnGlobal(Simbolo simbolo) {
        global.put(simbolo.getNombre(), simbolo);
    }

    public void definirSilencioso(Simbolo simbolo) {
        scopeActual.put(simbolo.getNombre(), simbolo);
        System.out.println("[TABLA_SIMBOLOS] Definiendo silencioso: " + simbolo.getNombre());
    }
}