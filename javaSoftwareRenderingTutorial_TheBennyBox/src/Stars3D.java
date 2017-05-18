/**
 * Created by Ben Loisch on 5/16/2017.
 */
public class Stars3D {
    private final float m_spread;
    private final float m_speed;

    private final float m_starX[];
    private final float m_starY[];
    private final float m_starZ[];

    public Stars3D(int numStars, float spread, float speed) {
        m_spread = spread;
        m_speed = speed;

        m_starX = new float[numStars];
        m_starY = new float[numStars];
        m_starZ = new float[numStars];

        for (int i = 0; i < m_starX.length; i++) {
            InitStar(i);
        }
    }

    //take existing star and place at random new coordinates
    //-1<x<1
    //-1<y<1
    //0<z<1
    private void InitStar(int index) {
        m_starX[index] = ((float)Math.random() - 0.5f) * 2 * m_spread;
        m_starY[index] = ((float)Math.random() - 0.5f) * 2 * m_spread;
        m_starZ[index] = ((float)Math.random() + 0.00001f) * m_spread;
    }

    public void UpdateAndRender(Bitmap target, float delta)     {
        target.Clear((byte)0x00);

        double theta = 70.0; //degrees
        theta /= 2.0;
        double tan = Math.tan(theta);

        float halfWidth = target.GetWidth() / 2.0f;
        float halfHeight = target.GetHeight() / 2.0f;
        for (int i = 0; i < m_starX.length; i++) {
            m_starZ[i] -= delta * m_speed;

            if (m_starZ[i] <= 0) {
                InitStar(i);
            }

            //    =        divide by z coord       convert to screen space
            int x = (int)((m_starX[i]/(m_starZ[i] * tan)) * halfWidth + halfWidth);
            int y = (int)((m_starY[i]/(m_starZ[i] * tan)) * halfHeight + halfHeight);

            //if outside of screen space, InitStar()
            if (x < 0 || x >= target.GetWidth() || y < 0 || y >= target.GetHeight()) {
                InitStar(i);
            } else {
                target.DrawPixel(x, y, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
            }
        }
    }
}
