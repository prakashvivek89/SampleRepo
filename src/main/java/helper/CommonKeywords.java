package helper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.aventstack.extentreports.Status;

import exceptions.DatabaseExceptions;
import exceptions.SeleniumExceptions;

/**
 * This class contains all the common web actions which is used in test scripts.
 */

public class CommonKeywords extends WebdriverHelper{
	private final long DRIVER_WAIT_TIME = 15;
	public Map<Integer, HashMap<String, String>> storeVariables = new ConcurrentHashMap<>();
	public Map<Integer, HashMap<String, String>> storeDBValues = new ConcurrentHashMap<>();
	public Map<Integer, HashMap<String, String>> val_Thread = new ConcurrentHashMap<>();
	 String parentWindowHandle = null;
	private  final Logger LOG = LoggerFactory.getLogger(CommonKeywords.class);
	WebDriver driver = WebdriverHelper.getWebDriver();
	private WebDriverWait wait = new WebDriverWait(driver, DRIVER_WAIT_TIME);

	public  void getWebelementAction(String action, By by, String[] data) throws SeleniumExceptions, IOException, SQLException, DatabaseExceptions {
		switch (action.toLowerCase()) {
		case "entertext":
				checkIfElementIsPassed(by, action);
				waitForExpectedAndEnterText(by, data);
			break;
			
		case "entertextbyscript":
			checkIfElementIsPassed(by, action);
			enterTextByScript(by, data);
		break;

		case "click":
			checkIfElementIsPassed(by, action);
			waitForClick(by);
			break;

		case "validateelementpresent":
			checkIfElementIsPassed(by, action);
			elementIsPresent(by);
			break;

		case "validateelementnotpresent":
			checkIfElementIsPassed(by, action);
			elementIsNotPresent(by);
			break;

		case "currenturlcontains":
			currentURLContains(data);
			break;

		case "gettextandmatch":
			checkIfElementIsPassed(by, action);
			getTextAndMatch(by, data);
			break;

		case "gettext":
			checkIfElementIsPassed(by, action);
			getText(by, data);
			break;

		case "textmatch":
			textMatch(data);
			break;

		case "textpartialmatch":
			textPartialMatch(data);
			break;

		case "verifytextnotcontained":
			checkIfElementIsPassed(by, action);
			verifyTextNotContained(by, data);
			break;

		case "textnotmatch":
			textNotMatch(data);
			break;

		case "notextpresent":
			checkIfElementIsPassed(by, action);
			noTextPresent(by);
			break;

		case "getattribute":
			checkIfElementIsPassed(by, action);
			getAttribute(by, data);
			break;

		case "waitforelementtodisappear":
			checkIfElementIsPassed(by, action);
			waitforelementtodisappear(by);
			break;

		case "waitforelementtovisible":
			checkIfElementIsPassed(by, action);
			waitforelementtovisible(by);
			break;

		case "clear":
			checkIfElementIsPassed(by, action);
			clear(by);
			break;

		case "selectitembytext":
			checkIfElementIsPassed(by, action);
			selectItemByText(by, data);
			break;

		case "selectitembyvalue":
			checkIfElementIsPassed(by, action);
			selectItemByValue(by, data);
			break;

		case "selectitembyindex":
			checkIfElementIsPassed(by, action);
			selectItemByIndex(by, data);
			break;

		case "executescript":
			checkIfElementIsPassed(by, action);
			executeScript(by, data);
			break;

		case "fileupload":
			checkIfElementIsPassed(by, action);
			fileUpload(by, data);
			break;

		case "wait":
			wait(data);
			break;

		case "verifytextcontainedinlist":
			checkIfElementIsPassed(by, action);
			verifyTextContainedInList(by, data);
			break;

		case "verifytextpresentinlist":
			checkIfElementIsPassed(by, action);
			verifyTextPresentInList(by, data);
			break;

		case "getdatafromdatabase":
			getDataFromDataBase(data);
			break;

		case "getdata":
			getData(data);
			break;

		case "getdatafordbvalidation":
			checkIfElementIsPassed(by, action);
			getDataForDBValidation(by, data);
			break;

		case "readfile":
			readfile(data);
			break;
		
		case "updatedatabase":
			updateDatabase(data);
			break;
			
		case "navigateto":
			navigateToURL(WebdriverHelper.siteURL);
			break;

		case "closebrowser":
			closeCurrentBrowser();
			break;

		case "quitbrowser":
			closeBrowser();
			break;

		case "refreshbrowser":
			refreshBrowser();
			break;

		case "goforward":
			goForward();
			break;

		case "goback":
			goBack();
			break;

		case "switchtonexttab":
			switchToNextTab();
			break;

		case "switchtoprevioustab":
			switchToPreviousTab();
			break;

		case "comparewithdb":
			compareWithDB();
			break;
			
				case "":
			throw new SeleniumExceptions("Please enter an action");

		default:
			throw new SeleniumExceptions("Action : '" + action + "' is not defined");
		}
	}

	
	/** This method moves the user to the next tab*/	
	private  void switchToNextTab() {
		parentWindowHandle = driver.getWindowHandle();
		driver.getWindowHandles();
		 ArrayList<String> windowHandles = new ArrayList<String>(driver.getWindowHandles());
		for (String window : windowHandles) {
			if (!(window.equals(parentWindowHandle))) {
				driver.switchTo().window(window);
				driver.switchTo().defaultContent();
				if (driver.toString().contains("IE")) {
					((JavascriptExecutor) getWebDriver()).executeScript("window.focus();");
				}
			}
		}
	}

