package etf.openpgp.mn170387dba170390d;

import java.io.IOException;
import java.net.URL;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import etf.openpgp.mn170387dba170390d.NewKeyPairController;

public class StartupController implements Initializable {
	
	@FXML
    private AnchorPane mainScene;
	
	@FXML
    private Button buttonNew;

    @FXML
    private ImageView imageViewNewKey;

    @FXML
    private Button buttonImport;

    @FXML
    private ImageView imageViewImportKey;

    @FXML
    private Button buttonSign;

    @FXML
    private ImageView imageViewSign;

    @FXML
    private Button buttonDecrypt;

    @FXML
    private ImageView imageViewDecrypt;
    
    @FXML
    private TableView<MyKey> tableRings;

    @FXML
    private TableColumn<MyKey, String> NameTab;

    @FXML
    private TableColumn<MyKey, String> emailTab;

    @FXML
    private TableColumn<MyKey, String> keyIDTab;
    
    ///////////////GENERISANJE PARA KLJUCEVA//////////////////////////////////
    int rsaKeyLength = 0;
    String name = "";
    String email = "";
    String passPhrase = "";
    KeyPairGenerator kpg = null;

    //Image keyImg = new Image(getClass().getResourceAsStream("key.png"));
   // Image keyAddImg = new Image(getClass().getResourceAsStream("keyadd.png"));
    //Image lockImg = new Image(getClass().getResourceAsStream("encr.png"));
    //Image unlockImg = new Image(getClass().getResourceAsStream("unlock.png"));
    
    @SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

    	NameTab.setCellValueFactory(new PropertyValueFactory<>("name"));
    	emailTab.setCellValueFactory(new PropertyValueFactory<>("email"));
    	keyIDTab.setCellValueFactory(new PropertyValueFactory<>("keyID"));
    	
    	tableRings.setItems(getKeys());
    	tableRings.setPlaceholder(new Label("No keys to display"));
    	
	}

    @FXML
    void decryptOrVerifyFile(ActionEvent event) {

    }
    

    @FXML
    void generateKeyPair(ActionEvent event) {
    	Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("NewKeyPair.fxml"));
            Stage stage = new Stage();
            NewKeyPairController.setStage(stage);
            stage.setTitle("Generate New Key Pair");
            Scene scene = new Scene(root, 450, 450);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void importKey(ActionEvent event) {

    }

    @FXML
    void signOrEncryptFile(ActionEvent event) {

    }
    
    public ObservableList<MyKey> getKeys(){
    	
    	ObservableList<MyKey> myKeys = FXCollections.observableArrayList();
    	
    	Iterator<PGPSecretKeyRing> it = Main.skrcoll.getKeyRings();
    	while(it.hasNext()) { //za svaki kljuc
    		PGPSecretKeyRing skr = it.next();
    		String name="",email="",keyID="";
    		Iterator<String> itattr = skr.getSecretKey().getUserIDs(); //samo za master ce izvuci jedan id ime<email>
    		while(itattr.hasNext()) {
    			String attr = itattr.next();
    			String[] split = attr.split("<");
    			name = split[0];
    			email = split[1].replace(">", "");
    		}
    		keyID = Long.toHexString(skr.getSecretKey().getKeyID());
    		MyKey newKey = new MyKey(name,email,keyID);
    		myKeys.add(newKey);
    		System.out.println(newKey.toString());
    	}
    	
    	return myKeys;
    }
    
    
    public void refreshz() {
    	tableRings.setItems(getKeys());
    	tableRings.refresh();
    	System.out.println("KURCINA");
    }

}
