
package uber.paste.ws.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uber.paste.ws.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _AddPaste_QNAME = new QName("http://ws.paste.uber/", "addPaste");
    private final static QName _AddPasteResponse_QNAME = new QName("http://ws.paste.uber/", "addPasteResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uber.paste.ws.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddPasteResponse }
     * 
     */
    public AddPasteResponse createAddPasteResponse() {
        return new AddPasteResponse();
    }

    /**
     * Create an instance of {@link AddPaste }
     * 
     */
    public AddPaste createAddPaste() {
        return new AddPaste();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddPaste }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.paste.uber/", name = "addPaste")
    public JAXBElement<AddPaste> createAddPaste(AddPaste value) {
        return new JAXBElement<AddPaste>(_AddPaste_QNAME, AddPaste.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddPasteResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.paste.uber/", name = "addPasteResponse")
    public JAXBElement<AddPasteResponse> createAddPasteResponse(AddPasteResponse value) {
        return new JAXBElement<AddPasteResponse>(_AddPasteResponse_QNAME, AddPasteResponse.class, null, value);
    }

}
