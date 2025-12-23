package pe.edu.upc.noctuwell.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pe.edu.upc.noctuwell.dtos.*;
import pe.edu.upc.noctuwell.entities.Specialist;
import pe.edu.upc.noctuwell.services.SpecialistService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/noctuwell/specialists")
@CrossOrigin("*")
public class SpecialistController {

    @Autowired
    private SpecialistService specialistService;

    @GetMapping
    public ResponseEntity<List<SpecialistDTO>> listAll() {
        List<SpecialistDTO> specialists = specialistService.listAll().stream()
                .map(s -> new SpecialistDTO(
                        s.getId(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getPhone(),
                        s.getCertification(),
                        s.getDescription(),
                        s.getExperience(),
                        s.getUser().getId(),
                        s.getTypeSpecialist().getId()
                ))
                .collect(Collectors.toList());
        return new ResponseEntity<>(specialists, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Specialist> findById(@PathVariable Long id) {
        Specialist specialist = specialistService.findById(id);
        return (specialist != null)
                ? new ResponseEntity<>(specialist, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<SpecialistDTO> add(@RequestBody SpecialistDTO specialistpostDTO) {
        specialistpostDTO = specialistService.add(specialistpostDTO);
        return new ResponseEntity<>(specialistpostDTO, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<SpecialistDTO> edit(@RequestBody SpecialistDTO dto) {
        SpecialistDTO edited = specialistService.edit(dto);
        return new ResponseEntity<>(edited, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialistService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<List<SpecialistRatingDTO>> getTopRatedSpecialists(
            @RequestParam(defaultValue = "5") int limit) {

        List<SpecialistRatingDTO> list = specialistService.getTopRatedSpecialists(limit);

        if(list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/experience-ranking")
    public ResponseEntity<List<SpecialistExperienceDTO>> getExperienceRanking() {
        List<SpecialistExperienceDTO> list = specialistService.getSpecialistExperienceRanking();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/most-diagnoses")
    public ResponseEntity<List<SpecialistMostDiagnosesDTO>> getSpecialistsWithMostDiagnoses() {
        List<SpecialistMostDiagnosesDTO> list =
                specialistService.listSpecialistsWithMostDiagnoses();

        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/without-reviews")
    public ResponseEntity<List<SpecialistWithoutReviewsDTO>> getSpecialistsWithoutReviews() {
        List<SpecialistWithoutReviewsDTO> list =
                specialistService.listSpecialistsWithoutReviews();
        if (list.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/me")
    public ResponseEntity<Specialist> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        Specialist specialist = specialistService.getMyProfile(userDetails.getUsername());
        return ResponseEntity.ok(specialist);
    }


}