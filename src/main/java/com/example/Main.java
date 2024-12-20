package com.example;

import com.beust.jcommander.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;


public class Main {

    @Parameter(names="-o", description="Custom path for resulting files", validateWith = ValidatePath.class)
    private static String path="";

    @Parameter(names="-p", description="Prefix for resulting files", validateWith = ValidatePrefix.class)
    private static String prefix = "";

    @Parameter(names="-a", description="Adjunct data to existing files")
    private static boolean append;

    @Parameter(names="-s", description="Statistics mode short")
    private static boolean statShort;

    @Parameter(names="-f", description="Statistics mode full")
    private static boolean statFull;

    @Parameter(description = "Input files")
    private static List<String> inputData = new ArrayList<>();

    private static List<File> inputFiles = new ArrayList<>();
    private static List<String> stringData = new ArrayList<>();
    private static List<Long> longData = new ArrayList<>();
    private static List<Double> doubleData = new ArrayList<>();

    public static void main(String[] args) {

        System.out.println("Execution starting...");

        //parsing arguments
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);

        //checks if input files are present
        if(!inputData.isEmpty()) {
            for(String s : inputData){
                File file = new File(s);
                if(file.exists())
                    inputFiles.add(file);
                else
                    System.out.println(s + " does not exist;");
            }
        }
        else {
            throw new IllegalArgumentException("No input files specified;");
        }

        //creating file objects
        File str = new File(path+prefix+"strings.txt");
        File longs = new File(path+prefix+"integer.txt");
        File doub = new File(path+prefix+"floats.txt");

        //mapping files to corresponding data structures
        Map<File, List<?>> dataMap = new HashMap<>();
        dataMap.put(str, stringData);
        dataMap.put(longs, longData);
        dataMap.put(doub, doubleData);

        //parsing the original data and adding it to the corresponding data structure
        for(File f : inputFiles){
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        long nimLong = Long.parseLong(line);
                        longData.add(nimLong);
                        continue;
                    }
                    catch (NumberFormatException e) {

                    }
                    try {
                        double numDouble = Double.parseDouble(line);
                        doubleData.add(numDouble);
                        continue;
                    }
                    catch (NumberFormatException e) {

                    }
                    stringData.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //deleting entries with empty data structures
        for(Map.Entry<File, List<?>> entry : dataMap.entrySet()){
            if(dataMap.get(entry.getKey()).isEmpty())
                dataMap.remove(entry.getKey());
        }

        //prints statistics for each type of data (depends on the stat flag)
        for(Map.Entry<File, List<?>> entry : dataMap.entrySet()){
            List<?> value = dataMap.get(entry.getKey());
            File key = entry.getKey();
            if(statShort || statFull){
                System.out.println(getShortStat(key, value));
                if(statFull)
                    System.out.print(getFullStat(value));
            }
        }

        //writes data into the corresponding file
        for (Map.Entry<File, List<?>> entry : dataMap.entrySet()) {
            File file = entry.getKey();
            List<?> value = entry.getValue();
            writeData(file, value, append);
        }

        System.out.println("\nExecution completed;");
    }

    public static void writeData(File file, List<?> data, boolean a) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, a))) {
            for (Object item : data) {
                writer.write(item.toString());
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getShortStat(File file, List<?> data){
        StringBuilder stat = new StringBuilder("");

        stat.append("\nStatistics for "+file.getName()+" new records:")
                .append("\nNumber of elements: "+data.size());

        return stat.toString();
    }

    public static String getFullStat(List<?> data){
        DoubleStream stream;

        //checks the data type
        if (data.get(0) instanceof String)
            stream = data.stream().map(String.class::cast).mapToDouble(String::length);
        else if (data.get(0) instanceof Long)
            stream = data.stream().map(Long.class::cast).mapToDouble(Long::doubleValue);
        else
            stream = data.stream().map(Double.class::cast).mapToDouble(Double::doubleValue);

        //gets statistics
        DoubleSummaryStatistics stats = stream.summaryStatistics();
        StringBuilder stat = new StringBuilder("");

        stat.append("Max/Longest: "+stats.getMax())
                .append("\nMin/Shortest: "+stats.getMin())
                .append("\nSum: "+stats.getSum())
                .append("\nAvg: "+stats.getAverage())
                .append("\n");

        return stat.toString();
    }
}







