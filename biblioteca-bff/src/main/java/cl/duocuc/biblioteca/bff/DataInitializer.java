package cl.duocuc.biblioteca.bff;

import cl.duocuc.biblioteca.bff.entity.LibroEntity;
import cl.duocuc.biblioteca.bff.entity.PrestamoEntity;
import cl.duocuc.biblioteca.bff.entity.ReservaEntity;
import cl.duocuc.biblioteca.bff.entity.UsuarioEntity;
import cl.duocuc.biblioteca.bff.repository.LibroRepository;
import cl.duocuc.biblioteca.bff.repository.PrestamoRepository;
import cl.duocuc.biblioteca.bff.repository.ReservaRepository;
import cl.duocuc.biblioteca.bff.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final LibroRepository libroRepo;
    private final PrestamoRepository prestamoRepo;
    private final ReservaRepository reservaRepo;

    public DataInitializer(UsuarioRepository usuarioRepo,
                           LibroRepository libroRepo,
                           PrestamoRepository prestamoRepo,
                           ReservaRepository reservaRepo) {
        this.usuarioRepo = usuarioRepo;
        this.libroRepo = libroRepo;
        this.prestamoRepo = prestamoRepo;
        this.reservaRepo = reservaRepo;
    }

    @Override
    public void run(String... args) {
        // ── Usuarios ──────────────────────────────────────────────
        UsuarioEntity u1 = usuario("Juan",    "Perez",    "juan.perez@correo.cl",    "+56912345678");
        UsuarioEntity u2 = usuario("Maria",   "Gonzalez", "maria.gonzalez@correo.cl", "+56987654321");
        UsuarioEntity u3 = usuario("Carlos",  "Romero",   "carlos.romero@correo.cl",  "+56955555555");
        UsuarioEntity u4 = usuario("Valentina","Silva",   "valentina.silva@correo.cl", null);

        List<UsuarioEntity> usuarios = usuarioRepo.saveAll(List.of(u1, u2, u3, u4));

        // ── Libros ────────────────────────────────────────────────
        LibroEntity l1 = libro("Cien anos de soledad",    "Gabriel Garcia Marquez", "978-0-06-088328-7", 1);
        LibroEntity l2 = libro("Don Quijote de la Mancha","Miguel de Cervantes",    "978-84-376-0494-7", 1);
        LibroEntity l3 = libro("El alquimista",            "Paulo Coelho",           "978-0-06-112241-5", 0);
        LibroEntity l4 = libro("1984",                     "George Orwell",          "978-0-452-28423-4", 1);
        LibroEntity l5 = libro("Clean Code",               "Robert C. Martin",       "978-0-13-235088-4", 1);
        LibroEntity l6 = libro("El principito",            "Antoine de Saint-Exupery","978-84-261-3289-9", 0);

        List<LibroEntity> libros = libroRepo.saveAll(List.of(l1, l2, l3, l4, l5, l6));

        // ── Prestamos ─────────────────────────────────────────────
        // l3 y l6 ya estan marcados como no disponibles (prestados)
        PrestamoEntity p1 = prestamo(usuarios.get(0).getId(), libros.get(2).getId(), diasAtras(5),  null,          "ACTIVO");
        PrestamoEntity p2 = prestamo(usuarios.get(1).getId(), libros.get(5).getId(), diasAtras(12), null,          "ACTIVO");
        PrestamoEntity p3 = prestamo(usuarios.get(2).getId(), libros.get(0).getId(), diasAtras(20), diasAtras(3),  "DEVUELTO");
        PrestamoEntity p4 = prestamo(usuarios.get(3).getId(), libros.get(1).getId(), diasAtras(30), diasAtras(15), "DEVUELTO");

        prestamoRepo.saveAll(List.of(p1, p2, p3, p4));

        // ── Reservas ──────────────────────────────────────────────
        ReservaEntity r1 = reserva(usuarios.get(2).getId(), libros.get(1).getId(), new Date(), "PENDIENTE");
        ReservaEntity r2 = reserva(usuarios.get(0).getId(), libros.get(3).getId(), diasAtras(1), "COMPLETADA");
        ReservaEntity r3 = reserva(usuarios.get(1).getId(), libros.get(4).getId(), diasAtras(2), "PENDIENTE");

        reservaRepo.saveAll(List.of(r1, r2, r3));

        System.out.println("=================================================");
        System.out.println("  Datos iniciales cargados:");
        System.out.println("  - " + usuarioRepo.count()  + " usuarios");
        System.out.println("  - " + libroRepo.count()    + " libros");
        System.out.println("  - " + prestamoRepo.count() + " prestamos");
        System.out.println("  - " + reservaRepo.count()  + " reservas");
        System.out.println("=================================================");
    }

    // ── helpers ──────────────────────────────────────────────────

    private UsuarioEntity usuario(String nombre, String apellido, String email, String telefono) {
        UsuarioEntity u = new UsuarioEntity();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setTelefono(telefono);
        return u;
    }

    private LibroEntity libro(String titulo, String autor, String isbn, int disponible) {
        LibroEntity l = new LibroEntity();
        l.setTitulo(titulo);
        l.setAutor(autor);
        l.setIsbn(isbn);
        l.setDisponible(disponible);
        return l;
    }

    private PrestamoEntity prestamo(Long usuarioId, Long libroId,
                                    Date fechaPrestamo, Date fechaDevolucion, String estado) {
        PrestamoEntity p = new PrestamoEntity();
        p.setUsuarioId(usuarioId);
        p.setLibroId(libroId);
        p.setFechaPrestamo(fechaPrestamo);
        p.setFechaDevolucion(fechaDevolucion);
        p.setEstado(estado);
        return p;
    }

    private ReservaEntity reserva(Long usuarioId, Long libroId, Date fechaReserva, String estado) {
        ReservaEntity r = new ReservaEntity();
        r.setUsuarioId(usuarioId);
        r.setLibroId(libroId);
        r.setFechaReserva(fechaReserva);
        r.setEstado(estado);
        return r;
    }

    private Date diasAtras(int dias) {
        return new Date(System.currentTimeMillis() - (long) dias * 24 * 60 * 60 * 1000);
    }
}
