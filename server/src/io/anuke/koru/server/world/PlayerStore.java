package io.anuke.koru.server.world;

import com.esotericsoftware.kryo.*;
import com.esotericsoftware.kryo.io.*;
import io.anuke.koru.*;
import io.anuke.koru.network.*;
import io.anuke.koru.world.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/** Stores player data with atomic writes and a background save queue. */
public class PlayerStore{
    private static final int version = 1;

    private final Path directory;
    private final Kryo kryo = new Kryo();
    private final ConcurrentHashMap<String, PlayerData> cache = new ConcurrentHashMap<>();
    private final LinkedBlockingQueue<PlayerData> queue = new LinkedBlockingQueue<>();
    private final Object writeLock = new Object();
    private final Thread thread;
    private volatile boolean running = true;

    public PlayerStore(Path directory){
        this.directory = directory;

        try{
            Files.createDirectories(directory);
        }catch(IOException e){
            throw new RuntimeException("Failed to create player data directory!", e);
        }

        Registrator.registerPlayers(kryo);
        kryo.register(PlayerSave.class);

        thread = new Thread(this::run, "PlayerSave");
        thread.setDaemon(true);
        thread.start();
    }

    public PlayerData load(String name){
        if(name == null){
            return null;
        }

        PlayerData cached = cache.get(name);
        if(cached != null){
            return cached;
        }

        Path path = playerFile(name);
        if(!Files.exists(path)){
            return null;
        }

        synchronized(writeLock){
            try(Input input = new Input(new FileInputStream(path.toFile()))){
                PlayerSave save = kryo.readObject(input, PlayerSave.class);
                if(save == null || save.data == null){
                    return null;
                }

                if(save.version > version){
                    Koru.log("Unsupported player data version for " + name + ".");
                    return null;
                }

                if(!name.equals(save.data.name)){
                    return null;
                }

                return save.data;
            }catch(Exception e){
                Koru.log("Error reading player data for " + name + "!");
                e.printStackTrace();
                return null;
            }
        }
    }

    public boolean has(String name){
        return name != null && (cache.containsKey(name) || Files.exists(playerFile(name)));
    }

    /** Queues player data for writing. The cache makes the data available immediately on reconnect. */
    public void save(PlayerData data){
        if(data == null || data.name == null){
            return;
        }

        cache.put(data.name, data);
        queue.offer(data);
    }

    /** Writes all queued player data and waits until it is on disk. */
    public void flush(){
        synchronized(writeLock){
            PlayerData data;
            while((data = queue.poll()) != null){
                write(data);
            }
        }
    }

    public void close(){
        running = false;
        thread.interrupt();

        try{
            thread.join(2000);
        }catch(InterruptedException ignored){
        }

        flush();
    }

    private void run(){
        try{
            while(running || !queue.isEmpty()){
                PlayerData data = queue.poll(500, TimeUnit.MILLISECONDS);
                if(data != null){
                    synchronized(writeLock){
                        write(data);
                    }
                }
            }
        }catch(InterruptedException ignored){
        }
    }

    private void write(PlayerData data){
        Path path = playerFile(data.name);
        Path temp = null;

        try{
            Files.createDirectories(directory);
            temp = Files.createTempFile(directory, "player-", ".tmp");

            try(Output output = new Output(new FileOutputStream(temp.toFile()))){
                PlayerSave save = new PlayerSave();
                save.version = version;
                save.data = data;
                kryo.writeObject(output, save);
            }

            try{
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }catch(AtomicMoveNotSupportedException e){
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }

            cache.remove(data.name, data);
        }catch(Exception e){
            Koru.log("Error writing player data for " + data.name + "!");
            e.printStackTrace();

            if(temp != null){
                try{
                    Files.deleteIfExists(temp);
                }catch(IOException ignored){
                }
            }
        }
    }

    private Path playerFile(String name){
        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(name.getBytes(StandardCharsets.UTF_8));
        return directory.resolve(encoded + ".player");
    }
}
