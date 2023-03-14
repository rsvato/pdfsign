package org.example.pdfsign;

import com.itextpdf.text.DocumentException;
import org.apache.commons.io.IOUtils;
import org.example.pdfsign.files.FileService;
import org.example.pdfsign.signer.PdfSignService;
import org.example.pdfsign.signer.SignedFileHolder;

import java.io.*;
import java.security.GeneralSecurityException;

public class TestSign {

    public static void main(String[] args) throws IOException, DocumentException, GeneralSecurityException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileService fileService = new FileService();
        fileService.setBase("examples");
        PdfSignService pdfSignService = new PdfSignService(fileService);
        pdfSignService.createExternalSignatureContainer("test.pdf", "abcd");
        String src = new String(IOUtils.toByteArray(new FileInputStream("examples/hash")));
        SignedFileHolder holder = pdfSignService.sign("test.pdf", "abcd", src.getBytes());
        try (FileOutputStream fos = new FileOutputStream("signed/" + holder.name())) {
            fos.write(holder.content());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
