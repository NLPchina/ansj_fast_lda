import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.util.CollectionUtil;
import org.nlpcn.commons.lang.util.IOUtil;
import org.nlpcn.commons.lang.util.MapCount;
import org.nlpcn.commons.lang.util.WordWeight;

public class HotFinder {
	public static void main(String[] args) throws IOException { //用来寻找热点
		WordWeight ww = new WordWeight(500000, 200000) ;
		
		
		String temp = null ;
		
		try(BufferedReader reader = IOUtil.getReader("test_data/fl.txt", IOUtil.UTF8)){
			while((temp=reader.readLine())!=null){
				List<Term> parse = NlpAnalysis.parse(temp) ;
				for (Term term : parse) {
					ww.add(term.getName(), "all");
				}
			}
		}
		
		
		try(BufferedReader reader = IOUtil.getReader("test_data/corpus.txt", IOUtil.UTF8)){
			while((temp=reader.readLine())!=null){
				List<Term> parse = NlpAnalysis.parse(temp) ;
				for (Term term : parse) {
					ww.add(term.getName(), "sport");
				}
			}
		}
		
		
		
		MapCount<String> mapCount = ww.exportChiSquare().get("sport") ;
		
		
		List<Entry<String, Double>> sortMapByValue = CollectionUtil.sortMapByValue(mapCount.get(), 2) ;
		
		int i = 0  ;
		for (Entry<String, Double> entry : sortMapByValue) {
			System.out.println(entry);
			if(i++>20){
				break ;
			}
		}
	}
}
