import java.io.IOException;

/**
 * Created by Ben Loisch on 5/16/2017.
 */
public class Main {
    public static void main(String args[]) {
        Display display = new Display(800, 600, "Software rendering");
        RenderContext target = display.GetFrameBuffer();
        Stars3D stars = new Stars3D(4096, 64.0f, 20.0f);

        Bitmap texture = new Bitmap(32, 32);
        for (int j = 0; j < texture.GetHeight(); j++) {
            for (int i = 0; i < texture.GetWidth(); i++) {
                texture.DrawPixel(i, j,
                        (byte)(Math.random() * 255.0 + 0.5),
                        (byte)(Math.random() * 255.0 + 0.5),
                        (byte)(Math.random() * 255.0 + 0.5),
                        (byte)(Math.random() * 255.0 + 0.5));
            }
        }

        try {
            texture = new Bitmap("bricks.jpg");
        } catch (IOException e) {}

        Mesh mesh = null;
        try {
            mesh = new Mesh("monkey2.obj");
        } catch (IOException e) {};

        Vertex minYVert = new Vertex(new Vector4f(-1, -1, 0), new Vector4f(0.0f, 1.0f, 0.0f, 0.0f)); //bottom left
        Vertex midYVert = new Vertex(new Vector4f(-1, 1, 0), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f)); //top left
        Vertex maxYVert = new Vertex(new Vector4f(1, -1, 0), new Vector4f(1.0f, 1.0f, 0.0f, 0.0f)); //bottom right
        Vertex minYVert2 = new Vertex(new Vector4f(-1, 1, 0), new Vector4f(0.0f, 0.0f, 0.0f, 0.0f)); //top left
        Vertex midYVert2 = new Vertex(new Vector4f(1, 1, 0), new Vector4f(1.0f, 0.0f, 0.0f, 0.0f)); //top right
        Vertex maxYVert2 = new Vertex(new Vector4f(1, -1, 0), new Vector4f(1.0f, 1.0f, 0.0f, 0.0f)); //bottom right

        Matrix4f projection = new Matrix4f().InitPerspective((float)Math.toRadians(70.0f),
                (float)target.GetWidth()/(float) target.GetHeight(), 0.1f, 1000.0f);

        float rotCounter = 0.0f;
        long previousTime = System.nanoTime();
        while(true) {
            long currentTime = System.nanoTime();
            float delta = (float)((currentTime - previousTime) / 1000000000.0);
            previousTime = currentTime;

            rotCounter += delta;
            //Matrix4f transform = World(trans, rot, scale) * View(no cam right now) * Projection
            Matrix4f translation = new Matrix4f().InitTranslation(0.0f, 0.0f, 3.0f + (5.0f * (float)Math.sin(rotCounter)));
            Matrix4f rotation = new Matrix4f().InitRotation(0.0f, rotCounter, 0.0f);
            Matrix4f scale = new Matrix4f().InitScale(0.001f, 0.001f, 0.001f);
            Matrix4f transform = projection.Mul(translation.Mul(rotation));

            target.Clear((byte)0x00);
            target.ClearDepthBuffer();
            //target.FillTriangle(maxYVert.Transform(transform), midYVert.Transform(transform), minYVert.Transform(transform), texture);
            //target.FillTriangle(maxYVert2.Transform(transform), midYVert2.Transform(transform), minYVert2.Transform(transform), texture);

            target.DrawMesh(mesh, transform, texture);

            display.SwapBuffers();

        }
    }
}
