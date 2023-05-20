package uniandes.cupi2.discotienda.test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import uniandes.cupi2.discotienda.mundo.ArchivoVentaException;
import uniandes.cupi2.discotienda.mundo.Discotienda;
import uniandes.cupi2.discotienda.mundo.ElementoExisteException;


public class DiscoTiendaTest {

    private Discotienda discotienda;
    private static final String RUTA_FACTURAS = "./data/facturas";

    @BeforeEach
    public void setUP() throws Exception {
        this.discotienda = new Discotienda("./data/discotienda.discos");
        discotienda.agregarDisco("Grita - II", "Armenia", "Rock", "data/imagenes/Grita.jpeg");
        discotienda.agregarCancionADisco("Grita - II", "El Tiempo", 3, 38, 10000, 386, 128);
        discotienda.agregarDisco("Siento Que No Sientas", "Nubes Negras", "Indie", "data/imagenes/Siento.jpg");
    }

    @Test
    @DisplayName("Dar Disco con Nombre")
    public void testSimpleDarDisco() {
        assertEquals("Grita - II", discotienda.darDisco("Grita - II").darNombreDisco(), "Dar disco simple falló (Nombre canción)");
        assertEquals("Armenia", discotienda.darDisco("Grita - II").darArtista(), "Dar disco simple falló (Nombre artista)");
        assertEquals("Rock", discotienda.darDisco("Grita - II").darGenero(), "Dar disco simple falló (Genero)");
    }

    @Test
    @DisplayName("Dar Disco con Nombre, Artista, Cancion")
    public void testDarDisco() {
        assertEquals("Grita - II", discotienda.darDisco("Grita - II", "Armenia", "El Tiempo").darNombreDisco(), "Dar disco falló (Nombre canción)");
        assertEquals("Armenia", discotienda.darDisco("Grita - II", "Armenia", "El Tiempo").darArtista(), "Dar disco falló (Nombre artista)");
        assertEquals("Rock", discotienda.darDisco("Grita - II", "Armenia", "El Tiempo").darGenero(), "Dar disco falló (Genero)");
    }


    @Test
    @DisplayName("Agregar Disco")
    public void testAgregarDisco() {
        assertEquals("Siento Que No Sientas", discotienda.darDisco("Siento Que No Sientas").darNombreDisco(), "Agregar disco falló (Nombre canción)");
        assertEquals("Nubes Negras", discotienda.darDisco("Siento Que No Sientas").darArtista(), "Agregar disco falló (Nombre artista)");
        assertEquals("Indie", discotienda.darDisco("Siento Que No Sientas").darGenero(), "Agregar disco falló (Genero)");
    }

    @Test
    @DisplayName("Agregar Cancion a Disco")
    public void testAgregarCancionADisco() throws ElementoExisteException {
        discotienda.agregarCancionADisco("Siento Que No Sientas", "Siento Que No Sientas", 3, 5, 2000, 360, 128);
        assertEquals("Siento Que No Sientas", discotienda.darDisco("Siento Que No Sientas").darCancion("Siento Que No Sientas").darNombre(), "Agregar Canción falló (Nombre canción)");
        assertEquals(3, discotienda.darDisco("Siento Que No Sientas").darCancion("Siento Que No Sientas").darMinutos(), "Agregar Canción falló (Minutos)");
        assertEquals(5, discotienda.darDisco("Siento Que No Sientas").darCancion("Siento Que No Sientas").darSegundos(), "Agregar Canción falló (Segundos)");
        assertEquals(2000, discotienda.darDisco("Siento Que No Sientas").darCancion("Siento Que No Sientas").darPrecio(), "Agregar Canción falló (Precio)");
        assertEquals(360, discotienda.darDisco("Siento Que No Sientas").darCancion("Siento Que No Sientas").darTamano(), "Agregar Canción falló (Tamaño)");
        assertEquals(128, discotienda.darDisco("Siento Que No Sientas").darCancion("Siento Que No Sientas").darCalidad(), "Agregar Canción falló (Calidad)");
    }

    @Test
    @DisplayName("Vender Disco")
    public void testVenderCancion() {
        int cantidad = discotienda.darDisco("Grita - II").darCancion("El Tiempo").darUnidadesVendidas();

        assertAll(
            () -> discotienda.venderCancion(discotienda.darDisco("Grita - II"), discotienda.darDisco("Grita - II").darCancion("El Tiempo"), "n.p.q@uniandes.com", RUTA_FACTURAS),
            () -> assertTrue(cantidad < discotienda.darDisco("Grita - II").darCancion("El Tiempo").darUnidadesVendidas())
        );
    }

    @Test
    @DisplayName("Dar Discos")
    public void testDarDiscos() {
        ArrayList discos = discotienda.darDiscos();
    }

    @Test
    @DisplayName("Validar Email")
    public void testValidarEmail() {
        assertAll(
            () -> assertTrue(discotienda.validarEmail("n.p.q@uniandes.com")),
            () -> assertTrue(discotienda.validarEmail("mpe123@yahoo.com")),
            () -> assertFalse(discotienda.validarEmail("mpe123yahoo.com")),
            () -> assertFalse(discotienda.validarEmail("antoNavarro@hotmailcom"))
        );
    }

    @Test
    @DisplayName("Generar Factura")
    public void testGenerarFactura() throws IOException {
        File folder = new File("./data/facturas");
        File[] files = folder.listFiles();
        if(files!=null) { 
            for(File f: files) {
                f.delete();
            }
        }

        discotienda.venderCancion(discotienda.darDisco("Grita - II"), discotienda.darDisco("Grita - II").darCancion("El Tiempo"), "n.p.q@uniandes.com", RUTA_FACTURAS);

        folder = new File("./data/facturas");
        files = folder.listFiles();

        boolean flag = files.length == 1;
        
        assertTrue(flag, "No se generó la factura");
    }

    @Test
    @DisplayName("Vender Lista Canciones")
    public void testVenderListaCanciones() throws ArchivoVentaException, FileNotFoundException, IOException {
        File folder = new File("./data/facturas");
        File[] files = folder.listFiles();
        if(files!=null) { 
            for(File f: files) {
                f.delete();
            }
        }

        File archivoPedido = new File("./data/pedido.txt");

        discotienda.venderListaCanciones(archivoPedido, RUTA_FACTURAS);

        folder = new File("./data/facturas");
        files = folder.listFiles();

        boolean flag = files.length == 1;
        
        assertTrue(flag, "No se generó la factura");
    }

}
