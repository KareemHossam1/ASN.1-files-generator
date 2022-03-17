package simulation.tool.v.pkg2.pkg0;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class GeneratingThread extends Thread{
    String filesNameRe,formatRe ,dataRe,delimiterDataRe,rowsRe,timeRe,pathRe,tmpString;
    Pattern pattern;
    Matcher matcher;
    int fileName = 0;
    BufferedWriter output ;
    public GeneratingThread(String name) {
        pattern = Pattern.compile("FilesName: (?<filesNameRe>\\S+) , (?<formatRe>\\S+) , (?<dataRe>\\S+) "
                + ", Delimiter: (?<delimiterDataRe>\\S+) , (?<pathRe>.+) , (?<timeRe>\\d+) ms , #(?<rowsRe>\\d+)");
        matcher = pattern.matcher(name);
        while (matcher.find()){
            filesNameRe = matcher.group("filesNameRe");
            formatRe = matcher.group("formatRe");
            dataRe = matcher.group("dataRe");
            delimiterDataRe = matcher.group("delimiterDataRe");
            pathRe = matcher.group("pathRe");
            timeRe = matcher.group("timeRe");
            rowsRe = matcher.group("rowsRe");
        }
    }
    
    @Override
    public void run(){
        while(true){
            try{
                Thread.sleep(Integer.valueOf(timeRe));
                createFile(pathRe,formatRe);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            } catch (IOException ex) {
                Logger.getLogger(GeneratingThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void createFile(String path, String format ) throws IOException{
        switch (formatRe) {
            case "json":
                JSON jsonFile = new JSON(Integer.valueOf(rowsRe),dataRe,pathRe,filesNameRe);
                break;
            case "ASN.1":
                ASN1 ASN1File = new ASN1(pathRe,filesNameRe,Integer.valueOf(rowsRe));
                break;
            default:
                output = null;
                try {
                    String realRowsRe=rowsRe;
                    if(Integer.parseInt(rowsRe)<=1000000){
                        File file = new File(path+"\\"+generateFileName()+"_0"+"."+format);
                        while(file.exists())
                            file = new File(path+"\\"+generateFileName()+"_0"+"."+format);
                        output = new BufferedWriter(new FileWriter(file));
                        this.generateData();
                    }
                    else{
                        fileName++;
                        int temp=0;
                        int rowsReTemp=Integer.parseInt(rowsRe);
                        while(rowsReTemp>0){
                            if(rowsReTemp>=1000000)
                                rowsRe="1000000";
                            else
                                rowsRe=String.valueOf(rowsReTemp);
                            File file = new File(path+"\\"+fileName+"_"+temp+"."+format);
                            while(file.exists())
                                file = new File(path+"\\"+generateFileName()+"_"+temp+"."+format);
                            output = new BufferedWriter(new FileWriter(file));
                            this.generateData();
                            output.close();
                            temp++;
                            rowsReTemp-=1000000;
                        }
                    }
                    rowsRe=realRowsRe;
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
                finally {
                    if ( output != null ) {
                        output.close();
                    }
                }   break;
        }
    }
    
    private String generateFileName(){
        fileName++;
        return this.filesNameRe+ "_" + String.valueOf(fileName);
    }
    
    private void generateData() throws IOException{
        int rowsNumber=1;
        for(int i=0;i<Integer.parseInt(rowsRe);i++){
            tmpString=dataRe;
            this.replaceDelimiter();
            this.compileDataFields();            
            output.write(tmpString+"\n");
            rowsNumber++;
        }
    }
    
    public void compileDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now(); 
        tmpString=tmpString.replaceAll("\\<\\%date\\>", dtf.format(now));
    }
    
    public void compileDuration(){
        DecimalFormat durationFormatter = new DecimalFormat("00");
        String randomDuration=durationFormatter.format((int)generateRandomBetween(0,59))+
                ":"+durationFormatter.format((int)generateRandomBetween(0,59));
        tmpString=tmpString.replaceAll("\\<\\%duration\\>",randomDuration);
    }
    
    public void compileDigits(){
        Pattern integarPattern=Pattern.compile("\\<\\\"(\\d{1,})\\\"\\>");
        Matcher integarMatcher=integarPattern.matcher(tmpString);
        if(integarMatcher.find()){
            tmpString=tmpString.replaceFirst(("\\<\\\"(\\d{1,})\\\"\\>"+delimiterDataRe), integarMatcher.group(1));
        }
    }
    
    public void compileNumeric_Digits(){
        Pattern numericPattern=Pattern.compile("\\<\\%numeric(\\d*)\\>");
        Matcher numericMatcher=numericPattern.matcher(tmpString);
        double randomNumeric;
        while(numericMatcher.find())
            {
                int companies[]={10,11,12,15};
                
                int randomCompanyIndex=(int)generateRandomBetween(3,0);
                int numberOfDigits=Integer.parseInt(numericMatcher.group(1));
                if(numberOfDigits==11){
                    randomNumeric=companies[randomCompanyIndex];
                    randomNumeric*=100000000;
                    randomNumeric+=generateRandomBetween((Math.pow(10,numberOfDigits-3)-1.0),0);
                }else
                    randomNumeric=generateRandomBetween((Math.pow(10,numberOfDigits)-1.0),0);
                String repeat = new String(new char[numberOfDigits]).replace("\0", "0");
                DecimalFormat numericFormatter = new DecimalFormat(repeat);
                String randomNumericStr=numericFormatter.format(randomNumeric);
                tmpString=tmpString.replaceFirst(numericMatcher.group(0),randomNumericStr);
            }
    }
    
    public void compileDataFields(){
        this.compileDate();
        this.compileDigits();
        this.compileDuration();
        this.compileNumeric_Digits();
        
    }
    
    public void replaceDelimiter(){
        tmpString=tmpString.replaceAll(("\\>(\\D)\\<"),(">"+delimiterDataRe+"<"));
    }
    
    public double generateRandomBetween(double min,double max){
        return Math.floor(Math.random()*(max-min+1)+min);
    }
}