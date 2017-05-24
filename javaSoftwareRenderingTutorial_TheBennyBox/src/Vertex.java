/**
 * Created by Ben Loisch on 5/18/2017.
 */
public class Vertex {
    private Vector4f m_pos;
    private Vector4f m_color;

    public float GetX() { return m_pos.GetX(); }
    public float GetY() { return m_pos.GetY(); }
    public Vector4f GetPosition() { return m_pos; }
    public Vector4f GetColor() { return m_color; }

    Vertex(float x, float y, float z, float w) {
        m_pos = new Vector4f(x, y, z, w);
    }

    Vertex(float x, float y, float z) {
        m_pos = new Vector4f(x, y, z, 1);
    }

    Vertex(Vector4f pos, Vector4f color) {
        m_pos = pos;
        m_color = color;
    }

    public Vertex Transform(Matrix4f transform) {
        return new Vertex(transform.Transform(m_pos), m_color);
    }

    public Vertex PersepctiveDivide() {
        return new Vertex(new Vector4f(m_pos.GetX()/m_pos.GetW(),
                m_pos.GetY()/m_pos.GetW(),
                m_pos.GetZ()/m_pos.GetW(),
                m_pos.GetW()), m_color);
    }

    public float TriangleArea(Vertex b, Vertex c) {
        float x1 = b.GetX() - m_pos.GetX();
        float y1 = b.GetY() - m_pos.GetY();

        float x2 = c.GetX() - m_pos.GetX();
        float y2 = c.GetY() - m_pos.GetY();

        return ((x1 * y2) - (x2 * y1));
    }
}
