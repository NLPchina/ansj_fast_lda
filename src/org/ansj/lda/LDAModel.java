package org.ansj.lda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.ansj.lda.pojo.Doc;
import org.ansj.lda.pojo.Topic;
import org.ansj.lda.pojo.Vector;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.Files;
import com.google.common.primitives.Doubles;

public abstract class LDAModel {
	/**
	 * 主题数
	 */
	protected int topicNum;

	protected double alpha = 0.5;

	protected double beta = 0.1;

	/**
	 * 迭代次数
	 */
	protected int iteration = 100;

	/**
	 * 多少次迭代保存一次,默认不保存
	 */
	protected int saveStep = Integer.MAX_VALUE;

	/**
	 * 开始的保存的迭代次数,默认不保存
	 */
	protected int beginSaveIters = Integer.MAX_VALUE;

	// Structure

	/**
	 * v词数 m文档数
	 */
	protected int vCount, dCount;

	/**
	 * 文档-词矩阵
	 */
	protected List<Doc> docs = Lists.newArrayList();;

	/**
	 * 主题-词矩阵
	 */
	protected Topic[] topics = null;

	/**
	 * 词和id双向map
	 */
	protected BiMap<String, Integer> vectorMap = HashBiMap.create();

	/**
	 * @param alpha
	 * @param beta
	 * @param iteration迭代次数
	 * @param saveStep每多少步保存一次
	 * @param beginSaveIters开始的保存的迭代次数
	 */
	public LDAModel(int topicNum, double alpha, double beta, int iteration, int saveStep, int beginSaveIters) {
		super();
		this.topicNum = topicNum;
		this.alpha = alpha;
		this.beta = beta;
		this.iteration = iteration;
		this.saveStep = saveStep;
		this.beginSaveIters = beginSaveIters;
	}

	/**
	 * 增加文章
	 * 
	 * @param words
	 */
	public void addDoc(List<String> words) {
		Doc doc = new Doc(topicNum);
		Integer id = null;
		int topicId = 0;
		dCount++;
		for (String string : words) {
			id = vectorMap.get(string);
			if (id == null) {
				id = vCount;
				vectorMap.put(string, vCount);
				vCount++;
			}
			// random topic 门洛奇
			topicId = (int) (Math.random() * topicNum);
			// 文档增加向量
			doc.addVector(new Vector(id, topicId));
		}
		docs.add(doc);
	}

	/**
	 * 开始训练
	 * 
	 * @throws IOException
	 */
	public void trainAndSave(String modelPath, String charset) throws IOException {

		fullTopicVector();

		System.out.println("insert model ok! ");
		// 迭代收敛
		for (int i = 0; i < iteration; i++) {
			if ((i >= beginSaveIters) && (((i - beginSaveIters) % saveStep) == 0)) {
				saveModel(i + "", modelPath, charset);
			}
		}

		for (Doc doc : docs) {
			for (Vector vector : doc.vectors) {
				sampleTopic(doc, vector);
			}
		}

		System.out.println("explan model ok!");
		saveModel("result", modelPath, charset);

		System.out.println("save Model ok!");
	}

	/**
	 * 保存模型
	 * 
	 * @param i
	 * @throws IOException
	 */
	private void saveModel(String iters, String modelPath, String charset) throws IOException {
		// TODO Auto-generated method stub
		double[][] phi = new double[topicNum][vCount];
		double[][] theta = new double[dCount][topicNum];

		updateEstimatedParameters(phi, theta);

		saveModel(iters, phi, theta, modelPath, charset);
	}

	private static final String LINES = "\n";
	private static final char[] LINEC = "\n".toCharArray();

