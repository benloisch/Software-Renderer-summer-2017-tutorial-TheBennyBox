public class Gradients {
	//private Vector4f[] m_color;
    //private Vector4f m_colorXStep;
    //private Vector4f m_colorYStep;

    private float[] m_texCoordX;
    private float[] m_texCoordY;
    private float[] m_oneOverZ;
    private float[] m_depth;
    private float[] m_lightAmt;

    private float m_texCoordXXStep;
    private float m_texCoordXYStep;
    private float m_texCoordYXStep;
    private float m_texCoordYYStep;
    private float m_oneOverZXStep;
    private float m_oneOverZYStep;
    private float m_depthXStep;
    private float m_depthYStep;
    private float m_lightAmtXStep;
    private float m_lightAmtYStep;

    public float GetTexCoordX(int loc) { return m_texCoordX[loc]; }
    public float GetTexCoordY(int loc) { return m_texCoordY[loc]; }
    public float GetOneOverZ(int loc) { return m_oneOverZ[loc]; }
    public float GetDepth(int loc) { return m_depth[loc]; }
    public float GetLightAmt(int loc) { return m_lightAmt[loc]; }

    public float GetTexCoordXXStep() { return m_texCoordXXStep; }
    public float GetTexCoordXYStep() { return m_texCoordXYStep; }
    public float GetTexCoordYXStep() { return m_texCoordYXStep; }
    public float GetTexCoordYYStep() { return m_texCoordYYStep; }

    public float GetOneOverZXStep() { return m_oneOverZXStep; }
    public float GetOneOverZYStep() { return m_oneOverZYStep; }

    public float GetDepthXStep() { return m_depthXStep; }
    public float GetDepthYStep() { return m_depthYStep; }

    public float GetLightAmtXStep() { return m_lightAmtXStep; }
    public float GetLightAmtYStep() { return m_lightAmtYStep; }

    //public Vector4f GetColor(int loc) { return m_color[loc]; }
    //public Vector4f GetColorXStep() { return m_colorXStep; }
    //public Vector4f GetColorYStep() { return m_colorYStep; }

    public float CalcXStep(float[] values, Vertex minYVert, Vertex midYVert, Vertex maxYVert, float oneOverdx) {
        return
                (((values[1] - values[2]) *
                (minYVert.GetY() - maxYVert.GetY())) -
                ((values[0] - values[2]) *
                (midYVert.GetY() - maxYVert.GetY()))) * oneOverdx;
    }

    public float CalcYStep(float[] values, Vertex minYVert, Vertex midYVert, Vertex maxYVert, float oneOverdy) {
        return
                (((values[1] - values[2]) *
                (minYVert.GetX() - maxYVert.GetX())) -
                ((values[0] - values[2]) *
                (midYVert.GetX() - maxYVert.GetX()))) * oneOverdy;
    }

    private float Saturate(float val) {
        if (val < 0.0f) {
            return 0.0f;
        }
        if (val > 1.0f) {
            return 1.0f;
        }

        return val;
    }

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
        m_oneOverZ = new float[3];
        m_depth = new float[3];
        m_lightAmt = new float[3];

        m_oneOverZ[0] = 1.0f/minYVert.GetPosition().GetW();
        m_oneOverZ[1] = 1.0f/midYVert.GetPosition().GetW();
        m_oneOverZ[2] = 1.0f/maxYVert.GetPosition().GetW();

        m_depth[0] = minYVert.GetPosition().GetZ();
        m_depth[1] = midYVert.GetPosition().GetZ();
        m_depth[2] = maxYVert.GetPosition().GetZ();

        m_texCoordX[0] = minYVert.GetTexCoords().GetX() * m_oneOverZ[0];
        m_texCoordX[1] = midYVert.GetTexCoords().GetX() * m_oneOverZ[1];
        m_texCoordX[2] = maxYVert.GetTexCoords().GetX() * m_oneOverZ[2];

        m_texCoordY[0] = minYVert.GetTexCoords().GetY() * m_oneOverZ[0];
        m_texCoordY[1] = midYVert.GetTexCoords().GetY() * m_oneOverZ[1];
        m_texCoordY[2] = maxYVert.GetTexCoords().GetY() * m_oneOverZ[2];

        Vector4f lightDir = new Vector4f(1, 0, -1);
        m_lightAmt[0] = (Saturate(minYVert.GetNormal().Dot(lightDir)) * 0.9f) + 0.1f;
        m_lightAmt[1] = (Saturate(midYVert.GetNormal().Dot(lightDir)) * 0.9f) + 0.1f;
        m_lightAmt[2] = (Saturate(maxYVert.GetNormal().Dot(lightDir)) * 0.9f) + 0.1f;

        m_texCoordXXStep = CalcXStep(m_texCoordX, minYVert, midYVert, maxYVert, oneOverdx);
        m_texCoordXYStep = CalcYStep(m_texCoordX, minYVert, midYVert, maxYVert, oneOverdy);
        m_texCoordYXStep = CalcXStep(m_texCoordY, minYVert, midYVert, maxYVert, oneOverdx);
        m_texCoordYYStep = CalcYStep(m_texCoordY, minYVert, midYVert, maxYVert, oneOverdy);

        m_oneOverZXStep = CalcXStep(m_oneOverZ, minYVert, midYVert, maxYVert, oneOverdx);
        m_oneOverZYStep = CalcYStep(m_oneOverZ, minYVert, midYVert, maxYVert, oneOverdy);

        m_depthXStep = CalcXStep(m_depth, minYVert, midYVert, maxYVert, oneOverdx);
        m_depthYStep = CalcYStep(m_depth, minYVert, midYVert, maxYVert, oneOverdy);

        m_lightAmtXStep = CalcXStep(m_lightAmt, minYVert, midYVert, maxYVert, oneOverdx);
        m_lightAmtYStep = CalcYStep(m_lightAmt, minYVert, midYVert, maxYVert, oneOverdy);

        /*
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
        */
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