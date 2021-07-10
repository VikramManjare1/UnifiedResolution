package unifiedResolution;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 */

public class XMLHandler {
	private DocumentBuilderFactory dbf;
	private DocumentBuilder db;
	private Document doc;
	private Node root;
	
	private void removeWhitespaceNodes(Node e){
        NodeList children = e.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child instanceof Text && ((Text) child).getData().trim().length() == 0) {
                e.removeChild(child);
            }
            else if (child instanceof Element){
                removeWhitespaceNodes(child);
            }
        }
    }
	
	public XMLHandler(String filePath) {
		File file = new File(filePath);
		dbf = DocumentBuilderFactory.newInstance();
		
		try {
			db = dbf.newDocumentBuilder();  
			doc = db.parse(file);  
			doc.getDocumentElement().normalize();
			
			root = doc.getDocumentElement();
			removeWhitespaceNodes(root);
		}
		catch (Exception e){  
			e.printStackTrace();  
		}
	}
	
	/*public String getRootNodeName() { 
		return root.getNodeName();  
	}
	
	public int getNumberOfExpressions() {
		return root.getChildNodes().getLength();
	}*/
	
	public NodeList getExpressions() {
		return root.getChildNodes();
	}
	
	//XML node is complex if it has at least 1 child, which again has at least one child
	//Simple XML node has only 1 or 2 leaf nodes 
	public boolean isSimple(Node e) {
		if(e.getChildNodes().getLength()>=1 && e.getChildNodes().item(0).getChildNodes().getLength()>=1) return false;
		return true;
	}
}
