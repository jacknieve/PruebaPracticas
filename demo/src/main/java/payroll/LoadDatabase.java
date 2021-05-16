package payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

@Configuration
class LoadDatabase {

  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  @Bean
  CommandLineRunner initDatabase(EmployeeRepository repository) {

    return args -> {
      log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
      log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
    };
  }
  
  @Bean
  CommandLineRunner initDB(EmployeeRepository repository) {
	  ArrayList<Employee> employees = new ArrayList<>();
	  try {
		  	URL resource = getClass().getClassLoader().getResource("kk.txt");
		  	System.out.println(resource);
		  	if (resource == null) {
		        throw new IllegalArgumentException("file not found!");
		    }
			File inputf = new File(resource.toURI());
			System.out.println("File created");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(inputf);
			System.out.println("Document parsed");
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("employee");
			for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               Element eElement = (Element) nNode;
	               Long id = Long.parseLong(eElement.getAttribute("id"));
	               System.out.println(""+id);
	               String name = eElement.getElementsByTagName("name").item(0).getTextContent();
	               String role = eElement.getElementsByTagName("role").item(0).getTextContent();
	               employees.add(new Employee(id,name,role));
	            }
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	  return args -> {
	    	for(Employee e : employees) {
	    		 log.info("Preloading " + repository.save(e));
	    	}
	    };
    
  }
}