import java.util.Vector;

/**
 * Created by Ben Loisch on 5/22/2017.
 */
public class Edge {
    private float m_x;
    private float m_xStep;
    private int m_yStart;
    private int m_yEnd;

    //private Vector4f m_color;
    //private Vector4f m_colorStep;

    private float m_texCoordX;
    private float m_texCoordXStep;
    private float m_texCoordY;
    private float m_texCoordYStep;

    private float m_oneOverZ;
    private float m_oneOverZStep;

    public float GetTexCoordX() { return m_texCoordX; }
    public float GetTexCoordY() { return m_texCoordY; }
    public float GetX() { return m_x; }
    public int GetYStart() { return m_yStart; }
    public int GetYEnd() { return m_yEnd; }
    public float GetOneOverZ() { return m_oneOverZ; }

    public Edge(Gradients gradients, Vertex minYVert, Vertex maxYVert, int minYVertIndex) {
        m_yStart = (int)Math.ceil(minYVert.GetY());
        m_yEnd = (int)Math.ceil(maxYVert.GetY());

        //rise and run will be floating
        float yDist = maxYVert.GetY() - minYVert.GetY();
        float xDist = maxYVert.GetX() - minYVert.GetX();

        //yPreStep = nearest Scanline - actual y position of vertex
        float yPreStep = m_yStart - minYVert.GetY();

        m_xStep = (float)xDist / (float)yDist;
        m_x = minYVert.GetX() + yPreStep * m_xStep;
        float xPreStep = m_x - minYVert.GetX();

        //apply prestep of x and y to pinpoint start of gradient on the exact pixel center of nearest scanline
        //m_color = gradients.GetColor(minYVertIndex).Add(gradients.GetColorYStep().Mul(yPreStep)).Add(gradients.GetColorXStep().Mul(xPreStep));

        //add (full ColorYStep and m_xStep * ColorXStep) for every step along the edge
        //m_colorStep  = gradients.GetColorYStep().Add(gradients.GetColorXStep().Mul(m_xStep));

        m_texCoordX = gradients.GetTexCoordX(minYVertIndex) +
                gradients.GetTexCoordXXStep() * xPreStep +
                gradients.GetTexCoordXYStep() * yPreStep;

        //stepping down y axis by 1, and x axis by XXStep * slope of line
        m_texCoordXStep = gradients.GetTexCoordXYStep() + gradients.GetTexCoordXXStep() * m_xStep;

        m_texCoordY = gradients.GetTexCoordY(minYVertIndex) +
                gradients.GetTexCoordYXStep() * xPreStep +
                gradients.GetTexCoordYYStep() * yPreStep;

        //stepping down y axis by 1, and x axis by YXStep * slope of line
        m_texCoordYStep = gradients.GetTexCoordYYStep() + gradients.GetTexCoordYXStep() * m_xStep;

        m_oneOverZ = gradients.GetOneOverZ(minYVertIndex) +
                gradients.GetOneOverZXStep() * xPreStep +
                gradients.GetOneOverZYStep() * yPreStep;
        m_oneOverZStep = gradients.GetOneOverZYStep() + gradients.GetOneOverZXStep() * m_xStep;

    }

    public void Step() {
        m_x += m_xStep;
        m_texCoordX += m_texCoordXStep;
        m_texCoordY += m_texCoordYStep;
        m_oneOverZ += m_oneOverZStep;
    }
}
