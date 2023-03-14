package org.example.pdfsign.signer;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

public class BrowserSignatureContainer implements ExternalSignatureContainer {

    private final byte[] data;

    public BrowserSignatureContainer(byte[] hash) throws IOException {
        this.data = Base64.decodeBase64(hash);
    }

    @Override
    public byte[] sign(InputStream data) throws GeneralSecurityException {
        return this.data;
    }

    @Override
    public void modifySigningDictionary(PdfDictionary signDic) {}
}
