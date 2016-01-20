import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;

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
		BufferedReader newReader = Files.newReader(new File("test_data/sample.txt"), Charset.forName("gbk"));
		
		String temp = null;
		int id = 0 ;
		while ((temp = newReader.readLine()) != null) {
			lda.addDoc(String.valueOf(++id),temp);
		}

		lda.trainAndSave("result/computer", "utf-8");
	}
}
