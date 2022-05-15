package simulation.tool.v.pkg2.pkg0;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GeneratingThread extends Thread{
    String filesNameRe,formatRe ,dataRe,delimiterDataRe,rowsRe,timeRe, prePathRe,targetRe;
    Pattern pattern;
    Matcher matcher;
    public GeneratingThread(String name) {
        pattern = Pattern.compile("FilesName: (?<filesNameRe>\\S+) , (?<formatRe>\\S+) , (?<dataRe>\\S+) "
                + ", Delimiter: (?<delimiterDataRe>\\S+) , (?<prePathRe>.+) , (?<targetRe>.+) , (?<timeRe>\\d+) ms , #(?<rowsRe>\\d+)");
        matcher = pattern.matcher(name);
        while (matcher.find()){
            filesNameRe = matcher.group("filesNameRe");
            formatRe = matcher.group("formatRe");
            dataRe = matcher.group("dataRe");
            delimiterDataRe = matcher.group("delimiterDataRe");
            prePathRe = matcher.group("prePathRe");
            targetRe = matcher.group("targetRe");
            timeRe = matcher.group("timeRe");
            rowsRe = matcher.group("rowsRe");
        }
    }
    @Override
    public void run(){
        while(true){
            try{
                Thread.sleep(Integer.parseInt(timeRe));
                createFile();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            } catch (IOException ex) {
                Logger.getLogger(GeneratingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void createFile() throws IOException{
        switch (formatRe) {
            case "json":
                JSON jsonFile = new JSON(Integer.parseInt(rowsRe), prePathRe, filesNameRe, "C");
                String jsonFileName = jsonFile.getName();
                Files.copy(Paths.get(prePathRe + jsonFileName), Paths.get(targetRe + jsonFileName));
                break;
            case "ASN.1":
                ASN1 asnFile = new ASN1(prePathRe, filesNameRe, Integer.parseInt(rowsRe));
                String asnFileName = asnFile.getName();
                Files.copy(Paths.get(prePathRe + asnFileName + ".asn1"), Paths.get(targetRe + asnFileName + ".asn1"));
                break;
            case "csv":
                CSV csvFile = new CSV(Integer.parseInt(rowsRe), prePathRe, filesNameRe, delimiterDataRe, "C" );
                String csvFileName = csvFile.getName();
                Files.copy(Paths.get(prePathRe + csvFileName), Paths.get(targetRe + csvFileName));
        }
    }
}