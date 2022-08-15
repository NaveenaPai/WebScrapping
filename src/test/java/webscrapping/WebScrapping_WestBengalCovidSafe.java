package webscrapping;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class WebScrapping_WestBengalCovidSafe {

	static WebDriver driver;
	static String url = "https://westbengal.covidsafe.in/";

	public static void main(String[] args) throws Exception {
		try {
			setup();
			
			/******* On page load only 20 hospitals are loaded. 
			 Uncomment below line to scrap data for entire list of hospitals. ******/
			//LoadAllHospitals(); //Load all the hospitals by clicking "Load next 20" button
			
			GetHospitalDetails(); //Get the required hospital details
		} catch (Exception e) {
			throw e;
		} finally {
			driver.quit();
		}
	}

	/****** Driver Initialization & Navigating to the url ******/
	public static void setup() {
		String driverPath = System.getProperty("user.dir") + "/src/main/resources/Drivers/chromedriver.exe";

		// Set the chrome driver path
		System.setProperty("webdriver.chrome.driver", driverPath);

		// Create instance of chrome driver
		driver = new ChromeDriver();

		// Invoke the url
		driver.get(url);
		driver.manage().window().maximize();
	}
/*
	private static void LoadAllHospitals() throws InterruptedException {
		WebElement btnLoad = driver.findElement(By.xpath("//button[text()='Load next 20']"));
		
		try {
			while (btnLoad.isDisplayed()) {
				// Scroll to the webelement to avoid "Element Is Not Clickable at Point" exception
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnLoad);

				// wait until scroll is complete
				Thread.sleep(1000);
				btnLoad.click();
			}
		} catch (Exception e) {
		}
	}
*/
	public static void GetHospitalDetails() throws InterruptedException {

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(3))
				.pollingEvery(Duration.ofMillis(500)).ignoring(ElementClickInterceptedException.class);

		// Wait until the Web Table with hospital details is loaded
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table")));

		List<WebElement> numberofHospitals = driver.findElements(By.xpath("//table/tbody/tr[not (@class='border-bottom')]"));

		for (int i = 1; i <= numberofHospitals.size(); i++) {
			String parentLocator = "//table/tbody/tr[" + i + "]";

			// First Column (td[1])---> Hospital Name
			WebElement hospName = driver.findElement(By.xpath(parentLocator + "//td[1]//strong"));
			System.out.println("\nName of the hospital : " + hospName.getText());

			// Second Column (td[2])---> no.of beds without Oxygen
			WebElement bedsWithoutOxygen = driver.findElement(By.xpath(parentLocator + "//td[2]//span[contains(@class,'badge')]"));
			System.out.println("Total Available Beds without Oxygen : " + bedsWithoutOxygen.getText());

			// Third Column (td[3])---> no.of beds with Oxygen
			WebElement oxygenBeds = driver.findElement(By.xpath(parentLocator + "//td[3]//span[contains(@class,'badge')]"));
			System.out.println("Total Available Beds with Oxygen : " + oxygenBeds.getText());

			// Fourth Column (td[4])---> no.of ICU beds without Ventilator
			WebElement bedsWithoutVentilator = driver.findElement(By.xpath(parentLocator + "//td[4]//span[contains(@class,'badge')]"));
			System.out.println("Total Available Beds without Ventilator : " + bedsWithoutVentilator.getText());

			// Fifth Column (td[5])---> no.of ICU beds with Ventilator
			WebElement ventilatorBeds = driver.findElement(By.xpath(parentLocator + "//td[5]//span[contains(@class,'badge')]"));
			System.out.println("Total Available Beds with Ventilator : " + ventilatorBeds.getText());

			// Click to load contact details collapsible panel
			WebElement btnContact = driver.findElement(By.xpath(parentLocator + "//td[1]/p"));

			// Scroll to the webelement to avoid "Element Is Not Clickable at Point"
			// exception
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnContact);

			// wait until scroll is complete
			Thread.sleep(1000);
			btnContact.click();

			// Fetch the contact details
			String contactDetailsLocator = parentLocator + "/parent::tbody//tr[@class='border-bottom']";

			// Phone number in second line --> (p[2])
			WebElement phone = driver.findElement(By.xpath(contactDetailsLocator + "//p[2]//span"));
			System.out.println(phone.getText());

			// Address in fourth line --> (p[4])
			WebElement address = driver.findElement(By.xpath(contactDetailsLocator + "//p[4]//span"));
			System.out.println(address.getText());

			// Close the contact details collapsible panel
			btnContact.click();
		}
	}
}
