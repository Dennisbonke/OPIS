package mcp.mobius.opis.gui.font;

import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dennisbonke on 26-1-2015.
 */
public class TrueTypeFont {

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    private IntObject[] charArray = new IntObject[256];
    private Map customChars = new HashMap();
    private boolean antiAlias;
    private int fontSize = 0;
    private int fontHeight = 0;
    private int fontTextureID;
    private int textureWidth = 1024;
    private int textureHeight = 1024;
    private Font font;
    private FontMetrics fontMetrics;
    private int correctL = 9;
    private int correctR = 8;

    public TrueTypeFont(Font font, boolean antiAlias, char[] additionalChars)
    {
        this.font = font;
        this.fontSize = (font.getSize() + 3);
        this.antiAlias = antiAlias;

        createSet(additionalChars);
        System.out.println("TrueTypeFont loaded: " + font + " - AntiAlias = " + antiAlias);
        this.fontHeight -= 1;
        if (this.fontHeight <= 0) {
            this.fontHeight = 1;
        }
    }

    public TrueTypeFont(Font font, boolean antiAlias)
    {
        this(font, antiAlias, null);
    }

    public void setCorrection(boolean on)
    {
        if (on)
        {
            this.correctL = 2;
            this.correctR = 1;
        }
        else
        {
            this.correctL = 0;
            this.correctR = 0;
        }
    }

    private BufferedImage getFontImage(char ch)
    {
        BufferedImage tempfontImage = new BufferedImage(1, 1, 2);

        Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
        if (this.antiAlias == true) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(this.font);
        this.fontMetrics = g.getFontMetrics();
        int charwidth = this.fontMetrics.charWidth(ch) + 8;
        if (charwidth <= 0) {
            charwidth = 7;
        }
        int charheight = this.fontMetrics.getHeight() + 3;
        if (charheight <= 0) {
            charheight = this.fontSize;
        }
        BufferedImage fontImage = new BufferedImage(charwidth, charheight, 2);

        Graphics2D gt = (Graphics2D)fontImage.getGraphics();
        if (this.antiAlias == true) {
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        gt.setFont(this.font);

        gt.setColor(Color.WHITE);
        int charx = 3;
        int chary = 1;
        gt.drawString(String.valueOf(ch), charx, chary + this.fontMetrics.getAscent());


        return fontImage;
    }

    private void createSet(char[] customCharsArray)
    {
        if ((customCharsArray != null) && (customCharsArray.length > 0)) {
            this.textureWidth *= 2;
        }
        try
        {
            BufferedImage imgTemp = new BufferedImage(this.textureWidth, this.textureHeight, 2);
            Graphics2D g = (Graphics2D)imgTemp.getGraphics();

            g.setColor(new Color(0, 0, 0, 1));
            g.fillRect(0, 0, this.textureWidth, this.textureHeight);

            int rowHeight = 0;
            int positionX = 0;
            int positionY = 0;

            int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;
            for (int i = 0; i < 256 + customCharsLength; i++)
            {
                char ch = i < 256 ? (char)i : customCharsArray[(i - 256)];

                BufferedImage fontImage = getFontImage(ch);

                IntObject newIntObject = new IntObject(null);

                newIntObject.width = fontImage.getWidth();
                newIntObject.height = fontImage.getHeight();
                if (positionX + newIntObject.width >= this.textureWidth)
                {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }
                newIntObject.storedX = positionX;
                newIntObject.storedY = positionY;
                if (newIntObject.height > this.fontHeight) {
                    this.fontHeight = newIntObject.height;
                }
                if (newIntObject.height > rowHeight) {
                    rowHeight = newIntObject.height;
                }
                g.drawImage(fontImage, positionX, positionY, null);

                positionX += newIntObject.width;
                if (i < 256) {
                    this.charArray[i] = newIntObject;
                } else {
                    this.customChars.put(new Character(ch), newIntObject);
                }
                fontImage = null;
            }
            this.fontTextureID = loadImage(imgTemp);
        }
        catch (Exception e)
        {
            System.err.println("Failed to create font.");
            e.printStackTrace();
        }
    }

    private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2, float srcY2)
    {
        float DrawWidth = drawX2 - drawX;
        float DrawHeight = drawY2 - drawY;
        float TextureSrcX = srcX / this.textureWidth;
        float TextureSrcY = srcY / this.textureHeight;
        float SrcWidth = srcX2 - srcX;
        float SrcHeight = srcY2 - srcY;
        float RenderWidth = SrcWidth / this.textureWidth;
        float RenderHeight = SrcHeight / this.textureHeight;
        Tessellator t = Tessellator.field_78398_a;



        t.func_78374_a(drawX, drawY, 0.0D, TextureSrcX, TextureSrcY);



        t.func_78374_a(drawX, drawY + DrawHeight, 0.0D, TextureSrcX, TextureSrcY + RenderHeight);



        t.func_78374_a(drawX + DrawWidth, drawY + DrawHeight, 0.0D, TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);



        t.func_78374_a(drawX + DrawWidth, drawY, 0.0D, TextureSrcX + RenderWidth, TextureSrcY);
    }

