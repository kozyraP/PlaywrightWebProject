package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pages.HomePage;
import testUtils.BaseTest;

public class AddProductToCartTests extends BaseTest {

    @BeforeClass
    public void setupBeforeClass() {
        extentTest = reporter.createTest("Test Suite - Verify Add Product to Cart", "Verify Add Products to Cart");
    }

    @Test
    public void searchProductWhichNotExistsTest() {
        testNode = extentTest.createNode("TC_05 Verify Product search which not exists");
        testNode.assignCategory("Test Suite - Verify Add Product to Cart");
        homePage = new HomePage(page, testNode);
        Assert.assertFalse(homePage.searchProduct("InvalidProduct"));
    }

    @Test
    public void searchAndAddProductToCartWithoutLoginTest() {
        testNode = extentTest.createNode("TC_06 Verify Search And Add Product to Cart Without Login");
        testNode.assignCategory("Test Suite - Verify Add Product to Cart");
        homePage = new HomePage(page, testNode);
        Assert.assertTrue(homePage.searchProduct("Macbook"));
        Assert.assertNotNull(homePage.addProductToCart());
    }

    @Test
    public void searchAndAddProductToCartWithLoginTest() {
        testNode = extentTest.createNode("TC_07 Verify Search And Add Product to Cart With Login");
        testNode.assignCategory("Test Suite - Verify Add Product to Cart");
        homePage = new HomePage(page, testNode);
        loginPage = homePage.navigateToLoginPage();
        Assert.assertTrue(loginPage.doLogin(testProperties.getProperty("username"),
                testProperties.getProperty("password")));
        Assert.assertTrue(homePage.searchProduct("iPhone"));
        Assert.assertNotNull(homePage.addProductToCart());
    }
}
