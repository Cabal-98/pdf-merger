package it.feliziani.tools.pdf_page_merger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
public class PdfPageMergerApplication {

	public static void main(String[] args) {

		SpringApplication.run(PdfPageMergerApplication.class, args);
		System.setProperty("java.awt.headless", "false");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new PDFMergerApp().setVisible(true);
			}
		});
	}

}
