package pe.edu.upc.noctuwell.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class SlotDTO {
    private Long idSchedule;
    private LocalTime time;
    private boolean isAvailable;
}