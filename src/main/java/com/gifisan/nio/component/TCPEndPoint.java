package com.gifisan.nio.component;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.gifisan.nio.component.protocol.ProtocolDecoder;
import com.gifisan.nio.component.protocol.ProtocolEncoder;
import com.gifisan.nio.component.protocol.future.IOReadFuture;
import com.gifisan.nio.component.protocol.future.IOWriteFuture;

//FIXME 有的连接会断掉，但是没有执行session close，这些连接莫名其妙断掉的，
//发送消息后服务端收不到，也不会回复
//改进心跳机制，服务端客户端均要发出心跳
//客户端心跳：task&request超时即断开链接
//服务端心跳：session-manager监测上次收到回报时间，长时间没有交互则发出心跳包，
//下次循环时检测是否收到心跳
public interface TCPEndPoint extends EndPoint {

	public abstract void setCurrentWriter(IOWriteFuture writer);

	public abstract IOWriteFuture getCurrentWriter();

	public abstract boolean isOpened();

	public abstract boolean isNetworkWeak();

	public abstract void updateNetworkState(int length);

	public abstract void wakeup() throws IOException;

	public abstract IOReadFuture getReadFuture();

	public abstract void setReadFuture(IOReadFuture future);

	public abstract void incrementWriter();

	public abstract void decrementWriter();

	public abstract int read(ByteBuffer buffer) throws IOException;

	public abstract int write(ByteBuffer buffer) throws IOException;

	public abstract EndPointWriter getEndPointWriter();

	public abstract boolean isBlocking();
	
	public abstract ProtocolEncoder getProtocolEncoder() ;

	public abstract void setProtocolEncoder(ProtocolEncoder protocolEncoder) ;

	public abstract ProtocolDecoder getProtocolDecoder() ;

	public abstract void setProtocolDecoder(ProtocolDecoder protocolDecoder) ;
}
