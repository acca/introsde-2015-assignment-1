import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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

	static {
//		Person pallino = new Person();
//		Person pallo = new Person(new Long(1), "Pinco", "Pallo", "1984-06-21");
//		HealthProfile hp = new HealthProfile(68.0, 1.72);
//		Person john = new Person(new Long(2), "John", "Doe", "1985-03-20", hp);
//
//		database.put(pallino.getPersonId(), pallino);
//		database.put(pallo.getPersonId(), pallo);
//		database.put(john.getPersonId(), john);
	}

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
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node;
    }


    public Node getPersonHeight(String firstName, String lastName) throws XPathExpressionException {
    	String expression = "/people/person[firstname='" + firstName + "' and lastname='" + lastName + "']/healthprofile/height";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node;
    }

    public String getPersonWeight(String personId) throws XPathExpressionException {
    	String expression = "/people/person[@id='" + personId + "']/healthprofile/weight";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node.getTextContent();
    }


    public String getPersonHeight(String personId) throws XPathExpressionException {
    	String expression = "/people/person[@id='" + personId + "']/healthprofile/height";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        return node.getTextContent();
    }

    
    public NodeList getPersonWithWeight(String operator, String weight) throws XPathExpressionException {
    	String expression = "/people/person[healthprofile/weight" + operator + weight + "]";
    	System.out.println("Using expression: " + expression);
    	XPathExpression expr = xpath.compile(expression);        
    	NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);    	
    	return nodes;
    }

//	public Person createPerson(Long personId, String firstname, String lastname, String birthdate) {
//		Person p = new Person(personId, firstname, lastname, birthdate);
//		database.put(p.getPersonId(), p);
//		System.out.println("A new person record (" + p.getPersonId()
//				+ ") has been created for " + p.getLastname() + ", "
//				+ p.getFirstname() + " born on " + p.getBirthdate());
//		return p;
//	}

	public Person getPersonObjectByFullName(String firstName, String lastName) throws XPathExpressionException {
		String expression = "/people/person[firstname='" + firstName + "' and lastname='" + lastName + "']";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        if (node == null) {
        	throw new XPathExpressionException("Person not found in XML");
        }
        else {
        	NodeList personDetails = node.getChildNodes();
            return fromXMLtoPerson(personDetails);	
        }        
	}
	
	public Person getPersonObjectById(Long personId) throws XPathExpressionException {
		String personIdString = String.format("%04d", personId);
		String expression = "/people/person[@id='" + personIdString + "']";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);        
        if (node == null) {
        	throw new XPathExpressionException("Person not found in XML");
        }
        else {
        	NodeList personDetails = node.getChildNodes();
            return fromXMLtoPerson(personDetails);	
        }
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
				if (item.getNodeName() == "birthdate"){
					String birthDateString = item.getFirstChild().getTextContent();
					Date birthDate = null;
					try {
						birthDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(birthDateString);
						myPerson.setBirthdateFromString(birthDateString);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					myPerson.setBirthdate(birthDate);					
				}
				if (item.getFirstChild().getNodeType() == Node.TEXT_NODE){
					// item contains a text element
					System.out.println("\tTag content: " + item.getFirstChild().getNodeValue());	
				}
				if (item.getNodeName() == "healthprofile") {
					// item contains elements
					NodeList childs = item.getChildNodes();					
					Node subItem1 = childs.item(1);
					Node subItem2 = childs.item(3);
					Node subItem3 = childs.item(5);
					Node subItem4 = childs.item(7);
					String lastUpdate = subItem1.getFirstChild().getTextContent();
					System.out.println("\tLastupdate: " + lastUpdate);
					Double pWeight = Double.parseDouble(subItem2.getFirstChild().getTextContent());
					System.out.println("\tWeight: " + pWeight);
					Double pHeight = Double.parseDouble(subItem3.getFirstChild().getTextContent());
					System.out.println("\tHeight: " + pHeight);
					String pBMI = subItem4.getFirstChild().getTextContent();
					System.out.println("\tBMI: " + pBMI);
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
			String method = args[0];
			if (method.equals("getAllPersons")) {
					NodeList nodes = test.getAllPerson();				
					test.printNodes(nodes);
			}
			else {
				System.out.println("Are you sure you gave me ALL the information I need?");
			}
		} else {
			String method = args[0];
			if (method.equals("createNewPerson")) {
				System.out.println("Not yet implemented");
//				Long personId = Long.parseLong(args[1]);
//				String firstname = args[2];
//				String lastname = args[3];
//				String birthdate = args[4];
//				test.createPerson(personId, firstname, lastname, birthdate);
			} else if (method.equals("displayHealthProfile")) {
				Long personId = Long.parseLong(args[1]);
				test.displayHealthProfile(personId);				
			} else if (method.equals("updateHealthProfile")) {
				Long personId = Long.parseLong(args[1]);
				Double height = Double.parseDouble(args[2]);
				Double weight = Double.parseDouble(args[3]);
				test.updateHealthProfile(personId, height, weight);
			} else if (method.equals("getPersonByFullName")) {
				Node node = test.getPersonByFullName(args[1], args[2]);
				System.out.println(node.getTextContent());
			} else if (method.equals("getPersonObjectByFullName")) {
				Person person = test.getPersonObjectByFullName(args[1], args[2]);
				if (person == null) System.out.println("Person not found");
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
				NodeList nodes = test.getPersonWithWeight(args[1], args[2]);			
				test.printNodes(nodes);
			}
			else {
				System.out.println("The system did not find the method '"+method+"'");
			}
		}
	}

	private void printNodes(NodeList nodes) {
		if (nodes == null){
			System.out.println("Nobody found");
		}
		else {
			System.out.println("Found: " + nodes.getLength());
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				System.out.println(node.getTextContent());			
			}
		}
	}

	public HealthProfile displayHealthProfile(Long personId) throws XPathExpressionException {
		Person p = getPersonObjectById(personId);
		HealthProfile hp = p.gethProfile();
		System.out.println(p.getFirstname() + " has a weight of "
				+ hp.getWeight() + " Kg. and a height of " + hp.getHeight());
		return hp;
	}

	public HealthProfile updateHealthProfile(Long personId, Double height, Double weight) {
		Person p = database.get(personId);
		HealthProfile hp = p.gethProfile();
		hp.setHeight(height);
		hp.setWeight(weight);
		System.out.println(p.getFirstname() + " has updated weight to "
				+ hp.getWeight() + " Kg. and updated height to "
				+ hp.getHeight());
		return hp;
	}
	
	public NodeList getAllPerson() throws XPathExpressionException {
		String expression = "//person";
    	System.out.println("Using expression: " + expression);
        XPathExpression expr = xpath.compile(expression);        
        NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        return nodes;
	}

}