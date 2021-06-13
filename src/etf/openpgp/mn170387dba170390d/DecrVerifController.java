package etf.openpgp.mn170387dba170390d;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.ldap.HasControls;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.its.asn1.HashAlgorithm;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPMarker;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class DecrVerifController {
	
    @FXML
    private Button btn;
    
    @FXML
    private TextField fileName;

    @FXML
    private TextField encWith;

    @FXML
    private TextField signBy;
    
    @FXML
    private TextField integrity;


    public static String pw;
    
    public static Stage stage;
    
    

	public PGPSecretKey findSecretKey(long publicKeyId) {
		try {
			return Main.skrcoll.getSecretKey(publicKeyId);
		} catch (PGPException e) {
			System.out.println("NEMAS SECRET KEY U FindSecretKey");
			e.printStackTrace();
		}

//		Iterator<PGPSecretKeyRing> it = Main.skrcoll.getKeyRings();
//		while (it.hasNext()) {
//			PGPSecretKeyRing skr = it.next();
//			long keyID;
//			keyID = skr.getSecretKey().getKeyID();
//			if (keyID == publicKeyId) {
//				key = skr.getSecretKey();
//				return key;
//			}
//
//		}
		return null;

	}

	public PGPPublicKey findPublicKey(long id) {
//		ArrayList<String> list = new ArrayList<>();
//		PGPPublicKey key = null;
//
//		Iterator<PGPPublicKeyRing> it = Main.pkrcoll.getKeyRings();
//		while (it.hasNext()) {
//			PGPPublicKeyRing pkr = it.next();
//			long keyID;
//			keyID = pkr.getPublicKey().getKeyID();
//			if (keyID == id) {
//				key = pkr.getPublicKey();
//				return key;
//			}
//
//		}
		PGPPublicKey k = null;
		try {
			k = Main.pkrcoll.getPublicKey(id);
		} catch (PGPException e) {
			System.out.println("NEMAS PUBLIC KEY!!!");
			e.printStackTrace();
		}
		
		try {
			k = Main.pkrcollmy.getPublicKey(id);
		} catch (PGPException e) {
			System.out.println("NEMAS PUBLIC KEY!!!");
			e.printStackTrace();
		}
		
		return k;
	}


	@SuppressWarnings("resource")
	@FXML
	void decryptOrVerifyFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose file to decrypt and/or verify");
		String[] extensions = { "*.txt" };
		fc.getExtensionFilters().add(new ExtensionFilter("All files", extensions));
		String desktopPath = System.getProperty("user.home") + "/Desktop";
		File init = new File(desktopPath);
		fc.setInitialDirectory(init);
		File file = fc.showOpenDialog(stage);
		if (file != null) {
			fileName.setText(file.getName());
			try {
				InputStream in = new FileInputStream(file);
				//in = new ArmoredInputStream(in);
				// ------------------
				//InputStream inPGP;
				InputStream inPGP = PGPUtil.getDecoderStream(in); //?ili samo ovo?
				//procitace ako je radix64, ako nije nikom nista!
				
				//byte[] arr = inPGP.readAllBytes();

				//System.out.println("PROVERA BYTE in: "+new String(arr, StandardCharsets.UTF_8));
				// base64 -> byte[]
				//byte[] byteArray = Base64.decode(in.readAllBytes());
				//ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
				//in = PGPUtil.getDecoderStream(bais);
				// General class for reading a PGP object stream.
				
				
				KeyFingerPrintCalculator calc = new BcKeyFingerprintCalculator();
				PGPObjectFactory pgpF = new PGPObjectFactory(inPGP,calc);

				PGPEncryptedDataList enc = null;
				
				Object o = pgpF.nextObject();

				//za slucaj da jeste ali nije
				if (o instanceof PGPMarker) {
					o = pgpF.nextObject();
					//System.out.println("JESTE LI?");
				}

				boolean notEncr = false;

				if (o instanceof PGPEncryptedDataList) {
					enc = (PGPEncryptedDataList) o;
					System.out.println("PRVI IF");
				} else {
					notEncr = true;
					System.out.println("not encypted");
					encWith.setText("Not encrypted");
				}

				//
				// find the secret key
				//
				PGPPrivateKey privateKey = null;
				PGPPublicKeyEncryptedData pbe = null;
				InputStream clear;
				PGPObjectFactory plainFact=null;
				if (!notEncr) { //enkr je pa trazi priv da dekr
					Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects(); 
					
					while (privateKey == null && it.hasNext()) { //moze biti enkript za vise ljudi od kojih si ti 1 ili ne
						//System.out.println("BR ITER");
						pbe = (PGPPublicKeyEncryptedData) it.next();
						PGPSecretKey secretKey = findSecretKey(pbe.getKeyID());
						if (secretKey != null) {
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
				        
						PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(
								new BcPGPDigestCalculatorProvider()).build(pw.toCharArray());
						
							privateKey = secretKey.extractPrivateKey(decryptor);
							//postavis info o tome koji tvoj kljuc je koriscen za dekripciju
							Iterator<String> itattr = secretKey.getPublicKey().getUserIDs();
							String id="";
				    		while(itattr.hasNext()) {
				    			String attr = itattr.next();
				    			id=id+attr+" ";
				    		}
							encWith.setText(id);
						}
					}
					if (privateKey == null) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Bad passphrase / No Secret Key");
						alert.setHeaderText("You have entered bad passphrase or you don't have Secret Key");
						alert.setContentText("Your secret key is encrypted with different passphrase, please "
								+ "insert the correct one. Or you don't have specified Secret Key");
						alert.showAndWait();
						return;
					}
					clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));

					plainFact = new PGPObjectFactory(clear, calc); 
				}

				
				//dekriptovo si do ovde, sad ide unzip ako ga ima pa verifikacija. radix je uradjen pri citanju gore
				
				Object message;

				PGPOnePassSignatureList onePassSignatureList = null;
				PGPSignatureList signatureList = null;
				PGPCompressedData compressedData;

				message = plainFact.nextObject();
				ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();

				while (message != null) {
					//ako je dupli zip valjda onda ovako
					if (message instanceof PGPCompressedData) {
						compressedData = (PGPCompressedData) message;
						plainFact = new PGPObjectFactory(compressedData.getDataStream(), calc);
						message = plainFact.nextObject();
					}

					if (message instanceof PGPLiteralData) {
						// have to read it and keep it somewhere.
						PGPLiteralData ld = (PGPLiteralData) message;
						InputStream unc = ld.getInputStream();
						//System.out.println("Literal data: ");
						try {
				    		byte[] buf = new byte[4096];
				    		int i;
							while((i = unc.read(buf,0,4096))>-1) {
								actualOutput.write(buf,0,i); 
							}
							
						} catch (IOException e2) {
							e2.printStackTrace();
						}
					} else if (message instanceof PGPOnePassSignatureList) {
						onePassSignatureList = (PGPOnePassSignatureList) message;
						System.out.println("one pass signature list");
					} else if (message instanceof PGPSignatureList) {
						signatureList = (PGPSignatureList) message;
						System.out.println("signature list");
					} else {
						throw new PGPException("message unknown message type.");
					}
					message = plainFact.nextObject();
				}
				actualOutput.close();

				//dovde smo procitali literal data, !!!upisali u fajl!!!, sad ide verifikacija potpisa
				//trazimo publicKey od sendera da verifikujemo nekako
				PGPPublicKey publicKey = null;
				byte[] output = actualOutput.toByteArray();
				System.out.println("!!!! "+new String(output,StandardCharsets.UTF_8));
				if (onePassSignatureList == null || signatureList == null) {
					System.out.println("signatures not found.");
					signBy.setText("Not signed");
					throw new PGPException("signatures not found.");
				} else {

					for (int i = 0; i < onePassSignatureList.size(); i++) {
						PGPOnePassSignature ops = onePassSignatureList.get(0);
						System.out.println("verifier : " + Long.toHexString(ops.getKeyID()));
						// InputStream publicKeyIn =null ;//= findPublicKey(ops.getKeyID());
						// PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(
						// (Collection<PGPPublicKeyRing>) PGPUtil.getDecoderStream(publicKeyIn));
						publicKey = findPublicKey(ops.getKeyID());
						if (publicKey != null) {
							ops.init(new BcPGPContentVerifierBuilderProvider(),publicKey);
							ops.update(output); 
							PGPSignature signature = signatureList.get(0);
							if (ops.verify(signature)) {
								Iterator<String> userIds = publicKey.getUserIDs();
								while (userIds.hasNext()) {
									String userId = (String) userIds.next();
									signBy.setText(userId + " - Signature verified");
								}
								//System.out.println("Signature verified");
							} else {
								//System.out.println("Signature verification failed");
								signBy.setText("Signature verification failed");
							}
						}
					}

				}
				if(pbe.isIntegrityProtected()) {
					System.out.println("Cuvanje integriteta ukljuceno!");
					integrity.setText("ON");
				}
				if (pbe.isIntegrityProtected() && !pbe.verify()) {
					integrity.setText("ON, but integrity is LOST");
					throw new PGPException("Data is integrity protected but integrity is lost.");
				} else if (publicKey == null) {
					signBy.setText("Not signed");
					System.out.println("Signature not found");
				} else {
					try {
				    	fc = new FileChooser();
				    	fc.setTitle("Choose location");
				    	fc.setInitialDirectory(init);
				    	String[] cut = file.getName().split("\\."); //jajceDecfZIPENcrty.txt
				    	fc.setInitialFileName(cut[0]+"DcrVrfy."+cut[1]);
				    	File fileOut = fc.showSaveDialog(stage);
				    	if(fileOut==null)
				    		return;
				    	OutputStream out = new FileOutputStream(fileOut);
				    	out.write(output);

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (Exception e) {
							System.out.println("Nisi selektovao");
						}
				}
				

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

}
