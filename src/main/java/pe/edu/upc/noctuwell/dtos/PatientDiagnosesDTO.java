package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDiagnosesDTO {
    private Long patientId;
    private String firstName;
    private String lastName;
    private Long diagnosesCount;


}
