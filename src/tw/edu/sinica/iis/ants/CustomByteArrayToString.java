package tw.edu.sinica.iis.ants;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.simple.ByteArrayToObject;

public class CustomByteArrayToString extends ByteArrayToObject {

	@Override
	public Object doTransform(Object src, String encoding)
			throws TransformerException {

		//String a = new String((byte [])src);
		//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!" + a);
		return super.doTransform(src, encoding);
	}

}
