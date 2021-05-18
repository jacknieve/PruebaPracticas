package payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@RestController
class EmployeeController {

  private final EmployeeRepository repository;

  EmployeeController(EmployeeRepository repository) {
    this.repository = repository;
  }


  // Aggregate root
  // tag::get-aggregate-root[]
  /*@GetMapping("/employees")
  List<Employee> all() {
    return repository.findAll();
  }*/
  
  @GetMapping("/employees")
  List<Employee> all() {
	  ArrayList<Employee> employees = new ArrayList<>();
	  try {
		  	/*URL resource = getClass().getClassLoader().getResource("kk.txt");
		  	System.out.println(resource);
		  	if (resource == null) {
		        throw new IllegalArgumentException("file not found!");
		    }
			File inputf = new File(resource.toURI());*/
			File inputf = new File("target/classes/kk.txt");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(inputf);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("employee");
			for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               Element eElement = (Element) nNode;
	               Long id = Long.parseLong(eElement.getAttribute("id"));
	               String name = eElement.getElementsByTagName("name").item(0).getTextContent();
	               String role = eElement.getElementsByTagName("role").item(0).getTextContent();
	               employees.add(new Employee(id,name,role));
	            }
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
    //return repository.findAll();
	  return employees;
  }
  // end::get-aggregate-root[]

  @PostMapping("/employees")
  Employee newEmployee(@RequestBody Employee newEmployee) {
    return repository.save(newEmployee);
  }

  // Single item
  
  /*@GetMapping("/employees/{id}")
  Employee one(@PathVariable Long id) {
    
    return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
  }*/
  
  /*@GetMapping("/employees/{id}")
  EntityModel<Employee> one(@PathVariable Long id) {

    Employee employee = repository.findById(id) //
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    
  }*/
  
  @GetMapping("/employees/{id}")
  Employee one(@PathVariable Long id) {
	  Employee e = null;
	  try {
		  	/*URL resource = getClass().getClassLoader().getResource("ids/"+id+"/kk.zip");
		  	System.out.println(resource);
		  	if (resource == null) {
		        throw new IllegalArgumentException("file not found!");
		    }
		  	File inputf = unzip(resource.toString(),getClass().getClassLoader().getResource("ids/"+id+"").toString());*/
		  	File inputf = unzip("target/classes/ids/"+id+"/kk.zip","target/classes/ids/"+id+"/");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.parse(inputf);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("employee");
	        Node nNode = nList.item(0);
	        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	           Element eElement = (Element) nNode;
	           Long idd = Long.parseLong(eElement.getAttribute("id"));
	           String name = eElement.getElementsByTagName("name").item(0).getTextContent();
	           String role = eElement.getElementsByTagName("role").item(0).getTextContent();
	           e= new Employee(idd,name,role);
	        }
			
			
		} catch (Exception ex) {
			System.out.println(ex);
		}
	  return e;
  }

  @PutMapping("/employees/{id}")
  Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
    
    return repository.findById(id)
      .map(employee -> {
        employee.setName(newEmployee.getName());
        employee.setRole(newEmployee.getRole());
        return repository.save(employee);
      })
      .orElseGet(() -> {
        newEmployee.setId(id);
        return repository.save(newEmployee);
      });
  }

  @DeleteMapping("/employees/{id}")
  void deleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
  }
  
  File unzip(String filePath,String fileUnzipPath) {
	  byte[] buffer = new byte[2048];
	  File f = null;
      try {
          ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));
          ZipEntry entry = zis.getNextEntry();
              String nombreArchivo = entry.getName();
              f = new File(fileUnzipPath+""+nombreArchivo);
              FileOutputStream fos = new FileOutputStream(f);
              int len;
              while ((len = zis.read(buffer)) > 0) {
                  fos.write(buffer, 0, len);
              }
              fos.close();
              zis.closeEntry();
	          zis.close();
	            
	            
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
             
	  return f; 
  }
}
