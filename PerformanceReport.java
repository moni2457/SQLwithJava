import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class PerformanceReport {
	
	public void getSummary(String startDate, String endDate, String outputFile) {
		try {
			//validating the start date and end date by matching with Regex
		boolean validateStartDate = false;
		boolean validateEndDate = false;
		validateStartDate = startDate.matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");
		validateEndDate = endDate.matches("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");
		//checking if input values are null or not
		if(startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()
				&& validateStartDate == true &&  validateEndDate == true && !outputFile.isEmpty()
				&& outputFile.trim() != null) {
			//Initializing all the objects
			Connection connect = null;
			Statement statement1 = null;
			Statement statement2 = null;
			Statement statement3 = null;
			ResultSet resultSet1= null;
			ResultSet resultSet2= null;
			ResultSet resultSet3= null;
			Document doc = null;
			//initialization of the JDBC driver and the establishing the connectivity
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC", "mshah", "B00830791");
			// Creating an XML document
			DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=dbf.newDocumentBuilder();
			doc=db.newDocument();
			
			// Creating the root element of the main XML FILE
			Element root=doc.createElement("year_end_summary");
			doc.appendChild(root);
			Element year = doc.createElement("year");
			root.appendChild(year);
			
			//Creating start date and end date tags
			Element startDateTag =doc.createElement("startDate");
			startDateTag.appendChild(doc.createTextNode(startDate));
			year.appendChild(startDateTag);
			
			Element endDateTag = doc.createElement("endDate");
			endDateTag.appendChild(doc.createTextNode(endDate));
			year.appendChild(endDateTag);
			Element customerList=doc.createElement("customer_list");
			  root.appendChild(customerList);
			
			statement1 = connect.createStatement();
			statement2 = connect.createStatement();
			statement3 = connect.createStatement();
			statement1.execute("use csci3901;");
			//Executing the customer information query
			resultSet1= statement1.executeQuery("select c.customerName,c.addressLine1,c.city,c.postalCode,c.country,count(distinct o.customerNumber) as totalOrder,sum(priceEach*quantityOrdered) as sumorder  from customers c join orders o  on c.customerNumber = o.customerNumber join orderdetails od on o.orderNumber = od.orderNumber where o.orderDate between '"+startDate+"' and '"+endDate+"' and o.status != 'Cancelled' group by o.customerNumber ;");
			
			while (resultSet1.next()) {
				//creating XML tags for customer query
				  Element customer=doc.createElement("customer");
				  customerList.appendChild(customer);
				  Element custName=doc.createElement("customerName");
				  if(resultSet1.getString("customerName")!= null) {
				  custName.appendChild(doc.createTextNode(resultSet1.getString("customerName")));
				  }
				  customer.appendChild(custName);
				  
				  Element address=doc.createElement("address");
				  customer.appendChild(address);
				  
				  Element streetAddress=doc.createElement("street_address");
				  if(resultSet1.getString("addressLine1")!= null) {
				  streetAddress.appendChild(doc.createTextNode(resultSet1.getString("addressLine1")));
				  }
				  address.appendChild(streetAddress);
				  
				  Element city=doc.createElement("city");
				  if(resultSet1.getString("city")!= null) {
				  city.appendChild(doc.createTextNode(resultSet1.getString("city")));
				  }
				  address.appendChild(city);

				  
				  Element postal=doc.createElement("postal_code");
				  if(resultSet1.getString("postalCode")!= null) {
					  postal.appendChild(doc.createTextNode(resultSet1.getString("postalCode")));
				  }
				  address.appendChild(postal);
				  
				  Element country=doc.createElement("country");
				  if(resultSet1.getString("country")!= null) {
				  country.appendChild(doc.createTextNode(resultSet1.getString("country")));
				  }
				  address.appendChild(country);
				  
				  Element totalOrder=doc.createElement("totalOrder");
				  if(resultSet1.getString("totalOrder")!= null) {
				  totalOrder.appendChild(doc.createTextNode(resultSet1.getString("totalOrder")));
				  }
				  customer.appendChild(totalOrder);
				  
				  
				  Element sumorder=doc.createElement("sumorder");
				  if(resultSet1.getString("sumOrder")!= null) {
				  sumorder.appendChild(doc.createTextNode(resultSet1.getString("sumOrder")));
				  }
				  customer.appendChild(sumorder);
				  
			}
			//execution of second query
				  resultSet2= statement2.executeQuery("select p.productLine, p.productName, p.productVendor, od.quantityOrdered, (od.quantityOrdered*od.priceEach) as sales from products p join orderdetails od on p.productCode = od.productCode join orders o on o.orderNumber = od.orderNumber  where o.orderDate between '"+startDate+"' and '"+endDate+"'and o.status != 'Cancelled' ;");
				  while(resultSet2.next()) {
					  //creating XML tags for query 2
						Element productList =doc.createElement("product_list");
						  root.appendChild(productList);
						  Element productSet =doc.createElement("product_set");
						  productList.appendChild(productSet);
						  Element productLineName=doc.createElement("product_line_name");
						  if(resultSet2.getString("productLine")!= null) {
						  productLineName.appendChild(doc.createTextNode(resultSet2.getString("productLine")));
						  }
						  productSet.appendChild(productLineName);
						  
						  Element product =doc.createElement("product");
						  productSet.appendChild(product);
						  
						  Element productName=doc.createElement("product_name");
						  if(resultSet2.getString("productName")!= null) {
						  productName.appendChild(doc.createTextNode(resultSet2.getString("productName")));
						  }
						  product.appendChild(productName);
						  
						  Element productVendor=doc.createElement("product_vendor");
						  if(resultSet2.getString("productVendor")!= null) {
						  productVendor.appendChild(doc.createTextNode(resultSet2.getString("productVendor")));
						  }
						  product.appendChild(productVendor);
						 
						  Element unitsSold=doc.createElement("units_sold");
						  if(resultSet2.getString("quantityOrdered")!= null) {
						  unitsSold.appendChild(doc.createTextNode(resultSet2.getString("quantityOrdered")));
						  }
						  product.appendChild(unitsSold);
						  
						  Element totalSales1=doc.createElement("total_sales");
						  if(resultSet2.getString("sales")!= null) {
						  totalSales1.appendChild(doc.createTextNode(resultSet2.getString("sales")));
						  }
						  product.appendChild(totalSales1);
				  }
						  
						 //Execution of Third Query
						  resultSet3= statement3.executeQuery("select e.firstName, e.lastName, o.city, count(distinct c.customerNumber) as customersActive, sum(od.priceEach*od.quantityOrdered) as totalSales from offices o join employees e on e.officeCode = o.officeCode join customers c on e.employeeNumber = c.salesRepEmployeeNumber  join orders os on os.customerNumber = c.customerNumber join orderdetails od on od.orderNumber = os.orderNumber where  os.orderDate between '"+startDate+"' and '"+endDate+"' and os.status != 'Cancelled' ");
						  while(resultSet3.next()) {
							  //creation of XML tags 
							  Element staffList =doc.createElement("staff_list");
							  root.appendChild(staffList);
							  Element employee =doc.createElement("employee");
							  staffList.appendChild(employee);
							  
							  Element firstName=doc.createElement("first_name");
							  if(resultSet3.getString("firstName")!= null) {
							  firstName.appendChild(doc.createTextNode(resultSet3.getString("firstName")));
							  }
							  employee.appendChild(firstName);
							  
							  Element lastName=doc.createElement("last_name");
							  if(resultSet3.getString("lastName")!= null) {
							  lastName.appendChild(doc.createTextNode(resultSet3.getString("lastName")));
							  }
							  employee.appendChild(lastName);
							 
							  Element officeCity=doc.createElement("office_city");
							  if(resultSet3.getString("city")!= null) {
							  officeCity.appendChild(doc.createTextNode(resultSet3.getString("city")));
							  }
							  employee.appendChild(officeCity);
							  
							  Element activeCustomers=doc.createElement("active_customers");
							  if(resultSet3.getString("customersActive")!= null) {
							  activeCustomers.appendChild(doc.createTextNode(resultSet3.getString("customersActive")));
							  }
							  employee.appendChild(activeCustomers);
							  
							  Element totalSales=doc.createElement("total_sales");
							  if(resultSet3.getString("totalSales")!= null) {
							  totalSales.appendChild(doc.createTextNode(resultSet3.getString("totalSales")));
							  }
							  employee.appendChild(totalSales);
						  }	
			
			// creating the XML file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult((outputFile));
            transformer.transform(domSource, streamResult);
 
            System.out.println("Done creating XML File");
            resultSet3.close();
            resultSet2.close();
			resultSet1.close();
			statement3.close();
			statement2.close();
			statement1.close();
			connect.close();
			
		} else {
			System.out.println("Enter valid date or valid path");
		}
		
	} catch(Exception e){
		System.out.println(e);
	}	
}
}
