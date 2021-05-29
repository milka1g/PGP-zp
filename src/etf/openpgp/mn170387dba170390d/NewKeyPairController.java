package etf.openpgp.mn170387dba170390d;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.PGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.bcpg.sig.Features;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPKeyPair;
 
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import etf.openpgp.mn170387dba170390d.StartupController;
import etf.openpgp.mn170387dba170390d.Main;


public class NewKeyPairController implements Initializable {

    @FXML
    private AnchorPane newKeyPane;
    
    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private ChoiceBox<Integer> RSAkeySize;
    
    @FXML
    private Button buttonGenerate;
    
    @FXML
    private Label errorMsg;
    
    @FXML
    private ImageView twoKeysView;
    
    @FXML
    private PasswordField passwordField1;

    @FXML
    private PasswordField passwordField2;
    
    @FXML
    private Label pwError;
   
    
    private Integer[] keySize = {1024,2048,4096};
    private static Stage stage;
    static int rsaKeyLength = 1024;
    static String name_ = "";
    static String email_ = "";
    private static String passPhrase = "";
    public static final int s2kcount = 0x10;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		RSAkeySize.getItems().addAll(keySize);
		RSAkeySize.setValue(1024);
		RSAkeySize.setOnAction(this::setKeySize);
	}
	
	private void setKeySize(ActionEvent e) {
		rsaKeyLength = RSAkeySize.getValue();
		System.out.println("Vrednost keysize:" + rsaKeyLength);
	}
	
	public static void setStage(Stage st) {
		stage = st;
	}
	
    @FXML
    void submitKeyPairData(ActionEvent event) {
    	email_ = email.getText();
		name_ = name.getText();
		if(email_.equals("") && name_.equals("")){
			errorMsg.setText("You must provide e-mail and name");
		} else if (name_.equals("")) {
			errorMsg.setText("You must provide name");
		} else if (email_.equals("")) {
			errorMsg.setText("You must provide e-mail");
		} else {
			Parent root;
	        try { 
	            root = FXMLLoader.load(getClass().getResource("Password.fxml"));
	            Stage stage = new Stage();
	            PasswordController.setStage(stage);
	            stage.setTitle("Passphrase");
	            Scene scene = new Scene(root, 300, 200);
	            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	            stage.setScene(scene);
	            //stage.initStyle(StageStyle.UNDECORATED);
	            stage.setResizable(false);
	            stage.initModality(Modality.APPLICATION_MODAL);
	            stage.showAndWait();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	
		
    }
    
    
    public static void setPwAndGen(String pw) throws PGPException, IOException {
    	passPhrase = pw;
    	generate(name_+"<"+email_+">",passPhrase, rsaKeyLength);
    	stage.close();
    }
    
    public static void generate(String id, String password, int keysize
    			) throws PGPException, IOException {
    
    	
		char[] pass = password.toCharArray();
		
		PGPKeyRingGenerator pgpKeyRingGenerator = generateKeyRingGenerator(id, pass, s2kcount, keysize);
		
		PGPPublicKeyRing pgpPublicKeyRing = pgpKeyRingGenerator.generatePublicKeyRing();
		
		Main.pkrcollmy = PGPPublicKeyRingCollection.addPublicKeyRing(Main.pkrcollmy, pgpPublicKeyRing);
		
		/**
		 * i private i public key imaju isti ID 
		 *  "all fingerprints are calculated from the public key material only."
		 * */

		
		PGPSecretKeyRing pgpSecretKeyRing = pgpKeyRingGenerator.generateSecretKeyRing();
		Main.skrcoll = PGPSecretKeyRingCollection.addSecretKeyRing(Main.skrcoll, pgpSecretKeyRing);
		
		StartupController c = (StartupController) Main.loader.getController();
		c.refreshz();
		
    	}




//PREUZETO S INTERNETA I PREPRAVLJENO ZA NASE POTREBE JER OVO NI ISUS NE ZNA!!! 
/**
* @param id ID
* @param pass password
* @param s2kcount is a number between 0 and 0xff that controls the
* number of times to iterate the password hash before use. More
* iterations are useful against offline attacks, as it takes more
* time to check each password. The actual number of iterations is
* rather complex, and also depends on the hash function in use.
* Refer to Section 3.7.1.3 in rfc4880.txt. Bigger numbers give
* you more iterations.  As a rough rule of thumb, when using
* SHA256 as the hashing function, 0x10 gives you about 64
* iterations, 0x20 about 128, 0x30 about 256 and so on till 0xf0,
* or about 1 million iterations. The maximum you can go to is
* 0xff, or about 2 million iterations.  I'll use 0xc0 as a
* default -- about 130,000 iterations.
* @return PGP key ring generator
 * @throws FileNotFoundException ,16
*/
	private static PGPKeyRingGenerator generateKeyRingGenerator(String id, char[] pass, int s2kcount, int keysize) 
			throws PGPException, FileNotFoundException {
		
			RSAKeyPairGenerator rsaKeyPairGenerator = new RSAKeyPairGenerator();
			rsaKeyPairGenerator.init(new RSAKeyGenerationParameters(BigInteger.valueOf(0x10001),
			  new SecureRandom(), keysize, 12));
			// First create the master key with the generator.
			PGPKeyPair pgpKeyPairMaster =
			    new BcPGPKeyPair(PGPPublicKey.RSA_GENERAL, rsaKeyPairGenerator.generateKeyPair(), new Date());
			// Then an encryption subkey.
			PGPKeyPair pgpKeyPairSub =
			    new BcPGPKeyPair(PGPPublicKey.RSA_GENERAL, rsaKeyPairGenerator.generateKeyPair(), new Date());
			// Add a self-signature on the id
			PGPSignatureSubpacketGenerator pgpSignatureSubpacketGenerator = new PGPSignatureSubpacketGenerator();
			// Add signed metadata on the signature:
			// 1) Declare its purpose
			pgpSignatureSubpacketGenerator.setKeyFlags(false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
			// 2) Set preferences for secondary crypto algorithms to use when sending messages to this key
			pgpSignatureSubpacketGenerator.setPreferredSymmetricAlgorithms(false, new int[] {
			    SymmetricKeyAlgorithmTags.TRIPLE_DES,
			    SymmetricKeyAlgorithmTags.IDEA
			});
			pgpSignatureSubpacketGenerator.setPreferredHashAlgorithms(false, new int[] {
			    HashAlgorithmTags.SHA1
			});
			// 3) Request senders add additional checksums to the message (useful when verifying unsigned messages)
			//pgpSignatureSubpacketGenerator.setFeature(false, Features.FEATURE_MODIFICATION_DETECTION);
			
			// Create a signature on the encryption subkey
			PGPSignatureSubpacketGenerator signatureSubpacketGenerator = new PGPSignatureSubpacketGenerator();
			// Add metadata to declare its purpose
			signatureSubpacketGenerator.setKeyFlags(false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);
			// Objects used to encrypt the secret key
			PGPDigestCalculator sha1DigestCalculator = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA1);
			//PGPDigestCalculator sha256DigestCalculator = new BcPGPDigestCalculatorProvider().get(HashAlgorithmTags.SHA256);
			PBESecretKeyEncryptor pbeSecretKeyEncryptor = new BcPBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256,
			    sha1DigestCalculator, s2kcount).build(pass);
			// Finally, create the keyring itself.
			// The constructor takes parameters that allow it to generate the self signature.
			PGPKeyRingGenerator keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION,
			    pgpKeyPairMaster, id, sha1DigestCalculator, pgpSignatureSubpacketGenerator.generate(), null,
			    new BcPGPContentSignerBuilder(pgpKeyPairMaster.getPublicKey().getAlgorithm(), HashAlgorithmTags.SHA1),
			 pbeSecretKeyEncryptor);
			// Add our encryption subkey, together with its signature
			keyRingGen.addSubKey(pgpKeyPairSub, signatureSubpacketGenerator.generate(), null);
	      
			return keyRingGen;
		}

}
