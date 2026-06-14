import java.time.LocalDate

// =====================================================
// CLASE ABSTRACTA
// Aprendemos:
// - Abstracción
// - Herencia
// =====================================================

abstract class Persona(
    open val id: Int,
    open var nombre: String
) {
    abstract fun mostrarInformacion()
}

// =====================================================
// HERENCIA
// Usuario hereda de Persona
// =====================================================

class Usuario(
    override val id: Int,
    override var nombre: String
) : Persona(id, nombre) {

    override fun mostrarInformacion() {
        println("Usuario -> ID: $id | Nombre: $nombre")
    }
}

// =====================================================
// HERENCIA
// Bibliotecario hereda de Persona
// =====================================================

class Bibliotecario(
    override val id: Int,
    override var nombre: String
) : Persona(id, nombre) {

    override fun mostrarInformacion() {
        println("Bibliotecario -> ID: $id | Nombre: $nombre")
    }
}

// =====================================================
// INTERFAZ
// Aprendemos:
// - Contratos
// - Polimorfismo
// =====================================================

interface Prestable {
    fun prestar(usuario: Usuario)
    fun devolver()
}

// =====================================================
// CLASE ABSTRACTA
// MaterialBiblioteca
//
// Aprendemos:
// - Abstracción
// - Polimorfismo
//
// Un Libro, Revista o Tesis son materiales.
// =====================================================

abstract class MaterialBiblioteca(
    open val id: Int,
    open var titulo: String
) {
    abstract fun mostrarDetalle()
}

// =====================================================
// COMPOSICIÓN
//
// Prestamo contiene Usuario + Fecha
// =====================================================

data class Prestamo(
    val usuario: Usuario,
    val fechaPrestamo: LocalDate = LocalDate.now()
)

// =====================================================
// CLASE LIBRO
//
// Aprendemos:
// - Herencia
// - Interfaces
// - Encapsulamiento
// =====================================================

class Libro(
    override val id: Int,
    override var titulo: String,
    var autor: String
) : MaterialBiblioteca(id, titulo), Prestable {

    private var prestamo: Prestamo? = null

    override fun prestar(usuario: Usuario) {

        if (prestamo != null) {
            println("El libro ya está prestado.")
            return
        }

        prestamo = Prestamo(usuario)

        println(
            "'$titulo' prestado a ${usuario.nombre}"
        )
    }

    override fun devolver() {

        if (prestamo == null) {
            println("El libro ya estaba disponible.")
            return
        }

        prestamo = null

        println("'$titulo' fue devuelto.")
    }

    fun disponible(): Boolean {
        return prestamo == null
    }

    fun obtenerPrestamo(): Prestamo? {
        return prestamo
    }

    override fun mostrarDetalle() {

        val estado =
            if (disponible())
                "Disponible"
            else
                "Prestado a ${prestamo!!.usuario.nombre}"

        println(
            """
            ID: $id
            Título: $titulo
            Autor: $autor
            Estado: $estado
            """.trimIndent()
        )
    }
}

// =====================================================
// OTRA CLASE QUE HEREDA
//
// Demostración de polimorfismo
// =====================================================

class Revista(
    override val id: Int,
    override var titulo: String,
    val numeroEdicion: Int
) : MaterialBiblioteca(id, titulo) {

    override fun mostrarDetalle() {
        println(
            "Revista -> $titulo | Edición $numeroEdicion"
        )
    }
}

// =====================================================
// CLASE PRINCIPAL
//
// Encapsulamiento:
// Todo es private
// =====================================================

class Biblioteca {

    private val materiales =
        mutableListOf<MaterialBiblioteca>()

    private val usuarios =
        mutableListOf<Usuario>()

    private val historialPrestamos =
        mutableListOf<String>()

    // ==========================================
    // CRUD USUARIOS
    // ==========================================

    fun crearUsuario(usuario: Usuario) {
        usuarios.add(usuario)
        println("Usuario registrado.")
    }

    fun listarUsuarios() {

        if (usuarios.isEmpty()) {
            println("No existen usuarios.")
            return
        }

        usuarios.forEach {
            it.mostrarInformacion()
        }
    }

    fun actualizarUsuario(
        id: Int,
        nuevoNombre: String
    ) {

        val usuario =
            usuarios.find { it.id == id }

        if (usuario == null) {
            println("Usuario no encontrado.")
            return
        }

        usuario.nombre = nuevoNombre

        println("Usuario actualizado.")
    }

    fun eliminarUsuario(id: Int) {

        val eliminado =
            usuarios.removeIf { it.id == id }

        if (eliminado)
            println("Usuario eliminado.")
        else
            println("Usuario no encontrado.")
    }

    // ==========================================
    // CRUD LIBROS
    // ==========================================

    fun crearLibro(libro: Libro) {

        materiales.add(libro)

        println("Libro agregado.")
    }

