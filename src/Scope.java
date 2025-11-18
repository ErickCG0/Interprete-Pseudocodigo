public interface Scope {
    String getScopeName();
    Scope getEnclosingScope();
    void define(Simbolo sym);
    Simbolo resolve(String name);
}
