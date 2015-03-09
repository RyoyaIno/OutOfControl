package main;

import java.io.*;

import javax.sound.sampled.*;

/**
 * 効果音Clipの管理をするクラス
 */
public class DataClip {
	// WAVEファイルからロードしたサウンドデータ
	public byte[] data;
	// どこまで再生したかを表すインデックス
	public int index;
	// WAVEファイルのフォーマット
	public AudioFormat format;

	// 再生中か
	public boolean running = false;
	// 1フレーム（ゲームループ1周）で再生するバイト数を計算する
	public int sampleRate;

	/**
	 * コンストラクタ1
	 *
	 * @param data
	 * @param format
	 */
	public DataClip(byte[] data, AudioFormat format) {
		this.data = data;
		this.index = 0;
		this.format = format;
	}

	/**
	 * コンストラクタ2
	 *
	 * @param audioStream
	 * @throws IOException
	 */
	public DataClip(AudioInputStream audioStream) throws IOException {
		index = 0;
		format = audioStream.getFormat();

		// WAVEファイルの大きさを求める
		int length = (int) (audioStream.getFrameLength() * format
				.getFrameSize());
		// その大きさのbyte配列を用意
		data = new byte[length];
		// dataにWAVEデータを格納する
		DataInputStream is = new DataInputStream(audioStream);
		is.readFully(data);
	}

	/**
	 * 1フレームで再生するバイト数を計算する
	 *
	 * @param milliseconds
	 *            1フレームの時間
	 */
	public void calculateSampleRate(int milliseconds) {
		// System.out.println("      Channels  : " + format.getChannels());
		// System.out.println("      SampleRate: " + format.getSampleRate());
		// System.out.println("SampleSizeInBits: " +
		// format.getSampleSizeInBits());
		// System.out.println("      frame time: " + milliseconds);
		sampleRate = (int) ((milliseconds * (format.getChannels()
				* format.getSampleRate() * format.getSampleSizeInBits() / 8)) / 1000);
		// System.out.println("      SampleRate: " + sampleRate);
		// System.out.println();
	}
}
