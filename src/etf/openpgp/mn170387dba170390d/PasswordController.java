package etf.openpgp.mn170387dba170390d;

import java.io.IOException;

import org.bouncycastle.openpgp.PGPException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PasswordController {

	  @FXML
	    private PasswordField passwordField1;

	    @FXML
	    private PasswordField passwordField2;
	    
	    @FXML
	    private Label pwError;
	    
	    private static Stage stage;
	    
	    public static void setStage(Stage st) {
			stage = st;
		}
	    
	  
	    @FXML
	    void passwordCheckAndGen(ActionEvent event) throws PGPException, IOException {
	    	String pw1 = passwordField1.getText();
	    	String pw2 = passwordField2.getText();

	    	if((pw1.equals("") && pw2.equals(""))) {
	    		pwError.setText("You must insert password!");
	    	} else if(!pw1.equals(pw2)) {
	    		pwError.setText("Passwords do not match!");
	    	} else if (pw1.length()<3) {
	    		pwError.setText("Minimum password length is 3!");
	    	}
	    	else {
	    		pwError.setTextFill(Color.BLACK);
	    		pwError.setText("Generating keys, please wait...");
	    		NewKeyPairController.setPwAndGen(pw1);
	    		stage.close();
	    		//stage.close();
	    	}
	    	
	    }
}
