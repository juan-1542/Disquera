package co.edu.ucentral.disquera.Controladores;

import co.edu.ucentral.disquera.Persistencia.Entidades.Contrato;
import co.edu.ucentral.disquera.Servicios.ContratoService;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import java.io.InputStream;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/contratos")
public class ContratoControlador {

    private final ContratoService contratoService;


    @Autowired
    public ContratoControlador(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    // Página principal de gestión de contratos para artistas
    @GetMapping("/gestionar")
    public String vistaGestionContratos(Model model) {
        List<Contrato> contratos = contratoService.listarContratos();
        model.addAttribute("contratos", contratos);

        long activos = contratos.stream().filter(c -> "Activo".equals(c.getEstado())).count();
        long pendientes = contratos.stream().filter(c -> "Pendiente".equals(c.getEstado())).count();
        long vencidos = contratos.stream().filter(c -> "Vencido".equals(c.getEstado())).count();

        model.addAttribute("activos", activos);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("vencidos", vencidos);

        return "gestionarContratos";
    }

    // Mostrar formulario para generar contrato
    @GetMapping("/nuevo")
    public String mostrarFormularioContrato(Model model) {
        model.addAttribute("contrato", new Contrato());
        return "generarContrato";
    }

    // Procesar envío del formulario y mostrar mensaje de confirmación
    @PostMapping("/guardar")
    public String guardarContrato(@ModelAttribute("contrato") Contrato contrato, Model model) {
        contratoService.guardarContrato(contrato);
        model.addAttribute("mensaje", "¡Contrato generado exitosamente! La solicitud será revisada por el administrador.");
        return "confirmacionContrato";
    }

    // Mostrar formulario para editar contrato
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.listarContratos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (contrato != null) {
            model.addAttribute("contrato", contrato);
            return "editarContrato";
        }
        return "redirect:/contratos/gestionar";
    }

    // Procesar edición del contrato
    @PostMapping("/actualizar")
    public String actualizarContrato(@ModelAttribute("contrato") Contrato contrato, Model model) {
        contratoService.guardarContrato(contrato);
        model.addAttribute("mensaje", "¡Contrato actualizado exitosamente!");
        return "redirect:/contratos/gestionar";
    }

    // Eliminar contrato
    @GetMapping("/eliminar/{id}")
    public String eliminarContrato(@PathVariable Long id) {
        contratoService.eliminarContrato(id);
        return "redirect:/contratos/gestionar";
    }

    // Página de administración para revisar contratos pendientes
    @GetMapping("/admin/contratos")
    public String vistaAdminContratos(Model model) {
        List<Contrato> contratosPendientes = contratoService.listarContratosPendientes();
        model.addAttribute("contratosPendientes", contratosPendientes);
        return "adminContratos";
    }

    // Mostrar detalles del contrato
    @GetMapping("/admin/detalle/{id}")
    public String mostrarDetalleContrato(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.listarContratos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (contrato != null) {
            model.addAttribute("contrato", contrato);
            return "detalleContrato";
        }
        return "redirect:/contratos/admin/contratos";
    }

    // Aceptar contrato
    @GetMapping("/admin/aceptar/{id}")
    public String aceptarContrato(@PathVariable Long id) {
        contratoService.actualizarEstado(id, "Activo");
        return "redirect:/contratos/admin/contratos";
    }

    @PostMapping("/admin/rechazar")
    public String rechazarContrato(@RequestParam Long id, @RequestParam String motivo) {
        contratoService.rechazarContrato(id, motivo);
        return "redirect:/contratos/admin/contratos";
    }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> descargarContratoPdf(@PathVariable Long id) {
        Contrato contrato = contratoService.listarContratos().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (contrato == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Título
            document.add(new Paragraph("CONTRATO DE REPRESENTACIÓN ARTÍSTICA")
                    .setFont(font)
                    .setBold()
                    .setFontSize(16)
                    .setMarginBottom(20));

            // Introducción formal del contrato
            String intro = String.format("El presente contrato confirma que el artista %s (nombre real: %s), identificado con teléfono %s, ha firmado un acuerdo con nuestra disquera para la representación, lanzamiento y promoción de álbumes y canciones del género %s.",
                    contrato.getNombreArtistico(),
                    contrato.getNombreReal(),
                    contrato.getTelefono() != null ? contrato.getTelefono() : "N/A",
                    contrato.getGeneroMusical() != null ? contrato.getGeneroMusical() : "N/A"
            );
            document.add(new Paragraph(intro).setFont(font).setFontSize(12).setMarginBottom(15));

            // Detalles de duración y regalías
            String detalles = String.format("La duración del contrato es de %d meses, con un porcentaje de regalías del %.2f%% para el artista.",
                    contrato.getDuracionMeses() != null ? contrato.getDuracionMeses() : 0,
                    contrato.getPorcentajeGanancia() != null ? contrato.getPorcentajeGanancia() : 0.0
            );
            document.add(new Paragraph(detalles).setFont(font).setFontSize(12).setMarginBottom(15));

            // Fechas
            String fechas = String.format("El contrato inició el día %s y tiene una vigencia hasta el día %s.",
                    contrato.getFechaInicio() != null ? contrato.getFechaInicio().toString() : "N/A",
                    contrato.getFechaVencimiento() != null ? contrato.getFechaVencimiento().toString() : "N/A"
            );
            document.add(new Paragraph(fechas).setFont(font).setFontSize(12).setMarginBottom(15));

            // Estado actual del contrato
            String estado = contrato.calcularEstado();
            document.add(new Paragraph("Estado actual del contrato: " + estado)
                    .setFont(font)
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(30));

            // Firma del artista (simulada)
            String firma = String.format("Firma del artista: ________________________________\n\n%s",
                    contrato.getNombreArtistico()
            );
            document.add(new Paragraph(firma).setFont(font).setFontSize(12).setMarginBottom(30));

            // Fecha del documento (hoy)
            String fechaHoy = "Fecha del documento: " + java.time.LocalDate.now().toString();
            document.add(new Paragraph(fechaHoy).setFont(font).setFontSize(10).setItalic());

            document.close();

            byte[] pdfBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Contrato_" + contrato.getId() + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

}