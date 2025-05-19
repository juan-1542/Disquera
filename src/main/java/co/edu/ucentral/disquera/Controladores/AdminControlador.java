package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Album;
import co.edu.ucentral.disquera.Persistencia.Entidades.Cancion;
import co.edu.ucentral.disquera.Persistencia.Entidades.Usuario;
import co.edu.ucentral.disquera.Persistencia.Entidades.Venta;
import co.edu.ucentral.disquera.Servicios.AlbumServicio;
import co.edu.ucentral.disquera.Servicios.CancionServicio;
import co.edu.ucentral.disquera.Servicios.ContratoService;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private static final Logger LOGGER = Logger.getLogger(AdminControlador.class.getName());

    private final AlbumServicio albumServicio;
    private final CancionServicio cancionServicio;
    private final ContratoService contratoServicio;
    private final VentaServicio ventaServicio;
    private final UsuarioServicio usuarioServicio;

    public AdminControlador(AlbumServicio albumServicio, CancionServicio cancionServicio,
                            ContratoService contratoService, VentaServicio ventaServicio,
                            UsuarioServicio usuarioServicio) {
        this.albumServicio = albumServicio;
        this.cancionServicio = cancionServicio;
        this.contratoServicio = contratoService;
        this.ventaServicio = ventaServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/ventas")
    public String listarVentas(Model modelo) {
        LocalDate hoy = LocalDate.now();

        List<Album> albumes = albumServicio.listarTodos().stream()
                .filter(album -> album.getEstado() == Album.Estado.APROBADO)
                .filter(album -> {
                    Date fechaLanzamiento = album.getFechaLanzamiento();
                    if (fechaLanzamiento == null) {
                        LOGGER.warning("Álbum " + album.getNombre() + " tiene fechaLanzamiento nula");
                        return false;
                    }
                    LocalDate fecha = fechaLanzamiento instanceof java.sql.Date
                            ? ((java.sql.Date) fechaLanzamiento).toLocalDate()
                            : fechaLanzamiento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return fecha.isBefore(hoy.plusDays(1));
                })
                .collect(Collectors.toList());

        List<Cancion> sencillos = cancionServicio.listarSencillos().stream()
                .filter(cancion -> cancion.getEstado() == Cancion.Estado.APROBADO)
                .filter(cancion -> {
                    LocalDate fechaLanzamiento;
                    if (cancion.getAlbum() != null) {
                        Date fecha = cancion.getAlbum().getFechaLanzamiento();
                        if (fecha == null) {
                            LOGGER.warning("Canción " + cancion.getTitulo() + " tiene álbum con fechaLanzamiento nula");
                            return false;
                        }
                        fechaLanzamiento = fecha instanceof java.sql.Date
                                ? ((java.sql.Date) fecha).toLocalDate()
                                : fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    } else {
                        fechaLanzamiento = hoy;
                    }
                    return fechaLanzamiento.isBefore(hoy.plusDays(1));
                })
                .collect(Collectors.toList());

        modelo.addAttribute("albumes", albumes);
        modelo.addAttribute("sencillos", sencillos);
        modelo.addAttribute("formatos", Venta.Formato.values());
        return "admin_ventas";
    }

    @PostMapping("/ventas/registrar")
    public String registrarVenta(
            @RequestParam(value = "albumId", required = false) Long albumId,
            @RequestParam(value = "cancionId", required = false) Long cancionId,
            @RequestParam("unidadesVendidas") Integer unidadesVendidas,
            @RequestParam("usuario") String usuario,
            @RequestParam("formato") Venta.Formato formato,
            Model modelo) {
        LOGGER.info("Registrando venta: albumId=" + albumId + ", cancionId=" + cancionId +
                ", unidades=" + unidadesVendidas + ", usuario=" + usuario + ", formato=" + formato);

        boolean exito = ventaServicio.registrarVenta(albumId, cancionId, unidadesVendidas, usuario, formato);
        if (exito) {
            return "redirect:/admin/ventas?success";
        } else {
            modelo.addAttribute("error", "No se pudo registrar la venta. Verifique los datos.");
            return listarVentas(modelo);
        }
    }

    @GetMapping("/pagos")
    public String listarPagos(
            @RequestParam(value = "usuario", required = false) String usuario,
            Model modelo) {
        List<Venta> ventasSolicitadas;
        List<Venta> ventasPagadas;

        if (usuario != null && !usuario.isEmpty()) {
            LOGGER.info("Filtrando pagos para usuario: " + usuario);
            ventasSolicitadas = ventaServicio.consultarVentasPorUsuarioYEstados(usuario, List.of("SOLICITADO"));
            ventasPagadas = ventaServicio.consultarVentasPorUsuarioYEstados(usuario, List.of("PAGADO"));
        } else {
            LOGGER.info("Listando todos los pagos solicitados y pagados");
            ventasSolicitadas = ventaServicio.consultarVentasPorEstado("SOLICITADO");
            ventasPagadas = ventaServicio.consultarVentasPorEstado("PAGADO");
        }

        List<Usuario> artistas = usuarioServicio.listarArtistas();
        modelo.addAttribute("ventas", ventasSolicitadas);
        modelo.addAttribute("ventasPagadas", ventasPagadas);
        modelo.addAttribute("artistas", artistas);
        modelo.addAttribute("usuario", usuario);
        return "admin_pagos";
    }

    @PostMapping("/pagos/aprobar")
    public String aprobarPago(
            @RequestParam("ventaId") Long ventaId,
            @RequestParam(value = "usuario", required = false) String usuario,
            Model modelo) {
        LOGGER.info("Procesando aprobación de pago para venta ID: " + ventaId);
        boolean exito = ventaServicio.aprobarPago(ventaId);
        if (exito) {
            String redirectUrl = usuario != null && !usuario.isEmpty() ?
                    "/admin/pagos?usuario=" + usuario : "/admin/pagos";
            return "redirect:" + redirectUrl + "&success";
        } else {
            modelo.addAttribute("error", "No se pudo aprobar el pago. Verifique el estado de la venta.");
            return listarPagos(usuario, modelo);
        }
    }

    @GetMapping("/pagos/reporte")
    public void generarReportePagadas(
            @RequestParam("usuario") String usuario,
            HttpServletResponse response,
            Model modelo) throws IOException {
        if (usuario == null || usuario.isEmpty()) {
            LOGGER.warning("No se proporcionó usuario para el reporte");
            response.sendRedirect("/admin/pagos?error=Seleccione un artista para generar el reporte");
            return;
        }

        LOGGER.info("Generando reporte PDF de regalías pagadas para usuario: " + usuario);
        List<Venta> ventasPagadas = ventaServicio.consultarVentasPorUsuarioYEstados(usuario, List.of("PAGADO"));
        Usuario artista = usuarioServicio.buscarPorUsuario(usuario);
        if (artista == null) {
            LOGGER.warning("Usuario no encontrado: " + usuario);
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
        Paragraph saludo = new Paragraph("Reporte de regalías pagadas al artista " + artista.getNombre() + ".")
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
        Paragraph total = new Paragraph("Total de Ganancias Pagadas: $" + df.format(totalGanancias))
                .setFontSize(12)
                .setBold()
                .setMarginTop(20);
        document.add(total);

        // Pie de página
        Paragraph footer = new Paragraph("Reporte generado por Startune Records - " + LocalDate.now().format(formatter))
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);

        document.close();
    }

    @GetMapping("/cronograma")
    public String listarCronograma(
            @RequestParam(value = "usuario", required = false) String usuario,
            Model modelo) {
        List<Album> albumes;
        List<Cancion> sencillos;

        if (usuario != null && !usuario.isEmpty()) {
            LOGGER.info("Filtrando lanzamientos para usuario: " + usuario);
            albumes = albumServicio.buscarPorUsuario(usuario);
            sencillos = cancionServicio.buscarPorUsuario(usuario).stream()
                    .filter(cancion -> cancion.getAlbum() == null)
                    .collect(Collectors.toList());
        } else {
            LOGGER.info("Listando todos los lanzamientos");
            albumes = albumServicio.listarTodos();
            sencillos = cancionServicio.listarSencillos();
        }

        List<Usuario> artistas = usuarioServicio.listarArtistas();
        modelo.addAttribute("albumes", albumes);
        modelo.addAttribute("sencillos", sencillos);
        modelo.addAttribute("artistas", artistas);
        modelo.addAttribute("usuario", usuario);
        return "admin_cronograma";
    }
}