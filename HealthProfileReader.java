import java.util.HashMap;
import java.util.Map;
import java.io.*;

import pojos.HealthProfile;
import pojos.Person;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HealthProfileReader {
	
	Document doc;
    XPath xpath;

	public static Map<Long, Person> database = new HashMap<Long, Person>(); //

	public void loadXML() throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        doc = builder.parse("people.xml");

        //creating xpath object
        getXPathObj();
    }

    public XPath getXPathObj() {

        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        return xpath;
    }

    public Node getPersonByFullName(String firstName, String lastName) throws XPathExpressionException {
    	String expression = "/people/person[firstname='" + firstName + "' and lastname='" + lastName + "']";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
  //      XPathExpression expr = xpath.compile("/people/");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node;
    }

    public Node getPersonWeight(String firstName, String lastName) throws XPathExpressionException {
    	String expression = "/people/person[firstname='" + firstName + "' and lastname='" + lastName + "']/healthprofile/weight";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
  //      XPathExpression expr = xpath.compile("/people/");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node;
    }


    public Node getPersonHeight(String firstName, String lastName) throws XPathExpressionException {
    	String expression = "/people/person[firstname='" + firstName + "' and lastname='" + lastName + "']/healthprofile/height";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
  //      XPathExpression expr = xpath.compile("/people/");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node;
    }

    public NodeList getPersonWithWeight(String weight, String operator) throws XPathExpressionException {    	
    	String expression = "/people/person[healthprofile/weight" + operator + weight + "]";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
  //      XPathExpression expr = xpath.compile("/people/");        
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        return nodes;
    }


	/* Solution to Exercise #01-2b 
	 * Check the browser with: 
	 * http://localhost:8080/axis2/services/HealthProfileReader/createPerson?personId=15&firstname=Alicia&lastname=Recalde&birthdate=1985-03-20
	 * 
	 */
	public static Person createPerson(Long personId, String firstname,
			String lastname, String birthdate) {
		Person p = new Person(personId, firstname, lastname, birthdate);
		database.put(p.getPersonId(), p);
		System.out.println("A new person record (" + p.getPersonId()
				+ ") has been created for " + p.getLastname() + ", "
				+ p.getFirstname() + " born on " + p.getBirthdate());
		return p;
	}

	public Person getPersonObjectByFullName(String firstName, String lastName) throws XPathExpressionException {
		String expression = "/people/person[firstname='" + firstName + "' and lastname='" + lastName + "']";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
  //      XPathExpression expr = xpath.compile("/people/");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        NodeList personDetails = node.getChildNodes();
        return fromXMLtoPerson(personDetails);
	}

	private Person fromXMLtoPerson(NodeList personDetails){
		Person myPerson = new Person();

		for (int i=0; i < personDetails.getLength(); i++) {			
			Node item = personDetails.item(i);
			if (item.getNodeType() != Node.TEXT_NODE){
				// item is tag
				System.out.println("Item tag: " + item.getNodeName());
				
				if (item.getNodeName() == "firstname"){
					myPerson.setFirstname(item.getFirstChild().getTextContent());				
				}
				if (item.getNodeName() == "lastname"){
					myPerson.setLastname(item.getFirstChild().getTextContent());
				}				
				if (item.getFirstChild().getNodeType() == Node.TEXT_NODE){
					// item contains a text element
					System.out.println("Item first child content: " + item.getFirstChild().getNodeValue());	
				}
				if (item.getNodeName() == "healthprofile") {
					// item contains elements
					NodeList childs = item.getChildNodes();					
					Node subItem1 = childs.item(1);
					Node subItem2 = childs.item(3);
					Double pWeight = Double.parseDouble(subItem1.getFirstChild().getTextContent());
					Double pHeight = Double.parseDouble(subItem2.getFirstChild().getTextContent());
					HealthProfile hp = new HealthProfile(pWeight, pHeight);
					myPerson.sethProfile(hp);
				}
			}
		}
		return myPerson;
	}

	/**
	 * The health profile reader gets information from the command line about
	 * weight and height and calculates the BMI of the person based on this
	 * parameters
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

		HealthProfileReader test = new HealthProfileReader();
		test.loadXML();	

		/* Solution to Exercise #01-2e */
		int argCount = args.length;
		if (argCount == 0) {
			System.out
					.println("I cannot create people out of thing air. Give me at least a name and lastname.");
		} else if (argCount < 2) {
			System.out
					.println("Are you sure you gave me ALL the information I need?");
		} else {
			String method = args[0];
			if (method.equals("createNewPerson")) {
				Long personId = Long.parseLong(args[1]);
				String firstname = args[2];
				String lastname = args[3];
				String birthdate = args[4];
				createPerson(personId, firstname, lastname, birthdate);
			} else if (method.equals("displayHealthProfile")) {
				Long personId = Long.parseLong(args[1]);
				displayHealthProfile(personId);				
			} else if (method.equals("updateHealthProfile")) {
				Long personId = Long.parseLong(args[1]);
				Double height = Double.parseDouble(args[2]);
				Double weight = Double.parseDouble(args[3]);
				updateHealthProfile(personId, height, weight);
			} else if (method.equals("getPersonByFullName")) {
				Node node = test.getPersonByFullName(args[1], args[2]);
				System.out.println(node.getTextContent());
			} else if (method.equals("getPersonObjectByFullName")) {
				Person person = test.getPersonObjectByFullName(args[1], args[2]);
				System.out.println("Printing person object:");
				System.out.println(person.getFirstname());
				System.out.println(person.getLastname());
				System.out.println(person.getPersonId());
				System.out.println(person.getBirthdate());				
				System.out.println(person.gethProfile());				
			} else if (method.equals("getPersonWeight")) {				
				Node node = test.getPersonWeight(args[1], args[2]);
				System.out.println(node.getTextContent());
			} else if (method.equals("getPersonHeight")) {
				Node node = test.getPersonHeight(args[1], args[2]);
				System.out.println(node.getTextContent());
			} else if (method.equals("getPersonWithWeight")) {
				//System.out.println(args[2]);
				NodeList nodes = test.getPersonWithWeight(args[1], args[2]);
				if (nodes == null){
					System.out.println("Nobody found using your filter");
				}
				else {
					System.out.println("Found: " + nodes.getLength());
					for (int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						System.out.println(node.getTextContent());
					}
				}				
				
			} else {
				System.out.println("The system did not find the method '"+method+"'");
			}
		}
	}

	/* Solution to Exercise #01-2c 
	 * Check in browser with: 
	 * http://localhost:8080/axis2/services/HealthProfileReader/displayHealthProfile?personId=1
	 */
	public static HealthProfile displayHealthProfile(Long personId) {
		Person p = database.get(personId);
		HealthProfile hp = p.gethProfile();
		System.out.println(p.getFirstname() + " has a weight of "
				+ hp.getWeight() + " Kg. and a height of " + hp.getHeight());
		return hp;
	}

	/* Solution to Exercise #01-2d 	 
	 * Check in browser with: 
	 * http://localhost:8080/axis2/services/HealthProfileReader/updateHealthProfile?personId=1&height=1.78&weight=74
	 */
	public  static HealthProfile updateHealthProfile(Long personId, Double height,
			Double weight) {
		Person p = database.get(personId);
		HealthProfile hp = p.gethProfile();
		hp.setHeight(height);
		hp.setWeight(weight);
		System.out.println(p.getFirstname() + " has updated weight to "
				+ hp.getWeight() + " Kg. and updated height to "
				+ hp.getHeight());
		return hp;
	}	

}