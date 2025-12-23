package pe.edu.upc.noctuwell.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST ={
            // -- Swagger
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",

            // -- Login
            "/upc/users/login/**",

            // -- Resigtro
            "/upc/users/register/**",
    };

    // Authentication Manager
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
    }

    // Security Filter Chain

    /*

    1. Cuales van a ser los Request(pedidos) que seran evaluados para saber si el usuario tiene permisos sobre estos
        a. AnyRequest -> Todos los pedidos
        b. RequestMatchers -> Se evalua solo los que coincidan con las rutas
        c. RequestMatches + HttpMethod -> Se evalua a los que coincidan con la ruta y con el metodo (GET, POST, etc.)

    2. Cual es la regla de autorización que se van a aplicar sobre estos Request(pedidos)
        a. permitAll()
        b. denyAll()
        c. requestMatchers()
        d. hasRole()
        e. hayAnyRole()
        f. hasAuthority()
        g. hasAnyAuthority()
        h. SpEL Spring Expression Language
        i. authenticated()

    */

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.addFilterBefore( jwtRequestFilter , UsernamePasswordAuthenticationFilter.class );
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());
        http.authorizeHttpRequests( (auth) -> auth
                //.anyRequest().permitAll()
                .requestMatchers(AUTH_WHITELIST).permitAll()

                //APPOINTMENTS --------------------------------------------------------------------------------------------------------------------

// Listar todas las citas (solo admin)
                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Ver cita por ID (todos los involucrados)
                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Citas por paciente
                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments/patient/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Citas por especialista
                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments/specialist/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Crear cita
                        .requestMatchers(HttpMethod.POST, "/noctuwell/appointments")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Editar cita
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/appointments")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Eliminar cita (más seguro solo admin)
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/appointments/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Buscar por estado
                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments/status/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Reportes
                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments/specialists-most-patients")
                        .hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/noctuwell/appointments/patients-most-appointments")
                        .hasAuthority("ROLE_ADMIN")

                // DIAGNOSES -----------------------------------------------------------------------------------------------------------------------------------------

                        .requestMatchers(HttpMethod.GET, "noctuwell/diagnoses/my")
                        .hasAuthority("ROLE_PATIENT")

                        .requestMatchers(HttpMethod.GET, "/noctuwell/diagnoses/my-patients")
                        .hasAuthority("ROLE_SPECIALIST")

// Listar todos los diagnósticos
                        .requestMatchers(HttpMethod.GET, "/noctuwell/diagnoses")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Buscar diagnóstico por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/diagnoses/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear diagnóstico
                        .requestMatchers(HttpMethod.POST, "/noctuwell/diagnoses")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Editar diagnóstico
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/diagnoses")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Eliminar diagnóstico
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/diagnoses/*")
                        .hasAuthority("ROLE_ADMIN")

                 // HISTORIES --------------------------------------------------------------------------------------------------------------------
                        .requestMatchers(HttpMethod.GET, "/noctuwell/histories/patient/*/for-me")
                        .hasAuthority("ROLE_SPECIALIST")
// Crear historia
                        .requestMatchers(HttpMethod.POST, "/noctuwell/histories")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Editar historia
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/histories")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Eliminar historia
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/histories/*")
                        .hasAnyAuthority("ROLE_ADMIN")

                        // Listar todas las historias
                        .requestMatchers(HttpMethod.GET, "/noctuwell/histories")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST","ROLE_PATIENT")

// Rutas especiales primero
                        .requestMatchers(HttpMethod.GET, "/noctuwell/histories/my")
                        .hasAuthority("ROLE_PATIENT")
                        .requestMatchers(HttpMethod.GET, "/noctuwell/histories/my-patients")
                        .hasAuthority("ROLE_SPECIALIST")

// Luego las de ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/histories/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")



                        // MESSAGES --------------------------------------------------------------------------------------------------------------------

// Listar todos los mensajes
                        .requestMatchers(HttpMethod.GET, "/noctuwell/messages")
                        .hasAnyAuthority("ROLE_ADMIN")

// Buscar mensaje por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/messages/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear mensaje
                        .requestMatchers(HttpMethod.POST, "/noctuwell/messages")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Editar mensaje
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/messages")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Eliminar mensaje
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/messages/*")
                        .hasAnyAuthority("ROLE_ADMIN")


                        .requestMatchers(HttpMethod.GET, "/noctuwell/messages/appointment/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")
                // PATIENTS --------------------------------------------------------------------------------------------------------------------

                        .requestMatchers(HttpMethod.GET, "/noctuwell/patients/my")
                        .hasAuthority("ROLE_SPECIALIST")

// Listar todos los pacientes
                        .requestMatchers(HttpMethod.GET, "/noctuwell/patients")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Buscar paciente por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/patients/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Crear paciente
                        .requestMatchers(HttpMethod.POST, "/noctuwell/patients")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Editar paciente
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/patients/update")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_PATIENT")

// Eliminar paciente
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/patients/*")
                        .hasAuthority("ROLE_ADMIN")

// Top pacientes con más diagnósticos
                        .requestMatchers(HttpMethod.GET, "/noctuwell/patients/top-diagnoses")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Pacientes sin citas
                        .requestMatchers(HttpMethod.GET, "/noctuwell/patients/without-appointments")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

                // PLANS --------------------------------------------------------------------------------------------------------------------

// Listar todos los planes
                        .requestMatchers(HttpMethod.GET, "/noctuwell/plans")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Buscar plan por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/plans/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear plan
                        .requestMatchers(HttpMethod.POST, "/noctuwell/plans")
                        .hasAuthority("ROLE_ADMIN")

// Editar plan
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/plans/update")
                        .hasAuthority("ROLE_ADMIN")

// Eliminar plan
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/plans/*")
                        .hasAuthority("ROLE_ADMIN")

// Reporte: planes más usados
                        .requestMatchers(HttpMethod.GET, "/noctuwell/plans/report/most-used")
                        .hasAuthority("ROLE_ADMIN")

                // REVIEWS --------------------------------------------------------------------------------------------------------------------

                        .requestMatchers(HttpMethod.POST, "/noctuwell/reviews/me")
                        .hasAuthority("ROLE_PATIENT")
// Listar todas las reseñas
                        .requestMatchers(HttpMethod.GET, "/noctuwell/reviews")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Buscar reseña por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/reviews/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear reseña
                        .requestMatchers(HttpMethod.POST, "/noctuwell/reviews")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_PATIENT")

// Editar reseña
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/reviews")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_PATIENT")

// Eliminar reseña
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/reviews/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_PATIENT")

                // SCHEDULES --------------------------------------------------------------------------------------------------------------------

// Listar todos los horarios
                        .requestMatchers(HttpMethod.GET, "/noctuwell/schedules")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Buscar horario por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/schedules/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear horario
                        .requestMatchers(HttpMethod.POST, "/noctuwell/schedules")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Editar horario
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/schedules")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Eliminar horario
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/schedules/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

                // SPECIALISTS -----------------------------------------------------------------------------------------------------------------

                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists/me")
                        .hasAuthority("ROLE_SPECIALIST")
// Listar todos los especialistas
                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Buscar especialista por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear especialista
                        .requestMatchers(HttpMethod.POST, "/noctuwell/specialists")
                        .hasAuthority("ROLE_ADMIN")

// Editar especialista
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/specialists")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Eliminar especialista
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/specialists/*")
                        .hasAuthority("ROLE_ADMIN")

// Top especialistas mejor calificados
                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists/top-rated")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Tipo de especialista con más experiencia
                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists/top-type-experience")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Especialistas con más diagnósticos
                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists/most-diagnoses")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

// Especialistas sin reseñas
                        .requestMatchers(HttpMethod.GET, "/noctuwell/specialists/without-reviews")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST")

                // TYPE SPECIALISTS ------------------------------------------------------------------------------------------------------------

// Listar todos los tipos de especialista
                        .requestMatchers(HttpMethod.GET, "/noctuwell/typespecialists")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Buscar tipo de especialista por ID
                        .requestMatchers(HttpMethod.GET, "/noctuwell/typespecialists/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Crear tipo de especialista
                        .requestMatchers(HttpMethod.POST, "/noctuwell/typespecialists")
                        .hasAuthority("ROLE_ADMIN")

// Editar tipo de especialista
                        .requestMatchers(HttpMethod.PUT, "/noctuwell/typespecialists/update")
                        .hasAuthority("ROLE_ADMIN")

// Eliminar tipo de especialista
                        .requestMatchers(HttpMethod.DELETE, "/noctuwell/typespecialists/*")
                        .hasAuthority("ROLE_ADMIN")

// Tipo de especialista con más reseñas
                        .requestMatchers(HttpMethod.GET, "/noctuwell/typespecialists/most-reviewed")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

// Promedio de calificación por tipo de especialista
                        .requestMatchers(HttpMethod.GET, "/noctuwell/typespecialists/average-rating")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_SPECIALIST", "ROLE_PATIENT")

                // USERS / AUTH ----------------------------------------------------------------------------------------------------------------

                        .requestMatchers(HttpMethod.GET, "/upc/users")
                        .permitAll()

                        .requestMatchers(HttpMethod.PUT, "/upc/users/update-authority")
                        .permitAll()

                        .requestMatchers(HttpMethod.PUT, "/upc/users/*")
                        .hasAuthority("ROLE_ADMIN")
// Login (público)
                        .requestMatchers(HttpMethod.POST, "/upc/users/login")
                        .permitAll()

// Registro de usuario (público)
                        .requestMatchers(HttpMethod.POST, "/upc/users/register")
                        .permitAll()

// Crear usuario (uso interno, sólo ADMIN por seguridad)
                        .requestMatchers(HttpMethod.POST, "/upc/users")
                        .hasAuthority("ROLE_ADMIN")

// Buscar usuario por ID (solo ROLE_TAYLOR, como en el controller)
                        .requestMatchers(HttpMethod.GET, "/upc/users/*")
                        .hasAnyAuthority("ROLE_TAYLOR", "ROLE_ADMIN")
//HU71
                        .requestMatchers("/noctuwell/plans/report/**").authenticated()
                        //HU71
                        .requestMatchers("/noctuwell/typespecialists/review-statistics").authenticated()

                        .requestMatchers("/noctuwell/specialists/experience-ranking").authenticated()
                        .requestMatchers("/noctuwell/availability").authenticated()
                        .requestMatchers("/noctuwell/patients/by-user/**").authenticated()

        );

        http.sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }



    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