	/** This method moves the user to the previous tab*/
	private  void switchToPreviousTab() {
		driver.findElement(By.tagName("html")).sendKeys(Keys.CONTROL, Keys.PAGE_UP);
		driver.switchTo().defaultContent();
	}

	
	/** This method is used for selecting the value from a drop down by visible text*/
	private  void selectItemByText(By by, String[] data) throws SeleniumExceptions {
		Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
		try {
			if (!(data[0].isEmpty())) {
				sel.selectByVisibleText(data[0]);
			} else {
				sel.selectByIndex(0);
			}
		} catch (Exception e) {
			throw new SeleniumExceptions(
					"Dropdown with locator '" + by + "' does not contain the visible text '" + data[0] + "'");
		}
	}

	/** This method is used for selecting the value from a drop down by value*/
	private  void selectItemByValue(By by, String[] data) throws SeleniumExceptions {
		Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
		try {
			if (!(data[0].isEmpty())) {
				sel.selectByValue(data[0]);
			} else {
				sel.selectByIndex(0);
			}
		} catch (Exception e) {
			throw new SeleniumExceptions(
					"Dropdown with locator '" + by + "' does not contain the value '" + data[0] + "'");
		}
	}

	
	/** This method is used for selecting the value from a drop down by index*/
	private  void selectItemByIndex(By by, String data[]) throws SeleniumExceptions {
		Select sel = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)));
		try {
			if (!(data[0].isEmpty())) {
				sel.selectByIndex(Integer.parseInt(data[0]));
			} else {
				sel.selectByIndex(0);
			}
		} catch (Exception e) {
			throw new SeleniumExceptions(
					"Dropdown with locator '" + by + "' does not contain the index '" + data[0] + "'");
		}
	}

	
	/** This method is used for clearing any text present in the input text box*/
	private  void clear(By by) {
		wait.until(ExpectedConditions.visibilityOfElementLocated(by)).clear();
	}

	
	/** This method waits for any element to get disappear*/
	private  void waitforelementtodisappear(By by) {
		int count = 0;
		while(!(driver.findElements(by).isEmpty())&&count<5) {
			wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
			count = count+1;
		}
	}

	
	/** This method waits for any element to get visible*/
	private  void waitforelementtovisible(By by) {
		JavascriptExecutor jse2 = (JavascriptExecutor)driver;
		jse2.executeScript("arguments[0].scrollIntoView()", wait.until(ExpectedConditions.visibilityOfElementLocated(by))); 
		
	}

	
	/** This method gets the text from any web element and matches the same with the expected text
	 * @throws SeleniumExceptions */
	private  void getTextAndMatch(By by, String[] data){
		if (storeVariables.get((int)  (Thread.currentThread().getId())).containsKey(data[0])) {
			Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText(),
					storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]));
		} else {
			Assert.assertEquals(wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText(), data[0]);
		}
	}

	private  void verifyTextNotContained(By by, String[] data) {		
		Assert.assertTrue(!(data[0].contains(wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText())), "'" + data[0] + "' contains '"
				+ wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText() + "'");
	}

	private  void verifyTextPresentInList(By by, String[] data) {
		List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
		List<String> text = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			text.add(elements.get(i).getText());
		}
		Assert.assertTrue(text.contains(data[0]), data[0] + " is not present");
	}

	private  void verifyTextContainedInList(By by, String[] data) throws SeleniumExceptions {
		List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getText().contains(data[0])) {

			} else {
				throw new SeleniumExceptions(data[0] + " is not present");
			}
		}
	}

	private void noTextPresent(By by) throws SeleniumExceptions{
		WebElement ele = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		
		if(!(ele.getAttribute("value")==null||ele.getAttribute("value").isEmpty())) {
			throw new SeleniumExceptions("Text '"+ ele.getAttribute("value")+ "' is present");
		}
		else if (!(ele.getAttribute("text")==null||ele.getAttribute("text").isEmpty())) {
			throw new SeleniumExceptions("Text '"+ ele.getAttribute("text")+ "' is present for the web element " + by);
		}		
		
	}

	private  void textNotMatch(String[] data) {
		if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) != null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) != null)) {
			Assert.assertNotEquals(storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]),
					storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]));
		} else if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) == null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) != null)) {
			Assert.assertNotEquals(data[0], storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]));
		} else if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) != null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) == null)) {
			Assert.assertNotEquals(storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]), data[1]);
		} else if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) == null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) == null)) {
			Assert.assertNotEquals(data[0], (data[1]));
		}
	}

	private  void getText(By by, String[] data) throws SeleniumExceptions {
		if(data.length>=1) {
			storeVariables.get((int)  (Thread.currentThread().getId())).put(data[0], wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText());
		}
		else {
			throw new SeleniumExceptions("Kindly enter a variable to store the text value for element " + by);
		}
	}

	private void getAttribute(By by, String[] data) throws SeleniumExceptions {
			if (wait.until(ExpectedConditions.visibilityOfElementLocated(by))
					.getAttribute(data[1]) != null) {
				storeVariables.get((int)(Thread.currentThread().getId())).put(data[0],
						wait.until(ExpectedConditions.visibilityOfElementLocated(by))
								.getAttribute(data[1]).trim());
			} else if (data[1].equalsIgnoreCase("text")) {
				storeVariables.get((int)(Thread.currentThread().getId())).put(data[0],
						wait.until(ExpectedConditions.visibilityOfElementLocated(by)).getText());
			} else if (data[1].equalsIgnoreCase("dropdownvalue")) {
				storeVariables.get((int)(Thread.currentThread().getId())).put(data[0],
						new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)))
								.getFirstSelectedOption().getAttribute("value"));
			} else if (data[1].equalsIgnoreCase("dropdowntext")) {
				storeVariables.get((int)(Thread.currentThread().getId())).put(data[0],
						new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(by)))
								.getFirstSelectedOption().getText());
			} 
			
			else if (data[1].equalsIgnoreCase("dropdownindex")) {
				List<WebElement> list = new Select(waitForExpectedElement(by)).getOptions();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getText()
							.equals(new Select(waitForExpectedElement(by)).getFirstSelectedOption().getText())) {
						storeVariables.get((int)(Thread.currentThread().getId())).put(data[0], String.valueOf(i));
						break;
					}
				}
			}
			
			else {
				throw new SeleniumExceptions("Attribute '" + data[1] + "' is not valid for the element " + by);
			}
		}

	private  void textMatch(String[] data) throws SeleniumExceptions {
		if(data.length>1) {
			if ((storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]) != null)
					&& (storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]) != null)) {
				Assert.assertEquals(storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]).trim(),
						storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]).trim());

			} else if ((storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]) == null)
					&& (storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]) != null)
					&& !(storeDBValues.get((int) (Thread.currentThread().getId())).containsKey(data[0]))) {
				Assert.assertEquals(data[0].trim(),
						storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]).trim());
			}

			else if ((storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]) != null)
					&& (storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]) == null)) {
				Assert.assertEquals(storeVariables.get((int)(Thread.currentThread().getId())).get(data[0].trim()),
						data[1].trim());
			}

			else if ((storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]) == null)
					&& (storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]) == null)
					&& !(storeDBValues.get((int) (Thread.currentThread().getId())).containsKey(data[0]))) {
				Assert.assertEquals(data[0].trim(), (data[1]).trim());
			}

			else if (storeDBValues.get((int) (Thread.currentThread().getId())).containsKey(data[0])) {
				Assert.assertEquals(data[1].trim(),
						storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0]).trim());
			}
		} else {
			throw new SeleniumExceptions("Enter two values to get matched");
		}
	}

	/**private  void textPartialMatch(String[] data) throws SeleniumExceptions {
		if ((storeVariables.get((int)(Thread.currentThread().getId())).get(data[0]) != null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) != null)) {
			
			Assert.assertTrue(storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0])
					.contains(storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1])), "Text '" + storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0])
					+ "' does not contains the text'" + storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) + "'");
			
			
			
			
			if (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0])
					.contains(storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]))) {
			} else {
				throw new SeleniumExceptions("Text '" + storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0])
						+ "' does not contains the text'" + storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) + "'");
			}
		}

		else if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) == null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) != null)) {
			if (data[0].contains(storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]))) {

			} else {
				throw new SeleniumExceptions("Text '" + data[0] + "' does not contains the text'"
						+ storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) + "'");
			}
		}

		else if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) != null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) == null)) {
			if (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]).contains(data[1])) {

			} else {
				throw new SeleniumExceptions("Text '" + storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0])
						+ "' does not contains the text'" + data[1] + "'");
			}
		}

		else if ((storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]) == null)
				&& (storeVariables.get((int)  (Thread.currentThread().getId())).get(data[1]) == null)) {
			if (data[0].contains(data[1])) {

			} else {
				throw new SeleniumExceptions("Text '" + data[0] + "' does not contains the text'"
						+ data[1] + "'");
			}
		}
	}*/
	
	
	private void textPartialMatch(String[] data) {
		
		String message = "' does not contains the text '";

		if (storeVariables.get((int) (Thread.currentThread().getId())).get(data[0])!=null && storeVariables.get((int) (Thread.currentThread().getId())).get(data[1])!=null) {
			String leftvaluestoreVariables = storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]);
			String rightvaluestoreVariables = storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]);
			Assert.assertTrue(leftvaluestoreVariables.contains(rightvaluestoreVariables), "Text '" + leftvaluestoreVariables + message + rightvaluestoreVariables + "'");
		}

		else if (storeVariables.get((int) (Thread.currentThread().getId())).get(data[0])==null && storeVariables.get((int) (Thread.currentThread().getId())).get(data[1])!=null) {
			String rightvaluestoreVariables = storeVariables.get((int) (Thread.currentThread().getId())).get(data[1]);
			Assert.assertTrue(data[0].contains(rightvaluestoreVariables), "Text '" + data[0] + message + rightvaluestoreVariables + "'");
		}

		else if (storeVariables.get((int) (Thread.currentThread().getId())).get(data[0])!=null && storeVariables.get((int) (Thread.currentThread().getId())).get(data[1])==null) {
			String leftvaluestoreVariables = storeVariables.get((int) (Thread.currentThread().getId())).get(data[0]);
			Assert.assertTrue(leftvaluestoreVariables.contains(data[1]), "Text '" + leftvaluestoreVariables + message + data[1] + "'");
		}

		else if (storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0])!=null && storeDBValues.get((int) (Thread.currentThread().getId())).get(data[1])!=null) {
			String leftvaluestoreDBValues = storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0]);
			String rightvaluestoreDBValues = storeDBValues.get((int) (Thread.currentThread().getId())).get(data[1]);
			Assert.assertTrue(leftvaluestoreDBValues.contains(rightvaluestoreDBValues), "Text '" + leftvaluestoreDBValues + message + rightvaluestoreDBValues + "'");
		}

		else if (storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0])==null && storeDBValues.get((int) (Thread.currentThread().getId())).get(data[1])!=null) {
			String rightvaluestoreDBValues = storeDBValues.get((int) (Thread.currentThread().getId())).get(data[1]);
			Assert.assertTrue(data[0].contains(rightvaluestoreDBValues), "Text '" + data[0] + message + rightvaluestoreDBValues + "'");
		}

		else if (storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0])!=null && storeDBValues.get((int) (Thread.currentThread().getId())).get(data[1])==null) {
			String leftvaluestoreDBValues = storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0]);
			Assert.assertTrue(leftvaluestoreDBValues.contains(data[1]),
					"Text '" + leftvaluestoreDBValues + message + data[1] + "'");
		}

		else if ((storeVariables.get((int) (Thread.currentThread().getId())).get(data[0])==null) && (storeVariables.get((int) (Thread.currentThread().getId())).get(data[1])==null)
				&& (storeDBValues.get((int) (Thread.currentThread().getId())).get(data[0])==null) && (storeDBValues.get((int) (Thread.currentThread().getId())).get(data[1])==null)) {
			Assert.assertTrue(data[0].contains(data[1]), "Text '" + data[0] + message + data[1] + "'");
		}
	}

	private  void currentURLContains(String [] url) throws SeleniumExceptions {
		if (storeVariables.get((int)  (Thread.currentThread().getId())).get(url[0]) == null) {
			wait.until(ExpectedConditions.urlContains(url[0]));
		} else if (storeVariables.get((int)  (Thread.currentThread().getId())).get(url[0]) != null) {
			wait.until(ExpectedConditions.urlContains(storeVariables.get((int)  (Thread.currentThread().getId())).get(url[0])));
		}
		else {
			throw new SeleniumExceptions(
					url[0] + " is not present in the current URL '" + driver.getCurrentUrl() + "'");
		}
	}

	/**This method verifies web element should not be present on the web page 
	 * This method takes one parameter, By(used to locate the web element).
	 */
	private  void elementIsNotPresent(By by) {
		Assert.assertTrue(wait.until(ExpectedConditions.invisibilityOfElementLocated(by)), "Web Element with value : '"+by+"' is present on the web page.");
	}

	
	/**This method verifies whether element should be present on the web page.
	 * This method takes one parameter, By(used to locate the web element).*/
	
	private  void elementIsPresent(By by) throws SeleniumExceptions {
		Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(by)).isDisplayed(), "Web Element with value : '"+by+"' is not present on the web page.");
	}

	
	/**This method closes the current browser, but does not kill the web driver session session.*/
	private  void closeCurrentBrowser() {
		parentWindowHandle = driver.getWindowHandle();
		driver.getWindowHandles();
		driver.close();
		 ArrayList<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
		for (String window : windowHandles) {
			if (!(window.equals(parentWindowHandle))) {
				driver.switchTo().window(window);
				driver.switchTo().defaultContent();
				if (driver.toString().contains("IE")) {
					((JavascriptExecutor) getWebDriver()).executeScript("window.focus();");
				}
			}
		}
		driver.switchTo().defaultContent();
	}

	
	/**This method navigates to the given URL*/
	private  void navigateToURL(String url) {
		driver.manage().deleteAllCookies();
		driver.navigate().to(url);
		driver.manage().window().maximize();
		String overrideLink = "overrideLink";
		while(!(driver.findElements(By.id(overrideLink)).isEmpty())) {
			driver.findElement(By.id(overrideLink)).click();
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(overrideLink)));
		}
		Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.INFO,"navigated to URL'<b>"+url+"</b>'");
	}

	
	/**This method enters the value in the textboxes.
	 * This method takes two parameters, By(used to locate the webelement) and a String array of size 1(used as input to the textbox).*/
	private  void waitForExpectedAndEnterText(By by, String[] data) throws SeleniumExceptions {
		WebElement ele = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		
		if (data[0].equalsIgnoreCase("randstring")) {
			data[0] = RandomStringUtils.randomAlphabetic(6);
			
		} else if (data[0].toLowerCase().contains("randint")) {
			if (data[0].contains("-")) {
				int range = Integer.parseInt(data[0].split("-")[1]);
				data[0] = String.valueOf(RandomStringUtils.randomNumeric(range));
			} else {
				throw new SeleniumExceptions("Please enter a range");
			}
		} else if (data[0].toLowerCase().contains("randemail")) {
			data[0] = RandomStringUtils.randomAlphabetic(6) + "@mailinator.com";
		} 
		
		if ((storeVariables.get((int)(Thread.currentThread().getId()))!=null)&&storeVariables.get((int) (Thread.currentThread().getId())).containsKey(data[0])) {
			data[0] = storeVariables.get((int)  (Thread.currentThread().getId())).get(data[0]);
		}
		try {
			Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.INFO,"Entering '"+data[0]+"' in text box "+by);
			if (ele.isDisplayed()) {
				ele.clear();
				if(ele.getAttribute("value").isEmpty()&&!(data[0].isEmpty())) {
					wait.until(ExpectedConditions.visibilityOfElementLocated(by)).sendKeys(data[0]);
					int count  = 0;
					while(ele.getAttribute("value").isEmpty()&&count<5) {
						ele.sendKeys(Keys.HOME,Keys.chord(Keys.SHIFT,Keys.END),data[0]);
						count = count + 1;
						Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.PASS, "entered '"+data[0]+"' in text box "+by);
					}
				}
				else {
					ele.sendKeys(Keys.HOME,Keys.chord(Keys.SHIFT,Keys.END),data[0]);
//					Reporting.extentTestMap.get((int) (Thread.currentThread().getId())).log(Status.PASS, "entered '"+data[0]+"' in text box "+by);
				}
			}
		} catch (Exception e) {
			throw new SeleniumExceptions(e.getMessage());
		}
		
	}

	
	private  void enterTextByScript(By by, String[] text) throws SeleniumExceptions {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		if(text[0].length()>0&&text[0]!=null) {
		if (text[0].equalsIgnoreCase("randstring")) {
			text[0] = RandomStringUtils.randomAlphabetic(6);
		} else if (text[0].toLowerCase().contains("randint")) {
			if (text[0].contains("-")) {
				int range = Integer.parseInt(text[0].split("-")[1]);
				text[0] = String.valueOf(RandomStringUtils.randomNumeric(range));
			} else {
				throw new SeleniumExceptions("Please enter a range");
			}
		} else if (text[0].toLowerCase().contains("randemail")) {
			text[0] = RandomStringUtils.randomAlphabetic(6) + "@mailinator.com";
		} 
		if (storeVariables.get((int)  (Thread.currentThread().getId())).containsKey(text[0])) {
			text[0] = storeVariables.get((int)  (Thread.currentThread().getId())).get(text[0]);
		}
		
		js.executeScript("arguments[0].value='" + text[0] + "';", driver.findElement(by));
		}
		else{
			js.executeScript("arguments[0].value='" + "" + "';", driver.findElement(by));
		}
	}
	
	
	/**This method forcefully waits for the specified time*/
	private void wait(String[] data) {
		if (data[0].contains(".")) {
			data[0] = data[0].split("\\.")[0];
		}
		int miliSeconds = Integer.parseInt(data[0]) * 1000;
		try {
			Thread.currentThread().sleep(miliSeconds);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			Thread.currentThread().interrupt();
		}
	}

	
	/** This method clicks the web element. This method takes one parameter, By(used to locate the web element)*/
	private void waitForClick(By by) {
//		JavascriptExecutor jse2 = (JavascriptExecutor)driver;
//		jse2.executeScript("arguments[0].scrollIntoView()", wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(by))); 
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			Actions actions = new Actions(driver);
			actions.moveToElement(wait.ignoring(StaleElementReferenceException.class).until(ExpectedConditions.elementToBeClickable(by))).click().build().perform();
		}
	}

	
	/** This method navigates to the previous page*/
	private  void goBack() {
		driver.navigate().back();
	}

	
	/** This method navigates to the next page*/
	private  void goForward() {
		driver.navigate().forward();
	}

	
	/** This method refreshes the browser web page*/
	private  void refreshBrowser() {
		driver.navigate().refresh();
	}

	
	/** This method executes the java script on a particular web element present on the browser web page*/
	private  void executeScript(By by, String[] data) throws SeleniumExceptions {
		if(data.length<1) {
			throw new SeleniumExceptions("Enter the java script");
		}
		else{
			JavascriptExecutor js = (JavascriptExecutor) driver;
			driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
			js.executeScript(data[0], driver.findElement(by));
		}
		
	}
	
	private void getDataFromDataBase(String [] query) throws DatabaseExceptions, IOException, SQLException {
		HashMap<String, String> vl = new HashMap<>();
		String queryToExecute = null;
		HashMap<String, String> listDBValidation = new HashMap<>();
		val_Thread.put((int) (Thread.currentThread().getId()), vl);
		storeDBValues.put((int) (Thread.currentThread().getId()), listDBValidation);
		storeDBValues.get((int) (Thread.currentThread().getId())).clear();
		val_Thread.get((int) (Thread.currentThread().getId())).clear();
		try (Statement stmt = DatabaseConnection.getCon().createStatement();) {
			queryToExecute = seperateSQLQueryAndTestdata(query);
			
			ResultSet rs = stmt.executeQuery(queryToExecute);
			if (rs.next()) {
				getDataFromResultSet(rs);
			} else {
				throw new DatabaseExceptions("For query : <br /><b>" + queryToExecute + "</b><br /> No rows returned ");
			}
		} catch (SQLException e) {
			throw new DatabaseExceptions(
					"Query : <br /><b>" + queryToExecute + "</b><br /> returns error message : '<b>" + e.getMessage() + "</b>'");
		}
	}
	
	private String seperateSQLQueryAndTestdata(String [] query) throws IOException {
			String data = null;
			String queryToExecute = null;
			if (query.length>1) {
				data = query[1];
				if (storeVariables.get((int)  (Thread.currentThread().getId())).containsKey(data)) {
					data = storeVariables.get((int)  (Thread.currentThread().getId())).get(data);
				}
			}
			if (query[0].contains(".sql") || query[0].contains(".SQL")) {
				try(DataInputStream in = new DataInputStream(
						new FileInputStream(System.getProperty("user.dir") + "\\SQLQueries" + "\\" + query[0]));
						BufferedReader br = new BufferedReader(new InputStreamReader(in))){
					String strLine;
					StringBuilder sb = new StringBuilder();
					while ((strLine = br.readLine()) != null) {
						if (strLine.contains("{argument}")) {
							strLine = strLine.replaceAll("\\{argument}", data);
						}
						sb.append(strLine + "\n ");
					}
					queryToExecute = sb.toString();
				}
				catch (Exception e) {
					throw new FileNotFoundException("File not found");
				}
			}
			if (query[0].contains("query{")) {
				query[0] = query[0].replace("query{", "").replace("}", "").replace("{", "");
				if (query[0].contains("argument")) {
					queryToExecute = query[0].replaceAll("argument", data);
				}
			}
		return queryToExecute;
	}
	
	@SuppressWarnings("deprecation")
	private void getDataFromResultSet(ResultSet rs) throws SQLException, DatabaseExceptions {
			do {
			ResultSetMetaData metadata = rs.getMetaData();
			int columncount = metadata.getColumnCount();
			for (int i = 1; i <= columncount; i++) {
				String columnName = metadata.getColumnName(i);
				int type = metadata.getColumnType(i);
				if (type == Types.VARCHAR || type == Types.CHAR || type == Types.LONGVARCHAR) {
					if (rs.getString(columnName) != null) {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, rs.getString(columnName));
					} else {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, "");
					}

				} else if (type == Types.INTEGER || type == Types.TINYINT || type == Types.SMALLINT) {
					if (String.valueOf(rs.getInt(columnName)) != null) {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getInt(columnName)));
					} else {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, "");
					}
				}

				else if (type == Types.BOOLEAN||type == Types.BIT) {
					val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getBoolean(columnName)));
				}

				else if (type == Types.BLOB||type == Types.BINARY) {
					val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getString(columnName)));
				}
				
				else if (type == Types.OTHER||type == Types.JAVA_OBJECT||type == Types.TIME) {
					val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getByte(columnName)));
				}	
				
				else if (type == Types.DOUBLE || type == Types.FLOAT) {
					if (String.valueOf(rs.getDouble(columnName)) != null) {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getDouble(columnName)));
					} else {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, "");
					}
				}

				else if (type == Types.DECIMAL) {
					if (String.valueOf(rs.getBigDecimal(columnName)) != null) {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getBigDecimal(columnName)));
					} else {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, "");
					}
				}
				
				else if (type == Types.NULL) {
					val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getString(columnName)));
				}

				else if (type == Types.DATE || type == Types.TIMESTAMP) {
					Timestamp timestamp = rs.getTimestamp(columnName);
					if (timestamp != null) {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(timestamp.getDate()));
					}

					if (timestamp == null) {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(""));
					}

					else {
						val_Thread.get((int)  (Thread.currentThread().getId())).put(columnName, String.valueOf(rs.getDate(columnName).toString()));
					}
				}
				else {
					throw new DatabaseExceptions("Type '"+type+"' is not defined in framework for column '" + columnName +"'");
				}
			}
		} while (rs.next());
	}
	
	private void updateDatabase(String [] query) throws SQLException, IOException, DatabaseExceptions {
		String queryToExecute = null;
		try (Statement stmt = DatabaseConnection.getCon().createStatement();) {
			queryToExecute = seperateSQLQueryAndTestdata(query);
			stmt.executeUpdate(queryToExecute);
		} catch (SQLException se) {
			throw new DatabaseExceptions("Query : <br /><b>" + queryToExecute + "</b><br /> returns error message : '<b>"
					+ se.getMessage() + "</b>'");
		}
	}

	private  void getData(String[] data) throws DatabaseExceptions {
		storeVariables.get((int)(Thread.currentThread().getId())).put(data[0], val_Thread.get((int)  (Thread.currentThread().getId())).get(data[1]));
		if(data.length>1) {
			if(val_Thread.get((int)(Thread.currentThread().getId())).containsKey(data[1])) {
				storeVariables.get((int)(Thread.currentThread().getId())).put(data[0], val_Thread.get((int)(Thread.currentThread().getId())).get(data[1]));
			}
			else {
				throw new DatabaseExceptions("Column '"+data[1]+"' is not present in query");
			}
		}
		else {
			throw new DatabaseExceptions("Either variable or the column name is missing for '"+data[0]+"'");
		}
	}

	private  void getDataForDBValidation(By by, String [] testdata) throws SeleniumExceptions {
		WebElement ele = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		HashMap<String, String> dbvalues = storeDBValues.get((int)(Thread.currentThread().getId()));
		
		if (testdata.length>1) {
			if (ele.getAttribute(testdata[1]) != null) {
				dbvalues.put(testdata[0], ele.getAttribute(testdata[1]).trim());
				
			} else if (testdata[1].equalsIgnoreCase("text")) {
				dbvalues.put(testdata[0], ele.getText());
				
			} else if (testdata[1].equalsIgnoreCase("dropdownvalue")) {
				dbvalues.put(testdata[0], new Select(ele).getFirstSelectedOption().getAttribute("value"));
				
			} else if (testdata[1].equalsIgnoreCase("dropdowntext")) {
				dbvalues.put(testdata[0], new Select(ele).getFirstSelectedOption().getText());
			}
			else if (testdata[1].equalsIgnoreCase("dropdownindex")) {
				List<WebElement> list = new Select(ele).getOptions();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getText().equals(new Select(ele).getFirstSelectedOption().getText())) {
						dbvalues.put(testdata[0], String.valueOf(i));
						break;
					}
				}
			}			
			else {
				throw new SeleniumExceptions("Attribute '" + testdata[1] + "' is not valid for the element with value "+by);
			}
		}
		else {
			throw new SeleniumExceptions("Please enter the Database column to be matched or the attribute to be matched");
		}
	}

	private  WebElement waitForExpectedElement(By by) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	private  void compareWithDB() throws SeleniumExceptions {	
		if(!(val_Thread.get((int)  (Thread.currentThread().getId())).isEmpty())) {
			for (String key : storeDBValues.get((int)  (Thread.currentThread().getId())).keySet()) {
				if (val_Thread.get((int)  (Thread.currentThread().getId())).containsKey(key)
						&& val_Thread.get((int)  (Thread.currentThread().getId())).get(key).equalsIgnoreCase(storeDBValues.get((int)  (Thread.currentThread().getId())).get(key))) {

				} else if (val_Thread.get((int)  (Thread.currentThread().getId())).containsKey(key)
						&& !(val_Thread.get((int)  (Thread.currentThread().getId())).get(key).equalsIgnoreCase(storeDBValues.get((int)  (Thread.currentThread().getId())).get(key)))) {
					throw new SeleniumExceptions("Database column '"+key+"' with value '" + val_Thread.get((int)  (Thread.currentThread().getId())).get(key)
							+ "' does not matches with portal value '" + storeDBValues.get((int)  (Thread.currentThread().getId())).get(key)+"'");
				}

				else if (!(val_Thread.get((int)  (Thread.currentThread().getId())).containsKey(key))) {
					throw new SeleniumExceptions("Field '"+key+"' does not exist in DB query");
				}

			}
		}
		else {
			throw new SeleniumExceptions("empty map from DB");
		}
		
	}

	private void fileUpload(By by, String[] data){
/**		String workingDir = System.getProperty("user.dir")+File.separator+ "FileUpload" + File.separator + data[0];
		String filePath = "C:\\Vivek\\WorkSpace\\AdviserKeywordDriven_Parallel\\FileUpload" + File.separator + data[0];*/
		String filePath = FILEUPLOADPATH + File.separator + data[0];
		if (driver.toString().contains("phantom")) {
			filePath = filePath.replace("\\", "/");
			((PhantomJSDriver) getWebDriver())
					.executePhantomJS("var page = this; page.uploadFile('input[type=file]','" + filePath + "');");
		} else {
			try{
/**				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].setAttribute('value','"+workingDir+"')", ele);*/

				WebElement ele = driver.findElement(by);
				LocalFileDetector detect  = new LocalFileDetector();
				File file = detect.getLocalFile(filePath);
				((RemoteWebElement) ele).setFileDetector(detect);
				ele.sendKeys(file.getAbsolutePath());
			}
			catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}
	}
			
	
	private  void readfile(String[] data) throws IOException {
		String workingDir = System.getProperty("user.dir");
		String filePath = workingDir + "\\FileUpload" + File.separator + data[1];
		try (DataInputStream in = new DataInputStream(new FileInputStream(filePath));
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String strLine;
			StringBuilder sb = new StringBuilder();
			while ((strLine = br.readLine()) != null) {
				sb.append(strLine + "\n");
			}
			storeVariables.get((int)  (Thread.currentThread().getId())).put(data[0], sb.toString().trim());
		}
		catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
	
	private void checkIfElementIsPassed(By by, String action) throws SeleniumExceptions {
		if(by==null) {
			throw new SeleniumExceptions("Web element is not defined for Action '<b>"+action+"</b>'");
		}
	}
}
