package pe.edu.upc.noctuwell.services;

import pe.edu.upc.noctuwell.dtos.HistoryDTO;
import pe.edu.upc.noctuwell.entities.History;

import java.util.List;

public interface HistoryService {

    HistoryDTO add(HistoryDTO historyDTO, String username);

    History edit(History history);

    void delete(Long id);

    History findById(Long id);

    List<History> listAll();

    List<History> listByPatientUsername(String username);

    List<History> listBySpecialistUsername(String username);

    List<History> getHistoriesByPatientForLoggedSpecialist(Long patientId);
}
