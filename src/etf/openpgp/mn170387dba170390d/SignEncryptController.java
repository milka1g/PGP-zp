package etf.openpgp.mn170387dba170390d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class SignEncryptController implements Initializable {
	
	@FXML
    private ChoiceBox<String> signChoicebox;

    @FXML
    private CheckBox signCheckBox;

    @FXML
    private CheckBox encryptCheckbox;

    @FXML
    private ChoiceBox<String> encryptChoicebox;

    @FXML
    private CheckBox zipCheckbox;

    @FXML
    private CheckBox radixCheckbox;
    
    @FXML
    private Button okButton;
    
    @FXML
    private Button fileButton;

    @FXML
    private Label filename;
    
    private static Stage stage;
    
    OutputStream out = null;
    InputStream in = null;
    private boolean encrypt=false, radix=false, zip=false, sign=false;
    private String selSecretKey = "", selPublicKey="";
    
    
    
    public static void setStage(Stage st) {
		stage = st;
	}
	

    @FXML
    void encryptCheckboxChange(ActionEvent event) {
    	if(encryptCheckbox.isSelected()) {
    		encrypt = true;
    		encryptChoicebox.setDisable(false);
    	}
    	else {
    		encrypt = false;
    		encryptChoicebox.setDisable(true);
    	}
    }

    @FXML
    void radixCheckboxChange(ActionEvent event) {
    	if(radixCheckbox.isSelected())
    		radix = true;
    	else radix = false;
    }

    @FXML
    void signCheckboxChange(ActionEvent event) {
    	if(signCheckBox.isSelected())
    		sign = true;
    	else sign= false;
    	if(sign) {
    		signChoicebox.setDisable(false);
    	} else 
    		signChoicebox.setDisable(true);
    }

    @FXML
    void zipCheckboxChange(ActionEvent event) {
    	if(zipCheckbox.isSelected()) {
    		zip = true;
    	}
    	else {
    		zip= false;
    	}
    }
    
    
    @FXML
    void submit(ActionEvent event) {
    	
    }
    
    @FXML
    void selectFile(ActionEvent event) {
    	FileChooser fc = new FileChooser();
    	fc.setTitle("Choose file to sign and/or encrypt");
    	fc.getExtensionFilters().add(new ExtensionFilter("Text files","*.txt"));
    	fc.getExtensionFilters().add(new ExtensionFilter("Word files","*.docx"));
    	fc.getExtensionFilters().add(new ExtensionFilter("PDF files","*.pdf"));
    	String desktopPath = System.getProperty("user.home") + "/Desktop";
    	File init = new File(desktopPath);
    	fc.setInitialDirectory(init);
    	File file = fc.showOpenDialog(stage);
    	if(file!=null) {
    		filename.setText(file.getName());
			try {
				in = new FileInputStream(file);
				in = new ArmoredInputStream(in);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		signChoicebox.getItems().addAll(getSecretKeys());
		signChoicebox.setOnAction(this::getSecretKey);
		
		encryptChoicebox.getItems().addAll(getPublicKeys());
		encryptChoicebox.setOnAction(this::getPublicKey);
		
		encryptChoicebox.setDisable(true);
		signChoicebox.setDisable(true);
	}
	
	private List<String> getSecretKeys(){
		ArrayList<String> list = new ArrayList<>();
		
		Iterator<PGPSecretKeyRing> it = Main.skrcoll.getKeyRings();
    	while(it.hasNext()) {
    		PGPSecretKeyRing skr = it.next();
    		String at="",keyID="";
    		Iterator<String> itattr = skr.getSecretKey().getUserIDs();
    		while(itattr.hasNext()) {
    			at = itattr.next();
    		}
    		keyID = Long.toHexString(skr.getSecretKey().getKeyID());
    		list.add(at + "   " + keyID);
    		
    	}
		return list;
	}
	
	private List<String> getPublicKeys(){
		ArrayList<String> list = new ArrayList<>();
		
		Iterator<PGPPublicKeyRing> it = Main.pkrcoll.getKeyRings();
    	while(it.hasNext()) {
    		PGPPublicKeyRing skr = it.next();
    		String at="",keyID="";
    		Iterator<String> itattr = skr.getPublicKey().getUserIDs();
    		while(itattr.hasNext()) {
    			at = itattr.next();
    		}
    		keyID = Long.toHexString(skr.getPublicKey().getKeyID());
    		list.add(at + "   " + keyID);
    		
    	}
		return list;
	}
	
	
	
	public void getSecretKey(ActionEvent e) {
		String cut = signChoicebox.getValue();
		selSecretKey = cut.substring(cut.length()-16);
		System.out.println("selektovo si secret " + selSecretKey);
	}
	
	public void getPublicKey(ActionEvent e) {
		String cut = encryptChoicebox.getValue();
		selPublicKey = cut.substring(cut.length()-16);
		System.out.println("selektovo si public " + selPublicKey);
	}

}
