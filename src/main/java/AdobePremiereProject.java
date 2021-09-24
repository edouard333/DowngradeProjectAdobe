
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Modifie la version d'un projet Adobe Premiere.
 *
 * @author Edouard Jeanjean <edouard128@hotmail.com>
 */
public class AdobePremiereProject {

  /**
   * Version CC2015 d'Adobe Premiere Pro.
   */
  public static final String VERSION_CC2015 = "31";

  /**
   * Version CC2017 d'Adobe Premiere Pro (version 11.0).
   */
  public static final String VERSION_CC2017 = "32";

  /**
   * Version CC2017.1.2 d'Adobe Premiere Pro (version 11.???).
   */
  public static final String VERSION_CC2017_1_2 = "33";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 12.1.2).
   */
  public static final String VERSION_CC2018 = "35";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.0).
   */
  public static final String VERSION_CC2019 = "36";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.1).
   */
  public static final String VERSION_CC2019_01 = "36";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.1.1).
   */
  public static final String VERSION_CC2019_02 = "36";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.1.2).
   */
  public static final String VERSION_CC2019_03 = "36";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.1.3).
   */
  public static final String VERSION_CC2019_04 = "36";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.1.4).
   */
  public static final String VERSION_CC2019_05 = "37";

  /**
   * Version CC2018 d'Adobe Premiere Pro (version 13.1.5).
   */
  public static final String VERSION_CC2019_06 = "37";

  /**
   * Version CC2020 d'Adobe Premiere Pro (version 14.3.1).
   */
  public static final String VERSION_CC2020 = "38";

  /**
   * ...
   *
   * @return Liste des versions.
   */
  public String[] getVersions() {
    return new String[]{VERSION_CC2015, VERSION_CC2017, VERSION_CC2018, VERSION_CC2019, VERSION_CC2020};
  }

  /**
   * Modifie la version d'un projet Adobe Premiere.
   *
   * @param fichier Nom du fichier de projet.
   * @param version Version qu'on veut pour ce projet.
   *
   * @throws java.io.FileNotFoundException
   * @throws javax.xml.parsers.ParserConfigurationException
   * @throws org.xml.sax.SAXException
   * @throws javax.xml.transform.TransformerConfigurationException
   */
  public AdobePremiereProject(File fichier, String version) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException {

    String name_file = fichier.getAbsolutePath().replace(".prproj", "");

    System.out.println("Nom de fichier : " + fichier.getName());

    String name_file_tmp = name_file + ".tmp";

    decompressGzipFile(name_file + ".prproj", name_file_tmp);

    Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(name_file_tmp));

    NodeList list = xml.getDocumentElement().getChildNodes();

    // Récupère et modifie la valeur actuelle.
    for (int i = 0; i < list.getLength(); i++) {
      //System.out.println("Node : " + list.item(i).getNodeName());

      if (list.item(i).getNodeType() == Node.ELEMENT_NODE && list.item(i).getNodeName().equals("Project")) {

        Element balise_project = (Element) list.item(i);

        String attribute_version = balise_project.getAttribute("Version");

        // Si pour l'attribut "Version" il y a une valeur, c'est la bonne balise !
        if (!attribute_version.equals("")) {
          balise_project.setAttribute("Version", version);

          // On ne doit plus rien faire, donc on peut quitter la boucle.
          break;
        }
      }
    }

    // Sauve les changements.
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(xml);
    StreamResult result = new StreamResult(new File(name_file + "2.tmp"));
    transformer.transform(source, result);

    //For console Output.
    StreamResult consoleResult = new StreamResult(System.out);
    transformer.transform(source, consoleResult);

    compressGzipFile(name_file + "2.tmp", name_file + "_CC2017.prproj");

    // On supprime les fichiers temporaires.
    new File(name_file + ".tmp").delete();
    new File(name_file + "2.tmp").delete();
  }

  /**
   * Décompresser un fichier.
   *
   * @param gzipFile
   * @param newFile
   */
  private static void decompressGzipFile(String gzipFile, String newFile) {
    try {
      FileInputStream fis = new FileInputStream(gzipFile);
      GZIPInputStream gis = new GZIPInputStream(fis);
      FileOutputStream fos = new FileOutputStream(newFile);
      byte[] buffer = new byte[1024];
      int len;
      while ((len = gis.read(buffer)) != -1) {
        fos.write(buffer, 0, len);
      }
      //close resources
      fos.close();
      gis.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Compresse le fichier.
   *
   * @param file
   * @param gzipFile
   */
  private static void compressGzipFile(String file, String gzipFile) {
    try {
      FileInputStream fis = new FileInputStream(file);
      FileOutputStream fos = new FileOutputStream(gzipFile);
      GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
      byte[] buffer = new byte[1024];
      int len;
      while ((len = fis.read(buffer)) != -1) {
        gzipOS.write(buffer, 0, len);
      }
      //close resources
      gzipOS.close();
      fos.close();
      fis.close();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
