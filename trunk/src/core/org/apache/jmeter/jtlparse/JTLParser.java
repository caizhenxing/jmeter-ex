package org.apache.jmeter.jtlparse;
import java.io.File;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

/**
 * parse the jtl file
 * @author chenchao.yecc
 * @since jex001A
 */
public class JTLParser {

	private File file = null;


	public void setJmeterLogFile(String path) {
		this.file = new File(path);
	}

	public void parse() throws Exception {
		// this.prepareToStoreOrSend();
//		parse(0);
		parseJTLFile();
	}

	public void parseJTLFile() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(file);
			Element root = doc.getRootElement();
			root.accept(new MyVisitor());
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
    private class MyVisitor extends VisitorSupport {

		// public void visit(Element element){
		// }

		public void visit(Attribute attr) {

			switch (attr.getName().hashCode()) {
			// t ATT_TIME
			case 116:
				
				break;
			// lt LATENCY
			case 3464:
				
				break;
			// ts TIME_STAMP
			case 3711:
				break;
			// s SUCCESS
			case 115:
				break;	
			// lb LABEL
			case 3446:
				break;
			// rc RESPONSE_CODE
			case 3633:
				break;
			// rm RESPONSE_MESSAGE
			case 3643:
				break;
			// tn THREADNAME
			case 3706:
				break;
			// dt DATA_TYPE
			case 3216:
				break;
			// by BYTES
			case 3159:
				break;
			// de ENCODING
//			case 3159:
//				break;
			// ec ERROR_COUNT
//			case 3159:
//				break;
			// hn HOSTNAME
//				case 3159:
//					break;
			// na ALL_THRDS
//				case 3159:
//					break;
			// ng GRP_THRDS
//				case 3159:
//					break;
			// rs RESPONSE_CODE_OLD
//				case 3159:
//					break;
			// sc SAMPLE_COUNT
//				case 3159:
//					break;
			default:
				break;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		final JTLParser parser = new JTLParser();
		System.out.println("by".hashCode());
		parser.setJmeterLogFile("D:\\Tools\\jakarta-jmeter-2.3.4\\bin\\http.jtl");
		parser.parseJTLFile();
	}
}
