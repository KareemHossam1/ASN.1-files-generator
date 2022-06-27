package simulation.tool.v.pkg2.pkg0;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.SplittableRandom;
public final class CSV {
    int recordsNum;
    final String[] companiesDigits = {"010","011","012","015"};
    File file;
    String path, fileName, type, delimiter;
    FileWriter fileWriter;
    StringBuilder recordBuilder;
    Random randomReading;
    public CSV(int recordsNum, String path, String fileName, String delimiter, String type){
        this.fileName = fileName;
        this.type = type;
        this.recordsNum = recordsNum;
        this.path = path;
        this.delimiter = delimiter;
        this.randomReading = new Random();
        this.recordBuilder = new StringBuilder();
        file = new File(path + "\\" + GenerateFileName());
        try{
            fileWriter = new FileWriter(file);
            switch (type) {
                case "C":
                    createCdrCsvFile();
                case "M":
                    createMeterCsvFile();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void createCdrCsvFile() throws IOException {
        for (int i = 1; i <= recordsNum; i++) {
            recordBuilder.append(GeneratePhone()).append(delimiter);
            recordBuilder.append(GeneratePhone()).append(delimiter);
            recordBuilder.append(System.currentTimeMillis()).append(delimiter);
            recordBuilder.append(new SplittableRandom().nextInt(1, 3600)).append("\n");
            fileWriter.write(recordBuilder.toString());
            recordBuilder.setLength(0);
        }
        fileWriter.close();
    }
    private void createMeterCsvFile() throws IOException {
        Long endDate = generateTimestamp();
        Long startDate = endDate - 900;
        for (int i = 1000000; i <= recordsNum+1000000; i++) {
            recordBuilder.append(generateRecordType()).append(delimiter);
            recordBuilder.append(startDate).append(delimiter);
            recordBuilder.append(endDate).append(delimiter);
            recordBuilder.append(generateMeterSerial(i)).append(delimiter);
            recordBuilder.append(generateUOM()).append(delimiter);
            recordBuilder.append(generateReading()).append("\n");
            fileWriter.write(recordBuilder.toString());
            recordBuilder.setLength(0);
        }
        fileWriter.close();
    }

    private String generateRecordType(){
        return "U";
    }
    private Long generateTimestamp(){
        return System.currentTimeMillis() / 1000L;
    }
    private String generateMeterSerial(int id){
        return "SER" + id;
    }
    private String generateUOM(){
        return "KWH";
    }
    private float generateReading(){
        return (randomReading.nextFloat() * ((30 - 1) + 1)) + 1;
    }

    private String GeneratePhone(){
        return companiesDigits[new SplittableRandom().nextInt(0, 4)]
                + String.valueOf(new SplittableRandom().nextInt(100000000, 199999999)).substring(1);
    }
    private String GenerateFileName(){
        fileName += "_" + System.currentTimeMillis()  + ".csv";
        return fileName;
    }
    public String getName(){
        return fileName;
    }
}