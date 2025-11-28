import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PseudoInterprete {
    TablaSimbolos ts;
    ArrayList<Tupla> tuplas;
    Map<String, Integer> direccionesFunciones;

    // Pila de llamadas para manejar recursión
    private Stack<ContextoLlamada> pilaLlamadas = new Stack<>();

    // Variable temporal para almacenar el último valor de retorno
    private Object ultimoValorRetorno = 0.0f;
    private boolean hayValorRetorno = false;

    public PseudoInterprete(TablaSimbolos ts, ArrayList<Tupla> tuplas, Map<String, Integer> direccionesFunciones) {
        this.ts = ts;
        this.tuplas = tuplas;
        this.direccionesFunciones = direccionesFunciones;
    }

    public void interpretar() {

        for (Tupla t : tuplas) {
            if (t instanceof AsignacionConFuncion) {
                ((AsignacionConFuncion) t).setInterprete(this);
            }
        }


        int indiceTupla = buscarInicioProgramaPrincipal();

        if (indiceTupla == -1) {
            System.out.println("Error: No se encontró el programa principal");
            return;
        }

        Tupla t = tuplas.get(indiceTupla);

        do {


            if (t instanceof LlamadaFuncion) {
                indiceTupla = ejecutarLlamadaFuncion((LlamadaFuncion) t, indiceTupla);
            } else if (t instanceof Regresa) {
                indiceTupla = ejecutarRegresa((Regresa) t);
            } else if (t instanceof InicioFuncion) {
                // Si llegamos a un InicioFuncion desde el flujo normal, lo saltamos
                // (solo se ejecuta mediante llamadas)
                indiceTupla = saltarFuncion(indiceTupla);
            } else {
                indiceTupla = t.ejecutar(ts);
            }

            if (indiceTupla >= 0 && indiceTupla < tuplas.size()) {
                t = tuplas.get(indiceTupla);
            } else {
                break;
            }
        } while (!(t instanceof FinPrograma));

        System.out.println("\n[INTERPRETE] Programa finalizado exitosamente.");
    }

    private int buscarInicioProgramaPrincipal() {
        // Buscar todas las funciones primero
        int ultimoRegresa = -1;
        for (int i = 0; i < tuplas.size(); i++) {
            if (tuplas.get(i) instanceof Regresa) {
                ultimoRegresa = i;
            }
        }

        // El programa principal empieza después del último regresa
        if (ultimoRegresa >= 0 && ultimoRegresa + 1 < tuplas.size()) {
            return ultimoRegresa + 1;
        }

        return 0;
    }

    private int saltarFuncion(int indiceInicio) {
        // Saltar hasta encontrar el Regresa correspondiente
        for (int i = indiceInicio + 1; i < tuplas.size(); i++) {
            if (tuplas.get(i) instanceof Regresa) {
                return i + 1; // Saltar después del Regresa
            }
        }
        return indiceInicio + 1;
    }

    private int ejecutarLlamadaFuncion(LlamadaFuncion llamada, int direccionRetorno) {
        String nombreFuncion = llamada.getNombreFuncion().getNombre();
        ArrayList<Token> argumentos = llamada.getArgumentos();

       // System.out.println("[INTERPRETE] Llamando función: " + nombreFuncion);

        // Obtener la dirección de la función
        Integer direccionFuncion = direccionesFunciones.get(nombreFuncion);
        if (direccionFuncion == null) {
            System.out.println("Error: Función '" + nombreFuncion + "' no encontrada");
            return direccionRetorno + 1;
        }

        // Obtener la tupla de inicio de función
        Tupla tuplaFuncion = tuplas.get(direccionFuncion);
        if (!(tuplaFuncion instanceof InicioFuncion)) {
            System.out.println("Error: No se encontró el inicio de la función '" + nombreFuncion + "'");
            return direccionRetorno + 1;
        }

        InicioFuncion inicioFuncion = (InicioFuncion) tuplaFuncion;
        ArrayList<Token> parametros = inicioFuncion.getParametros();


        ts.pushScope();

        // Guardar el contexto actual
        ContextoLlamada contexto = new ContextoLlamada(nombreFuncion, direccionRetorno + 1);
        pilaLlamadas.push(contexto);

        // Definir parámetros en el scope local y asignar valores de argumentos
        for (int i = 0; i < Math.min(argumentos.size(), parametros.size()); i++) {
            Token argToken = argumentos.get(i);
            Token paramToken = parametros.get(i);

            Object valorArgumento = obtenerValorToken(argToken);

            // Crear variable local para el parámetro
            Variable paramVar = new Variable(paramToken.getNombre(), null);
            paramVar.setValor(valorArgumento);

            // Definir en el scope local usando definirSilencioso
            ts.definirSilencioso(paramVar);


        }

        // Saltar a la primera instrucción de la función (después de InicioFuncion)
        return direccionFuncion + 1;
    }

    private int ejecutarRegresa(Regresa regresa) {
        Token valorRetorno = regresa.getValorRetorno();

        if (pilaLlamadas.isEmpty()) {
            //System.out.println("[INTERPRETE] Regresa sin contexto de función (fin de función en programa principal)");
            return regresa.getSaltoVerdadero();
        }

        ContextoLlamada contexto = pilaLlamadas.pop();

        // Guardar el valor de retorno
        if (valorRetorno != null) {
            ultimoValorRetorno = obtenerValorToken(valorRetorno);
            hayValorRetorno = true;
            /*System.out.println("[INTERPRETE] Función " + contexto.getNombreFuncion() +
                    " retorna: " + ultimoValorRetorno);*/
        }

        // *** DESTRUIR SCOPE LOCAL ***
        ts.popScope();

        // Retornar a la dirección de retorno
        return contexto.getDireccionRetorno();
    }

    private Object obtenerValorToken(Token token) {
        if (token.getTipo().getNombre().equals("NUMERO")) {
            return Float.parseFloat(token.getNombre());
        } else if (token.getTipo().getNombre().equals("VERDADERO")) {
            return true;
        } else if (token.getTipo().getNombre().equals("FALSO")) {
            return false;
        } else if (token.getTipo().getNombre().equals("VARIABLE")) {
            Variable v = (Variable) ts.resolver(token.getNombre());
            if (v != null) {
                return v.getValor();
            }
        }
        return 0.0f;
    }

    public Object getUltimoValorRetorno() {
        return ultimoValorRetorno;
    }

    public boolean hayValorRetorno() {
        return hayValorRetorno;
    }

    public void limpiarValorRetorno() {
        hayValorRetorno = false;
        ultimoValorRetorno = 0.0f;
    }

    // Clase interna para almacenar contexto de llamadas
    private static class ContextoLlamada {
        private String nombreFuncion;
        private int direccionRetorno;

        public ContextoLlamada(String nombreFuncion, int direccionRetorno) {
            this.nombreFuncion = nombreFuncion;
            this.direccionRetorno = direccionRetorno;
        }

        public String getNombreFuncion() {
            return nombreFuncion;
        }

        public int getDireccionRetorno() {
            return direccionRetorno;
        }
    }
}