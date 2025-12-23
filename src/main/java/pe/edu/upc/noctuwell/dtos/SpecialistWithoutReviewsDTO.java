package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistWithoutReviewsDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String certification;
    private String description;
    private Integer experience;
}
