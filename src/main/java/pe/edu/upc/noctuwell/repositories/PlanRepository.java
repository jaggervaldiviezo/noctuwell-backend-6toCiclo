package pe.edu.upc.noctuwell.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.upc.noctuwell.entities.Plan;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query(value = """
        SELECT 
            p.id          AS planId,
            p.name        AS plan_name,
            COUNT(pa.id)  AS total_patients
        FROM 
            plans p
            LEFT JOIN patients pa ON pa.plan_id = p.id
        GROUP BY 
            p.id, p.name
        ORDER BY 
            total_patients DESC
    """, nativeQuery = true)
    List<Object[]> findPlanStatisticsRaw();
}
