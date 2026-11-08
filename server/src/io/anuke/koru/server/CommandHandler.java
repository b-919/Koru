package io.anuke.koru.server;

import arc.func.*;
import arc.math.*;
import arc.struct.*;
import arc.util.*;
import io.anuke.koru.entities.*;
import io.anuke.koru.entities.types.*;
import io.anuke.koru.network.*;
import java.util.*;

import static arc.util.ColorCodes.*;


public class CommandHandler{
    final KoruServer server;
    private ObjectMap<String, Command> commands = new ObjectMap<>();
    Scanner scan;

    public CommandHandler(KoruServer server){
        this.server = server;

        scan = new Scanner(System.in);

        new Thread(() -> {
            while(scan.hasNextLine()){
                try{
                    process(scan.nextLine().toLowerCase());
                }catch(Exception e){
                    print(purple + "Command error!");
                    e.printStackTrace();
                }
            }
        }){{setDaemon(true);}}.start();

        register();
    }

    private void register(){
        cmd("help", () -> {
            print(yellow + " - Commands - ");
            for(Command c : commands.values()){
                print("* " + c.name + " " + c.params);
            }
        });

        cmd("spawn", () -> {
            for(int i = 0; i < 30; i++){
                TestEntity entity = new TestEntity();
                entity.pos.set(Mathf.random(-40, 40), Mathf.random(-40, 40));
                entity.add();
                Net.sendEntity(entity);
            }

            print("spawning 30 entities. (now "+purple+Entities.all().size+lightCyan+" entities at "+purple+ 1f/(Time.delta/60f)+lightCyan +" FPS.)");
        });

        cmd("entities", () -> {
            print(yellow + Entities.all().size);
        });

        cmd("fps", () -> {
            print(yellow + 1f/(Time.delta/60f));
        });

        cmd("cells", () -> {
            print(yellow + Entities.instance.getCellAmount());
        });

    }

    private void process(String s){

        String c = s.indexOf(" ") == -1 ? "" : s.split(" ")[0];
        String[] args = s.substring(c.length()+1).split(" ");

        if(c.isEmpty()){
            c = s;
            args = new String[]{};
        }

        if(commands.containsKey(c)){
            Command com = commands.get(c);
            if(args.length == com.paramLength){
                com.runner.get(args);
            }else{
                print("Command usage: " + c + " " + com.params);
            }
        }else{
            print(purple + "Invalid command.");
        }

    }

    private void print(String s){
        System.out.println(lightCyan + s + red);
    }

    private void cmd(String name, Runnable r){
        commands.put(name, new Command(name, "", s -> {
            r.run();
        }));
    }

    private void cmd(String name, String params, Cons<String[]> cons){
        commands.put(name, new Command(name, params, cons));
    }

    private class Command{
        String name, params;
        Cons<String[]> runner;
        int paramLength;

        public Command(String name, String params, Cons<String[]> runner){
            this.name = name;
            this.params = params;
            this.runner = runner;
            this.paramLength = params.length() == 0 ? 0 : (params.length() - params.replaceAll(" ", "").length() + 1);
        }
    }
}
