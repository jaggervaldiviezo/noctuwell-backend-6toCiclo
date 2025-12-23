package pe.edu.upc.noctuwell.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateDTO {
    private String comment;
    private Long calificacion;
    private Long specialistId;

    // Getters y setters
}
