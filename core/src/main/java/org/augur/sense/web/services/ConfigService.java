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
package org.augur.sense.web.services;

import org.augur.sense.api.AnalyticsEngine;
import org.augur.sense.api.ConfigurationManager;
import org.augur.sense.api.Constants;
import org.augur.sense.api.model.CameraConfig;
import org.augur.sense.api.model.CameraGroup;
import org.augur.sense.api.model.Zone;
import org.augur.sense.api.util.Args;
import org.augur.sense.api.util.ImageUtils;
import org.augur.sense.core.db.dao.CameraConfigDAO;
import org.augur.sense.core.db.dao.ZoneDAO;
import org.augur.sense.api.AnalyticsEngine;
import org.augur.sense.api.ConfigurationManager;
import org.augur.sense.api.Constants;
import org.augur.sense.api.model.CameraConfig;
import org.augur.sense.api.model.CameraGroup;
import org.augur.sense.api.model.Zone;
import org.augur.sense.api.resources.Camera;
import org.augur.sense.api.util.Args;
import org.augur.sense.api.util.ImageUtils;
import org.augur.sense.core.db.dao.CameraConfigDAO;
import org.augur.sense.core.db.dao.ZoneDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @return camera id
     * @throws IOException
     */
    public int addCameraConfig(CameraConfig cameraConfig) throws IOException {
        logger.debug("Adding camera configuration - {}", cameraConfig);

        // resizing camera view
        byte[] resized = ImageUtils.resize(cameraConfig.getView(), Constants.CAMERA_VIEW_WIDTH, Constants.CAMERA_VIEW_HEIGHT);
        cameraConfig.setView(resized);

        cameraConfig.setId(0);
        cameraConfig.getPointMapping().setCameraConfig(cameraConfig);
        CameraConfig existing = cameraConfigDAO.findByCameraIpAndPort(cameraConfig.getIpAndPort());
        if (existing != null) {
            logger.warn("Found existing camera config {}. Deleting", existing);

            cameraConfig.setCameraId(existing.getCameraId());

            if (cameraConfig.getPointMapping() == null
                    || cameraConfig.getPointMapping().getScreenSpacePoints().size() == 0
                    || cameraConfig.getPointMapping().getWorldSpacePoints().size() == 0) {
                cameraConfig.setPointMapping(existing.getPointMapping().clone());
                cameraConfig.getPointMapping().setCameraConfig(cameraConfig);
            }

            if (cameraConfig.getCameraGroup() == null) {
                cameraConfig.setCameraGroup(existing.getCameraGroup());
            }

            cameraConfigDAO.delete(existing);
            cameraConfigDAO.save(cameraConfig);
        } else {
            cameraConfig.setCameraId(configurationManager.getNextCameraId());
            cameraConfigDAO.save(cameraConfig);
            logger.debug("Added camera configuration: {}", cameraConfig);
        }

        configurationManager.addCameraConfig(cameraConfig);
        return cameraConfig.getCameraId();
    }

    /**
     * Get the floor plan or map of the enclosed are which the {@link AnalyticsEngine} is going to cover
     *
     * @param cameraId camera ID
     * @return byte array of the map image
     */
    public CameraConfig getCameraConfig(int cameraId) {
        if (!configurationManager.isConfigured()) {
            return null;
        }

        CameraConfig cameraConfig = configurationManager.getCameraConfig(cameraId);
        cameraConfig.getPointMapping().getScreenSpacePoints().forEach(p -> {
            p.setX(p.getX() * cameraConfig.getWidth() / Constants.CAMERA_VIEW_WIDTH);
            p.setY(p.getY() * cameraConfig.getHeight() / Constants.CAMERA_VIEW_HEIGHT);
        });

        logger.debug("Get camera config: {}", cameraConfig);
        return cameraConfig;
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

    /**
     * Get the available camera groups
     *
     * @return groups
     */
    public List<CameraGroup> getCameraGroups() {
        return cameraConfigDAO.cameraGroups();
    }

    /**
     * Adds a new camera group
     *
     * @param cameraGroup camera group
     */
    public void addCameraGroup(CameraGroup cameraGroup) {
        Args.notNull(cameraGroup.getMap(), "Map");
        Args.notNull(cameraGroup.getName(), "Name");

        logger.debug("Adding camera group: {}", cameraGroup);

        try {
            byte[] resized = ImageUtils.resize(cameraGroup.getMap(),
                    Constants.MAP_IMAGE_WIDTH, Constants.MAP_IMAGE_HEIGHT);
            cameraGroup.setMap(resized);
        } catch (IOException e) {
            logger.error("Unable to resize image for camera group: {}", cameraGroup, e);
        }
        cameraConfigDAO.addCameraGroup(cameraGroup);
    }

    /**
     * get the zones of a particular camera group
     *
     * @param cameraGroupId camera group id
     * @return zones of that camera group
     */
    public List<Zone> getCameraGroupZones(int cameraGroupId) {
        try {
            CameraGroup cameraGroup = cameraConfigDAO.findCameraGroupById(cameraGroupId);
            return cameraGroup.getZones();
        } catch (Exception e) {
            logger.error("Error when getting zones for camera group - {} : {}", cameraGroupId, e.getMessage());
        }
        return Collections.emptyList();
    }

    public Zone addZone(Zone zone) {
        return zoneDAO.save(zone);
    }

    public void updateZone(Zone updatedZone) {
        Zone zone = zoneDAO.findById(updatedZone.getId());

        if (zone == null) {
            logger.warn("No zone found for updating: {}", updatedZone);
            throw new IllegalArgumentException("No zone found for Id - " + updatedZone.getId());
        }

        zone.setZoneName(updatedZone.getZoneName());
        zone.setZoneLimit(updatedZone.getZoneLimit());
        zone.setXCoordinates(updatedZone.getXCoordinates());
        zone.setYCoordinates(updatedZone.getYCoordinates());
        try {
            zoneDAO.update(zone);
        } catch (Exception e) {
            logger.error("Error when updating zone: {} -> {}", zone, e.getMessage());
        }
    }

    public void deleteZone(int zoneId) {
        zoneDAO.delete(zoneId);
    }

    public List<Zone> getZones() {
        return zoneDAO.list();
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
