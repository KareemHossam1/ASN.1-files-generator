package simulation.tool.v.pkg2.pkg0;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SplittableRandom;
public final class JSON {
    int recordsNum;
    final String[] companiesDigits = {"010","011","012","015"};
    File file;
    String path, fileName, type;
    public JSON(int recordsNum, String path, String fileName, String type){
        this.fileName = fileName;
        this.type = type;
        this.recordsNum = recordsNum;
        this.path = path;
        file = new File(path + "\\" + GenerateFileName() );
        createJsonFile();
    }
    private void createJsonFile(){
        JSONArray cdrList = new JSONArray();
        for (int i = 1; i <= recordsNum; i++) {
            JSONObject cdr = new JSONObject();
            cdr.put("numberA", GeneratePhone());
            cdr.put("numberB", GeneratePhone());
            cdr.put("timestamp", System.currentTimeMillis());
            cdr.put("duration", new SplittableRandom().nextInt(1, 3600));
            cdrList.add(cdr);
        }
        //Write JSON file
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(cdrList.toJSONString().replace("},","},\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String GeneratePhone(){
        return companiesDigits[new SplittableRandom().nextInt(0, 4)]
                + String.valueOf(new SplittableRandom().nextInt(100000000, 199999999)).substring(1);
    }
    private String GenerateFileName(){
        fileName += "_" + System.currentTimeMillis() + ".json";
        return fileName;
    }
    public String getName(){
        return fileName;
    }
}