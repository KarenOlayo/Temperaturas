public class ResultadoExtremos {

    private final String ciudadMasCalurosa;
    private final double temperaturaMax;
    private final String ciudadMasFria;
    private final double temperaturaMin;

    public ResultadoExtremos(String ciudadMasCalurosa, double temperaturaMax, String ciudadMasFria, double temperaturaMin) {
        this.ciudadMasCalurosa = ciudadMasCalurosa;
        this.temperaturaMax = temperaturaMax;
        this.ciudadMasFria = ciudadMasFria;
        this.temperaturaMin = temperaturaMin;
    }

    public String getCiudadMasCalurosa() {
        return ciudadMasCalurosa;
    }

    public double getTemperaturaMax() {
        return temperaturaMax;
    }

    public String getCiudadMasFria() {
        return ciudadMasFria;
    }

    public double getTemperaturaMin() {
        return temperaturaMin;
    }
}
