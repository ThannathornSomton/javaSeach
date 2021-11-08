import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.regex.*; 
import java.lang.Object;

import java.io.InputStream;
import java.io.FileInputStream;


import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.util.regex.Pattern;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigestSpi;
import java.security.MessageDigest;

public class Searchfile {

    static String rootpath = "C:\\Users\\Desktop\\Downloads\\TRY\\Search\\HW" ;
 
    public static final String xmlFilePath = "C:\\Users\\Desktop\\Desktop\\GAP\\save.xml";
    public static void main(final String[] args) throws SAXException, IOException, ParserConfigurationException {

        try {
 
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
    
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
    
            Document document = documentBuilder.newDocument();
            // root element
            Element root = document.createElement("company");
            document.appendChild(root);
    
            //parse(document,root,0) ;
            savefile(rootpath,document,root ) ;
    
                // create the xml file
                //transform the DOM Object to an XML File
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                StreamResult streamResult = new StreamResult(new File(xmlFilePath));
     
                // If you use
                // StreamResult result = new StreamResult(System.out);
                // the output will be pushed to the standard output ...
                // You can use that for debugging 
     
                transformer.transform(domSource, streamResult);
     
                System.out.println("Done creating XML File");
     
            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (TransformerException tfe) {
                tfe.printStackTrace();
            }

        String search = "Main.java" ;
        
        new Searchfile("save.xml",search);

    }

    public Searchfile(final String file,String search) throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        final Document doc = docBuilder.parse(this.getClass().getResourceAsStream(file));
        final List<String> l = new ArrayList<String>();
        parse(doc, l, doc.getDocumentElement(),search,Pattern.compile(search, Pattern.CASE_INSENSITIVE));
        System.out.println("finish");
    }

    private void parse(final Document doc, final List<String> list, final Element e, String search, Pattern pattern) {
        final NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node n = children.item(i);
            if(n.getNodeName().equals("Folder")) {
                parse(doc, list, (Element) n,search,pattern);
            }
            else if (n.getNodeName().equals("File")){
                String filename = n.getTextContent();
                if(pattern.matcher(filename).find()){
                System.out.println(filename);
            }}
        }
    }

    private static void savefile(String dir ,Document document ,Element element){
        File file = new File(dir);
          if (file.isDirectory() && !file.isHidden()) {
            String names[] = file.list();
            // setting attribute to element
          Element newChild = document.createElement("Folder");
          String[] splits = dir.split("\\\\") ;
           Attr attr = document.createAttribute("name");
           attr.setValue(splits[splits.length-1]);
           newChild.setAttributeNode(attr);
           element.appendChild(newChild);
            for(int i = 0; i < names.length;i++) {
              savefile(dir+"\\"+names[i],document,newChild);
            }
          }
          else{
            if(!file.isHidden()){

            SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String checksum = "";
            String size = Double.toString(file.length()/1000.0)+" KB";
            try{
               MessageDigest md5Digest = MessageDigest.getInstance("MD5");
               checksum = getFileChecksum(md5Digest, file);
            }catch(IOException e ){}
            catch(NoSuchAlgorithmException e){}

              String[] splits = dir.split("\\\\") ;
              Element newChild = document.createElement("File");
              Attr strmd5 = document.createAttribute("md5");
              Attr strsize = document.createAttribute("size");
              Attr strdate = document.createAttribute("date");
              strmd5.setValue(checksum);
              strsize.setValue(size);
              strdate.setValue(date.format(file.lastModified()));
              newChild.setAttributeNode(strmd5);
              newChild.setAttributeNode(strsize);
              newChild.setAttributeNode(strdate);
              newChild.appendChild(document.createTextNode(splits[splits.length-1]));
              element.appendChild(newChild);
            }
          }
      }

   private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
    //Get file input stream for reading the file content
    FileInputStream fis = new FileInputStream(file);
     
    //Create byte array to read data in chunks
    byte[] byteArray = new byte[1024];
    int bytesCount = 0; 
      
    //Read file data and update in message digest
    while ((bytesCount = fis.read(byteArray)) != -1) {
        digest.update(byteArray, 0, bytesCount);
    };
     
    //close the stream; We don't need it now.
    fis.close();
     
    //Get the hash's bytes
    byte[] bytes = digest.digest();
     
    //This bytes[] has bytes in decimal format;
    //Convert it to hexadecimal format
    StringBuilder sb = new StringBuilder();
    for(int i=0; i< bytes.length ;i++)
    {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
    }
     
    //return complete hash
   return sb.toString();
   }

}