	private void saveModel(String iters, double[][] phi, double[][] theta, String modelPath, String charsetName) throws IOException {
		// TODO Auto-generated method stub
		String modelName = "lda_" + iters;
		File modelDir = new File(modelPath);
		// 创建路径
		if (!modelDir.isDirectory()) {
			modelDir.mkdirs();
		}

		Charset charset = Charset.forName(charsetName);

		/**
		 * 配置信息
		 */
		StringBuilder sb = new StringBuilder();
		sb.append("alpha = " + alpha + LINES);
		sb.append("topicNum = " + topicNum + LINES);
		sb.append("docNum = " + dCount + LINES);
		sb.append("termNum = " + vCount + LINES);
		sb.append("iterations = " + iteration + LINES);
		sb.append("saveStep = " + saveStep + LINES);
		sb.append("beginSaveIters = " + beginSaveIters);

		Files.write(sb, new File(modelDir, modelName + ".params"), charset);

		/**
		 * lda.phi K*V
		 */
		BufferedWriter writer = Files.newWriter(new File(modelDir, modelName + ".phi"), charset);
		for (int i = 0; i < topicNum; i++) {
			writer.write(Joiner.on("\t").join(Doubles.asList(phi[i])));
			writer.write(LINEC);
		}
		writer.flush();
		writer.close();

		// lda.theta M*K
		writer = Files.newWriter(new File(modelDir, modelName + ".theta"), charset);
		for (int i = 0; i < dCount; i++) {
			writer.write(Joiner.on("\t").join(Doubles.asList(theta[i])));
			writer.write(LINEC);
		}
		writer.flush();
		writer.close();

		// lda.tassign
		writer = Files.newWriter(new File(modelDir, modelName + ".tassign"), charset);
		Doc doc = null;
		Vector vector = null;
		for (int m = 0; m < dCount; m++) {
			doc = docs.get(m);
			for (int n = 0; n < doc.vectors.size(); n++) {
				vector = doc.vectors.get(n);
				writer.write(vector.id + ":" + vector.topicId + "\t");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();

		// lda.twords phi[][] K*V
		writer = Files.newWriter(new File(modelDir, modelName + ".twords"), charset);
		int topNum = 20;
		double[] scores = null;
		VecotrEntry pollFirst = null;
		for (int i = 0; i < topicNum; i++) {
			writer.write("topic " + i + "\t:\n");
			MinMaxPriorityQueue<VecotrEntry> mmp = MinMaxPriorityQueue.create();
			scores = phi[i];
			for (int j = 0; j < vCount; j++) {
				mmp.add(new VecotrEntry(j, scores[j]));
			}

			for (int j = 0; j < topNum; j++) {
				if (mmp.isEmpty()) {
					break;
				}
				pollFirst = mmp.pollFirst();
				writer.write("\t"+vectorMap.inverse().get(pollFirst.id) + " " + pollFirst.score + "\n");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) {
		MinMaxPriorityQueue<VecotrEntry> mmp = MinMaxPriorityQueue.create();
		mmp.add(new VecotrEntry(1, 1.0));
		mmp.add(new VecotrEntry(3, 3.0));
		mmp.add(new VecotrEntry(2, 2.0));

		for (int i = 0; i < 2; i++) {
			System.out.println(mmp.pollFirst().id);
			;
		}

	}

	/**
	 * tmd 排序类
	 * 
	 * @author ansj
	 * 
	 */
	static class VecotrEntry implements Comparable<VecotrEntry> {
		int id;
		double score;

		public VecotrEntry(int id, double score) {
			this.id = id;
			this.score = score;
		}

		@Override
		public int compareTo(VecotrEntry o) {
			// TODO Auto-generated method stub
			if (this.score > o.score) {
				return -1;
			} else {
				return 1;
			}
		}

	}

	/**
	 * 填充topic array矩阵
	 */
	private void fullTopicVector() {
		topics = new Topic[topicNum];
		for (int i = 0; i < topics.length; i++) {
			topics[i] = new Topic(vCount);
		}

		for (Doc doc : docs) {
			for (Vector vector : doc.vectors) {
				topics[vector.topicId].addVector(vector);
			}
		}
	}

	/**
	 * Compute p(z_i = k|z_-i, w) 抽样
	 * 
	 * @param doc
	 * @param vector
	 */
	protected abstract void sampleTopic(Doc doc, Vector vector);

	/**
	 * 更新估计参数
	 */
	protected abstract void updateEstimatedParameters(double[][] phi, double[][] theta);
}
