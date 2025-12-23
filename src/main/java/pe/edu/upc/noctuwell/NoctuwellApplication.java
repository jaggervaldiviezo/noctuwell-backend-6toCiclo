package pe.edu.upc.noctuwell;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pe.edu.upc.noctuwell.entities.*;
import pe.edu.upc.noctuwell.repositories.*;
import pe.edu.upc.noctuwell.services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@SpringBootApplication
public class NoctuwellApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoctuwellApplication.class, args);
    }

    @Bean
    public CommandLineRunner startConfiguration(
            UserService userService,
            AuthorityService authorityService,
            PatientRepository patientRepository,
            SpecialistRepository specialistRepository,
            TypeSpecialistRepository typeSpecialistRepository,
            PlanRepository planRepository,
            HistoryRepository historyRepository,
            AppointmentRepository appointmentRepository,
            DiagnosisRepository diagnosisRepository,
            ScheduleRepository scheduleRepository,
            MessageRepository messageRepository,
            ReviewRepository reviewRepository
    ) {
        return args -> {

            // =========================================================================
            // 1. ROLES Y AUTORIDADES
            // =========================================================================

            Authority authority1 = authorityService.addAuthority(new Authority("ROLE_ADMIN"));
            Authority authority2 = authorityService.addAuthority(new Authority("ROLE_SPECIALIST"));
            Authority authority3 = authorityService.addAuthority(new Authority("ROLE_PATIENT"));
            Authority authority4 = authorityService.addAuthority(new Authority(""));

            // =========================================================================
            // 2. TIPO DE ESPECIALISTAS
            // =========================================================================

            TypeSpecialist cardio = new TypeSpecialist(null, "Cardiólogo", "Especialista en enfermedades del corazón", null);
            TypeSpecialist nutri = new TypeSpecialist(null, "Nutricionista", "Especialista en nutrición y dietética", null);
            TypeSpecialist somnologo = new TypeSpecialist(null, "Somnólogo", "Especialista en trastornos del sueño", null);

            typeSpecialistRepository.saveAll(List.of(cardio, nutri, somnologo));

            // =========================================================================
            // 3. PLANES
            // =========================================================================

            Plan basic = new Plan(null, "Plan Básico", "Consultas generales y acceso limitado", 29.99, null);
            Plan premium = new Plan(null, "Plan Premium", "Consultas ilimitadas y atención prioritaria", 59.99, null);

            planRepository.saveAll(List.of(basic, premium));

            // =========================================================================
            // 4. USUARIOS (USERS)
            // =========================================================================

            User admin = userService.addUser(new User(null, "noctu", "noctu", true, List.of(authority1)));

            User esp1User = userService.addUser(new User(null, "esp1", "esp1", true, List.of(authority2)));
            User esp2User = userService.addUser(new User(null, "esp2", "esp2", true, List.of(authority2)));
            User esp3User = userService.addUser(new User(null, "esp3", "esp3", true, List.of(authority2)));
            User esp4User = userService.addUser(new User(null, "esp4", "esp4", true, List.of(authority2)));
            User esp5User = userService.addUser(new User(null, "esp5", "esp5", true, List.of(authority2)));
            User esp6User = userService.addUser(new User(null, "esp6", "esp6", true, List.of(authority2)));

            User pac1User = userService.addUser(new User(null, "paciente1", "paciente1", true, List.of(authority3)));
            User pac2User = userService.addUser(new User(null, "paciente2", "paciente2", true, List.of(authority3)));
            User pac3User = userService.addUser(new User(null, "paciente3", "paciente3", true, List.of(authority3)));
            User pac4User = userService.addUser(new User(null, "paciente4", "paciente4", true, List.of(authority3)));

            User norol = userService.addUser(new User(null, "norol", "norol", true, List.of(authority4)));

            // =========================================================================
            // 5. ESPECIALISTAS
            // =========================================================================

            Specialist esp1 = new Specialist(null, "Carlos", "Ramírez", "987654321", "CMP12345",
                    "Cardiólogo con 10 años de experiencia", 10, esp1User, cardio, null, null, null, null, null);
            Specialist esp2 = new Specialist(null, "Lucía", "Gómez", "986541237", "CMP67890",
                    "Nutricionista deportiva", 6, esp2User, nutri, null, null, null, null, null);
            Specialist esp3 = new Specialist(null, "Daniela", "Mendez", "955333444", "CMP34345",
                    "Nutricionista y consejera ocupacional", 10, esp3User, nutri, null, null, null, null, null);
            Specialist esp4 = new Specialist(null, "Roberto", "Salazar", "944111222", "CMP99999",
                    "Nutricionista clínico sin reseñas todavía", 4, esp4User, nutri, null, null, null, null, null);
            Specialist esp5 = new Specialist(null, "Andrea", "Vargas", "933000111", "CMP50505",
                    "Somnóloga especializada en insomnio", 8, esp5User, somnologo, null, null, null, null, null);
            Specialist esp6 = new Specialist(null, "Javier", "Rojas", "922888777", "CMP60606",
                    "Especialista en apneas y ronquidos", 5, esp6User, somnologo, null, null, null, null, null);

            specialistRepository.saveAll(List.of(esp1, esp2, esp3, esp4, esp5, esp6));

            // Establecer la relación inversa en TypeSpecialist
            cardio.setSpecialists(List.of(esp1));
            nutri.setSpecialists(List.of(esp2, esp3, esp4));
            somnologo.setSpecialists(List.of(esp5, esp6));
            typeSpecialistRepository.saveAll(List.of(cardio, nutri, somnologo));

            // =========================================================================
            // 6. PACIENTES (PATIENTS)
            // =========================================================================

            Patient p1 = new Patient(null, "María", "Fernández", "F", 60, 165, "999888777",
                    LocalDate.of(1998, 5, 20), pac1User, basic, null, null, null, null);
            Patient p2 = new Patient(null, "Jorge", "Pérez", "M", 75, 178, "988777666",
                    LocalDate.of(1995, 3, 10), pac2User, premium, null, null, null, null);
            Patient p3 = new Patient(null, "Lucia", "Mendoza", "F", 55, 159, "911222333",
                    LocalDate.of(2001, 3, 19), pac3User, premium, null, null, null, null);
            Patient p4 = new Patient(null, "Diego", "Lopez", "M", 70, 172, "944555111",
                    LocalDate.of(1999, 2, 15), pac4User, basic, null, null, null, null);

            patientRepository.saveAll(List.of(p1, p2, p3, p4));

            // =========================================================================
            // 7. HISTORIALES (HISTORY) - CREACIÓN DE OBJETOS PARA REFERENCIAS FUTURAS
            // =========================================================================

            // P1 Histories
            History h1_cardio = new History(null, "Sin antecedentes cardíacos graves", "Ninguna", "Ninguno", p1, cardio);
            History h2_nutri = new History(null, "Intolerancia a la lactosa, dieta a base de plantas", "Ninguna", "Suplementos B12", p1, nutri);

            // P2 Histories
            History h3_cardio = new History(null, "Hipertensión controlada, antecedentes familiares", "Polvo", "Losartán", p2, cardio);
            History h4_nutri = new History(null, "Dieta deportiva enfocada en rendimiento", "Ninguna", "Proteína whey", p2, nutri);
            History h5_somno_p2 = new History(null, "Riesgo de apnea del sueño leve", "Ninguna", "Ninguno", p2, somnologo);

            // P3 Histories
            History h6_nutri_p3 = new History(null, "Estrés laboral y dieta irregular, buscando equilibrio", "Ninguna", "Ninguno", p3, nutri);
            History h7_cardio_p3 = new History(null, "Sin antecedentes, chequeos preventivos", "Ninguna", "Ninguno", p3, cardio);

            // P4 Histories
            History h8_somno = new History(null, "Problemas de sueño ocasionales, dificultad para conciliar", "Ninguna", "Ninguno", p4, somnologo);


            historyRepository.saveAll(List.of(h1_cardio, h2_nutri, h3_cardio, h4_nutri, h5_somno_p2, h6_nutri_p3, h7_cardio_p3, h8_somno));


            // =========================================================================
            // 8. CITAS (APPOINTMENTS)
            // =========================================================================

            // esp1 (Cardio) - p1, p2, p3
            Appointment a1 = new Appointment(null, LocalDate.of(2025, 10, 1), LocalTime.of(9, 0), "Control de ansiedad/chequeo", "AGENDADO", p1, esp1, null, null);
            Appointment a2 = new Appointment(null, LocalDate.of(2025, 10, 2), LocalTime.of(10, 0), "Evaluación inicial", "COMPLETO", p2, esp1, null, null);
            Appointment a7 = new Appointment(null, LocalDate.of(2025, 10, 7), LocalTime.of(10, 0), "Chequeo post tratamiento", "COMPLETO", p1, esp1, null, null);
            Appointment a12 = new Appointment(null, LocalDate.of(2025, 10, 12), LocalTime.of(15, 0), "Chequeo cardiológico completo", "COMPLETO", p3, esp1, null, null);

            // esp2 (Nutri) - p3, p2
            Appointment a3 = new Appointment(null, LocalDate.of(2025, 10, 3), LocalTime.of(15, 0), "Seguimiento de tratamiento nutricional", "AGENDADO", p3, esp2, null, null);
            Appointment a8 = new Appointment(null, LocalDate.of(2025, 10, 8), LocalTime.of(11, 0), "Control nutricional", "COMPLETO", p2, esp2, null, null);
            Appointment a11 = new Appointment(null, LocalDate.of(2025, 10, 11), LocalTime.of(13, 0), "Nutrición deportiva avanzada", "COMPLETO", p2, esp2, null, null);

            // esp3 (Nutri - Daniela) - p1, p2, p3
            Appointment a4 = new Appointment(null, LocalDate.of(2025, 10, 4), LocalTime.of(11, 0), "Revisión plan nutricional familiar", "AGENDADO", p1, esp3, null, null);
            Appointment a5 = new Appointment(null, LocalDate.of(2025, 10, 5), LocalTime.of(14, 0), "Consulta de hábitos alimenticios", "COMPLETO", p2, esp3, null, null);
            Appointment a6 = new Appointment(null, LocalDate.of(2025, 10, 6), LocalTime.of(16, 0), "Consulta nutricional", "COMPLETO", p1, esp3, null, null);
            Appointment a9 = new Appointment(null, LocalDate.of(2025, 10, 9), LocalTime.of(9, 0), "Evaluación de dieta por estrés", "COMPLETO", p3, esp3, null, null);
            Appointment a10 = new Appointment(null, LocalDate.of(2025, 10, 10), LocalTime.of(14, 0), "Seguimiento de plan de alimentación", "COMPLETO", p1, esp3, null, null);

            // esp5 (Somnólogo) - p4
            Appointment a13 = new Appointment(null, LocalDate.of(2025, 11, 1), LocalTime.of(9, 30), "Consulta inicial por insomnio", "AGENDADO", p4, esp5, null, null);
            Appointment a14 = new Appointment(null, LocalDate.of(2025, 11, 2), LocalTime.of(11, 0), "Polisomnografía (Resultados)", "COMPLETO", p4, esp5, null, null);

            // esp6 (Somnólogo) - p2
            Appointment a15 = new Appointment(null, LocalDate.of(2025, 11, 3), LocalTime.of(14, 30), "Evaluación de apnea del sueño", "COMPLETO", p2, esp6, null, null);
            Appointment a16 = new Appointment(null, LocalDate.of(2025, 11, 4), LocalTime.of(16, 0), "Seguimiento terapia CPAP", "AGENDADO", p2, esp6, null, null);

            appointmentRepository.saveAll(List.of(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16));

            // =========================================================================
// 9. DIAGNÓSTICOS (DIAGNOSES) - ¡ACTUALIZADO CON HISTORY_ID Y PATIENT!
// =========================================================================

// Nuevo Constructor Asumido: id, description, type, recommendations, date, appointment, history, specialist, patient

// Diagnósticos de esp1 (Cardio) -> P: p1, p2, p3
            Diagnosis d1 = new Diagnosis(null, "Presión arterial estable", "Cardiología", "Continuar con chequeos regulares", LocalDate.now(), a1, h1_cardio, esp1, p1); // p1: h1_cardio
            Diagnosis d2 = new Diagnosis(null, "Evaluación inicial (normal)", "Cardiología", "Continuar hábitos saludables", LocalDate.now(), a2, h3_cardio, esp1, p2); // p2: h3_cardio
            Diagnosis d3 = new Diagnosis(null, "Control estable", "Cardiología", "Continuar medicación", LocalDate.now(), a7, h1_cardio, esp1, p1); // p1: h1_cardio
            Diagnosis d4 = new Diagnosis(null, "Presión arterial normal", "Cardiología", "Revisar en 3 meses", LocalDate.now(), a12, h7_cardio_p3, esp1, p3); // p3: h7_cardio_p3

// Diagnósticos de esp2 (Nutri) -> P: p2
            Diagnosis d6 = new Diagnosis(null, "Plan nutricional para deportistas", "Nutrición", "Reducir carbohidratos", LocalDate.now(), a8, h4_nutri, esp2, p2); // p2: h4_nutri
            Diagnosis d9 = new Diagnosis(null, "Mejora nutricional", "Nutrición", "Incrementar proteínas", LocalDate.now(), a11, h4_nutri, esp2, p2); // p2: h4_nutri

// Diagnósticos de esp3 (Nutri - Daniela) -> P: p2, p3, p1
            Diagnosis d5 = new Diagnosis(null, "Hábitos alimenticios por mejorar", "Nutrición", "Recomendación: dieta mediterránea", LocalDate.now(), a5, h4_nutri, esp3, p2); // p2: h4_nutri
            Diagnosis d7 = new Diagnosis(null, "Dieta afectada por estrés", "Nutrición", "Recomendación de comidas calmantes", LocalDate.now(), a9, h6_nutri_p3, esp3, p3); // p3: h6_nutri_p3
            Diagnosis d8 = new Diagnosis(null, "Progreso positivo en dieta", "Nutrición", "Continuar plan de alimentación", LocalDate.now(), a10, h2_nutri, esp3, p1); // p1: h2_nutri
            Diagnosis d12 = new Diagnosis(null, "Revisión de peso y nutrientes", "Nutrición", "Mantener ingesta de fibra", LocalDate.now(), a6, h2_nutri, esp3, p1); // p1: h2_nutri

// Diagnósticos de esp5 (Somnólogo) -> P: p4
            Diagnosis d10 = new Diagnosis(null, "Higiene del sueño deficiente", "Somnología", "Recomendación: establecer rutina", LocalDate.now(), a14, h8_somno, esp5, p4); // p4: h8_somno

// Diagnósticos de esp6 (Somnólogo) -> P: p2
            Diagnosis d11 = new Diagnosis(null, "Apnea obstructiva del sueño leve", "Somnología", "Iniciar terapia CPAP", LocalDate.now(), a15, h5_somno_p2, esp6, p2); // p2: h5_somno_p2

            diagnosisRepository.saveAll(List.of(d1, d2, d3, d4, d5, d6, d7, d8, d9, d10, d11, d12));

            // Establecer la relación inversa en Appointment (Solo para citas COMPLETO)
            a1.setDiagnoses(List.of(d1));
            a2.setDiagnoses(List.of(d2));
            a5.setDiagnoses(List.of(d5));
            a6.setDiagnoses(List.of(d12));
            a7.setDiagnoses(List.of(d3));
            a8.setDiagnoses(List.of(d6));
            a9.setDiagnoses(List.of(d7));
            a10.setDiagnoses(List.of(d8));
            a11.setDiagnoses(List.of(d9));
            a12.setDiagnoses(List.of(d4));
            a14.setDiagnoses(List.of(d10));
            a15.setDiagnoses(List.of(d11));

            // =========================================================================
            // 10. HORARIOS (SCHEDULES)
            // =========================================================================

            Schedule s1 = new Schedule(null, LocalTime.of(8, 0), esp1);
            Schedule s2 = new Schedule(null, LocalTime.of(9, 0), esp2);
            Schedule s3 = new Schedule(null, LocalTime.of(8, 0), esp3);
            Schedule s4 = new Schedule(null, LocalTime.of(9, 0), esp1);
            Schedule s5 = new Schedule(null, LocalTime.of(10, 0), esp2);
            Schedule s6 = new Schedule(null, LocalTime.of(10, 30), esp5);
            Schedule s7 = new Schedule(null, LocalTime.of(11, 0), esp6);

            scheduleRepository.saveAll(List.of(s1, s2, s3, s4, s5, s6, s7));

            // =========================================================================
            // 11. MENSAJES (MESSAGES)
            // =========================================================================

            Message m1 = new Message(null, "Buenos días doctor, tengo dudas sobre mi cita.", LocalDateTime.now(), "patient1", a1, p1, esp1);
            Message m2 = new Message(null, "Claro, revisemos su historial en la próxima consulta.", LocalDateTime.now(), "doc1", a1, p1, esp1);

            Message m3 = new Message(null, "Doctor, puedo hacer ejercicio hoy?", LocalDateTime.now(), "patient1", a7, p1, esp1);
            Message m4 = new Message(null, "Sí, pero de forma moderada.", LocalDateTime.now(), "doc1", a7, p1, esp1);

            Message m5 = new Message(null, "¿Debo cambiar mi dieta ahora?", LocalDateTime.now(), "patient2", a8, p2, esp2);
            Message m6 = new Message(null, "Vamos a ajustarla un poco.", LocalDateTime.now(), "doc2", a8, p2, esp2);

            Message m7 = new Message(null, "Tengo dudas sobre mi plan nutricional.", LocalDateTime.now(), "patient3", a9, p3, esp3);
            Message m8 = new Message(null, "Lo revisamos mañana en consulta, no se preocupe.", LocalDateTime.now(), "doc3", a9, p3, esp3);

            Message m9 = new Message(null, "Doctora, ¿qué puedo hacer para conciliar el sueño?", LocalDateTime.now(), "patient4", a13, p4, esp5);
            Message m10 = new Message(null, "Comience por evitar pantallas una hora antes de dormir.", LocalDateTime.now(), "doc5", a13, p4, esp5);

            messageRepository.saveAll(List.of(m1, m2, m3, m4, m5, m6, m7, m8, m9, m10));

            // Establecer la relación inversa en Appointment
            a1.setMessages(List.of(m1, m2));
            a7.setMessages(List.of(m3, m4));
            a8.setMessages(List.of(m5, m6));
            a9.setMessages(List.of(m7, m8));
            a13.setMessages(List.of(m9, m10));

            // =========================================================================
            // 12. RESEÑAS (REVIEWS)
            // =========================================================================

            Review r1 = new Review(null, "Excelente atención, muy profesional.", LocalDate.now(), 5L, p1, esp1);
            Review r2 = new Review(null, "Muy amable y atenta.", LocalDate.now(), 4L, p2, esp2);
            Review r3 = new Review(null, "Muy profesional y detallado.", LocalDate.now(), 5L, p1, esp1);
            Review r4 = new Review(null, "Me ayudó bastante con mi dieta.", LocalDate.now(), 4L, p2, esp2);
            Review r5 = new Review(null, "Buen trato y claridad en explicaciones de mi dieta.", LocalDate.now(), 5L, p3, esp3);
            Review r6 = new Review(null, "Recomiendo su plan nutricional.", LocalDate.now(), 4L, p1, esp3);
            Review r7 = new Review(null, "Chequeo rápido y eficiente.", LocalDate.now(), 5L, p2, esp1);
            Review r8 = new Review(null, "Me ayudó a entender mi problema de sueño.", LocalDate.now(), 5L, p4, esp5);
            Review r9 = new Review(null, "Información muy técnica, pero útil.", LocalDate.now(), 4L, p2, esp6);

            reviewRepository.saveAll(List.of(r1, r2, r3, r4, r5, r6, r7, r8, r9));

            // =========================================================================
            // 13. RELACIONES FINALES (Para consistencia en las entidades)
            // =========================================================================

            // Pacientes con TODAS sus citas
            p1.setAppointments(List.of(a1, a4, a6, a7, a10));
            p2.setAppointments(List.of(a2, a5, a8, a11, a15, a16));
            p3.setAppointments(List.of(a3, a9, a12));
            p4.setAppointments(List.of(a13, a14));

            // Especialistas con TODAS sus citas
            esp1.setAppointments(List.of(a1, a2, a7, a12));
            esp2.setAppointments(List.of(a3, a8, a11));
            esp3.setAppointments(List.of(a4, a5, a6, a9, a10));
            esp5.setAppointments(List.of(a13, a14));
            esp6.setAppointments(List.of(a15, a16));

            // Persistir los cambios en las listas de relaciones
            patientRepository.saveAll(List.of(p1, p2, p3, p4));
            specialistRepository.saveAll(List.of(esp1, esp2, esp3, esp4, esp5, esp6));

            // Asegurar que todas las entidades con relaciones complejas sean persistidas/actualizadas
            appointmentRepository.saveAll(List.of(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16));
        };
    }

}
