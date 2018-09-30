import java.util.logging.Level

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

object HistoriasRNE  extends App{

  def testProxy() = {
    // start the proxy
    val proxy : BrowserMobProxy = new BrowserMobProxyServer()
    proxy.start(0);

    // get the Selenium proxy object
    val seleniumProxy : Proxy = ClientUtil.createSeleniumProxy(proxy);

    // configure it as a desired capability
    val capabilities : DesiredCapabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

    // start the browser up
    val driver : WebDriver  = new FirefoxDriver(capabilities);

    // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT);

    // create a new HAR with the label "yahoo.com"
    proxy.newHar("yahoo.com");

    // open yahoo.com
    driver.get("http://yahoo.com");

    // get the HAR data
    val har : Har = proxy.getHar();
  }

  def testNoProxy() = {


    val options = new FirefoxOptions()
    options.setLogLevel(Level.OFF)

    // Create a new instance of the Firefox driver
    // Notice that the remainder of the code relies on the interface,
    // not the implementation.
    val driver : WebDriver = new FirefoxDriver(options)

    // And now use this to visit Google
    driver.get("http://www.google.com")
    // Alternatively the same thing can be done like this
    // driver.navigate().to("http://www.google.com")

    // Find the text input element by its name
    val element : WebElement = driver.findElement(By.name("q"))

    // Enter something to search for
    element.sendKeys("Cheese!")

    // Now submit the form. WebDriver will find the form for us from the element
    element.submit()

    // Check the title of the page
    System.out.println("Page title is: " + driver.getTitle())
    
    // Google's search is rendered dynamically with JavaScript.
    // Wait for the page to load, timeout after 10 seconds
    val wait = new WebDriverWait(driver,10)
    wait.until{ d =>
      d.getTitle().toLowerCase().startsWith("cheese!")
    }

    // Should see: "cheese! - Google Search"
    println("Page title is: " + driver.getTitle())
    
    //Close the browser
    driver.quit()
  }

  test();
}
