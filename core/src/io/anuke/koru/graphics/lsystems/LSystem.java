package io.anuke.koru.graphics.lsystems;

import arc.struct.Seq;
import arc.math.Mathf;
import java.util.Stack;

public abstract class LSystem{
	public LSystemData data;
	public String string;
	protected Seq<LTree.Line> lines = new Seq<>();
	protected Seq<LTree.Leaf> leaves = new Seq<>();
	public float x, y, lastx, lasty, angle;
	public int maxstack;
	public float timeOffset = 0;
	protected Stack<State> stack = new Stack<>();

	public LSystem(LSystemData data){
		this.data = data;
		this.string = data.axiom;
		for(int i = 0; i < data.iterations; i++){
			StringBuilder next = new StringBuilder();
			for(char c : string.toCharArray()){
				next.append(data.rules.getOrDefault(c, String.valueOf(c)));
			}
			string = next.toString();
		}
	}

	public LSystemData getData(){
		return data;
	}

	protected void recache(){
		lines.clear();
		leaves.clear();
		stack.clear();
		x = y = lastx = lasty = 0;
		angle = 90;

		for(char c : string.toCharArray()){
			drawc(c);
		}
		maxstack = 0;
		for(LTree.Line line : lines) maxstack = Math.max(maxstack, line.stack);
	}

	protected void drawc(char c){
		if(c == 'F'){
			drawForward();
		}else if(c == '+'){
			angle += data.space;
		}else if(c == '-'){
			angle -= data.space;
		}else if(c == '['){
			push();
		}else if(c == ']'){
			pop();
		}
	}

	protected void drawForward(){
		float sway = data.swayscl * Mathf.sin((timeOffset + getTime()) / data.swayphase + stack.size() * data.swayspace);
		float radians = Mathf.degRad * (-angle + 180f + sway);

		float nx = data.len * Mathf.cos(radians);
		float ny = data.len * Mathf.sin(radians);
		LTree.Line line = new LTree.Line();
		line.x1 = lastx; line.y1 = lasty; line.x2 = lastx + nx; line.y2 = lasty + ny;
		line.stack = stack.size();
		lines.add(line);
		lastx += nx;
		lasty += ny;
	}

	protected void push(){
		stack.push(new State(lastx, lasty, angle));
	}

	protected void pop(){
		State s = stack.pop();
		lastx = s.x;
		lasty = s.y;
		angle = s.angle;
	}

	protected abstract float getTime();

	protected abstract void drawLines();

	protected static class State{
		float x, y, angle;
		public State(float x, float y, float angle){
			this.x = x; this.y = y; this.angle = angle;
		}
	}
}


