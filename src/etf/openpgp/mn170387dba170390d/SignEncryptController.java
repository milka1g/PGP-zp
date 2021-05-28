package etf.openpgp.mn170387dba170390d;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SignEncryptController implements Initializable {
	
	@FXML
    private ChoiceBox<?> signChoicebox;

    @FXML
    private CheckBox signCheckBox;

    @FXML
    private CheckBox encryptCheckbox;

    @FXML
    private ChoiceBox<?> encryptChoicebox;

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
    
    public static void setStage(Stage st) {
		stage = st;
	}
	

    @FXML
    void encryptCheckboxChange(ActionEvent event) {

    }

    @FXML
    void radixCheckboxChange(ActionEvent event) {

    }

    @FXML
    void signCheckboxChange(ActionEvent event) {

    }

    @FXML
    void zipCheckboxChange(ActionEvent event) {

    }
    
    @FXML
    void submit(ActionEvent event) {

    }
    
    @FXML
    void selectFile(ActionEvent event) {

    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}
	

}
