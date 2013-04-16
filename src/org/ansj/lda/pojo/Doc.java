package org.ansj.lda.pojo;

import java.util.List;

import com.google.common.collect.Lists;

public class Doc {
	private String name;

	// 文档词矩阵
	public List<Vector> vectors;

	// 文档topic矩阵
	public int[] topicArray = null;

	public Doc(String name, int topicNum) {
		this.name = name;
		topicArray = new int[topicNum];
		vectors = Lists.newArrayList();
	}

	public void addVector(Vector vector) {
		// TODO Auto-generated method stub
		vectors.add(vector);
		topicArray[vector.topicId]++;
	}

	public void removeVector(Vector vector) {
		// TODO Auto-generated method stub
		topicArray[vector.topicId]--;
	}

	public void updateVecotr(Vector vector) {
		// TODO Auto-generated method stub
		topicArray[vector.topicId]++;
	}

	public String getName() {
		return name;
	}
	
}
