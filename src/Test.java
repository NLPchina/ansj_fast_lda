import java.io.File;
import java.io.IOException;

import org.ansj.lda.LDA;
import org.ansj.lda.impl.LDAGibbsModel;
import org.ansj.util.impl.AnsjAnalysis;

public class Test {

	public static void main(String[] args) throws IOException {
		LDA lda = new LDA(AnsjAnalysis.DEFAUlT,new LDAGibbsModel(20, 0.5, 0.1, 100, Integer.MAX_VALUE, Integer.MAX_VALUE));
		
		File file = new File("test_data/LdaOriginalDocs") ;
		
		for (File doc : file.listFiles()) {
			if(!doc.isHidden()&&doc.canRead()){
				lda.addDoc(doc,"utf-8") ;
			}
		}

		lda.trainAndSave("result/", "utf-8") ;
	}
}
