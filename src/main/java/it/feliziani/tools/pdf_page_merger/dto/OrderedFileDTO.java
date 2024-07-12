package it.feliziani.tools.pdf_page_merger.dto;

import org.springframework.web.multipart.MultipartFile;

public class OrderedFileDTO extends OrderedDTO {

    private MultipartFile file;

    public OrderedFileDTO(String fileName, int order, MultipartFile file) {
        super(fileName, order);
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
