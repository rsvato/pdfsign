package org.example.pdfsign.signer;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.ExternalBlankSignatureContainer;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;
import com.itextpdf.text.pdf.security.MakeSignature;
import org.apache.commons.io.IOUtils;
import org.example.pdfsign.files.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Map;

@Component
public class PdfSignService {
    private final FileService fileService;
    public PdfSignService(FileService fileService) {
        this.fileService = fileService;
    }

    public HashResponse createExternalSignatureContainer(String filename, String signatureFieldName) {
        try {
            byte[] file = fileService.getFile(filename);
            PdfReader pdfReader = new PdfReader(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            char pdfVersion = pdfReader.getPdfVersion();
            PdfStamper stamper = PdfStamper.createSignature(pdfReader, bos, pdfVersion, null, true);
            PdfSignatureAppearance signatureAppearance = stamper.getSignatureAppearance();
            Rectangle signatureRectangle = new Rectangle(40, 740, 180, 790); //TODO: calculate place at the end of document???
            signatureAppearance.setVisibleSignature(signatureRectangle, pdfReader.getNumberOfPages(), signatureFieldName);
            ExternalSignatureContainer container = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE,
                    PdfName.ADBE_PKCS7_DETACHED);
            MakeSignature.signExternalContainer(signatureAppearance, container, 8192);
            InputStream rangeStream = signatureAppearance.getRangeStream();
            byte[] tempFile = bos.toByteArray();
            fileService.createTempFile(tempFile, makeTempFileName(filename, signatureFieldName));
            byte[] hashData = IOUtils.toByteArray(rangeStream);
            return new HashResponse(signatureFieldName, Base64.getEncoder().encodeToString(hashData));
        } catch (GeneralSecurityException | IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private static String makeTempFileName(String filename, String signatureFieldName) {
        return signatureFieldName + "." + filename;
    }

    public SignedFileHolder sign(String filename, String field, byte[] signature) throws IOException {
        try {
            BrowserSignatureContainer container = new BrowserSignatureContainer(signature);
            byte[] file = fileService.getTempFile(makeTempFileName(filename, field));
            PdfReader reader = new PdfReader(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            MakeSignature.signDeferred(reader, field, bos, container);
            byte[] content = bos.toByteArray();
            fileService.saveSignedFile(filename, content);
            return new SignedFileHolder(filename, content);
        } catch (IOException | GeneralSecurityException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
