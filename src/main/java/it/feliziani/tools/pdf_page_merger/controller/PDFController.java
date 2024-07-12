package it.feliziani.tools.pdf_page_merger.controller;

import com.itextpdf.text.DocumentException;
import it.feliziani.tools.pdf_page_merger.dto.OrderedDTO;
import it.feliziani.tools.pdf_page_merger.service.MergePagesService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class PDFController {

    private final MergePagesService merge;

    @CrossOrigin
    @PostMapping(value = "/ordered-merge", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Resource> mergePDF(@RequestPart(value = "file") List<MultipartFile> files,
                                             @RequestPart(value = "body") List<OrderedDTO> dati) throws IOException, DocumentException {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String today = now.format(formatter);
        String fileName = String.format("MergedPDF_v_%s.pdf",today);

        ByteArrayOutputStream out = merge.mergePdfFiles(files,dati);
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }

    @GetMapping(value = "/info")
    public String tutorial() {
        return """
                Il metodo '/ordered-merge' consuma:
                un insieme di File (multipart/form-data) come List<MultiPartFile> con chiave 'file'
                un json che descrive l'ordine dei file con chiave 'body' nel formato:
                \t[
                \t    {"fileName": "file1.pdf", "order": 1},
                \t    {"fileName": "file2.pdf", "order": 2},
                \t    {"fileName": "file3.pdf", "order": 3},
                \t    ...
                \t    {"fileName": "fileN.pdf", "order": N},
                \t  ]
                che viene convertito in una List<OrderedDTO>
                """;
    }


}
