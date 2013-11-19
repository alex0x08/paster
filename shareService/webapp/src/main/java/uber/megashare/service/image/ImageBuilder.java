/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
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


/**
 * Builder impelementation to work with images (resize at now)
 * Works in headless mode
 * <p/>
 * Билдер для работы с изображениями. (маштабирование на данный момент)
 * Работает в headless-режиме
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
    private ImageInfo ii_instance = new ImageInfo();
    private BufferedImage source, //исходное изображение / original image
            scaled; //результат преобразований / scaled image
    private int min_width, //минимальная ширина изображения для преобразования // minimum width to start scaling
            min_height, //минимальная высота изображения // minimum height to ..
            max_height=1600,max_width=1600,
            scale_rate; // TODO: describe it
    /**
     * byte array with scaled image
     */
    private byte[] scaled_array;
    /**
     * if source image format is supported
     * this variable sets during setSource(..) call
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
            if (!ii_instance.check() || ii_instance.getWidth()>max_width || ii_instance.getHeight() > max_height ) {
                unsupported = true;
                return this;
            }
            iin.reset(); // resets stream. see upper.
            this.source = ImageIO.read(iin);
        } finally {

            try {
                iin.close();
            } catch (Exception e) {
            }

            try {
                in.close();
            } catch (Exception e) {
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
     * @return ImageInfo object which holds all information about image (size,width,height,mime..)
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
     * scale to icon (uses predefined width,height)
     * маштабировать в иконку
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
                } catch (Exception e) {
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
     * @param width  original width
     * @param height original height
     * @return array of two elements 0 - width, 1  - height
     */
    private int[] calcSizes(int width, int height) {

        int[] out = new int[2];

        out[0] = width;
        out[1] = height;

        float xx = width / scale_rate;
        float yy = height / scale_rate;
        float zz = (xx + yy) / 2;
        float max_z = (min_height + min_width) / scale_rate;

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
     * creates bufferedimage object from array of bytes
     *
     * @param bytes array of bytes contains image
     * @param w     width
     * @param h     height
     * @return bufferedimage object
     * @see BufferedImage
     */
    public static BufferedImage createImage(byte[] bytes, int w, int h) {
        int redMask = 1;
        int greenMask = 2;
        int blueMask = 3;
        int lineStride = w * 3;
        int pixelStride = 3;
     //   boolean flipped = false;

        // TODO: would it be quicker to use a type that took a byte array directly?

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[w * h];
        int pixelIndex = 0;
        int lineOffset = 0;
       /* if (flipped) {
            lineOffset = (h - 1) * lineStride;
        }*/

        for (int y = 0; y < h; ++y) {
            int off = lineOffset;
            for (int x = 0; x < w; ++x) {
                byte r = bytes[off + redMask - 1];
                byte g = bytes[off + greenMask - 1];
                byte b = bytes[off + blueMask - 1];
                int pixel = 0;
                pixel += r & 0xff;    // red
                pixel *= 256;
                pixel += g & 0xff; // green
                pixel *= 256;
                pixel += b & 0xff;    // blue
                pixels[pixelIndex++] = pixel;
                off += pixelStride;
            }
           /* if (flipped) {
                lineOffset -= lineStride;
            } else {*/
                lineOffset += lineStride;
            //}
        }

        bi.setRGB(0, 0, w, h, pixels, 0, w);
        //pixels = null;

        return bi;

    }

    /**
     * creates scaled instance of input bufferedimage
     *
     * @param source input bufferedimage
     * @param width  image with
     * @param height image height
     * @return scaled bufferedimage
     */
    public static BufferedImage getScaledInstance(BufferedImage source, int width,
                                                  int height) {
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = target.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        double scalex = (double) target.getWidth() / source.getWidth();
        double scaley = (double) target.getHeight() / source.getHeight();
        AffineTransform xform = AffineTransform.getScaleInstance(scalex, scaley);
        g2.drawRenderedImage(source, xform);
        g2.dispose();

        return target;
    }

    //@Override
    public String display() {

        return ii_instance.getFormatName() + ", " + ii_instance.getMimeType() + ", "
                + ii_instance.getWidth() + " x " + ii_instance.getHeight() + " pixels, "
                + ii_instance.getBitsPerPixel() + " bits per pixel, "
                + ii_instance.getNumberOfImages() + " image(s), "
                ;

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
