package simulation.tool.v.pkg2.pkg0;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SplittableRandom;
public final class ASN1 {
    final String[] companiesDigits = {"30 ","31 ","32 ","35 "}; // Vodafone = 30, Etisalat = 31, Orange = 32, We = 35
    int CDRsNumber , CDRsLengthWithoutHeader =0 , fileNumber = 0, addLineIteration = 1, addLineCounter = 0;
    BufferedWriter fileBuffer;
    File fileFile;
    String CDR;
    public ASN1(String path, String fileName, int rows){
        CDRsNumber = rows;
        try{
            createFile( path , fileName );
        }
        catch(IOException e){
            System.out.println("IOException in constructor of ASN1");
        }
    }
    
    private void createFile(String path, String fileName) throws IOException{
        fileBuffer = null;
        try{
            fileFile = new File(path + "\\" + fileName + generateFileName()+".asn1");
            while(fileFile.exists()){
                fileFile = new File(path + "\\" + fileName + generateFileName()+".asn1");
            }
            fileBuffer = new BufferedWriter(new FileWriter(fileFile));
            /*  
                Firstly, write the file header
                "ff ff ff ff" ---> Initial values of File length
                "nn nn nn nn" ---> Initial values of CDRs Number
                The rest of values are fixed bytes of the header
            */
            fileBuffer.write("ff ff ff ff 00 00 00 32 a2 a2 b6 2d da 00 b6 2d da 00 nn nn nn nn 00 00 00 01 04 a7 01 02 c7 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            for(int i=0 ; i< CDRsNumber ; i++){ // Generate CDRs
                fileBuffer.write(" " + generateCDR());
            }
        }
        catch ( IOException e ) {
            System.out.println("IOException in createFile()");
        }
        finally {
            if ( fileBuffer != null ) {
                fileBuffer.close();
            }
        }
        modifyFile(fileFile);
    }
    
    private String generateFileName(){
        fileNumber++;
        return "_" + fileNumber;
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
        String returnString = "";
        for (char ch : asciiField.toCharArray()) {
            returnString += Integer.toHexString(ch) + " ";
        }
        return returnString;
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
        String randomDuration =  intToHex( new SplittableRandom().nextInt(1, 360000000), 0);
        //     Tag         + Length of duration value                  + " " + Duration Value
        return "9f 81 48 " + intToHex((randomDuration.length()+1)/3, 0)+ " " + randomDuration;    
    }
    
    private String intToHex(int integerField, int numberOfHexBytes){
        String returnString = Integer.toHexString(integerField);
        switch (numberOfHexBytes) {
            case 0:
                // 0 means it doesn't matter the number of bytes, but it should be an even number
                if(returnString.length() % 2 != 0){
                    returnString = "0" + returnString;
                }   break;
            default:
                while(returnString.length() < numberOfHexBytes){
                    returnString = "0" + returnString;
                }   break;
        }
        returnString = returnString.replaceAll("..(?!$)", "$0 ");  // Add a space after every 2 letters
        return returnString;
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
    
    /*
        This method : 1- Calculates real values of file length and number of CDRs 
                      2- Modifies the file header with these values
                      3- Add \n after every 32 pair of characters instead of writing all data in a single line
    */
    private void modifyFile(File file) throws IOException{
        RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
        // File length
        randomFile.seek(0); // Go to first character
        // File length(8 bytes) = File header length (50) + CDR Header length (4) * Number of CDRs + Total CDRs length without headers 
        String fileLengthString = intToHex( 50 + 4 * CDRsNumber + CDRsLengthWithoutHeader , 8);
        randomFile.write(fileLengthString.getBytes());
        // Number of CDRs
        randomFile.seek(54);   // 54 is the position of CDRs number bytes
        randomFile.write(intToHex(CDRsNumber, 8).getBytes());
        // This program writes all data in one line, so we need to add a line after every 32 byte
        while(95 * addLineIteration + addLineCounter < randomFile.length()){
            randomFile.seek(95 * addLineIteration + addLineCounter);  
            randomFile.write("\n".getBytes());
            addLineIteration++;
            addLineCounter++;
        }
        randomFile.close();
    }
}
// Added ASN.1 to combo box
// Added if statement for generating ASN.1 to a new class
// Switch Case
// My Case