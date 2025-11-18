import java.util.LinkedHashMap;
import java.util.Map;

public class FuncionSimbolo extends Simbolo implements Scope {
    Scope enclosingScope;
    Map<String, Simbolo> members = new LinkedHashMap<String, Simbolo>();
    Tipo tipoRetorno;

    public FuncionSimbolo(String nombre, Tipo tipoRetorno, Scope enclosingScope) {
        super(nombre);
        this.tipoRetorno = tipoRetorno;
        this.enclosingScope = enclosingScope;
    }

    public String getScopeName() {
        return nombre;
    }

    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    public void define(Simbolo sym) {
        members.put(sym.getNombre(), sym);
    }

    public Simbolo resolve(String name) {
        Simbolo s = members.get(name);

        if (s != null) {
            return s;
        }

        if (enclosingScope != null)
            return enclosingScope.resolve(name);

        return null;
    }

    public Tipo getTipoRetorno() {
        return tipoRetorno;
    }

    public String toString() {
        return "funcion " + nombre + " : " + tipoRetorno;
    }
}
