/* Packet Headers:
 * 0: client -> server - position update
 * 1: server -> client - position update
 * 2: client -> server - request id
 * 3: server -> client - respond id
 */

package networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

import renderEngine.Window;

public class SocketManager {
	private final int IN_BUFFER_SIZE = 4096;
	
	private Socket socket = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	private byte[] inBuffer;
	private ByteBuffer byteBuffer;
	private Map<Integer, NetTransform> players;
	private int ourId = -1;
	private long startTime;
	
	public SocketManager(String ip, int port) {
		try {
			this.socket = new Socket(ip, port);
			this.outputStream = this.socket.getOutputStream();
			this.inputStream = this.socket.getInputStream();
			this.inBuffer = new byte[IN_BUFFER_SIZE];
			this.byteBuffer = ByteBuffer.allocate(IN_BUFFER_SIZE);
			this.players = new HashMap<Integer, NetTransform>();
			this.startTime = Window.getCurrentTime();
			requestId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void requestId() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(2);
		buffer.flip();
		
		try {
			this.outputStream.write(buffer.array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendPosition(float x, float y, float z) {
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.putInt(0);
		buffer.putInt((int)(Window.getCurrentTime() - startTime));
		buffer.putFloat(x);
		buffer.putFloat(y);
		buffer.putFloat(z);
		buffer.flip();
				
		try {
			this.outputStream.write(buffer.array());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readData() {
		try {
			while (this.inputStream.available() != 0) {
				this.inputStream.read(this.inBuffer);
				byteBuffer.put(this.inBuffer);
				byteBuffer.flip();
				
				int packetType = byteBuffer.getInt(0);
								
				if (packetType == 1) { //Position updates.
					int packetLength = byteBuffer.getInt(4);
					
					NetTransform[] transforms = getPlayerNetTransforms();
					for(int i = 0; i < transforms.length; i++)
						transforms[i].setAlive(false);
					
					int currentPosition = 8;
					for (int i = 0; i < packetLength; i++) {
						int id = byteBuffer.getInt(currentPosition);
						currentPosition += 4;
						int time = byteBuffer.getInt(currentPosition);
						currentPosition += 4;
						float x = byteBuffer.getFloat(currentPosition);
						currentPosition += 4;
						float y = byteBuffer.getFloat(currentPosition);
						currentPosition += 4;
						float z = byteBuffer.getFloat(currentPosition);
						currentPosition += 4;
						
						if(this.players.containsKey(id) == false) 
							this.players.put(id, new NetTransform(id));
						this.players.get(id).setPosition(x, y, z, time);
						this.players.get(id).setAlive(true);
					}
					
					//Kill off all the player's that we didn't get an update for.
					transforms = getPlayerNetTransforms();
					for(int i = 0; i < transforms.length; i++) {
						if (transforms[i].getAlive() == false) 
							this.players.remove(transforms[i].getID());
					}
				}else if(packetType == 3){
					ourId = byteBuffer.getInt(4);;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public NetTransform[] getPlayerNetTransforms() {
		return this.players.values().toArray(new NetTransform[0]);
	}
	
	public int getOurId() { return ourId; }
	
	public void closeConnection() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
