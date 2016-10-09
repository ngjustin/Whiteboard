
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileHandler<A> {
	public void save(String fileName, A[] data) {
		try {
			File file = new File(fileName);
			XMLEncoder xmlOut = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
			
			xmlOut.writeObject(data);
			xmlOut.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getXMLString(A model) {
		OutputStream memStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(memStream);
        encoder.writeObject(model);
        encoder.close();
        return memStream.toString();
	}
	
	public A[] open(String fileName) {
		A[] data = null;
		try {
			File file = new File(fileName);
			XMLDecoder xmlIn = new XMLDecoder(new BufferedInputStream(
		            new FileInputStream(file))); 
			data = (A[]) xmlIn.readObject();
			xmlIn.close();	
		}
		catch (Exception e) {
			e.printStackTrace();
			return data;
		}
		return data;
	}
	
}	
