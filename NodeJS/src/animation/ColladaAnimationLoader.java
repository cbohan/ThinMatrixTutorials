package animation;

import org.joml.Matrix4f;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;

public class ColladaAnimationLoader {
	public static Joint loadSkeleton(String fileName) {
		Document colladaDoc = getDocument(fileName);
		Element skin = getSkinElement(colladaDoc);
		String[] jointNames = getJointNames(skin);

		Element rootJoint = getRootJointElement(colladaDoc);
		Joint skeleton = createSkeleton(rootJoint, jointNames);
		
		return skeleton;
	}
	
	public static Animation loadAnimation(String fileName) {
		Document colladaDoc = getDocument(fileName);
		Element library_animations = (Element)colladaDoc.getDocumentElement().getElementsByTagName("library_animations").item(0);
		NodeList animations = library_animations.getElementsByTagName("animation");
		
		//loop through each animation node and get put the joint transform in the correct keyframe.
		Element matrixInput = findElementWithIdThatContains((Element)animations.item(0), "source", "matrix-input");
		
		Element inputFloatArray = null;
		try {
			inputFloatArray = (Element)matrixInput.getElementsByTagName("float_array").item(0);
		} catch (Exception e) {
			System.err.println("Try the following export settings:");
			System.err.println("Selection Only");
			System.err.println("Include Armatures");
			System.err.println("Include Shape Keys");
			System.err.println("Texture Options: Copy");
			System.err.println("Collada Options: Triangulate, Use Object Instances, Transformation Type Matrix");
			System.exit(-1);
		}
		
		float[] keyframeTimes = parseFloatArray(inputFloatArray.getTextContent());
		KeyFrame[] keyframes = new KeyFrame[keyframeTimes.length];
		for (int i = 0; i < keyframeTimes.length; i++)
			keyframes[i] = new KeyFrame(keyframeTimes[i]);
		
		for (int i = 0; i < animations.getLength(); i++) {
			try {
				Element currentAnimation = (Element)animations.item(i);
				Element matrixOutput = findElementWithIdThatContains(currentAnimation, "source", "matrix-output");
				Element floatArray = (Element)matrixOutput.getElementsByTagName("float_array").item(0);
				float[] matrixFloats = parseFloatArray(floatArray.getTextContent());
				String boneName = currentAnimation.getAttribute("id").split("Armature_")[1].split("_pose")[0];
				
				float[] matrixArray = new float[16];
				for (int j = 0; j < matrixFloats.length / 16; j++) {
					for (int n = 0; n < 16; n++) { matrixArray[n] = matrixFloats[j * 16 + n]; }
					Matrix4f poseMatrix = createMatrixFromFloatArray(matrixArray);
					keyframes[j].addToPose(boneName, poseMatrix);				
				}
			} catch (Exception e) {}
		}
		
		return new Animation(keyframes[keyframes.length - 1].getTimeStamp(), keyframes);
	}
	
	private static Joint createSkeleton(Element currentJoint, String[] jointNames) {
		//Create the joint.
		String jointName = currentJoint.getAttribute("id");
		int jointId = 0;
		for (int i = 0; i < jointNames.length; i++) 
			if (jointName.equals(jointNames[i])) 
				jointId = i;
		float[] matrixFloats = parseFloatArray(currentJoint.getElementsByTagName("matrix").item(0).getTextContent());
		Matrix4f localBindTransform = createMatrixFromFloatArray(matrixFloats);
		Joint joint = new Joint(jointId, jointName, localBindTransform);
		
		//Add child joints.
		NodeList childJoints = currentJoint.getChildNodes();
		for (int i = 0; i < childJoints.getLength(); i++) {
			if (childJoints.item(i).getNodeName().equals("node")) {
				Joint childJoint = createSkeleton((Element)childJoints.item(i), jointNames);
				joint.addChild(childJoint);
			}
		}
		
		return joint;
	}
	
	private static Matrix4f createMatrixFromFloatArray(float[] f) {
		Matrix4f mat = new Matrix4f();
		
		mat.m00(f[0]);
		mat.m10(f[1]);
		mat.m20(f[2]);
		mat.m30(f[3]);
		
		mat.m01(f[4]);
		mat.m11(f[5]);
		mat.m21(f[6]);
		mat.m31(f[7]);
		
		mat.m02(f[8]);
		mat.m12(f[9]);
		mat.m22(f[10]);
		mat.m32(f[11]);
		
		mat.m03(f[12]);
		mat.m13(f[13]);
		mat.m23(f[14]);
		mat.m33(f[15]);
		
		return mat;
	}
	
	private static String[] getJointNames(Element skin) {
		Element skinJoints = findElementWithIdThatContains(skin, "source", "skin-joints");
		Element nameArray = (Element)skinJoints.getElementsByTagName("Name_array").item(0);
		return nameArray.getTextContent().split(" ");
	}
	
	private static Element findElementWithIdThatContains(Element element, String nodeType, String contains) {
		NodeList sourceNodes = element.getElementsByTagName(nodeType);
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
	
	private static Element getRootJointElement(Document colladaDoc) {
		Element library_visual_scenes = (Element)colladaDoc.getDocumentElement().getElementsByTagName("library_visual_scenes").item(0);
		Element visual_scene = (Element)library_visual_scenes.getElementsByTagName("visual_scene").item(0);
		Element armatureNode = findElementWithIdThatContains(visual_scene, "node", "Armature");
		Element rootJoint = (Element)armatureNode.getElementsByTagName("node").item(0);
		return rootJoint;
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
	
	private static float[] parseFloatArray(String floats){
		String[] floatStrings = floats.split(" ");
		float[] values = new float[floatStrings.length];
		for (int i = 0; i < floatStrings.length; i++) {
			values[i] = Float.parseFloat(floatStrings[i]);
		}
		return values;
	}
}