    fun listarLibros() {

        val libros =
            materiales.filterIsInstance<Libro>()

        if (libros.isEmpty()) {
            println("No existen libros.")
            return
        }

        libros.forEach {
            println("----------------")
            it.mostrarDetalle()
        }
    }

    fun actualizarLibro(
        id: Int,
        nuevoTitulo: String,
        nuevoAutor: String
    ) {

        val libro =
            materiales
                .filterIsInstance<Libro>()
                .find { it.id == id }

        if (libro == null) {
            println("Libro no encontrado.")
            return
        }

        libro.titulo = nuevoTitulo
        libro.autor = nuevoAutor

        println("Libro actualizado.")
    }

    fun eliminarLibro(id: Int) {

        val eliminado =
            materiales.removeIf {
                it is Libro && it.id == id
            }

        if (eliminado)
            println("Libro eliminado.")
        else
            println("Libro no encontrado.")
    }

    // ==========================================
    // PRÉSTAMOS
    // ==========================================

    fun prestarLibro(
        idLibro: Int,
        idUsuario: Int
    ) {

        val libro =
            materiales
                .filterIsInstance<Libro>()
                .find { it.id == idLibro }

        if (libro == null) {
            println("Libro no encontrado.")
            return
        }

        val usuario =
            usuarios.find { it.id == idUsuario }

        if (usuario == null) {
            println("Usuario no encontrado.")
            return
        }

        if (libro.disponible()) {

            libro.prestar(usuario)

            historialPrestamos.add(
                "${usuario.nombre} prestó '${libro.titulo}' (${LocalDate.now()})"
            )
        } else {
            println("Libro no disponible.")
        }
    }

    fun devolverLibro(idLibro: Int) {

        val libro =
            materiales
                .filterIsInstance<Libro>()
                .find { it.id == idLibro }

        if (libro == null) {
            println("Libro no encontrado.")
            return
        }

        libro.devolver()
    }

    // ==========================================
    // REPORTES
    // ==========================================

    fun mostrarHistorial() {

        if (historialPrestamos.isEmpty()) {
            println("No existen préstamos.")
            return
        }

        historialPrestamos.forEach {
            println(it)
        }
    }
}

// =====================================================
// MENÚ PRINCIPAL
//
// Aprendemos:
// - Loops
// - When
// - Entrada de usuario
// =====================================================

fun main() {

    val biblioteca = Biblioteca()

    while (true) {

        println()
        println("================================")
        println(" SISTEMA DE BIBLIOTECA ")
        println("================================")
        println("1. Registrar usuario")
        println("2. Listar usuarios")
        println("3. Actualizar usuario")
        println("4. Eliminar usuario")
        println()
        println("5. Registrar libro")
        println("6. Listar libros")
        println("7. Actualizar libro")
        println("8. Eliminar libro")
        println()
        println("9. Prestar libro")
        println("10. Devolver libro")
        println("11. Ver historial")
        println()
        println("0. Salir")
        print("Seleccione: ")

        when (readln().toIntOrNull()) {

            1 -> {

                print("ID: ")
                val id = readln().toInt()

                print("Nombre: ")
                val nombre = readln()

                biblioteca.crearUsuario(
                    Usuario(id, nombre)
                )
            }

            2 -> biblioteca.listarUsuarios()

            3 -> {

                print("ID Usuario: ")
                val id = readln().toInt()

                print("Nuevo nombre: ")
                val nombre = readln()

                biblioteca.actualizarUsuario(
                    id,
                    nombre
                )
            }

            4 -> {

                print("ID Usuario: ")
                val id = readln().toInt()

                biblioteca.eliminarUsuario(id)
            }

            5 -> {

                print("ID Libro: ")
                val id = readln().toInt()

                print("Título: ")
                val titulo = readln()

                print("Autor: ")
                val autor = readln()

                biblioteca.crearLibro(
                    Libro(
                        id,
                        titulo,
                        autor
                    )
                )
            }

            6 -> biblioteca.listarLibros()

            7 -> {

                print("ID Libro: ")
                val id = readln().toInt()

                print("Nuevo título: ")
                val titulo = readln()

                print("Nuevo autor: ")
                val autor = readln()

                biblioteca.actualizarLibro(
                    id,
                    titulo,
                    autor
                )
            }

            8 -> {

                print("ID Libro: ")
                val id = readln().toInt()

                biblioteca.eliminarLibro(id)
            }

            9 -> {

                print("ID Libro: ")
                val libro = readln().toInt()

                print("ID Usuario: ")
                val usuario = readln().toInt()

                biblioteca.prestarLibro(
                    libro,
                    usuario
                )
            }

            10 -> {

                print("ID Libro: ")
                val libro = readln().toInt()

                biblioteca.devolverLibro(libro)
            }

            11 -> biblioteca.mostrarHistorial()

            0 -> {
                println("Hasta luego.")
                break
            }

            else -> {
                println("Opción inválida.")
            }
        }
    }
}