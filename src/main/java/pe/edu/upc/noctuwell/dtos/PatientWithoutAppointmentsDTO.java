package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientWithoutAppointmentsDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String phone;
    private LocalDate birthDate;
}
