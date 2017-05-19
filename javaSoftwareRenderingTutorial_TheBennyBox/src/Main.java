/**
 * Created by Ben Loisch on 5/16/2017.
 */
public class Main {
    public static void main(String args[]) {
        Display display = new Display(800, 600, "Software rendering");
        RenderContext target = display.GetFrameBuffer();
        Stars3D stars = new Stars3D(4096, 64.0f, 20.0f);

        Vertex minYVert = new Vertex(100, 100);
        Vertex midYVert = new Vertex(150, 0);
        Vertex maxYVert = new Vertex(80, 300);

        long previousTime = System.nanoTime();

        while(true) {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime) / 1000000000.0);
            previousTime = currentTime;

            //stars.UpdateAndRender(target, delta);
            target.Clear((byte)0x00);

            //fill in xMin and xMax values
            /*
            for (int j = 100; j < 200; j++) {
                target.DrawScanBuffer(j, 300 - j, 300 + j);
            } */

            //fill m_scanBuffer x-values with edges created by these three vertices
            //target.ScanConvertTriangle(minYVert, midYVert, maxYVert, 0);

            //draw between yMin and yMax
            //target.FillShape(100, 300);

            target.FillTriangle(maxYVert, midYVert, minYVert);

            display.SwapBuffers();
        }
    }
}
