package simulation.tool.v.pkg2.pkg0;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
public class GUI {
    
    int threadsNumber,editedItemIndex;
    Label titleLbl,dataLbl,delimiterLbl,formatLbl,pathLbl,timeLbl,rowsLbl,filesNameLbl;
    ComboBox formatCombo,delimiterCombo;
    TextField dataTxt,pathTxt,timeTxt,rowsTxt,filesNameTxt;
    Button generateBtn,stopBtn,resetBtn,addBtn,editBtn,copyBtn,removeBtn,saveBtn,loadBtn;
    ListView list;
    ObservableList items;
    List<GeneratingThread> threads;
    HBox dataH,delimiterH,formatH, pathH,rowsH,timeH,filesNameH,addH,btnH;
    VBox listV,mainV;
    Stage primaryStage;
    String editedItem;
    
    public GUI(Stage primaryStage){
        guiBuilder();
        buttonsEventHandellers();
        Scene scene = new Scene(mainV, 410, 710);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Simulation tool");
        primaryStage.setScene(scene);
        primaryStage.show();
}
    
    private void guiBuilder(){
        
        titleLbl = new Label("       Simulation Tool V 4.0");
        titleLbl.setFont(new Font("Arial", 30));
        dataLbl = new Label("Data\t          ");
        delimiterLbl =new Label("Delimeter  ");
        formatLbl = new Label("Format      ");
        pathLbl = new Label("Path\t          ");
        timeLbl = new Label("Time(ms)   ");
        rowsLbl = new Label("Rows         ");
        filesNameLbl = new Label("Files Name");
        
        dataTxt = new TextField();
        dataTxt.setPrefWidth(290);
        delimiterCombo=new ComboBox();
        formatCombo = new ComboBox();
        timeTxt = new TextField("1000");
        pathTxt = new TextField("D:\\DATA\\Level 04\\Graduation project\\Test");
        pathTxt.setPrefWidth(290);
        dataTxt.setText("<%date>;<\"011\">;<%numeric8>;<%numeric11>;<%duration>");
        rowsTxt = new TextField("2");
        filesNameTxt = new TextField("Kareem");
        
        generateBtn = new Button("Generate");
        stopBtn = new Button("Stop");
        resetBtn = new Button("Reset");
        addBtn = new Button("+");
        addBtn.setMaxWidth(80);
        editBtn = new Button("Edit");
        editBtn.setMaxWidth(80);
        copyBtn = new Button("Copy");
        copyBtn.setMaxWidth(80);
        removeBtn = new Button("Remove");
        removeBtn.setMaxWidth(80);
        saveBtn = new Button("Save");
        saveBtn.setMaxWidth(80);
        loadBtn = new Button("Load");
        loadBtn.setMaxWidth(80);
        
        list = new ListView();
        threads = new ArrayList<>();
        
        dataH = new HBox(20);
        delimiterH=new HBox(20);
        btnH = new HBox(20);
        btnH.setPadding(new Insets(0,0,0,80));
        formatH = new HBox(20);
        pathH = new HBox(20);
        rowsH = new HBox(20);
        timeH = new HBox(20);
        addH = new HBox(20);
        filesNameH = new HBox(20);
        
        listV = new VBox(15);
        mainV = new VBox(15);
        
        delimiterCombo.getItems().addAll(",","/","-","_",";");
        delimiterCombo.setPromptText(",");
        delimiterCombo.getSelectionModel().select(0);
        
        formatCombo.getItems().addAll("txt","json","csv","ASN.1");
        formatCombo.setPromptText("ASN.1");
        formatCombo.getSelectionModel().select(3);
        
        stopBtn.setDisable(true);
        list.setPrefHeight(200);
        list.setPrefWidth(287);
        
        dataH.getChildren().addAll(dataLbl,dataTxt);
        delimiterH.getChildren().addAll(delimiterLbl,delimiterCombo);
        formatH.getChildren().addAll(formatLbl,formatCombo);
        pathH.getChildren().addAll(pathLbl,pathTxt);
        rowsH.getChildren().addAll(rowsLbl,rowsTxt);
        timeH.getChildren().addAll(timeLbl,timeTxt);
        filesNameH.getChildren().addAll(filesNameLbl,filesNameTxt);
        btnH.getChildren().addAll(generateBtn,stopBtn,resetBtn);
        listV.getChildren().addAll(addBtn,editBtn,copyBtn,removeBtn,saveBtn,loadBtn);
        addH.getChildren().addAll(listV,list);
        mainV.getChildren().addAll(titleLbl,dataH,pathH,timeH,rowsH,filesNameH,formatH,delimiterH,addH,btnH);
        mainV.setPadding(new Insets(20,0,0,15));
        
    }
    
