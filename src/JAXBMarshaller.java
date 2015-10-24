

import people.generated.*;
import people.generated.People.Person;
import people.generated.People.Person.Healthprofile;
import javax.xml.bind.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class JAXBMarshaller {
	public void generateXMLDocument(File xmlDocument) {
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance("people.generated");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
			people.generated.ObjectFactory factory = new people.generated.ObjectFactory();
			
			
			Person person = new Person();
			person.setId(new Long(1));
			person.setFirstname("David");
			person.setLastname("Santoro");
			// bithdate

			Healthprofile hp = factory.createPeoplePersonHealthprofile();
			hp.setHeight(1.06);
			hp.setWeight(20.5);
			
			person.setHealthprofile(hp);
			People people = factory.createPeople();
			people.getPerson().add(person);
			
			marshaller.marshal(people,new FileOutputStream(xmlDocument));

		} catch (IOException e) {
			System.out.println(e.toString());

		} catch (JAXBException e) {
			System.out.println(e.toString());

		}

	}

	public static void main(String[] argv) {
		String xmlDocument = "people-gen.xml";
		JAXBMarshaller jaxbMarshaller = new JAXBMarshaller();
		jaxbMarshaller.generateXMLDocument(new File(xmlDocument));
		try (BufferedReader br = new BufferedReader(new FileReader(xmlDocument))) {
			   String line = null;
			   while ((line = br.readLine()) != null) {
			       System.out.println(line);
			   }
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
//	public String getBirthdateAsString(){		
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//		return dateFormat.format(this.birthdate);
//	}
//	
//	public void setBirthdateFromString(String birthDate){		
//		try {
//			this.birthdate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(birthDate);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//	}
}
