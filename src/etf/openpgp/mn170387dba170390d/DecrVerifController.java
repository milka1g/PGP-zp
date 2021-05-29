package etf.openpgp.mn170387dba170390d;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
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
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class DecrVerifController {
	
    @FXML
    private Button btn;


	

	public PGPSecretKey findSecretKey(long publicKeyId) {
		ArrayList<String> list = new ArrayList<>();
		PGPSecretKey key = null;

		Iterator<PGPSecretKeyRing> it = Main.skrcoll.getKeyRings();
		while (it.hasNext()) {
			PGPSecretKeyRing skr = it.next();
			long keyID;
			keyID = skr.getSecretKey().getKeyID();
			if (keyID == publicKeyId) {
				key = skr.getSecretKey();
				return key;
			}

		}

		return key;
	}

	public PGPPublicKey findPublicKey(long id) {
		ArrayList<String> list = new ArrayList<>();
		PGPPublicKey key = null;

		Iterator<PGPPublicKeyRing> it = Main.pkrcoll.getKeyRings();
		while (it.hasNext()) {
			PGPPublicKeyRing pkr = it.next();
			long keyID;
			keyID = pkr.getPublicKey().getKeyID();
			if (keyID == id) {
				key = pkr.getPublicKey();
				return key;
			}

		}
		return key;
	}


	@SuppressWarnings("resource")
	@FXML
	void decryptOrVerifyFile(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Choose file to decrypt and/or verify");
		String[] extensions = { "*.pgp", "*.gpg" };
		fc.getExtensionFilters().add(new ExtensionFilter("OpenPGP files", extensions));
		String desktopPath = System.getProperty("user.home") + "/Desktop";
		File init = new File(desktopPath);
		fc.setInitialDirectory(init);
		File file = fc.showOpenDialog(Main.primaryStage);
		if (file != null) {
			System.out.println("Fajl koji se dekriptuje: " + file.getName());
			try {
				InputStream in = new FileInputStream(file);
				//in = new ArmoredInputStream(in);
				// ------------------
				InputStream inPGP;
				inPGP = PGPUtil.getDecoderStream(in); //?ili samo ovo?
				
				byte[] arr = inPGP.readAllBytes();
				System.out.println("PPROVERA BYTE "+new String(arr, StandardCharsets.UTF_8));
				// base64 -> byte[]
				//byte[] byteArray = Base64.decode(in.readAllBytes());
				//ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
				//in = PGPUtil.getDecoderStream(bais);
				// General class for reading a PGP object stream.
				KeyFingerPrintCalculator calc = new BcKeyFingerprintCalculator();
				PGPObjectFactory pgpF = new PGPObjectFactory(inPGP, calc);

				PGPEncryptedDataList enc = null;
				Object o = pgpF.nextObject();

				if (o instanceof PGPMarker) {
					o = pgpF.nextObject();
				}

				boolean notEncr = false;

				if (o instanceof PGPEncryptedDataList) {
					enc = (PGPEncryptedDataList) o;
				} else {
					notEncr = true;
					System.out.println("not encypted");
				}

				//
				// find the secret key
				//
				PGPPrivateKey sKey = null;
				PGPPublicKeyEncryptedData pbe = null;
				InputStream clear;
				PGPObjectFactory plainFact=null;
				if (!notEncr) {
					Iterator it = enc.getEncryptedDataObjects();
					
					while (sKey == null && it.hasNext()) {
						pbe = (PGPPublicKeyEncryptedData) it.next();
						PGPSecretKey psKey = findSecretKey(pbe.getKeyID());
						String pass = "1234"; // *** OVO SE UNOSI ***
						PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(
								new BcPGPDigestCalculatorProvider()).build(pass.toCharArray());
						if (psKey != null) {
							sKey = psKey.extractPrivateKey(decryptor);
						}
					}
					if (sKey == null) {
						System.out.println("Unable to find secret key to decrypt the message");
					}
					clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));

					plainFact = new PGPObjectFactory(clear, calc);
				}

				

				Object message;

				PGPOnePassSignatureList onePassSignatureList = null;
				PGPSignatureList signatureList = null;
				PGPCompressedData compressedData;

				message = plainFact.nextObject();
				ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();

				while (message != null) {
					if (message instanceof PGPCompressedData) {
						compressedData = (PGPCompressedData) message;
						plainFact = new PGPObjectFactory(compressedData.getDataStream(), calc);
						message = plainFact.nextObject();
					}

					if (message instanceof PGPLiteralData) {
						// have to read it and keep it somewhere.
						PGPLiteralData ld = (PGPLiteralData) message;
						InputStream unc = ld.getInputStream();
						int ch;
						System.out.println("Literal data: ");
						while ((ch = unc.read()) >= 0) {
							System.out.println((char) (ch));
						}
						System.out.println("");

						// Streams.pipeAll(((PGPLiteralData) message).getInputStream(), actualOutput);
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

				PGPPublicKey publicKey = null;
				byte[] output = actualOutput.toByteArray();
				if (onePassSignatureList == null || signatureList == null) {
					throw new PGPException("Signatures not found.");
				} else {

					for (int i = 0; i < onePassSignatureList.size(); i++) {
						PGPOnePassSignature ops = onePassSignatureList.get(0);
						System.out.println("verifier : " + ops.getKeyID());
						// InputStream publicKeyIn =null ;//= findPublicKey(ops.getKeyID());
						// PGPPublicKeyRingCollection pgpRing = new PGPPublicKeyRingCollection(
						// (Collection<PGPPublicKeyRing>) PGPUtil.getDecoderStream(publicKeyIn));
						publicKey = findPublicKey(ops.getKeyID());
						if (publicKey != null) {
							ops.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey);
							ops.update(output);
							PGPSignature signature = signatureList.get(i);
							if (ops.verify(signature)) {
								Iterator<?> userIds = publicKey.getUserIDs();
								while (userIds.hasNext()) {
									String userId = (String) userIds.next();
									System.out.println(String.format("Signed by {%s}", userId));
								}
								System.out.println("Signature verified");
							} else {
								System.out.println("Signature verification failed");
							}
						}
					}

				}

				if (pbe.isIntegrityProtected() && !pbe.verify()) {
					throw new PGPException("Data is integrity protected but integrity is lost.");
				} else if (publicKey == null) {
					System.out.println("Signature not found");
				} else {
					System.out.println(output);
				}

				// --------------------
				System.out.println("DEKRIPTOVANO");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {

			} catch (Exception e) {

			}
		}
	}
	

}