    private void buttonsEventHandellers(){
        
        generateBtn.setOnAction((event) -> {
            items = list.getItems();
            items.forEach((item)->{
                GeneratingThread thread = new GeneratingThread(item.toString());
                thread.start();
                threads.add(thread);
            });
            generateBtn.setDisable(true);
            resetBtn.setDisable(true);
            stopBtn.setDisable(false);
            addBtn.setDisable(true);
            editBtn.setDisable(true);
            copyBtn.setDisable(true);
            removeBtn.setDisable(true);
            saveBtn.setDisable(true);
            loadBtn.setDisable(true);
        });
        
        stopBtn.setOnAction((event)->{
            threads.forEach((thread)->{
                thread.stop();
            });
            generateBtn.setDisable(false);
            resetBtn.setDisable(false);
            stopBtn.setDisable(true);
            addBtn.setDisable(false);
            editBtn.setDisable(false);
            copyBtn.setDisable(false);
            removeBtn.setDisable(false);
            saveBtn.setDisable(false);
            loadBtn.setDisable(false);
        });
        
        resetBtn.setOnAction((event->{
            list.getItems().clear();
            dataTxt.setText("");
            pathTxt.setText("");
            timeTxt.setText("1000");
            rowsTxt.setText("1000");
            filesNameTxt.setText("");
            formatCombo.getSelectionModel().select(0);
            delimiterCombo.getSelectionModel().select(0);
        }));
        
        addBtn.setOnAction((event)->{
            list.getItems().add("FilesName: " + filesNameTxt.getText()+" , "+formatCombo.getValue()+" , "+
                    dataTxt.getText()+" , Delimiter: "+delimiterCombo.getValue()+" , "+
                    Paths.get(pathTxt.getText()).toAbsolutePath()+" , "+timeTxt.getText()+
                    " ms , #" + rowsTxt.getText());
            list.getSelectionModel().select(list.getItems().size()-1);
        });
        
        editBtn.setOnAction((event)->{
            if("Edit".equals(editBtn.getText())){
                editBtn.setText("Finish");
                editedItem=String.valueOf(list.getSelectionModel().getSelectedItem());
                editedItemIndex=list.getSelectionModel().getSelectedIndex();
                putConfiguration(editedItem);
                
                list.setDisable(true);
                generateBtn.setDisable(true);
                resetBtn.setDisable(true);
                stopBtn.setDisable(true);
                addBtn.setDisable(true);
                copyBtn.setDisable(true);
                removeBtn.setDisable(true);
                saveBtn.setDisable(true);
                loadBtn.setDisable(true);
            }
            else{
                editBtn.setText("Edit");
                list.getItems().set(editedItemIndex, "FilesName: "+filesNameTxt.getText()+" , "
                        +formatCombo.getValue()+" , "+dataTxt.getText()+" , Delimiter: "+delimiterCombo.getValue()
                        +" , "+Paths.get(pathTxt.getText()).toAbsolutePath()+" , "+timeTxt.getText()+
                        " ms , #" + rowsTxt.getText());
                
                list.setDisable(false);
                generateBtn.setDisable(false);
                resetBtn.setDisable(false);
                stopBtn.setDisable(true);
                addBtn.setDisable(false);
                copyBtn.setDisable(false);
                removeBtn.setDisable(false);
                saveBtn.setDisable(false);
                loadBtn.setDisable(false);
            } 
        });
        
        copyBtn.setOnAction((event)->{
            list.getItems().add(list.getSelectionModel().getSelectedItem());
        });
        
        removeBtn.setOnAction((event->{
            list.getItems().remove(list.getSelectionModel().getSelectedItem());
        }));
        
        FileChooser saveFile = new FileChooser();
        saveFile.setTitle("Save");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("configSimulation", "*.configSimulation");
        saveFile.getExtensionFilters().add(extFilter);
        saveBtn.setOnAction((event)->{
            File fileSaved =saveFile.showSaveDialog(primaryStage);
            if(fileSaved!=null){
                PrintWriter writer;
                try {
                    writer = new PrintWriter(fileSaved);
                    for(int i=0;i<list.getItems().size();i++)
                        writer.println(String.valueOf(list.getItems().get(i)));
                    writer.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        FileChooser loadFile = new FileChooser();
        loadFile.setTitle("Load");
        loadFile.getExtensionFilters().add(extFilter);
        loadBtn.setOnAction((event)->{
            list.getItems().remove(list.getSelectionModel().getSelectedItem());
            File fileLoaded =loadFile.showOpenDialog(primaryStage);
            if (fileLoaded != null) {
                try {
                    Scanner read = new Scanner(fileLoaded);
                    while(read.hasNextLine()){
                        list.getItems().add(read.nextLine());
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
        });
    }
    
    private void putConfiguration(String config){
        Pattern searchForRegex=Pattern.compile("FilesName: (?<filesNameRe>\\S+) , (?<formatRe>\\S+) , (?<dataRe>\\S+) "
                    + ", Delimiter: (?<delimiterDataRe>\\S+) , (?<pathRe>.+) , (?<timeRe>\\d+) ms , #(?<rowsRe>\\d+)");
            Matcher foundRegex=searchForRegex.matcher(config);
            while (foundRegex.find()){
                filesNameTxt.setText(foundRegex.group("filesNameRe"));
                formatCombo.getSelectionModel().select(foundRegex.group("formatRe"));
                dataTxt.setText(foundRegex.group("dataRe"));
                delimiterCombo.getSelectionModel().select(foundRegex.group("delimiterDataRe"));
                pathTxt.setText(foundRegex.group("pathRe"));
                timeTxt.setText(foundRegex.group("timeRe"));
                rowsTxt.setText(foundRegex.group("rowsRe"));
            }
    }

}