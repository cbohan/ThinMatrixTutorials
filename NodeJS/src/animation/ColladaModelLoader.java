package animation;

import org.xml.sax.*;

import models.ModelLoader;
import models.RawModel;

import org.joml.*;
import org.joml.Math;
import org.w3c.dom.*;

import java.util.*;

import javax.xml.parsers.*;

public class ColladaModelLoader {
	public static RawModel load(String fileName) {
		Document colladaDoc = getDocument(fileName);
		Element mesh = getMeshElement(colladaDoc);
		Element skin = getSkinElement(colladaDoc);
		
		//model info
		float[] positions = getPositionsArray(mesh);
		float[] normals = getNormalsArray(mesh);
		float[] uvs = getUVsArray(mesh);
		
		//skinning info
		float[] weights = getWeightsArray(skin);
		int[] vcount = getVCountArray(skin);
		int[] v = getVArray(skin);
		JointWeights[] jointWeights = createJointWeightsArray(weights, vcount, v);
				
		return createRawModel(mesh, positions, normals, uvs, jointWeights);
	}
	
	private static RawModel createRawModel(Element mesh, float[] positions, float[] normals, float[] uvs, JointWeights[] jointIdWeights) {
		Element polyList = (Element)mesh.getElementsByTagName("polylist").item(0);
		int numInputs = polyList.getElementsByTagName("input").getLength();
		Element p = (Element)polyList.getElementsByTagName("p").item(0);
		int[] vertsList = parseIntArray(p.getTextContent());
		
		int currentVertId = 0;
		Map<String, Vertex> vertices = new HashMap<String, Vertex>();
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < vertsList.length / numInputs; i++) {
			int positionIndex = vertsList[i * numInputs + 0];
			int normalIndex = vertsList[i * numInputs + 1];
			int uvsIndex = vertsList[i * numInputs + 2];
			String key = positionIndex + " " + normalIndex + " " + uvsIndex;
			
			if (vertices.containsKey(key)) {
				indices.add(vertices.get(key).id);
			} else {
				Vector3f position = new Vector3f(positions[positionIndex*3 + 0], 
						positions[positionIndex*3 + 1], positions[positionIndex*3 + 2]);
				Vector3f normal = new Vector3f(normals[normalIndex*3 + 0], 
						normals[normalIndex*3 + 1], normals[normalIndex*3 + 2]);
				Vector2f uv = new Vector2f(uvs[uvsIndex*2 + 0], 1 - uvs[uvsIndex*2 + 1]);
				
				indices.add(currentVertId);
				Vertex vert = new Vertex(currentVertId++, positionIndex, position, normal, uv);
				vertices.put(key, vert);
			}
		}
		
		float[] modelPositions = new float[vertices.size() * 3];
		float[] modelNormals = new float[vertices.size() * 3];
		float[] modelUVs = new float[vertices.size() * 2];
		int[] jointIds = new int[vertices.size() * 3];
		float[] jointWeights = new float[vertices.size() * 3];
		int[] modelIndices = new int[indices.size()];
				
		for (Vertex v : vertices.values()) {			
			modelPositions[v.id*3 + 0] = v.position.x;
			modelPositions[v.id*3 + 1] = v.position.y;
			modelPositions[v.id*3 + 2] = v.position.z;

			modelNormals[v.id*3 + 0] = v.normal.x;
			modelNormals[v.id*3 + 1] = v.normal.y;
			modelNormals[v.id*3 + 2] = v.normal.z;

			modelUVs[v.id*2 + 0] = v.uvs.x;
			modelUVs[v.id*2 + 1] = v.uvs.y;
			
			jointIds[v.id*3 + 0] = jointIdWeights[v.positionIndex].joint1;
			jointIds[v.id*3 + 1] = jointIdWeights[v.positionIndex].joint2;
			jointIds[v.id*3 + 2] = jointIdWeights[v.positionIndex].joint3;
			
			jointWeights[v.id*3 + 0] = jointIdWeights[v.positionIndex].weight1;
			jointWeights[v.id*3 + 1] = jointIdWeights[v.positionIndex].weight2;
			jointWeights[v.id*3 + 2] = jointIdWeights[v.positionIndex].weight3;
		}
		
		for (int i = 0; i < indices.size(); i++)
			modelIndices[i] = indices.get(i);

