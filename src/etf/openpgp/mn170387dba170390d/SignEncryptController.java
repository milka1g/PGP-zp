package etf.openpgp.mn170387dba170390d;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.Deflater;

import javax.swing.filechooser.FileNameExtensionFilter;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
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
    private CheckBox zipCheckbox;

    @FXML
    private CheckBox radixCheckbox;
    
    @FXML
    private Button okButton;
    
    @FXML
    private Button fileButton;

    @FXML
    private Label filename;
    
    @FXML
    private ListView<String> encryptList;
    
    @FXML
    private RadioButton algo3des;

    @FXML
    private ToggleGroup algo;

    @FXML
    private RadioButton algoidea;
    
    private static Stage stage;
    
   // OutputStream out = null;
    InputStream in = null;
    private boolean encrypt=false, radix=false, zip=false, sign=false;
    private String selSecretKey = "", selPublicKey="";
    private ObservableList<String> selPublicKeys;
    private List<String> selPublicKeyHexIDs;
    public static String pw;
    private String msg;
    public static int algorithm= PGPEncryptedData.TRIPLE_DES;
    
    public static final int BUFFER_SIZE=4096;
    
    
    public static void setStage(Stage st) {
		stage = st;
	}
	

    @FXML
    void encryptCheckboxChange(ActionEvent event) {
    	if(encryptCheckbox.isSelected()) {
    		encrypt = true;
    		encryptList.setDisable(false);
    		algo3des.setDisable(false);
    		algoidea.setDisable(false);
    	}
    	else {
    		encrypt = false;
    		encryptList.setDisable(true);
    		algo3des.setDisable(true);
    		algoidea.setDisable(true);
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
    	////////////////////PROVERA AKO NEMAMO OTVOREN FAJL NE VREDI NISTA RADITI//////////////////
    	if(filename.getText().equals("") || filename.getText().isEmpty()) {
    		Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("No file found");
			alert.setHeaderText("No file selected for signing and/or encryption");
			alert.setContentText("You have to select the file you want to sign and/or encrypt");
			alert.showAndWait();
			return;
    	}
    	byte[] arr = {};  //u ovom nizu su svi bajtovi koje cemo mi upisati u nas izlazni fajl
    	///////////////////////////////////////////////////////////////////////////////////////////
    	//ByteArrayOutputStream enables you to capture data written to a stream in a byte array.
    	//You write your data to the ByteArrayOutputStream and when you are done
    	//you call the its toByteArray() method to obtain all the written data in a byte array
    	//znaci ti pises u bcarrout (u prazno bukvalno) ali to hvatas u barr i mozes da izvuces to sa barr.toByteArray()
    	ByteArrayOutputStream barr = new ByteArrayOutputStream();
    	BCPGOutputStream bcarrout = new  BCPGOutputStream(barr); 
    	
    	//new String(bytes, StandardCharsets.UTF_8);
    	
    	/////////////////////DA SE PROCITA NAS INPUT FILE////////////////////
    	ByteArrayOutputStream input = new ByteArrayOutputStream(); //da iscitamo in inputstream 
    	try {
    		byte[] buf = new byte[BUFFER_SIZE];
    		int i;
			while((i = in.read(buf,0,BUFFER_SIZE))>-1) { //citas u chunkovima od 4096 i upisujes u ovaj input, return je kolko B je procitao
				input.write(buf,0,i); //upises sve iz buf u input
			}
			
		} catch (IOException e2) {}
    	
    	byte inputarr[] = input.toByteArray(); //sad imamo citav ulaz procitan u bajtovima
    	System.out.println( " AAAA" + new String(inputarr, StandardCharsets.UTF_8));
    	try {
			input.close();
		} catch (IOException e2) {}
    	
    	//////////////////////////////////////////////////////////////////////
    	//System.out.println("PROVERA1: "+new String(inputarr, StandardCharsets.UTF_8)); ovo radi
    	
    	PGPSignatureGenerator signatureGenerator = null;
    	PGPEncryptedDataGenerator encryptedDataGenerator = null;
    	PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
    	
    	OutputStream out = null;
    	File file = null;
    	
    	//////////////////ODREDJUJEMO GDE CEMO SACUVATI NAS FAJL///////////////////
    	FileChooser fc = null;
    	
    	String[] split = filename.getText().split("\\.");
    	String name = split[0] + (sign?"Signed":"")+ (encrypt?"Encrypted":"") + (zip?"ZIP":"")+(radix?"R64":"") + "." + split[1];
    	try {
	    	fc = new FileChooser();
	    	fc.setTitle("Choose location");
	    	String desktopPath = System.getProperty("user.home") + "/Desktop";
	    	File init = new File(desktopPath);
	    	fc.setInitialDirectory(init);
	    	fc.setInitialFileName(name);
	    	file = fc.showSaveDialog(stage);
	    	if(file==null)
	    		return;
	    	out = new FileOutputStream(file);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("Nisi selektovao");
			}
    	
   
    	/**
    	 * Digital signatures enable the recipient of information to verify
    	 * the authenticity of the information's origin, and also verify that
    	 * the information is intact. Thus, public key digital signatures provide
    	 * authentication and data integrity. A digital signature also provides
    	 * non-repudiation, which means that it prevents the sender from claiming
    	 * that he or she did not actually send the information. These features
    	 * are every bit as fundamental to cryptography as privacy, if not more.
    	 *
    	 * A one-way hash function takes variable-length input - in this case,
    	 *  a message of any length, even thousands or millions of bits -
    	 *  and produces a fixed-length output; say, 160-bits. The hash function
    	 *  ensures that, if the information is changed in any way-  even by just one bit
    	 *  - an entirely different output value is produced.
    	 *  
    	 *  PGP uses a cryptographically strong hash function on the plaintext
    	 *  the user is signing. This generates a fixed-length data item known as
    	 *  a message digest. (Again, any change to the information results in a totally different digest.
    	 *  
    	 *  Then PGP uses the digest and the private key to create the 'signature'. 
    	 *  PGP transmits the signature and the plaintext together. Upon receipt of
    	 *  the message, the recipient uses PGP to recompute the digest, thus verifying
    	 *  the signature. PGP can encrypt the plaintext or not; signing plaintext is useful
    	 *  if some of the recipients are not interested in or capable of verifying the signature.
    	  */
    	if(sign) {
    		PGPDigestCalculatorProvider calcProv = null;
			try {
				calcProv = new JcaPGPDigestCalculatorProviderBuilder().build();
			} catch (PGPException e) {
			}
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
	        PGPSecretKey sk = null;
	        try {
				 sk = Main.skrcoll.getSecretKey(Long.parseUnsignedLong(selSecretKey,16));
			} catch (PGPException e) {
				System.out.println("NEMA TI TOG SECRET KLJUCA");
			}

    	    PBESecretKeyDecryptor pbeSecretKeyDecryptor = new BcPBESecretKeyDecryptorBuilder(calcProv).build(pw.toCharArray());
    	    PGPPrivateKey privateKey = null;
    	    try {
    	    	privateKey = sk.extractPrivateKey(pbeSecretKeyDecryptor);
			} catch (PGPException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Bad passphrase");
				alert.setHeaderText("You have entered bad passphrase");
				alert.setContentText("Your secret key is encrypted with different passphrase, please "
						+ "insert the correct one.");
				alert.showAndWait();
				return;
			}
    	    
    	    
            signatureGenerator = new PGPSignatureGenerator(new BcPGPContentSignerBuilder(sk.getPublicKey().getAlgorithm(),
            		HashAlgorithmTags.SHA1)); // mozda PGPPublicKey.RSA_GENERAL?
            try {
				signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey);
				
				signatureGenerator.generateOnePassVersion(false).encode(bcarrout); //upise header od potpisa
				
				OutputStream sOut = literalDataGenerator.open(bcarrout, PGPLiteralData.BINARY, 
						name, inputarr.length, new Date()); //vidi name da obrises 
				for(int i=0; i!=inputarr.length; i++) {
					sOut.write(inputarr[i]);
					signatureGenerator.update(inputarr[i]);
				}

				signatureGenerator.generate().encode(bcarrout);
				sOut.close();
				bcarrout.close();
				
				arr = barr.toByteArray(); //sad tu imamo prvo ovaj header pa celu poruku pa potpis na kraju
				//na dalje vidis dal ces to da kompresujes pa enkriptujes
				System.out.println("PROVERA sign: "+new String(arr, StandardCharsets.UTF_8));
				
				
			} catch (PGPException e) {
				e.printStackTrace();
			} 
            catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	if(zip) {
    		ByteArrayOutputStream pomOut = new ByteArrayOutputStream();
			PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZIP);
			if(!sign) {
				try {
					PGPUtil.writeFileToLiteralData(compressedDataGenerator.open(pomOut), PGPLiteralData.BINARY, file);
					//System.out.println("PROVERA !sign u ZIP: "+new String(pomOut.toByteArray(), StandardCharsets.UTF_8));
				} catch (IOException e) {
					e.printStackTrace();
				}
				//da ne moramo da se cimamo sa arr nego samo ova utility fja da prepise izabran fajl od gore u pomOut
			} else {
				//e jebiga sad moras da wrappujes u kompresor ovaj pomOut i da fake upisujes u neki OutputStream tj
				// da u pomOut ispadne da si upisao kompresovan sadrzaj pa  izvuces sa .toByteArray()
				try {
					OutputStream po = compressedDataGenerator.open(pomOut);
					int i = 0;
					while(i<arr.length) { //corrected
						po.write(arr[i]);
						i++;
					}
					po.close();
					//pomOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			arr = pomOut.toByteArray(); //sta god da si uradio samo metnes opet u glavni arr
			System.out.println("PROVERA sign u ZIPU: "+new String(arr, StandardCharsets.UTF_8));
			
		}
    	
    	if(!sign && !zip) {
            ByteArrayOutputStream hout = new ByteArrayOutputStream();
            try {
                PGPUtil.writeFileToLiteralData(hout, PGPLiteralData.BINARY, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            arr =  hout.toByteArray();
    	}
    	
    	if(encrypt){
    		//dovati kljuceve selektovane
    		List<PGPPublicKey> pubKeys = new ArrayList<>();
    		for(String s : selPublicKeyHexIDs) {
    			try {
					pubKeys.add(Main.pkrcoll.getPublicKey(Long.parseUnsignedLong(s,16)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (PGPException e) {
					e.printStackTrace();
				}
    		}
    		
    		encryptedDataGenerator = new PGPEncryptedDataGenerator(new BcPGPDataEncryptorBuilder(algorithm)
    				.setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()));
            for(PGPPublicKey p : pubKeys) {
            	encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(p));
            }
            
            ByteArrayOutputStream pom = new ByteArrayOutputStream();
            byte[] temp = arr.clone();
            OutputStream eOut;
            try {
            	eOut = encryptedDataGenerator.open(pom, temp.length);
            	eOut.write(temp);
            	eOut.close();
    			
            	arr = pom.toByteArray();//upises u glavni arr ovo sto si eknriptovao
            	//System.out.println("PROVERA ENCRY: "+new String(arr, StandardCharsets.UTF_8));
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (PGPException e) {
				e.printStackTrace();
			}
            
    	}
    	
		try {
			if (radix){
	        	out = new ArmoredOutputStream(out);
	        }
			out.write(arr);
			out.close();
		} catch (FileNotFoundException e2) {} 
		catch (IOException e) {}


         stage.close();
         
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
				//in = new ArmoredInputStream(in);
				//byte[] text = in.readAllBytes();
				//msg = new String(text, StandardCharsets.UTF_8);
				//System.out.println("Ime fajla selektovanog: "+file.getAbsolutePath());
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
		
		encryptList.getItems().addAll(getPublicKeys());
		encryptList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		encryptList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				selPublicKeys = encryptList.getSelectionModel().getSelectedItems();
//				for(String s: selPublicKeys) {
//					System.out.println("Selektovan:"+s);
//					
//				}
//				System.out.println("");
				getPublicKeyIDsHEX();
			}
			
		});
		
		encryptList.setDisable(true);
		signChoicebox.setDisable(true);
		algo3des.setDisable(true);
		algoidea.setDisable(true);
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
		String[] buf = cut.split(" ");
		selSecretKey = buf[buf.length-1];
		System.out.println("Selektovo si secret " + selSecretKey);
	}
	
	public void getPublicKeyIDsHEX() {
		
		selPublicKeyHexIDs = new ArrayList<>();
		for(String s : selPublicKeys) {
			String[] buf = s.split(" ");
			selPublicKeyHexIDs.add(buf[buf.length-1]);
		}
		for(String s:selPublicKeyHexIDs) {
			System.out.println("Selektovao si public"+s);
		}
		System.out.println("");
	}
	

    @FXML
    void getAlgo(ActionEvent event) {
    	if(algo3des.isSelected())
    		algorithm = PGPEncryptedData.TRIPLE_DES;
    	if(algoidea.isSelected())
    		algorithm = PGPEncryptedData.IDEA;
    }

}
