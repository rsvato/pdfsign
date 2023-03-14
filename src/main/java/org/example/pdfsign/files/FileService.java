package org.example.pdfsign.files;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileService {

    @Value("${pdfsign.baseDir}")
    private String base;

    @Value("${pdfsign.tempDir}")
    private String temp;

    @Value("${pdfsign.outputDir}")
    private String out;

    public List<String> listFiles() {
        Path basePath = Paths.get(base);
        List<String> result = new ArrayList<>();
        try (DirectoryStream<Path> d = Files.newDirectoryStream(basePath)) {
            for (Path child: d) {
                String fileName = child.getFileName().toString();
                if (fileName.endsWith(".pdf")) {
                    result.add(fileName);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public byte[] getFile(String name) throws IOException {
        return readFile(base, name);
    }

    public byte[] getTempFile(String name) throws IOException {
        return readFile(temp, name);
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public void createTempFile(byte[] external, String s) {
        writeFile(external, s, temp);
    }

    public void saveSignedFile(String filename, byte[] content) {
        writeFile(content, filename, out);
    }

    private void writeFile(byte[] external, String s, String directory) {
        try (FileOutputStream f = new FileOutputStream(directory + "/" + s)){
            f.write(external);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readFile(String directory, String name) throws IOException {
        return IOUtils.toByteArray(new FileInputStream(directory + "/" + name));
    }
}
