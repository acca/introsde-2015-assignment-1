

import java.io.File;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import dao.PeopleStore;
import pojos.HealthProfile;
import pojos.Person;


public class HealthProfileJson {
	public static Person person = new Person();	
	public static PeopleStore people = new PeopleStore();

	public static void initializeDB() {
		Person person = new Person();				
		person.setPersonId(new Long(101));
		person.setFirstname("Daniele");
		person.setLastname("Santoro");
		person.setBirthdate(new Date());
		HealthProfile hp = new HealthProfile(65, 1.70, new Date());		
		person.sethProfile(hp);
		
		Person person1 = new Person(new Long(1001),"Paolo", "Micheli", new Date(1980, 10, 3));
		
		Person person2 = new Person();
		person2.setFirstname("Michela");
		person2.setLastname("Bianchi");
		
		people.getData().add(person);
		people.getData().add(person1);
		people.getData().add(person2);
	}	

	public static void main(String[] args) throws Exception {
		
		initializeDB();
		
		// Jackson Object Mapper 
		ObjectMapper mapper = new ObjectMapper();
		
		// Adding the Jackson Module to process JAXB annotations
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        
		// configure as necessary
		mapper.registerModule(module);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        String result = mapper.writeValueAsString(people);
        System.out.println(result);
        mapper.writeValue(new File("people-gen.json"), people);
    }
}

