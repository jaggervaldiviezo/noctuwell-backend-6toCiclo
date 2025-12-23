package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistRatingDTO {
    private String specialistName; // Para el eje X del gráfico
    private Double averageRating;  // Para el eje Y (valor)
}