/*
 * Copyright 2017 Eduze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.eduze.fyp.web.services;

import org.eduze.fyp.api.AnalyticsEngine;
import org.eduze.fyp.api.ConfigurationManager;
import org.eduze.fyp.api.model.CameraConfig;
import org.eduze.fyp.api.model.Zone;
import org.eduze.fyp.api.resources.Camera;
import org.eduze.fyp.api.util.ImageUtils;
import org.eduze.fyp.core.db.dao.CameraConfigDAO;
import org.eduze.fyp.core.db.dao.ZoneDAO;
import org.eduze.fyp.web.resources.MapConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eduze.fyp.api.Constants.CAMERA_VIEW_HEIGHT;
import static org.eduze.fyp.api.Constants.CAMERA_VIEW_WIDTH;

public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    private ZoneDAO zoneDAO;
    private CameraConfigDAO cameraConfigDAO;

    @Autowired
    private ConfigurationManager configurationManager;

    /**
     * Obtain an ID for camera. This must be called and an ID should be obtained in order to call any other method
     *
     * @return camera ID
     */
    public Camera getCameraId() {
        int cameraId = configurationManager.getNextCameraId();
        return new Camera(cameraId);
    }

    /**
     * Adds a camera view to the configuration. The camera view submitted here will be used lataer for point
     * configuration
     *
     * @param cameraConfig {@link CameraConfig} instance to be configured
     * @throws IOException
     */
    public void addCameraConfig(CameraConfig cameraConfig) throws IOException {
        logger.debug("Adding camera configuration - {}", cameraConfig);

        // resizing camera view
        byte[] resized = ImageUtils.resize(cameraConfig.getView(), CAMERA_VIEW_WIDTH, CAMERA_VIEW_HEIGHT);
        cameraConfig.setView(resized);

        cameraConfig.getPointMapping().setCameraConfig(cameraConfig);
        CameraConfig existing = cameraConfigDAO.findByCameraId(cameraConfig.getCameraId());
        if (existing != null) {
            cameraConfigDAO.delete(existing);
            cameraConfigDAO.save(cameraConfig);
        } else {
            cameraConfigDAO.save(cameraConfig);
        }
        configurationManager.addCameraConfig(cameraConfig);
    }

    /**
     * Get the floor plan or map of the enclosed are which the {@link AnalyticsEngine} is going to cover
     *
     * @param cameraId camera ID
     * @return byte array of the map image
     */
    public MapConfiguration getMap(int cameraId) throws IOException {
        if (!configurationManager.isConfigured()) {
            return null;
        }

        BufferedImage map = configurationManager.getMap();
        byte[] mapImageBytes = ImageUtils.bufferedImageToByteArray(map);

        MapConfiguration mapConfiguration = new MapConfiguration();
        mapConfiguration.setMapImage(mapImageBytes);
        mapConfiguration.setMapping(configurationManager.getPointMapping(cameraId));
        mapConfiguration.setMapHeight(map.getHeight());
        mapConfiguration.setMapWidth(map.getWidth());

        return mapConfiguration;
    }

    public Map<String, byte[]> getMap() throws IOException {
        Map<String, byte[]> cameraViews = new HashMap<>();
        BufferedImage map = configurationManager.getMap();
        byte[] bytes = ImageUtils.bufferedImageToByteArray(map);
        cameraViews.put("mapImage", bytes);
        return cameraViews;
    }

    public Map<Integer, CameraConfig> getCameraConfigs() {
        return configurationManager.getCameraConfigs();
    }

    public Zone addZone(Zone zone) {
        return zoneDAO.save(zone);
    }

    public void updateZone(Zone updatedZone) {
        Zone zone = zoneDAO.findById(updatedZone.getId());

        if (zone == null) {
            throw new IllegalArgumentException("No zone found for Id - " + updatedZone.getId());
        }

        zone.setZoneName(updatedZone.getZoneName());
        zone.setZoneLimit(updatedZone.getZoneLimit());
        zone.setXCoordinates(updatedZone.getXCoordinates());
        zone.setYCoordinates(updatedZone.getYCoordinates());
        zoneDAO.update(zone);
    }

    public void deleteZone(int zoneId) {
        zoneDAO.delete(zoneId);
    }

    public List<Zone> getZones() {
        return zoneDAO.list();
    }

    public CameraConfig getCameraConfig(int cameraId) {
        return configurationManager.getCameraConfig(cameraId);
    }

    public void setConfigurationManager(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public void setZoneDAO(ZoneDAO zoneDAO) {
        this.zoneDAO = zoneDAO;
    }

    public void setCameraConfigDAO(CameraConfigDAO cameraConfigDAO) {
        this.cameraConfigDAO = cameraConfigDAO;
    }
}
