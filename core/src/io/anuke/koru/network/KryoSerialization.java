package io.anuke.koru.network;

import arc.net.NetSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import java.nio.ByteBuffer;

public class KryoSerialization implements NetSerializer{
	private Kryo kryo;
	private ByteBufferInput input = new ByteBufferInput();
	private ByteBufferOutput output = new ByteBufferOutput();

	public KryoSerialization(){
		this.kryo = new Kryo();
	}

	public Kryo getKryo(){
		return kryo;
	}

	@Override
	public synchronized void write(ByteBuffer buffer, Object object){
		output.setBuffer(buffer);
		kryo.writeClassAndObject(output, object);
	}

	@Override
	public synchronized Object read(ByteBuffer buffer){
		input.setBuffer(buffer);
		return kryo.readClassAndObject(input);
	}
}


