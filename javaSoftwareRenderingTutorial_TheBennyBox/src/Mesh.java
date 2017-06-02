import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ben Loisch on 6/1/2017.
 */
public class Mesh {
    private List<Vertex> m_vertices;
    private List<Integer> m_indices;

    public Vertex GetVertex(int i) { return m_vertices.get(i); }
    public int GetIndex(int i) { return m_indices.get(i); }
    public int GetNumIndices() { return m_indices.size(); }

    public Mesh (String fileName) throws IOException {
        IndexedModel model = null;
        try {
            model = new OBJModel(fileName).ToIndexedModel();
        } catch (IOException e) {};

        m_vertices = new ArrayList<Vertex>();
        for (int i = 0; i < model.GetPositions().size(); i++) {
            m_vertices.add(new Vertex(model.GetPositions().get(i),
                                    model.GetTexCoords().get(i)));
        }

        m_indices = model.GetIndices();
    }

    public void DrawMesh(RenderContext context, Mesh mesh, Matrix4f transform, Bitmap texture) {
        for (int i = 0; i < mesh.GetNumIndices(); i += 3) {
            context.DrawTriangle(mesh.GetVertex(mesh.GetIndex(i)).Transform(transform),
                    mesh.GetVertex(mesh.GetIndex(i+1)).Transform(transform),
                    mesh.GetVertex(mesh.GetIndex(i+2)).Transform(transform),
                    texture);
        }
    }
}
