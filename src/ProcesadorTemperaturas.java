import java.nio.file.Paths;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcesadorTemperaturas {

    public static List<RegistroTemperatura> cargarDesdeCSV(String rutaArchivo) {

        List<RegistroTemperatura> registros = new ArrayList<>();

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("d/M/yyyy");

        try (Stream<String> lineas = Files.lines(Paths.get(rutaArchivo))){
            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new RegistroTemperatura(textos[0], LocalDate.parse(textos[1], formatoFecha),
                            Double.parseDouble(textos[2])))
                    .collect(Collectors.toList());
        }
        catch (Exception ex) {
            return Collections.emptyList();}
        }

    public static List<String> getCiudades(List<RegistroTemperatura> registros) {

        return registros.stream()
                .map(RegistroTemperatura::getCiudad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<RegistroTemperatura> filtrarPorFecha(LocalDate desde, LocalDate hasta, List<RegistroTemperatura> registros) {
        return registros.stream()
                .filter(registro -> !registro.getFecha().isBefore(desde) && !registro.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public static Map<String, Double> calcularTemperaturaPromedioPorCiudad(List<RegistroTemperatura> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(RegistroTemperatura::getCiudad,
                        Collectors.averagingDouble(RegistroTemperatura::getTemperatura)));
    }

    public static ResultadoExtremos temperaturasExtremasPorFecha(LocalDate fecha, List<RegistroTemperatura> registros) {
        var registrosFiltrados = registros.stream()
                .filter(registro -> registro.getFecha().isEqual(fecha))
                .collect(Collectors.toList());
        
        if (registrosFiltrados.isEmpty()) {
            return new ResultadoExtremos("No hay datos", 0, "No hay datos", 0);
        }
    
        var ciudadMasCalurosa = registrosFiltrados.stream()
                .max(Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .get();
        
        var ciudadMasFria = registrosFiltrados.stream()
                .min(Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .get();
        
        return new ResultadoExtremos(
                ciudadMasCalurosa.getCiudad(), ciudadMasCalurosa.getTemperatura(),
                ciudadMasFria.getCiudad(), ciudadMasFria.getTemperatura()
        );
    }
    
    }