package pe.edu.upc.noctuwell.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "histories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String background;
    private String allergies;
    private String medications;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "type_specialist_id")  // 👈 este es el nombre de la columna
    private TypeSpecialist typeSpecialist;
    //    @JsonIgnore
//    @OneToMany(mappedBy = "history", fetch = FetchType.LAZY)
//    private List<Diagnosis> diagnoses;
}
