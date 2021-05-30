package etf.openpgp.mn170387dba170390d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.S2K;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRing;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    public static String pw = "";
    public static boolean exportSecret;
    

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
    	
    	tableRings.setRowFactory(tv -> new TableRow<MyKey>() {
    	    @Override
    	    protected void updateItem(MyKey item, boolean empty) {
    	        super.updateItem(item, empty);
    	        if (item == null || item.isPublic()== true)
    	           setStyle("-fx-background-color: #395f61");
    	        else {
    	        	setStyle("-fx-background-color: #008B8B");
    	        }
    	    }
    	});
    	
  
    	
    	MenuItem mi1 = new MenuItem("Export");
    	MenuItem mi2 = new MenuItem("Delete");
    	
    	
    	mi1.setOnAction((ActionEvent event) -> {
    	    System.out.println("Export");
    	    MyKey item = (MyKey) tableRings.getSelectionModel().getSelectedItem();
    	    if(item!=null) {
    	    	PGPKeyRing p = null;
    	    	String name = "";
    	    	Stage stage= null;
    	    	if(item.isPublic()) {
    	    		try {
						p = Main.pkrcoll.getPublicKeyRing(item.getKeyIdLong());
					} catch (PGPException e) {
						e.printStackTrace();
					}
    	    		name = item.getName()+"PUBLIC.asc";
    	    	} else {
    	    		
    	    		Parent root;
			        try { 
			            root = FXMLLoader.load(getClass().getResource("SecOrPub.fxml"));
			            stage = new Stage();
			            SecOrPubController.setStage(stage);
			            stage.setTitle("Secret or Public");
			            Scene scene = new Scene(root);
			            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			            stage.setScene(scene);
			            stage.setResizable(false);
			            stage.initStyle(StageStyle.UNDECORATED);
			            stage.initModality(Modality.APPLICATION_MODAL);
			            stage.showAndWait();
			        }
			        catch (IOException e) {
			            e.printStackTrace();
			        }
    	    		
			        if(exportSecret) {
			        	try {
							p = Main.skrcoll.getSecretKeyRing(item.getKeyIdLong());
						} catch (PGPException e) {
							e.printStackTrace();
						}
			        	name = item.getName()+"SECRET.asc";
			        } else {
			        	try {
			        		p = Main.pkrcollmy.getPublicKeyRing(item.getKeyIdLong());
						} catch (PGPException e) {
							e.printStackTrace();
						}
			        	name = item.getName()+"PUBLIC.asc";
			        }
    	    		
    	    	}
    	    	try {
    	    	FileChooser fc = new FileChooser();
    	    	fc.setTitle("Choose location");
    	    	fc.getExtensionFilters().add(new ExtensionFilter("ASC files","*.asc"));
    	    	String desktopPath = System.getProperty("user.home") + "/Desktop";
    	    	File init = new File(desktopPath);
    	    	fc.setInitialDirectory(init);
    	    	fc.setInitialFileName(name);
    	    	File file = fc.showSaveDialog(stage);
    	    		OutputStream out = new FileOutputStream(file);
    	    		out = new ArmoredOutputStream(out);
    	    		p.encode(out);
    	    		out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					System.out.println("Nisi selektovo");
				}
    	    }
    	    
    	});
    	
    	mi2.setOnAction((ActionEvent event) -> {
    	    MyKey item = (MyKey) tableRings.getSelectionModel().getSelectedItem();
    	    if(item!=null) {
	    	    PGPDigestCalculatorProvider calcProv = null;
				try {
					calcProv = new JcaPGPDigestCalculatorProviderBuilder().build();
				} catch (PGPException e) {
				}
				
				if(!item.isPublic()) {
					Parent root;
			        try { 
			            root = FXMLLoader.load(getClass().getResource("PwKey.fxml"));
			            Stage stage = new Stage();
			            PwKeyController.setStage(stage);
			            stage.setTitle("Passphrase");
			            Scene scene = new Scene(root, 250, 200);
			            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			            stage.setScene(scene);
			            stage.setResizable(false);
			            stage.initModality(Modality.APPLICATION_MODAL);
			            stage.showAndWait();
			        }
			        catch (IOException e) {
			            e.printStackTrace();
			        }
			        //System.out.println("Brises secret key ID:" + item.getKeyIdLong());
			        PGPSecretKey skr = null;
			        PGPSecretKeyRing skrToDel = null;
			        PGPPublicKeyRing pkrToDel = null;
			        try {
						skrToDel = Main.skrcoll.getSecretKeyRing(item.getKeyIdLong());
						pkrToDel = Main.pkrcollmy.getPublicKeyRing(item.getKeyIdLong());
					} catch (PGPException e1) {
						System.out.println("NEMA TI TOG SECRET KEYRINGA");
					}
			        try {
						 skr = Main.skrcoll.getSecretKey(item.getKeyIdLong());
					} catch (PGPException e) {
						System.out.println("NEMA TI TOG SECRET KLJUCA");
					}
			        //System.out.println("IZVUKAO skr : " + skr.getKeyID());
			      //OVO TI TREBA DA EXTRACTUJES PRIVATE IZ SECRET KEYA.
		    	    PBESecretKeyDecryptor pbeSecretKeyDecryptor = new BcPBESecretKeyDecryptorBuilder(calcProv).build(pw.toCharArray());
		    	    PGPPrivateKey a = null;
		    	    try {
						a = skr.extractPrivateKey(pbeSecretKeyDecryptor);
					} catch (PGPException e) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Bad passphrase");
						alert.setHeaderText("You have entered bad passphrase");
						alert.setContentText("Your secret key is encrypted with different passphrase, please "
								+ "insert the right one. After that you can delete your secret key and its subkey.");
						alert.showAndWait();
					}
		    	    if(a!=null) {
		    	    	//vreme da se obrise iz Main.skrcoll 
		    	    	Main.skrcoll = PGPSecretKeyRingCollection.removeSecretKeyRing(Main.skrcoll, skrToDel);
		    	    	if(pkrToDel!=null) //jer mozda si brisao secret koji nisi ti pravio pa nemas nista u pkrcollmy
		    	    		Main.pkrcollmy = PGPPublicKeyRingCollection.removePublicKeyRing(Main.pkrcollmy, pkrToDel);
		    	    	refreshz();
		    	    }
				} else {
					//brise se javni kljuc, samo ga izbrisi i tjt
					PGPPublicKeyRing pkr = null;
					try {
						 pkr = Main.pkrcoll.getPublicKeyRing(item.getKeyIdLong());
					} catch (PGPException e) {
						System.out.println("Nema ti ovog public keyringa");
						e.printStackTrace();
					}
					if(pkr!=null) {
						Main.pkrcoll = PGPPublicKeyRingCollection.removePublicKeyRing(Main.pkrcoll, pkr);
						refreshz();
					}
				}
    	    }
    	}); 
    	
    	ContextMenu menu = new ContextMenu();
    	menu.getItems().addAll(mi1,mi2);
    	tableRings.setContextMenu(menu);
    	
	}

    @FXML
    void decryptOrVerifyFile(ActionEvent event) {
    	Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("DecrVerif.fxml"));
            Stage stage = new Stage();
            DecrVerifController.stage = stage;
            stage.setTitle("Decrypt and/or Verify");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        }
        catch (IOException e) {
            e.printStackTrace();
        }

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
    void importKey(ActionEvent event) throws IOException, PGPException {
    	FileChooser fc = new FileChooser();
    	fc.setTitle("Choose key to import");
    	fc.getExtensionFilters().add(new ExtensionFilter("ASC files","*.asc"));
    	String desktopPath = System.getProperty("user.home") + "/Desktop";
    	File init = new File(desktopPath);
    	fc.setInitialDirectory(init);
    	File file = fc.showOpenDialog(Main.primaryStage);
    	if(file!=null) {
    		System.out.println(file.getName());
    		try {
				InputStream in = new FileInputStream(file);
				in = new ArmoredInputStream(in);
				PGPPublicKeyRing pkr = new BcPGPPublicKeyRing(in);
				Main.pkrcoll = PGPPublicKeyRingCollection.addPublicKeyRing(Main.pkrcoll, pkr);
				refreshz();
				System.out.println("UVZEN " + pkr.getPublicKey().getKeyID());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				try {
				//ne citas public nego secreet
				System.out.println("E pa citas pogresan, ovo ti je SECRET");
				InputStream in = new FileInputStream(file);
				in = new ArmoredInputStream(in);
				PGPSecretKeyRing skr = new BcPGPSecretKeyRing(in);
				Main.skrcoll = PGPSecretKeyRingCollection.addSecretKeyRing(Main.skrcoll, skr);
				refreshz();
				} catch (Exception ee) {
					System.out.println("Imas duplikat SECRETA");
				}
				//e.printStackTrace();
			} catch (Exception e){
				System.out.println("POKUSAO SI ISTI DA METNES");
				
			}
    	}
    }

    @FXML
    void signOrEncryptFile(ActionEvent event) {
    	Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("SignEncrypt.fxml"));
            Stage stage = new Stage();
            SignEncryptController.setStage(stage);
            stage.setTitle("Sign and/or encrypt");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    	
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
    		long keyIdLong = skr.getSecretKey().getKeyID();
    		MyKey newKey = new MyKey(name,email,keyID, keyIdLong, false);
    		myKeys.add(newKey);
    		//System.out.println(newKey.toString());
    	}
    	
    	Iterator<PGPPublicKeyRing> itp = Main.pkrcoll.getKeyRings();
    	while(itp.hasNext()) {
    		PGPPublicKeyRing pkr = itp.next();
    		String name="",email="",keyID="";
    		Iterator<String> itattr = pkr.getPublicKey().getUserIDs();
    		while(itattr.hasNext()) {
    			String attr = itattr.next();
    			String[] split = attr.split("<");
    			name = split[0];
    			email = split[1].replace(">", "");
    		}
    		keyID = Long.toHexString(pkr.getPublicKey().getKeyID());
    		long keyIdLong = pkr.getPublicKey().getKeyID();
    		MyKey newKey = new MyKey(name,email,keyID, keyIdLong, true);
    		myKeys.add(newKey);
    	}
    	
    	return myKeys;
    }
    
    
    public void refreshz() {
    	tableRings.setItems(getKeys());
    	tableRings.refresh();
    }
    

}
