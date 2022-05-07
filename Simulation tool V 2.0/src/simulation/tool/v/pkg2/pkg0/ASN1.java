package simulation.tool.v.pkg2.pkg0;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SplittableRandom;

public final class ASN1 {
    final String[] companiesDigits = {"30 ","31 ","32 ","35 "}; // Vodafone = 30, Etisalat = 31, Orange = 32, We = 35
    int CDRsNumber , CDRsLengthWithoutHeader =0 ;
    BufferedWriter fileBuffer;
    File fileFile;
    String CDR;
    StringBuilder fileNameBuilder;
    public ASN1(String path, String fileName, int rows){
        CDRsNumber = rows;
        fileNameBuilder = new StringBuilder(fileName);
        try{
            createFile( path , fileNameBuilder );
        }
        catch(IOException e){
            System.out.println("IOException in constructor of ASN1");
        }
    }
    
    private void createFile(String path, StringBuilder fileNameBuilder) throws IOException{
        fileBuffer = null;
        try{
            fileFile = new File(path + "\\" + generateFileName(fileNameBuilder)+".asn1");
            fileBuffer = new BufferedWriter(new FileWriter(fileFile));
            /*  
                Firstly, write the file header
                "ff ff ff ff" ---> Initial values of File length
                "nn nn nn nn" ---> Initial values of CDRs Number
                The rest of values are fixed bytes of the header
            */
            fileBuffer.write("ff ff ff ff 00 00 00 32 a2 a2 b6 2d da 00 b6 2d da 00 nn nn nn nn 00 00 00 01 04 a7 01 02 c7 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            for(int i=0 ; i< CDRsNumber ; i++){ // Generate CDRs
                fileBuffer.write("\n" + generateCDR());
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        finally {
            if ( fileBuffer != null ) {
                fileBuffer.close();
            }
        }
    }
    
    private StringBuilder generateFileName(StringBuilder fileNameBuilder){
        fileNameBuilder.append("_").append(System.currentTimeMillis());
        return fileNameBuilder;
    }
    
    private String generateCDR(){
        CDR = listOfCallingPartyAddress() + calledPartyAddress() + serviceRequestTimeStamp() + duration();
        CDR = addCDRHeader(CDR) + CDR;
        return CDR;
    }
    
    private String listOfCallingPartyAddress(){
        /*  
            Tag "a6 13 81 11" + Value
            Length will always = 17 decimal (11 hex) 
            Phone number value will always start with "tel:+201" = "74 65 6c 3a 2b 32 30 31" 
            Firstly we will choose a company randomly
            Secondly we will generate random sequence of 8 numbers
        */ 
        //     TaG                                   + Random Company Digit
        return "a6 13 81 11 74 65 6c 3a 2b 32 30 31 "+ companiesDigits[new SplittableRandom().nextInt(0, 4)]
                // Choose random value from 100000000 to 199999999 then remove first digit to get a random 8-digits number
                + stringToHex(String.valueOf(new SplittableRandom().nextInt(100000000, 199999999)).substring(1)); 
    }
    
    private String stringToHex(String asciiField){
        StringBuilder returnString = new StringBuilder();
        for (char ch : asciiField.toCharArray()) {
            returnString.append(Integer.toHexString(ch)).append(" ");
        }
        return returnString.toString();
    }
    
    private String calledPartyAddress(){
        /*  
            Tag "a7 13 81 11" + Value
            Length will always = 17 decimal (11 hex) 
            Phone number value will always start with "tel:+201" = "74 65 6c 3a 2b 32 30 31" 
            Firstly we will choose a company randomly
            Secondly we will generate random sequence of 8 numbers
        */ 
        //     TaG  + Random Company Digit
        return "a7 13 81 11 74 65 6c 3a 2b 32 30 31 "+ companiesDigits[new SplittableRandom().nextInt(0, 4)]
                // Choose random value from 100000000 to 199999999 then remove first digit to get a random 8-digits number
                + stringToHex(String.valueOf(new SplittableRandom().nextInt(100000000, 199999999)).substring(1)); 
    }
    
    private String serviceRequestTimeStamp(){
        Date date = new Date();
        String nowDate = new SimpleDateFormat("yy MM dd HH mm ss").format(date);
        //     Tag      + Value        + "+02:00"
        return "89 09 " + nowDate + " 2B 02 00 "; 
    }
    
    private String duration(){
        String randomDuration =  intToHex( new SplittableRandom().nextInt(100001, 360000000), 0);
        //     Tag         + Length of duration value                  + " " + Duration Value
        return "9f 81 48 " + intToHex((randomDuration.length()+1)/3, 0)+ " " + randomDuration;    
    }
    
    private String intToHex(int integerField, int numberOfHexBytes){
        StringBuilder returnString = new StringBuilder(Integer.toHexString(integerField));
        if (numberOfHexBytes == 0) {// 0 means it doesn't matter the number of bytes, but it should be an even number
            if (returnString.length() % 2 != 0) {
                returnString.insert(0, "0");
            }
        } else {
            while (returnString.length() < numberOfHexBytes) {
                returnString.insert(0, "0");
            }
        }
        returnString = new StringBuilder(returnString.toString().replaceAll("..(?!$)", "$0 "));  // Add a space after every 2 letters
        return returnString.toString();
    }
    
    private String addCDRHeader(String CDRWithoutHeader){
        int CDRsLengthInBytes = (CDRWithoutHeader.length()+1)/3;
        /*
            This variable will be used in file header not CDR header.
            It's used to calculate the total length of CDRs without headers
        */
        CDRsLengthWithoutHeader += CDRsLengthInBytes;
        // Calculate CDR Length without spaces in terms of bytes (2 Chars)
        return intToHex(CDRsLengthInBytes, 4)+" a2 29 ";
    }

    public String getName(){
        return fileNameBuilder.toString();
    }
}