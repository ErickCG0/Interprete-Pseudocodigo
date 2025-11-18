import java.util.HashMap;
import java.util.Map;

public abstract class BaseScope implements Scope {
    Scope enclosingScope;
    Map<String, Simbolo> members = new HashMap<String, Simbolo>();

    public BaseScope(Scope currentScope) {
        this.enclosingScope = currentScope;
    }

    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    public void define(Simbolo sym) {
        members.put(sym.getNombre(), sym);
    }

    public Simbolo resolve(String name) {
        Simbolo s = members.get(name);
        if (s != null)
            return s;

        if (enclosingScope != null)
            return enclosingScope.resolve(name);

        return null;
    }
}