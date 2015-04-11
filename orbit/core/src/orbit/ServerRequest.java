package orbit;

import java.io.Serializable;

public class ServerRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	private String request;	//string needed for server to fulfill request
	private Object o;		//data needed for database to fulfill request
	
	public ServerRequest(String request, Object o){
		this.request = request;
		this.o = o;
	}
	
	public String getRequest(){
		return request;
	}
	
	public Object getObject(){
		return o;
	}
}
