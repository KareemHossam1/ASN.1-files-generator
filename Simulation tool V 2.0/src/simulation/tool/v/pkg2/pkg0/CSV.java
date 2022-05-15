package simulation.tool.v.pkg2.pkg0;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SplittableRandom;
public final class CSV {
    int recordsNum;
    final String[] companiesDigits = {"010","011","012","015"};
    File file;
    String path, fileName, type, delimiter;
    FileWriter fileWriter;
    StringBuilder cdrBuilder;
    public CSV(int recordsNum, String path, String fileName, String delimiter, String type){
        this.fileName = fileName;
        this.type = type;
        this.recordsNum = recordsNum;
        this.path = path;
        this.delimiter = delimiter;
        cdrBuilder = new StringBuilder();
        file = new File(path + "\\" + GenerateFileName());
        try{
            fileWriter = new FileWriter(file);
            createCSVFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void createCSVFile() throws IOException {
        for (int i = 1; i <= recordsNum; i++) {
            cdrBuilder.append(GeneratePhone()).append(delimiter);
            cdrBuilder.append(GeneratePhone()).append(delimiter);
            cdrBuilder.append(System.currentTimeMillis()).append(delimiter);
            cdrBuilder.append(new SplittableRandom().nextInt(1, 3600)).append("\n");
            fileWriter.write(cdrBuilder.toString());
            cdrBuilder.setLength(0);
        }
        fileWriter.close();
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