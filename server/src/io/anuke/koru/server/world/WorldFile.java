package io.anuke.koru.server.world;

import arc.util.*;
import arc.util.pooling.*;
import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryo.io.*;
import io.anuke.koru.*;
import io.anuke.koru.modules.*;
import io.anuke.koru.network.*;
import io.anuke.koru.world.*;
import io.anuke.koru.world.materials.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class WorldFile extends WorldLoader{
    public final String filename = "chunk", extension = ".kw";
    private Path file;
    private ConcurrentHashMap<String, Path> files = new ConcurrentHashMap<String, Path>();
    private Kryo kryo; // for reading only
    private ConcurrentHashMap<Long, Chunk> loadedchunks = new ConcurrentHashMap<Long, Chunk>(); // server-side chunks
    private Generator generator;
    private StructureGenerator structGenerator;
    private ChunkWriter[] writers = new ChunkWriter[Runtime.getRuntime().availableProcessors()];
    private Object lock = new Object();
    private boolean debug;

    public WorldFile(Path file, Generator generator, StructureGenerator structGenerator) {

        if(!Files.isDirectory(file)){
            throw new RuntimeException("World file has to be a directory!");

        }
        for(int i = 0; i < writers.length; i++){
            writers[i] = new ChunkWriter();
        }

        kryo = new Kryo();
        kryo.register(Chunk.class);
        kryo.register(Material.class, new Registrator.MaterialsSerializer());
        this.file = file;
        this.generator = generator;
        this.structGenerator = structGenerator;

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(file)){
            for(Path path : stream){
                if(path.toString().endsWith(extension)){
                    files.put(path.getFileName().toString(), path);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
        if(files.size() > 0){
            Koru.log("Found " + files.size() + " world chunk" + (files.size() == 1 ? "" : "s") + ".");
        }else{
            Koru.log("Found empty world.");
        }

    }

    public boolean chunkIsSaved(int x, int y){
        return getPath(x, y) != null;
    }

    public void writeChunk(Chunk chunk, int writer){
        Path path = Paths.get(file.toString(), "/" + fileName(chunk.x, chunk.y));

        while(!writers[writer].writing()){ // to prevent screwups with multiple threads using writer 0
            writers[writer].writeChunk(chunk, path);
            files.put(path.getFileName().toString(), path);
            return;
        }
    }

    public Chunk readChunk(int x, int y){
        synchronized(lock){
            if(debug){
                Koru.log(ColorCodes.red + "BEGIN" + ColorCodes.yellow + " read chunk" + ColorCodes.reset);
            }
            Path path = getPath(x, y);

            long time = Time.millis();

            try{

                Input input = null;
                FileInputStream file = new FileInputStream(path.toFile());
                    input = new Input(file);
                Chunk chunk = kryo.readObject(input, Chunk.class);

                for(int cx = 0; cx < World.chunksize; cx ++){
                    for(int cy = 0; cy < World.chunksize; cy ++){
                        chunk.tiles[cx][cy].x = cx + chunk.worldX();
                        chunk.tiles[cx][cy].y = cy + chunk.worldY();
                    }
                }

                input.close();
                file.close();

                if(debug){
                    Koru.log("Chunk read time elapsed: " + Time.timeSinceMillis(time));

                }
                if(debug){
                    Koru.log(ColorCodes.green + "END" + ColorCodes.yellow + " read chunk" + ColorCodes.reset);
                }
                return chunk;
            }catch(Exception e){
                Koru.log("Error writing chunk!");
                e.printStackTrace();
            }
            throw new RuntimeException("Error reading chunk!");
        }
    }

    public Chunk generateChunk(int chunkx, int chunky){
        Chunk chunk = Pools.obtain(Chunk.class, Chunk::new);
        chunk.set(chunkx, chunky);
        generator.generateChunk(chunk);
        loadedchunks.put(hashCoords(chunkx, chunky), chunk);
        structGenerator.generateChunk(chunk);
        return chunk;
    }

    @Override
    public void unloadChunk(Chunk chunk){
        writeChunk(chunk, 0);
        loadedchunks.remove(hashCoords(chunk.x, chunk.y));
    }

    @Override
    public Chunk getChunk(int chunkx, int chunky){
        Chunk chunk = loadedchunks.get(hashCoords(chunkx, chunky));
        if(chunk == null){
            if(chunkIsSaved(chunkx, chunky)){
                Chunk schunk = readChunk(chunkx, chunky);
                loadedchunks.put(hashCoords(chunkx, chunky), schunk);
                return schunk;
            }else{
                return generateChunk(chunkx, chunky);
            }
        }
        return chunk;
    }

    public Collection<Chunk> getLoadedChunks(){
        return loadedchunks.values();
    }

    public String fileName(int x, int y){
        return filename + hashCoords(x, y) + extension;
    }

    public int totalChunks(){
        return files.size();
    }

    private Path getPath(int x, int y){
        return files.get(fileName(x, y));
    }

    public static long hashCoords(int a, int b){
        return (((long) a) << 32) | (b & 0xffffffffL);
    }
}


