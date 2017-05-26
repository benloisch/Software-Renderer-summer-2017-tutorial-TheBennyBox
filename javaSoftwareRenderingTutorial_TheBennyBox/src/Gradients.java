public class Gradients {
	//private Vector4f[] m_color;
    //private Vector4f m_colorXStep;
    //private Vector4f m_colorYStep;

    private float[] m_texCoordX;
    private float[] m_texCoordY;

    private float m_texCoordXXStep;
    private float m_texCoordXYStep;
    private float m_texCoordYXStep;
    private float m_texCoordYYStep;

    public float GetTexCoordX(int loc) { return m_texCoordX[loc]; }
    public float GetTexCoordY(int loc) { return m_texCoordY[loc]; }

    public float GetTexCoordXXStep() { return m_texCoordXXStep; }
    public float GetTexCoordXYStep() { return m_texCoordXYStep; }
    public float GetTexCoordYXStep() { return m_texCoordYXStep; }
    public float GetTexCoordYYStep() { return m_texCoordYYStep; }

    //public Vector4f GetColor(int loc) { return m_color[loc]; }
    //public Vector4f GetColorXStep() { return m_colorXStep; }
    //public Vector4f GetColorYStep() { return m_colorYStep; }

    public Gradients(Vertex minYVert, Vertex midYVert, Vertex maxYVert) {
        //m_color = new Vector4f[3];

        float oneOverdx = 1.0f /
                (((midYVert.GetPosition().GetX() - maxYVert.GetPosition().GetX()) *
                (minYVert.GetPosition().GetY() - maxYVert.GetPosition().GetY())) -
                ((minYVert.GetPosition().GetX() - maxYVert.GetPosition().GetX()) *
                (midYVert.GetPosition().GetY() - maxYVert.GetPosition().GetY())));

        float oneOverdy = -oneOverdx;

        m_texCoordX = new float[3];
        m_texCoordY = new float[3];

        m_texCoordX[0] = minYVert.GetTexCoords().GetX();
        m_texCoordX[1] = midYVert.GetTexCoords().GetX();
        m_texCoordX[2] = maxYVert.GetTexCoords().GetX();

        m_texCoordY[0] = minYVert.GetTexCoords().GetY();
        m_texCoordY[1] = midYVert.GetTexCoords().GetY();
        m_texCoordY[2] = maxYVert.GetTexCoords().GetY();

        m_texCoordXXStep =
                (((m_texCoordX[1] - m_texCoordX[2]) *
                (minYVert.GetY() - maxYVert.GetY())) -
                ((m_texCoordX[0] - m_texCoordX[2]) *
                (midYVert.GetY() - maxYVert.GetY()))) * oneOverdx;

        m_texCoordXYStep =
                (((m_texCoordX[1] - m_texCoordX[2]) *
                (minYVert.GetX() - maxYVert.GetX())) -
                ((m_texCoordX[0] - m_texCoordX[2]) *
                (midYVert.GetX() - maxYVert.GetX()))) * oneOverdy;

        m_texCoordYXStep =
                (((m_texCoordY[1] - m_texCoordY[2]) *
                (minYVert.GetY() - maxYVert.GetY())) -
                ((m_texCoordY[0] - m_texCoordY[2]) *
                (midYVert.GetY() - maxYVert.GetY()))) * oneOverdx;

        m_texCoordYYStep =
                (((m_texCoordY[1] - m_texCoordY[2]) *
                (minYVert.GetX() - maxYVert.GetX())) -
                ((m_texCoordY[0] - m_texCoordY[2]) *
                (midYVert.GetX() - maxYVert.GetX()))) * oneOverdy;

        //m_color[0] = minYVert.GetColor();
        //m_color[1] = midYVert.GetColor();
        //m_color[2] = maxYVert.GetColor();

        //m_colorXStep = (((m_color[1].Sub(m_color[2])).Mul(
        //        (minYVert.GetY() - maxYVert.GetY()))).Sub(
        //        ((m_color[0].Sub(m_color[2])).Mul((midYVert.GetY() - maxYVert.GetY()))))).Mul(oneOverdx);

        //m_colorYStep = (((m_color[1].Sub(m_color[2])).Mul(
        //        (minYVert.GetX() - maxYVert.GetX()))).Sub(
        //        ((m_color[0].Sub(m_color[2])).Mul((midYVert.GetX() - maxYVert.GetX()))))).Mul(oneOverdy);
    }
}