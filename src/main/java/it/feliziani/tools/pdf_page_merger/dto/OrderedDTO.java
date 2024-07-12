package it.feliziani.tools.pdf_page_merger.dto;

import org.springframework.web.multipart.MultipartFile;

public class OrderedDTO {

    private String objName;
    private int order;

    public OrderedDTO(String objName, int order) {
        this.objName = objName;
        this.order = order;
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
