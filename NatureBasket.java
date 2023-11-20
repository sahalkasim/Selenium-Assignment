package org.example.SeleniumAssignment;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NatureBasket {

    public static void main(String[] args) throws InterruptedException, IOException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        // Set the path to the ChromeDriver executable


        // Create a new instance of the ChromeDriver with configured options
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Set page load strategy
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver,Duration.ofSeconds(10));

        // Navigate to the given URL
        driver.get("https://www.naturesbasket.co.in/");

        // Close pop-up window if it exists
        closePopupIfPresent(driver);

        // Click on the state dropdown and select Kolkata
        WebElement stateDropdown = driver.findElement(By.tagName("select"));
        stateDropdown.click();
        Select stateSelect = new Select(stateDropdown);
        stateSelect.selectByVisibleText("Kolkata");
        WebElement popupAccept = driver.findElement(By.id("btnPinOk"));
        popupAccept.click();

        // Mouse hover over the specified field
        WebElement hoverField = driver.findElement(By.id("ctl00_txtMasterSearch1"));
        Actions actions = new Actions(driver);
        actions.moveToElement(hoverField).click().build().perform();

        List<WebElement> dropdown = driver.findElements(By.xpath("//ul[@id=\"ctl00_NonPanIndia\"]/li/a"));
        for (WebElement element:dropdown
        ) {
            if(element.getText().equalsIgnoreCase("Chocolates")){

                actions.click(element).build().perform();
                break;
            }
        }

        // Add any two items of chocolates to Kart using JavaScript Executor
        addChocolatesToCart(driver,wait);

        // Click on any chocolate visible and go to the detailed view
        // Click on any of the visible chocolates and go to the detailed view
        driver.findElement(By.xpath("//img[@title='Ferrero Rocher Gift Pack 200G (16 Pc)']")).click();

        // Get the name of the chocolate heading
        String chocolateName = driver.findElement(By.xpath("//h1[text()=\"Ferrero Rocher Gift Pack 200G (16 Pc)\"]")).getText();

        // Go back to the previous page
        driver.navigate().back();

        // Scroll to the last part of the current page (possible once as it will load infinitely) and close any popup that comes if present.
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        try{ driver.findElement(By.id("btnClosePopUpBox")).click(); }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        // Enter dummy email ID
        WebElement emailField=driver.findElement(By.xpath("//input[@id=\"ctl00_txtNewletter\"]"));
        wait.until(ExpectedConditions.visibilityOf(emailField));
        emailField.sendKeys("dummyemailid@example.com");


        //Clear + paste chocolate name to the email field
        actions.keyDown(Keys.CONTROL).sendKeys("a").sendKeys(Keys.BACK_SPACE).keyUp(Keys.CONTROL).sendKeys(chocolateName).build().perform();
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", emailField);


        // Take screenshot
        String timestamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        String screenshotName = chocolateName + "_" + timestamp + ".png";
        TakesScreenshot screenshot=(TakesScreenshot) driver;
        File srcF = screenshot.getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(srcF,new File ("./Screenshot/"+screenshotName+".png"));

        // Close the browser window
        driver.quit();
    }

    private static void closePopupIfPresent(WebDriver driver) {
        try {
            WebElement popupCloseButton = driver.findElement(By.id("btnPinCancel"));
            if (popupCloseButton.isDisplayed()) {
                popupCloseButton.click();
            }
        } catch (Exception e) {
            // Popup not present, continue with the next steps
        }
    }

    private static void addChocolatesToCart(WebDriver driver,WebDriverWait wait) throws InterruptedException {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        WebElement addChocolate1 = driver.findElement(By.xpath("//div[contains(@class,'pro-id_7358')]//div//div[contains(@class,'search_AddCart1')]"));
        jsExecutor.executeScript("arguments[0].click()",addChocolate1);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("txt")));
        jsExecutor.executeScript("document.querySelector(\"#txt\").value='700001'");
        WebElement submitButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnAddPin")));
        jsExecutor.executeScript("arguments[0].click();",submitButton);
        WebElement addOk= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@onclick=\"RapidOK()\"]")));
        jsExecutor.executeScript("arguments[0].click();",addOk);
        WebElement addChocolate2= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'pro-id_1236')]//div//div[contains(@class,'search_AddCart1')]")));
        jsExecutor.executeScript("arguments[0].click();",addChocolate2);
    }
}

