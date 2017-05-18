/**
 * Created by Ben Loisch on 5/17/2017.
 */
public class RenderContext extends Bitmap{

    private final int m_scanBuffer[];

    public RenderContext(int width, int height) {
        super(width, height);
        m_scanBuffer = new int[height * 2];
    }

    //at y, xMin < drawPixels < xMax
    public void DrawScanBuffer(int yCoord, int xMin, int xMax) {
        m_scanBuffer[yCoord * 2] = xMin;
        m_scanBuffer[yCoord * 2 + 1] = xMax;
    }

    //yMin < DrawScanBuffer (draw a line) < yMax
    public void FillShape(int yMin, int yMax) {
        for (int j = yMin; j < yMax; j++) {
            int xMin = m_scanBuffer[j * 2];
            int xMax = m_scanBuffer[j * 2 + 1];

            for (int i = xMin; i < xMax; i++) {
                DrawPixel(i, j, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
            }
        }
    }
}
