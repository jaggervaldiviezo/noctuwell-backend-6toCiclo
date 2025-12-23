package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.HistoryDTO;
import pe.edu.upc.noctuwell.entities.History;
import pe.edu.upc.noctuwell.services.HistoryService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/noctuwell")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/histories")
    public ResponseEntity<List<History>> listAll( @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(historyService.listAll(), HttpStatus.OK);
    }

    @GetMapping("/histories/{id}")
    public ResponseEntity<History> findById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(historyService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/histories")
    public ResponseEntity<HistoryDTO> create(
            @RequestBody HistoryDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        HistoryDTO created = historyService.add(dto, userDetails.getUsername());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/histories")
    public ResponseEntity<History> edit(@RequestBody History history) {
        History edited = historyService.edit(history);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @DeleteMapping("/histories/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        historyService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/histories/my")
    public ResponseEntity<List<History>> listMyHistories(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<History> list = historyService.listByPatientUsername(userDetails.getUsername());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/histories/my-patients")
    public ResponseEntity<List<History>> listHistoriesOfMyPatients(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<History> list = historyService.listBySpecialistUsername(userDetails.getUsername());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/histories/patient/{patientId}/for-me")
    public ResponseEntity<List<History>> getHistoriesByPatientForLoggedSpecialist(@PathVariable Long patientId) {
        List<History> list = historyService.getHistoriesByPatientForLoggedSpecialist(patientId);
        return ResponseEntity.ok(list);
    }
}
