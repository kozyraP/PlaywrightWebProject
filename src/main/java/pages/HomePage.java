package pages;

import static base.PlaywrightFactory.takeScreenshot;
import static utils.ExtentReporter.extentLog;
import static utils.ExtentReporter.extentLogWithScreenshot;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class HomePage {

	private final Page page;
	private final ExtentTest extentTest;

	private static final String ADD_TO_CART = "text='Add to Cart'";
	private static final String ALERT = "div.alert";
	private static final String LOGIN_LINK = "a:text('Login')";
	private static final String MY_ACCOUNT_LINK = "a[title='My Account']";
	private static final String PRODUCT_CAPTION = ".caption h4 a";
	private static final String PRODUCT_SEARCH_RESULT = "div.product-thumb";
	private static final String SEARCH = "input[name='search']";
	private static final String SEARCH_ICON = "div#search button";
	private static final String SEARCH_PAGE_HEADER = "div#content h1";

	public HomePage(Page page, ExtentTest extentTest) {
		this.page = page;
		this.extentTest = extentTest;
	}

	public String getHomePageTitle() {
		page.waitForLoadState();
		return page.title();
	}

	public boolean searchProduct(String productName) {
		page.fill(SEARCH, productName);
		page.click(SEARCH_ICON);
		String header = page.textContent(SEARCH_PAGE_HEADER);
		extentLog(extentTest, Status.PASS, "Search of '" + header + "' Product is successful");
		if (page.locator(PRODUCT_SEARCH_RESULT).count() > 0) {
			extentLog(extentTest, Status.PASS, "Search of '" + productName + "' Product is successful");
			return true;
		}
		extentLogWithScreenshot(extentTest, Status.INFO, "No Product is available for the search '" + productName + "'",
				takeScreenshot(page));
		return false;
	}

	public String addProductToCart() {
		Locator productLocator = page.locator(PRODUCT_SEARCH_RESULT).nth(0);
		productLocator.locator(ADD_TO_CART).click();
		String product = productLocator.locator(PRODUCT_CAPTION).textContent();
		if (page.textContent(ALERT).contains("You have added " + product + " to your shopping cart!")) {
			extentLogWithScreenshot(extentTest, Status.PASS, "The '" + product + "' product is added to the cart.",
					takeScreenshot(page));
			return product;
		}
		extentLog(extentTest, Status.FAIL, "Unable to add the product to the cart");
		return null;
	}

	public LoginPage navigateToLoginPage() {
		page.click(MY_ACCOUNT_LINK);
		page.click(LOGIN_LINK);
		return new LoginPage(page, extentTest);
	}
}
