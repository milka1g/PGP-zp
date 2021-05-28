package etf.openpgp.mn170387dba170390d;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class PwKeyController {
	

    @FXML
    private AnchorPane pwkey;

    @FXML
    private PasswordField pw1;

    @FXML
    private PasswordField pw2;

    @FXML
    private Label pwErr;
    
    private static Stage stage;
    
    public static void setStage(Stage st) {
		stage = st;
	}
    

    @FXML
    void pwDeleteKey(ActionEvent event) {
    	String pass1 = pw1.getText();
    	String pass2 = pw2.getText();
    	
    	if((pass1.equals("") && pass2.equals(""))) {
    		pwErr.setText("You must insert password!");
    	} else if(!pass1.equals(pass2)) {
    		pwErr.setText("Passwords do not match!");
    	} else if (pass1.length()<3) {
    		pwErr.setText("Minimum length is 3!");
    	}
    	else {
    		StartupController.pw = pass1;
    		stage.close();
    		//stage.close();
    	}
    	
    }

}