    public int getWidth(String whatchars)
    {
        int totalwidth = 0;
        IntObject intObject = null;
        int currentChar = 0;
        int lastWidth = -10;
        for (int i = 0; i < whatchars.length(); i++)
        {
            currentChar = whatchars.charAt(i);
            if (currentChar < 256) {
                intObject = this.charArray[currentChar];
            } else {
                intObject = (IntObject)this.customChars.get(new Character((char)currentChar));
            }
            if (intObject != null)
            {
                totalwidth += intObject.width / 4;
                lastWidth = intObject.width;
            }
        }
        return totalwidth;
    }

    public int getHeight()
    {
        return this.fontHeight;
    }

    public int getHeight(String HeightString)
    {
        return this.fontHeight;
    }

    public int getLineHeight()
    {
        return this.fontHeight;
    }

    public void drawString(float x, float y, String whatchars, float scaleX, float scaleY, float... rgba)
    {
        if (whatchars == null) {
            whatchars = "";
        }
        drawString(x, y, whatchars, 0, whatchars.length() - 1, scaleX, scaleY, 0, rgba);
    }

    public void drawString(float x, float y, String whatchars, float scaleX, float scaleY, int format, float... rgba)
    {
        if (whatchars == null) {
            whatchars = "";
        }
        drawString(x, y, whatchars, 0, whatchars.length() - 1, scaleX, scaleY, format, rgba);
    }

