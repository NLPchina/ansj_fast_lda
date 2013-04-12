package org.ansj.lda.pojo;


public class Topic {
	
	public int vCount ;
	
	public int[] vectorIdArray = null ;
	
	public Topic(int vCount){
		vectorIdArray = new int[vCount] ;
	}

	
	public void removeVector(Vector vector){
		this.vCount-- ;
		vectorIdArray[vector.id]-- ;
	}

	public void addVector(Vector vector) {
		// TODO Auto-generated method stub
		this.vCount++ ;
		vectorIdArray[vector.id]++ ;
	}
}
