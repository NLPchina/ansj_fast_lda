package org.ansj.util.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.ansj.dic.DicReader;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.Analysis;
import org.ansj.util.recognition.NatureRecognition;

public class AnsjAnalysis implements Analysis {

	public static final Analysis DEFAUlT = new AnsjAnalysis(true);

	@SuppressWarnings(value = { "all" })
	private HashSet<String> filter = null;

	/**
	 * 停用词词典
	 * 
	 * @param filter
	 */
	public AnsjAnalysis(HashSet<String> filter) {
		this.filter = filter;
	}

	/**
	 * 是否需要过滤停用词
	 * 
	 * @param needFilter
	 */
	public AnsjAnalysis(boolean needFilter) {
		if (needFilter) {
			filter = initSystemFilter();
		}
	}

	private HashSet<String> initSystemFilter() {
		// TODO Auto-generated method stub
		HashSet<String> hs = new HashSet<String>();
		BufferedReader reader = DicReader.getReader("newWord/newWordFilter.dic");
		String temp = null;

		try {
			while ((temp = reader.readLine()) != null) {
				hs.add(temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hs;
	}

	@Override
	public List<String> getWords(Reader reader) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = null ;
		try {
			br = new BufferedReader(reader);
			String temp = null;
			List<String> all = new ArrayList<String>();
			while ((temp = br.readLine()) != null) {
				List<Term> paser = ToAnalysis.paser(temp);
				new NatureRecognition(paser).recognition();
				for (Term term : paser) {
					if (!filter(term)) {
						all.add(term.getName());
					}
				}
			}
			return all;
		} finally {
			if(br!=null)
				br.close() ;
		}
	}

	/**
	 * 第一层词性过滤
	 * 
	 * @param term
	 * @return
	 */
	public boolean filter(Term term) {
		if (filter == null) {
			return true;
		}
		String natureStr = term.getNatrue().natureStr;
		if (natureStr == null || "w".equals(natureStr) || "m".equals(natureStr)) {
			return true;
		}
		if (term.getName().length() == 1) {
			return true;
		}
		return filter(term.getName());
	}

	@Override
	public boolean filter(String word) {
		return filter.contains(word);
	}
}
