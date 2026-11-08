package io.anuke.koru.server.world;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryo.io.*;
import io.anuke.koru.*;
import io.anuke.koru.network.*;
import io.anuke.koru.world.*;
import io.anuke.koru.world.materials.*;
import java.io.*;
import java.nio.file.*;

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
