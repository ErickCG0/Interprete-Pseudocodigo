import java.util.ArrayList;

public class PseudoInterprete {
    TablaSimbolos ts;

    public PseudoInterprete(TablaSimbolos ts) {
        this.ts = ts;
    }

    public void interpretar(ArrayList<Tupla> tuplas) {
        int indiceTupla = 0;
        Tupla t = tuplas.get(0);

        do {
            indiceTupla = t.ejecutar(ts);
            t = tuplas.get(indiceTupla);
        } while (!(t instanceof FinPrograma));
    }
}
