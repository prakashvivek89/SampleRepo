package helper;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import com.assertthat.selenium_shutterbug.utils.web.Browser;
import com.assertthat.selenium_shutterbug.utils.web.UnableTakeSnapshotException;
import javax.imageio.ImageIO;

/**
 * Class contains the method to take webpage screenshot on each test failure
 * */

public class TakeScreenshotUtility extends WebdriverHelper{
	public static final String RELATIVE_COORDS_JS = "js/relative-element-coords.js";
	public static final String MAX_DOC_WIDTH_JS = "js/max-document-width.js";
	public static final String MAX_DOC_HEIGHT_JS = "js/max-document-height.js";
	public static final String VIEWPORT_HEIGHT_JS = "js/viewport-height.js";
	public static final String VIEWPORT_WIDTH_JS = "js/viewport-width.js";
	public static final String SCROLL_TO_JS = "js/scroll-to.js";
	public static final String SCROLL_INTO_VIEW_JS = "js/scroll-element-into-view.js";
	public static final String CURRENT_SCROLL_Y_JS = "js/get-current-scrollY.js";
	public static final String CURRENT_SCROLL_X_JS = "js/get-current-scrollX.js";
	public static final String DEVICE_PIXEL_RATIO = "js/get-device-pixel-ratio.js";
	private static int docHeight = -1;
	private static int docWidth = -1;
	private static int viewportWidth = -1;
	private static int viewportHeight = -1;
	private static Double devicePixelRatio = 1.0;
	private static final Logger LOG = LoggerFactory
			.getLogger(TakeScreenshotUtility.class);

	public static String captureScreenshot(WebDriver driver, String screenshotName) {
		try {
			ImageIO.write(image(), "PNG", new File(
					Launcher.currDir + File.separator + "Screenshots" + File.separator + screenshotName + ".png"));

		} catch (Exception e) {
			LOG.error("Exception while taking screenshot " + e.getMessage());
		}
		return new File(Launcher.currDir + File.separator + "Screenshots" + File.separator + screenshotName + ".png")
				.getAbsolutePath();
	}

	public static BufferedImage image() throws InterruptedException {
		final int _docWidth = getDocWidth();
		final int _docHeight = getDocHeight();
		BufferedImage combinedImage = new BufferedImage(_docWidth, _docHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = combinedImage.createGraphics();
		int _viewportWidth = getViewportWidth();
		int _viewportHeight = getViewportHeight();
		final int scrollBarMaxWidth = 40;

		if (_viewportWidth < _docWidth
				|| (_viewportHeight < _docHeight && _viewportWidth - scrollBarMaxWidth < _docWidth))
			_viewportHeight -= scrollBarMaxWidth; // some space for a scrollbar
		if (_viewportHeight < _docHeight)
			_viewportWidth -= scrollBarMaxWidth; // some space for a scrollbar

		int horizontalIterations = (int) Math.ceil(((double) _docWidth) / _viewportWidth);
		int verticalIterations = (int) Math.ceil(((double) _docHeight) / _viewportHeight);
		outer_loop: for (int j = 0; j < verticalIterations; j++) {
			scrollTo(0, j * _viewportHeight);
			for (int i = 0; i < horizontalIterations; i++) {
				scrollTo(i * _viewportWidth, _viewportHeight * j);
				Image image = takeScreenshot();
				g.drawImage(image, getCurrentScrollX(), getCurrentScrollY(), null);
				if (_docWidth == image.getWidth(null) && _docHeight == image.getHeight(null)) {
					break outer_loop;
				}
			}
		}
		g.dispose();
		return combinedImage;
	}

	public static BufferedImage takeScreenshot() {
		File srcFile = ((TakesScreenshot) getUnderlyingDriver()).getScreenshotAs(OutputType.FILE);
		try {
			return ImageIO.read(srcFile);
		} catch (IOException e) {
			throw new UnableTakeSnapshotException(e);
		} finally {
			if (srcFile.exists()) {
				srcFile.delete();
			}
		}

	}

	public static WebDriver getUnderlyingDriver() {
		return getWebDriver();
	}

	public static int getCurrentScrollX() {
		return (int) (((Long) executeJsScript(Browser.CURRENT_SCROLL_X_JS)) * devicePixelRatio);
	}

	public static int getCurrentScrollY() {
		return (int) (((Long) executeJsScript(Browser.CURRENT_SCROLL_Y_JS)) * devicePixelRatio);
	}

	public static int getDocWidth() {
		return docWidth != -1 ? docWidth : (int) (((Long) executeJsScript(MAX_DOC_WIDTH_JS)) * devicePixelRatio);
	}

	public static int getDocHeight() {
		return docHeight != -1 ? docHeight : (int) (((Long) executeJsScript(MAX_DOC_HEIGHT_JS)) * devicePixelRatio);
	}

	public static int getViewportWidth() {
		return viewportWidth != -1 ? viewportWidth
				: (int) (((Long) executeJsScript(VIEWPORT_WIDTH_JS)) * devicePixelRatio);
	}

	public static int getViewportHeight() {
		return viewportHeight != -1 ? viewportHeight
				: (int) (((Long) executeJsScript(VIEWPORT_HEIGHT_JS)).intValue() * devicePixelRatio);
	}

	public static void scrollTo(int x, int y) {
		executeJsScript(SCROLL_TO_JS, x / devicePixelRatio, y / devicePixelRatio);
	}

	public static Object executeJsScript(String filePath, Object... arg) {
		String script = FileUtil.getJsScript(filePath);
		JavascriptExecutor js = (JavascriptExecutor) getWebDriver();
		return js.executeScript(script, arg);
	}
}
