package it.feliziani.tools.pdf_page_merger.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import it.feliziani.tools.pdf_page_merger.dto.OrderedDTO;
import it.feliziani.tools.pdf_page_merger.dto.OrderedFileDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MergePagesService {

    public ByteArrayOutputStream mergePdfFiles(List<MultipartFile> files, List<OrderedDTO> dati) throws IOException, DocumentException {

        List<OrderedFileDTO> orderedFiles = new ArrayList<>();
        for(OrderedDTO dato : dati) {
            OrderedFileDTO orderedFile = new OrderedFileDTO(dato.getObjName(), dato.getOrder(),findFileByName(dato.getObjName(), files));
            orderedFiles.add(orderedFile);
        }
        orderedFiles.sort(Comparator.comparingInt(OrderedFileDTO::getOrder));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfCopy copy = new PdfCopy(document, outputStream);
        document.open();

        for (OrderedFileDTO file : orderedFiles) {
            try(ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getFile().getBytes())) {
              addPdfToCopy(inputStream, copy);
            }
        }

        document.close();
        return outputStream;
    }

    private static void addPdfToCopy(InputStream pdfInputStream, PdfCopy copy) throws IOException, BadPdfFormatException {
        PdfReader reader = new PdfReader(pdfInputStream);
        int numberOfPages = reader.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            copy.addPage(copy.getImportedPage(reader, i));
        }
        reader.close();
    }

     private static MultipartFile findFileByName(String fileName, List<MultipartFile> files) {
        try {
            for (MultipartFile file : files) {
                if (file.getOriginalFilename().equals(fileName)) {
                    return file;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("File " + fileName + " non trovato");
        }
         return null;
     }


}