		return ModelLoader.loadAnimatedModelToVAO(modelPositions, modelUVs, modelNormals, jointIds, jointWeights, modelIndices);
	}
	
	private static JointWeights[] createJointWeightsArray(float[] weights, int[] vcount, int[] v) {
		JointWeights[] jointWeights = new JointWeights[vcount.length];
		
		int vPos = 0;
		for (int i = 0; i < vcount.length; i++) {
			jointWeights[i] = new JointWeights();
			for (int j = 0; j < vcount[i]; j++) {
				int joint = v[vPos*2 + 0];
				float weight = weights[v[vPos*2 + 1]];
				
				jointWeights[i].addJointWeight(joint, weight);
				
				vPos++;
			}
		}
		
		return jointWeights;
	}
	
	private static int[] getVCountArray(Element skin) {
		Element vertexWeights = (Element)skin.getElementsByTagName("vertex_weights").item(0);
		Element vCount = (Element)vertexWeights.getElementsByTagName("vcount").item(0);
		return parseIntArray(vCount.getTextContent());
	}
	
	private static int[] getVArray(Element skin) {
		Element vertexWeights = (Element)skin.getElementsByTagName("vertex_weights").item(0);
		Element v = (Element)vertexWeights.getElementsByTagName("v").item(0);
		return parseIntArray(v.getTextContent());
	}
	
	private static float[] getWeightsArray(Element skin) {
		Element weightsElement = findElementWithIdThatContains(skin, "skin-weights");
		Element floatArray = (Element)weightsElement.getElementsByTagName("float_array").item(0);
		return parseFloatArray(floatArray.getTextContent());
	} 
	
	private static float[] getPositionsArray(Element mesh) {
		Element positionsElement = findElementWithIdThatContains(mesh, "position");
		Element floatArray = (Element)positionsElement.getElementsByTagName("float_array").item(0);
		return parseFloatArray(floatArray.getTextContent());
	}
	
	private static float[] getNormalsArray(Element mesh) {
		Element positionsElement = findElementWithIdThatContains(mesh, "normals");
		Element floatArray = (Element)positionsElement.getElementsByTagName("float_array").item(0);
		return parseFloatArray(floatArray.getTextContent());
	}
	
	private static float[] getUVsArray(Element mesh) {
		try {
			Element positionsElement = findElementWithIdThatContains(mesh, "map");
			Element floatArray = (Element)positionsElement.getElementsByTagName("float_array").item(0);
			return parseFloatArray(floatArray.getTextContent());
		} catch(Exception e) {
			System.err.println("You probably forgot to add UVs dummy.");
			System.exit(-1);
			return null;
		}
	}
	
	private static float[] parseFloatArray(String floats){
		String[] floatStrings = floats.split(" ");
		float[] values = new float[floatStrings.length];
		for (int i = 0; i < floatStrings.length; i++) {
			values[i] = Float.parseFloat(floatStrings[i]);
		}
		return values;
	}
	
	private static int[] parseIntArray(String ints){
		String[] intStrings = ints.split(" ");
		int[] values = new int[intStrings.length];
		for (int i = 0; i < intStrings.length; i++) {
			values[i] = Integer.parseInt(intStrings[i]);
		}
		return values;
	}
	
	private static Element findElementWithIdThatContains(Element element, String contains) {
		NodeList sourceNodes = element.getElementsByTagName("source");
		for (int i = 0; i < sourceNodes.getLength(); i++) {
			Node sourceNode = sourceNodes.item(i);
			NamedNodeMap attributes = sourceNode.getAttributes();
			for (int j = 0; j < attributes.getLength(); j++) {
				Node attribute = attributes.item(j);
				if (attribute.getNodeValue().contains(contains) && attribute.getNodeName().equals("id"))
					return (Element)sourceNode;
			}
		}
		
		return null;
	}
	
	private static Element getMeshElement(Document colladaDoc) {
		//geometry data will be under COLLADA -> library_geometries -> geometry -> mesh
		Element library_geometries = (Element)colladaDoc.getDocumentElement().getElementsByTagName("library_geometries").item(0);
		Element geometry = (Element)library_geometries.getElementsByTagName("geometry").item(0);
		Element mesh = (Element)geometry.getElementsByTagName("mesh").item(0);
		return mesh;
	}
	
	private static Element getSkinElement(Document colladaDoc) {
		Element library_controllers = (Element)colladaDoc.getDocumentElement().getElementsByTagName("library_controllers").item(0);
		Element controller = (Element)library_controllers.getElementsByTagName("controller").item(0);
		Element skin = (Element)controller.getElementsByTagName("skin").item(0);
		return skin;
	}
	
	private static Document getDocument(String fileName) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			return builder.parse(new InputSource(fileName));
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		
		return null;
	}

}

class JointWeights {
	public int joint1, joint2, joint3;
	public float weight1, weight2, weight3;
		
	public JointWeights() {
		this.joint1 = 0;
		this.joint2 = 0;
		this.joint3 = 0;
		
		this.weight1 = 0;
		this.weight2 = 0;
		this.weight3 = 0;
	}
	
	public void addJointWeight(int joint, float weight) {
		float lowestWeight = weight1;
		int lowestJoint = 1;
		
		if (weight2 < lowestWeight) {
			lowestWeight = weight2;
			lowestJoint = 2;
		}
		
		if (weight3 < lowestWeight) {
			lowestWeight = weight3;
			lowestJoint = 3;
		}
		
		if (weight > lowestWeight) {
			if (lowestJoint == 1) {
				joint1 = joint;
				weight1 = weight;
			} else if (lowestJoint == 2) {
				joint2 = joint;
				weight2 = weight;
			} else {
				joint3 = joint;
				weight3 = weight;
			}
		}
	}
	
	public void normalize() {
		float length = (float) Math.sqrt((weight1 * weight1) + (weight2 * weight2) + (weight3 * weight3));
		
		if (length == 0) {
			weight1 = weight2 = weight3 = 1f/3f;
			return;
		}
		
		weight1 = weight1 / length;
		weight2 = weight2 / length;
		weight3 = weight3 / length;
	}
}

class Vertex {
	public int id;
	public int positionIndex;
	public Vector3f position;
	public Vector3f normal;
	public Vector2f uvs;
	
	public Vertex(int i, int pi, Vector3f pos, Vector3f norm, Vector2f uv) {
		id = i;
		positionIndex = pi;
		position = pos;
		normal = norm;
		uvs = uv;
	}
}
