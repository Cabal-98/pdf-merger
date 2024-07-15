package it.feliziani.tools.pdf_page_merger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

import it.feliziani.tools.pdf_page_merger.dto.OrderedDTO;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;

public class PDFMergerApp extends JFrame {
    private JTextField outputFileName;
    private JButton addFilesButton;
    private JButton mergeButton;
    private JButton resetButton;
    private JTextArea fileListArea;
    private List<File> files;

    public PDFMergerApp() {
        files = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setTitle("PDF Merger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setLayout(new BorderLayout());

        outputFileName = new JTextField("MergedPDF.pdf");
        addFilesButton = new JButton("Add Files");
        mergeButton = new JButton("Upload and Merge");
        resetButton = new JButton("Reset");
        fileListArea = new JTextArea();
        fileListArea.setEditable(false);

        addFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFiles();
            }
        });

        mergeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    uploadAndMergeFiles();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(outputFileName, BorderLayout.CENTER);
        topPanel.add(addFilesButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(mergeButton);
        bottomPanel.add(resetButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(fileListArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void addFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                files.add(file);
                fileListArea.append(file.getName() + "\n");
            }
        }
    }

    private void uploadAndMergeFiles() throws IOException {
        if (files.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select files to upload.");
            return;
        }

        String outputFileName = this.outputFileName.getText();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://localhost:8080/ordered-merge");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        for (File file : files) {
            builder.addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY));
        }

        List<OrderedDTO> body = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            body.add(new OrderedDTO(files.get(i).getName(), i + 1));
        }
        String jsonBody = new Gson().toJson(body);
        builder.addTextBody("body", jsonBody, ContentType.APPLICATION_JSON);

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity responseEntity = response.getEntity();
                byte[] result = EntityUtils.toByteArray(responseEntity);
                Files.write(new File(outputFileName).toPath(), result);
                JOptionPane.showMessageDialog(this, "PDF merged successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to merge PDF files.");
            }
        }
    }

    private void resetFields() {
        files.clear();
        fileListArea.setText("");
        outputFileName.setText("MergedPDF.pdf");
    }
}
