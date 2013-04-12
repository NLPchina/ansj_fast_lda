import java.io.BufferedReader;
import java.io.File;

import org.ansj.lda.LDA;
import org.ansj.lda.impl.LDAGibbsModel;
import org.ansj.util.Analysis;
import org.ansj.util.impl.DicAnalysis;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Test2 {
	public static void main(String[] args) throws Exception {
	
		Analysis dicAnalysis = DicAnalysis.getInstance(new File("library/result_1_3.dic"), "UTF-8");
	
		LDA lda = new LDA(dicAnalysis, new LDAGibbsModel(10, 5, 0.1, 100, Integer.MAX_VALUE, Integer.MAX_VALUE));
		BufferedReader newReader = Files.newReader(new File("/Users/ansj/Documents/temp/computer_300000.txt"), Charsets.UTF_8);
		
		String temp = null;
		while ((temp = newReader.readLine()) != null) {
			lda.addDoc(temp);
		}

		lda.trainAndSave("result/computer", "utf-8");
	}
}
