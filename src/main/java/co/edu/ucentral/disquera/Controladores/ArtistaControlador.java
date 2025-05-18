package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Venta;
import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.ArtistaServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.UsuarioServicio;
import co.edu.ucentral.disquera.Servicios.VentaServicio;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/artista")
public class ArtistaControlador {

    private static final Logger LOGGER = Logger.getLogger(ArtistaControlador.class.getName());

    private final ArtistaServicio artistaServicio;
    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;
    private final VentaServicio ventaServicio;
    private final UsuarioServicio usuarioServicio;

    @Autowired
    public ArtistaControlador(ArtistaServicio artistaServicio, AlbumServicio albumServicio,
                              CancionServicio cancionServicio, VentaServicio ventaServicio,
                              UsuarioServicio usuarioServicio) {
        this.artistaServicio = artistaServicio;
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
        this.ventaServicio = ventaServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/panel")
    public String mostrarPanel(Model modelo, HttpSession session) {
        String usuario = getSessionUser(session);
        if (usuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Mostrando panel para artista: " + usuario);
        modelo.addAttribute("username", usuario);
        return "artista";
    }

    @GetMapping("/verlanzamientos")
    public String verLanzamientos(Model modelo, HttpSession session) {
        String usuario = getSessionUser(session);
        if (usuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Obteniendo lanzamientos para artista: " + usuario);

        var albumes = albumServicio.buscarPorUsuario(usuario);
        LOGGER.info("Álbumes encontrados: " + albumes.size());
        modelo.addAttribute("albumes", albumes);

        var canciones = cancionServicio.buscarPorUsuario(usuario);
        LOGGER.info("Canciones encontradas: " + canciones.size());
        modelo.addAttribute("canciones", canciones);

        modelo.addAttribute("username", usuario);
        return "verlanzamientos";
    }

    @GetMapping("/regalias")
    public String mostrarRegalias(Model model, HttpSession session) {
        String usuUsuario = getSessionUser(session);
        if (usuUsuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Consultando regalías para artista: " + usuUsuario);

        List<Venta> ventas = ventaServicio.consultarRegaliasPorUsuario(usuUsuario);
        double totalRegalias = ventaServicio.obtenerTotalRegaliasPendientes(usuUsuario);
        List<Venta> pagosSolicitados = ventaServicio.consultarVentasPorUsuarioYEstados(usuUsuario, List.of("SOLICITADO", "PAGADO"));

        model.addAttribute("ventas", ventas);
        model.addAttribute("totalRegalias", totalRegalias);
        model.addAttribute("pagosSolicitados", pagosSolicitados);
        model.addAttribute("username", usuUsuario);
        return "artista_regalias";
    }

    @PostMapping("/regalias/solicitar")
    public String solicitarPagoRegalias(Model model, HttpSession session) {
        String usuUsuario = getSessionUser(session);
        if (usuUsuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            return "redirect:/iniciosesion";
        }
        LOGGER.info("Procesando solicitud de pago para artista: " + usuUsuario);

        boolean exito = ventaServicio.solicitarPagoRegalias(usuUsuario);
        if (exito) {
            model.addAttribute("mensaje", "Solicitud de pago enviada exitosamente.");
        } else {
            model.addAttribute("error", "No hay regalías pendientes para solicitar.");
        }

        return mostrarRegalias(model, session);
    }

    @GetMapping("/regalias/reporte")
    public void generarReportePagadas(HttpSession session, HttpServletResponse response) throws IOException {
        String usuUsuario = getSessionUser(session);
        if (usuUsuario == null) {
            LOGGER.warning("No se encontró usuario en la sesión, redirigiendo a iniciosesion");
            response.sendRedirect("/iniciosesion");
            return;
        }

        LOGGER.info("Generando reporte PDF de regalías pagadas para usuario: " + usuUsuario);
        List<Venta> ventasPagadas = ventaServicio.consultarVentasPorUsuarioYEstados(usuUsuario, List.of("PAGADO"));
        Usuario artista = usuarioServicio.buscarPorUsuario(usuUsuario);
        if (artista == null) {
            LOGGER.warning("Usuario no encontrado: " + usuUsuario);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Artista no encontrado");
            return;
        }

        // Configurar respuesta como PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_regalias_pagadas.pdf");

        // Crear documento PDF
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Logo
        try {
            Image logo = new Image(ImageDataFactory.create(getClass().getResource("/static/images/startune-logo.png")));
            logo.setWidth(100);
            logo.setAutoScaleHeight(true);
            logo.setTextAlignment(TextAlignment.CENTER);
            document.add(logo);
            document.add(new Paragraph("\n"));
        } catch (Exception e) {
            LOGGER.warning("No se pudo cargar el logo: " + e.getMessage());
        }

        // Título
        Paragraph title = new Paragraph("Reporte de Regalías Pagadas")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Saludo
        Paragraph saludo = new Paragraph("Hola, " + artista.getNombre() + "! Se te han pagado estas comisiones por regalías:")
                .setFontSize(12)
                .setMarginBottom(20);
        document.add(saludo);

        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 15, 15, 20, 20, 15}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        String[] headers = {"Lanzamiento", "Unidades", "Formato", "Regalías ($)", "Fecha", "Estado"};
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5);
            table.addHeaderCell(cell);
        }

        // Formato de números y fechas
        DecimalFormat df = new DecimalFormat("#.00");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Datos
        for (Venta venta : ventasPagadas) {
            String lanzamiento = venta.getAlbum() != null ? venta.getAlbum().getNombre() : venta.getCancion().getTitulo();
            table.addCell(new Cell().add(new Paragraph(lanzamiento)));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(venta.getUnidadesVendidas()))));
            table.addCell(new Cell().add(new Paragraph(venta.getFormato().toString())));
            table.addCell(new Cell().add(new Paragraph(df.format(venta.getMontoRegalias()))));
            table.addCell(new Cell().add(new Paragraph(venta.getFechaVenta().format(formatter))));
            table.addCell(new Cell().add(new Paragraph(venta.getEstadoPago())));
        }

        document.add(table);

        // Total
        double totalGanancias = ventasPagadas.stream()
                .mapToDouble(Venta::getMontoRegalias)
                .sum();
        Paragraph total = new Paragraph("Total de Ganancias: $" + df.format(totalGanancias))
                .setFontSize(12)
                .setBold()
                .setMarginTop(20);
        document.add(total);

        // Pie de página
        Paragraph footer = new Paragraph("Reporte generado por Startune Records - " + java.time.LocalDate.now().format(formatter))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);

        document.close();
    }

    private String getSessionUser(HttpSession session) {
        String usuario = (String) session.getAttribute("username");
        LOGGER.info("Usuario en sesión: " + usuario);
        return usuario;
    }
}