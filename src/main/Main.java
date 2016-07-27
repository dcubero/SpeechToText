package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;


public class Main {
	private static String claveApi = "AIzaSyDLk_2k9KfXf7WfqsoyLz7EFRJ4wcDjmpg";
	private static String request = "https://www.google.com/speech-api/v2/"
			+ "recognize?xjerr=1&client=chromium&lang=es-ES&key=";
	
	
	public static void main(String[] args) throws IOException {
		
		//Look and Feel del sistema
		 try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		FileFilter filtroFlac = new ExtensionFileFilter("Archivos .FLAC", new String [] {"flac"});
		fileChooser.setFileFilter(filtroFlac);
		
		int resultado = fileChooser.showOpenDialog(null);
		
		if (resultado == JFileChooser.CANCEL_OPTION){
			System.out.println("No se ha seleccionado ningun archivo");
		}else{
			System.out.println(fileChooser.getSelectedFile().toString());
			
			  
			Path path = Paths.get(fileChooser.getSelectedFile().getPath());
			byte[] data = Files.readAllBytes(path);
			
			URL url = new URL(request+claveApi);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "audio/x-flac; rate=35000");
			connection.setRequestProperty("User-Agent", "speech2text"); 
			connection.setConnectTimeout(60000);
			connection.setUseCaches (false);
			
		       	
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
			wr.write(data);
			wr.flush();
			wr.close();

			System.out.println("Enviado");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String decodedString;
			
			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
			}
			
			connection.disconnect();
			
		}		
	}
}


class ExtensionFileFilter extends FileFilter{
	
	String descripcion;
	String extensiones[];
	
	public ExtensionFileFilter(String descripcion, String extensiones[]) {
		this.descripcion = descripcion;
		this.extensiones = extensiones;
	}

	@Override
	public boolean accept(File archivo) {
		if (archivo.isDirectory()){
			return true;
		} else {
			String ruta = archivo.getAbsolutePath().toLowerCase();
			for (int i = 0, n = extensiones.length; i < n; i++){
				String extension = extensiones[i];
				if ((ruta.endsWith(extension) && (ruta.charAt(ruta.length() - extension.length() - 1)) == '.')){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return descripcion;
	}
}