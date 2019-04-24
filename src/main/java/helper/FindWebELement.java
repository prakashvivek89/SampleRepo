package helper;

import org.openqa.selenium.By;

import exceptions.FrameworkExceptions;
import readexcel.ReadObjectRepo;
import readexcel.TestcaseFlow;

/**
 * This class locates all the web element in the web page.
 */

public class FindWebELement extends TestcaseFlow{

	public By getWebElement(String ele, String[] data) throws FrameworkExceptions  {
		By by = null;
		if (ReadObjectRepo.objectrepo.containsKey(ele.toLowerCase())&&ReadObjectRepo.objectrepo.get(ele.toLowerCase()) != null) {
				
				String type = ReadObjectRepo.objectrepo.get(ele.toLowerCase()).get("type");
				String value = ReadObjectRepo.objectrepo.get(ele.toLowerCase()).get("value");
				
			if(value.isEmpty()) {
				throw new FrameworkExceptions("In object repository, no value is defined against <b>'Value'</b> column for the Webelement '<b>" + ele + "</b>'");
			}
				/*  Code for dynamic locator  */
				
				if (value.contains("{$argument}")) {
					String[] first = value.split("\\{");
					String[] second = first[1].split("\\}");
					if (second.length > 1) {
						value = first[0] + data[0] + second[1];
					} else {
						value = first[0] + data[0];
					}
				}
				switch (type) {
				case "id":
					by = By.id(value);
					break;
				case "classname":
					by = By.className(value);
					break;
				case "name":
					by = By.name(value);
					break;
				case "linktext":
					by = By.linkText(value);
					break;
				case "xpath":
					by = By.xpath(value);
					break;
				case "css":
					by = By.cssSelector(value);
					break;
				case "":
					throw new FrameworkExceptions("In object repository, no value is defined against <b>'Type'</b> column for the webelement <b>" + ele+"</b>");
				default:
					throw new FrameworkExceptions("In object repository, invalid value is defined against <b>'Type'</b> column for the web element <b>" + ele+"</b>");
				}
				return by;
			} else {
				throw new FrameworkExceptions("Webelement <b>'" + ele.toLowerCase() + "'</b> does not exist in the object repository");
			}
	
	}
}
