package org.example.pdfsign.web;


import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.example.pdfsign.files.FileService;
import org.example.pdfsign.signer.HashResponse;
import org.example.pdfsign.signer.PdfSignService;
import org.example.pdfsign.signer.SignedFileHolder;
import org.example.pdfsign.utils.FieldNameGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class FilesController {
    private final FileService listings;
    private final PdfSignService signer;
    private final FieldNameGenerator generator;

    public FilesController(FileService listings, PdfSignService signer,
                           FieldNameGenerator generator) {
        this.listings = listings;
        this.signer = signer;
        this.generator = generator;
    }

    @GetMapping("/files")
    public List<String> files() {
        return listings.listFiles();
    }

    @GetMapping("/files/{file}/hash")
    public HashResponse hash(@PathVariable("file") String file) {
        String fieldName = generator.generateFieldName();
        HashResponse response = signer.createExternalSignatureContainer(file, fieldName);
        return response;
    }

    @PutMapping("/files/{file}/{field}/sign")
    public ResponseEntity<String> sign(@PathVariable("file") String file,
                                       @PathVariable("field") String field,
                                       HttpServletRequest request) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] req = IOUtils.toByteArray(inputStream);
        SignedFileHolder holder = signer.sign(file, field, req);
        return ResponseEntity.ok(holder.name());
    }

}
