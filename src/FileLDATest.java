import java.io.File;
import java.io.IOException;

import org.ansj.lda.LDA;

public class FileLDATest {
	public static void main(String[] args) throws IOException {
		File[] files = new File("/Users/ansj/Desktop/搜索组分享/文本分类语料库").listFiles();

		LDA lda = new LDA(10);
		for (File dir : files) {
			if (dir.isHidden() || !dir.isDirectory()) {
				continue;
			}
			for (File file : dir.listFiles()) {
				if (file.isHidden() || !file.getName().toLowerCase().endsWith(".txt")) {
					continue;
				}
				lda.addDoc(file, "gb2312");
			}

		}

		lda.trainAndSave("result/cluster", "utf-8");
	}
}
