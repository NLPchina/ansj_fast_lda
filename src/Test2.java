import java.io.File;
import java.io.IOException;

import org.ansj.lda.LDA;
import org.ansj.lda.impl.LDAGibbsModel;
import org.ansj.util.impl.AnsjAnalysis;

public class Test2 {
	public static void main(String[] args) throws IOException {
		File file = new File("/Users/ansj/Desktop/搜索组分享/文本分类语料库");
		LDA lda = new LDA(AnsjAnalysis.DEFAUlT, new LDAGibbsModel(20, 0.5, 0.1, 2000, Integer.MAX_VALUE, Integer.MAX_VALUE));
		for (File dir : file.listFiles()) {
			if (dir.isHidden() || !dir.isDirectory()) {
				continue;
			}
			for (File doc : dir.listFiles()) {
				if (doc.canRead() && doc.getName().toLowerCase().endsWith(".txt")) {
					lda.addDoc(doc, "gb2312");
				}
			}
		}

		lda.trainAndSave("result2", "utf-8");
	}
}
