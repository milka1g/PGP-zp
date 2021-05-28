package etf.openpgp.mn170387dba170390d;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SecOrPubController {

    @FXML
    private RadioButton secretkey;

    @FXML
    private ToggleGroup export;

    @FXML
    private RadioButton publickey;

    @FXML
    private Button decisionBtn;
    
    private static Stage stage;
    private boolean isSecret;
    
    public static void setStage(Stage st) {
		stage = st;
	}

    @FXML
    void export(ActionEvent event) {
    	if(secretkey.isSelected())
    		isSecret = true;
    	if(publickey.isSelected()) 
    		isSecret = false;
    }

    @FXML
    void getDecision(ActionEvent event) {
    	if(secretkey.isSelected())
    		isSecret = true;
    	if(publickey.isSelected()) 
    		isSecret = false;
    	StartupController.exportSecret = isSecret;
    	stage.close();
    }
}
