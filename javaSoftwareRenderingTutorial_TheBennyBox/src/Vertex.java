/**
 * Created by Ben Loisch on 5/18/2017.
 */
public class Vertex {
    private float m_x;
    private float m_y;

    public float GetX() { return m_x; }
    public float GetY() { return m_y; }

    public void SetX(float x) { m_x = x; }
    public void SetY(float y) { m_y = y; }

    Vertex(float x, float y) {
        m_x = x;
        m_y = y;
    }
}
