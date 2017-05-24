import java.util.Vector;

/**
 * Created by Ben Loisch on 5/22/2017.
 */
public class Edge {
    private float m_x;
    private float m_xStep;
    private int m_yStart;
    private int m_yEnd;

    private Vector4f m_color;
    private Vector4f m_colorStep;

    public Vector4f GetColor() { return m_color; }
    public float GetX() { return m_x; }
    public int GetYStart() { return m_yStart; }
    public int GetYEnd() { return m_yEnd; }

    public Edge(Gradients gradients, Vertex minYVert, Vertex maxYVert, int minYVertIndex) {
        m_yStart = (int)Math.ceil(minYVert.GetY());
        m_yEnd = (int)Math.ceil(maxYVert.GetY());

        //rise and run will be floating
        float yDist = maxYVert.GetY() - minYVert.GetY();
        float xDist = maxYVert.GetX() - minYVert.GetX();

        float yPreStep = m_yStart - minYVert.GetY();

        m_xStep = (float)xDist / (float)yDist;
        m_x = minYVert.GetX() + yPreStep * m_xStep;
        float xPreStep = m_x - minYVert.GetX();

        m_color = gradients.GetColor(minYVertIndex).Add(gradients.GetColorYStep().Mul(yPreStep)).Add(gradients.GetColorXStep().Mul(xPreStep));
        m_colorStep  = gradients.GetColorYStep().Add(gradients.GetColorXStep().Mul(m_xStep));

    }

    public void Step() {
        m_x += m_xStep;
        m_color = m_color.Add(m_colorStep);
    }
}
