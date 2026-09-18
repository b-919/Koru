package io.anuke.koru.server.world;

import java.io.FileOutputStream;
import java.nio.file.Path;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import io.anuke.koru.Koru;
import io.anuke.koru.network.Registrator;
import io.anuke.koru.world.Chunk;
import io.anuke.koru.world.materials.Material;

public class ChunkWriter{
	private Kryo kryo;
	private boolean writing;
	
	public ChunkWriter(){
		kryo = new Kryo();
		kryo.register(Chunk.class);
		kryo.register(Material.class, new Registrator.MaterialsSerializer());
	}

	public void writeChunk(Chunk chunk, Path path){
		writing = true;
		try{
			FileOutputStream file = new FileOutputStream(path.toString());
			Output output = new Output(file);
			kryo.writeObject(output, chunk);
			output.close();

		}catch(Exception e){
			Koru.log("Error writing chunk!");
			e.printStackTrace();
		}
		writing = false;
	}
	
	public boolean writing(){
		return writing;
	}
}
