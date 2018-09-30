import java.io.File
import java.util.logging.Level

import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.mitm.KeyStoreFileCertificateSource
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager
import net.lightbody.bmp.proxy.CaptureType
import net.lightbody.bmp.{BrowserMobProxy, BrowserMobProxyServer}
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxDriverLogLevel, FirefoxOptions, FirefoxProfile}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

object HistoriasRNE  extends App{


  def createProxy() = {
    val existingCertificateSource =
      new KeyStoreFileCertificateSource("PKCS12", new File("clave.pfx" ), "1", "")

    // configure the MitmManager to use the custom KeyStore source
    val mitmManager = ImpersonatingMitmManager.builder()
      .rootCertificateSource(existingCertificateSource)
      .build()

    // when using LittleProxy, use the .withManInTheMiddle method on the bootstrap:
    val bootstrap = DefaultHttpProxyServer.bootstrap()
      .withManInTheMiddle(mitmManager)

    // when using BrowserMob Proxy, use .setMitmManager() on the BrowserMobProxy object:
    val proxyServer = new BrowserMobProxyServer()
    proxyServer.setMitmManager(mitmManager)

    proxyServer
  }

  def testProxy() = {
    // start the proxy
    val proxy = createProxy()
    proxy.start(0)

    // get the Selenium proxy object
    val seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

    // configure it as a desired capability
    val capabilities : DesiredCapabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

    //val options = new FirefoxOptions()
    //options.setLogLevel(FirefoxDriverLogLevel.FATAL)
    //options.setProxy(seleniumProxy)


    // start the browser up

    val options = new FirefoxOptions(capabilities)
    //val profileDir = new File("/home/alvaro/.mozilla/firefox/wn03h4uv.selenium")
    //val profile = new FirefoxProfile( profileDir)
    //options.setProfile( profile )
    //options.addArguments("-profile", profileDir.toString )
    //options.addArguments("-marionette-port", "2828")
    options.setAcceptInsecureCerts(true)
    println( "Voy a crear el driver")
    val driver  = new FirefoxDriver( options )

    println( "Driver Creeado")

    // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
    proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT);

    // create a new HAR with the label "yahoo.com"
    proxy.newHar("yahoo.com");

    // open yahoo.com
    println( "El driver irá a yahoo")
    driver.get("http://yahoo.com")
    println( "el driver ha ido a yahoo")

    // get the HAR data
    val har  = proxy.getHar()


    har.writeTo( new File("con-proxy.har") )


  }

  def testNoProxy() = {


    val options = new FirefoxOptions()
    options.setLogLevel(FirefoxDriverLogLevel.FATAL)

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

  testProxy()
}
