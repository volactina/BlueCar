package javapkg;

import com.google.gson.Gson;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

//ws://127.0.0.1:9999/ABC_compiler/websocket
@ServerEndpoint("/websocket")
public class WebSocket {
	private static int onlineCount = 0;
	private static ConcurrentHashMap<ABC_compiler, WebSocket> map = new ConcurrentHashMap<>();

	private ABC_compiler compiler;
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException {
		this.session = session;
		compiler = new ABC_compiler(
				new ByteArrayInputStream("#define HAHAHA 1".getBytes()));
		map.put(compiler, this);
		addOnlineCount();
//		compiler.init();
//		compiler.parse();
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		map.remove(compiler);  //从set中删除
		subOnlineCount();           //在线数减1
	}

	/**
	 * 收到客户端消息后调用的方法
	 * @param message 客户端发送过来的消息
	 * @param session 可选的参数
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println("来自客户端的消息:" + message);
		compiler.ReInit(new ByteArrayInputStream(message.getBytes()));
		compiler.init();
		compiler.parse();
	}

	/**
	 * 发生错误时调用
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/**
	 * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
	 * @param message
	 * @throws IOException
	 */
	public synchronized void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
//		this.session.getAsyncRemote().sendText(message);
	}

	public static void sendMessage(ABC_compiler compiler, String msg) throws IOException {
		System.out.println("sendMessage from Server: " + msg);
		map.get(compiler).sendMessage(msg);
//		Message obj = new Gson().fromJson(msg, Message.class);
//		obj.display();
	}

	public static synchronized void addOnlineCount() {
		WebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocket.onlineCount--;
	}

}

