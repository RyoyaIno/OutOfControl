package main;

import java.awt.Container;

import javax.swing.JFrame;

public class MainFrame extends JFrame{

	/**
	 * constructer
	 */
	public MainFrame() {
		// タイトルを設定
		setTitle("OutOfControl");

		// パネルを作成
		MainPanel panel = new MainPanel();
		Container contentPane = getContentPane();
		contentPane.add(panel);

		// パネルサイズに合わせてフレームサイズを自動設定
		pack();
	}

	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}