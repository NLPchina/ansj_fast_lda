package org.ansj.lda.pojo;

import java.util.List;

import com.google.common.collect.Lists;

public class Doc {
	
	//文档词矩阵
	public List<Vector> vectors ; 
	
	//文档topic矩阵
	public int[] topicArray = null ;
	
	public Doc(int topicNum){
		topicArray = new int[topicNum] ;
		vectors = Lists.newArrayList() ;
	}

	public void addVector(Vector vector) {
		// TODO Auto-generated method stub
		vectors.add(vector) ;
		topicArray[vector.topicId]++ ;
	}
	

	public void removeVector(Vector vector) {
		// TODO Auto-generated method stub
		topicArray[vector.topicId]-- ;
	}

	public void updateVecotr(Vector vector) {
		// TODO Auto-generated method stub
		topicArray[vector.topicId]++ ;
	}
	
}
