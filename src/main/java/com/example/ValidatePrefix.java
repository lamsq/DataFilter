package com.example;

import com.beust.jcommander.*;

public class ValidatePrefix implements IParameterValidator {
    public void validate(String name, String p) throws ParameterException {
        String forbidden = "[\\\\/:*?\"<>|]";
        if (p.matches(".*"+forbidden+".*")) {
            throw new IllegalArgumentException("File name contains special characters: \\ / : * ? \" < > |");
        }
    }
}
