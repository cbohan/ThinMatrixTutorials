package models;

import java.io.*;
import java.util.*;
import org.joml.*;

public class OBJLoader {
	private static Map<String, RawModel> objModelMap = new HashMap<String, RawModel>();
	
	public static RawModel loadOBJ(String fileName) {
		return loadOBJ(fileName, false);
	}
	
	public static RawModel loadOBJ(String fileName, boolean invertUVs) {
		if (objModelMap.containsKey(fileName))
			return objModelMap.get(fileName);
		
		long beforeTime = System.nanoTime();
		
		FileReader fr = null;
		
		try {
			fr = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Could not find file: " + fileName);
			e.printStackTrace();
			System.exit(-1);
		}
		
		BufferedReader reader = new BufferedReader(fr);
		String line;
		
		List<Vector3f> positions = new ArrayList<Vector3f>();
		List<Vector2f> uvs = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		
		float[] positionsArray = null;
		float[] normalsArray = null;
		float[] uvsArray = null;
		int[] indicesArray = null;
		
		try {
			while(true) {
				line = reader.readLine();
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")){
					Vector3f position = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					positions.add(position);
				}else if(line.startsWith("vt ")){
					Vector2f uv = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
					if (invertUVs) { uv.y = 1 - uv.y; }
					uvs.add(uv);
				}else if(line.startsWith("vn ")){
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3]));
					normals.add(normal);
				}else if(line.startsWith("f ")){
					break;
				}
			}
			
			List<Vertex> vertices = new ArrayList<Vertex>();
			
			while(line != null){
				if(line.startsWith("f ") == false){
					line = reader.readLine();
					continue;
				}
				
				String[] currentLine = line.split(" ");
				Vertex v1 = new Vertex(currentLine[1], positions, uvs, normals);
				Vertex v2 = new Vertex(currentLine[2], positions, uvs, normals);
				Vertex v3 = new Vertex(currentLine[3], positions, uvs, normals);
				
				int v1Pos = contains(vertices, v1);
				int v2Pos = contains(vertices, v2);
				int v3Pos = contains(vertices, v3);
				
				if (v1Pos != -1)
					indices.add(v1Pos);
				else {
					vertices.add(v1);
					indices.add(vertices.size() - 1);
				}
				
				if (v2Pos != -1)
					indices.add(v2Pos);
				else {
					vertices.add(v2);
					indices.add(vertices.size() - 1);
				}
				
				if (v3Pos != -1)
					indices.add(v3Pos);
				else {
					vertices.add(v3);
					indices.add(vertices.size() - 1);
				}
				
				line = reader.readLine();
			}
			
			reader.close();
			
			positionsArray = new float[vertices.size() * 3];
			normalsArray = new float[vertices.size() * 3];
			uvsArray = new float[vertices.size() * 2];
			
			for(int i = 0; i < vertices.size(); i++) {
				Vertex v = vertices.get(i);
				
				positionsArray[i * 3 + 0] = v.position.x;
				positionsArray[i * 3 + 1] = v.position.y;
				positionsArray[i * 3 + 2] = v.position.z;

				normalsArray[i * 3 + 0] = v.normal.x;
				normalsArray[i * 3 + 1] = v.normal.y;
				normalsArray[i * 3 + 2] = v.normal.z;
				
				uvsArray[i * 2 + 0] = v.uv.x;
				uvsArray[i * 2 + 1] = v.uv.y;
			}
			
			indicesArray = new int[indices.size()];
			for(int i = 0; i < indices.size(); i++)
				indicesArray[i] = indices.get(i);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		RawModel model = ModelLoader.loadToVAO(positionsArray, uvsArray, normalsArray, indicesArray);
		objModelMap.put(fileName, model);
		
		long afterTime = System.nanoTime();
		double duration = (afterTime - beforeTime) / 1000000000.0;
		System.out.println("Loaded model: " + fileName + " (" + duration + " seconds)");
		
		return model;
	}
	
	private static int contains(List<Vertex> vertices, Vertex vert) {
		for (int i = 0; i < vertices.size(); i++){
			if (vertices.get(i).equals(vert))
				return i;
		}
		
		return -1;
	}
}

class Vertex {
	public String string;
	public Vector3f position;
	public Vector2f uv;
	public Vector3f normal;
	
	public Vertex(String s, List<Vector3f> positions, List<Vector2f> uvs, List<Vector3f> normals) {
		string = s;
		String[] parts = string.split("/");
		position = positions.get(Integer.parseInt(parts[0]) - 1);
		uv = uvs.get(Integer.parseInt(parts[1]) - 1);
		normal = normals.get(Integer.parseInt(parts[2]) - 1);
	}
	
	public boolean equals(Vertex v) {
		return string.equals(v.string);
	}
}
