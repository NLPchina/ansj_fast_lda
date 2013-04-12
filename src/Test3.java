import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.ansj.lda.LDA;
import org.ansj.lda.impl.LDAGibbsModel;
import org.ansj.util.impl.AnsjAnalysis;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Test3 {

	public static void main(String[] args) throws IOException {
		LDA lda = new LDA(AnsjAnalysis.DEFAUlT,new LDAGibbsModel(20, 0.5, 0.1, 100, Integer.MAX_VALUE, Integer.MAX_VALUE));
		BufferedReader newReader = Files.newReader(new File("test_data/sample.txt"), Charsets.UTF_8);
		String temp =null ;
		int i = 0 ;
		while((temp=newReader.readLine())!=null){
			lda.addDoc(temp) ;
			if(i++>1000){
				break ;
			}
		}

		lda.trainAndSave("result/", "utf-8") ;
	}
}
