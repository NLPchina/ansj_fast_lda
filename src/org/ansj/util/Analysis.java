package org.ansj.util;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * 分词的接口
 * 
 * @author ansj
 */
public interface Analysis {
	
	/**
	 * 传入正文获得分词结果
	 * 
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public List<String> getWords(Reader reader) throws IOException;

	/**
	 * 停用词过滤
	 * 
	 * @param word单个的词
	 * @return boolean
	 */
	boolean filter(String word);
}
