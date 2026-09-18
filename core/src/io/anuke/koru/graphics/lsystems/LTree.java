package io.anuke.koru.graphics.lsystems;

import arc.struct.Seq;

public class LTree{
	public Seq<Line> lines = new Seq<>();
	public Seq<Leaf> leaves = new Seq<>();

	public static class Line implements Comparable<Line>{
		public float x1, y1, x2, y2, thickness;
		public int stack;

		@Override
		public int compareTo(Line o){
			if(stack == o.stack) return 0;
			return stack < o.stack ? -1 : 1;
		}
	}

	public static class Leaf{
		public float x, y;
	}
}