    public void drawString(float x, float y, String whatchars, int startIndex, int endIndex, float scaleX, float scaleY, int format, float... rgba)
    {
        GL11.glPushMatrix();
        GL11.glScalef(scaleX, scaleY, 1.0F);

        IntObject intObject = null;



        float totalwidth = 0.0F;
        int i = startIndex;
        float startY = 0.0F;
        int d;
        int c;
        switch (format)
        {
            case 1:
                d = -1;
                c = this.correctR;
        }
        while (i < endIndex)
        {
            if (whatchars.charAt(i) == '\n') {
                startY -= this.fontHeight;
            }
            i++; continue;
            for (int l = startIndex; l <= endIndex; l++)
            {
                int charCurrent = whatchars.charAt(l);
                if (charCurrent == 10) {
                    break;
                }
                if (charCurrent < 256) {
                    intObject = this.charArray[charCurrent];
                } else {
                    intObject = (IntObject)this.customChars.get(new Character((char)charCurrent));
                }
                totalwidth += intObject.width - this.correctL;
            }
            totalwidth /= -2.0F;



            d = 1;
            c = this.correctL;
        }
        GL11.glBindTexture(3553, this.fontTextureID);
        Tessellator t = Tessellator.field_78398_a;
        t.func_78382_b();
        if (rgba.length == 4) {
            t.func_78369_a(rgba[0], rgba[1], rgba[2], rgba[3]);
        }
        while ((i >= startIndex) && (i <= endIndex))
        {
            int charCurrent = whatchars.charAt(i);
            if (charCurrent < 256) {
                intObject = this.charArray[charCurrent];
            } else {
                intObject = (IntObject)this.customChars.get(new Character((char)charCurrent));
            }
            if (intObject != null)
            {
                if (d < 0) {
                    totalwidth += (intObject.width - c) * d;
                }
                if (charCurrent == 10)
                {
                    startY -= this.fontHeight * d;
                    totalwidth = 0.0F;
                    if (format == 2)
                    {
                        for (int l = i + 1; l <= endIndex; l++)
                        {
                            charCurrent = whatchars.charAt(l);
                            if (charCurrent == 10) {
                                break;
                            }
                            if (charCurrent < 256) {
                                intObject = this.charArray[charCurrent];
                            } else {
                                intObject = (IntObject)this.customChars.get(new Character((char)charCurrent));
                            }
                            totalwidth += intObject.width - this.correctL;
                        }
                        totalwidth /= -2.0F;
                    }
                }
                else
                {
                    drawQuad(totalwidth + intObject.width + x / scaleX, startY + y / scaleY, totalwidth + x / scaleX, startY + intObject.height + y / scaleY, intObject.storedX + intObject.width, intObject.storedY + intObject.height, intObject.storedX, intObject.storedY);
                    if (d > 0) {
                        totalwidth += (intObject.width - c) * d;
                    }
                }
                i += d;
            }
        }
        t.func_78381_a();


        GL11.glPopMatrix();
    }

    public static int loadImage(BufferedImage bufferedImage)
    {
        try
        {
            short width = (short)bufferedImage.getWidth();
            short height = (short)bufferedImage.getHeight();

            int bpp = (byte)bufferedImage.getColorModel().getPixelSize();

            DataBuffer db = bufferedImage.getData().getDataBuffer();
            ByteBuffer byteBuffer;
            if ((db instanceof DataBufferInt))
            {
                int[] intI = ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
                byte[] newI = new byte[intI.length * 4];
                for (int i = 0; i < intI.length; i++)
                {
                    byte[] b = intToByteArray(intI[i]);
                    int newIndex = i * 4;

                    newI[newIndex] = b[1];
                    newI[(newIndex + 1)] = b[2];
                    newI[(newIndex + 2)] = b[3];
                    newI[(newIndex + 3)] = b[0];
                }
                byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(newI);
            }
            else
            {
                byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(((DataBufferByte)bufferedImage.getData().getDataBuffer()).getData());
            }
            byteBuffer.flip();


            int internalFormat = 32856;
            int format = 6408;
            IntBuffer textureId = BufferUtils.createIntBuffer(1);
            GL11.glGenTextures(textureId);
            GL11.glBindTexture(3553, textureId.get(0));

            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);


            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 10241, 9728);







            GL11.glTexEnvf(8960, 8704, 8448.0F);

            GLU.gluBuild2DMipmaps(3553, internalFormat, width, height, format, 5121, byteBuffer);
            return textureId.get(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
        return -1;
    }

    public static boolean isSupported(String fontname)
    {
        Font[] font = getFonts();
        for (int i = font.length - 1; i >= 0; i--) {
            if (font[i].getName().equalsIgnoreCase(fontname)) {
                return true;
            }
        }
        return false;
    }

    public static Font[] getFonts()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }

    public static byte[] intToByteArray(int value)
    {
        return new byte[] { (byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value };
    }

    public void destroy()
    {
        IntBuffer scratch = BufferUtils.createIntBuffer(1);
        scratch.put(0, this.fontTextureID);
        GL11.glBindTexture(3553, 0);
        GL11.glDeleteTextures(scratch);
    }

    private class IntObject
    {
        public int width;
        public int height;
        public int storedX;
        public int storedY;

        private IntObject() {}
    }

}
