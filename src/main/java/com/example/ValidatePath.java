package com.example;

import com.beust.jcommander.*;
import java.io.*;
import java.nio.file.*;

public class ValidatePath implements IParameterValidator {
    public void validate(String name, String p) throws ParameterException {

        Path path = Paths.get(p);

        if (Files.exists(path) && !Files.isDirectory(path))
            throw new ParameterException(path.toAbsolutePath()+" path is not a directory;");

        if (!Files.isDirectory(path)) {
            System.out.println(p+" not found\nCreating directory...");
            try {
                Files.createDirectories(path);
                System.out.println(path.toAbsolutePath()+" directory created;");
            } catch (IOException e) {
                throw new ParameterException("Check the path and try again", e);
            }
        }
    }
}
