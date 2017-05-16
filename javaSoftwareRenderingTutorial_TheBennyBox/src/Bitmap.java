import java.util.Arrays;

/**
 * Created by Ben Loisch on 5/16/2017.
 */
public class Bitmap {
    private final int m_width;
    private final int m_height;
    private final char m_components[];

    public Bitmap(int width, int height) {
        m_width      = width;
        m_height     = height;
        m_components = new char[width * height * 4];
    }

    public void Clear(char shade) {
        Arrays.fill(m_components, shade);
    }

    public void DrawPixel(int x, int y, char a, char r, char g, char b) {
        int index               = (x + (y * m_width)) * 4; //mul by 4 because 4 components per pixel
        m_components[index    ] = a;
        m_components[index + 1] = r;
        m_components[index + 2] = g;
        m_components[index + 3] = b;
    }

    public void CopyToIntArray(int[] dest) {
        for (int i = 0; i < m_width * m_height; i++) {
            int a = ((int)m_components[(i * 4)]) << 24;
            int r = ((int)m_components[(i * 4) + 1]) << 16;
            int g = ((int)m_components[(i * 4) + 2]) << 8;
            int b = ((int)m_components[(i * 4) + 3]);

            dest[i] = a | r | g | b;
        }
    }
}
