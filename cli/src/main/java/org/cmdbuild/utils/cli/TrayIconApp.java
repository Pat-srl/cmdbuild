/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TrayIconApp {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String url;
	private BufferedImage currentImage;
	private ServiceStatus serviceStatus = ServiceStatus.UNKNOWN;
	private TrayIcon trayIcon;
	private Dimension size;
	private Map<ServiceStatus, BufferedImage> images;

	public TrayIconApp(String cmdbuildUrl) {
		this.url = checkNotNull(cmdbuildUrl);
	}

	public void runAndJoin() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(() -> {
			try {
				checkArgument(SystemTray.isSupported(), "system tray is not supported");

				SystemTray tray = SystemTray.getSystemTray();
				size = tray.getTrayIconSize();

				images = Maps.newEnumMap(ServiceStatus.class);
				images.put(ServiceStatus.UNKNOWN, buildIconImage(Color.GRAY));
				images.put(ServiceStatus.DOWN, buildIconImage(Color.RED));
				images.put(ServiceStatus.BUSY, buildIconImage(Color.ORANGE));
				images.put(ServiceStatus.UP, buildIconImage(Color.GREEN));

				currentImage = images.get(serviceStatus);

				trayIcon = new TrayIcon(currentImage);
				trayIcon.setToolTip(url);
				tray.add(trayIcon);

			} catch (AWTException ex) {
				throw new RuntimeException(ex);
			}
		});
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(() -> {
			ServiceStatus newStatus;
			try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(1000).setSocketTimeout(1000).build()).build()) {
				HttpGet request = new HttpGet(url + "/services/rest/v2/utils/test");
				HttpResponse response = httpClient.execute(request);
				String responseContent = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
				logger.info("response = {}", responseContent);
				if (response.getStatusLine().getStatusCode() == 200) {
					newStatus = ServiceStatus.UP;
				} else {
					newStatus = ServiceStatus.BUSY;
				}
			} catch (Exception ex) {
				logger.trace("check error", ex);
				logger.info("check error: {}", ex.toString());
				newStatus = ServiceStatus.DOWN; //TODO handle different exceptions
			}
			serviceStatus = newStatus;
			SwingUtilities.invokeLater(() -> {
				updateIconImage();
			});
		}, 1, 2, TimeUnit.SECONDS);
	}

	enum ServiceStatus {
		UNKNOWN, DOWN, BUSY, UP
	}

	private void updateIconImage() {
		BufferedImage newImage = images.get(serviceStatus);
		if (newImage != currentImage) {
			currentImage = newImage;
			trayIcon.setImage(currentImage);
		}
	}

	private BufferedImage buildIconImage(Color color) {
		logger.info("set icon image for service status = {}", serviceStatus);
//            BufferedImage image = gc.createCompatibleImage(w, h);
//		BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
//		BufferedImage myImage = iconImage == null ? new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB) : iconImage;
		BufferedImage myImage = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(size.width, size.height);
		Graphics graphics = myImage.getGraphics();
		graphics.setColor(color);
//		graphics.setColor(ImmutableMap.of(ServiceStatus.UNKNOWN, Color.GRAY, ServiceStatus.DOWN, Color.RED, ServiceStatus.BUSY, Color.ORANGE, ServiceStatus.UP, Color.GREEN).get(serviceStatus));
		graphics.fillRect(0, 0, size.width, size.height);
		return myImage;
	}

}
