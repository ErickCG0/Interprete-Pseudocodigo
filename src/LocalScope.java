public class LocalScope extends BaseScope {
    public LocalScope(Scope currentScope) {
        super(currentScope);
    }

    public String getScopeName() {
        return "local";
    }
}
