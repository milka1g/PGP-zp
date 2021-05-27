package etf.openpgp.mn170387dba170390d;
	
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRingCollection;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;



public class Main extends Application {
	
	public static Stage primaryStage;
	public static FXMLLoader loader;
	
	//cuvanje nasih kljuceva
	//public static ArrayList<PGPPublicKeyRing> pkr = null;
	//public static ArrayList<PGPSecretKeyRing> skr = null;
	
	public static PGPPublicKeyRingCollection pkrcoll;
	public static PGPPublicKeyRingCollection pkrcollmy;
	public static PGPSecretKeyRingCollection skrcoll;
	
	static {
		//treba napraviti da iscita iz fajla nekog gde ce se svi cuvati
		try {
			InputStream inputPkrMy = new FileInputStream("usermy.pkr");
			InputStream inputPkr = new FileInputStream("user.pkr");
			InputStream inputSkr = new FileInputStream("user.skr");
			
			inputPkrMy = new ArmoredInputStream(inputPkrMy);
			inputPkr = new ArmoredInputStream(inputPkr);
			inputSkr = new ArmoredInputStream(inputSkr);
			
			pkrcoll = new BcPGPPublicKeyRingCollection(inputPkr);
			skrcoll = new BcPGPSecretKeyRingCollection(inputSkr);
			pkrcollmy = new BcPGPPublicKeyRingCollection(inputPkrMy);
			
			
	        System.out.println("VELICINA pkrcoll " + pkrcoll.size());
	        System.out.println("VELICINA skrcoll " + skrcoll.size());
	        System.out.println("VELICINA pkrcollmy " + pkrcollmy.size());
			
		    
		} catch (IOException e) {
			e.printStackTrace();
		
		//pkr = new ArrayList<PGPPublicKeyRing>();
		//skr = new ArrayList<PGPSecretKeyRing>();
		
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			loader = new FXMLLoader(getClass().getResource("Startup.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root,800,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Image icon = new Image("aca.jpg");
			primaryStage.getIcons().add(icon);
			primaryStage.setTitle("Aleksandra");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
					Iterator<PGPPublicKeyRing> ii = pkrcoll.getKeyRings();
					while(ii.hasNext()) {
					PGPPublicKeyRing i = ii.next();
					System.out.println("Ovo je id od public keya:" + i.getPublicKey().getKeyID());
				}
				
					System.out.println("VELICINA pkrcoll " + pkrcoll.size());
			        System.out.println("VELICINA skrcoll " + skrcoll.size());
			        System.out.println("VELICINA pkrcollmy " + pkrcollmy.size());
				        
				    try {
						OutputStream    publicKeyRingStream = new FileOutputStream("user.pkr",false);
				        OutputStream    secretKeyRingStream = new FileOutputStream("user.skr",false);
				        OutputStream    publicKeyRingStreamMy = new FileOutputStream("usermy.pkr",false);
				        publicKeyRingStream = new ArmoredOutputStream(publicKeyRingStream);
				        secretKeyRingStream = new ArmoredOutputStream(secretKeyRingStream);
				        publicKeyRingStreamMy = new ArmoredOutputStream(publicKeyRingStreamMy);
				      
				        pkrcoll.encode(publicKeyRingStream);
				        skrcoll.encode(secretKeyRingStream);
				        pkrcollmy.encode(publicKeyRingStreamMy);
				        
						publicKeyRingStream.close();
						secretKeyRingStream.close();
						publicKeyRingStreamMy.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        

				}
				
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
