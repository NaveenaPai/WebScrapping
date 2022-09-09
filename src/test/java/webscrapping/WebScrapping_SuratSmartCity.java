package webscrapping;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

public class WebScrapping_SuratSmartCity {

	static WebDriver driver;
	static String url = "http://office.suratsmartcity.com/SuratCOVID19/Home/COVID19BedAvailabilitydetails";
	static String zoneOption = "Central Zone"; // All Zones,West Zone,Central Zone,North Zone,East Zone - A,South Zone,
												// South West Zone,South East Zone,East Zone - B,South Zone-B

	public static void main(String[] args) throws InterruptedException {
		setup();
		SelectZone();
		GetHospitalDetails();
		driver.quit();
	}

	/****** Driver Initialization & Navigating to the website url ******/
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

	private static void SelectZone() {
		WebElement zoneDropDown = driver.findElement(By.id("ddlZone"));
		Select zones = new Select(zoneDropDown);
		zones.selectByVisibleText(zoneOption);
	}

	public static void GetHospitalDetails() throws InterruptedException {

		List<WebElement> hospitalList = driver.findElements(By.cssSelector(".card.custom-card"));
		String hospitalName, totalBeds, vacantBeds, contactNumber, contactAddress;
		String[] beds;
		 Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
			       .withTimeout(Duration.ofSeconds(5))
			       .pollingEvery(Duration.ofMillis(500));
			      // .ignoring(NoSuchElementException.class);
		
		for (int i = 1; i <= hospitalList.size(); i++) {

			System.out.println("\nZone : " + zoneOption);

			String parentLocator = "//div[@class='card custom-card'][" + i + "]";

			// When the contact modal pop up closes, allow time for main page controls to load
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(parentLocator + "//a")));

			hospitalName = driver.findElement(By.xpath(parentLocator + "//a")).getText();

			System.out.println("Name of the hospital : " + hospitalName.replace("Contact", ""));

			/***** Search for additional hospital details relative to current hospital ****/

			// Total Beds
			totalBeds = driver.findElement(By.xpath(parentLocator + "//span[@class='count-text']")).getText();
			beds = totalBeds.split(" - ");
			System.out.println("Total Beds           :" + beds[1]);

			// Total Available Beds
			vacantBeds = driver.findElement(By.xpath(parentLocator + "//span[contains(@class,'pr-2')]")).getText();
			beds = vacantBeds.split(" - ");
			System.out.println("Total available Beds :" + beds[1]);

			// Open the collapsible panel with hospital bed details
			driver.findElement(By.xpath(parentLocator)).click();
			String collapsiblePanel = "//div[@id='collapseOne-" + i + "']";

			// Introduce a wait time for the collapsible panel to appear
			 wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(collapsiblePanel+ "//div[text()='HDU(O2)']/following-sibling::div")));

			// O2 Beds
			WebElement o2Beds = driver.findElement(By.xpath(collapsiblePanel + "//div[text()='HDU(O2)']/following-sibling::div"));
			System.out.println("O2 Beds availibility :" + o2Beds.getText());

			// Ventialtor(s) availability
			WebElement ventillators = driver.findElement(By.xpath(collapsiblePanel + "//div[text()='Ventilator']/following-sibling::div"));
			System.out.println("Ventialtor(s) availability :" + ventillators.getText());

			// Contact Details

			// Click on the hospital name to open the contact details pop up
			driver.findElement(By.xpath(parentLocator + "//a")).click();

			// Wait for the modal pop up to open before fetching value
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("modal-body")));
		
			contactNumber = driver.findElement(By.id("lblhosCno")).getText();
			contactAddress = driver.findElement(By.id("lblhosaddress")).getText();
			System.out.println("Contact Number     :" + contactNumber);
			System.out.println("Contact Address    :" + contactAddress);

			// Close the pop up
			driver.findElement(By.className("close")).click();

		}

	}

}
