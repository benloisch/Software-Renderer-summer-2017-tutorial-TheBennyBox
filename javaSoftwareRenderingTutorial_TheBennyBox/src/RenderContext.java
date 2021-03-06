import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ben Loisch on 5/17/2017.
 */
public class RenderContext extends Bitmap{

    //y-length buffer that holds xMin and xMax
    //private final int m_scanBuffer[];
    private float[] m_zBuffer;

    public RenderContext(int width, int height) {
        super(width, height);
        //m_scanBuffer = new int[height * 2];
        m_zBuffer = new float[width * height];
    }

    public void DrawTriangle(Vertex v1, Vertex v2, Vertex v3, Bitmap texture) {
        if (v1.IsInsideViewFrustum() && v2.IsInsideViewFrustum() && v3.IsInsideViewFrustum()) {
            FillTriangle(v1, v2, v3, texture);
            return;
        }

        List<Vertex> vertices = new ArrayList<>();
        List<Vertex> auxillaryList = new ArrayList<>();

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);

        if (ClipPolygonAxis(vertices, auxillaryList, 0) &&
                ClipPolygonAxis(vertices, auxillaryList, 1) &&
                ClipPolygonAxis(vertices, auxillaryList, 2)) {

            Vertex initialVertex = vertices.get(0);
            for (int i = 1; i < vertices.size() - 1; i++) {
                FillTriangle(initialVertex, vertices.get(i), vertices.get(i+1), texture);
            }
        }
    }

    public void ClearDepthBuffer() {
        for (int i = 0; i < m_zBuffer.length; i++) {
            m_zBuffer[i] = Float.MAX_VALUE;
        }
    }

    private boolean ClipPolygonAxis(List<Vertex> vertices, List<Vertex> auxillaryList, int componentIndex) {
        ClipPolygonComponent(vertices, componentIndex, 1.0f, auxillaryList);
        vertices.clear();

        if (auxillaryList.isEmpty()) {
            return false;
        }

        ClipPolygonComponent(auxillaryList, componentIndex, -1.0f, vertices);
        auxillaryList.clear();

        return !vertices.isEmpty();
    }

    private void ClipPolygonComponent(List<Vertex> vertices, int componentIndex, float componentFactor, List<Vertex> result) {
        Vertex previousVertex = vertices.get(vertices.size() - 1);
        float previousComponent = previousVertex.Get(componentIndex) * componentFactor;
        boolean previousInside = previousComponent <= previousVertex.GetPosition().GetW();

        Iterator<Vertex> it = vertices.iterator();
        while (it.hasNext()) {
            Vertex currentVertex = it.next();
            float currentComponent = currentVertex.Get(componentIndex) * componentFactor;
            boolean currentInside = currentComponent <= currentVertex.GetPosition().GetW();

            if (currentInside ^ previousInside) {
                float lerpAmt = (previousVertex.GetPosition().GetW() - previousComponent) /
                        ((previousVertex.GetPosition().GetW() - previousComponent) -
                        (currentVertex.GetPosition().GetW() - currentComponent));

                result.add(previousVertex.Lerp(currentVertex, lerpAmt));
            }

            if (currentInside) {
                result.add(currentVertex);
            }

            previousVertex = currentVertex;
            previousComponent = currentComponent;
            previousInside = currentInside;
        }
    }

    //at y, xMin < drawPixels < xMax
    public void DrawScanBuffer(int yCoord, int xMin, int xMax) {
        //m_scanBuffer[yCoord * 2] = xMin;
        //m_scanBuffer[yCoord * 2 + 1] = xMax;
    }

    //yMin < DrawScanBuffer (draw horizontal line) < yMax
    public void FillShape(int yMin, int yMax) {
        for (int j = yMin; j < yMax; j++) {
            //int xMin = m_scanBuffer[j * 2];
            //int xMax = m_scanBuffer[j * 2 + 1];

            //for (int i = xMin; i < xMax; i++) {
            //    DrawPixel(i, j, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF);
            //}
        }
    }

    public void DrawMesh(Mesh mesh, Matrix4f transform, Bitmap texture) {
        for (int i = 0; i < mesh.GetNumIndices(); i += 3) {
            Matrix4f identity = new Matrix4f().InitIdentity();
            FillTriangle(mesh.GetVertex(mesh.GetIndex(i)).Transform(transform, identity),
                    mesh.GetVertex(mesh.GetIndex(i+1)).Transform(transform, identity),
                    mesh.GetVertex(mesh.GetIndex(i+2)).Transform(transform, identity),
                    texture);
        }
    }

    private void FillTriangle(Vertex v1, Vertex v2, Vertex v3, Bitmap texture) {
        Matrix4f screenSpaceTransform = new Matrix4f().InitScreenSpaceTransform(GetWidth()/2.0f, GetHeight()/2.0f);

        Matrix4f identity = new Matrix4f().InitIdentity();
        //v1,v2, and v3 are in NDC space
        //we must still divide by w (PerspectiveDivide()) and then convert to screen space
        Vertex minYVert = v1.Transform(screenSpaceTransform, identity).PersepctiveDivide();
        Vertex midYVert = v2.Transform(screenSpaceTransform, identity).PersepctiveDivide();
        Vertex maxYVert = v3.Transform(screenSpaceTransform, identity).PersepctiveDivide();

        if (minYVert.TriangleArea(maxYVert, midYVert) >= 0) {
            return;
        }

        if (maxYVert.GetY() < midYVert.GetY()) {
            Vertex temp = maxYVert;
            maxYVert = midYVert;
            midYVert = temp;
        }

        if (midYVert.GetY() < minYVert.GetY()) {
            Vertex temp = midYVert;
            midYVert = minYVert;
            minYVert = temp;
        }

        if (maxYVert.GetY() < midYVert.GetY()) {
            Vertex temp = maxYVert;
            maxYVert = midYVert;
            midYVert = temp;
        }

        ScanTriangle(minYVert, midYVert, maxYVert,
                minYVert.TriangleArea(maxYVert, midYVert) >= 0, texture);
        //float area = minYVert.TriangleArea(maxYVert, midYVert);
        //int handedness = area >= 0 ? 1 : 0; //if area >= 0, set to 1

        //fill m_scanBuffer x-values with edges created by these three vertices
        //ScanConvertTriangle(minYVert, midYVert, maxYVert, handedness);

        //draw between yMin and yMax
        //FillShape((int)Math.ceil(minYVert.GetY()), (int)Math.ceil(maxYVert.GetY()));
    }

    //handedness = 0, draw into min part of m_scanBuffer
    //handedness = 1, draw into max part of m_scanBuffer
    private void ScanTriangle(Vertex minYVert, Vertex midYVert, Vertex maxYVert, boolean handedness, Bitmap texture) {
        Gradients gradients = new Gradients(minYVert, midYVert, maxYVert);
        Edge topToBottom = new Edge(gradients, minYVert, maxYVert, 0);
        Edge topToMiddle = new Edge(gradients, minYVert, midYVert, 0);
        Edge middleToBottom = new Edge(gradients, midYVert, maxYVert, 1);

        Edge left = topToBottom;
        Edge right = topToMiddle;
        if (handedness) {
            Edge temp = left;
            left = right;
            right = temp;
        }

        int yStart = topToMiddle.GetYStart();
        int yEnd = topToMiddle.GetYEnd();

        for (int j = yStart; j < yEnd; j++) {
            DrawScanLine(gradients, left, right, j, texture);
            left.Step();
            right.Step();
        }

        left = topToBottom;
        right = middleToBottom;
        if (handedness) {
            Edge temp = left;
            left = right;
            right = temp;
        }

        yStart = middleToBottom.GetYStart();
        yEnd = middleToBottom.GetYEnd();

        for (int j = yStart; j < yEnd; j++) {
            DrawScanLine(gradients, left, right, j, texture);
            left.Step();
            right.Step();
        }
    }

    private void DrawScanLine(Gradients gradients, Edge left, Edge right, int j, Bitmap texture)  {
        int xMin = (int)Math.ceil(left.GetX());
        int xMax = (int)Math.ceil(right.GetX());
        float xPrestep = xMin - left.GetX();
        //Vector4f minColor = left.GetColor();
        //Vector4f maxColor = right.GetColor();

        //at this y-scanline, getTexCoord + amount offset by pixel center
        float xDist = right.GetX() - left.GetX();
        float texCoordXXStep = (right.GetTexCoordX() - left.GetTexCoordX())/xDist;
        float texCoordYXStep = (right.GetTexCoordY() - left.GetTexCoordY())/xDist;
        float oneOverZXStep = (right.GetOneOverZ() - left.GetOneOverZ())/xDist;
        float depthXStep = (right.GetDepth() - left.GetDepth())/xDist;
        float lightAmtXStep = gradients.GetLightAmtXStep();
        float lightAmt = left.GetLightAmt() + lightAmtXStep * xPrestep;

        //float texCoordX = left.GetTexCoordX() + gradients.GetTexCoordXXStep() * xPrestep;
        //float texCoordY = left.GetTexCoordY() + gradients.GetTexCoordYXStep() * xPrestep;

        float texCoordX = left.GetTexCoordX() + texCoordXXStep * xPrestep;
        float texCoordY = left.GetTexCoordY() + texCoordYXStep * xPrestep;
        float oneOverZ = left.GetOneOverZ() + oneOverZXStep * xPrestep;
        float depth = left.GetDepth() + depthXStep * xPrestep;

        //float lerpAmt = 0.0f;
        //float lerpStep = 1.0f / (float)(xMax - xMin);

        for (int i = xMin; i < xMax; i++) {
            //Vector4f color = minColor.Lerp(maxColor, lerpAmt);

            //byte r = (byte)(color.GetX() * 255.0f + 0.5f);
            //byte g = (byte)(color.GetY() * 255.0f + 0.5f);
            //byte b = (byte)(color.GetZ() * 255.0f + 0.5f);

            int index = i + j * GetWidth();
            if(depth < m_zBuffer[index]) {
                //DrawPixel(i, j, (byte)0xFF, b, g, r);
                m_zBuffer[index] = depth;
                float z = 1.0f / oneOverZ;
                int srcX = (int) ((texCoordX * z) * (texture.GetWidth() - 1) + 0.5f);
                int srcY = (int) ((texCoordY * z) * (texture.GetHeight() - 1) + 0.5f);

                CopyPixel(i, j, srcX, srcY, texture, lightAmt);

                try {
                    //CopyPixel(i, j, srcX, srcY, texture);
                } catch (Exception e) {
                    int stop = 0;
                }
            }

            oneOverZ += gradients.GetOneOverZXStep();
            texCoordX += gradients.GetTexCoordXXStep();
            texCoordY += gradients.GetTexCoordYXStep();
            depth += depthXStep;
            lightAmt += lightAmtXStep;
            //lerpAmt += lerpStep;
        }
    }

    //handedness = 0, draw into min part of m_scanBuffer
    //handedness = 1, draw into max part of m_scanBuffer
    public void ScanConvertTriangle(Vertex minYVert, Vertex midYVert, Vertex maxYVert, int handedness) {
        ScanConvertLine(minYVert, maxYVert, 0 + handedness);
        ScanConvertLine(minYVert, midYVert, 1 - handedness);
        ScanConvertLine(midYVert, maxYVert, 1 - handedness);
    }

    //use Bresenham's line algorithm to define edge of triangle (putting x-values in m_scanBuffer[])
    private void ScanConvertLine(Vertex minYVert, Vertex maxYVert, int whichSide) {
        int yStart = (int)Math.ceil(minYVert.GetY());
        int yEnd = (int)Math.ceil(maxYVert.GetY());
        //int xStart = (int)Math.ceil(minYVert.GetX());
        //int xEnd = (int)Math.ceil(maxYVert.GetX());

        //rise and run will be floating
        float yDist = maxYVert.GetY() - minYVert.GetY();
        float xDist = maxYVert.GetX() - minYVert.GetX();

        if (yDist <= 0)
            return;

        float xStep = (float)xDist / (float)yDist;
        float yPreStep = yStart - minYVert.GetY();
        float curX = minYVert.GetX() + yPreStep * xStep;

        for (int j = yStart; j < yEnd; j++) {
            //if whichSide = 0, write to min part of scan buffer
            //if whichSide = 1, write to max part of scan buffer
            //m_scanBuffer[j * 2 + whichSide] = (int)Math.ceil(curX);
            curX += xStep;
        }
    }
}
