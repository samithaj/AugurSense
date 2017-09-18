/*
 * Copyright 2017 Eduze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to
 * whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eduze.fyp;

public class Constants {

    private Constants() { }

    /** Interval in which we ask from cameras to process a new frame */
    public static final long FRAME_PROCESSING_INTERVAL = 2000;
    /** The suffix in the URL to which we notify to start processing a new frame */
    public static final String CAMERA_NOTIFICATION_PATH_PATTERN = "/getMap/%d";
    public static final String CAMERA_NOTIFICATION_PATH = "/getMap/";

    /** Threshold to be used to determine whether two points are the same */
    public static final double DISTANCE_THRESHOLD = 50;

    /** Global map refresh interval */
    public static final long MAP_REFRESH_INTERVAL = 5;
    public static final long MAP_REFRESH_THRESHOLD = 3;

    public static class Properties {
        public static final String FLOOR_MAP_IMAGE = "org.eduze.fyp.config.map";
    }
}