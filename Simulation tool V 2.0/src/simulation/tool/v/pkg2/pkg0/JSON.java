package simulation.tool.v.pkg2.pkg0;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class JSON {
    int rows,fileNameOrder = 0; 
    String tempString,data,filesName ;
    BufferedWriter output ;
    public JSON(int rowsI, String dataI, String path, String fileName){
        data = dataI;
        rows=rowsI;
        filesName = fileName;
        try{
            this.createFile(path);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private void compileDate(){
        DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;  
        LocalDateTime now = LocalDateTime.now(); 
        tempString = tempString.replaceAll("\\<\\%date\\>", "\"date\":\""+dtf.format(now)+"\"");
    }
    
    private void compileDuration(){
        DecimalFormat durationFormatter = new DecimalFormat("00");
        String randomDuration = durationFormatter.format(generateMin_Sec())+":"+
                durationFormatter.format(generateMin_Sec());
        tempString = tempString.replaceAll("\\<\\%duration\\>","\"duration\":\""+randomDuration+"\"");
    }
    
    private void compileCaller(){
        Pattern callerPattern=Pattern.compile("\\<\\\"(\\d{3})\\\"\\>\\;\\<\\%numeric8\\>");
        Matcher callerMatcher=callerPattern.matcher(tempString);
        double randomNumeric;
        while(callerMatcher.find()){
            randomNumeric=(int)Math.floor(Math.random()*((Math.pow(10,8)-1))-0+1)+0;
            String repeat = new String(new char[8]).replace("\0", "0");
            DecimalFormat numericFormatter = new DecimalFormat(repeat);
            String randomNumericStr=numericFormatter.format(randomNumeric);
            tempString=tempString.replaceFirst(callerMatcher.group(0),("\"number2\":\""+callerMatcher.group(1)+randomNumericStr+"\""));
        }
    }
    
    private void compileNumeric_Digits(){
        Pattern numericPattern=Pattern.compile("\\<\\%numeric(\\d*)\\>");
        Matcher numericMatcher=numericPattern.matcher(tempString);
        double randomNumeric;
        while(numericMatcher.find())
            {
                int companies[]={10,11,12,15};
                int randomCompanyIndex=(int)Math.floor(Math.random()*(3-0+1)+0);
                int numberOfDigits=Integer.parseInt(numericMatcher.group(1));
                if(numberOfDigits==11){
                    randomNumeric=companies[randomCompanyIndex];
                    randomNumeric*=100000000.0;
                    randomNumeric+=(int)Math.floor(Math.random()*(((Math.pow(10,numberOfDigits-3)-1))-0+1)+0);
                }
                else
                    randomNumeric=(int)Math.floor(Math.random()*((Math.pow(10,numberOfDigits)-1))-0+1)+0;
                String repeat = new String(new char[Integer.parseInt(numericMatcher.group(1))]).replace("\0", "0");
                DecimalFormat numericFormatter = new DecimalFormat(repeat);
                String randomNumericStr=numericFormatter.format(randomNumeric);
                tempString=tempString.replaceFirst(numericMatcher.group(0),"\"number2\":\""+randomNumericStr+"\"");
            }
    }
    
    private void compileDigits(){
        Pattern integarPattern=Pattern.compile("\\<\\\"(\\d{1,})\\\"\\>");
        Matcher integarMatcher=integarPattern.matcher(tempString);
        if(integarMatcher.find()){
            tempString = tempString.replaceFirst("\\<\\\"(\\d{1,})\\\"\\>\\;", integarMatcher.group(1));
        }
    }
    
    private void compileDataFields(){
        this.compileDate();
        this.compileCaller();
        this.compileDigits();
        this.compileDuration();
        this.compileNumeric_Digits();
    }
    
    private int generateMin_Sec(){
        return (int)Math.floor(Math.random()*60);
    }
    
     private String generateFileName(){
        fileNameOrder++;
        return filesName+"_"+String.valueOf(fileNameOrder);
    }
     
    private void generateData() throws IOException{
        output.write("{\n");
        for(int i=0;i<rows;i++){
            tempString = data;
            this.compileDataFields();
            this.replaceDelimiter();
            if(i!=rows-1)
                output.write("\"row"+String.valueOf(i+1)+"\":{"+tempString+"},\n");
            else
                output.write("\"row"+String.valueOf(i+1)+"\":{"+tempString+"}");
        }
        output.write("\n}");
    }
    
    private void createFile(String path ) throws IOException{
            output = null;
            try {
                File file = new File(path+"\\"+generateFileName()+".json");
                while(file.exists())
                    file = new File(path+"\\"+generateFileName()+".json");
                output = new BufferedWriter(new FileWriter(file));
                this.generateData();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            finally {
                if ( output != null ) {
                    output.close();
                }
            }
    }
    
    private void replaceDelimiter(){
        tempString = tempString.replaceAll(";", ",");
        tempString = tempString.replaceFirst("number2", "number1");
    }
}