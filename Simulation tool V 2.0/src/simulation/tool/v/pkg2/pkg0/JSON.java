package simulation.tool.v.pkg2.pkg0;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.SplittableRandom;
public final class JSON {
    int recordsNum;
    final String[] companiesDigits = {"010","011","012","015"};
    File file;
    String path, fileName, type;
    Random randomReading;
    public JSON(int recordsNum, String path, String fileName, String type){
        this.fileName = fileName;
        this.type = type;
        this.recordsNum = recordsNum;
        this.path = path;
        file = new File(path + "\\" + generateFileName() );
        this.randomReading = new Random();
        switch (type){
            case "C": createCDRsJsonFile();
            case "M": createMetersJsonFile();
        }


    }
    private void createCDRsJsonFile(){
        JSONArray cdrList = new JSONArray();
        for (int i = 1; i <= recordsNum; i++) {
            JSONObject cdr = new JSONObject();
            cdr.put("numberA", generatePhone());
            cdr.put("numberB", generatePhone());
            cdr.put("timestamp", System.currentTimeMillis());
            cdr.put("duration", new SplittableRandom().nextInt(1, 3600));
            cdrList.add(cdr);
        }
        //Write JSON file
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(cdrList.toJSONString().replace("},","},\n")); // To add line after every record
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMetersJsonFile(){
        Long endDate = generateTimestamp();
        Long startDate = endDate - 900;
        JSONArray meterList = new JSONArray();
        for (int i = 1000000; i <= recordsNum+1000000; i++) {
            JSONObject meter = new JSONObject();
            meter.put("record_type_id", generateRecordType());
            meter.put("start_datetime", startDate);
            meter.put("end_datetime", endDate);
            meter.put("device_id", generateMeterSerial(i));
            meter.put("uom_alias", generateUOM());
            meter.put("reading", generateReading());
            meterList.add(meter);
        }
        //Write JSON file
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(meterList.toJSONString().replace("},","},\n")); // To add line after every record
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Meters Methods
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


    // CDRs Methods
    private String generatePhone(){
        return companiesDigits[new SplittableRandom().nextInt(0, 4)]
                + String.valueOf(new SplittableRandom().nextInt(100000000, 199999999)).substring(1);
    }
    private String generateFileName(){
        fileName += "_" + System.currentTimeMillis() + ".json";
        return fileName;
    }
    public String getName(){
        return fileName;
    }
}