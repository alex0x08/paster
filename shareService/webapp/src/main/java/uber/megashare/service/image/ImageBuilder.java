/**
 * Copyright (C) 2011 Alex <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.service.image;

import uber.megashare.base.LoggedClass;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import org.imgscalr.Scalr;

/**
 * Builder impelementation to work with images (resize at now) Works in headless
 * mode
 * <p/>
 * Билдер для работы с изображениями. (маштабирование на данный момент) Работает
 * в headless-режиме
 *
 * @author alex
 * @see ImageInfo
 * @since 3.0
 */
public class ImageBuilder extends LoggedClass {

    /**
     *
     */
    private static final long serialVersionUID = 436371342840138085L;
    /**
     * Support class used for type recognition.
     */
    private final ImageInfo ii_instance = new ImageInfo();
    private BufferedImage source, //исходное изображение / original image
            scaled; //результат преобразований / scaled image
    private int min_width, //минимальная ширина изображения для преобразования // minimum width to start scaling
            min_height, //минимальная высота изображения // minimum height to ..
            max_height = 3000, max_width = 3000,
            scale_rate; // TODO: describe it
    /**
     * byte array with scaled image
     */
    private byte[] scaled_array;
    /**
     * if source image format is supported this variable sets during
     * setSource(..) call
     */
    private boolean unsupported;

    /**
     * creates new default builder with no source attached
     */
    protected ImageBuilder() {
    }

    /**
     * builder function to set source
     *
     * @param source array of bytes
     * @return self
     * @throws IOException
     */
    public ImageBuilder setSource(byte[] source) throws IOException {

        setSource(new ByteArrayInputStream(source));
        return this;
    }

    /**
     * builder function to set source
     *
     * @param in inputstream
     * @return self
     * @throws IOException
     */
    public ImageBuilder setSource(InputStream in) throws IOException {

        if (in == null) {
            getLogger().warn("WARN Input stream is null!");
            return this;
        }

        BufferedInputStream iin = new BufferedInputStream(in);
        iin.mark(Integer.MAX_VALUE);// prepare to reset stream. see lower. 
        //this is dirty hack to avoid creating new stream
        try {
            ii_instance.setInput(iin);
            if (!ii_instance.check() || ii_instance.getWidth() > max_width || ii_instance.getHeight() > max_height) {
                unsupported = true;
                return this;
            }
            iin.reset(); // resets stream. see upper.
            this.source = ImageIO.read(iin);
        } finally {

            try {
                iin.close();
            } catch (IOException e) {
            }

            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return this;
    }

    /**
     * builder function to set source
     *
     * @param source bufferedimage
     * @return self
     */
    public ImageBuilder setSource(BufferedImage source) {
        this.source = source;
        return this;
    }

    /**
     * @return if source image was detected and supported
     */
    public boolean isUnsupported() {
        return unsupported;
    }

    /**
     * @return ImageInfo object which holds all information about image
     * (size,width,height,mime..)
     */
    public ImageInfo getImageInfo() {
        return ii_instance;
    }

    /**
     * @return current miminum source image width to start scaling
     */
    public int getMinimumWidth() {
        return min_width;
    }

    /**
     * @return current miminum source image height to start scaling
     */
    public int getMinimumHeight() {
        return min_height;
    }

    /**
     * builder function to set minimum width of source image to start scaling
     *
     * @param width minimum width
     * @return self
     */
    public ImageBuilder setMinimumWidth(int width) {
        min_width = width;
        return this;
    }

    /**
     * builder function to set minimum height of source image to start scaling
     *
     * @param height mimimum height
     * @return self
     */
    public ImageBuilder setMinimumHeight(int height) {
        min_height = height;
        return this;
    }

    public ImageBuilder setScaleRate(int rate) {
        scale_rate = rate;
        return this;
    }

    public int getScaleRate() {
        return scale_rate;
    }

    /**
     * scale to icon (uses predefined width,height) маштабировать в иконку
     *
     * @return self
     * @throws IOException
     */
    public ImageBuilder scaleToIcon() throws IOException {

        min_height = 16;
        min_width = 16;

        //    min_height=64;
        //  min_width=64;
        scale_rate = 2;

        scale();

        return this;
    }

    /**
     * scale to profile (preview) size
     *
     * @return self
     * @throws IOException
     */
    public ImageBuilder scaleToProfile() throws IOException {

        min_height = 64;
        min_width = 64;
        scale_rate = 2;

        scale();

        return this;
    }

    /**
     * scale image
     *
     * @return self
     * @throws IOException
     */
    public ImageBuilder scale() throws IOException {

        if (min_width >= ii_instance.getWidth() && min_height >= ii_instance.getHeight()) {
            scaled = source;

        } else {

            int[] sz = calcSizes(ii_instance.getWidth(), ii_instance.getHeight());
            scaled = getScaledInstance(source, sz[0], sz[1]);
        }

        ByteArrayOutputStream out = null;
        try {

            out = new ByteArrayOutputStream();
            ImageIO.write(scaled, "png", out);
            scaled_array = out.toByteArray();

        } finally {

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return this;
    }

    public InputStream getScaledAsStream() {

        return new ByteArrayInputStream(scaled_array);
    }

    public byte[] getScaledAsBytes() {

        return scaled_array;
    }

    public BufferedImage getSource() {
        return this.source;
    }

    public BufferedImage getScaled() {
        return this.scaled;
    }

    /**
     * calculate new width and height to scale to
     *
     * @param width original width
     * @param height original height
     * @return array of two elements 0 - width, 1 - height
     */
    private int[] calcSizes(int width, int height) {

        int[] out = new int[2];

        out[0] = width;
        out[1] = height;

        float xx = width / scale_rate,
                yy = height / scale_rate,
                zz = (xx + yy) / 2,
                max_z = (min_height + min_width) / scale_rate;

        if (zz > max_z) {
            float coeff = max_z / zz;
            out[0] = Math.round(xx * coeff);
            out[1] = Math.round(yy * coeff);
        }
        return out;
    }

    public static ImageBuilder createInstance() {
        return new ImageBuilder();
    }

    

    /**
     * creates scaled instance of input bufferedimage
     *
     * @param source input bufferedimage
     * @param width image with
     * @param height image height
     * @return scaled bufferedimage
     */
    public static BufferedImage getScaledInstance(BufferedImage source, int width,
            int height) {

        return Scalr.resize(source, Scalr.Mode.AUTOMATIC, width, height);

    }

    //@Override
    public String display() {

        return ii_instance.getFormatName() + ", " + ii_instance.getMimeType() + ", "
                + ii_instance.getWidth() + " x " + ii_instance.getHeight() + " pixels, "
                + ii_instance.getBitsPerPixel() + " bits per pixel, "
                + ii_instance.getNumberOfImages() + " image(s), ";

    }

    public static ImageBuilder getInstance() {
        return new ImageBuilder();
    }

    public static ImageBuilder getInstanceFor(InputStream stream) throws IOException {
        return new ImageBuilder().setSource(stream);
    }

    public static ImageBuilder getInstanceFor(byte[] bytes) throws IOException {
        return new ImageBuilder().setSource(bytes);
    }

    public static ImageBuilder getInstanceFor(BufferedImage image) throws IOException {
        return new ImageBuilder().setSource(image);
    }

}
