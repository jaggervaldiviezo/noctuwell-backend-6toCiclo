package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeSpecialistAverageScoreDTO {
    private Long id;
    private String name;
    private String description;
    private Double averageScore;
    private Long reviewCount;
}
