public class TipoToken {
    private String nombre;
    private String patron;

    public TipoToken(String nombre, String patron) {
        this.nombre = nombre;
        this.patron = patron;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPatron() {
        return patron;
    }

    public static String NUMERO = "NUMERO";
    public static String CADENA = "CADENA";
    public static String OPARITMETICO = "OPARITMETICO";
    public static String OPRELACIONAL = "OPRELACIONAL";
    public static String IGUAL = "IGUAL";
    public static String COMA = "COMA";
    public static String PUNTOS = "PUNTOS";
    public static String SEPARADOR = "SEPARADOR";
    public static String PARENTESISIZQ = "PARENTESISIZQ";
    public static String PARENTESISDER = "PARENTESISDER";
    public static String INICIOPROGRAMA = "INICIOPROGRAMA";
    public static String FINPROGRAMA = "FINPROGRAMA";
    public static String FUNCION = "FUNCION";
    public static String FINFUNCION = "FINFUNCION";
    public static String REGRESA = "REGRESA";
    public static String LEER = "LEER";
    public static String ESCRIBIR = "ESCRIBIR";
    public static String SI = "SI";
    public static String ENTONCES = "ENTONCES";
    public static String FINSI = "FINSI";
    public static String MIENTRAS = "MIENTRAS";
    public static String FINMIENTRAS = "FINMIENTRAS";
    public static String REPITE = "REPITE";
    public static String FINREPITE = "FINREPITE";
    public static String DECLARARVARIABLES = "DECLARARVARIABLES";
    public static String VARIABLE = "VARIABLE";
    public static String ENTERO = "ENTERO";
    public static String FLOTANTE = "FLOTANTE";
    public static String BOOLEANO = "BOOLEANO";
    public static String ESPACIO = "ESPACIO";
    public static String ERROR = "ERROR";
}
