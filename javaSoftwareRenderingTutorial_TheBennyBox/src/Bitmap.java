import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by Ben Loisch on 5/16/2017.
 */
public class Bitmap {
    //width of image in pixels
    private final int m_width;
    //height of image in pixels
    private final int m_height;
    //every pixel component in the image
    private final byte m_components[];

    public Bitmap(int width, int height) {
        m_width      = width;
        m_height     = height;
        m_components = new byte[width * height * 4];
    }

    public int GetWidth() {
        return m_width;
    }

    public int GetHeight() {
        return m_height;
    }

    public void Clear(byte shade) {
        Arrays.fill(m_components, shade);
    }

    public void DrawPixel(int x, int y, byte a, byte b, byte g, byte r) {
        int index               = (x + (y * m_width)) * 4; //mul by 4 because 4 components per pixel
        m_components[index    ] = a;
        m_components[index + 1] = b;
        m_components[index + 2] = g;
        m_components[index + 3] = r;
    }

    public void CopyToByteArray(byte[] dest) {
        for (int i = 0; i < m_width * m_height; i++) {
            dest[i * 3]     = m_components[i * 4 + 1];
            dest[i * 3 + 1] = m_components[i * 4 + 2];
            dest[i * 3 + 2] = m_components[i * 4 + 3];
        }
    }

    /*
    public void CopyToIntArray(int[] dest) {
        for (int i = 0; i < m_width * m_height; i++) {
            int a = ((int)m_components[(i * 4)]) << 24;
            int r = ((int)m_components[(i * 4) + 1]) << 16;
            int g = ((int)m_components[(i * 4) + 2]) << 8;
            int b = ((int)m_components[(i * 4) + 3]);

            dest[i] = a | r | g | b;
        }
    }
    */
}