package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeSpecialistMostReviewsDTO {
    private Long id;
    private String name;
    private String description;
    private Long totalReviews;
}
